package engine.erp.produce;

import java.util.*;
import engine.util.EngineRuntimeException;

/**
 * <p>Title: 分切配料的最佳组合</p>
 * <p>Description: 分切配料的最佳组合<br>
 * 2004.4.24: 各个递归的方法添加递归层数的限制
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */
final class BestCombination
{

  private BestCombination(){}

  private static ArrayList calcList = new ArrayList();

  /**
   * 计算分切需要的原料（即用什么原料分切）
   * @param oldprodMaterials 产品数组
   * @param olddesMaterials 原料数组
   * @param info 分切设置信息
   * @return 返回CombinationInfo分切的组合信息，返回null，表示没有可用方法
   */
  public static CombinationInfo calcCombination(DispartMaterial[] oldprodMaterials,
      DispartMaterial[] olddesMaterials, DispartInfo info)
  {
    if(oldprodMaterials == null || oldprodMaterials.length == 0 ||
       olddesMaterials == null || olddesMaterials.length == 0 || info == null)
      return null;

    DispartMaterial[] prodMaterials = checkInvalid(oldprodMaterials);
    DispartMaterial[] desMaterials = checkInvalid(olddesMaterials);
    synchronized(calcList)
    {
      //按宽度从大到小排序
      DispartMaterial[] prodSorted = sortMaterial(prodMaterials, null);
      DispartMaterial[] desSorted = sortMaterial(desMaterials, info);
      //得到宽度可用于分割的原料数组
      DispartMaterial[] widthJustDisparts = widthJustDispart(prodSorted, desSorted);
      if(widthJustDisparts == null)
        return null;
      //总面积, 如果库存不够将返回
      float totalArea = getTotalArea(prodMaterials);
      float totalStockArea = getTotalArea(widthJustDisparts);
      if(totalStockArea < totalArea)
        return null;

      //按面积从大到小排序
      DispartMaterial[] desAreaSorted = sortMaterial(desMaterials, null, true);
      //得到可用于分切的组合(二维数组)
      ArrayList desList = new ArrayList(desSorted.length);
      ArrayList first = new ArrayList();
      areaJustDispart(desAreaSorted, prodMaterials, 0, totalArea, first, desList, 0);
      //是否有最佳组合, 若没有, 则需要得到最大宽度的余料，最小面积的组合
      boolean isBest = desList.size() > 0;
      if(!isBest)
      {
        DispartMaterial[] widthCanDisparts = widthCanDispart(desAreaSorted, prodSorted);
        if(widthCanDisparts != null)
        {
          first.clear();
          areaCanDispart(widthCanDisparts, 0, totalArea, first, desList, 0);
        }
      }
      //
      return desList.size() == 0 ? null : new CombinationInfo(isBest, prodSorted, desList);
    }
  }

  /**
   * 搭配可以用来分切的面积。
   * @param desMaterials 原料数组
   * @param startIndex 开始循环的下标
   * @param remainArea 剩余的面积
   * @param parentComb 父组合数组
   * @param results 组合的结果数组
   * @param level 递归层数
   */
  private static void areaCanDispart(DispartMaterial[] desMaterials, int startIndex,
      float remainArea, ArrayList parentComb, List results, int level)
  {
    if(level> 19)
      throw new EngineRuntimeException("递归的层数太大了！");

    for(int i=startIndex; i<desMaterials.length; i++)
    {
      //商表示可有几个最大面积的原料组成
      float maxArea = desMaterials[i].getMaxArea();
      int quotient = (int)(remainArea / maxArea) + (remainArea % maxArea > 0 ? 1 : 0);
      if(quotient <= 0)
        continue;

      boolean isOver = false;
      //库存数量是否够用
      if(desMaterials[i].getTotalNum() < quotient)
      {
        isOver = true;
        quotient = desMaterials[i].getTotalNum();
      }
      //如果够用并可整除，表示满足条件
      if(!isOver)
      {
        DispartMaterial desMaterial = (DispartMaterial)desMaterials[i].clone();
        desMaterial.setUsedNum(quotient);
        DispartMaterial[] oneResult = new DispartMaterial[parentComb.size()+1];
        parentComb.toArray(oneResult);
        oneResult[oneResult.length-1] = desMaterial;
        results.add(oneResult);
        if(i == desMaterials.length -1)
          break;
        quotient--;
      }
      //quotient减1。调用下一个递归
      //quotient=0时,不需要调用下一个递归。因为下一个循环就是一个递归了。不需要再重复一次
      for(; quotient>0; quotient--)
      {
        //不满足条件将组合clone并添加当前原料的clone传递给下一次递归
        DispartMaterial desMaterial = (DispartMaterial)desMaterials[i].clone();
        desMaterial.setUsedNum(quotient);
        ArrayList selfComb = (ArrayList)parentComb.clone();
        selfComb.add(desMaterial);
        //剩余的面积
        float selfRemainMin = remainArea
                          - (desMaterials[i].getMaxArea() + desMaterials[i].getMinArea())/2 * quotient;
        areaCanDispart(desMaterials, i+1, selfRemainMin, selfComb, results, level+1);
      }
    }
  }

  /**
   * 处理不能最佳分切的原料组合
   * @param widthCanDispart 宽度可用来分切的物料数组
   * @param prodSorted 排过序的产品数组
   * @return 返回可用来分切的原料的组合
   */
  private static DispartMaterial[] widthCanDispart(DispartMaterial[] widthCanDispart,
      DispartMaterial[] prodSorted)
  {
    DispartMaterial[] widthCanDispartSorted = sortMaterial(widthCanDispart, null);
    ArrayList widthCanDispartList = new ArrayList();
    ArrayList templist = new ArrayList();
    for(int i=0; i<widthCanDispartSorted.length; i++)
    {
      //得到各个物料组合比例
      MaterialCombination.procCombination(widthCanDispartSorted[i], prodSorted);
      MaterialCombination[] prodCombs = widthCanDispartSorted[i].getProdMaterialCombinations();
      if(prodCombs == null || prodCombs.length == 0)
        continue;
      templist.clear();
      for(int j=0; j<prodCombs.length; j++)
      {
        //得到该原料宽度一个组合比例中的各个产品数组
        DispartMaterial[] prodMaterials = prodCombs[j].getProdMaterials();
        for(int l=0; l<prodMaterials.length; l++)
          if(templist.indexOf(prodMaterials[l]) < 0)
            templist.add(prodMaterials[l]);
      }
      //处理可用的原料
      boolean isCanUse = true;
      for(int j=0; j<prodSorted.length; j++)
      {
        if(templist.indexOf(prodSorted[j]) < 0)
        {
          isCanUse = false;
          break;
        }
      }
      if(isCanUse)
        widthCanDispartList.add(widthCanDispartSorted[i]);
    }

    return widthCanDispartList.size() == 0 ? null :
           (DispartMaterial[])widthCanDispartList.toArray(new DispartMaterial[widthCanDispartList.size()]);
  }

  /**
   * 将物料分段方案（即如何分切）。
   * 选择分切方案后，得到如何分切组合的方法
   * @param combinationInfo 分切方案的组合信息
   * @param selectedIndex 选中的一种分切方案序号
   * @return 返回最终的分段方案
   */
  public static DispartMaterial[] splitSectionMode(CombinationInfo combinationInfo, int selectedIndex)
  {
    List list = combinationInfo.getDesMaterials();
    if(list == null)
      throw new EngineRuntimeException("CombinationInfo has not Original Materials");
    List results = new ArrayList();
    DispartMaterial[] originMaterials = (DispartMaterial[])list.get(selectedIndex);
    //循环原料数组
    for(int i=0; i<originMaterials.length; i++)
    {
      DispartMaterial originMaterial = originMaterials[i];
      /*2004.4.26 在搭配宽度时候已经处理过了
      最佳组合是没有处理过的： 处理产品相对于原料的各个宽度比例组合，并尝试组合的可用性
      if(combinationInfo.isBest())
        MaterialCombination.procCombination(originMaterial, combinationInfo.getSortedProdMaterials());
      */
      //2004.4.26 添加判断物料的可用性
      ArrayList prodCombinations = originMaterial.getProdMaterialCombinationList();
      if(prodCombinations == null)
        throw new EngineRuntimeException("非法物料组合！");

      prodCombinations = (ArrayList)prodCombinations.clone();
      float originLength = originMaterial.getLength();
      for(int j=0; j<originLength; j++)
      {
        if(combinationInfo.isBest())
          splitJustSection(originMaterial, j, prodCombinations, results);
        else
        {
          splitCanSection(originMaterial, j, prodCombinations, results);
        }
      }
    }
    //
    return (DispartMaterial[])results.toArray(new DispartMaterial[results.size()]);
  }

  /**
   * 得到最佳的好用的组合比例。宽度不剩余为原则。
   * @param prodCombinations 产品比率组合
   * @return 返回最佳的好用的组合比例,若没有返回null;
   */
  private static MaterialCombination getBestCombination(List prodCombinations)
  {
    return null;
  }

  /**
   * 一段物料的可分配方案。
   * @param originMaterial 原材料
   * @param section 段序号
   * @param prodCombinations 产品比率组合
   * @param results 分切组合的列表
   */
  private static void splitCanSection(DispartMaterial originMaterial, int section,
                                   List prodCombinations, List results)
  {
    //得到最佳的和较好的组合比例(宽度不剩余为原则),没有最好用较好
    MaterialCombination best = null; //
    MaterialCombination better = null;
    for(int i=0; i<prodCombinations.size(); i++)
    {
      MaterialCombination prodCombination = (MaterialCombination)prodCombinations.get(i);
      DispartMaterial[] prodMaterials = prodCombination.getProdMaterials();
      int[]             prodRates     = prodCombination.getProdRates();
      float width = 0;
      for(int j=0; j<prodRates.length; j++)
      {
        int num = prodMaterials[j].getRemainNum();
        num = prodRates[j] > num ? num : prodRates[j];
        width += prodMaterials[j].getWidth() * num;
      }
      //当前的组合是不能用，则继续下一个组合
      if(width == 0)
      {
        prodCombinations.remove(i);
        i--;
      }
      else if(width >= originMaterial.getMinWidth() && width <= originMaterial.getMaxArea())
      {
        best = prodCombination;
        break;
      }
      else if(better == null)
        better = prodCombination;
    }

    if(best != null || better != null)
    {
      MaterialCombination prodCombination = best != null ?  best : better;
      DispartMaterial[] prodMaterials = prodCombination.getProdMaterials();
      int[]             prodRates     = prodCombination.getProdRates();
      float width = 0;
      for(int j=0; j<prodMaterials.length; j++)
      {
        int num = prodMaterials[j].getRemainNum();
        num = prodRates[j] > num ? num : prodRates[j];
        width += prodMaterials[j].getWidth() * num;
        for(int l=0; l<num; l++)
        {
          DispartMaterial prodResult = (DispartMaterial)prodMaterials[j].clone();
          prodResult.originalMaterial = originMaterial;
          prodResult.section = section;
          prodResult.totalNum = 1;
          prodResult.usedNum = 0;
          results.add(prodResult);
        }
        prodMaterials[j].usedNum += num;
      }
      if(width < originMaterial.getMinWidth())
      {
        DispartMaterial prodResult = new DispartMaterial(null, originMaterial.getMaxWidth()-width, 1, 1);
        prodResult.originalMaterial = originMaterial;
        prodResult.section = section;
        prodResult.isFlotsam = true;
        results.add(prodResult);
      }
    }
    else //如果没有可用产品了
    {
      DispartMaterial prodResult = new DispartMaterial(null, originMaterial.getMaxWidth(), 1, 1);
      prodResult.originalMaterial = originMaterial;
      prodResult.section = section;
      prodResult.isFlotsam = true;
      results.add(prodResult);
    }
  }

  /**
   * 一段物料的完全分配方案。
   * @param originMaterial 原材料
   * @param section 段序号
   * @param prodCombinations 产品比率组合
   * @param results 分切组合的列表
   */
  private static void splitJustSection(DispartMaterial originMaterial, int section,
                                   List prodCombinations, List results)
  {
    //循环组合
    for(int i=0; i<prodCombinations.size(); i++)
    {
      MaterialCombination prodCombination = (MaterialCombination)prodCombinations.get(i);
      DispartMaterial[] prodMaterials = prodCombination.getProdMaterials();
      int[]             prodRates     = prodCombination.getProdRates();
      boolean isCanUse = true;
      for(int j=0; j<prodRates.length; j++)
      {
        //产品的比例 是否大于 产品的剩余需求量
        if(prodRates[j] > prodMaterials[j].getRemainNum())
        {
          isCanUse = false;
          break;
        }
      }
      //当前的组合不能用，则继续下一个组合
      if(!isCanUse)
      {
        prodCombinations.remove(i);
        i--;
        continue;
      }
      for(int j=0; j<prodMaterials.length; j++)
      {
        for(int l=0; l<prodRates[j]; l++)
        {
          DispartMaterial prodResult = (DispartMaterial)prodMaterials[j].clone();
          prodResult.originalMaterial = originMaterial;
          prodResult.section = section;
          prodResult.totalNum = 1;
          prodResult.usedNum = 0;
          results.add(prodResult);
        }
        prodMaterials[j].usedNum += prodRates[j];
      }
      //有一个可用的组合就可以返回了。因为只需要得到一种分切方法就可以了
      return;
    }
  }

  /**
   * 搭配面积。将剩余的面积余库存原料的面积相除。如果刚刚可整除的话，就可再配下一个原料。
   * 如果原料面积合计比剩余的面积大，尝试用小一点的面积的原料。
   * @param desMaterials 原料数组
   * @param prodMaterials 产品数组
   * @param startIndex 开始循环的下标
   * @param remainArea 剩余的面积
   * @param parentComb 父组合数组
   * @param results 组合的结果数组
   * @param level 递归层数
   */
  private static void areaJustDispart(DispartMaterial[] desMaterials, DispartMaterial[] prodMaterials,
      int startIndex, float remainArea, ArrayList parentComb, List results, int level)
  {
    if(level> 19)
      throw new EngineRuntimeException("递归的层数太大了！");
    for(int i=startIndex; i<desMaterials.length; i++)
    {
      //剩余的面积不能小于原料的最小面积
      float minArea = desMaterials[i].getMinArea();
      if(remainArea < minArea)
        continue;
      //商表示可有几个最大面积的原料组成
      float maxArea = desMaterials[i].getMaxArea();
      int quotient = (int)(remainArea / maxArea) + (remainArea % maxArea > 0 ? 1 : 0);
      boolean isOver = false;
      //如果最小面积*quotient > 剩余的面积，需要减少原料的数量
      if(minArea * quotient > remainArea)
      {
        isOver = true;
        quotient--;
      }
      //库存数量是否够用
      if(desMaterials[i].getTotalNum() < quotient)
      {
        isOver = true;
        quotient = desMaterials[i].getTotalNum();
      }
      //如果够用并可整除，表示满足条件
      if(!isOver)
      {
        DispartMaterial desMaterial = (DispartMaterial)desMaterials[i].clone();
        desMaterial.setUsedNum(quotient);
        DispartMaterial[] oneResult = new DispartMaterial[parentComb.size()+1];
        parentComb.toArray(oneResult);
        oneResult[oneResult.length-1] = desMaterial;
        //check valid
        if(checkMaterialCombination(oneResult, prodMaterials))
          results.add(oneResult);

        if(i == desMaterials.length -1)
          break;
        quotient--;
      }
      //quotient减1。调用下一个递归
      //quotient=0时,不需要调用下一个递归。因为下一个循环就是一个递归了。不需要再重复一次
      for(; quotient>0; quotient--)
      {
        //不满足条件将组合clone并添加当前原料的clone传递给下一次递归
        DispartMaterial desMaterial = (DispartMaterial)desMaterials[i].clone();
        desMaterial.setUsedNum(quotient);
        ArrayList selfComb = (ArrayList)parentComb.clone();
        selfComb.add(desMaterial);
        //剩余的面积
        float selfRemainMin = remainArea
                          - (desMaterials[i].getMaxArea() + desMaterials[i].getMinArea())/2 * quotient;
        areaJustDispart(desMaterials, prodMaterials, i+1, selfRemainMin, selfComb, results, level+1);
      }
    }
  }

  /**
   * 检验产品组合是否是合理的。因为有时候面积刚刚好可以搭配，但是宽度就不能搭配
   * @param desMaterialResults 一个组合的原料数组
   * @param prodMaterials 产品数组
   * @return 是否可用
   */
  private static boolean checkMaterialCombination(DispartMaterial[] desMaterialResults, DispartMaterial[] prodMaterials)
  {
    if(calcList.size() > 0)
      calcList.clear();

    for(int i=0; i<desMaterialResults.length; i++)
    {
      MaterialCombination[] prodCombs = desMaterialResults[i].getProdMaterialCombinations();
      if(prodCombs == null || prodCombs.length == 0)
        continue;
      for(int j=0; j<prodCombs.length; j++)
      {
        //得到该原料宽度一个组合比例中的各个产品数组
        DispartMaterial[] prodMaterialsResults = prodCombs[j].getProdMaterials();
        for(int l=0; l<prodMaterialsResults.length; l++)
          if(calcList.indexOf(prodMaterialsResults[l]) < 0)
            calcList.add(prodMaterialsResults[l]);
      }
    }
    //处理可用的原料
    boolean isCanUse = true;
    for(int j=0; j<prodMaterials.length; j++)
    {
      if(calcList.indexOf(prodMaterials[j]) < 0)
      {
        isCanUse = false;
        break;
      }
    }
    return isCanUse;
  }

  /**
   * 得到宽度可用于分割的原料数组
   * @param prodSorted 排过序产品数组
   * @param desMaterials 排过序原料数组
   * @return 返回可用于分割的原料数组
   */
  private static DispartMaterial[] widthJustDispart(DispartMaterial[] prodSorted,
      DispartMaterial[] desSorted)
  {
    List combinations = new ArrayList(desSorted.length);
    for(int i=0; i<desSorted.length; i++)
    {
      float remainMin = desSorted[i].getMinWidth();
      float remainMax = desSorted[i].getMaxWidth();
      //调用递归函数。得到一个原料宽度可被完全分割的数组
      widthJustOneDispart(prodSorted, 0, desSorted[i], remainMin, remainMax, combinations, 0);
    }
    //处理产品相对于原料的各个宽度比例组合
    for(int i=0; i<combinations.size(); i++)
    {
      MaterialCombination.procCombination(desSorted[i], prodSorted);
      ArrayList prodCombinations = desSorted[i].getProdMaterialCombinationList();
      if(prodCombinations == null  || prodCombinations.size() == 0)
      {
        combinations.remove(i);
        i--;
      }
    }
    return combinations.size() == 0 ? null :
           (DispartMaterial[])combinations.toArray(new DispartMaterial[combinations.size()]);
  }

  /**
   * 得到一个原料宽度可被完全分割的产品的组合。先判断是否可被最大宽度的产品分割。
   * 若可分割成整数倍的话，则下一个继续下一个组合。若有余料，则配尝试配较小一点的产品
   * @param prodMaterials 产品数组
   * @param startIndex 开始循环的下标
   * @param desMaterial 原料信息
   * @param remainMin 剩余的最小宽度
   * @param remainMax 剩余的最大宽度
   * @param combinations 组合列表数组
   * @param level 递归层数
   */
  private static void widthJustOneDispart(DispartMaterial[] prodMaterials, int startIndex,
      DispartMaterial desMaterial, float remainMin, float remainMax,
      List combinations, int level)
  {
    if(level> 19)
      throw new EngineRuntimeException("递归的层数太大了！");
    for(int i=startIndex; i<prodMaterials.length; i++)
    {
      //如果已经有可用于宽度搭配的物资，就返回
      if(combinations.indexOf(desMaterial) > -1)
        return;
      //商表示可有几个最大宽度的原料组成
      DispartMaterial prodMaterial = prodMaterials[i];
      int quotient = (int)(remainMax / prodMaterial.getWidth());
      if(quotient == 0)
        continue;
      //剩余的长度是否还是超出
      boolean isOver = remainMin >  prodMaterial.getWidth() * quotient;
      if(!isOver)
      {
        combinations.add(desMaterial);
        return;
      }
      //剩余的区间宽度
      float selfRemainMin = remainMin - prodMaterial.getWidth()*quotient;
      float selfRemainMax = remainMax - prodMaterial.getWidth()*quotient;
      //若没有找到搭配。商减1。调用下一个递归
      for(int j=quotient; j>0; j--)
      {
        widthJustOneDispart(prodMaterials, i+1, desMaterial,
          selfRemainMin, selfRemainMax, combinations, level+1);
      }
    }
  }

  /**
   * 根据物料的宽度降序排序
   * @param desMaterials 需要排序的物料数组
   * @param info 分割信息
   * @return 返回排序后的数组
   */
  private static DispartMaterial[] sortMaterial(
      DispartMaterial[] desMaterials, DispartInfo info)
  {
    return sortMaterial(desMaterials, info, false);
  }

  /**
   * 将物料降序排序
   * @param desMaterials 需要排序的物料数组
   * @param info 分割信息
   * @param isArea 是：按面积降序排序， 否：按宽度降序排序
   * @return 返回排序后的数组
   */
  private static DispartMaterial[] sortMaterial(DispartMaterial[] desMaterials,
      DispartInfo info, boolean isArea)
  {
    if(calcList.size() > 0)
      calcList.clear();

    List list = calcList;
    for(int i=0; i<desMaterials.length; i++)
    {
      if(info != null)
        desMaterials[i].setDispartInfo(info);
      //
      boolean isInsert = false;
      for(int j=0; j<list.size(); j++)
      {
        DispartMaterial temp = (DispartMaterial)list.get(j);
        if(isArea)
        {
          if(temp.getMaxArea() > desMaterials[i].getMaxArea())
            continue;
          isInsert = true;
        }
        else
        {
          if(temp.getWidth() > desMaterials[i].getWidth())
            continue;
          isInsert = true;
        }
        //如果可插入
        if(isInsert)
        {
          list.add(j, desMaterials[i]);
          break;
        }
      }
      if(!isInsert)
        list.add(desMaterials[i]);
    }
    return (DispartMaterial[])list.toArray(new DispartMaterial[list.size()]);
  }

  /**
   * 得到产品的总的面积
   * @param prodMaterials 产品数组
   * @return 返回产品的总的面积
   */
  private static float getTotalArea(DispartMaterial[] prodMaterials)
  {
    float totalArea = 0;
    for(int i=0; i<prodMaterials.length; i++)
      totalArea += prodMaterials[i].getArea() * prodMaterials[i].totalNum;

    return totalArea;
  }

  /**
   * 检查不用的物料并剔除, 即保留length > 0
   * @param materials 物料数组
   * @return 返回新的物料数组
   */
  private static DispartMaterial[] checkInvalid(DispartMaterial[] materials)
  {
    List list = new ArrayList(materials.length);
    for(int i=0; i<materials.length; i++)
    {
      if(materials[i].getLength() > 0)
        list.add(materials[i]);
    }
    return (DispartMaterial[])list.toArray(new DispartMaterial[list.size()]);
  }

  public static void main(String[] args)
  {
    //需要分切的数组
    /*DispartMaterial[] prodMaterials = new DispartMaterial[]{
      new DispartMaterial("6", 6, 1, 10),
      new DispartMaterial("5", 5, 1, 20),
      new DispartMaterial("4", 4, 1, 20),
      new DispartMaterial("3", 3, 1, 20)
    };
    //原料数组
    DispartMaterial[] desMaterials = new DispartMaterial[]{
      new DispartMaterial("20", 20, 10, 1),
      new DispartMaterial("10", 10, 10, 10)
    };*/
    DispartMaterial[] prodMaterials = new DispartMaterial[]{
      new DispartMaterial("10", 10, 1, 4),
      new DispartMaterial("5", 5, 1, 9),
    };
    //原料数组
    DispartMaterial[] desMaterials = new DispartMaterial[]{
      new DispartMaterial("20", 20, 6, 1)
    };
    //分切的信息
    DispartInfo info = new DispartInfo(0, 0);
    //最优组合数组
    CombinationInfo combinationInfo = calcCombination(prodMaterials, desMaterials, info);
    DispartMaterial[] results = splitSectionMode(combinationInfo, 0);
    return;
  }
}
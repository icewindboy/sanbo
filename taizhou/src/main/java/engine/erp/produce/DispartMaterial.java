package engine.erp.produce;

import engine.util.EngineException;
import engine.util.list.IntList;
import java.util.ArrayList;
import java.util.List;
/**
 * <p>Title: 分切对象</p>
 * <p>Description: 分切对象: 可以是分切需要的原料，也可以是分切的目标物资</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */
public final class DispartMaterial implements Cloneable
{
  //原料物资id
  Object id = null;
  //原料物资宽度
  private float width;
  //最小宽度
  private float minWidth;
  //最大宽度
  private float maxWidth;
  //原来物资长度(指长度分割的段数)
  private float length;
  //最小面积
  private float minArea;
  //最大面积
  private float maxArea;
  //总数量(总件数)
  int   totalNum;

  //-------------------------
  //产品对象
  //-------------------------
  //保存原料信息
  DispartMaterial originalMaterial = null;
  //面积:
  private float area;
  //是否是余料
  boolean isFlotsam = false;
  //分段序号
  int section = 0;

  //-------------------------
  //原料对象
  //-------------------------
  //需要用到的各个的宽度产品组合信息
  private ArrayList prodMaterialCombinations = null;
  //物料的已经用到的数量(件数)
  int   usedNum = 0;

  /**
   * 构造函数
   * @param id 原料物资
   * @param width 原料物资宽度
   * @param length 原来物资长度(指长度分割的段数)
   * @param totalNumber 总数量(总件数)//相同宽度和相同长度的库存数量
   */
  public DispartMaterial(Object id, float width, float length, int totalNum)
  {
    this.id = id;
    this.width = width;
    this.length = length;
    this.area = width * length;
    this.totalNum = totalNum;
  }

  /**
   * 设置分割系数
   * @param info 分割系数
   */
  public void setDispartInfo(DispartInfo info)
  {
    this.minWidth = this.width - info.ignoreMaxWidth;
    this.minArea = this.length * this.minWidth;
    this.maxWidth = this.width - info.ignoreMinWidth;
    this.maxArea = this.length * this.maxWidth;
  }

  public Object getId()
  {
    return id;
  }

  /**
   * 该物料是否是余料
   * @return 该物料是否是余料
   */
  public boolean isFlotsam()
  {
    return this.isFlotsam;
  }

  /**
   * 得到原料信息
   * @return 返回原料信息
   */
  public  DispartMaterial getOriginalMaterial()
  {
    return this.originalMaterial;
  }

  /**
   * 得到分段序号
   * @return 分段序号
   */
  public int getSection()
  {
    return this.section;
  }

  /**
   * 添加一种宽度产品组合信息
   * @param combination 宽度产品组合信息
   */
  public void addProdMaterialCombination(MaterialCombination combination)
  {
    if(combination == null)
      return;
    if(prodMaterialCombinations == null)
      prodMaterialCombinations = new ArrayList();
    prodMaterialCombinations.add(combination);
  }

  /**
   * 得到所有的宽度产品的组合信息列表
   * @return 所有的宽度产品的组合信息列表
   */
  MaterialCombination[] getProdMaterialCombinations()
  {
    if(prodMaterialCombinations == null || prodMaterialCombinations.size() == 0)
      return null;
    MaterialCombination[] combinations = new MaterialCombination[prodMaterialCombinations.size()];
    prodMaterialCombinations.toArray(combinations);
    return combinations;
  }

  /**
   * 得到所有的宽度产品的组合信息列表
   * @return 所有的宽度产品的组合信息列表
   */
  ArrayList getProdMaterialCombinationList()
  {
    return prodMaterialCombinations;
  }

  /**
   * 得到物料的宽度
   * @return 返回物料的宽度
   */
  public float getWidth(){return width;}

  /**
   * 得到物料的长度
   * @return 返回物料的长度
   */
  public float getLength()
  {
    return length;
  }

  /**
   * 得到物料的最小可用宽度
   * @return 返回物料的最小可用宽度
   */
  public float getMinWidth()
  {
    return minWidth;
  }

  /**
   * 得到物料的最大可用宽度
   * @return 返回物料的最大可用宽度
   */
  public float getMaxWidth()
  {
    return maxWidth;
  }

  /**
   * 得到原料物资最小可用面积
   * @return 返回原物资最小可用面积
   */
  public float getMinArea()
  {
    return this.minArea;
  }

  /**
   * 得到原料物资最大可用面积
   * @return 返回原料最大可用面积
   */
  public float getMaxArea()
  {
    return this.maxArea;
  }

  /**
   * 得到产品物资面积
   * @return 返回产品原料面积
   */
  public float getArea()
  {
    return this.area;
  }

  /**
   * 得到库存总数量
   * @return 返回库存总数量
   */
  public int getTotalNum()
  {
    return totalNum;
  }

  /**
   * 得到剩余的数量
   * @return 剩余的数量
   */
  public int getRemainNum()
  {
    return totalNum - usedNum;
  }

  /**
   * 得到已经使用的数量
   * @return 已经使用的数量
   */
  public int getUsedNum()
  {
    return usedNum;
  }

  /**
   * 是否还有剩余物料
   * @return 返回是否还有剩余物料
   */
  public boolean hasRemain()
  {
    return totalNum > 0 && totalNum > usedNum;
  }

  /**
   * 设置分割用的数量
   * @param num 分割用的数量
   */
  public void setUsedNum(int num)
  {
    this.usedNum = num;
  }

  /**
   * 实现克隆一个对象
   * @return 返回克隆好的对象
   */
  public Object clone()
  {
    try{
      return super.clone();
    }
    catch(CloneNotSupportedException ex){
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }
}

/**
 * <p>Title: 分切的信息</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */
final class DispartInfo
{
  //毛边的忽略的区间值
  //忽略的宽度的最小值
  float ignoreMinWidth;
  //忽略的宽度的最大值
  float ignoreMaxWidth;
  //废料的最小库存
  float flotsamMinWidth;

  /**
   * 构造函数
   * @param ignoreMinWidth 需要减去的最小宽度（一般是毛边）
   * @param ignoreMaxWidth 需要减去的最大宽度（一般是毛边）
   */
  public DispartInfo(float ignoreMinWidth, float ignoreMaxWidth)
  {
    this(ignoreMinWidth, ignoreMaxWidth, 0);
  }

  /**
   * 构造函数
   * @param ignoreMinWidth 需要减去的最小宽度（一般是毛边）
   * @param ignoreMaxWidth 需要减去的最大宽度（一般是毛边）
   * @param flotsamMinWidth 余料的最小宽度
   */
  public DispartInfo(float ignoreMinWidth, float ignoreMaxWidth, float flotsamMinWidth)
  {
    this.ignoreMinWidth = ignoreMinWidth;
    this.ignoreMaxWidth = ignoreMaxWidth;
  }

  /**
   * 是否允许废料
   * @return 返回是否允许废料
   */
  public boolean hasFlotsam()
  {
    return flotsamMinWidth > 0;
  }
}

/**
 * <p>Title: 物料组合比例</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */
final class MaterialCombination implements Cloneable
{
  //产品的信息列表
  private ArrayList prodMaterials = null;
  //与产品信息列表对应的各个数量的比例
  private IntList   prodRates     = null;

  /**
   * 添加一个产品信息，默认比例为1
   * @param prodMaterial 产品信息
   */
  public void addProdMaterialRate(DispartMaterial prodMaterial)
  {
    addProdMaterialRate(prodMaterial, 1);
  }

  /**
   * 添加一个产品信息，默认比例为1
   * @param prodMaterial 产品信息
   * @param time 比例系数
   */
  public void addProdMaterialRate(DispartMaterial prodMaterial, int time)
  {
    if(time < 0)
      return;
    if(prodMaterials == null)
    {
      prodMaterials = new ArrayList();
      prodMaterials.add(prodMaterial);
      prodRates = new IntList();
      prodRates.add(time);
    }
    else
    {
      int index = prodMaterials.indexOf(prodMaterial);
      if(index < 0)
      {
        prodMaterials.add(prodMaterial);
        prodRates.add(time);
      }
      else
        prodRates.set(index, prodRates.get(index)+time);
    }
  }

  /**
   * 得到该原料宽度一个组合比例中的各个产品数组
   * @return 返回该原料一个组合比例中的各个产品数组
   */
  public List getProdMaterialList()
  {
    return prodMaterials;
  }

  /**
   * 得到该原料宽度一个组合比例中的各个产品数组
   * @return 返回该原料一个组合比例中的各个产品数组
   */
  public DispartMaterial[] getProdMaterials()
  {
    return prodMaterials == null || prodMaterials.size()==0 ?  null :
          (DispartMaterial[])prodMaterials.toArray(new DispartMaterial[prodMaterials.size()]);
  }

  /**
   * 得到该原料宽度一个组合比例中数组
   * @return 返回得到该原料宽度一个组合比例中的各个产品数组
   */
  public IntList getProdRateList()
  {
    return prodRates;
  }

  /**
   * 得到该原料宽度一个组合比例中数组
   * @return 返回得到该原料宽度一个组合比例中的各个产品数组
   */
  public int[] getProdRates()
  {
    return prodRates == null || prodRates.size() == 0 ? null : prodRates.toArray();
  }

  /**
   * 深度克隆一个对象
   * @return 返回克隆好的对象
   */
  public Object clone()
  {
    try{
      MaterialCombination c = (MaterialCombination)super.clone();
      //克隆保存产品的信息列表的数组，不克隆里面的对象
      if(this.prodMaterials != null)
        c.prodMaterials = (ArrayList)this.prodMaterials.clone();
      //
      if(this.prodRates != null)
        c.prodRates = (IntList)this.prodRates.clone();
      //
      return c;
    }
    catch(CloneNotSupportedException ex){
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }

  /**
   * 处理产品相对于原料的各个宽度比例组合
   * @param desMaterial 原料对象
   * @param prodSorted 产品数组对象
   */
  public static void procCombination(DispartMaterial desMaterial,
      DispartMaterial[] prodMaterials)
  {
    synchronized(desMaterial)
    {
      if(desMaterial.getProdMaterialCombinationList() != null)
        desMaterial.getProdMaterialCombinationList().clear();

      float remainMin = desMaterial.getMinWidth();
      float remainMax = desMaterial.getMaxWidth();
      MaterialCombination firstComb = new MaterialCombination();
      //调用递归函数。得到一个原料宽度可被完全分割的组合
      //List combinations = new ArrayList(prodMaterials.length);
      widthJustCombination(desMaterial, prodMaterials, 0, remainMin, remainMax, firstComb);
    }
  }

  /**
   * 得到一个原料宽度可被完全分割的产品的组合。先判断是否可被最大宽度的产品分割。
   * 若可分割成整数倍的话，则下一个继续下一个组合。若有余料，则配尝试配较小一点的产品
   * @param prodMaterials 原料数组
   * @param startIndex 开始循环的下标
   * @param remainMin 剩余的最小宽度
   * @param remainMax 剩余的最大宽度
   * @param parentComb 父配料比例对象
   * @param combinations 组合列表数组
   */
  private static void widthJustCombination(DispartMaterial desMaterial,
      DispartMaterial[] prodMaterials, int startIndex, float remainMin,
      float remainMax, MaterialCombination parentComb)
  {
    for(int i=startIndex; i<prodMaterials.length; i++)
    {
      //商表示可有几个最大宽度的原料组合成
      float prodWidth = prodMaterials[i].getWidth();
      int quotient = (int)(remainMin / prodWidth) + (remainMin % prodWidth > 0 ? 1 : 0);
      if(quotient == 0)
        continue;
      boolean isOver = prodMaterials[i].getWidth() * quotient  > remainMax;
      quotient = isOver ? quotient-1 : quotient;
      //是否可整除
      if(!isOver)
      {
        //增加自身的组合
        MaterialCombination selfComb = (MaterialCombination)parentComb.clone();
        selfComb.addProdMaterialRate(prodMaterials[i], quotient);
        //添加组合
        desMaterial.addProdMaterialCombination(selfComb);
        if(i == prodMaterials.length-1)
          break;
        quotient--;
      }
      //quotient减1。调用下一个递归
      //quotient=0时,不需要调用下一个递归。因为下一个循环就是一个递归了。不需要再重复一次
      for(; quotient>0; quotient--)
      {
        //增加自身的组合
        MaterialCombination selfComb = (MaterialCombination)parentComb.clone();
        selfComb.addProdMaterialRate(prodMaterials[i], quotient);
        //剩余的区间宽度
        float selfRemainMin = remainMin - prodMaterials[i].getWidth()*quotient;
        float selfRemainMax = remainMax - prodMaterials[i].getWidth()*quotient;
        //尝试下一个小的料
        widthJustCombination(desMaterial, prodMaterials, i+1,
          selfRemainMin, selfRemainMax, selfComb);
      }
    }
  }
}
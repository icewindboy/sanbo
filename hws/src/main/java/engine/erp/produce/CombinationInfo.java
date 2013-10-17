package engine.erp.produce;

import java.util.List;
/**
 * <p>Title: 分切的组合信息</p>
 * <p>Description: 分切的组合信息</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */

public final class CombinationInfo
{
  private List desMaterials = null;
  private DispartMaterial[] prodSorted = null;
  private boolean isBest;

  CombinationInfo(boolean isBest, DispartMaterial[] prodSorted, List desMaterials)
  {
    this.isBest = isBest;
    this.prodSorted = prodSorted;
    this.desMaterials = desMaterials;
  }

  /**
   * 是否是最佳配料
   * @return 返回是否是最佳配料
   */
  public boolean isBest()
  {
    return isBest;
  }

  /**
   * 返回结果原料的数组。数组元素包含的是DispartMaterial[].
   * 指的是原料搭配的二维数组。DispartMaterial.getUsedNum()表示使用掉的数量
   * @return
   */
  public List getDesMaterials()
  {
    return desMaterials;
  }

  /**
   * 返回经过分组后的结果产品数组
   * @return 返回经过分组后的结果产品数组
   */
  public DispartMaterial[] getSortedProdMaterials()
  {
    return prodSorted;
  }
}
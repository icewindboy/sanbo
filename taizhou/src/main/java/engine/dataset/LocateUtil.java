package engine.dataset;

/**
 * <p>Title: 定位数据集的一些常量</p>
 * <p>Description: 定位数据集的一些常量</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public interface LocateUtil
{
  /** Allow partial matches for String columns.
  */
  public static final int PARTIAL         = 0x1;
  /** Search from current dataSet position.
  */
  public static final int NEXT            = 0x2;
  /** Search backwards from current dataSet postion.
  */
  public static final int PRIOR           = 0x4;
  /** CaseInsensitive search for String Columns.
  */
  public static final int CASE_INSENSITIVE = 0x8;
  /* Locate closest match. NOT SUPPORTED YET.
  public static final int CLOSEST         = 0x10;
  */

  /** Locate first occurance.
  */
  public static final int FIRST           = 0x20;
  /** Locate Last occurance
  */
  public static final int LAST            = 0x40;
  /** Fast semantics.  Search values not initialized for
      Next/Prior operations.  Values used from previous
      search.
  */
  public static final int FAST            = 0x80;
         // Used internally to force scoping of the search.
         //
         static final int DETAIL          = 0x100;

  public static final int NEXT_FAST       = NEXT|FAST;
  public static final int PRIOR_FAST      = PRIOR|FAST;


  static final        int START_MASK      = (FIRST|LAST|NEXT|PRIOR);
}

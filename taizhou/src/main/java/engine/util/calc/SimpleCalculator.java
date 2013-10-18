package engine.util.calc;

import java.math.BigDecimal;
/**
 * <p>Title: 简单计算器(四则运算类)</p>
 * <p>Description: 支持加减乘除括号乘方</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class SimpleCalculator
{
  private SimpleCalculator(){}

  //接近2维数组。Y维下标表示前一个字符优先级别类型，X维下标表示当前字符优先级别类型。
  //两个下标交叉的值表示是否需要检验的. 1=需要校验, 0=不需要校验
  private final static int[][] adj = {
    /*0, 1, 2, 3, 4, 5, 6, 7,*/  //7:  gettype保存的空的类型
    { 1, 1, 1, 1, 1, 0, 1, 0 }, /* 0:  0123456789 */
    { 1, 0, 0, 0, 0, 0, 0, 0 }, /* 1;  .         */
    { 1, 0, 0, 0, 0, 1, 0, 0 }, /* 2:  + -       */
    { 1, 0, 0, 0, 0, 1, 0, 0 }, /* 3:  * /       */
    { 1, 0, 0, 0, 0, 1, 0, 0 }, /* 4:  ^         */
    { 1, 0, 1, 0, 0, 1, 0, 0 }, /* 5:  (         */
    { 0, 0, 1, 1, 1, 0, 1, 0 }  /* 6:  )         */
  };
  /* 运算符号的栈外优先级别, 分别表示 0123456789, ., +-, *, ^, (, )*/
  private final static int[] osp      = { -1, -1, 1, 2, 3, 4, -1 };
  /* 运算符号的栈内优先级别*/
  private final static int[] isp      = { -1, -1, 1, 2, 3, 0, -1 };
  /* 允许输入的字符串*/
  private final static String canInputStr = "0123456789.+-*/^()";

  //字符优先级别类型
  private static int gettype[] = new int[256];
  static {
    for (int i = 0; i < 256; i++)
      gettype[i] = 7;

    gettype['0'] = 0;// gettype[48]
    gettype['1'] = 0;
    gettype['2'] = 0;
    gettype['3'] = 0;
    gettype['4'] = 0;
    gettype['5'] = 0;
    gettype['6'] = 0;
    gettype['7'] = 0;
    gettype['8'] = 0;
    gettype['9'] = 0;

    gettype['.'] = 1;

    gettype['+'] = 2;
    gettype['-'] = 2;

    gettype['*'] = 3;
    gettype['/'] = 3;

    gettype['^'] = 4;

    gettype['('] = 5;
    gettype[')'] = 6;
  }

  /**
   * 判断数学表达式是否是可用的
   * @param expression 数学表达式
   * @return 返回是否可用
   */
  public static boolean isInvalid(String expression)
  {
    try{
      verify(expression);
      return true;
    }
    catch(ExpressionException ex){
      return false;
    }
  }

  /**
   * 检验数学表达式是否是可用的
   * @param expression 数学表达式
   * @throws ExpressionException 检验表达式的异常
   */
  public static void verify(String expression) throws ExpressionException
  {
    if(expression == null)
      throw new NullPointerException("the expression is null");

    //是否有非法字符
    for (int i = 0; i < expression.length(); i++)
    {
      if (canInputStr.indexOf(expression.charAt(i)) < 0)
        throw new ExpressionException(ExpressionException.INVALID_CHAR, "表达式含有非法字符。只允许输入以下:0123456789.+-*/^()");
    }

    StringBuffer bufNumber = new StringBuffer(20);
    boolean isClearBuf = false;
    StringBuffer bufTemp = new StringBuffer(20);    //临时的
    //第一个字符的优先级别类型 用 左括号的优先级别类型代替
    char previousChar = '('; /*前一个字符, 第一个字符的前一个是没有的, 默认初始化'('*/
    int previousType = 5;  /* 前一个字符的优先级别类型, 表示第一个字符的前一个是没有的, 默认初始化5*/

    char currentChar;      /* 当前字符*/
    int currentType;    /* 当前字符的优先级别类型*/

    int leftCount = 0;  /* 表达式的左括号数量*/
    int rightCount = 0; /* 表达式的右括号数量*/

    for (int pos = 0; pos < expression.length(); pos++)
    {
      /* 统计表达式中的左右括号*/
      for (int i = 0; i <= pos; i++)
      {
        if ( expression.charAt(i) == '(' )
          leftCount++;
        if ( expression.charAt(i) == ')' )
          rightCount++;
      }

      currentChar = expression.charAt(pos);
      currentType = gettype[currentChar];
      //
      if (adj[previousType][currentType] == 1 )
      {
        //currentChar: 0123456789.
        if ( currentType <= 1 )
        {
          //清除以前的数字
          if(isClearBuf){
            bufNumber.setLength(0);
            isClearBuf = false;
          }

          /* 判断小数点是否可以接受 */
          if ( currentChar == '.' && bufNumber.toString().indexOf('.') > -1)
            throw new ExpressionException(ExpressionException.INVALID_NUMBER, "非法数字, 小数点太多");

          /* 限制操作数最多20位数 */
          bufNumber.append(currentChar);
          if(bufNumber.length() > 20)
            throw new ExpressionException(ExpressionException.NUMBER_TOO_LONG, "数值太大了, 最大20位");
        }
        else
        {
          isClearBuf = true;
          /* 判断操作符的排列的正确性*/
          if (currentType > 2 && currentType < 4 && previousType > 2 && previousType < 4)
            throw new ExpressionException(ExpressionException.INVALID_OPERATE_SYMBOL, "非法使用运算符");

          /* 判断该右括号是否可以接受 */
          if ( currentChar == ')' )
          {
            if ( leftCount < rightCount )
              throw new ExpressionException(ExpressionException.PARENTHESIS_NOT_MATCH, "括号的数量不匹配");
            else if (previousChar == '(')
              throw new ExpressionException(ExpressionException.INVALID_OPERATE_SYMBOL, "非法使用运算符");
          }
          /* 判断是否除0错误 */
          else if(currentChar == '/')
          {
            bufTemp.setLength(0);
            for(int i = pos+1; i<expression.length(); i++){
              char numChar = expression.charAt(i);
              if(gettype[numChar] <= 1)
                bufTemp.append(numChar);
              else
                break;
            }
            if(Double.parseDouble(bufTemp.toString())==0)
              throw new ExpressionException(ExpressionException.DIVISOR_IS_ZERO, "除数为0错误");
          }
        }
      }
      //保存上一个字符
      previousChar = currentChar;
      previousType = currentType;
    }
  }

  /**
   * 计算数学表达式(四则运算)
   * @param expression 数学表达式
   * @return 返回计算结果
   */
  public synchronized static BigDecimal arithmetic(String expression)
      throws ExpressionException
  {
    //
    verify(expression);

    int thetype;                        /* 当前字符的类别吗 */
    int optop = 0;                      /* 运算符堆栈顶指针 */
    int numtop = 0; 		                /* 操作数堆栈顶指针 */
    char op[] = new char[64]; 			    /* 运算符堆栈*/
    BigDecimal num[] = new BigDecimal[64];/* 操作符堆栈*/
    StringBuffer numBuf = new StringBuffer(20);//

    /* 为了便于判断, 在原表达式的基础上加上一对括号. 存放临时表达式*/
    String s = "(" + expression + ")";
    op[optop++] = '(';

    int i = 1;
    while (optop > 0)
    {
      thetype = gettype[ s.charAt(i) ];
      if ( thetype == 0 || (thetype == 2 && s.charAt(i - 1) == '(' ) ) /*操作數進棧 */
      {
        /*double t = 0.0;  // 操作数整数部分的值
        double f = 0;    // 操作數小數部分的值
        double r = 0.1;  // 小數的权值, 0.1, 0.01, 0.001...
        double flag = 1; // 当前正在读取的是小数部分还是整数部分; 1表示整数部分; 0表示小數部分
        double sign = 1; // 操作數的正负符号
        */
        numBuf.setLength(0);
        if ( thetype== 2 ) /* 操作數的第一個字符为正负号 */
        {
          if (s.charAt(i++) == '-')
            numBuf.append('-');
          //sign = -1;
          if ( s.charAt(i) == '(' )     /* 若后面跟的是左括号, 則操作數0进栈, 正负号作为运算符进栈 */
          {
            op[optop++] = s.charAt(i - 1);
          }
        }
        char numChar;
        for (; gettype[(numChar=s.charAt(i))] <= 1; i++)
          numBuf.append(numChar);

        num[numtop++] = new BigDecimal(numBuf.length() > 0 ? numBuf.toString() : "0") ;//(t + f) * sign;
      }
      else
      {
        /* 栈顶操作符优先级高于当前操作符优先级，则当前操作符进栈*/
        if (osp[thetype] > isp[gettype[op[optop-1]]])
        {
          op[optop++] = s.charAt(i++);
        }
        else
        {
          /*栈顶操作符优先级低于当前操作符优先级，则依次取栈顶运算符进行计算*/
          while(osp[thetype] <= isp[gettype[op[optop-1]]] && op[optop-1] != '(' )
          {
            BigDecimal x = num[numtop-2];
            BigDecimal y = num[numtop-1];

            BigDecimal result = null;
            switch (op[optop-1])
            {
              case '+':
                result = x.add(y);
                break;
              case '-':
                result = x.subtract(y);
                break;
              case '*':
                result = x.multiply(y);
                break;
              case '^':
                result = new BigDecimal(Math.pow(x.doubleValue(), y.doubleValue()));
                break;
              case '/':
                result = x.divide(y, BigDecimal.ROUND_UNNECESSARY);
                break;
            }

            num[numtop-2] = result;
            numtop--;
            optop--;
          }  /* end of while */
          if ( op[optop-1] == '(' && s.charAt(i) == ')' ) /* 退去栈顶的右括号 */
            optop--;
          else
            op[optop++] = s.charAt(i); /* 当前操作符进栈*/
          i++;
        }   /* end of if */
      }   /* end of if  */
    }   /* end of while top */
    return num[0];
  }
}
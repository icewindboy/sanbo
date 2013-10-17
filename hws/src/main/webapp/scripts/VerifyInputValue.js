// 判断字符串是否为数字型
function isNumber(CheckStr){
  if (CheckStr==null)
    return true;
  var checkOK  = "-0123456789.";
  var allValid = true;
  for (var i = 0; i < CheckStr.length; i++)
  {
    ch = CheckStr.charAt(i);
    for (var j = 0; j < checkOK.length; j++)
    {
      if (ch == checkOK.charAt(j))
        break;
      if (j == checkOK.length-1)
      {
        allValid = false;
        break;
      }
    }
  }
  return allValid;
}

//打开URL
function openurl(url,iWidth,iHeight,noresizable){
  var Width;
  var Height;
  if(iWidth > 0)    Width = iWidth
  else    Width = screen.width-10;

  if(iHeight > 0)    Height = iHeight
  else    Height = screen.height-130;

  var winretu = window.open(url,"","left=0,top=0,width="+ Width +",height="+ Height +",menubar=yes,toolbar=yes,status=no,scrollbars=yes,resizable="+ (noresizable ? "yes" : "no"));//790,height=455
  return winretu;
}
//弹出一个模态对话框
function showModal(url,iWidth,iHeight,noresizable){
  var Width;
  var Height;
  if(iWidth > 0)    Width = iWidth
  else    Width = screen.width-10;

  if(iHeight > 0)    Height = iHeight
  else    Height = screen.height-130;

  var winretu;
  if(window.showModalDialog)
    winretu = window.showModalDialog(url," ","dialogWidth:"+ Width +"px;dialogHeight:"+ Height +"px;center:yes;help:no;resizable:" +(noresizable ? "yes" : "no")+ ";status:no");
  else
    winretu = openurl(url,Width,Height,noresizable);
  return winretu;
}
//弹出日历的框架
function PopUpCalendarDlg(ctrlobj)
{
  showx = event.screenX - event.offsetX - 4 - 210 ; // + deltaX;
  showy = event.screenY - event.offsetY + 18; // + deltaY;
  newWINwidth = 210 + 4 + 18;

  var retval;
  if(window.showModalDialog)
    retval = window.showModalDialog("../js_css/calendardlg.htm", "", "dialogWidth:197px; dialogHeight:210px; dialogLeft:"+showx+"px; dialogTop:"+showy+"px; status:no; directories:yes;scrollbars:no;Resizable=no; ");
  else
    retval = window.open("../js_css/calendardlg.htm","","left="+showx+"px,top="+showy+"px,width=197px,height=210px,menubar=yes,toolbar=no,status=no,scrollbars=no,resizable=no");

  if( retval != null )
  {
     getFocusCom(ctrlobj).value = retval;
  }
}
//
function getFocusCom(e_name){
  var e = null;
  if (e_name == null) return null;

  for(var i=0;i<form1.elements.length;i++) {
    e = form1.elements[i];
    if (e.name == e_name.name) break;
  }

  return e;
}
//功能：日期检查函数，支持3种年、月、日之间的分隔符 "-"、"."和"/"可以选择年、月、日是否应该完整。
//		正确的日期格式为：2001-2-13 2001 2001-2 2001.2.13  2001.2 2001/2/3，日期范围为 1-1-1 到 9999-12-31
//		同时，对当前年当前月的天数也做了判断，如：2001-2-29 2001-4-31 都是非法的日期
//参数：strDate ---- 需要判断的日期字符串
//		intFlag: 1 ---- 可以没有日  2 ---- 可以没有日和月 0 ---- 年月日必须齐全
//返回值：true ---- 日期合法	false ---- 日期不合法
function isDate(strDate,intFlag)
{
	var strCheckDate = strDate + "";//进一步确认哪来判断的肯定是一串字符串

	if(strCheckDate == "")		//空字符串,不是合法的日期字符串，返回false
	{
		return true;
	}

	//判断传进来的数据是那种格式写成日期
	var intIndex = -1;		//利用正则表达式，查找字符串中是否包含某个字符，没找到为-1,否则为 （0 - String.length - 1）
	var arrDate;			//分别存储年月日
	var regExpInfo = /\./;		//正则表达式，匹配第一个出现 "."的位置

	//在这里之所以不使用replace函数把所有的"."和"/"换成"-",然后分别存储年月日，是因为用户有可能输入 2001/3-2,就判断不出它是不合法日期了
	intIndex = strCheckDate.search(regExpInfo);//查找是否含有 "."
	if(intIndex == - 1)			   //不包含
	{
		regExpInfo = /-/;
		intIndex = strCheckDate.search(regExpInfo);

		if(intIndex == -1)
		{
			regExpInfo = /\//;	  //查找是否含有 "/"
			intIndex = strCheckDate.search(regExpInfo);

			if(intIndex == -1)
			{
				arrDate = new Array(strCheckDate);//只包含年
			}
			else
			{
				arrDate = strCheckDate.split("/");//2001/3/7 型
			}
		}
		else
		{
			arrDate = strCheckDate.split("-");	//2001-3-7 型
		}
	}
	else
	{
		arrDate = strCheckDate.split(".");		//2001.3.7 型
	}

	if(arrDate.length > 3)					//如果分离出来的项超过3，除了年月日还有其它的，不合法日期，返回false
	{
		return false;
	}
	else if(arrDate.length > 0)
	{
		//判断年是否合法
		if(IsIntNum(arrDate[0]))			//是数字型
		{
		//------- 2002.06.10 modified by sgx
                  //begin
			if(arrDate[1]=="08")
			{
			 arrDate[1] = 8;
			}
		        if(arrDate[1]=="09")
			{
			 arrDate[1] = 9;
			}
		//end

			if(parseInt(arrDate[0]) < 1 || parseInt(arrDate[0]) > 9999)//年范围为1 - 9999
			{
				return false;
			}
		}
		else
		{
			return false;		 //年不是是数字型，错误
		}

		//判断月是否合法
		if(arrDate.length > 1)
		{
			if(IsIntNum(arrDate[1])) //是数字型
			{
				if(parseInt(arrDate[1]) < 1 || parseInt(arrDate[1]) > 12)
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else	//没有月
		{
			if(intFlag != 2)	 //必须得有月
			{
				return false;
			}
		}

		//判断日是否合法
		if(arrDate.length > 2)
		{
			if(IsIntNum(arrDate[2])) //是数字型
			{
				var intDayCount = ComputerDay(parseInt(arrDate[0]),parseInt(arrDate[1]));
				if(intDayCount < parseInt(arrDate[2]))
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			if(intFlag == 0)	  //必须得有日
			{
				return false;
			}
		}
	}
	return true;
}

//参数：CheckStr ---- 需要判断的字符串
//返回值：true ---- 数字型 false ---- 非数字型
// 判断字符串是否为数字型
function IsIntNum(CheckStr)
{
	var checkOK  = "0123456789";
	var allValid = true;
	for (var i = 0; i < CheckStr.length;  i++)
	{
		ch = CheckStr.charAt(i);
		for (var j = 0;  j < checkOK.length;  j++)
			if (ch == checkOK.charAt(j))
					break;
		if (j == checkOK.length)
		{
				allValid = false;
				break;
		}

	}
	if (!allValid)
		return (false);
	else
		return (true);
}
//**********************************************************************************************************
//功能：判断intYear年intMonth月的天数
//返回值：intYear年intMonth月的天数
function ComputerDay(intYear,intMonth)
{
    var dtmDate = new Date(intYear,intMonth,-1);
    var intDay = dtmDate.getDate() + 1;

    return intDay;
}
//功能：去掉字符串前后空格
//返回值：去掉空格后的字符串
function RemoveBrank(strSource)
{
 return strSource.replace(/^\s*/,'').replace(/\s*$/,'');
}

//得到下一个输入框
function getNextElement(field) {
  var form = field.form;
  for(var e = 0; e <form.elements.length; e++)
    if(field == form.elements[e])
      break;
  //找下一个输入域
  for(e++; e <form.elements.length; e++)
    if(form.elements[e].type=="text" && (!form.elements[e].readOnly))
      break;
  if(e == form.elements.length)
  {
    //找到第一个输入域
    for(e = 0; e <form.elements.length; e++)
    {
      if(form.elements[e].type=="text"&& (!form.elements[e].readOnly))
        break;
    }
    if(e == form.elements.length)
      return field;
    else
      return  form.elements[e];
  }
  else
    return form.elements[e];
}
/**
 * 检查表单中至少有一个checkbox打勾
 */
function Check(form)
{
  for(var i=0;i<form.elements.length;i++)
  {
    var e = form.elements[i];
    if(e.type == 'checkbox' && e.checked == true)
    {
      return true;
    }
  }
  return false;
}
/**
 * 改变按钮的风格
 */
function changeStyle(isTextFill, isSelectFill) {
  for(var i = 0; i < document.forms.length; i++)
  {
    for(var j = 0; j < document.forms[i].elements.length; j++)
    {
      var obj = document.forms[i].elements[j];
      if (obj.type == "button")
        obj.className = "button";
    }
  }
}
/**
 * 改变输入框的宽度为100％
 */
function changeInputWidth() {
  for(var i = 0; i < document.forms.length; i++){
    for(var j = 0; j < document.forms[i].elements.length; j++)
    {
      var obj = document.forms[i].elements[j];
      if(obj.type == "text" || obj.type=="password" || obj.type == "select-one")
        obj.style.width = "100%";
    }
  }
}

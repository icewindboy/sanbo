//JavaScript脚本语言子程序集合

//修改指定表格单元的背景与前景
function ChangeTdColor(mytd)
{
  mytd.bgColor="#6699FF";
  mytd.style.color="white";
}

//把表格单元的背景和前景恢复原始状态
function ResetTdColor(mytd)
{
  mytd.bgColor="";
  mytd.style.color="Black";
}

//显示与隐藏指定层
function Show(Layer)
{
  if(Layer.style.visibility=="hidden")
    Layer.style.visibility="visible";
  else
    Layer.style.visibility="hidden";
}

//从列表框Select1向列表框Select2添加项目
function Add(Select1,Select2)
{
  var item=0;
  if(Select1.length<1)
  {
    alert("没有可加入的项！！！");
    return;
  }
  if(Select1.selectedIndex>=0)
    item=Select1.selectedIndex;
  Disp.value=item;
  Select2.options[Select2.length]=new Option(Select1.options[item].value,Select1.options[item].value);
  Select1.options[item]=null;
}


//从列表框Select2向列表框Select1添加项目
function Remove(Select2,Select1)
{
  var item=0;
  if(Select2.length<1)
  {
    alert("没有可移除的项！！！");
    return;
  }
  if(Select2.selectedIndex>=0)
    item=Select2.selectedIndex;
  Disp.value=item;
  Select1.options[Select1.length]=new Option(Select2.options[item].value,Select2.options[item].value);
  Select2.options[item]=null;
}

//树形结构
var SubTree=0;
var images=new Array();
images[0]=new Image(20,18);
images[0].src="images/plus.gif";   //结点为展开
images[1]=new Image(20,18);
images[1].src="images/minus.gif";  //结点已展开
images[2]=new Image(20,18);
images[2].src="images/open.gif";   //界面图

function ShowHide(Layer, Imager)
{
  if(Layer.style.display=="none")
  {
    Layer.style.display="block";
    Imager.src=images[1].src;
  }
  else
  {
    Layer.style.display="none";
    Imager.src=images[0].src;
  }
}

//ID:结点ID号（唯一标识）
//MOTHER：指出其上级结点ID，为0时表示为根结点。
//DESCRIPT：结点描述
//ISFOLDER：是否为结点集，值（TRUE，FALSE）
//Script：结点名
function Node(Id, Mother, Descript, IsFolder, url)
{
  this.Id=Id;
  this.Descript=Descript;
  this.Mother=Mother;
  this.IsFolder=IsFolder;
  this.url=url;
  return this;
}

function ShowTree(Nodes, Node, isShowRoot)
{
  var i=0;
  if(isShowRoot){
    document.write("<a class=tree>"+Node.Descript+"</a>");
    document.write("<div id='Lroot'>");
  }
  while(i<Nodes.length) //遍历所有结点
  {
    if(Nodes[i].Mother==Node.Id) //找出结点集的子结点
    {
      //SubTree++;  //结点层次递增
      ShowTreeNode(Nodes, Nodes[i]); //递归调用
      //SubTree--; //结点层次还原
    }
    i++;
  }
  if(isShowRoot)
    document.write("</div>");
}
//显示
function ShowTreeNode(Nodes, Node)
{
  var i=0,j;
  if(Node.IsFolder==true)  //如果结点为结点集
  {
    for(j=0;j<SubTree;j++) //输出结点集描述
      document.write("&nbsp;&nbsp;");
    document.write("<a class=tree href='javascript:ShowHide(L"+Node.Id+",I"+Node.Id+")'><img id='I"+Node.Id+"' src='"+images[0].src+"' border=0>"+Node.Descript+"</a><br>");
    document.write("<div id='L"+Node.Id+"' style='display:none'>");

    while(i<Nodes.length) //遍历所有结点
    {
      if(Nodes[i].Mother==Node.Id) //找出结点集的子结点
      {
        SubTree++;  //结点层次递增
        ShowTreeNode(Nodes,Nodes[i]); //递归调用
        SubTree--; //结点层次还原
      }
      i++;

    }
    document.write("</div>");
  }
  else
  {  //如果结点为叶子结点，则直接输出
    for(j=0;j<SubTree;j++)
      document.write("&nbsp;&nbsp;");
    document.write("<a class=tree><img src='"+images[2].src+"' border=0>" + Node.Descript+"</a><br>");
  }

//  if(SubTree==0)
//    document.write("</div>");
}
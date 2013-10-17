// BrowserCheck Object
function TBrowser() {
  var b = navigator.appName;
  if (b=="Netscape")
    this.b = "ns";
  else if (b == "Microsoft Internet Explorer")
    this.b = "ie";
  else
    this.b = b;
  this.version = navigator.appVersion;
  this.v = parseInt(this.version);
  this.ns = (this.b=="ns" && this.v>=4);
  this.ns4 = (this.b=="ns" && this.v==4);
  this.ns5 = (this.b=="ns" && this.v==5);
  this.ie = (this.b=="ie" && this.v>=4);
  this.ie4 = (this.version.indexOf('MSIE 4')>0);
  this.ie5 = (this.version.indexOf('MSIE 5')>0);
  this.min = (this.ns || this.ie);
  return this;
}
//----------------------
//动态层 控件客户端脚本
//----------------------
/**
 * @param id 需要的DIV或FRAME的ID
 * @param is TBrowser对象
 * @param nestref 嵌入到该id的下一级的DIV或FRAME的ID(用于判断是否有嵌入对象的引用)
 * @param frame 框架的ID(用于判断是否是框架网页)
 * @return 动态层对象
 */
function GetDynamicLayer(id, is, nestref, frame)
{
  var dynLayer = new TDynamicLayer(id, is, nestref);
  if (!is.ns5 && !TDynamicLayer.set && !frame)
    TDynamicLayerInit(is);
  dynLayer.frame = frame || self;
  if (is.ns) {
    if (is.ns4) {
      if (!frame)
      {
        if (!nestref)
          var nestref = TDynamicLayer.nestRefArray[id];

        dynLayer.css = (nestref)? eval("document."+nestref+".document."+id) : document.layers[id];
      }
      else
        dynLayer.css = (nestref)? eval("frame.document."+nestref+".document."+id) : frame.document.layers[id];

      dynLayer.elm = dynLayer.event = dynLayer.css;
      dynLayer.doc = dynLayer.css.document;
    }
    else if (is.ns5)
    {
      dynLayer.elm = document.getElementById(id)
      dynLayer.css = dynLayer.elm.style;
      dynLayer.doc = document;
    }
    dynLayer.x = dynLayer.css.left;
    dynLayer.y = dynLayer.css.top;
    dynLayer.w = dynLayer.css.clip.width;
    dynLayer.h = dynLayer.css.clip.height;
  }
  else if (is.ie) {
    dynLayer.elm = dynLayer.event = dynLayer.frame.document.all[id];
    dynLayer.css = dynLayer.frame.document.all[id].style;
    dynLayer.doc = document;
    dynLayer.x = dynLayer.elm.offsetLeft;
    dynLayer.y = dynLayer.elm.offsetTop;
    dynLayer.w = (is.ie4)? dynLayer.css.pixelWidth : dynLayer.elm.offsetWidth;
    dynLayer.h = (is.ie4)? dynLayer.css.pixelHeight : dynLayer.elm.offsetHeight
  }
  dynLayer.obj = id + "DynamicLayer";
  //dynLayer.obj = dynLayer; //why before code is error??
  eval(dynLayer.obj + "=dynLayer");
  return dynLayer;
}

TDynamicLayer.nestRefArray = new Array();
TDynamicLayer.refArray = new Array();
TDynamicLayer.refArray.i = 0;
TDynamicLayer.set = false;
// TDL_Init Function
function TDynamicLayerInit(nestref) {
  var is = this.browser;
  if (!TDynamicLayer.set)
    TDynamicLayer.set = true;

  var ref;//嵌入对象的引用
  if (is.ns) {
    if (nestref)
      ref = eval('document.'+nestref+'.document');
    else {
      nestref = '';
      ref = document;
    }
    for (var i=0; i<ref.layers.length; i++)
    {
      var divname = ref.layers[i].name;
      TDynamicLayer.nestRefArray[divname] = nestref;
      var index = divname.indexOf("Div")
      if (index > 0) {
        //alert("GetDynamicLayer('"+divname +"',is,'"+ nestref+"')");
        eval(divname.substr(0,index)+" = GetDynamicLayer('"+divname +"',is,'"+ nestref+"')");
      }
      if (ref.layers[i].document.layers.length > 0) {
        TDynamicLayer.refArray[TDynamicLayer.refArray.length] = (nestref=='')? ref.layers[i].name : nestref+'.document.'+ref.layers[i].name;
      }
    }
    if (TDynamicLayer.refArray.i < TDynamicLayer.refArray.length) {
      TDynamicLayerInit(TDynamicLayer.refArray[TDynamicLayer.refArray.i++])
    }
  }
  else if (is.ie) {
    for (var i=0; i<document.all.tags("DIV").length; i++) {
      var divname = document.all.tags("DIV")[i].id;
      var index = divname.indexOf("Div")
      if (index > 0) {
        eval(divname.substr(0,index)+" = GetDynamicLayer('"+divname+"', is)");
      }
    }
  }
  return true;
}

function TDynamicLayer(id, browser, nestref) {
  this.id = id;
  this.browser = browser;
  //this.nestRefArray = new Array();//
  //this.refArray = new Array();//
  //this.refArray.i = 0;
  //this.set = false;

  this.frame = null;
  this.elm = null;
  this.event = null;
  this.css = null;
  this.doc = null;
  this.x = null;
  this.y = null;
  this.w = null;
  this.h = null;
  this.nestref = nestref;
  this.obj = "dyn_obj";

  this.moveTo = TDL_MoveTo;
  this.moveX = TDL_MoveX;
  this.moveY = TDL_MoveY;
  this.moveBy = TDL_MoveBy;
  this.show = TDL_Show;
  this.hide = TDL_Hide;
  this.setWidth = TDL_SetWidth;
  this.setHeight = TDL_SetHeight;

  this.slideInit = TDL_SlideInit;
  this.slideTo = TDL_SlideTo;
  this.slideBy = TDL_SlideBy;
  this.slideStart = TDL_SlideStart;
  this.slide = TDL_Slide;
  this.onSlide = new Function();
  this.onSlideEnd = new Function();

  this.clipInit = TDL_ClipInit;
  this.clipTo = TDL_ClipTo;
  this.clipBy = TDL_ClipBy;
  this.clipValues = TDL_ClipValues;

  this.write = TDL_Write;
}

function TDL_MoveTo(x, y) {
  var is = this.browser;
  if (x != null) {
    this.x = x;
    if (is.ns)
      this.css.left = this.x;
    else
      this.css.pixelLeft = this.x;
  }
  if (y!=null) {
    this.y = y;
    if (is.ns)
      this.css.top = this.y;
    else
      this.css.pixelTop = this.y;
  }
}
function TDL_MoveX(x) {
  if (x!=null)
    this.moveTo(x, null);
}
function TDL_MoveY(y) {
  if (y!=null)
    this.moveTo(null, y);
}
function TDL_MoveBy(x,y) {
  this.moveTo(this.x+x,this.y+y)
}
function TDL_Show() {
  this.css.visibility = (this.browser.ns4)? "show" : "visible"
}
function TDL_Hide() {
  this.css.visibility = (this.browser.ns4)? "hide" : "hidden"
}
function TDL_SetWidth(w) {
  this.css.width = w+"px"
}
function TDL_SetHeight(h) {
  this.css.height = h+"px"
}
//DynLayerTest = new Function('return true')

// Slide Methods
function TDL_SlideTo(endx, endy, inc, speed, fn) {
  if (endx==null)
    endx = this.x;
  if (endy==null)
    endy = this.y;
  var distx = endx-this.x;
  var disty = endy-this.y;
  this.slideStart(endx, endy, distx, disty, inc, speed, fn);
}
function TDL_SlideBy(distx, disty, inc, speed, fn) {
  var endx = this.x + distx;
  var endy = this.y + disty;
  this.slideStart(endx, endy, distx, disty, inc, speed, fn)
}
function TDL_SlideStart(endx,endy,distx,disty,inc,speed,fn) {
  if (this.slideActive)
    return;
  if (!inc)
    inc = 10;
  if (!speed)
    speed = 20;
  var num = Math.sqrt(Math.pow(distx,2) + Math.pow(disty,2))/inc;
  if (num==0)
    return;
  var dx = distx/num;
  var dy = disty/num;
  if (!fn)
    fn = null;
  this.slideActive = true;
  this.slide(dx, dy, endx, endy, num, 1, speed, fn);
}
function TDL_Slide(dx, dy, endx, endy, num, i, speed, fn) {
  if (!this.slideActive)
    return;
  if (i++ < num) {
    this.moveBy(dx, dy);
    this.onSlide();
    if (this.slideActive)
      setTimeout(this.obj+".slide("+dx+","+dy+","+endx+","+endy+","+num+","+i+","+speed+",\""+fn+"\")",speed);
    else
      this.onSlideEnd();
  }
  else {
    this.slideActive = false;
    this.moveTo(endx,endy);
    this.onSlide();
    this.onSlideEnd();
    eval(fn);
  }
}
function TDL_SlideInit() {}

// Clip Methods
function TDL_ClipInit(clipTop, clipRight, clipBottom, clipLeft) {
  if (is.ie) {
    if (arguments.length==4)
      this.clipTo(clipTop,clipRight,clipBottom,clipLeft);
    else if (is.ie4)
      this.clipTo(0,this.css.pixelWidth,this.css.pixelHeight,0);
  }
}
function TDL_ClipTo(t,r,b,l) {
  if (t==null)
    t = this.clipValues('t');
  if (r==null)
    r = this.clipValues('r');
  if (b==null)
    b = this.clipValues('b');
  if (l==null)
    l = this.clipValues('l');
  if (is.ns) {
    this.css.clip.top = t;
    this.css.clip.right = r;
    this.css.clip.bottom = b;
    this.css.clip.left = l;
  }
  else if (is.ie)
    this.css.clip = "rect("+t+"px "+r+"px "+b+"px "+l+"px)";
}
function TDL_ClipBy(t, r, b, l) {
  this.clipTo(this.clipValues('t')+t, this.clipValues('r')+r,
              this.clipValues('b')+b, this.clipValues('l')+l );
}
function TDL_ClipValues(which) {
  var is = this.browser;
  if (is.ie)
    var clipv = this.css.clip.split("rect(")[1].split(")")[0].split("px");
  if (which=="t")
    return (is.ns)? this.css.clip.top : Number(clipv[0]);
  if (which=="r")
    return (is.ns)? this.css.clip.right : Number(clipv[1]);
  if (which=="b")
    return (is.ns)? this.css.clip.bottom : Number(clipv[2]);
  if (which=="l")
    return (is.ns)? this.css.clip.left : Number(clipv[3]);
}

// Write Method
function TDL_Write(html) {
  var is = this.browser;
  if (is.ns) {
    this.doc.open();
    this.doc.write(html);
    this.doc.close();
  }
  else if (is.ie) {
    this.event.innerHTML = html;
  }
}
//----------------------
//迷你滚动条 控件客户端脚本
//----------------------
function TMiniScroll(window, content) {
  this.window = window;
  this.content = content;
  this.inc = 8;
  this.speed = 20;
  this.contentHeight = (is.ns)? this.content.doc.height : this.content.elm.scrollHeight;
  this.contentWidth = (is.ns)? this.content.doc.width : this.content.elm.scrollWidth;
  this.up = TMS_Up;
  this.down = TMS_Down;
  this.left = TMS_Left;
  this.right = TMS_Right;
  this.stop = TMS_Stop;
  this.activate = TMS_Activate;
  this.activate(this.contentWidth, this.contentHeight);
  return this;
}
function TMS_Activate() {
  this.offsetHeight = this.contentHeight - this.window.h;
  this.offsetWidth = this.contentWidth - this.window.w;
  this.enableVScroll = (this.offsetHeight>0);
  this.enableHScroll = (this.offsetWidth>0);
}
function TMS_Up() {
  if (this.enableVScroll)
    this.content.slideTo(null, 0, this.inc, this.speed);
}
function TMS_Down() {
  if (this.enableVScroll)
    this.content.slideTo(null, -this.offsetHeight, this.inc, this.speed);
}
function TMS_Left() {
  if (this.enableHScroll)
    this.content.slideTo(0, null, this.inc, this.speed);
}
function TMS_Right() {
  if (this.enableHScroll)
    this.content.slideTo(-this.offsetWidth, null, this.inc, this.speed);
}
function TMS_Stop() {
  this.content.slideActive = false;
}

// CSS Function
function css(id,left,top,width,height,color,vis,z,other) {
  if (id=="START")
    return '<STYLE TYPE="text/css">\n';
  else if (id=="END")
    return '</STYLE>';
  var str = (left!=null && top!=null)? '#'+id+' {position:absolute; left:'+left+'px; top:'+top+'px;' : '#'+id+' {position:relative;'
  if (arguments.length>=4 && width!=null) str += ' width:'+width+'px;'
  if (arguments.length>=5 && height!=null) {
    str += ' height:'+height+'px;'
    if (arguments.length<9 || other.indexOf('clip')==-1) str += ' clip:rect(0px '+width+'px '+height+'px 0px);'
  }
  if (arguments.length>=6 && color!=null) str += (is.ns)? ' layer-background-color:'+color+';' : ' background-color:'+color+';'
  if (arguments.length>=7 && vis!=null) str += ' visibility:'+vis+';'
  if (arguments.length>=8 && z!=null) str += ' z-index:'+z+';'
  if (arguments.length==9 && other!=null) str += ' '+other
  str += '}\n'
  return str;
}
function WriteCSS(str,showAlert) {
  str = css('START') + str + css('END');
  document.write(str);
  if (showAlert)
    alert(str);
}
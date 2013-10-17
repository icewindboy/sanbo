<#macro selectArea>
<div id="panelArea" style="margin:10px 0 10px 0;padding:0; ">
	<div id="select_area_div"></div>
</div>
<script language="javascript">

var folderflag = true;
var DC = DesTreeConfig;
var inputId = "";
var inputName = "";
var dtree_d_g = new DesTree("dtree_d_g",'<@s.text name="el.admin.regin.root"/>',1);;
function showSelectedArea(){
	var nodeId=dtree_d_g.selectedNodes[0].id;
	dtree_d_g.iclick(DesTreeConfig.nname + this.id + nodeId);
}
function showSchoolList(){

	var nodeId=dtree_d_g.selectedNodes[0].id;

	shoolView(nodeId);
}
function showOrgList(){

	var nodeId=dtree_d_g.selectedNodes[0].id;

	orgView(nodeId);
}
function orgView(id){
  	var url="${base}/auth/popuptree/showOrgList.action";

  	var params="id="+id;
  	loadModule(url,params,"orglist",null,null);
  }
function shoolView(id){
  	var url="${base}/auth/popuptree/showShoolList.action";

  	var params="id="+id;
  	loadModule(url,params,"schoollist",null,null);
  }
function loadChildrenOfArea() {
	ajax_load_children(dtree_d_g.selectedNodes[0].id);

}

function ajax_load_children(nodeId){
	var pars='id='+nodeId;
	var url = '${base}/auth/popuptree/getChildrenOfArea.action';

	ajaxRequest(url,pars,onComplete,null);
}

function onComplete(res){

	var jsondata = eval("("+res.responseText+")");
	var json=jsondata["actiondata"];
	for(var m=0;m<json.length;m++){
		dtree_d_g.add(json[m]["id"], json[m]["parentId"],DC.branch,json[m]["name"]);
	}
}





returnValue = function(){

	if(typeof(dtree_d_g.selectedNodes[0].text) == 'undefined'){

		alert('<@s.text name="el.admin.regin.selectarea"/>');
	}
	else{

		eval('panel_area').closePanel();

		setCustomerValueToInput(dtree_d_g.selectedNodes[0].id,dtree_d_g.selectedNodes[0].text);
		
		if(typeof(old_area_id)!='undefined' && old_area_id !='' && old_area_id != $('user.userInfo.region.id').value){
			$('user.userInfo.school.id').value = '';
			$('user.userInfo.school.name').value = '';
		}
	}

}
function getSchoolName(obj){
	var trobj = obj.parentNode.parentNode;
	inputId = obj.value;
	if(_isIE){
		inputName = trobj.childNodes[1].innerHTML;
	}
	else
	{

		inputName = extractNodes(trobj)[1].textContent;
	}
	//alert(inputName);
}
function setInputValue(){

	if(inputName == '')
	{
		alert('<@s.text name="el.admin.regin.selectgroup"/>');
	}
	else{
		eval('panel_area').closePanel();
		setCustomerValueToInput(inputId,inputName);
	}

}
function extractNodes(pNode){
    if(pNode.nodeType == 3)
        return null;
    var node,nodes = new Array();
    for(var i=0;node= pNode.childNodes[i];i++){
        if(node.nodeType == 1)
            nodes.push(node);
    }
    return nodes;
}
function closePanel(){
	eval('panel_area').closePanel();
}

</script>
</#macro>

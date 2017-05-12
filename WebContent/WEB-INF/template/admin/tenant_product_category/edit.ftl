<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.article.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="${base}/static/css/cloud-admin.css" >	
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript">
$().ready(function() {
});
</script>
<script src="http://www.denghuafeng.com/jquery-1.4.4.min.js"></script>
<script type="text/javascript">
function checkNextObj(obj){
 var chs=$(obj).next().next().children().children('input:checkbox');
 if(chs!=null){
  $(chs).attr('checked',obj.checked);
  var len=chs.length;
  for(var i=0;i<len;i++){
      checkNextObj(chs[i]);
  }
 }
}
function checkParentObj(obj){
 var chs=$(obj).parent().parent().parent().children('input:checkbox');
  if(chs!=null){
  var len=chs.length;
  for(var i=0;i<len;i++){
   if(chs[i]!=obj){
    chs[i].checked = false;
    checkParentObj(chs[i]);   
    }
  }
 }
}
$(function(){
 $('input:checkbox').click(
  function(){
   checkNextObj(this);
   checkParentObj(this);
  });    
}
);
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 企业行业分类
	</div>
	<form id="inputForm" action="save.jhtml" method="post"  enctype="multipart/form-data">
		<table class="input">
		<tr>
		  <th><h4>选择企业行业分类:</h4></th>
		  <td></td>
		</tr>
		  [#list productCategoryList as productCategory]
		   [#if productCategory.grade==0]
		  <tr>
		   <td>
		    &nbsp;
		   </td>
		   <td>
		     <ul id="tree" class="treeview filetree">
                 <li>
                     <input name="ids"  type="checkbox" value="" />
                     <span><font szie="5px"><strong>${productCategory.name}</strong></font></span>
                     <ul>
					    	[#list productCategory.children  as children]
					    	   [#list productCategoryList as productCategorys]
					    	     [#if productCategorys.id==children.id]
					        	 <li>
                                   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input name="ids" [#if ("1" == (productCategorys.isChoice)!)] checked="checked"[/#if]  type="checkbox" value="${children.id}" />
                                    <span>${children.name}</span> </li>
                                 [/#if]
                               [/#list]
						    [/#list]
					</ul>
				</li>
		     </ul>
		   </td>
		  </tr>
		  [/#if]
		  [/#list]
		  <tr>
		    <td>
		    </td>
		  </tr>
		</table>
		<table class="input">
			<tr>
				<th>
				    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="hidden" name="tenantId" value="${tenant.id}" />
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.specification.list")}</title>


<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<!-- ztree -->
<link rel="stylesheet" href="${base}/resources/admin/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${base}/resources/admin/ztree/js/jquery.ztree.core-3.5.min.js"></script>
<script type="text/javascript" src="${base}/resources/admin/ztree/js/jquery.ztree.exedit-3.5.min.js"></script>
<script type="text/javascript" src="${base}/resources/admin/ztree/js/jquery.ztree.excheck-3.5.min.js"></script>
<style type="text/css">
  ul.ztree{
	   height:500px;
    }
</style>
<script type="text/javascript">
var $listForm=null;
$().ready(function() {

	
	[@flash_message /]
	$listForm = $("#listForm");
	$.get("tree.jhtml",function(data){
		$.fn.zTree.init($("#ztree"), ztreeSetting, data);
		var treeObj = $.fn.zTree.getZTreeObj("ztree");
		var nodes = treeObj.getNodes();//获取所有的父节点
		
		var selectId=$("#id").val();//79;//"${productCategoryIdSelect}"
		
		if(selectId!=""){
			//设置选中
			treeObj.checkNode(treeObj.getNodeByParam("id",selectId,null), true, true, false);
			//默认展开选中的节点
			treeObj.selectNode(treeObj.getNodeByParam("id",selectId,null),true);
		}
		
	});
	
	
	
});

var ztreeSetting = {
		data: {
			simpleData: {
				enable: true
			}
		},
		check: {
			enable: false,
			
			
		}
		,  
        callback: {  
            
        	onClick: onNodeClick
        }
	};
	function onNodeClick(event, treeId, node, clickFlag) {
		$("#id").val(node.id);
		$("#pcid").val(node.id);
		$("#productCategoryId").val(node.id);
	 
		addCookie("specificationPreProductCategoryId", node.id, {expires: 24 * 60 * 60});
		
		$listForm.submit();
		//$.get("${base}/admin/specification/list.jhtml",{id:node.id},function(data){
		//});
	}
 
	
</script>
</head>
<body>
	<div class="path">
	  
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.specification.list")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list.jhtml" method="get">
		<div class="bar">
		    <input type="hidden" id="productCategoryId" name="productCategoryId"  value="${productCategoryId}" maxlength="200" />
		
		    <input type="hidden" id="id" name="id"  value="${id}" maxlength="200" />
			<a href="add.jhtml" class="iconButton">
				<span class="addIcon">&nbsp;</span>${message("admin.common.add")}
			</a>
			<div class="buttonWrap">
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div class="menuWrap">
					<a href="javascript:;" id="pageSizeSelect" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<div class="popupMenu">
						<ul id="pageSizeOption">
							<li>
								<a href="javascript:;"[#if page.pageSize == 10] class="current"[/#if] val="10">10</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 20] class="current"[/#if] val="20">20</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 50] class="current"[/#if] val="50">50</a>
							</li>
							<li>
								<a href="javascript:;"[#if page.pageSize == 100] class="current"[/#if] val="100">100</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "name"] class="current"[/#if] val="name">${message("Specification.name")}</a>
						</li>
					</ul>
				</div>
			</div>
		</div>
		<table id="listTable" class="list" style="border:1px;">
		<tr>
				<th>
					<a href="javascript:;" class="sort" name="name">&nbsp;&nbsp;&nbsp;&nbsp;选择商品分类&nbsp;&nbsp;&nbsp;&nbsp;</a>
				</th>
				<th>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="name">分类规格列表</a>
				</th>
			</tr>
		<tr>
		   <td valign="top">
		      <table>
		      <tr>
		       <td>
		        &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;
		       </td>
		      </tr>
		         <tr>
		           <td>
		             <div class="zTreeDemoBackground left">
			               <ul id="ztree" class="ztree"></ul>
		              </div>  
		           </td>
		         </tr>
		      </table>
		   </td>
		   <td valign="top"><hr style="width:1px;height:500px; "></hr></td>
	 	  <td valign="top" style="width:100%">
		    <table id="listTable" class="list">
			<tr>
				<td class="check">
					<input type="checkbox" id="selectAll" />
				</td>
				<td>
					<a href="javascript:;" class="sort" name="name">${message("Specification.name")}</a>
				</td>
				<td>
					<a href="javascript:;" class="sort" name="name">${message("ParameterGroup.productCategory")}</a>
				</td>
				<td>
					<a href="javascript:;" class="sort" name="type">${message("Specification.type")}</a>
				</td>
				<td>
					<span>${message("Specification.specificationValues")}</span>
				</td>
				<td>
					<span>${message("admin.common.order")}</span>
				</td>
				<td>
					<span>${message("admin.common.handle")}</span>
				</td>
			</tr>
			[#list page.content as specification]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${specification.id}" />
					</td>
					<td>
						${specification.name}
						[#if specification.memo??]
							<span class="gray">[${specification.memo}]</span>
						[/#if]
					</td>
					
					<td>
					 		[#if specification.productCategory??]
									${specification.productCategory.name}
						   [/#if]
					</td>
					
					<td>
						${message("Specification.Type." + specification.type)}
					</td>
					<td>
						[#list specification.specificationValues as specificationValue]
							${specificationValue.name}
							[#if specificationValue_index > 5]
								...[#break /]
							[/#if]
						[/#list]
					</td>
					<td>
						${specification.order}
					</td>
					<td>
						<a href="edit.jhtml?id=${specification.id}">[${message("admin.common.edit")}]</a>
					</td>
				</tr>
			[/#list]
			</table>
		 
			[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
					[#include "/admin/include/pagination.ftl"]
			[/@pagination]
		  </td>
		  </tr>
		 </table>
	</form>
</body>
</html>
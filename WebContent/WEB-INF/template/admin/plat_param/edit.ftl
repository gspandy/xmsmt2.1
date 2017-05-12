<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.product.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
	.specificationSelect {
		height: 100px;
		padding: 5px;
		overflow-y: scroll;
		border: 1px solid #cccccc;
	}
	
	.specificationSelect li {
		float: left;
		min-width: 150px;
		_width: 200px;
	}
</style>
<script type="text/javascript">
$().ready(function() {
	[@flash_message /]
	

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 平台设置
	</div>
		<ul id="tab" class="tab">
		   <li>
				<input type="button" value="收/退货设置" />
			</li>
			<li>
				<input type="button" value="结算设置" />
			</li>
		</ul>
		<form id="inputForm" action="saveDate.jhtml" method="post" enctype="multipart/form-data">
		<table class="input tabContent">
		<tr>
		   <td>
		     <table class="input">
			  [#list platParams as platParam]
		     [#if platParam.type==2]
			<tr>
				<th>
					${platParam.paramCnName}: 
				</th>
				<td>
				    <input type="hidden" name="ids" class="text" value="${platParam.id}"/>
				    [#if platParam.paramValue==null]
				      <input type="text" name="paramValues" class="text" value="${platParam.defaultValue}"  />
				    [#else]
				       <input type="text" name="paramValues" class="text" value="${platParam.paramValue}"  />
					[/#if]
				</td>
			</tr>
			 [/#if]
			[/#list]
			  <tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
				</td>
			</tr>
		     </table>
		   </td>
		  </tr>
		</table>
		</form>
		<form id="inputForm" action="save.jhtml" method="post" enctype="multipart/form-data">
		<table class="input tabContent">
		  <tr>
		   <td>
		   <table class="input">
		   [#list platParam as platParam]
		     [#if platParam.type==1]
			<tr>
				<th>
					${platParam.paramCnName}: 
				</th>
				<td>
				    <input type="hidden" name="id" class="text" value="${platParam.id}"/>
					 [#if platParam.paramValue==null]
				      <input type="text" name="paramValue" class="text" value="${platParam.defaultValue}"  />
				    [#else]
				       <input type="text" name="paramValue" class="text" value="${platParam.paramValue}"  />
					[/#if]
				</td>
			</tr>
			 [/#if]
			[/#list]
		     <tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
				</td>
			</tr>
		</table>
		   </td>
		  </tr>
		</table>
		</form>
		
</body>
</html>

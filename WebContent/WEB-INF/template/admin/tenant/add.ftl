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
	var $inputForm = $("#inputForm");
	var $div = $("#div");
	var timeout;
	
	// 表单验证
	$inputForm.validate({
		rules: {
 			articleCategoryId: "required",
			"name": {
 				required: true
 			},
 			"tenantType": {
 				required: true
 			},
 			"shortName": {
 				required: true
 			},
 			"address": {
 				required: true
 			},
 			"linkman": {
				required: true
 			},
 			"telephone": {
 				required: true
 			},
 			"username": {
 				required: true
 			},
 			"password": {
 				required: true
 			},
 			"legalRepr": {
 				required: true
 			},
 			"licenseCode": {
 				required: true
 			}
 		}
 	});

});
function disPlay(){
	var i=document.from2.skipType.selectedIndex;
	var type=document.from2.skipType.options[i].value; 
	if(type==0){
		document.from2.productIdName.disabled=false;
		document.from2.url.disabled=true;
		document.from2.url.value="";
	}else if(type==1){
		document.from2.url.disabled=false
		document.from2.productIdName.disabled=true;
		document.from2.productIdName.value="";
	}
}
function disPlayT(){
	var i=document.from2.skipTypeT.selectedIndex;
	var type=document.from2.skipTypeT.options[i].value; 
	if(type==0){
		document.from2.productIdNameT.disabled=false;
		document.from2.urlT.disabled=true;
		document.from2.urlT.value="";
	}else if(type==1){
		document.from2.urlT.disabled=false
		document.from2.productIdNameT.disabled=true;
		document.from2.productIdNameT.value="";
	}
}
function disPlayTH(){
	var i=document.from2.skipTypeTH.selectedIndex;
	var type=document.from2.skipTypeTH.options[i].value; 
	if(type==0){
		document.from2.productIdNameTH.disabled=false;
		document.from2.urlTH.disabled=true;
		document.from2.urlTH.value="";
	}else if(type==1){
		document.from2.urlTH.disabled=false
		document.from2.productIdNameTH.disabled=true;
		document.from2.productIdNameTH.value="";
	}
}
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 企业设置
	</div>
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="企业基本信息" />
			</li>
		</ul>
		<form id="inputForm" action="save.jhtml" method="post" enctype="multipart/form-data">
		<table class="input tabContent">
		  <tr>
		   <td>
		   <table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>企业名称: 
				</th>
				<td>
					<input type="text" name="name" class="text" value=""  />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>品牌: 
				</th>
				<td>
					<input type="text" name="shortName" class="text" value=""  />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>企业法人: 
				</th>
				<td>
					<input type="text" name="legalRepr" class="text" value=""  />
				</td>
			</tr>
			<tr>
				<th>
				 <span class="requiredField">*</span>工商营业执照号: 
				</th>
				<td>
					<input type="text" name="licenseCode" class="text" value=""  />
				</td>
			</tr>
  			<tr>
  				<th>
  						logo:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file" id="file" name="file" value=""/><font color="red">上传规格：150px*150px   文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
  			<tr>
  				<th>
  					邀请函图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file" id="imageFile" name="imageFile" value=""/><font color="red">上传规格：640px*1136px  文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
  			<tr>
  				<th>
  				 邀请朋友成为店长:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file" id="openShopFile" name="openShopFile" value=""/><font color="red">上传规格：1080px*1037px  文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
  			<tr>
  				<th>
  				邀请函列表图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file" id="shareImageFile" name="shareImageFile" value=""/><font color="red">上传规格：1080px*1037px  文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>地址: 
				</th> 
				<td>
			   	<input type="text" name="address" class="text"  value="" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>联系人: 
				</th>
				<td>
					<input type="text" name="linkman" class="text" value=""/>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>联系电话: 
				</th>
				<td>
					<input type="text" name="telephone" class="text" value=""/>
				</td>
			</tr>
		 	<tr>
				<th>
					<span class="requiredField">*</span>状态:
				</th>
				<td>
						<select name="status">
							<option value="">--请选择--</option>
							<option value="wait"  >申请中</option>
							<option value="success"  >已开通</option>
							<option value="fail"  >暂停</option>
							<option value="none"  >未关闭</option>
						</select>
				</td>
			</tr>
			<tr>
			    <th>
					发现企业内购券:
				</th>
				<td>
				       <input type="checkbox" name="firstRentFreePeriod" value="1"/>发现 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color="red">设置为“发现”后则在买家app“发现”中可领取该企业的内购券</font>
				</td>
			</tr>
			<tr>
			  <th>
			      <span class="requiredField">*</span>超级管理员设置
			  </th>
			  <td>
			    <table class="input">
			   <tr>
				<th>
					<span class="requiredField">*</span>用户名: 
				</th>
				<td>
					<input type="text"id="username" name="username" class="text" value=""  /> &nbsp;&nbsp;&nbsp;<font color="red">命名规则：品牌英文字母+序号，如qiaodan01”</font>
				</td>
			  </tr>
			   <tr>
				<th>
					<span class="requiredField">*</span>密码: 
				</th>
				<td>
					<input type="password" id="password" name="password" class="text" value=""  />
				</td>
			  </tr>
		     </table>
			  </td>
			</tr>
		 	<tr>
				<th>
					${message("Article.tags")}:
				</th>
				<td>
					[#list tags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}" checked="checked" />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
				<th>
					企业简介:
				</th>
				<td>
					<textarea id="editor" name="introduction" class="editor"></textarea>
				</td>
			</tr>
		     <tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list.jhtml'" />
				</td>
			</tr>
		</table>
		   </td>
		  </tr>
		</table>
		</form>
</body>
</html>

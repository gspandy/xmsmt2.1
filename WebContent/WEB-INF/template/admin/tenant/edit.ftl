<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.article.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
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
 			"address": {
 				required: true
 			},
 			"linkman": {
				required: true
 			},
 			"telephone": {
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
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 企业设置
	</div>
	<form id="inputForm" action="save.jhtml" method="post"  enctype="multipart/form-data">
		<input type="hidden" name="id" value="${tenant.id}" />
		<input type="hidden" name="template" value="${tenant.template}" />
		<input type="hidden" name="score" value="${tenant.score}" />
		<input type="hidden" name="totalScore" value="${tenant.totalScore}" />
		<input type="hidden" name="price" value="${tenant.price}" />
		<input type="hidden" name="typeId" value="2" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>企业名称: 
				</th>
				<td>
					<input type="text" name="name" class="text" value="${(tenant.name)!}"  />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>品牌: 
				</th>
				<td>
					<input type="text" name="shortName" class="text" value="${(tenant.shortName)!}"  />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>企业法人: 
				</th>
				<td>
					<input type="text" name="legalRepr" class="text" value="${tenant.legalRepr}"  />
				</td>
			</tr>
			<tr>
				<th>
				   <span class="requiredField">*</span>工商营业执照号: 
				</th>
				<td>
					<input type="text" name="licenseCode" class="text" value="${tenant.licenseCode}"  />
				</td>
			</tr>
  			<tr>
  				<th>
  					图片上传：
  				</th>
  				<td>
  				   <table border="1">
  				     <tr>
  				       <td>logo</td>
  				       <td>邀请函图片</td>
  				       <td>邀请朋友成为店长</td>
  				       <td>邀请函列表图片</td>
  				     </tr>
  				     <tr>
  				       <td>
  				          <div>
  					        [#if tenant.logo??]
  					        <div style="width:100px;">
  						    <img  src="${tenant.logo}" width="160px"/>
  						    </div>
  					        [/#if]
  					        </div>
  				       </td>
  				       <td>
  				          <div>
  					        [#if tenant.invationImage??]
  					         <div style="width:100px";>
  						       <img  src="${tenant.invationImage}" width="160px"/>
  						     </div>
  					        [/#if]
  					      </div>
  				       </td>
  				       <td>
  				          <div>
  					         [#if tenant.openShopImage??]
  				            	<div style="width:100px;">
  					        <div>
  						        <img  src="${tenant.openShopImage}" width="160px"/>
  					        </div>
  					     </div>
  					[/#if]
  					</div>
  				       </td>
  				       <td>
  				          <div>
  					        [#if tenant.shareImage??]
  					         <div style="width:100px";>
  						       <img  src="${tenant.shareImage}" width="160px"/>
  						     </div>
  					        [/#if]
  					      </div>
  				       </td>
  				     </tr>
  				     <tr>
  				       <td>
  				         <span class="fieldSet">
  					      
  					         <div>
  					         <input type="hidden" name="logoId" value="${tenant.logoId}" />
  					         <input type="hidden" name="logo" value="${tenant.logo}" />
  				            <input type="file" id="file" name="file" value="${tenant.logo}"/><p><font color="red">上传规格：150px*150px 文件格式:*.png</font></p>
  					       </div>
  					     </span>
                       </td>
  				       <td>
  				         <span class="fieldSet">
  					
  					<div>
  					<input type="hidden" name="invationImageId" value="${tenant.invationImageId}" />
  				     <input type="hidden" name="invationImage" value="${tenant.invationImage}" />
  				    <input type="file" id="imageFile" name="imageFile" value="${tenant.invationImage}"/><p><font color="red">上传规格：640px*1136px  文件格式:*.png</font></p>
  				    </div>
  					</span>
  				       </td>
  				       <td>
  				     <span class="fieldSet">
  					<div>
  					<input type="hidden" name="openShopImageId" value="${tenant.openShopImageId}" />
  				     <input type="hidden" name="openShopImage" value="${tenant.openShopImage}" />
  				    <input type="file" id="openShopFile" name="openShopFile" value="${tenant.openShopImage}"/><p><font color="red">上传规格：1080px*1037px  文件格式:*.png</font></p>
  				    </div>
  					</span></td>
  					  <td>
  				     <span class="fieldSet">
  					<div>
  				     <input type="hidden" name="shareImage" value="${tenant.shareImage}" />
  				    <input type="file" id="shareImageFile" name="shareImageFile" value="${tenant.shareImage}"/><p><font color="red">上传规格：1080px*1037px  文件格式:*.png</font></p>
  				    </div>
  					</span></td>
  				     </tr>
  				    </table>
  				</td>
  			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>地址: 
				</th> 
				<td>
			   	<input type="text" name="address" class="text"  value="${(tenant.address)!}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>联系人: 
				</th>
				<td>
					<input type="text" name="linkman" class="text" value="${(tenant.linkman)!}"/>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>联系电话: 
				</th>
				<td>
					<input type="text" name="telephone" class="text" value="${(tenant.telephone)!}"/>
				</td>
			</tr>
		 	<tr>
				<th>
					<span class="requiredField">*</span>状态:
				</th>
				<td>
						<select name="status">
							<option value="">--请选择--</option>
							<option value="wait" [#if ("wait" == (tenant.status)!)] selected[/#if] >申请中</option>
							<option value="success" [#if ("success" == (tenant.status)!)] selected[/#if] >已开通</option>
							<option value="fail" [#if ("fail" == (tenant.status)!)] selected[/#if] >暂停</option>
							<option value="none" [#if ("none" == (tenant.status)!)] selected[/#if] >未关闭</option>
						</select>
				</td>
			</tr>
			<tr>
			    <th>
					发现企业内购券:
				</th>
				<td>
				       <input type="checkbox" name="firstRentFreePeriod"  [#if ("1" == (tenant.firstRentFreePeriod)!)]checked[/#if] value="1"/>发现&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color="red">设置为“发现”后则在买家app“发现”中可领取该企业的内购券</font>
				</td>
			</tr>
		 	<tr>
				<th>
					${message("Article.tags")}:
				</th>
				<td>
					[#list tags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}"[#if tenant.tags?seq_contains(tag)] checked="checked"[/#if] />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
				<th>
					企业简介:
				</th>
				<td>
					<textarea id="editor" name="introduction" class="editor">${tenant.introduction?html}</textarea>
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
	</form>
</body>
</html>
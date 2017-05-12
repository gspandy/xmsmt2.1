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
	var $areaId = $("#areaId");
	var $communityId = $("#communityId");
	var timeout;
	
	[@flash_message /]
	function getCommunity() {
		    if ($areaId.val()>0) {
				$.ajax({
					url: "get_community.jhtml",
					type: "GET",
					data: {areaId:$areaId.val()},
					dataType: "json",
					cache: false,
					success: function(map) {
					   var opt="<option value=0>--请选择--</option>";
					   for (var key in map) {  
					     opt = opt +"<option value="+key+">"+map[key]+"</option>";
             }  
             $communityId.html(opt);
					}
				});
				} else {
             $communityId.html("<option value=0>--无--</option>");
				}

	}

  function areaSelect(){
		clearTimeout(timeout);
 		timeout = setTimeout(function() {
			getCommunity();	
		}, 500);
  }
	
		// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area.jhtml",
		fn:areaSelect
	});
	
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
			"areaId": {
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
			}
		}
	});
	getCommunity();

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 商家认证审核
	</div>
	<form id="inputForm" action="checkSave.jhtml" method="post"  enctype="multipart/form-data">
		<input type="hidden" name="id" value="${tenant.id}" />
		<table class="input">
				<tr>
					<th>
						<span class="requiredField">*</span>商家名称: 
					</th>
					<td>
						<input type="text" name="name" class="text" value="${(tenant.name)!}" readonly="readonly" />
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>商家简称: 
					</th>
					<td>
						<input type="text" name="shortName" class="text" value="${(tenant.shortName)!}"   readonly="readonly" />
					</td>
				</tr>
  				<tr>
  					<th>
  						店铺分类:
  					</th>
  					<td>
  						<select id="tenantCategoryId" name="tenantCategoryId" readonly="readonly">
  							[#list tenantCategoryTree as tenantCategory]
  								<option value="${tenantCategory.id}"[#if tenantCategory == tenant.tenantCategory] selected="selected"[/#if]>
  									[#if tenantCategory.grade != 0]
  										[#list 1..tenantCategory.grade as i]
  											&nbsp;&nbsp;
  										[/#list]
  									[/#if]
  									${tenantCategory.name}
  								</option>
  							[/#list]
  						</select>
  					</td>
  				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>性质: 
					</th>
					<td>
						<select name="tenantType">
							<option value="">--请选择--</option>
							<option value="enterprise" [#if ("enterprise" == (tenant.tenantType)!)] selected[/#if] >企业单位</option>
							<option value="individual" [#if ("individual" == (tenant.tenantType)!)] selected[/#if] >个体经营</option>
							<option value="organization" [#if ("organization" == (tenant.tenantType)!)] selected[/#if] >事业单位或社会团体</option>
							<option value="personal" [#if ("personal" == (tenant.tenantType)!)] selected[/#if] >个人</option>
						</select>
					</td>
				</tr>
  			<tr>
  				<th>
  					logo:
  				</th>
  				<td>
  					<span class="fieldSet">
  						<a href="${tenant.logo}" target="_blank"><img src="${tenant.logo}"></a>
  					</span>
  				</td>
  			</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>所属地区:
					</th>
					<td>
						<span class="fieldSet">
							<input type="hidden" id="areaId" name="areaId" value="${(tenant.area.id)!}" treePath="${(tenant.area.treePath)!}"/>
						</span>
					</td>
				</tr>
				<tr>
					<th>
						所属社区:
					</th>
					<td>
						<span class="fieldSet">
					   	<select id="communityId" name="communityId">
  										<option value="">--请选择--</option>
		  				</select>
						</span>
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>地址: 
					</th> 
					<td>
				   	<input type="text" name="address" class="text"  value="${(tenant.address)!}" readonly="readonly"/>
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>联系人: 
					</th>
					<td>
						<input type="text" name="linkman" class="text" value="${(tenant.linkman)!}" readonly="readonly"/>
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>联系电话: 
					</th>
					<td>
						<input type="text" name="telephone" class="text" value="${(tenant.telephone)!}" readonly="readonly"/>
					</td>
				</tr>
				<tr>
					<th>
						<span class="requiredField">*</span>身份证号: 
					</th>
					<td>
						<input type="text" name="no" class="text" value="${(idcard.no)!}" readonly="readonly"/>
					</td>
				</tr>
		 		<tr>
					<th>
						<span class="requiredField">*</span>状态:
					</th>
					<td>
						<select name="status">
							<option value="">--请选择--</option>
							<option value="wait" [#if ("wait" == (tenant.status)!)] selected[/#if] >等待审核</option>
							<option value="success" [#if ("success" == (tenant.status)!)] selected[/#if] >认证通过</option>
							<option value="fail" [#if ("fail" == (tenant.status)!)] selected[/#if] >认证拒绝</option>
							<option value="none" [#if ("none" == (tenant.status)!)] selected[/#if] >未认证</option>
						</select>
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
					商家联盟:
				</th>
				<td>
					[#list uniontags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}"[#if tenant.unionTags?seq_contains(tag)] checked="checked"[/#if] />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>
				[#if tenant.licenseCode??]
				<tr>
					<th>
						经营许可证: 
					</th>
					<td>
						<input type="text" name="licenseCode" class="text" value="${(tenant.licenseCode)!}" readonly="readonly"/>
					</td>
				</tr>
				[/#if]
				[#if tenant.licensePhoto??]
				<tr>
  					<th>
  						营业执照:
  					</th>
  					<td>
  						<span class="fieldSet">
  							<a href="${tenant.licensePhoto.replace("thumbnail","source")}" target="_blank"><img src="${tenant.licensePhoto}"></a>
  						</span>
  					</td>
  				</tr>
				[/#if]
				<tr>
					<th>
						描述:
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
						<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='checkList.jhtml'" />
					</td>
				</tr>
		</table>
	</form>
</body>
</html>
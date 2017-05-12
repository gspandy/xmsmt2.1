<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.deliveryCenter.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=1.2"></script>
<script type="text/javascript">
$().ready(function() {
	var $inputForm = $("#inputForm");
	[@flash_message /]
	// 表单验证
	$inputForm.validate({
		rules: {
			serviceName: "required",
			realName: "required",
			serviceTel: "required",
			username: "required",
			password: "required"
		}
	});
	
	
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 客服管理
	</div>
	<div id="map" style="position:absolute;margin-top:110px;margin-left:380px;display: none;">
    <div style="width:520px;height:340px;border:1px solid gray" id="mapform"></div>
    <p><input type="button" onclick="closeMap()" value="关闭" /></p>
  </div>
	<form id="inputForm" action="save.jhtml" method="post" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${customService.id}" />
		<input type="hidden" name="adminId" value="${customService.admin.id}" />
		<input type="hidden" name="token" value="${customService.token}" />
		<table class="input">
			
			<tr>
  				<th>
  						头像:
  				</th>
  				<td>
  				   <table>
  				    <tr>
  				     <td> 
  				          <div>
  					        [#if customService.serviceImg??]
  					        <div style="width:100px;">
  						    <img  src="${customService.serviceImg}" width="160px"/>
  						    </div>
  					        [/#if]
  					        </div>
  				     </td>
  				    </tr>
  				    <tr>
  				      <td>
  				         <span class="fieldSet">
  				         <input type="hidden" id="serviceImg" name="serviceImg" value="${customService.serviceImg}"/>
  				         <input type="file" id="file" name="file" value="${customService.serviceImg}"/>
  					     </span>
  				      </td>
  				    </tr>
  				   </table>
  					
  				</td>
  			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>客服名称:
				</th>
				<td>
					<input type="text" name="serviceName" class="text" maxlength="200" value="${customService.serviceName}"/> 
				</td>
			</tr>
		    <tr>
				<th>
					<span class="requiredField">*</span>真实姓名:
				</th>
				<td>
					<input type="text" name="realName" class="text" maxlength="200" value="${customService.realName}"/>
				</td>
			</tr>
		    <tr>
				<th>
					<span class="requiredField"></span>电话:
				</th>
				<td>
					<input type="text" name="serviceTel" class="text" maxlength="200" value="${customService.serviceTel}"/>
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='serviceList.jhtml'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>

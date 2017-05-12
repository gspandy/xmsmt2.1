<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.deliveryCenter.add")} - Powered By rsico</title>
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
		<table class="input">
		     <tr>
		        <th><font size="4px">基本信息:</font></th>
		        <td></td>
		     </tr>
			 <tr>
  				<th>
  						头像:
  				</th>
  				<td>
  					<span class="fieldSet">
  					<input type="hidden" name="serviceImg" value="${base}/static/img/kefu.jpg" />
  				    <input type="file" id="file" name="file" value=""/>
  					</span>
  				</td>
  			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>客服名称:
				</th>
				<td>
					<input type="text" id="serviceName" name="serviceName" class="text" maxlength="200" />  
				</td>
			</tr>
		    <tr>
				<th>
					<span class="requiredField">*</span>真实姓名:
				</th>
				<td>
					<input type="text" id="realName" name="realName" class="text" maxlength="200" />
				</td>
			</tr>
		    <tr>
				<th>
					<span class="requiredField"></span>电话:
				</th>
				<td>
					<input type="text" id="serviceTel" name="serviceTel" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
		        <th><font size="4px">用户登录信息:</font></th>
		        <td></td>
		     </tr>
			<tr>
				<th>
					<span class="requiredField">*</span>用户名: 
				</th>
				<td>
					<input type="text" id="username" name="username" class="text" value=""  />&nbsp;&nbsp;&nbsp;<font color="red">命名规则：品牌名称+数字编号   例如：速卖通的一号客服名称  sumaitong01</font>
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

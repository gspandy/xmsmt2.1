<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.member.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {
	[@flash_message /]
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 实名认证
	</div>
	<form id="inputForm" action="certification.jhtml" method="post">
		<input type="hidden" name="id" value="${member.id}" />
		<table class="input tabContent">
			<tr>
				<th>
					${message("Member.username")}:
				</th>
				<td>
					${member.username}
				</td>
			</tr>
			<tr>
				<th>
					${message("Member.birth")}:
				</th>
				<td>
					${member.birth}
				</td>
			</tr>
			<tr>
				<th>
					${message("Member.address")}:
				</th>
				<td>
					[#if member.idcard??]${member.idcard.address}[#else]${member.address}[/#if]
				</td>
			</tr>
			<tr>
				<th>
					身份证号:
				</th>
				<td>
					[#if member.idcard??]${member.idcard.no}[/#if]
				</td>
			</tr>
			<tr>
				<th>
					有效期:
				</th>
				<td>
					[#if member.idcard??]${member.idcard.beginDate}——${member.idcard.endDate}[/#if]
				</td>
			</tr>
			<tr>
				<th>
					身份证正面照:
				</th>
				<td>
					[#if member.idcard??]<a href="${member.idcard.pathFront}" target="_blank"><img src="${member.idcard.pathFront}"></a>[/#if]
				</td>
			</tr>
			<tr>
				<th>
					身份证反面照:
				</th>
				<td>
					[#if member.idcard??]<a href="${member.idcard.pathBack}" target="_blank"><img src="${member.idcard.pathBack}"></a>[/#if]
				</td>
			</tr>
			<tr>
				<th>
					状态：
				</th>
				<td>
					<select name="status">
						<option value="">--请选择--</option>
						<option value="wait" [#if ("wait" == (member.idcard.authStatus)!)] selected[/#if] >等待审核</option>
						<option value="success" [#if ("success" == (member.idcard.authStatus)!)] selected[/#if] >认证通过</option>
						<option value="fail" [#if ("fail" == (member.idcard.authStatus)!)] selected[/#if] >认证拒绝</option>
						<option value="none" [#if ("none" == (member.idcard.authStatus)!)] selected[/#if] >未认证</option>
					</select>
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
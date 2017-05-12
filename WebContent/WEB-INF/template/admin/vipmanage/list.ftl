<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>店长等级设置</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $delete = $("#listTable a.delete");
	
	[@flash_message /]

	// 删除
	$delete.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				$.ajax({
					url: "delete.jhtml",
					type: "POST",
					data: {id: $this.attr("val")},
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							$this.closest("tr").remove();
						}
					}
				});
			}
		});
		return false;
	});

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 店长等级设置
	</div>
	<div class="bar">
		<a href="add.jhtml" class="iconButton">
			<span class="addIcon">&nbsp;</span>增加等级
		</a>
		<a href="javascript:;" id="refreshButton" class="iconButton">
			<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
		</a>
	</div>
	<table id="listTable" class="list">
		<tr>
			<th>
				<span>店长等级</span>
			</th>
			<th>
				<span>邀请店长人数</span>
			</th>
			<th>
				<span>邀请奖金比例</span>
			</th>
			<th>
				<span>内购券发放数量</span>
			</th>
			<th>
				<span>${message("admin.common.handle")}</span>
			</th>
		</tr>
		[#list vipLevel as vipLevel]
			<tr>
				<td>
					${vipLevel.levelName}
				</td>
				<td>
					${vipLevel.inviteCondition}
				</td>
				<td>
					${vipLevel.bonusLevel}%
				</td>
				<td>
					${vipLevel.ticketNum}
				</td>
				<td>
					<a href="edit.jhtml?id=${vipLevel.id}">[${message("admin.common.edit")}]</a>
					<a href="javascript:;" class="delete" val="${vipLevel.id}">[${message("admin.common.delete")}]</a>
				</td>
			</tr>
		[/#list]
	</table>
</body>
</html>
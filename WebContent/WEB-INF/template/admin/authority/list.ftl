<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.productCategory.list")} - Powered By rsico</title>
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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; ${message("admin.productCategory.list")}
	</div>
	<div class="bar">
		<a href="add.jhtml" class="iconButton">
			<span class="addIcon">&nbsp;</span>${message("admin.common.add")}
		</a>
		<a href="javascript:;" id="refreshButton" class="iconButton">
			<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
		</a>
	</div>
	<table id="listTable" class="list">
		<tr>
			<th>
				<span>权限名称</span>
			</th>
			<th>
				<span>权限码</span>
			</th>
			<th>
				<span>Url</span>
			</th>
			<th>
				<span>排序</span>
			</th>
			<th>
				<span>${message("admin.common.handle")}</span>
			</th>
		</tr>
		[#list authMap.keySet() as key]
			<tr>
				<td>
					<span style="margin-left: 0px;color: #000000;">
						${message("Authority.AuthorityGroup."+key)}
					</span>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
			</tr>
			[#list authMap.get(key) as authority]
			<tr>
				<td>
					<span style="margin-left:20px;color: #000000;">
						${authority.name}
					</span>
				</td>
				<td>
					${authority.authority}
				</td>
				<td>
					${authority.url}
				</td>
				<td>
					${authority.order}
				</td>
				<td>
					<a href="edit.jhtml?id=${authority.id}">[${message("admin.common.edit")}]</a>
					<a href="javascript:;" class="delete" val="${authority.id}">[${message("admin.common.delete")}]</a>
				</td>
			</tr>
			[/#list]
		[/#list]
	</table>
</body>
</html>
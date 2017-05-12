<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>企业公告 - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 企业公告
	</div>
	
	<ul id="tab" class="tab">
	        <li>
				<input class="current" value="公告历史纪录" type="button">
			</li>
			<li>
				<input class="current" value="发布企业公告" type="button">
			</li>
		</ul>
	
	<form id="listForm" action="announcement.jhtml" method="get">
		<div  class="input tabContent">
		<div class="bar">
		    <div class="buttonWrap">
		       &nbsp;&nbsp;&nbsp;
		    </div>
			<div class="buttonWrap">
				<a href="javascript:;" id="deleteButton" class="iconButton disabled">
					<span class="deleteIcon">&nbsp;</span>${message("admin.common.delete")}
				</a>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="address">标题</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="linkman">内容</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createDate">${message("admin.common.createDate")}</a>
				</th>
			</tr>
			[#list page.content as notice]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${notice.id}" />
					</td>
					<td>
						${notice.title}
					</td>
					<td>
						${notice.content}
					</td>
					<td>
						<span title="${notice.createDate?string("yyyy-MM-dd HH:mm:ss")}">${notice.createDate}</span>
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
		</div>
	</form>
	<form id="inputForm" onSubmit="return false;" action="${base}/admin/tenant/announcement.jhtml" method="post" enctype="multipart/form-data">
		<table style="display: table;" class="input tabContent">
			<tbody>
				<tr>
					<th><span class="requiredField">*</span>公告主题: </th>
					<td><input name="title" style="width:350px" class="text" placeholder="请输入主题" value="" type="text"></td>
				</tr>
				<tr>
					<th><span class="requiredField">*</span>公告内容:</th>
					<td>
						<textarea class="text" name="content"></textarea>
					</td>
				</tr>
				<tr>
					<th></th>
					<td>
						<input class="button" value="确&nbsp;&nbsp;定" id="btn" type="submit">
						
					</td>
				</tr>
			</tbody>
		</table>
	</form>
	
	<script>
		$().ready(function() {
			var $inputForm = $("#inputForm");
			
			// 表单验证
			$inputForm.validate({
				rules: {
					"title": {
		 				required: true
		 			},
		 			"content": {
		 				required: true
		 			}
		 		},submitHandler:function(form) {
					$.ajax({
						url: "${base}/admin/tenant/send_announcement.jhtml",
						type: "POST",
						data: $("#inputForm").serialize(),
						dataType: "json",
						success: function(data) {
							console.log(data.content);
							$.dialog({
								title: "保存成功",
								content: data.content,
								width: 200,
								modal: true,
								onOk: function() {
									location.href='${base}/admin/tenant/announcement.jhtml';
								}
							});
						}
					});
		 		}
		 	});
		
		});
	</script>
</body>
</html>

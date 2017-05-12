<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>商家业务员[#if systemShowPowered] - Powered By rsico[/#if]</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/font.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/account.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/member.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/shop/css/admin.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/common/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/common/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/common/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/common/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/shop/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/shop/js/list.js"></script>
<style type="text/css">
.salesman{
	background-color:white;
	border:1px solid #b7c8d9;
	position:absolute; 
	left:40%; 
	top:30%;
	font:12px/1.6em Verdana,Geneva,Arial,Helvetica,sans-serif; 
	margin-left:-100px;
	margin-top:-100px;
	width:400px; 
	height:200px; 
	text-align:center;
	line-height:25px;
	z-index:100001;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $delete = $("#list a.delete");
	var $addButton = $("#addButton");
	var $addCancelButton = $("#addCancelButton");
	var $salesman = $("#salesman");
	var $dialogOverlay = $("#dialogOverlay");
	
	[@flash_message /]

	// 删除
	$delete.click(function() {
		var $this = $(this);
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.deleteConfirm")}",
			onOk: function() {
				$.ajax({
					url: "remove.jhtml",
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
	$addButton.click(function() {
		$dialogOverlay.show();
		$salesman.show();
	});
	$addCancelButton.click(function() {
		$dialogOverlay.hide();
		$salesman.hide();
	});
});
</script>
</head>
<body>
	<div id="dialogOverlay" class="dialogOverlay"></div>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 业务员
	</div>
       <div class="page-nav page-nav-app vip-guide-nav" id="app_head_nav">
          <ul class="links" id="mod_menus">
            <li  ><a class="" hideFocus="" href="${base}/admin/tenant/memberlist.jhtml?id=${id}">员工列表</a></li>
			<li  ><a class="on" hideFocus="" href="${base}/admin/tenant/salesMan.jhtml?id=${id}">业务员</a></li>          
			</ul>
    	 </div>
    	 <div class="bar">
		<a href="javascript:;" class="iconButton" id="addButton">
			<span class="addIcon">&nbsp;</span>${message("admin.common.add")}
		</a>
		<a href="javascript:;" id="refreshButton" class="iconButton">
			<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
		</a>
	</div>
			<div class="list">
				<table class="list" id="list">
					<tr>
						<th>
							注册日期
						</th>
						<th>
							用户名
						</th>
						<th>
							会员姓名
						</th>
						<th>
							余额
						</th>
						<th>
							积分
						</th>
						<th>
							最近登录时间
						</th>
						<th>
							状态
						</th>
						<th>
							操作 
						</th>
					</tr>
			[#list page.content as member ]
				[#if member == member.tenant.salesman]
						<tr[#if !credit_has_next] class="last"[/#if]>
							<td>
								<span title="${member.createDate?string("yyyy-MM-dd HH:mm:ss")}">${member.createDate?string("yyyy-MM-dd HH:mm:ss")}</span>
							</td>
							<td>
								${member.username}
							</td>
							<td>
								${member.name}
							</td>
							<td style="text-align:right;">
								${currency(member.balance, true)}
							</td>
							<td style="text-align:right;">
								${member.point}
							</td>
							<td>
								${member.loginDate?string("yyyy-MM-dd HH:mm:ss")!"-"}
							</td>
							<td  style="border-right: 0px">
							  [#if member.isEnabled ]
							     正常
							  [#else]
								   禁用
								[/#if]
							</td>
							<td>
							<a href="javascript:;" class="delete" val="${id}">[${message("admin.common.delete")}]</a>
							</td>
						[/#if]
					[/#list]
				</table>
				[#if !page.content?has_content]
					<p>${message("shop.member.noResult")}</p>
				[/#if]
			[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "?pageNumber={pageNumber}"]
				[#include "/shop/include/pagination.ftl"]
			[/@pagination]
			</div>
				</div>
				</div>
			<div style="text-align:center;">
					<form id="salesmanForm" action="saveSalesman.jhtml?id=${id}" method="post">
					<div class="list">
		    		<div id="salesman" class="salesman hidden"  >
							<div style="position:absolute; bottom:30%;left:70px;">
							选择业务员:
								<select id="salesmanId" name="salesmanId" type="text" class="text" size="12" style="width:150px;height:100px;background:none;">
									[#list page.content as member]
									<option value="${member.id}" selected>${member.name}</option>
									[/#list]
								</select>
							</div>
							<div style="position:absolute; bottom:8%;left:130px;border:1px;">
								<input type="submit" id="unionSubmit" class="button" value="${message("shop.order.ok")}" />
								<input type="button" id="addCancelButton" class="button" value="${message("shop.order.cancel")}" />
							</div>
					</div>
					</div>
				</form>
			</div>		
</body>
</html>
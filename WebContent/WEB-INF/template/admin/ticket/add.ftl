<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.member.list")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	[@flash_message /]
	var $listForm = $("#listForm");
	var $sendAll = $("#sendAll");
	var $send = $("#send");

   $sendAll.click(function() {
         	 listForm.action="sendAll.jhtml";
	         listForm.submit();
      });
   $send.click(function() {
         	 listForm.action="send.jhtml";
	         listForm.submit();
      });

});

 function go()
 {
 	listForm.action="query.jhtml";
	listForm.submit();
 }
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 定向发放 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="add.jhtml" method="GET">
		<div class="bar">
			<div class="buttonWrap">
			            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size="2"><strong>发放张数:</strong></font>
				<select name="num" style="width:100px;height:25px;" >
						<option value="1"  >1</option>
						<option value="2"  >2</option>
						<option value="3"  >3</option>
						<option value="4"  >4</option>
						<option value="5"  >5</option>
						<option value="10" >10</option>
						<option value="15" >15</option>
						<option value="30" >30</option>
					</select> &nbsp;&nbsp;<font size="2"><strong>张</strong></font>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="button" id="send" name="send" style="width:100px;" value="发放" />&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" class="button" id="sendAll" name="sendAll" style="width:100px;" value="全部发放" />
			</div>
		
			
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow"></span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="button" onclick="go()">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "mobile"] class="current"[/#if] val="mobile">电话</a>
						</li>
					</ul>
				</div>
			</div>
		
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="username">${message("Member.username")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="email">手机</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createDate">${message("admin.common.createDate")}</a>
				</th>
				
			</tr>
			[#list page.content as member]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${member.id}" />
					</td>
					<td>
						${member.name}
					</td>
					<td>
						${member.mobile}
					</td>
					<td>
						<span title="${member.createDate?string("yyyy-MM-dd HH:mm:ss")}">${member.createDate}</span>
					</td>
					
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
		</form>
</body>
</html>
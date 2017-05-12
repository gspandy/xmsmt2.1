[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<title>${message("admin.order.list")} - Powered By rsico</title>
		<meta name="author" content="rsico Team" />
		<meta name="copyright" content="rsico" />
		<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
		<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
		<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
		<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
		<script type="text/javascript">
		function search(){
			// listForm.action="search.jhtml";
			listForm.submit();
		};
		function resetDate(){
			$("#beginDate").val("");
			$("#endDate").val("");
		};
		
		
		$().ready(function() {
		
			var $listForm = $("#listForm");
			var $filterSelect = $("#filterSelect");
			var $filterOption = $("#filterOption a");
			var $print = $(".print");
		
			[@flash_message /]
			
			// 订单筛选
			$filterSelect.mouseover(function() {
				var $this = $(this);
				var offset = $this.offset();
				var $menuWrap = $this.closest("div.menuWrap");
				var $popupMenu = $menuWrap.children("div.popupMenu");
				$popupMenu.css({left: offset.left, top: offset.top + $this.height() + 2}).show();
				$menuWrap.mouseleave(function() {
					$popupMenu.hide();
				});
			});
			
			// 筛选选项
			$filterOption.click(function() {
				var $this = $(this);
				var $dest = $("#" + $this.attr("name"));
				if ($this.hasClass("checked")) {
					$dest.val("");
				} else {
					$dest.val($this.attr("val"));
				}
				$listForm.submit();
				return false;
			});
			
			// 打印选择
			$print.on("click", function() {
				var $this = $(this);
				if ($this.attr("url") != "") {
					window.open($this.attr("url"));
				}
			});
			
			//配送方式
			$('.admin_seach').on('mouseover',function(){
				$('.admin_seach .admin_slist').hide();
				$(this).find('.admin_slist').show();
			});
			$('.admin_slist li').on('mouseover',function(){
				$('.admin_slist .admin_slist_cont').hide();
				$(this).find('.admin_slist_cont').show();
				$(this).find('a').addClass('down');
			});
			$('.admin_slist li').on('mouseout',function(){
				$(this).find('a').removeClass('down');
			});
			$('.admin_slist li a').on('click',function(){
				var p=$(this).text();
				$(this).parents('.admin_seach').find('strong').text(p);
			});
			$('.admin_slist_cont span').on('click',function(){
				var p=$(this).text();
				$(this).parents('.admin_seach').find('strong').text(p);
			});
			$('.admin_seach').on('mouseleave',function(){
				$('.admin_seach .admin_slist').hide();
			});
			
			var $selectAll = $("#selectAll");
			var $ids=$("input[name='ids']");
			var $printButton=$("#exportButton");
		});
		</script>
	</head>
	<body>
		<form action="${base}/admin/vipReport/exportVolume.jhtml" id="exportForm" method="post">
	    </form>
		<div class="path">
			<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; Vip销售量统计 <span>(${message("admin.page.total", page.total)})</span>
		</div>
		<form id="listForm" action="volumeList.jhtml" method="get">
			<div class="bar">
				<div class="buttonWrap">
					<a href="javascript:;" id="refreshButton" class="iconButton">
						<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
					</a>
					<div class="menuWrap">
						<a href="javascript:;" id="pageSizeSelect" class="button">
							${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
						</a>
						<div class="popupMenu">
							<ul id="pageSizeOption">
								<li>
									<a href="javascript:;"[#if page.pageSize == 10] class="current"[/#if] val="10">10</a>
								</li>
								<li>
									<a href="javascript:;"[#if page.pageSize == 20] class="current"[/#if] val="20">20</a>
								</li>
								<li>
									<a href="javascript:;"[#if page.pageSize == 50] class="current"[/#if] val="50">50</a>
								</li>
								<li>
									<a href="javascript:;"[#if page.pageSize == 100] class="current"[/#if] val="100">100</a>
								</li>
							</ul>
						</div>
					</div>
					<div class="menuWrap">
						<div style="float: right;">
							<input id="beginDate" type="text" name="beginDate" value="${beginDate}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd', maxDate: '#F{$dp.$D(\'endDate\')}'});" />
							-
							<input id="endDate" type="text" name="endDate" value="${endDate}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd', minDate: '#F{$dp.$D(\'beginDate\')}'});" />
							<input type="button" class="button" onclick="search()" value="搜索" />
							<input type="button" class="button" onclick="resetDate()" value="清空" />
						</div>
					</div>
				</div>
			</div>
			<table id="listTable" class="list">
				<tr>
					<th>
						<span>排名</span>
					</th>
					<th>
						<span>Vip</span>
					</th>
					<th>
						<span>商品销量（件）</span>
					</th>
					<th>
						<span>操作</span>
					</th>
				</tr>
				[#list page.content as vipReport]
					<tr>
						<td>
							${vipReport_index + 1 + (page.pageNumber-1) * page.pageSize}
						</td>
						<td>
							${vipReport.vipName}
						</td>
						<td>
							${vipReport.volume}
						</td>
						<td>
							<a href="volumeView.jhtml?id=${vipReport.vipId}&beginDate=${beginDate}&endDate=${endDate}">查看订单明细</a>
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
[#assign shiro=JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title></title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $listForm = $("#listForm");
	var $filterSelect = $("#filterSelect");
	var $filterOption = $("#filterOption a");
	var $print = $(".print");
	var $search = $("#search");
	var $mobile = $("#mobile");

	[@flash_message /]
	
	// 会员申请内购券
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
	
	 $search.click(function() {
   		  listForm.action="member_list.jhtml";
	      listForm.submit();
      });
      
      $mobile.keydown(function(e) {
	 	  var key = e.which;
	 	  if (key == 13) {
	 	  	 listForm.action="member_list.jhtml";
	     	 listForm.submit();
	 	  }
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
	
	var $selectAll = $("#selectAll");
	var $ids=$("input[name='ids']");
	var $printButton=$("#exportButton");
	// 全选
	$selectAll.click( function() {
		var $this = $(this);
		var $enabledIds = $("#listTable input[name='ids']:enabled");
		if ($this.prop("checked")) {
			$enabledIds.prop("checked", true);
			if ($enabledIds.filter(":checked").size() > 0) {
				$printButton.removeClass("disabled");
			} else {
				$deleteButton.addClass("disabled");
			}
		} else {
			$enabledIds.prop("checked", false);
			$printButton.addClass("disabled");
		}
	});
		
	// 选择
	$ids.click( function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").addClass("selected");
			$printButton.removeClass("disabled");
		} else {
			$this.closest("tr").removeClass("selected");
			if ($("#listTable input[name='ids']:enabled:checked").size() > 0) {
				$printButton.removeClass("disabled");
			} else {
				$printButton.addClass("disabled");
			}
		}
	});
	$printButton.click(function(){
		var $this =$(this);
		if($this.hasClass("disabled")){
			return false;
		}
		
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "是否导出Excel?",
			ok: message("admin.dialog.ok"),
			cancel: message("admin.dialog.cancel"),
			onOk: function() {
				$checkedIds.each(function(){
					$("#exportForm").append('<input type="hidden" name="ids" value="'+$(this).val()+'">');
				});
				$("#exportForm").submit();
			}
		});
		
	});
});

function ok(){
		var chk_value =[];
		$('input[name="ids"]:checked').each(function(){
			chk_value.push($(this).val());
		}); 
		 var size=$("input[type='checkbox'][name=ids]:checked").length;
	   	  if(size==0)
	   	  {
	   	     alert("请选中记录！")
	   	  }else{
		     listForm.action="agree.jhtml?ids="+chk_value;
		     listForm.submit();
		  }
	}
	
function no(){
		var chk_value =[];
		$('input[name="ids"]:checked').each(function(){
			chk_value.push($(this).val());
		}); 
		 var size=$("input[type='checkbox'][name=ids]:checked").length;
	   	  if(size==0)
	   	  {
	   	     alert("请选中记录！")
	   	  }else{
		     listForm.action="refuse.jhtml?ids="+chk_value;
		     listForm.submit();
		  }
	}
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/ticketApply/list.jhtml">${message("admin.path.index")}</a> &raquo; 会员申请内购券 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="${base}/admin/ticketApply/list.jhtml" method="get">
		<input type="hidden" id="applyTypeParam" name="applyTypeParam" value="memberApplyToTenant" />
		<div>
			<div style="margin:10px">
			会员手机号：&nbsp;<input type="text" id="mobile" name="mobile" class="text" value="${mobile}"  />&nbsp;&nbsp;
			申请状态：&nbsp;<select name="applyStatusParam" id="applyStatusParam" style="width:140px">
							<option value="">--请选择--</option>
							<option value="apply"  [#if ("apply" == applyStatusParam)] selected[/#if]>申请中</option>
							<option value="confirmed" [#if ("confirmed" == applyStatusParam)] selected[/#if] >同意发放</option>
							<option value="rejected" [#if ("rejected" == applyStatusParam)] selected[/#if] >拒绝</option>
						</select>
			<input type="button" class="button" id="search" value="搜索" />
			<input type="button" class="button" onclick="ok();" id="agree" value="同意" />
			<input type="button" class="button" onclick="no();" id="refuse" value="拒绝" />
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<span>序号</span>
				</th>
				<th>
					<span>店长姓名</span>
				</th>
				<th>
					<span>店长手机号</span>
				</th>
				<th>
					<span>申请时间</span>
				</th>
				<th>
					<span>结算状态</span>
				</th>
				<th>
					<span>操作</span>
				</th>
			</tr>
			[#list page.content as ticketApply]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${ticketApply.id}" />
					</td>
					<td>
						${ticketApply.id}
					</td>
					<td>
						${ticketApply.member.name}
					</td>
					<td>
						${ticketApply.member.mobile}
					</td>
					<td>
						<span title="${ticketApply.createDate?string("yyyy-MM-dd HH:mm:ss")}">${ticketApply.createDate}</span>
					</td>
					<td>
						[#if ticketApply.applyStatus == 'apply']申请中[/#if] 
						[#if ticketApply.applyStatus == 'confirmed']已发放[/#if] 
						[#if ticketApply.applyStatus == 'rejected']已拒绝[/#if] 
					</td>
					[#if ticketApply.applyStatus == 'apply']
						<td>
							<a href="${base}/admin/ticketApply/agree.jhtml?ids=${ticketApply.id}">[同意]</a>&nbsp;
							<a href="${base}/admin/ticketApply/refuse.jhtml?ids=${ticketApply.id}">[拒绝]</a>
						</td>
					[/#if] 
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>
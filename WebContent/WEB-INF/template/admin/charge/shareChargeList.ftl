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
        $().ready(function() {

            var $listForm = $("#listForm");
            var $filterSelect = $("#filterSelect");
            var $filterOption = $("#filterOption a");
            var $print = $(".print");
            var $export = $("#excel");
            var $excelDetail = $("#excelDetail");

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

            $export.click(function() {
                listForm.action="export.jhtml";
                listForm.submit();
                  listForm.action="shareChargeList.jhtml";
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
            
            $excelDetail.click(function() {
    
   	  
   	 		 $("#ids").val("1");
   	        listForm.action="export.jhtml";
        	listForm.submit();
       	    listForm.action="shareChargeList.jhtml";
       	  $("#ids").val("");
   	  
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
    </script>
</head>
<body>
<form action="${base}/admin/order/exportExcel.jhtml" id="exportForm" method="post">
</form>
<div class="path">
    <a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 分享佣金明细 <span></span>
</div>
<form id="listForm" action="shareChargeList.jhtml" method="get">
    <input type="hidden" id="orderStatus" name="orderStatus" value="${orderStatus}" />
    <input type="hidden" id="paymentStatus" name="paymentStatus" value="${paymentStatus}" />
    <input type="hidden" id="shippingStatus" name="shippingStatus" value="${shippingStatus}" />
    <div>
        <div style="margin:10px">
            选择时间：&nbsp;<select name="time" style="width:140px">
            <option value="">--请选择--</option>
            [#list queryRealTime as queryRealTime]
                <option value="${queryRealTime}"  [#if ("${queryRealTime}" == time)] selected[/#if]>${queryShowTime[queryRealTime_index]}</option>
            [/#list]

            </select>
            结算状态：&nbsp;<select name="orderStatusParam" style="width:140px">
            <option value="">--请选择--</option>
            <option value="0" [#if ("0" == orderStatusParam)] selected[/#if]>未结算</option>
            <option value="1" [#if ("1" == orderStatusParam)] selected[/#if] >可结算</option>
            <option value="2" [#if ("2" == orderStatusParam)] selected[/#if]>已结算</option>
 			<option value="3" [#if ("3" == orderStatusParam)] selected[/#if] >已取消</option>
 			<option value="4" [#if ("4" == orderStatusParam)] selected[/#if] >已发放</option>
        </select>
            [#--宝贝名称：&nbsp;<input type="text" name="productName" class="text" value="${productName}"  />&nbsp;&nbsp;--]
            店长名称：<input type="text" name="username" class="text" value="${username}"  />
            <input type="hidden" name="ids" id="ids" class="text" />
          [#--  订单编号：<input type="text" name="sn" class="text" value="${sn}"  />--]
       
      [#--  <div style="margin:10px">
            成交时间：
            <input id="startTime" type="text" name="startTime" value="${startTime}" class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd  HH:mm:ss', maxDate: '#F{$dp.$D(\'endTime\')}'});" />&nbsp;—
            <input id="endTime" type="text" name="endTime" value="${endTime}"class="text" maxlength="200" readonly onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd  HH:mm:ss', minDate: '#F{$dp.$D(\'startTime\')}'});" />
        </div>--]
            <input type="submit" class="button" value="搜索" />
            	<input type="button" id="excelDetail" class="button" value="导出明细报表"  style="height:28px;"/>
            	<input type="button" id="excel" class="button" value="导出本月明细报表"  style="height:28px;"/>
      
         </div>
    </div>
    <div style="height:30px"><span>订单合计金额： ${currency(totalAmount, true)}</span> </div>
    <div style="height:30px"><span>分享佣金合计金额： ${currency(totalSettleCharge, true)}</span> </div>
    <table id="listTable" class="list">
        <tr>
            <th class="check">
                <input type="checkbox" id="selectAll" />
            </th>
            <th>
                <span>序号</span>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="member">店长姓名</a>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="consignee">店长手机号</a>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="paymentMethodName">订单编号</a>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="shippingMethodName">付款日期</a>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="orderStatus">订单金额（¥）</a>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="tenant">分享佣金金额（¥）</a>
            </th>
            <th>
                <a href="javascript:;" class="sort" name="tenant">结算状态</a>
            </th>
		[@shiro.hasPermission name = "admin:print"]
            <th>
                <span>${message("admin.order.print")}</span>
            </th>
		[/@shiro.hasPermission]
		
        </tr>
	[#list page.content as order]
        <tr>
            <td>
                <input type="checkbox" name="ids" value="${order.orderId}" />
            </td>
            <td>
			${order_index?if_exists+1}
            </td>
            <td>
			${order.ownerName}
            </td>
            <td>
			${order.mobile}
            </td>
            <td>
			${order.sn}
            </td>
            <td>
			${order.paymentDate}
            </td>
            <td>
            
            	${currency(order.orderAmount, true)}
            </td>
            <td>
            	${currency(order.settleCharge, true)}
            </td>
            <td>
			${order.status}
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
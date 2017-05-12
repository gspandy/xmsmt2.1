<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.refunds.list")} - Powered By rsico</title>
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
	var $confirmAll = $("#confirmAll");
	var $confirm = $("#confirm");
  //$confirmAll.click(function() {
       //  var text=1;
	    // pwdIsNull(text);
//});
   $confirm.click(function() {
         //var text=0;
         //pwdIsNull(text);
      var size=$("input[type='checkbox'][name=ids]:checked").length;
   	  if(size==0)
   	  {
   	     alert("请选中需要确认的退款！")
   	  }else{
   	      $("#listForm").action="confirm.jhtml";
   		  $("#listForm").submit();
   	   }
      });

  });
  //输入支付密码
  function pwd(){
	  var size=$("input[type='checkbox'][name=ids]:checked").length;
   	  if(size==0)
   	  {
   	     alert("请选中需要确认的退款！")
   	  }else{
   	  $.dialog({
   		title: "支付密码",
   		[@compress single_line = true]
   			content: '
   			<table id="moreTable" class="moreTable input tabContent">
   				<tr>
   					<th>
   					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <font size="3px"><strong>请输入支付密码:<\/strong><\/font>&nbsp;&nbsp;
   					</th>
   					<td>
   					<input type="password" class="text" id="pwd" name="pwd"\/>
   					<\/td>
   				<\/tr>
   			<\/table>',
   		[/@compress]
   		width: 470,
   		modal: true,
   		ok: "${message("admin.dialog.ok")}",
   		cancel: "${message("admin.dialog.cancel")}",
   	    onOk: function() {
   		   var cashPwd=$("#pwd").val();
   		   if(cashPwd!=''){
   			       $("#cashPwd").val(cashPwd);
   				   $("#listForm").action="confirm.jhtml";
   				   $("#listForm").submit();
   		       }else{
   			       alert("请输入支付密码！");
   			       $(this).dialog("open");
   		       }
   		
   		  
   			
   		     }
      	   });
   	    }
  }  
  
  //输入支付密码
  function pwdAll(){
       $.dialog({
		title: "支付密码",
		[@compress single_line = true]
			content: '
			<table id="moreTable" class="moreTable input tabContent">
				<tr>
					<th>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <font size="3px"><strong>请输入支付密码:<\/strong><\/font>&nbsp;&nbsp;
					</th>
					<td>
					<input type="password" class="text" id="pwd" name="pwd"\/>
					<\/td>
				<\/tr>
			<\/table>',
		[/@compress]
		width: 470,
		modal: true,
		ok: "${message("admin.dialog.ok")}",
		cancel: "${message("admin.dialog.cancel")}",
		onOk: function() {
			var cashPwd=$("#pwd").val();
			if(cashPwd!=''){
				  $("#cashPwd").val(cashPwd);
				  $("#listForm").action="confirmAll.jhtml";
				  $("#listForm").submit();
			  }else{
				  alert("请输入支付密码！")
			  }
		}
	});
  }
   //设置支付密码
  function confirmPwd(text){
       var text=text;
   	  $.dialog({
   		title: "设置支付密码",
   		[@compress single_line = true]
   			content: '
   			<table id="moreTable" class="moreTable input tabContent">
   				<tr>
   					<th>
   					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <font size="3px"><strong>密码:<\/strong><\/font>&nbsp;&nbsp;
   					</th>
   					<td>
   					<input type="password" class="text" id="setPwd" name="setPwd"\/>
   					<\/td>
   				<\/tr>
   				<tr>
   					<th>
   					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <font size="3px"><strong>确认密码:<\/strong><\/font>&nbsp;&nbsp;
   					</th>
   					<td>
   					<input type="password" class="text" id="confirmPwd" name="confirmPwd"\/>
   					<\/td>
   				<\/tr>
   			<\/table>',
   		[/@compress]
   		width: 470,
   		modal: true,
   		ok: "${message("admin.dialog.ok")}",
   		cancel: "${message("admin.dialog.cancel")}",
   	    onOk: function() {
   	           var setPwd=$("#setPwd").val();
   	           var confirmPwd=$("#confirmPwd").val();
   	           if(setPwd==""){
   	             alert("请输入密码！"); 
   	              $(this).dialog("open");
   	           }else if(confirmPwd==""){
   	               alert("请输入确认密码！"); 
   	               $(this).dialog("open");
   	           }else if(setPwd==confirmPwd){
   	                 $.ajax({
	    	                  url: "savePwd.jhtml",
		                      type: "POST",
		                      data: {setPwd: $("#setPwd").val()},
		                      dataType: "json",
		                      beforeSend: function() {
          
		                       },
		                       success: function(data) {
		                       if(data==1){
		                         if(text==0){
		                               pwd();
		                             }
		        	             if(text==1) 
		        	                 {
		        	                    pwdAll();
		        	                  }
		                       }else if(data==2){
		                         alert("支付密码不能与登录密码相同请重新输入！");
		                       }else{
		                         alert("支付密码设置失败！");
		                       }
		                     }
	                    });
   	           }else{
   	               alert("密码与确认密码不一致请重新输入！");
   	               $(this).dialog("open");
   	           }
   		      
   		     }
      	   });
   	  
  }  
     //验证是否设置支付密码
     function pwdIsNull(text){
      var text=text;
	  $.ajax({
	    	url: "pwdIsNull.jhtml",
		    type: "GET",
		    dataType: "json",
		    beforeSend: function() {
          
		    },
		    success: function(data) {
		         var isNull=data;
		    	 if(isNull==1)
		         {     
		        	 if(confirm('没有支付密码是否设置?')){
		        	    confirmPwd(text);
		        	 }
		        	 
		         }else if(isNull==0){
		         if(text==0){
		              pwd();
		            }
		        	if(text==1) 
		        	{
		        	  pwdAll();
		        	}
		         }
		    }
	     });
     } 
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 退款确认订单 <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="confirm.jhtml" method="get">
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
				<input type="button" id="confirm" class="button" style="height:28px" value="确认退款" />
				<input type="hidden" id="confirmAll" class="button"  style="height:28px"value="全部确认" />
				<input type="hidden" id="cashPwd" name="cashPwd"/>
				</div>
			</div>
			<div class="menuWrap">
				<div class="search">
					<span id="searchPropertySelect" class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<div class="popupMenu">
					<ul id="searchPropertyOption">
						<li>
							<a href="javascript:;"[#if page.searchProperty == "sn"] class="current"[/#if] val="sn">${message("Refunds.sn")}</a>
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
					<a href="javascript:;" class="sort" name="sn">订单编号</a>
				</th>
				<th>
					订单金额
				</th>
				<th>
					<a href="javascript:;" class="sort" name="member">会员</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="type">支付方式</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="amount">退款金额</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="amount">订单状态</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createDate">${message("admin.common.createDate")}</a>
				</th>
				<th>
					<span>${message("admin.common.handle")}</span>
				</th>
			</tr>
			[#list page.content as refunds]
				<tr>
					<td>
						<input type="checkbox" name="ids" value="${refunds.id}" />
					</td>
					<td>
						${refunds.order.sn}
					</td>
					<td>
						${currency(refunds.order.amountPaid, true)}
					</td>
					<td>
						${refunds.order.member.nickName}
					</td>
					<td>
						${message("Refunds.Method." + refunds.method)}
					</td>
					<td>
						${currency(refunds.amount, true)}
					</td>
					<td>
						${refunds.order.orderStatusName}
					</td>
					<td>
						<span title="${refunds.createDate?string("yyyy-MM-dd HH:mm:ss")}">${refunds.createDate}</span>
					</td>
					<td>
						<a href="viewRefunds.jhtml?id=${refunds.order.id}">[${message("admin.common.view")}]</a>
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
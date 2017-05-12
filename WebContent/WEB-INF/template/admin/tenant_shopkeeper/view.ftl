<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title></title>
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
            var $inputForm = $("#inputForm");
            var $areaId = $("#areaId");
            var $search = $("#search");
            var $reset = $("#reset");
            
        [@flash_message /]
        
         $reset.click(function() {
   		  listForm.action="reset.jhtml";
	      listForm.submit();
      	});
        
        });
    </script>
</head>
<body>
<form id="listForm" action="reset.jhtml" method="get">
<input type="hidden" name="id" value="${tenantShopkeeper.id}" />
<ul id="tab" class="tab">
    <li>
        <input type="button" value="基本信息" />
    </li>
<!--     <li> -->
<!--         <input type="button" value="个人资料" /> -->
<!--     </li> -->
</ul>
<table class="input tabContent">
    <tr>
        <th>
        	手机号码
        </th>
        <td>
        	${tenantShopkeeper.member.mobile}
        </td>
    </tr>
    <tr>
        <th>
        	真实姓名
        </th>
        <td>
            ${tenantShopkeeper.member.name}
        </td>
    </tr>
    <tr>
        <th>
       		推荐人姓名
        </th>
        <td>
        	[#if tenantShopkeeper.recommendMember??]
        		${tenantShopkeeper.recommendMember.name}
        	[/#if]
        </td>
    </tr>
    <tr>
        <th>
        	店长开通日期
        </th>
        <td>
            ${tenantShopkeeper.openDate}
        </td>
    </tr>
    <tr>
        <th>
        	店长等级
        </th>
        <td>
        	${tenantShopkeeper.vipLevel.levelName}
        </td>
    </tr>
    <tr>
        <th>
        	支付宝账号
        </th>
        <td>
        	${cardNo}
        </td>
    </tr>
    <tr>
        <th>
        	提现密码
        </th>
        <td>
        	<input type="password" value="${tenantShopkeeper.member.cashPwd}" />
        	<input type="button" class="button" id="reset"  value="重置" />
        	<font color="red">如果重置密码，则密码改为123456。</font>
        </td>
    </tr>
    <tr>
        <th>
       		提现保障
        </th>
        <td>
        	${tenantShopkeeper.member.familyType}${tenantShopkeeper.member.familyName}
        </td>
    </tr>
</table>
<table class="input tabContent">
     <tr>
        <th>
        	昵称
        </th>
        <td>
        	${tenantShopkeeper.member.nickName}
        </td>
    </tr>    
     <tr>
        <th>
        	性别
        </th>
        <td>
        	[#if tenantShopkeeper.member.gender == "male"]男[/#if]
        	[#if tenantShopkeeper.member.gender == "female"]女[/#if]
        </td>
    </tr>    
</table>
<table class="input">
    <tr>
        <th>
            &nbsp;
        </th>
        <td>
            <input type="button" class="button" value="返回" onclick="location.href='list.jhtml'" />
        </td>
    </tr>
</table>
</form>
</body>
</html>
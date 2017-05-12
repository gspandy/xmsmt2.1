<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.product.add")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<style type="text/css">
	.specificationSelect {
		height: 100px;
		padding: 5px;
		overflow-y: scroll;
		border: 1px solid #cccccc;
	}
	
	.specificationSelect li {
		float: left;
		min-width: 150px;
		_width: 200px;
	}
</style>
<script type="text/javascript">
$().ready(function() {
	var $inputForm = $("#inputForm");
	[@flash_message /]
	// 表单验证
	$inputForm.validate({
		rules: {
 			articleCategoryId: "required",
			"name": {
 				required: true
 			},
 		}
 	});

});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;支付宝设置
	</div>
		<form id="inputForm" action="saveMemberBankEdits.jhtml" method="post" enctype="multipart/form-data">
		   <table class="input">
		   <input type="hidden" class="text" name="type" value="${memberBank.type}"/>
		   <input type="hidden" class="text" name="validity" value="${memberBank.validity}"/>
		   <tr>
				<th>
					收款支付宝账号：
				</th>
				<td>
				</td>
			</tr>
		   <tr>
				<th>
					企业名称：
				</th>
				<td>
				        <input type="hidden" class="text" name="id" value="${memberBank.id}"/>
  					    <input type="text" class="text" name="depositUser" value="${memberBank.depositUser}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝账号：
				</th>
				<td>
  					    <input type="text" class="text" name="cardNo" value="${memberBank.cardNo}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝PID：
				</th>
				<td>
  					    <input type="text" class="text" name="bankProvince" value="${memberBank.bankProvince}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝KEY：
				</th>
				<td>
  					    <input type="text" class="text" name="depositBank" value="${memberBank.depositBank}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝私钥：
				</th>
				<td>
  					    <input type="text" class="text" name="bankCity" value="${memberBank.bankCity}"/>
				</td>
			</tr>
		</table>
		
		<table class="input">
		   <input type="hidden" class="text" name="typePay" value="${memberBankPay.type}"/>
		   <input type="hidden" class="text" name="validityPay" value="${memberBankPay.validity}"/>
		   <tr>
				<th>
					付款支付宝账号：
				</th>
				<td>
				</td>
			</tr>
		   <tr>
				<th>
					企业名称：
				</th>
				<td>
				        <input type="hidden" class="text" name="idPay" value="${memberBankPay.id}"/>
  					    <input type="text" class="text" name="depositUserPay" value="${memberBankPay.depositUser}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝账号：
				</th>
				<td>
  					    <input type="text" class="text" name="cardNoPay" value="${memberBankPay.cardNo}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝PID：
				</th>
				<td>
  					    <input type="text" class="text" name="bankProvincePay" value="${memberBankPay.bankProvince}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝KEY：
				</th>
				<td>
  					    <input type="text" class="text" name="depositBankPay" value="${memberBankPay.depositBank}"/>
				</td>
			</tr>
			<tr>
				<th>
					支付宝私钥：
				</th>
				<td>
  					    <input type="text" class="text" name="bankCityPay" value="${memberBankPay.bankCity}"/>
				</td>
			</tr>
		     <tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
				</td>
			</tr>
		</table>
		</form>
		</form>
</body>
</html>

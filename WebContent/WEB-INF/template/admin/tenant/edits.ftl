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
	var $inputFormSell = $("#inputFormSell");
	var $div = $("#div");
	var timeout;
	[@flash_message /]
	// 表单验证
	$inputForm.validate({
		rules: {
 			articleCategoryId: "required",
			"name": {
 				required: true
 			},
 			"tenantType": {
 				required: true
 			},
 			"shortName": {
 				required: true
 			},
 			"address": {
 				required: true
 			},
 			"linkman": {
				required: true
 			},
 			"telephone": {
 				required: true
 			},
 			"legalRepr": {
 				required: true
 			},
 			"licenseCode": {
 				required: true
 			}
 		}
 	});
	$inputFormSell.validate({
		rules: {
			tradeNum: {
				integer: true,positive:true
			}
 		}
 	});
});
function disPlay(){
	var i=document.from2.skipType.selectedIndex;
	var type=document.from2.skipType.options[i].value; 
	if(type=="")
	{
		document.from2.url.disabled=true;
		document.from2.url.value="";
		document.from2.productIdName.disabled=true;
		document.from2.productIdName.value="";
	}else{
	   if(type==0){
		document.from2.productIdName.disabled=false;
		document.from2.url.disabled=true;
		document.from2.imageFile.disabled=false;
		document.from2.url.value="";
	}else if(type==1){
		document.from2.url.disabled=false
		document.from2.productIdName.disabled=true;
		document.from2.imageFile.disabled=false;
		document.from2.productIdName.value="";
	}
	}
	
}
function clean(type){
  if(type==0)
	{
		document.from2.productId.value="";
		document.from2.productIdName.value="";
	}else if(type==1){
		document.from2.productIdT.value="";
		document.from2.productIdNameT.value="";
	}else if(type==2){
		document.from2.productIdTH.value="";
		document.from2.productIdNameTH.value="";
	}
}
function disPlayT(){
	var i=document.from2.skipTypeT.selectedIndex;
	var type=document.from2.skipTypeT.options[i].value; 
	if(type=="")
	{
		document.from2.urlT.disabled=true;
		document.from2.urlT.value="";
		document.from2.productIdNameT.disabled=true;
		document.from2.productIdNameT.value="";
	}else{
	  if(type==0){
		  document.from2.productIdNameT.disabled=false;
		  document.from2.urlT.disabled=true;
		  document.from2.urlT.value="";
	  }else if(type==1){
		  document.from2.urlT.disabled=false
		  document.from2.productIdNameT.disabled=true;
		  document.from2.productIdNameT.value="";
	  }
	}
}
function disPlayTH(){
	var i=document.from2.skipTypeTH.selectedIndex;
	var type=document.from2.skipTypeTH.options[i].value; 
	if(type=="")
	{
		document.from2.urlTH.disabled=true;
		document.from2.urlTH.value="";
		document.from2.productIdNameTH.disabled=true;
		document.from2.productIdNameTH.value="";
	}else{
	   if(type==0){
		   document.from2.productIdNameTH.disabled=false;
		   document.from2.urlTH.disabled=true;
		   document.from2.urlTH.value="";
	    }else if(type==1){
		   document.from2.urlTH.disabled=false
		   document.from2.productIdNameTH.disabled=true;
		   document.from2.productIdNameTH.value="";
	   }
	}
}

function products(){
	$.ajax({
		url: "ajaxAdd.jhtml",
		type: "GET",
		data: {searchValue: encodeURI($("#searchValue").val())},
		dataType: "json",
		beforeSend: function() {
           //$("#listTable").empty();
		},
		success: function(data) {
		    $("#deleteTable").empty();
			var trHtml = "";
			trHtml +='<table id="listTable" class="list"><tr><th class="check"><input type="checkbox" id="selectAll" /></th><th><a href="javascript:;" class="sort" name="sn">编号</a></th><th><a href="javascript:;" class="sort" name="name">名称</a></th><th><a href="javascript:;"class="sort"name="productCategory">商品分类</a></th><th><a href="javascript:;" class="sort" name="price">售价</a></th><th><a href="javascript:;" class="sort" name="isMarketable">上架</a></th></tr>';
			if(data.productList!=null){
			 $.each(data.productList, function(i, product) {
				 trHtml +='<tr><td><input type="checkbox" name="ids" value="'+product.id+'" /></td><td>'+product.sn+'</td><td><span title="'+product.fullName+'" id="names'+product.id+'">'+product.fullName+'</span></td><td>'+product.productCategory.name+'</td><td>'+product.price+'</td>';
				if(product.isMarketable){
					 trHtml +='<td>'+"已上架"+'</td></tr>';
				}else{
				    trHtml +='<td>'+"未上架"+'</td></tr>';
				}	
			});	
                trHtml +='</table>';			
			}
					 
			$("#deleteTable").append(trHtml);
		}
	});
}
function proudt(text) {
     var types=text;
	$.dialog({
		title: "商品选择",
		[@compress single_line = true]
			content: '
			<form id="listForm" action="list.jhtml" method="get">
		     <input type="hidden" id="productCategoryId" name="productCategoryId" value="${productCategoryId}" \/>
		     <input type="hidden" id="brandId" name="brandId" value="${brandId}" \/>
		     <input type="hidden" id="promotionId" name="promotionId" value="${promotionId}" \/>
		     <input type="hidden" id="tagId" name="tagId" value="${tagId}" \/>
		     <input type="hidden" id="isMarketable" name="isMarketable" value="[#if isMarketable??]${isMarketable?string("true", "false")}[/#if]" \/>
		     <input type="hidden" id="isList" name="isList" value="[#if isList??]${isList?string("true", "false")}[/#if]" \/>
		     <input type="hidden" id="isTop" name="isTop" value="[#if isTop??]${isTop?string("true", "false")}[/#if]" />
		     <input type="hidden" id="isGift" name="isGift" value="[#if isGift??]${isGift?string("true", "false")}[/#if]" \/>
		     <input type="hidden" id="isOutOfStock" name="isOutOfStock" value="[#if isOutOfStock??]${isOutOfStock?string("true", "false")}[/#if]" \/>
		     <input type="hidden" id="isStockAlert" name="isStockAlert" value="[#if isStockAlert??]${isStockAlert?string("true", "false")}[/#if]" \/>
		     <div class="bar">
		     <\/div>
		     <div id="deleteTable">
		     <table id="listTable" class="list">
				<tr>
					<th class="check">
						<input type="radio" id="selectAll" \/>
					<\/th>
					<th>
						<a href="javascript:;" class="sort" name="sn">${message("Product.sn")}<\/a>
					<\/th>
					<th>
					    <a href="javascript:;" class="sort" name="name">${message("Product.name")}<\/a>
				    <\/th>
				    <th>
					<a href="javascript:;" class="sort" name="productCategory">${message("Product.productCategory")}<\/a>
				<\/th>
				<th>
					<a href="javascript:;" class="sort" name="price">${message("Product.price")}<\/a>
				<\/th>
				<th>
					<a href="javascript:;" class="sort" name="isMarketable">${message("Product.isMarketable")}<\/a>
				<\/th>
				<\/tr>
				[#if productList!=null]
				[#list productList as product]
					<tr>
						<td>
							<input type="radio" name="ids" value="${product.id}" \/>
						<\/td>
						<td>
							${product.sn}
						<\/td>
						<td>
						<span title="${product.fullName}" id="names${product.id}">
							${abbreviate(product.fullName, 50, "...")}
							[#if product.isGift]
								<span class="gray">[${message("admin.product.gifts")}]<\/span>
							[/#if]
						<\/span>
						[#list product.validPromotions as promotion]
							<span class="promotion" >${promotion.name}<\/span>
						[/#list]
					<\/td>
				<td>
					 ${product.productCategory.name}
				<\/td>
				<td>
					${currency(product.price)}
				<\/td>
			    <td>
			    [#if product.isMarketable]
                <span>     已上架<\/span>
                  [#else]
               <span>      未上架<\/span>
                [/#if]
	        	<\/td>
			  <\/tr>
				[/#list]
				[/#if]
			<\/table>
			<\/div>
		    <\/form>
			',
		[/@compress]
		width: 870,
		modal: true,
		ok: "${message("admin.dialog.ok")}",
		cancel: "${message("admin.dialog.cancel")}",
		onOk: function() {
			var text="";  
	        $("input[name=ids]").each(function() {  
	            if ($(this).attr("checked")) {  
	                text +=$(this).val();  
	            }  
	        });  
	        if(types==0)
	        {
	          document.from2.productId.value=text;
			  document.from2.productIdName.value=$("#names"+text).text();
	        }
	        if(types==1)
	        {
	          document.from2.productIdT.value=text;
			  document.from2.productIdNameT.value=$("#names"+text).text();
	        }
	        if(types==2)
	        {
	          document.from2.productIdTH.value=text;
			  document.from2.productIdNameTH.value=$("#names"+text).text();
	        }
	       
		}
	});
};
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo; 基本信息维护
	</div>
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="企业基本信息" />
			</li>
			<li>
				<input type="button" value="店长门槛" />
			</li>
			<li>
				<input type="button" value="内购店banner" />
			</li>
			<li>
				<input type="button" value="邀请奖金设置" />
			</li>
		</ul>
		<form id="inputForm" action="save.jhtml" method="post" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${tenant.id}" />
		<input type="hidden" name="template" value="${tenant.template}" />
		<input type="hidden" name="score" value="${tenant.score}" />
		<input type="hidden" name="totalScore" value="${tenant.totalScore}" />
		<input type="hidden" name="price" value="${tenant.price}" />
		<input type="hidden" name="typeId" value="1" />
		<table class="input tabContent">
		  <tr>
		   <td>
		   
		   <table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>企业名称: 
				</th>
				<td>
					<input type="text" name="name" class="text" value="${tenant.name}"  />
				</td>
			</tr>
			
			<tr>
				<th>
					<span class="requiredField">*</span>品牌: 
				</th>
				<td>
					<input type="text" name="shortName" class="text" value="${tenant.shortName}"  />
				</td>
			</tr>
			<tr>
				<th>
					 <span class="requiredField">*</span>企业法人: 
				</th>
				<td>
					<input type="text" name="legalRepr" class="text" value="${tenant.legalRepr}"  />
				</td>
			</tr>
			<tr>
				<th>
				   <span class="requiredField">*</span>工商营业执照号: 
				</th>
				<td>
					<input type="text" name="licenseCode" class="text" value="${tenant.licenseCode}"  />
				</td>
			</tr>
  			<tr>
  				<th>
  					图片上传：
  				</th>
  				<td>
  				    <table border="1">
  				     <tr>
  				       <td>logo</td>
  				       <td>邀请函图片</td>
  				       <td>邀请朋友成为店长</td>
  				       <td>邀请函列表图片</td>
  				     </tr>
  				     <tr>
  				       <td>
  					        [#if tenant.logo??]
  						    <img  src="${tenant.logo}" width="160px"/>
  					        [/#if]
  				       </td>
  				       <td>
  					        [#if tenant.invationImage??]
  						       <img  src="${tenant.invationImage}" width="160px"/>
  					        [/#if]
  				       </td>
  				       <td>
  					         [#if tenant.openShopImage??]
  						        <img  src="${tenant.openShopImage}" width="160px"/>
  					         [/#if]
  				       </td>
  				       <td>
  					         [#if tenant.shareImage??]
  						        <img  src="${tenant.shareImage}" width="160px"/>
  					         [/#if]
  				       </td>
  				     </tr>
  				     <tr>
  				       <td>
  				         <span class="fieldSet">
  					      
  					         <div>
  					         <input type="hidden" name="logoId" value="${tenant.logoId}" />
  					         <input type="hidden" name="logo" value="${tenant.logo}" />
  				            <input type="file" id="file" name="file" value="${tenant.logo}"/><p><font color="red">上传规格：150px*150px 文件格式:*.png</font></p>
  					       </div>
  					     </span>
                       </td>
  				       <td>
  				         <span class="fieldSet">
  					
  					<div>
  					<input type="hidden" name="invationImageId" value="${tenant.invationImageId}" />
  				     <input type="hidden" name="invationImage" value="${tenant.invationImage}" />
  				    <input type="file" id="imageFile" name="imageFile" value="${tenant.invationImage}"/><p><font color="red">上传规格：640px*1136px  文件格式:*.png</font></p>
  				    </div>
  					</span>
  				       </td>
  				       <td>
  				     <span class="fieldSet">
  					<div>
  					<input type="hidden" name="openShopImageId" value="${tenant.openShopImageId}" />
  				     <input type="hidden" name="openShopImage" value="${tenant.openShopImage}" />
  				    <input type="file" id="openShopFile" name="openShopFile" value="${tenant.openShopImage}"/><p><font color="red">上传规格：1080px*1037px  文件格式:*.png</font></p>
  				    </div>
  					</span></td>
  					 <td>
  				     <span class="fieldSet">
  					<div>
  				     <input type="hidden" name="shareImage" value="${tenant.shareImage}" />
  				    <input type="file" id="shareImageFile" name="shareImageFile" value="${tenant.shareImage}"/><p><font color="red">上传规格：1080px*1037px 文件格式:*.png</font></p>
  				    </div>
  					</span></td>
  				     </tr>
  				    </table>
  				</td>
  			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>地址: 
				</th> 
				<td>
			   	<input type="text" name="address" class="text"  value="${(tenant.address)!}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>联系人: 
				</th>
				<td>
					<input type="text" name="linkman" class="text" value="${(tenant.linkman)!}"/>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>联系电话: 
				</th>
				<td>
					<input type="text" name="telephone" class="text" value="${(tenant.telephone)!}"/>
				</td>
			</tr>
		 	<tr>
				<th>
					<span class="requiredField">*</span>状态:
				</th>
				<td>
						<select name="status">
							<option value="">--请选择--</option>
							<option value="wait" [#if ("wait" == (tenant.status)!)] selected[/#if] >申请中</option>
							<option value="success" [#if ("success" == (tenant.status)!)] selected[/#if] >已开通</option>
							<option value="fail" [#if ("fail" == (tenant.status)!)] selected[/#if] >暂停</option>
							<option value="none" [#if ("none" == (tenant.status)!)] selected[/#if] >未关闭</option>
						</select>
				</td>
			</tr>
		 	<tr>
				<th>
					${message("Article.tags")}:
				</th>
				<td>
					[#list tags as tag]
						<label>
							<input type="checkbox" name="tagIds" value="${tag.id}"[#if tenant.tags?seq_contains(tag)] checked="checked"[/#if] />${tag.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
				<th>
					企业简介:
				</th>
				<td>
					<textarea id="editor" name="introduction" class="editor">${tenant.introduction?html}</textarea>
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
		   </td>
		  </tr>
		</table>
		</form>
		<form id="inputFormSell" action="sellSave.jhtml" method="post" enctype="multipart/form-data">
		<table class="input tabContent">
		<tr>
		   <td>
		     <table class="input">
			   <tr>
				<th>
					<span class="requiredField">*</span>交易累计金额: 
				</th>
				<td>
				   <input type="hidden" name="id" value="${sell.id}" />
					<input type="text" name="tradeNum" class="text" value="${sell.tradeNum}"  />（元）<p><font color="red">说明：会员成为店长必须达到企业设定的成功交易累计金额，会员主动申请，由企业审核通过；或由其他店长主动邀请。<font></p>
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
		   </td>
		  </tr>
		</table>
		</form>
		<form id="inputFormRenovation" name="from2"action="renovationSave.jhtml" method="post" enctype="multipart/form-data">
		<table class="input tabContent">
		 <tr>
		   <td>
		   [#if renovationList!=null]
		   [#list  renovationList as renovation]
		   [#if renovation.bannerNum=="1"]
		   <input type="hidden" name="id" value="${renovation.id}" />
		   <table class="input">
		      <tr>
		        <th>
		        </th>
		        <td> 
		           <font size="3px"><strong>1</strong></font>
		        </td>
		      </tr>
		      <tr>
				<th>
					跳转方式: 
				</th>
				<td>
					<select name="skipType" style="width:100px" onchange="disPlay()">
							<option value="">--请选择--</option>
							<option value="0"  [#if ("0" == (renovation.skipType)!)] selected[/#if]>商品详情</option>
							<option value="1"  [#if ("1" == (renovation.skipType)!)] selected[/#if]>外部链接</option>
					</select>
				</td>
			  </tr>
		      <tr>
				<th>
					商品: 
				</th>
				<td>
				[#if renovation.product!=null]
					<input type="hidden" id="productId"  name="productId" value="${renovation.product.id}"/>
					<input type="text" id="productIdName" name="productIdName" class="text" value="${renovation.product.name}" onclick="proudt('0')" [#if ("1" == (renovation.skipType)!)] disabled[/#if]/>
				[#else]
				    <input type="hidden" id="productId" name="productId" value=""/>
				    <input type="text"id="productIdName" name="productIdName" class="text" value="" onclick="proudt('0')" [#if ("1" == (renovation.skipType)!)] disabled[/#if] />
				[/#if]
				    <input type="button" class="button" onclick="clean('0')" value="清除" />
				</td>
			  </tr>
			  <tr>
  				<th>
  					图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  					<div>
  					[#if renovation.picId??]
  						<img  src="${renovation.picId}" width="230px"/>
  					[/#if]
  					</div>
  					<div>
  					    <input type="hidden" id="image" name="image" value="${renovation.picId}"/>
  	                    <input type="file"  id="imageFile" name="productImages[0].file"  value=""/><font color="red">上传规格：640px*200px 文件格式:*.png</font>
  				    </div>
  					</span>
  				</td>
  			</tr>
  			 
			  <tr>
				<th>
					跳转链接: 
				</th>
				<td>
					<input type="text" name="url" class="text" value="${renovation.url}" [#if ("0" == (renovation.skipType)!)] disabled[/#if] [#if renovation.skipType==null ] disabled[/#if]/>
				</td>
			  </tr>
		     </table>
		     [/#if]
		     [/#list]
		     [#list  renovationList as renovation]
		     [#if renovation.bannerNum=="2"]
		    <input type="hidden" name="idT" value="${renovation.id}" />
		    <table class="input">
		      <tr>
		        <th>
		        </th>
		        <td> 
		           <font size="3px"><strong>2</strong></font>
		        </td>
		      </tr>
		      <tr>
				<th>
					跳转方式: 
				</th>
				<td>
					<select name="skipTypeT" style="width:100px" onchange="disPlayT()">
							<option value="">--请选择--</option>
							<option value="0"  [#if ("0" == (renovation.skipType)!)] selected[/#if]>商品详情</option>
							<option value="1"  [#if ("1" == (renovation.skipType)!)] selected[/#if]>外部链接</option>
					</select>
				</td>
			  </tr>
		      <tr>
				<th>
					商品: 
				</th>
				<td>
			    [#if renovation.product!=null]
					<input type="hidden"id="productIdT" name="productIdT" value="${renovation.product.id}"/>
					<input type="text" id="productIdNameT" name="productIdNameT" class="text" value="${renovation.product.name}" onclick="proudt('1')"  [#if ("1" == (renovation.skipType)!)] disabled[/#if]/>
				[#else]
				    <input type="hidden"id="productIdT"  name="productIdT" value=""/>
				    <input type="text" id="productIdNameT" name="productIdNameT" class="text" value="" onclick="proudt('1')" [#if ("1" == (renovation.skipType)!)] disabled[/#if] />
				[/#if]
				<input type="button" class="button" onclick="clean('1')" value="清除" />
				</td>
			  </tr>
			  <tr>
  				<th>
  					图片:
  				</th>
  				<td>
  				<span class="fieldSet">
  					<div>
  					[#if renovation.picId??]
  						<img  src="${renovation.picId}" width="230px"/>
  					[/#if]
  					</div>
  					<div>
  					    <input type="hidden" id="imageT" name="imageT" value="${renovation.picId}"/>
  	                    <input type="file"  name="productImages[1].file" value=""/><font color="red">上传规格：640px*200px 文件格式:*.png</font>
  				    </div>
  					</span>
  				</td>
  			</tr>
  			 
			  <tr>
				<th>
					跳转链接: 
				</th>
				<td>
					<input type="text" name="urlT" class="text" value="${renovation.url}" [#if ("0" == (renovation.skipType)!)] disabled[/#if] [#if renovation.skipType==null ] disabled[/#if]/>
				</td>
			  </tr>
		     </table>
		      [/#if]
		      [/#list]
		     [#list  renovationList as renovation]
		     [#if renovation.bannerNum=="3"]
		     <input type="hidden"  name="idTH" value="${renovation.id}" />
		    <table class="input">
		      <tr>
		        <th>
		        </th>
		        <td> 
		           <font size="3px"><strong>3</strong></font>
		        </td>
		      </tr>
		      <tr>
				<th>
					跳转方式: 
				</th>
				<td>
					<select name="skipTypeTH" style="width:100px" onchange="disPlayTH()">
							<option value="">--请选择--</option>
							<option value="0"  [#if ("0" == (renovation.skipType)!)] selected[/#if]>商品详情</option>
							<option value="1"  [#if ("1" == (renovation.skipType)!)] selected[/#if]>外部链接</option>
					</select>
				</td>
			  </tr>
		      <tr>
				<th>
					商品: 
				</th>
				<td>
				[#if renovation.product!=null]
					<input type="hidden" id="productIdTH" name="productIdTH" value="${renovation.product.id}"/>
					<input type="text" id="productIdNameTH" name="productIdNameTH" class="text" value="${renovation.product.name}" onclick="proudt('2')"  [#if ("1" == (renovation.skipType)!)] disabled[/#if]/>
				[#else]
				    <input type="hidden" name="productIdTH" value=""/>
				    <input type="text" name="productIdNameTH" class="text" value="" onclick="proudt('2')" [#if ("1" == (renovation.skipType)!)] disabled[/#if] />
				[/#if]	
				<input type="button" class="button" onclick="clean('2')" value="清除" />
				</td>
			  </tr>
			  <tr>
  				<th>
  					图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  					<div>
  					[#if renovation.picId??]
  						<img  src="${renovation.picId}" width="230px"/>
  					[/#if]
  					</div>
  					<div>
  					     <input type="hidden" id="imageTH" name="imageTH" value="${renovation.picId}"/>
  	                    <input type="file"  name="productImages[2].file" value=""/><font color="red">上传规格：640px*200px 文件格式:*.png</font>
  				    </div>
  					</span>
  				</td>
  			</tr>
  			 
			  <tr>
				<th>
					跳转链接: 
				</th>
				<td>
					<input type="text" name="urlTH" class="text" value="${renovation.url}" [#if ("0" == (renovation.skipType)!)] disabled[/#if] [#if renovation.skipType==null ] disabled[/#if]/>
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
		   </td>
		  </tr>
		</table>
		[/#if]
		[/#list]
		[/#if]
		
		
		
		
		   [#if renovationList==null]
		   <table class="input">
		      <tr>
		        <th>
		        </th>
		        <td> 
		           <font size="3px"><strong>1</strong></font>
		        </td>
		      </tr>
		      <tr>
				<th>
					跳转方式: 
				</th>
				<td>
					<select name="skipType" style="width:100px" onchange="disPlay()">
							<option value="">--请选择--</option>
							<option value="0" >商品详情</option>
							<option value="1" >外部链接</option>
					</select>
				</td>
			  </tr>
		      <tr>
				<th>
					商品: 
				</th>
				<td>
				<input type="hidden" id="productId" name="productId" value=""/>
					<input type="text" id="productIdName" name="productIdName" class="text" value="" onclick="proudt('0')"  [#if ("1" == (renovation.skipType)!)] disabled[/#if]/><input type="button" class="button" onclick="clean('0')" value="清除" />
				</td>
			  </tr>
			  <tr>
  				<th>
  					图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file"  name="productImages[0].file" value=""/><font color="red">上传规格：640px*200px 文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
  			 
			  <tr>
				<th>
					跳转链接: 
				</th>
				<td>
					<input type="text" name="url" class="text" value="" disabled='disabled'/>
				</td>
			  </tr>
		     </table>
		    <table class="input">
		      <tr>
		        <th>
		        </th>
		        <td> 
		           <font size="3px"><strong>2</strong></font>
		        </td>
		      </tr>
		      <tr>
				<th>
					跳转方式: 
				</th>
				<td>
					<select name="skipTypeT" style="width:100px" onchange="disPlayT()">
							<option value="">--请选择--</option>
							<option value="0"  >商品详情</option>
							<option value="1"  >外部链接</option>
					</select>
				</td>
			  </tr>
		      <tr>
				<th>
					商品: 
				</th>
				<td>
					<input type="hidden" id="productIdT" name="productIdT" value=""/>
					<input type="text" id="productIdNameT" name="productIdNameT" class="text" value="" onclick="proudt('1')"  disabled='disabled'/><input type="button" class="button" onclick="clean('1')" value="清除" />
				</td>
			  </tr>
			  <tr>
  				<th>
  					图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file"  name="productImages[1].file" value=""/><font color="red">上传规格：640px*200px  文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
  			 
			  <tr>
				<th>
					跳转链接: 
				</th>
				<td>
					<input type="text" name="urlT" class="text" value="" disabled='disabled'/>
				</td>
			  </tr>
		     </table>
		    <table class="input">
		      <tr>
		        <th>
		        </th>
		        <td> 
		           <font size="3px"><strong>3</strong></font>
		        </td>
		      </tr>
		      <tr>
				<th>
					跳转方式: 
				</th>
				<td>
					<select name="skipTypeTH" style="width:100px" onchange="disPlayTH()">
							<option value="">--请选择--</option>
							<option value="0"  >商品详情</option>
							<option value="1"  >外部链接</option>
					</select>
				</td>
			  </tr>
		      <tr>
				<th>
					商品: 
				</th>
				<td>
					<input type="hidden" id="productIdTH" name="productIdTH" value=""/>
					<input type="text" id="productIdNameTH" name="productIdNameTH" class="text" value="" onclick="proudt('2')"  disabled='disabled'/><input type="button" class="button" onclick="clean('2')" value="清除" />
				</td>
			  </tr>
			  <tr>
  				<th>
  					图片:
  				</th>
  				<td>
  					<span class="fieldSet">
  				    <input type="file" name="productImages[2].file" value=""/><font color="red">上传规格：640px*200px 文件格式:*.png</font>
  					</span>
  				</td>
  			</tr>
  			 
			  <tr>
				<th>
					跳转链接: 
				</th>
				<td>
					<input type="text" name="urlTH" class="text" value="" disabled='disabled'/>
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
		   </td>
		  </tr>
		</table>
		[/#if]
		</form>
		<form id="inputFormBonus" action="bonusSave.jhtml" method="post" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${bonusSet.id}" />
		<table class="input tabContent">
		<tr>
		   <td>
		     <table class="input">
			   <tr style="display:none;">
				<th>
					<span class="requiredField">*</span>本店销售额奖金比例: 
				</th>
				<td>
					<input type="text" name="tenantSellBonusRate" class="text" value="0" style="width:60px" />&nbsp;<font size="2px"><strong>：1</strong></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font  color="red">如设为0则该部分奖金不发放</font>
				</td>
			  </tr>
			   <tr>
				<th>
					<span class="requiredField">*</span>邀请奖金比例: 
				</th>
				<td>
					<input type="text" name="relativeSellBonusRate" class="text" value="${bonusSet.relativeSellBonusRate}"  style="width:60px"/>&nbsp;<font size="2px"><strong>%</strong></font>
					<p><font  color="red">店长会员每邀一位朋友成为店长， 则可从邀请的每位店长分享所产生的交易额获取邀请奖金。 </font></p>
					<p><font  color="red">邀请奖金次月1号进行上月邀请奖金结算，次月15号店长会员可进行邀请奖金提现申请，申请后由企业财务审核通过后自动发放到店长会员绑定的支付宝账账户。 </font></p>
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
		   </td>
		  </tr>
		</table>
	</form>
</body>
</html>

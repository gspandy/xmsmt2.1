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
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;内购券设置
	</div>
		<form id="inputForm" action="saveTenantTicket.jhtml" method="post" enctype="multipart/form-data">
		   <input type="hidden" name="id" value="${platParam.id}"/>
		   <table class="input">
			<tr>
				<th>
					<h3>有效内购券图片:</h3>
				</th>
				<td>
				 <span class="fieldSet">
			     	<table class="input">
			     	<tr>
			     	  <th>
					     已上传
				      </th>
			     	  <td>
  					      <div>
  					        	[#if platParam.effectiveImage??]
  						          <img  src="${platParam.effectiveImage}" width="230px"/>
  					            [/#if]
  					      </div>
  					    </td>
  					  </tr>
  					  <tr>
  					    <th>
				                    重新上传
				        </th>
				        <td>
  					      <div>
  					            <input type="hidden"  name="effectiveImage" value="${platParam.effectiveImage}"/>
  	                            <input type="file"  name="file" value="${platParam.effectiveImage}"/><font color="red">上传规格：150px*150px   文件格式:*.png</font>
  				          </div>
  					    
  					    </td>
  					  </tr>
  					</table>
  					</span>
				</td>
			</tr>
			<tr>
				<th>
					<h3>无效内购券图片：</h3>
				</th>
				<td>
				      <span class="fieldSet">
				      	<table class="input">
			     	<tr>
			     	  <th>
					     已上传
				      </th>
			     	  <td>
  					<div>
  					[#if platParam.expiryImage??]
  						    <img  src="${platParam.expiryImage}" width="230px"/>
  					  [/#if]
  					</div>
  					 </td>
  					  </tr>
  					  <tr>
  					    <th>
				                    重新上传
				        </th>
				        <td>
  					<div>
  					    <input type="hidden"  name="expiryImage" value="${platParam.expiryImage}"/>
  	                    <input type="file" name="imageFile" value=""/><font color="red">上传规格：150px*150px   文件格式:*.png</font>
  				    </div>
  				       </td>
  					  </tr>
  					</table>
  					</span>
				</td>
			</tr>
			<tr>
				<th>
					<h3>内购券尾图图片：</h3>
				</th>
				<td>
				      <span class="fieldSet">
				      <table class="input">
			     	<tr>
			     	  <th>
					     已上传
				      </th>
			     	  <td>
  					<div>
  					[#if platParam.tailImage??]
  						    <img  src="${platParam.tailImage}" width="230px"/>
  					  [/#if]
  					</div>
  					 </td>
  					  </tr>
  					  <tr>
  					    <th>
				                    重新上传
				        </th>
				        <td>
  					<div>
  					    <input type="hidden"  name="tailImage" value="${platParam.tailImage}"/>
  	                    <input type="file" name="tailImageFile" value=""/><font color="red">上传规格：150px*150px   文件格式:*.png</font>
  				    </div>
  				      </td>
  					  </tr>
  					</table>
  					</span>
				</td>
			</tr>
			<tr>
				<th>
					<h3>内购券失效尾图图片：</h3>
				</th>
				<td>
				      <span class="fieldSet">
				      	<table class="input">
			     	<tr>
			     	  <th>
					     已上传
				      </th>
			     	  <td>
  					<div>
  					[#if platParam.tailExpiredImage??]
  						    <img  src="${platParam.tailExpiredImage}" width="230px"/>
  					  [/#if]
  					</div>
  					</td>
  					  </tr>
  					  <tr>
  					    <th>
				                    重新上传
				        </th>
				        <td>
  					<div>
  					    <input type="hidden"  name="tailExpiredImage" value="${platParam.tailExpiredImage}"/>
  	                    <input type="file" name="tailExpiredImageFile" value=""/><font color="red">上传规格：150px*150px   文件格式:*.png</font>
  				    </div>
  				       </td>
  					  </tr>
  					</table>
  					</span>
				</td>
			</tr>
			<tr>
				<th>
					  内购券说明：
				</th>
				<td>
				     <textarea rows="5" cols="30" class="text"  name="content">[#if platParam.content??] ${platParam.content} [/#if]</textarea>
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
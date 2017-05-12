<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>${message("admin.article.edit")} - Powered By rsico</title>
<meta name="author" content="rsico Team" />
<meta name="copyright" content="rsico" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/editor/kindeditor.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript">
$().ready(function() {
    [@flash_message /]
	var $shareForm = $("#shareForm");
	var timeout;
	
	// 表单验证
	$shareForm.validate({
		rules: {
			"ticketShareTitle": {
 				required: true
 			},
 			"ticketShare": {
 				required: true
 			},
 			"invitationShareTitle": {
 				required: true
 			},
 			"invitaionShare": {
 				required: true
 			}
 		}
 	});
});
</script>
</head>
<body>
	<div class="path">
		<a href="${base}/admin/common/index.jhtml">${message("admin.path.index")}</a> &raquo;企业分享页管理
	</div>
	<ul id="tab" class="tab">
			<li>
				<input type="button" value="企业分享图片" />
			</li>
			<li>
				<input type="button" value="企业分享文字" />
			</li>
	</ul>
	<form id="inputForm" action="shareSave.jhtml" method="post"  enctype="multipart/form-data">
	<table class="input tabContent">
		  <tr>
		   <td>
		     <table class="input">
		    <tr>
		      <th></th>
		      <td>&nbsp;</td>
		    </tr>
  			<tr>
  				<th>
  					图片上传：
  				</th>
  				<td>
  				   <table border="1">
  				     <tr>
  				       <td>内购券分享图片</td>
  				       <td>企业邀请图片</td>
  				       <td>店长邀请图片</td>
  				     </tr>
  				     <tr>
  				       <td>
  				          <div>
  					        [#if ticketShare.smallUrl??]
  					         <div style="width:100px";>
  						       <img  src="${ticketShare.smallUrl}" width="160px"/>
  						     </div>
  						     [#else]
  						     <div style="width:100px";>
  						          &nbsp;&nbsp;&nbsp;
  						     </div>
  					        [/#if]
  					      </div>
  				       </td>
  				       <td>
  				          <div>
  					         [#if tenantInvitation.smallUrl??]
  				            	<div style="width:100px;">
  					                <div>
  						              <img  src="${tenantInvitation.smallUrl}" width="160px"/>
  					                </div>
  					            </div>
  					            [#else]
  						     <div style="width:100px";>
  						       &nbsp;&nbsp;&nbsp;
  						     </div>
  					        [/#if]
  					     </div>
  				       </td>
  				       <td>
  				          <div>
  					        [#if shopkeepInvitation.smallUrl??]
  					         <div style="width:100px";>
  						       <img  src="${shopkeepInvitation.smallUrl}" width="160px"/>
  						     </div>
  						     [#else]
  						     <div style="width:100px";>
  						       &nbsp;&nbsp;&nbsp;
  						     </div>
  					        [/#if]
  					      </div>
  				       </td>
  				     </tr>
  				     <tr>
  				       
  				       <td>
  				         <span class="fieldSet">
  					       <div>
  					         <input type="hidden" name="ticketShareId" value="${ticketShare.id}" />
  				             <input type="file" id="ticketShareFile" name="ticketShareFile" value="${ticketShare.medium_url}"/><p><font color="red">上传规格：540px*518px  文件格式:*.png</font></p>
  				          </div>
  					     </span>
  				       </td>
  				      <td>
  				        <span class="fieldSet">
  					     <div>
  					      <input type="hidden" name="tenantInvitationId" value="${tenantInvitation.id}" />

  				          <input type="file" id="tenantInvitationFile" name="tenantInvitationFile" value="${tenantInvitation.medium_url}"/><p><font color="red">上传规格：540px*632px  文件格式:*.png</font></p>
  				         </div>
  					   </span>
  					  </td>
  				      <td>
  				       <span class="fieldSet">
  					    <div>
  				         <input type="hidden" name="shopkeepInvitationId" value="${shopkeepInvitation.id}" />
  				         <input type="file" id="shopkeepInvitationFile" name="shopkeepInvitationFile" value="${shopkeepInvitation.medium_url}"/><p><font color="red">上传规格：540px*632px  文件格式:*.png</font></p>
  				        </div>
  					   </span>
  					  </td>
  				     </tr>
  				    </table>
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
   <form id="shareForm" action="shareSetSave.jhtml" method="post"  enctype="multipart/form-data">
   	<table class="input tabContent">
		  <tr>
		   <td>
		       <table class="input">
		         <tr>
		           <th>
		                       券分享:
		           </th>
		           <td>
		             <table class="input">
		              <tr>
		                <th>
		                                      标题：
		                </th>
		                <td>
		                   [#list shareList as share]
		                     [#if share.type=="ticketShareTitle"]
		                       <input type="hidden" name="ticketShareTitleId" value="${share.id}" />
		                       <input type="text" maxlength="150" name="ticketShareTitle" class="text" value="${share.content}" />
		                     [/#if]
		                   [/#list]
		                </td>
		               </tr>
		               <tr>
		                <th>
		                                     内容：
		                </th>
		                <td>
		                  [#list shareList as share]
		                   [#if share.type=="ticketShare"]
		                        <input type="hidden" name="ticketShareId" value="${share.id}" />
		                        <textarea maxlength="150" rows="5" cols="30" class="text"  name="ticketShare">${share.content}</textarea>  
		                   [/#if]
		                  [/#list]
		                </td>
		               </tr>
		             </table>
		           </td>
		         </tr>
		          <tr>
		           <th>
		                      邀请分享:
		           </th>
		           <td>
		             <table class="input">
		              <tr>
		                <th>
		                                      标题：
		                </th>
		                <td>
		                  [#list shareList as share]
		                   [#if share.type=="invitationShareTitle"]
		                     <input type="hidden" name="invitationShareTitleId" value="${share.id}" />
		                     <input type="text" maxlength="150" name="invitationShareTitle" class="text" value="${share.content}" />
		                   [/#if]
		                  [/#list]
		                </td>
		               </tr>
		               <tr>
		                <th>
		                                     内容：
		                </th>
		                <td>
		                 [#list shareList as share]
		                   [#if share.type=="invitaionShare"]
		                      <input type="hidden" name="invitaionShareId" value="${share.id}" />		                      
		                      <textarea maxlength="150" rows="5" cols="30" class="text"  name="invitaionShare">${share.content}</textarea>
		                   [/#if]
		                 [/#list]
		                </td>
		               </tr>
		             </table>
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

<div class="w_footer" style="width:100%;float:left">
	<div class="login_link">
		<h1>友情链接</h1>
		[@friend_link_list]
		     [#list friendLinks as friendLink]
		    	 <a href="${friendLink.url}" target="_blank"><span>${friendLink.name}</span></a> 
		     [/#list]
	    [/@friend_link_list]
	</div>
	<div class="login_copright">
		<p>Copyright© 2010-2014 ，${setting.siteName}版权所有 ${setting.certtext}</p>
	</div>
</div>
<link href="${base}/resources/shop/css/w_shopclass.css" rel="stylesheet" type="text/css">
<link href="${base}/resources/shop/css/shop-common.css" rel="stylesheet" type="text/css" />
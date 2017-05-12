<script type="text/javascript">
$().ready(function() {
	var $headerRegister = $("#headerRegister");
	var $headerUsername = $("#headerUsername");
	var $headerLogout = $("#headerLogout");
	var username = getCookie("username");
	if (username != null) {
		$headerUsername.text(username);
		$headerLogout.show();
		$headerRegister.hide();
	} else {
		$headerRegister.show();
		$headerLogout.hide();
	}
});
</script>
<!--登录条start-->
[#assign propertyBase="http://app.pmsaas.net" /]
<div class="s_pagestop">
	<div class="s_pcontent">
		<ul>
			<li class="s_phone"><span></span><a href="#">手机版</a>
				<div class="s_phonedown">
					<img src="${base}/resources/shop/images/s_phone.png">扫描下载手机app
				</div>
			</li>
			<li class="s_login" id="headerRegister" >
				你好，欢迎来到${setting.siteName}，<a href="${base}">登录</a>|<a href="${base}/register.jhtml">注册</a>
			</li>
			<li class="s_login" id="headerLogout">
				你好[<span id="headerUsername"></span>]，欢迎来到${setting.siteName}，<A href="${base}/logout.jhtml">[安全退出]</A>|<A title="进入会员中心" href="${base}/member/index.jhtml">[进入会员中心]</A>
			</li>
			<li class="s_about"><a href="${base}/member/index.jhtml">我的速卖通</a>
				<div class="s_litwo">
					<a href="${base}/member/index.jhtml"><span></span>会员中心</a>
				</div>
			</li>
			<li class="s_server"><a href="${base}/article/list/6.jhtml">服务中心</a>
				<div class="s_litwo">
					[@article_category_children_list articleCategoryId=6 count=3]
						[#list articleCategories as articleCategory]
							<a href="${base}/article/list/${articleCategory.id}.jhtml"><span></span>${articleCategory.name}</a>
						[/#list]
					[/@article_category_children_list]
				</div>
			</li>
			<li class="s_map"><a href="${base}/article/list/7.jhtml">网站导航</a>
				<div class="s_litwo">
					[@article_category_children_list articleCategoryId=7 count=3]
						[#list articleCategories as articleCategory]
							<a href="${base}/article/list/${articleCategory.id}.jhtml"><span></span>${articleCategory.name}</a>
						[/#list]
					[/@article_category_children_list]
				</div>
			</li>
		</ul>
	</div>
</div>

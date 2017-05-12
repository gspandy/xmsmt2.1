<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />		
		<meta name="full-screen" content="yes">
		<meta name="x5-fullscreen" content="true">
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<link rel="stylesheet" href="${base}/static/share/css/zhibang.css" />
		<script src="${base}/static/share/js/jquery-1.10.2.min.js"></script>
		<script src="${base}/static/layer/layer.js"></script>
		<title></title>
	</head>
	
	<body>
		<header class="header">
		  <div class="header-l"><a href="#" class="icon-Left-arrow"></a></div>
		  <div class="header-c">发福利了</div>
		  <div class="header-r"><a href="#"></a> </div>
		</header>
		
		<div class="container top-mg50">
			<!--广告图-->
			<div class="banner">
				<img src="${imageUrl}"/>
				<div class="banner-Text">
					<dl><dt><s><img src="${headImage}"/></s></dt><dd><h1>${shopKeeperName}</h1><h2>给你分享了一张限量${tenantName}内购券，此券一般人我不给他！</h2></dd></dl>
				</div>
			</div>
			<!---->
			<div class="voucher-body">
				<div class="voucher-list">
					<ul>
						<li class="pop-but">
							<#if ticketStatus == true>
								<h1><img src="${base}/static/share/img/gubianA01.png"/></h1>
								<h2>
									<dl>
										<dt><img src="${ticketLogo}"/></dt>
										<dd>
											<h1>${tenantName}专享内购券</h1>
											<h2>分享人：${shopKeeperName}</h2>
										</dd>
									</dl>
									<#if remmaingDays gte 0>
										<em><s>剩${remmaingDays}天</s>
											<i><a>立即领取></a></i>
										</em>
									<#else>
										<em><s></s>
											<i><a href="#">已过期></a></i>
										</em>
									</#if>
								</h2>
								<h3><img src="${base}/static/share/img/gubianB01.png"/></h3>
							<#else>
								<h1><img src="${base}/static/share/img/gubianA02.png"/></h1>
								<h2><dl style="background-color: #a8a8a8;"><dt><img src="${ticketLogo}"/></dt><dd><h1>${tenantName}专享内购券</h1><h2>分享人：${shopKeeperName}</h2></dd></dl><em><s></s><i><a href="#">已领取></a></i></em></h2>
								<h3><img src="${base}/static/share/img/gubianB02.png"/></h3>
							</#if>
							
						</li>
					</ul>
				</div>
				
			</div>
			
				<div class="Shop-body" style=" margin: 0px 10px 20px 10px; padding: 0px;">
				<div class="shop-line1">					
					<h2><a href="${base}/share/product.jhtml?tenantId=${tenantId}"><span></span><em>你知道内购券购买${tenantName}产品有多划算吗？ </em> </a></h2>					
				</div>
			</div>
		</div>
		
		
		<#if ticketStatus == false>
			<!--弹出窗口-->
			
			<div class="pop-bg">
				<div class="pop-box">
					<div id="pop-con" class="pop-con">
						<h4><span class="pop-icon-Right"></span></h4>
						<h1>${tenantName}内购券已领取赶紧去抢 购内购商品吧！</h1>					
						<h5 style="display: block;"><em>点稍后下载发下载链接地址到手机</em></h5>
						<h6><s><a class="but-link02 pop-close">稍后下载</a></s><i><a class="but-link01 mobile-but" href="${base}/share/download.mobile">下载APP</a></i></h6>
					</div>				
				</div>			
			</div>
	
			<script>
			   //弹出窗口
				$(".pop-but").click(function(){
					
				})
				
				$(".pop-box").show();
				$(".pop-bg").show();
				
				//关闭窗口
				$(".pop-close").click(function() {
					popclose();
				})
				
				function popclose(){
					$(".pop-box").hide();
					$(".pop-bg").hide();				
				}
	
				$(".mobile-but").click(function() {
					var mbinput=$("#mobile-input");
					var tipinfo=$(".pop-con h5");
					<!-- location.href="http://m.z8ls.com" -->
					popclose();
				})
			</script>
		<#else>
			<!--弹出窗口-->
			<div class="pop-bg">
				<div class="pop-box">
					<div id="pop-con" class="pop-con">
						<h1>输入手机号码领取内购券！</h1>
						<h2>
							<form id="form">
								<input type="text" name="tel" placeholder="手机号码" id="mobile-input"/>
								<input type="hidden" name="ticketId" value="${ticketId}"/>
							</form>
						</h2>
						<h5><em></em></h5>
						<h6><s><a class="but-link02 pop-close">算了</a></s><i><a class="but-link01 mobile-but">立即领取</a></i></h6>
					</div>				
				</div>			
			</div>
			
			<script>
			   //弹出窗口
				$(".pop-but").click(function(){
					$(".pop-box").show();
					$(".pop-bg").show();
				})
				
				//关闭窗口
				$(".pop-close").click(function() {
					popclose();
				})
				
				function popclose(){
					$(".pop-box").hide();
					$(".pop-bg").hide();
					popReset()
				}
			
				
				//点击确定按钮对手机号进行验证
				$(".mobile-but").click(function() {
					var mbinput=$("#mobile-input");
					var tipinfo=$(".pop-con h5");
					//判断是否为空
					if(mbinput.val()==""){
						tipinfo.show();
						tipinfo.find("em").html("手机号不能为空！")
						mbinput.focus();
						return false;
					}
					
					//验证手机号码格式
					if(!mbinput.val().match(/^1[3|4|7|5|8][0-9]\d{4,8}$/)){
						tipinfo.show();
						tipinfo.find("em").html("手机号码格式不正确！请重新输入！")
						mbinput.focus();
						return false;
					}else{
																			
						tipinfo.hide();
						ajaxget();
						popReset();
					}
				})
				
				function ajaxget(){
					var mbinput = $("#form").serialize();
					$.ajax({
						type:"POST",
						data: mbinput,
						url:"${base}/ticket/receiveTicket.jhtml",					
						dataType: 'json',
						success: function(data){
							if(data.success == "true") {
								//TODO
								//领取成功
								location.reload();
															$("#pop-con").empty();
							var popCon = '<h4><span class="pop-icon-Right"></span></h4>' + 
									'<h1>太棒了！下载APP 马上购物去啦！！！</h1>' +		
									'<h5><em>太棒了！下载APP 马上购物去啦！！！</em></h5>' + 
									'<h6><s><a class="but-link02 pop-close">稍后下载</a></s><i><a class="but-link01 mobile-but" href="${base}/share/download.jhtml">下载APP</a></i></h6>';
							$("#pop-con").append(popCon);
							} else {
								//TODO
								//领取失败
								layer.msg(data.message);
							$("#pop-con").empty();
							var popCon = '<h4><span class="pop-icon-Right"></span></h4>' + 
									'<h1>太棒了！下载APP 马上购物去啦！！！</h1>' +		
									'<h5><em>太棒了！下载APP 马上购物去啦！！！</em></h5>' + 
									'<h6><s><a class="but-link02 pop-close">稍后下载</a></s><i><a class="but-link01 mobile-but" href="${base}/share/download.jhtml">下载APP</a></i></h6>';
							$("#pop-con").append(popCon);
								
							}
						},
						error:function(xhq){
							
						}
					});
				}
				
				
				//input 获得焦点时，隐藏浮动头部，对话框移动到top10px
				
				$("#mobile-input").focus(function(){
					$(".header").hide();
					$(".container").removeClass("top-mg50");
					$(".pop-box").addClass("pop-top10");
				})
				//input 获得焦点时,复原
				function popReset(){
					$(".header").show();
					$(".container").addClass("top-mg50");
					$(".pop-box").removeClass("pop-top10");
				}
			</script>
				
		</#if>
	</body>
</html>
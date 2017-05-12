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
		  <div class="header-c">邀您开通店长</div>
		  <div class="header-r"><a href="#"></a> </div>
		</header>
		
		<div class="container top-mg50">
			
			<!--广告图-->
			<div class="banner">
				<img src="${inviationImage }"/>
				<div class="banner-Text">
					<dl><dt><s><img src="${tenantLogo }"/></s></dt><dd><h1>${tenantName }</h1><h2>朋友们，要感谢有我因为有我，你才有机会成为${tenantName }内购店长，动动手指月入过万！！！</h2></dd></dl>
				</div>
			</div>
			<!---->
			<div class="Shop-body">
				<div class="shop-line1">
					<h1><a class="but-link01 pop-but">立即成为店长</a></h1>
					<h2><a href="#"><span></span><em>成为店长是有多赚钱？</em> </a></h2>
				</div>
			</div>
		</div>
		
		
		<!--弹出窗口-->
		
		<div class="pop-bg">
			<div class="pop-box">
				<div id="pop-con" class="pop-con">
					
					<h1>输入手机号码立即成为店长</h1>
					<h2>
					<form id="form">
						<input type="text" name="tel" placeholder="手机号码" id="mobile-input"/>
						<input type="hidden" name="tenantId" value="${tenantId }"/>
					</form>
					</h2>
					<h5><em></em></h5>
					<h6><s><a class="but-link02 pop-close">算了</a></s><i><a class="but-link01 mobile-but" id="apply">立即开通</a></i></h6>
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
			$("#apply").click(function() {
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
				if(!mbinput.val().match(/^1[3|4|5|8][0-9]\d{4,8}$/)){
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
					data:mbinput,
					url:"${base}/invitation/tenantInvite.jhtml",					
					dataType: 'json',
					success: function(data){
						if(data.success == "true") {
							//TODO
							//邀请成功
							$("#pop-con").empty();
							var popCon = '<h4><span class="pop-icon-Right"></span></h4>' + 
									'<h1>太棒了！下载APP马上开通店长 赚钱啦！！！</h1>' +		
									'<h5><em>太棒了！下载APP马上开通店长 赚钱啦！！！</em></h5>' + 
									'<h6><s><a class="but-link02 pop-close">稍后下载</a></s><i><a class="but-link01 mobile-but" href="${base}/share/download.mobile">下载APP</a></i></h6>';
							$("#pop-con").append(popCon);
						} else {
							//TODO
							//邀请失败
							layer.msg(data.message);
														$("#pop-con").empty();
							var popCon = '<h4><span class="pop-icon-Right"></span></h4>' + 
									'<h1>太棒了！下载APP马上开通店长 赚钱啦！！！</h1>' +		
									'<h5><em>太棒了！下载APP马上开通店长 赚钱啦！！！</em></h5>' + 
									'<h6><s><a class="but-link02 pop-close">稍后下载</a></s><i><a class="but-link01 mobile-but" href="${base}/share/download.mobile">下载APP</a></i></h6>';
							$("#pop-con").append(popCon);
						}
					},
					error:function(xhq){
						alert("错误"+xhq.responseText);
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
	</body>
</html>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />		
		<meta name="full-screen" content="yes">
		<meta name="x5-fullscreen" content="true">
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		<link rel="stylesheet" href="${base}/static/share/css/swiper.min.css"/><!-- 滚动图用到的css -->
		<link rel="stylesheet" href="${base}/static/share/css/zhibang.css" />
		<script src="${base}/static/share/js/jquery-1.10.2.min.js"></script>
		<script src="${base}/static/layer/layer.js"></script>
		<title></title>
		
		
	<style>
	    .swiper-container {
	        width: 100%;
	        
	    }
	    .swiper-slide {
	        text-align: center;
	        font-size: 18px;
	        background: #fff;
	
	        /* Center slide text vertically */
	        display: -webkit-box;
	        display: -ms-flexbox;
	        display: -webkit-flex;
	        display: flex;
	        -webkit-box-pack: center;
	        -ms-flex-pack: center;
	        -webkit-justify-content: center;
	        justify-content: center;
	        -webkit-box-align: center;
	        -ms-flex-align: center;
	        -webkit-align-items: center;
	        align-items: center;
	    }
	    .swiper-slide img{ width: 100%;}
	 </style>

	</head>
	<body>
		<header class="header">
		  <div class="header-l"><a href="#" class="icon-Left-arrow"></a></div>
		  <div class="header-c">${tenantName}内购店</div>
		 <!-- <div class="header-d"><a href="#" class="icon-Left-Question">开通店长</a></div> -->
		</header>
		
		
		
		<div class="container top-mg50">
			 <!--列表 -->
			  <div class="newindex-list">
			    <ul id="content">
			     	
			    </ul>
			  </div>
			</div>
		</div>
			
		<!--弹出窗口-->
		<div class="pop-bg">
			<div class="pop-box">
				<div id="pop-con" class="pop-con">
					<h4><span class="pop-icon-Voucher"></span></h4>
					<h1>心动了吧！想购买商品需 要有内购券！</h1>					
					<h5><em></em></h5>
					<h6><s><a class="but-link02 pop-close">稍后领取</a></s><i><a class="but-link01 mobile-but">领取内购券</a></i></h6>
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
			}
		
			
			
			$(".mobile-but").click(function() {
				var mbinput=$("#mobile-input");
				var tipinfo=$(".pop-con h5");
				<!-- location.href="http://m.z8ls.com" -->
				popclose();

			})
		</script>
		
		<!-- 滚动图用到的脚本 -->
	    <script src="${base}/static/share/js/swiper.min.js"></script>	    
	    <script>
	    var swiper = new Swiper('.swiper-container', {
	        pagination: '.swiper-pagination',
	        paginationClickable: true
	    });
	    
	    var layerObj;
	    
	    $.ajax({
		    type: 'POST',
		    url: '${base}/product/productList.mobile?tenantId=${tenantId}&pageNumber=0&pageSize=10&categoryId=&categoryTenantId=',
		    beforeSend: function() {
		    	layerObj = layer.load(0, {shade: false});
		    },complete: function() {
		    	layer.close(layerObj);
		    },
		    success: function(data) {
		    	var success = data.success;
		    	if(success) {
		    		//清空内容区域
		    		$("#content").empty();
		    		//构造产品
		    		var productList = data.resultValue.productList.content;
		    		for(var i = 0; i < productList.length; i++) {
		    			var product = productList[i];
		    			
		    			var priceType = "";
		    			var priceTypeImage = "";
		    			if(product.priceType == 0) {
		    				priceType = "京东价";
		    				priceTypeImage = 'icon-jd@2x.png';
		    			} else if(product.priceType == 1) {
		    				priceType = "天猫价";
		    				priceTypeImage = 'icon-bg-07.png';
		    			} else if(product.priceType == 2){
		    				priceType = "淘宝价";
		    				priceTypeImage = 'icon-tb@2x.png';
		    			} else{
		    				priceType = "官网价";
		    				priceTypeImage = 'icon-md.png';
		    			}
		    			
		    			var content = '<li><a href="#">' + 
					        '<h1><img src="' + product.image + '"/> <!--<span>分享佣金￥' + product.rent + '</span>--> </h1>' + 
					        '<h2>' + product.name + '</h2>' + 
					        '<h3><b>￥' + product.price + '</b><span>内购价</span></h3>' + 
					        '<h4>市场价<s>￥' + product.marketPrice + '</s></h4>' + 
					        '<h5 style="background-image: url(${base}/static/share/img/' +  priceTypeImage + ');">' + priceType + '：' + product.ePrice + '</h5>' + 
						'</a></li>';
						$("#content").append(content);
		    		}
		    	}
		    } ,
		    dataType: "json"
		});
	    </script>
	</body>
</html>
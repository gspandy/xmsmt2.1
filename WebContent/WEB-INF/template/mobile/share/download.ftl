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
	</head>
	<body>		
		<div class="container">
			<div class="Download">
			<h1><img src="${base}/static/share/img/Download01.png"/></h1>
			<h2><a href="itms-services://?action=download-manifest&url=https%3A%2F%2Fwww.pgyer.com%2Fapiv1%2Fapp%2Fplist%3FaId%3Dc53c0cca7e37c1aa8a5e85843da7fe93%26_api_key%3Dc51d789e4b3b2ed1ec408161295cc540"><img src="${base}/static/share/img/Download02.png"/></a><a href="${base}/download/zb.apk"><img src="${base}/static/share/img/Download03.png"/></a></h2>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		function is_weixin() {
		    var ua = navigator.userAgent.toLowerCase();
		    if (ua.match(/MicroMessenger/i) == "micromessenger") {
		        return true;
		    } else {
		        return false;
		    }
		}
		var isWeixin = is_weixin();
		var winHeight = typeof window.innerHeight != 'undefined' ? window.innerHeight : document.documentElement.clientHeight;
		function loadHtml(){
			var div = document.createElement('div');
			div.id = 'weixin-tip';
			div.innerHTML = '<p><img width="100%" src="${base}/static/share/img/pop-tips.png" alt="微信打开"/></p>';
			document.body.appendChild(div);
		}
		
		function loadStyleText(cssText) {
	        var style = document.createElement('style');
	        style.rel = 'stylesheet';
	        style.type = 'text/css';
	        try {
	            style.appendChild(document.createTextNode(cssText));
	        } catch (e) {
	            style.styleSheet.cssText = cssText; //ie9以下
	        }
            var head=document.getElementsByTagName("head")[0]; //head标签之间加上style样式
            head.appendChild(style); 
	    }
	    var cssText = "#weixin-tip{position: fixed; left:0; top:0; background: rgba(0,0,0,0.8); filter:alpha(opacity=80); width: 100%; height:100%; z-index: 100;} #weixin-tip p{text-align: center; margin-top: 10%; padding:0 5%;}";
		if(isWeixin){
			loadHtml();
			loadStyleText(cssText);
		}
	</script>	
	
</html>
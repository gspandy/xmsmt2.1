<!DOCTYPE html>
<html lang="zh">
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta charset="utf-8">
		<title></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no">
		<meta name="description" content="">
		<meta name="author" content="">
		<!-- STYLESHEETS -->
		<!--[if lt IE 9]>
		<script src="js/flot/excanvas.min.js"></script>
		<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
		<script src="http://css3-mediaqueries-js.googlecode.com/svn/trunk/css3-mediaqueries.js"></script>
		<![endif]-->
		
		
				
		[#include "/admin/common/include.ftl"]
		
		<script src="${base}/static/layer/layer.js"></script>
	</head>
	<body style="overflow-y:auto">
		[#include "/admin/common/header.ftl"]
		<section id="page">
			[#include "/admin/common/sidebar.ftl"]
			<div id="main-content">
				<div class="container">
				
					<iframe id="iframe" name="iframe" src="index.jhtml" frameborder="0" width="100%" 
							onLoad="iFrameHeight(this)" scrolling="yes" >
					</iframe>
					
				</div>
			</div>
		</section>
	</body>
	
	<script type="text/javascript" language="javascript">
		function iFrameHeight(obj) { 
			var win=obj; 
 			if (document.getElementById) { 
 				if (win && !window.opera) { 
 					if (win.contentDocument && win.contentDocument.body.offsetHeight)
						win.height = win.contentDocument.body.offsetHeight + 360; 
 					else if(win.Document && win.Document.body.scrollHeight  )
 						win.height = win.Document.body.scrollHeight + 360;
 				} 
 			} 
 		} 
								    
 		function iFrameHeigh1() {
 			var ifm= document.getElementById("iframe");
 			var subWeb = document.frames ? document.frames["iframe"].document :
 			ifm.contentDocument;
 			if(ifm != null && subWeb != null) {
 				ifm.height = subWeb.body.clientHeight;
			}
		}


		$("#chatBtn").click(function() {
		//window.open('${base}/admin/customerServer/server.jhtml');
			layer.open({
			    type: 2,
			    title: '客户咨询',
			    shadeClose: true,
			    shade: 0.8,
			    area: ['80%', '100%'],
			    //参数传递，userId，imageId，username
			    content: '${base}/admin/customerServer/server.jhtml' //iframe的url
			}); 
		});
		
		
		function setNum(num) {
			$("#chatNum").text(num);
		}
		
		$(document).ready(function(){  
		     $(window).scroll(function (){                  // 让浮动层距离窗口顶部，始终保持180px 
		    	offsetTop= $(window).scrollTop()+ 180 +"px";  
		    }); 
		});
		
		var offsetTop=$(window).scrollTop()+ 180 +"px";;
		
		function topHeight(){
			return offsetTop;	 
		 }
	</script>
</html>
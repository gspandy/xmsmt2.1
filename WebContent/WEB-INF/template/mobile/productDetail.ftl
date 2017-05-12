<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />		
		<meta name="full-screen" content="yes">
		<meta name="x5-fullscreen" content="true">
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
		  <link rel="stylesheet" href="${base}/static/share/css/swiper.min.css">
        <link rel="stylesheet" href="${base}/static/share/css/swiper.min.css">
		<link rel="stylesheet" href="${base}/static/share/css/zhibang.css" />
		<script src="${base}/static/share/js/idangerous.swiper.min.js"></script>
		<script src="${base}/static/share/js/jquery.min.js"></script>

		<title>${product.fullName}</title>
		
		<style>
			html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, font, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, dd, dl, dt, li, ol, ul, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td {
				font-size: 14px;
			}
		</style>
	</head>
	<body>
	<script>
	    //种类
	    var specificationLength = ${product.specifications?size};
	   
	    //产品列表
	    var productMap = {};
	    [@compress single_line = true]
			productMap[${product.id}] = {
	    specificationValues: [
            [#list product.specificationValues as specificationValue]
	    "${specificationValue.id}"[#if specificationValue_has_next],[/#if]
   		 [/#list]
	    ],
	        productId: "${product.id}",
	    fullName : "${product.fullName}",
	    stock:"${product.stock}",
	  sales:"${product.sales}",
	    marketPrice : "${product.marketPrice}",
	    price : "${product.price}",
	    tenantId : "${product.tenant.id}",
	    tenantName : "${product.tenant.shortName}",
	    image : "${product.image}"
	    };
			
	    [#list product.siblings as product]
				productMap[${product.id}] = {
	    specificationValues: [
            [#list product.specificationValues as specificationValue]
	    "${specificationValue.id}"[#if specificationValue_has_next],[/#if]
    [/#list]
	    ],
	        productId: "${product.id}",
	    fullName : "${product.fullName}",
	     stock:"${product.stock}",
	  		sales:"${product.sales}",
	    marketPrice : "${product.marketPrice}",
	    price : "${product.price}",
	    tenantId : "${product.tenant.id}",
	    tenantName : "${product.tenant.shortName}",
	    image : "${product.image}"
	    };
	    [/#list]
    [/@compress]
	</script>
		<div class="container ">
			<div class="swiper-container">
		        <div class="swiper-wrapper">
		        	[#list product.productImages as item]
		        		[#if item_index == 0]
							<div class="swiper-slide"><img width="100%" src="${item.large}"></div>	        		
		        		[#else]
		        			<div class="swiper-slide"><img width="100%" src="${item.large}"></div>
		        		[/#if]
		        	[/#list]
		        </div>
		        <!-- banner上的圆点 -->
		        <div class="swiper-pagination swiper-pagination-clickable"><span class="swiper-pagination-bullet swiper-pagination-bullet-active"></span><span class="swiper-pagination-bullet"></span></div>
		    </div>
			<div class="Product-details-Price">
				<h1><span><b>￥</b>${product.price}</span><s>内购价</s><i>全场包邮</i></h1>
				<h2><span>市场价:￥${product.marketPrice}</span>
				[#if product.priceType == "jd"]
					<s style="background-image: url(${base}/static/share/img/icon-jd@2x.png);">京东价:￥${product.ePrice}</s>
				[#elseif product.priceType == "tmall"]
					<s style="background-image: url(${base}/static/share/img/icon-bg-07.png);">天猫价:￥${product.ePrice}</s>
				[#elseif product.priceType == "taobao"]
					<s style="background-image: url(${base}/static/share/img/icon-tb@2x.png);">淘宝价:￥${product.ePrice}</s>	
				[#elseif product.priceType == "store"]
				<s style="background-image: url(${base}/static/share/img/icon-md.png);">官网价:￥${product.ePrice}</s>
				[#else]
				<s style="background-image: url(${base}/static/share/img/icon-md.png);">团购价:￥${product.ePrice}</s>
				[/#if]
				</h2>
				<h3>${product.name}</h3>
			<!--<h4><span>库存 ${product.stock}件</span><span>销量 ${product.sales}件</span></h4>-->
								<h4><span id="stock_spn"></span><span id="sales_spn"></span></h4>		
			</div>
			
			[#assign specificationValues = product.goods.specificationValues /]
			[#list product.specifications as specification]
				<div class="Product-details-Size top-line" id="here">
	
					<h1>${specification.name}</h1>
					<ul>
						[#list specification.specificationValues as specificationValue]
							[#if specificationValues?seq_contains(specificationValue)]
							
							
								<li val="${specificationValue.id}" txt="${specification.name}：${specificationValue.name}" specification="${specification.name}" specificationValue="${specificationValue.name}"><a  ><span >
								[#if specification.type == "text"]
									${specificationValue.name}
								[#else]
									<img src="${specificationValue.image}" alt="${specificationValue.name}" title="${specificationValue.name}" />
								[/#if]
								</span><em></em></a></li>
							[/#if]
						[/#list]
					</ul>
				</div>
			[/#list]
			
			<!--正品 -->
			<div class="Product-details-Genuine top-line">
				<ul>
					<li><span></span><em>100%正品保证</em></li>
					<li><span></span><em>7天无理由退货</em></li>
				</ul>
			</div>
			
			<!--内购券 -->
			<div class="Product-details-Purchase top-line">
				<h1><span class="Product-se">商品信息</span><span onclick="toMain()">进入内购店首页</span></h1>
				<h2 onclick="saveMoney()"><span>内购券</span>单笔订单购买商品越多省钱越多</h2>
			</div>
			
			<!--信息 -->
			<div class="Product-details-info top-line">
				
				<ul>
					<li><span>品牌</span><em>${product.brand.name}</em></li>
					[#list product.parameterValue?keys as key]
						<li><span>${key.name} </span><em>${product.parameterValue.get(key)}</em></li>
					[/#list]
				</ul>
			</div>
			
			
			<!--图文 -->
			<div class="Product-details-Text top-line" > 
			    <h1><a href="#">图文详情  <span></span></a></h1>
			   
				<div class="details" onclick="clickImg()" id = "imgtest" >
				${product.introduction}
				</div>
				
			</div>
			
		</div>

		<!--返回顶部-->
		<div class="Return-top"><a href="#"><img src="${base}/static/share/img/Return-top.png"/></a></div>
		
		<script>
		
		//发生字符串
		var datatoandroid=null;
		//当前选中商品的JSON数据
		var JsondataSelProd=null;
		//尺码
		 var li= $(".Product-details-Size li");
		    li.not(".Product-details-Size-sl2").click(function(){
		        var lis = $(this).parent().find("li").not(".Product-details-Size-sl2");
		        lis.removeClass("Product-details-Size-sl1");
		        $(this).addClass("Product-details-Size-sl1");
				
		        //选中的数量
		        var selectLis = $(".Product-details-Size-sl1");
		        if(selectLis.length == specificationLength) {
		            var selectIds = new Array();
				
		            var selectObject = {};
		            var specifications = new Array();
		            for(var i = 0 ;  i < specificationLength; i++) {
		                selectIds.push($(selectLis[i]).attr("val"));
		                var obj = {
		                    key : $(selectLis[i]).attr("specification"),
		                    val : $(selectLis[i]).attr("specificationValue")
		                }
		                specifications.push(obj);
		            }
				
		            var isExist = false;
		            $.each(productMap, function(i, product) {//1,2
		                if (isExist == false && contains(product.specificationValues, selectIds)) {
		                    isExist = true;
		                    selectObject.productId = product.productId;
		                    selectObject.fullName = product.fullName;
		                    selectObject.stock= product.stock;
		                    selectObject.sales=product.sales;
		                    selectObject.image = product.image;
		                    selectObject.specifications = specifications;
		                    selectObject.marketPrice = product.marketPrice;
		                    selectObject.price = product.price;
		           
		                    selectObject.tenantId = product.tenantId;
		                    selectObject.tenantName = product.tenantName;
						
		                   // var url="testapp:addcat:" + JSON.stringify(selectObject);
							//发送给android
							datatoandroid= JSON.stringify(selectObject);
							//赋值，用于当前选中商品
							JsondataSelProd=JSON.stringify(selectObject);
							clickProduct();
							getValue2(datatoandroid);
		                    //document.location = url;
		                    return;
		                }
		            });
		            if(!isExist) {
		                $(this).addClass("Product-details-Size-sl2");
		            }
		        }
		    });
		 
		    // 判断是否包含
		    function contains(array, values) {
		        var contains = true;
		        for(i in values) {
		            if ($.inArray(values[i], array) < 0) {
		                contains = false;
		                break;
		            }
		        }
		        return contains;
		    }
			
		    //返回顶部	
		    $(window).scroll(function(){  
		        if ($(window).scrollTop()>100){  
		            $(".Return-top").fadeIn(1500);  
		        }  
		        else  
		        {  
		            $(".Return-top").fadeOut(1500);  
		        }  
		    });  

		    $("Return-top").click(function(){  
		        $('body,html').animate({scrollTop:0},1000);  
		        return false;  
		    });  
		    
			function getValue2(datatoandroid){
				    //alert(datatoandroid);
				    myObj.fun1FromAndroid(datatoandroid);  
				}
			function toMain(){
				    //alert(datatoandroid);
				  myObj.fun1FromAndroidToMain(); 
				}	
			function saveMoney(){
			    //alert(datatoandroid);
			  myObj.fromAndroidSaveMoney(); 
			}
			
			function clickImg(){
				 //alert($("imgtest").val());
			    //alert(imgUrl);
			    myObj.clickImgToAndroid(imgUrl);  
			}
			//点击某个商品类别
			function clickProduct()
			{	
				//alert("当前库存："+JsondataSelProd);
				var obj = $.parseJSON(JsondataSelProd);
				$("#stock_spn").html("");
				$("#stock_spn").text("库存"+obj.stock+"件");
				$("#sales_spn").html("");
				$("#sales_spn").text("销量"+obj.sales+"件");
				
			}
		//var aTag = document.getElementById("")
		//aTag.addEventListener('click', function(){  
	    //调用android本地方法  
	    //myObj.fun1FromAndroid(datatoandroid);  
	    //alert(datatoandroid);
	   // return false;  
	//}, false); 

//}
function comeHere(){

	        var cotentOffset = $('#here').offset(); 
	         var scrollTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
	        $('body,html').animate({scrollTop:cotentOffset.top},200);  
	       
	        return true;  

};
		</script>
				<!-- 滚动图用到的脚本 -->
	    <script src="${base}/static/share/js/swiper.min.js"></script>	    
	    <script>
	        var swiper = new Swiper('.swiper-container', {
	            pagination: '.swiper-pagination',
	            paginationClickable: true
	        });
	    </script>
	</body>
</html>
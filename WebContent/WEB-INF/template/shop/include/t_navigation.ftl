<div id="mainNav"><div class="nav_bg"><!--category开始-->
<div class="w_cart"><span>购物车</span><span id="cartCount">0</span></div>
<div class="w_cart_pull">
	<div id="noneCartItem" class="cart_none" style="padding:50px 30px;display:none">您的购物车里还没有商品，欢迎选购！</div> 
	<div id="showMiniCartDetail" class="list_detail">
 	</div>
</div>
<script type="text/javascript">
$().ready(function() {
	setTimeout(function(){
		getCart();
	},1000);
});
//进入购物车
function getCart() {
		$.ajax({
		 	url: "${base}/cart/get_cart_count.jhtml",
			type: "POST",
			dataType: "json",
			cache: false,
			success: function(data) {
				$("#cartCount").text(data.count);
			}
		});
		$.ajax({
			url:"${base}/cart/list_ajax.jhtml",
			type:"get",
			dataType:"json",
			cache:false,
			success:function(data){
				$("#showMiniCartDetail").html("");
				if(data==null){
					$("#noneCartItem").attr('style','display:block');
					return false;
				}
				if(data.cartItems==null||data.cartItems.length==0){
					$("#noneCartItem").attr('style','display:block');
					return false;
				}else{
					$("#noneCartItem").attr('style','display:none');
					$("#showMiniCartDetail").append("<ul></ul>");
					for(var i in data.cartItems){
						$("#showMiniCartDetail ul").append("<li id='mini_cart_li_0'><a traget='_blank' class='pro_img' href='#' ><img  alt="
														+data.cartItems[i].product.fullName
														+" src="
														+data.cartItems[i].product.thumbnail
														+" />"
														+"</a>"
														+"<a traget='_blank' class='pro_name' href=${base}/"
														+data.cartItems[i].product.path
														+">"
														+data.cartItems[i].product.name
														+"</a>"
														+"<span class='pro_price'>"
														+currency(data.cartItems[i].tempPrice,true)
														+"</span>"
														+"<div class='num_box'><b class='minusDisable'>-</b><input type='text' class='minicart_num' value="
														+data.cartItems[i].quantity
														+" /><b class='plus'>+</b><a href='javascript:void(0);' onclick='delete1("
														+data.cartItems[i].id
														+");'>删除</a></div></li>");
					}
				}
				$("#showMiniCartDetail").append("<div class='checkout_box'><p><span class='fl'>共<strong>"
				  									+data.quantity
				  									+"</strong>件商品</span>合计：<strong>"
				  									+currency(data.effectivePrice,true)
				  									+"</strong></p><a href='${base}/cart/list.jhtml' class='checkout_btn'>去结算</a></div>");	
			}				
		});
 };
 // 删除购物车item
 function delete1(id){
	 if (confirm("${message("shop.dialog.deleteConfirm")}")) {
			$.ajax({
				url: "${base}/cart/delete.jhtml",
				type: "POST",
				data: {id: id},
				dataType: "json",
				cache: false,
				success: function(data) {
					if (data.message.type == "success") {
						getCart();
					} else {
						return false;
					}
				}
			});
	}
	return false;
 };
</script>
<div class="w_nav_pulls">
	<ul>
		[@tenant_category_root_list  count = 10]
		[#list tenantCategories  as tenantCategory]
			<li><span>+</span><a href="javascript:void(0);">${tenantCategory.name}</a>
				<div class="w_nav_pullsbottom">
					[@tenant_category_children_list tenantCategoryId=tenantCategory.id count=3]
						[#list tenantCategories as tenantCategory]
							<A href="${base}${tenantCategory.path}">${tenantCategory.name}</A>
						[/#list]
					[/@tenant_category_children_list]
					<div style="clear:both"></div>
				</div>
				<div style="clear:both"></div>	
			</li>
		[/#list]
		[/@tenant_category_root_list]
		<li class="navmore"><a href="javascript:void(0)">查看更多</a></li>
	</ul>
</div>
<div id="category">
<H2 class="dt categoryFonts"><A class="mainNavFont" style="letter-spacing: 1px;" 
href="${base}/product_category.jhtml"><SPAN 
style="color: rgb(255, 255, 255);">商铺分类</SPAN> 		 </A>
</H2>
</div><!-- 网站主导航区 -->		 <!--category结束-->		 


<UL class="mainNavFont ul">
			<li style="background:none;" ><a href="${base}/shop/index.jhtml">回到首页</a></li>
			[@navigation_list position = "middle"]
				[#list navigations as navigation]
					<li[#if navigation.url = url] class="current"[/#if]>
						<a  href="${base}${navigation.url}"[#if navigation.isBlankTarget] target="_blank"[/#if]>${navigation.name}</a>
					</li>
				[/#list]
			[/@navigation_list]
</UL>

</div>
</div>
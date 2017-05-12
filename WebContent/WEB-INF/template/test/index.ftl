<form action="${base}/test/order/create.jhtml" method="POST">
	<input type="text" name="buys[0].productId" value="1" />
	<input type="text" name="buys[0].quantity" value="2" />
	
	<input type="text" name="buys[1].productId" value="3" />
	<input type="text" name="buys[1].quantity" value="4" />
	<input type="submit" value="提交" />
</form>
package net.wit.mobile.util.rong.models;

import net.sf.json.JSONObject;
import net.wit.mobile.util.rong.util.GsonUtil;

//系统消息
public class SystemMessage extends Message {
	
	
	/** extra*/
	public static final String BUYER_MSG_EXTRA= "BUYER_MSG";  //会员消息
	
	public static final String SHOPKEEPER_MSG_EXTRA  = "SHOPKEEPER_MSG";  //店主消息
	
	
	/**shopkeeper reminder  content*/
	public static final String SHOPKEEPER_TICKET_GRANT_REMINDER="${tenantName}商家已成功发放${ticketCount}张内购券,快去领取吧!"; //券券发放提醒;
	
	public static final String SHOPKEEPER_ORDER_END_REMINDER="会员:${buyerName}的购买编号为${orderSn}的订单已经结算完成! 您已获得${charge}元的分享佣金,下个月结算日时可以申请领取"; //店主的券订单结束提醒;
	
	public static final String SHOPKEEPER_INVATION_REMINDER = "您已成功邀请${name}成为${tenantName}内购店的店长";  //店主邀请开通店长提醒;
	
	public static final String SHOPKEEPER_PLACE_ORDER_REMINDER = "会员:${buyerName}的订购了编号为${orderSn}的订单，不出意外您将获得${charge}元的分享佣金！"; //店主的券下单提醒;
	
	public static final String SHOPKEEPER_COMMISSION_PAYMENT_REMINDER="商家已将您${month}的分享佣金${charge}元,发放至您指定的支付宝账号上，请注意查收！如若未收到，请及时的联系商家在线客服或者拨打咨询热线，望知悉！"; // 店主佣金发放提醒
   
	public static final String SHOPKEEPER_RENT_PAYMENT_REMINDER = "您已成功缴纳${num}月的租金!	"; //交租成功
	
	public static final String SHOPKEEPER_PAY_RENT_REMINDER = "您${month}的租金还未缴纳,记得在10号前缴纳哦!";	 //交租提醒
	
	public static final String SHOPKEEPER_FREE_END_REMINDER = "您的免租期将于${date}到期，每个自然月10号需要交纳30元的租金，记得按时交纳哦!"; //免租到期提醒;
	
	public static final String SHOPKEEPER_OPEN_REMINDER = "恭喜您成功开通${tenantName}内购店，店长内购券已放入您的券包，立即去领取吧!";  //成功开通店长消息提醒	
	
	public static final String SHOPKEEPER_BONUS_PAYMENT_REMINDER ="商家已将您${month}的奖金${bonus}元,发放至您指定的支付宝账号上，请注意查收！如若未收到，请及时的联系商家在线客服或者拨打咨询热线，望知悉！" ;//奖金发放提醒
	
	public static final String SHOPKEEPER_TICKET_BERECEVIED="您分享的${tenantName}内购券已经被手机号为${mobile}的好友领取！";
	
	public static final String SHOPKEEPER_TICKET_APPLY_CONFIRMED = "您申请的${tenantName}内购券已经审批通过，本次共发放${num}张内购券,快去领取吧!";
	
	public static final String SHOPKEEPER_TICKET_APPLY_REJECTED= "您申请的${tenantName}内购券被拒绝了,下次再试试运气吧!";
	
	public static final String SHOPKEEPER_TICKET_APPLY_REMINDER="您的好友${name}向您申请一张内购券，快快去分享给他赚取更多的分享佣金吧！";
			
	public static final String SHOPKEEPER_店长LEVEL_UP_MSG="祝贺您，您成功邀请${num}位店长,您的店长等级已经达到${levelName}的级别！"; //vip升级；		
	
	
 /** buyer reminder content*/
	/*付款成功提醒*/
	public static final String BUYER_ORDER_PAYMENT_REMINDER = "您在${tenantName}内购店拍下编号为${orderSn}的订单，已付款成功， 商家将很快为您发货！";
	/*发货提醒*/
	public static final String BUYER_ORDER_DELIVER_REMINDER="您在${tenantName}内购店拍下编号为${orderSn}的订单，商家已经为您发货！";
	/*签收提醒*/
	public static final String BUYER_ORDER_SIGN_REMINDER="您在${tenantName}内购店拍下编号为${orderSn}的订单，已经成功签收！";
	/*自动收货提醒*/
	public static final String BUYER_ORDER_AUTO_SIGN_REMINDER ="您在${tenantName}内购店拍下编号为${orderSn}的订单，已经过了自动收获期，系统将自动为您签收！";
	/*订单结束提醒*/
	public static final String BUYER_ORDER_END_REMINDER="您在${tenantName}内购店拍下编号为${orderSn}的订单，已经完成，系统将自动为标记为交易成功状态！";
	/*退款申请成功*/
	public static final String BUYER_ORDER_REFUND_APPLY_REMINDER="您在${tenantName}内购店拍下编号为${orderSn}的订单，退款申请成功！请耐心等待财务给您打款！";
	/*退货申请成功*/
	public static final String BUYER_ORDER_RETURN_GOODS_REMINDER = "您在${tenantName}内购店拍下编号为${orderSn}的订单，退货申请成功！我们收到货后将退还您的货款！";
	/*退款打款提醒*/
	public static final String BUYER_ORDER_MENY_RETURN_REMINDER= "您在${tenantName}内购店拍下编号为${orderSn}的订单，退款${money}元已经原路返回，请查收！";
	/*退货签收提醒*/
	public static final String BUYER_ORDER_RETURN_SIGN_REMINDER="您在${tenantName}内购店拍下编号为${orderSn}的订单的退货，商家已经签收，请耐心等待财务退款！";
	
	/*
	 * 买家收到邀请函的信息
	 */
	public static final String BUYER_APPLY_SHOPKEEPER_PASS= "您的向${tenantName}申请成为店长已经通过，退出重新登录即可成为店长啦";
	
	/*
	 * 买家收到邀请函的信息
	 */
	public static final String BUYER_APPLY_SHOPKEEPER_REJECT= "您的向${tenantName}申请成为店长已经被拒绝了，请电话联系客服姐姐咨询吧！";
	
	/*
	 * 买家收到邀请函的信息
	 */
	public static final String BUYER_BE_INVITED_MSG= "您的收到来至 好友${name}的${tenantName} 内购店的邀请函";
	
	/**
	 * 会员向企业申请券
	 */
	
	public static final String  BUYER_TICKET_APPLY_TO_TENANT_CONFIRMED= "您向${tenantName}申请一张内购券已经发放给您啦，快去查收一下吧 ！";
	
	/**
	 * 会员申请券被拒绝
	 */
	
	public static final String  BUYER_TICKET_APPLY_TO_TENANT_REJECTED= "您向${tenantName}申请一张内购券被拒绝啦,打电话问问客户姐姐吧！";
	/**
	 * 会员申请券
	 */
	
	public static final String  BUYER_TICKET_APPLY_CONFIRMED= "您的好友${ownerName}已经同意分享一张${tenantName}内购券给您，快去查收一下吧 ！";
	/**
	 * 会员申请被拒绝
	 */
	public static final String  BUYER_TICKET_APPLY_REJECTED= "您的好友${ownerName}已经拒绝分享一张${tenantName}内购券给您。或许他没券了,电话打听一下呗 ！";

	
	public static SystemMessage shopKeeperVipLevelUpMsg(String num,String levelName){
		String content = SHOPKEEPER_店长LEVEL_UP_MSG.replace("${num}", num).replace("${levelName}", levelName);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 店长-会员向店长申请券券通知给店长；
	 * @param name
	 * @return
	 */
	
	public static SystemMessage shopKeeperTicketApplyMsg(String name){
		String content = SHOPKEEPER_TICKET_APPLY_REMINDER.replace("${name}", name);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 店长-券券申请批准
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage shopKeeperTicketApplyConfirmed(String tenantName,String num){
		String content = SHOPKEEPER_TICKET_APPLY_CONFIRMED.replace("${tenantName}", tenantName).replace("${num}", num);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	
	/**
	 *  店长-券券申请拒绝
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage shopKeeperTicketApplyRejected(String tenantName){
		String content = SHOPKEEPER_TICKET_APPLY_REJECTED.replace("${tenantName}", tenantName);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 店主券被领取-提醒
	 * @param tenantName
	 * @param mobile
	 * @return
	 */
	public static SystemMessage shopKeeperTicketBeRecevied(String tenantName,String mobile){
		String content = SHOPKEEPER_TICKET_BERECEVIED.replace("${tenantName}", tenantName).replace("${mobile}", mobile);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 店主-发券提醒
	 * @param tenantName
	 * @param ticketCount
	 * @return
	 */
	public static  SystemMessage shopKeeperTicketMsg(String tenantName, String ticketCount){
		String content = SHOPKEEPER_TICKET_GRANT_REMINDER.replace("${tenantName}", tenantName).replace("${ticketCount}", ticketCount);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	/**
	 * 店主-有会员订单结算提醒
	 * @param buyerName
	 * @param orderSn
	 * @param charge
	 * @return
	 */
	public static SystemMessage shopKeeperOrderEndMsg(String buyerName, String orderSn,String charge){
		String content = SHOPKEEPER_ORDER_END_REMINDER.replace("${buyerName}", buyerName).replace("${orderSn}", orderSn).replace("${charge}", charge);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
 /**
  * 店主-成功邀请开通店长提醒
  * @param name
  * @param tenantName
  * @return
  */
	public static SystemMessage shopKeeperInvationMsg(String name, String tenantName){
		String content = SHOPKEEPER_INVATION_REMINDER.replace("${name}", name).replace("${tenantName}", tenantName);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 * 店主-有会员下单提醒
 * @param buyerName
 * @param orderSn
 * @param charge
 * @return
 */
	public static SystemMessage shopKeeperPlaceOrderMsg(String buyerName, String orderSn,String charge){
		String content = SHOPKEEPER_PLACE_ORDER_REMINDER.replace("${buyerName}", buyerName).replace("${orderSn}", orderSn).replace("${charge}", charge);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 * 店主-佣金发放提醒
 * @param month
 * @param charge
 * @return
 */
	public static SystemMessage shopKeeperChargePaymentMsg(String month, String charge){
		String content = SHOPKEEPER_COMMISSION_PAYMENT_REMINDER.replace("${month}", month).replace("${charge}", charge);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 * 店主-租金缴纳成功提醒
 * @param num
 * @return
 */
	public static SystemMessage shopKeeperRentPaymentMsg(String num){
		String content = SHOPKEEPER_RENT_PAYMENT_REMINDER.replace("${num}", num);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 * 店主-提醒店主去交租
 * @param month
 * @return
 */
	public static SystemMessage shopKeeperRentReminder(String month){
		String content = SHOPKEEPER_PAY_RENT_REMINDER.replace("${month}", month);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 *店主-提醒免租期结束
 * @param date
 * @return
 */
	public static SystemMessage shopKeeperFreeEndMsg(String date){
		String content = SHOPKEEPER_FREE_END_REMINDER.replace("${date}", date);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 * 店主-开通店长成功提醒
 * @param tenantName
 * @return
 */
	public static SystemMessage shopKeeperOpenMsg(String tenantName){
		String content = SHOPKEEPER_OPEN_REMINDER.replace("${tenantName}", tenantName);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
/**
 * 店主-奖金发放提醒
 * @param month
 * @param bonus
 * @return
 */
	public static SystemMessage shopKeeperBonusPaymentMsg(String month, String bonus){
		String content = SHOPKEEPER_BONUS_PAYMENT_REMINDER.replace("${month}", month).replace("${bonus}", bonus);
		return new SystemMessage(SHOPKEEPER_MSG_EXTRA, content);
	}
	

	
	
	
/**
 * 会员-订单付款成功提醒
 * @param tenantName
 * @param orderSn
 * @return
 */
	public static SystemMessage buyerOrderPaymentMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_PAYMENT_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
/**
 * 会员-订单发货提醒
 * @param tenantName
 * @param orderSn
 * @return
 */
	public static SystemMessage buyerOrderDeliverMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_DELIVER_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
/**
 * 会员-订单签收提醒
 * @param tenantName
 * @param orderSn
 * @return
 */
	public static SystemMessage buyerOrderSignMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_SIGN_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
/**
 * 会员-订单自动签收提醒
 * @param tenantName
 * @param orderSn
 * @return
 */
	public static SystemMessage buyerOrderAutoSignMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_AUTO_SIGN_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
/**
 * 会员-订单退款申请成功提醒
 * @param tenantName
 * @param orderSn
 * @return
 */
	public static SystemMessage buyerOrderRefundMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_REFUND_APPLY_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
/**
 * 会员-退货申请成功提醒
 * @param tenantName
 * @param orderSn
 * @return
 */
	public static SystemMessage buyerOrderReturnMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_RETURN_GOODS_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
/**
 * 会员-订单退款打款提醒
 * @param tenantName
 * @param orderSn
 * @param money
 * @return
 */
	public static SystemMessage buyerOrderMoneyReturnMsg(String tenantName, String orderSn,String money){
		String content = BUYER_ORDER_MENY_RETURN_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn).replace("${money}", money);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	/**
	 * 会员-订单退货商家签收提醒
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerOrderReturnSignMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_RETURN_SIGN_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	/**
	 * 会员-订单完成消息
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerOrderEndMsg(String tenantName, String orderSn){
		String content = BUYER_ORDER_END_REMINDER.replace("${tenantName}", tenantName).replace("${orderSn}", orderSn);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	/**
	 * 会员-券券申请同意
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerTicketApplyConfirmed(String ownerName,String tenantName){
		String content = BUYER_TICKET_APPLY_CONFIRMED.replace("${ownerName}", ownerName).replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 会员-券券申请拒绝
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerTicketApplyRejected(String ownerName,String tenantName){
		String content = BUYER_TICKET_APPLY_REJECTED.replace("${ownerName}", ownerName).replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	/**
	 * 买家收到邀请函提醒
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerBeInvited(String name,String tenantName){
		String content = BUYER_BE_INVITED_MSG.replace("${name}", name).replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 买家向企业申请内购券
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerApplyTicketToTenant(String tenantName){
		String content = BUYER_TICKET_APPLY_TO_TENANT_CONFIRMED.replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	
	/**
	 * 买家向企业申请内购券被拒绝
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerApplyTicketRejectByTenant(String tenantName){
		String content = BUYER_TICKET_APPLY_TO_TENANT_REJECTED.replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	/**
	 * 买家向企业申请成为店长被审核通过
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerApplyToShopKeeperPass(String tenantName){
		String content = BUYER_APPLY_SHOPKEEPER_PASS.replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}
	
	/**
	 * 买家向企业申请成为店长被审核拒绝
	 * @param tenantName
	 * @param orderSn
	 * @return
	 */
	public static SystemMessage buyerApplyToShopKeeperReject(String tenantName){
		String content = BUYER_APPLY_SHOPKEEPER_REJECT.replace("${tenantName}", tenantName);
		return new SystemMessage(BUYER_MSG_EXTRA, content);
	}


	
	

	private String content;
	private String extra;

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	public SystemMessage(String extra ,String content) {
		this.type = "RC:TxtMsg";
		this.extra = extra;
		this.content =content;
	}



	public String getContent() {
		return content;
	}

	public void setContent(String title,String content) {
		JSONObject js = new JSONObject();
		js.put("title", title);
		js.put("content", content);
		this.content =js.toString();
	}

	@Override
	public String toString() {
		return GsonUtil.toJson(this, SystemMessage.class);
	}
}

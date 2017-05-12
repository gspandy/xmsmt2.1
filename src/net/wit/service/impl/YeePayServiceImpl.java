package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.dao.SnDao;
import net.wit.entity.BaseEntity;
import net.wit.entity.Member;
import net.wit.entity.MemberBank;
import net.wit.entity.Order;
import net.wit.entity.OwnerCashDetail;
import net.wit.entity.Payment;
import net.wit.entity.Refunds;
import net.wit.entity.Sn;
import net.wit.entity.Tenant;
import net.wit.exception.OrderException;
import net.wit.service.MemberBankService;
import net.wit.service.MemberService;
import net.wit.service.OrderService;
import net.wit.service.OwnerCashDetailService;
import net.wit.service.OwnerServerice;
import net.wit.service.RefundsService;
import net.wit.service.TenantService;
import net.wit.service.YeePayService;
import net.wit.util.BizException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

/**
 * Service - 易宝支付服务类
 * @author Teddy
 * @version 1.0
 */
@Service("yeePayServiceImpl")
public class YeePayServiceImpl extends BaseServiceImpl<BaseEntity, Long> implements YeePayService {
	
	private Logger log = LoggerFactory.getLogger(YeePayServiceImpl.class);
	
	@Resource(name = "snDaoImpl")
	private SnDao snDao;
	
	@Resource(name = "memberServiceImpl")
	private MemberService memberService;
	
	@Resource(name = "memberBankServiceImpl")
	private MemberBankService memberBankService;
	
	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;
	
	@Resource(name = "orderServiceImpl")
	private OrderService orderService;
	
	@Resource(name = "refundsServiceImpl")
    private RefundsService refundsService;
	
	@Resource(name = "ownerServericeImpl")
	private OwnerServerice ownerServerice;

	@Resource(name = "ownerCashDetailServiceImpl")
	private OwnerCashDetailService ownerCashDetailService;
	
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> registerAccount(Map<String, String> params) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		Member member = null;
		Tenant tenant = null;
		MemberBank memberBank = new MemberBank();
		String customertype = params.get("customertype");// 前台传入：PERSON-个人；ENTERPRISE-企业
		String requestid = snDao.generate(Sn.Type.yeepayRegister);// 由系统自动生成 TODO
		String bindmobile = null;// 绑定手机号
		String signedname = params.get("name");// 签约名
		String linkman = params.get("name");// 联系人
		String idcard = params.get("idcard");// 身份证号
		String businesslicence = null;// 营业执照号
		String legalperson = params.get("name");// 姓名：个人姓名或者企业法人姓名
		String minsettleamount = null;// 起结金额
		String riskreserveday = "1";// 结算周期
		String bankaccountnumber = params.get("bankaccountnumber");// 银行卡号(参数传入)
		String bankname = params.get("bankname");// 开户行
		String accountname = params.get("name");// 开户名
		String bankaccounttype = null;// 银行卡类别
		String bankprovince = params.get("bankprovince");// 开户省
		String bankcity = params.get("bankcity");// 开户市
		String manualsettle = null;// 自助结算
		
		if (customertype != null && "PERSON".equalsIgnoreCase(customertype)) {// 个人用户
			// 获取会员信息表的数据
			member = this.memberService.find(new Long(params.get("memberId")));
			bindmobile = member.getMobile();
			minsettleamount = "100";// TODO
			bankaccounttype = "PrivateCash";
			manualsettle = "Y";// TODO
		} else if (customertype != null && "ENTERPRISE".equalsIgnoreCase(customertype)) {// 企业用户
			// 获取企业信息表的数据
			tenant = this.tenantService.find(new Long(params.get("tenantId")));
			bindmobile = tenant.getTelephone();
			legalperson = params.get("legalperson");
			businesslicence = tenant.getLicenseCode();
			minsettleamount = "10000";// TODO
			if(params.get("name")!=null&&!(params.get("name").equals(""))){
				accountname=params.get("name");
			}else{
				accountname = tenant.getName();
			}
			bankaccounttype = "PublicCash";
			manualsettle = "N";// TODO
		}
		
	 	params.put("requestid", 		requestid);
	 	params.put("bindmobile", 		bindmobile);
	 	params.put("customertype", 		customertype);
	 	params.put("signedname", 		signedname);
	 	params.put("linkman", 			linkman);
	 	params.put("idcard", 			idcard);
	 	params.put("businesslicence", 	businesslicence);
	 	params.put("legalperson", 		legalperson);
	 	params.put("minsettleamount", 	minsettleamount);
	 	params.put("riskreserveday", 	riskreserveday);
	 	params.put("bankaccountnumber",	bankaccountnumber);
	 	params.put("bankname",	 		bankname);
	 	params.put("accountname", 		accountname);
	 	params.put("bankaccounttype", 	bankaccounttype);
	 	params.put("bankprovince", 		bankprovince);
	 	params.put("bankcity", 			bankcity);
	 	params.put("manualsettle", 		manualsettle);
	 	
	 	log.info(" registerAccount --> params >>>>>>>>>>> : " + params.toString());
        
		Map<String, String> result = ZGTService.registerAccount(params);
		String code=result.get("code");
		if ("1".equals(result.get("code"))) {// 注册成功
			if (customertype != null && "PERSON".equalsIgnoreCase(customertype)) {// 个人用户
				member.setLedgerno(result.get("ledgerno"));
				member.setName(signedname);
				this.memberService.update(member);
			} else if (customertype != null && "ENTERPRISE".equalsIgnoreCase(customertype)) {// 企业用户
				tenant.setLedgerno(result.get("ledgerno"));
				this.tenantService.update(tenant);
			}
			// 保存会员银行卡信息
			memberBank.setCardNo(bankaccountnumber);
			memberBank.setDepositBank(bankname);// 开户行
			memberBank.setDepositUser(accountname);// 开户名
			memberBank.setMember(member);
			memberBank.setRepaymentDay(new Integer(riskreserveday));// 提现周期??
			memberBank.setTenant(tenant);
			memberBank.setType(MemberBank.Type.debit);// 保存为借记卡
			memberBank.setValidity(new Date());
			memberBank.setBankCity(bankcity);// 开户市
			memberBank.setBankProvince(bankprovince);// 开户省
			try {
				this.memberBankService.save(memberBank);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {// 注册失败
			throw new BizException("银行卡绑定失败");
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> updateAccount(Map<String, String> params) throws BizException {
		
		Member member = null;
		Tenant tenant = null;
		String customertype 		= params.get("customertype");// 前台传入：PERSON-个人；ENTERPRISE-企业
		String requestid 			= snDao.generate(Sn.Type.yeepayUpdate);// 修改请求号
        String ledgerno 			= null;// 子账户商编
        String bankaccountnumber 	= params.get("bankaccountnumber");// 银行卡号
        String bankname 			= params.get("bankname");// 开户行
        String accountname 			= params.get("accountname");// 开户名
        String bankaccounttype      = null;// 银行卡类别
        String bankprovince         = params.get("bankprovince");// 开户省
        String bankcity             = params.get("bankcity");// 开户市
        String minsettleamount      = "100";// 起结金额
        String riskreserveday       = "1";// 结算周期
        String manualsettle         = "Y";// 自助结算 TODO 
        String callbackurl          = params.get("callbackurl");// 后台回调地址 TODO 后台通知后再向易宝回写"SUCCESS"
        String bindmobile          	= null;// 绑定手机号
        String bankId               = params.get("bankId");
        params.remove("bankId");
        
        String customerType = params.get("customertype");// 商户类别
        if ("PERSON".equalsIgnoreCase(customerType)) {// 个人
        	member = this.memberService.find(new Long(params.get("memberId")));
        	ledgerno = member.getLedgerno();
        	bankaccounttype = "PrivateCash";
        	manualsettle = "Y";
        	bindmobile = member.getMobile();
        } else if ("ENTERPRISE".equalsIgnoreCase(customerType)) {// 企业
        	// 获取企业信息表的数据
        	tenant = this.tenantService.find(new Long(params.get("tenantId")));
        	ledgerno = tenant.getLegalRepr();
        	bindmobile = tenant.getTelephone();
        	minsettleamount = "10000";// TODO
        	if(params.get("name")!=null&&!(params.get("name").equals(""))){
				accountname=params.get("name");
			}else{
				accountname = tenant.getName();
			}
        	bankaccounttype = "PublicCash";
        	manualsettle = "N";// TODO
        	
        }
        
	 	params.put("requestid", 		requestid);
	 	params.put("ledgerno", 			ledgerno);
	 	params.put("bankaccountnumber",	bankaccountnumber);
	 	params.put("bankname",	 		bankname);
	 	params.put("accountname", 		accountname);
	 	params.put("bankaccounttype", 	bankaccounttype);
	 	params.put("bankprovince", 		bankprovince);
	 	params.put("bankcity", 			bankcity);
	 	params.put("minsettleamount", 	minsettleamount);
	 	params.put("riskreserveday", 	riskreserveday);
	 	params.put("manualsettle", 		manualsettle);
	 	params.put("callbackurl", 		callbackurl);
	 	params.put("bindmobile", 		bindmobile);
		
	 	log.info(" updateAccount --> params >>>>>>>>>>> : " + params.toString());
	 	
		Map<String, String> result = ZGTService.updateAccount(params);
		
		if ("1".equals(result.get("code"))) {// 修改成功
			// 更新会员银行卡信息
			MemberBank memberBank = new MemberBank();
			memberBank.setCardNo(bankaccountnumber);
			memberBank.setDepositBank(bankname);// 开户行
			memberBank.setDepositUser(accountname);// 开户名
			memberBank.setTenant(tenant);
			memberBank.setRepaymentDay(new Integer(riskreserveday));// 提现周期??
			memberBank.setTenant(tenant);
			memberBank.setType(MemberBank.Type.debit);// 保存为借记卡
			memberBank.setValidity(new Date());
			memberBank.setBankCity(bankcity);// 开户市
			memberBank.setBankProvince(bankprovince);// 开户省
			memberBank.setBankId(new Long(bankId));
			memberBank.setRequestId(requestid);
			try {
				this.memberBankService.save(memberBank);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {// 修改失败
			throw new BizException("修改银行卡绑定失败");
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> orderPayment(Map<String, String> params) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		
		String orderNo = params.get("orderNo");
		Order order = this.orderService.findBySn(orderNo);
		Member member = order.getMember();// 获取会员信息
		
	 	String requestid			= params.get("requestid");// 商户订单号
	 	String amount				= params.get("amount");// 商户订单金额
	 	String assure				= "0";// 是否需要担保 
	 	String productname			= "指帮内购";// 商品名称
	 	String productcat			= "指帮内购";// 商品类别
	 	String productdesc			= "指帮内购";// 商品描述
	 	String divideinfo			= "";// 分账信息
	 	String callbackurl			= params.get("callbackurl");// 
	 	String webcallbackurl		= "";// 
	 	String bankid				= "";// 银行编号
	 	String period				= "";// 担保有效期时间
	 	String memo		  			= "指帮内购";// 商户备注
	 	String payproducttype		= "ONEKEY";// 支付方式
	 	String userno		  		= member.getUsername();// 用户标识
	 	String isbind		  		= "";// 
	 	String bindid		  		= "";// 
	 	String ip		  			= member.getLoginIp();// 用户IP地址
	 	String cardname				= params.get("cardname");// 持卡人姓名
	 	String idcard				= params.get("idcard");// 身份证号
	 	String idcardtype			= params.get("idcardtype");// 银行卡号
	 	String bankcardnum			= "";// 
	 	String mobilephone			= "";// 
	 	String cvv2					= "";// 
	 	String expiredate			= "";// 
	 	String mcc					= "";// 
	 	String areacode				= "";// 
		
	 	params.put("requestid", 		requestid );		  
	 	params.put("amount", 			amount );				  
	 	params.put("assure", 			assure );				  
	 	params.put("productname",		productname );	  
	 	params.put("productcat", 		productcat );		  
	 	params.put("productdesc", 		productdesc );	  
	 	params.put("divideinfo", 		divideinfo ); 		  
	 	params.put("callbackurl", 		callbackurl );	  
	 	params.put("webcallbackurl", 	webcallbackurl );
	 	params.put("bankid",			bankid );				  
	 	params.put("period", 			period );				  
	 	params.put("memo", 				memo );					  
	 	params.put("payproducttype", 	payproducttype );  
	 	params.put("userno", 			userno );				  
	 	params.put("isbind", 			isbind );				  
	 	params.put("bindid", 			bindid );				  
	 	params.put("ip", 				ip );						  
	 	params.put("mcc", 				mcc );				  
	 	params.put("areacode", 			areacode );				  
	 	params.put("cardname", 			cardname );				  
	 	params.put("idcard", 			idcard );
	 	params.put("idcardtype", 		idcardtype );
	 	params.put("mobilephone", 		mobilephone );
	 	params.put("bankcardnum", 		bankcardnum );
	 	params.put("cvv2", 				cvv2 );
	 	params.put("expiredate", 		expiredate );
	 	
	 	log.info(" orderPayment --> params >>>>>>>>>>> : " + params.toString());
	 	
		Map<String, String> result = ZGTService.paymentRequest(params);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> divide(Map<String, String> params) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		
		String orderNo = params.get("orderNo");
		Order order = this.orderService.findBySn(orderNo);
		String ledgerno = order.getTenant().getLedgerno();// 获取需要分账的子账户编号
		
	 	String requestid			= snDao.generate(Sn.Type.yeepayDivide);// 分账请求号，每次不一样
	 	String orderrequestid		= params.get("paymentSn");// 支付单号
	 	if (order.getChargeAmt() == null) {
	 		order.setChargeAmt(new BigDecimal(0.00));
	 	}
//	 	BigDecimal chargeAmt = order.getAmountPaid().subtract(order.getChargeAmt());
	 	BigDecimal chargeAmt = order.getAmountPaid();
	 	String divideinfo			= ledgerno + ":AMOUNT" + chargeAmt.setScale(2, BigDecimal.ROUND_HALF_UP);// 订单金额 - 佣金（即表示易宝的手续费由平台来支出）
	 	
	 	params.put("requestid", 	  	requestid);		  
	 	params.put("orderrequestid", 	orderrequestid);		  
	 	params.put("divideinfo", 	  	divideinfo);				  
	 	
	 	log.info(" divide --> params >>>>>>>>>>> : " + params.toString());
	 	
		Map<String, String> result = ZGTService.divide(params);
		if ("1".equals(result.get("code"))) {// 分账成功
			// 不做处理
		} else {// 分账失败
			// 不做处理
		}
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> refund(Map<String, String> params,Refunds refunds) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		String orderNo = refunds.getOrder().getSn();
//		orderNo = "201509021471111";
		Order order = refunds.getOrder();
//		Refunds refunds = null;
//		String sn=order.getSn();
//	 	Set<Refunds> refundsSet = order.getRefunds();
//		if (refundsSet != null && refundsSet.iterator() != null) {
//			while (refundsSet.iterator().hasNext()) {
//				refunds = refundsSet.iterator().next();
//				//break;
//			}
//		}
		
		String ledgerno = order.getTenant().getLedgerno();// 获取需要分账的子账户编号
	 	String requestid			= snDao.generate(Sn.Type.yeepayRefund);// 退款请求号，通过sn去动态生成，每次必须唯一
	 	String orderrequestid=null;
	 	if(order.getPayments()!=null&&order.getPayments().iterator()!=null && order.getPayments().iterator().hasNext()){
	 		 orderrequestid		= order.getPayments().iterator().next().getSn();// 商户订单号 ：取payment表的sn
	 	}else{
	 		 orderrequestid		= "";
	 	}
	 	
//	 	String amount				= order.getAmountPaid().toString();// 
	 	String amount				= refunds.getAmount()
	 			.setScale(2, BigDecimal.ROUND_HALF_UP).toString();// 退款总金额：取退款单的金额（有可能部分退款）
//	 	String divideinfo			= ledgerno + ":AMOUNT" + refunds.getAmount()
//	 			.subtract(order.getOrderSettlement().getReturnCharge());// 退款单金额 - 佣金
	 	
	 	String divideinfo			= ledgerno + ":AMOUNT" + refunds.getAmount()
	 			.setScale(2, BigDecimal.ROUND_HALF_UP).toString();// 退款单金额 - 佣金
	 	
	 	String confirm				= "1";// 
	 	String memo					= "订单号【" + orderNo + "】退款，子账户【" + ledgerno + "】退款" 
	 			+ refunds.getAmount().subtract(order.getOrderSettlement().getReturnCharge()) + "元";// 
	 	
	 	params.put("requestid", 	  	requestid);				  
	 	params.put("orderrequestid",	orderrequestid);		  
	 	params.put("amount", 	  	  	amount);				  
	 	params.put("divideinfo", 	  	divideinfo);				  
	 	params.put("confirm", 	  	  	confirm);				  
	 	params.put("memo", 		  		memo);					  
	 	
	 	log.info(" refund --> params >>>>>>>>>>> : " + params.toString());
	 	
		Map<String, String> result = ZGTService.refund(params);
		
		if ("1".equals(result.get("code"))) {// 退款成功后的操作
			try {
				this.refundsService.refurns(refunds, order.getMember().getUsername());
			} catch (OrderException e) {
				throw new BizException(result.get("msg"));
			}
		} else {// 退款失败后的操作
			
		}
		
		return result;
	}

	/**
	 * 提现
	 */
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> cashTransfer(Map<String, String> params) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		String memberId				= params.get("memberId");// 店长会员ID
		String requestid			= snDao.generate(Sn.Type.yeepayCash);// 提现请求号
		String ledgerno				= params.get("ledgerno");// 子账户编号
		String amount				= params.get("amount");// 提现金额
		String callbackurl			= params.get("callbackurl");// 后台回调地址
		
		params.put("requestid", 		requestid);// 提现请求号
		params.put("ledgerno", 			ledgerno);// 
		params.put("amount", 			amount);
		params.put("callbackurl", 		callbackurl);
		
		log.info(" cashTransfer --> params >>>>>>>>>>> : " + params.toString());
		
		Map<String, String> result = ZGTService.cashTransfer(params);
		
		if ("1".equals(result.get("code"))) {// 提现申请成功
			Member member = this.memberService.find(new Long(memberId));
//			Owner  owner=ownerServerice.getOwner(new Long(memberId));
//			owner.setAccountBalance(owner.getAccountBalance().subtract(new BigDecimal(amount)));
//			ownerServerice.update(owner);//更新账户余额
			//添加一条提现流水记录
			OwnerCashDetail ownerCashDetail=new OwnerCashDetail();
			ownerCashDetail.setCashAmt(new BigDecimal(amount));
//			ownerCashDetail.setCashDate(new Date());
			Iterator iterator = member.getMemberBanks().iterator();
			MemberBank memberBank = new MemberBank();
			while(iterator.hasNext()) {
				memberBank = (MemberBank)iterator.next();
			}
			ownerCashDetail.setCashrequestid(requestid);
			ownerCashDetail.setMemberBankId(memberBank.getId());
			ownerCashDetail.setMemberId(member.getId());
//			ownerCashDetail.setStatus(Buss);
//			ownerCashDetailService.save(ownerCashDetail);
			
			// 记录一条提现状态为处理中的流水，并且扣减账户余额
			ownerServerice.accountCash(ownerCashDetail);
			
		} else {
			throw new BizException("提现失败");
		}
		
		return result;
	}

	/**
	 * 转账
	 */
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> transfer(Map<String, String> params) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		// 1、ledgerno非空sourceledgerno为空时：主账户转子账户（customernumber → ledgerno）
		// 2、ledgerno为空sourceledgerno非空时：子账户转主账户（sourceledgerno → customernumber）
	 	String requestid			= snDao.generate(Sn.Type.yeepayTransfer);// 转账请求号
	 	String ledgerno				= params.get("ledgerno");// 子账户商户编号
	 	String sourceledgerno		= params.get("sourceledgerno");// 子账户商编
	 	String amount				= params.get("amount");// 转账金额
	 	
	 	params.put("requestid", 		requestid);
	 	params.put("ledgerno", 			ledgerno);
	 	params.put("amount", 			amount);
	 	params.put("sourceledgerno", 	sourceledgerno);
	 	
	 	log.info(" transfer --> params >>>>>>>>>>> : " + params.toString());
		
		Map<String, String> result = ZGTService.transfer(params);
		return result;
	}
	
	/**
	 * 转账
	 */
	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	public Map<String, String> paymentQuery(Map<String, String> params) throws BizException {
		if (params == null) {
			params = new HashMap<String, String>();
		}
	 	String requestid			= params.get("requestid");// 商户订单号
	 	params.put("requestid", 		requestid);
	 	
	 	log.info(" transfer --> params >>>>>>>>>>> : " + params.toString());
		
		Map<String, String> result = ZGTService.transfer(params);
		
		if ("1".equals(result.get("code"))) {
			requestid = result.get("requestid");
			String amount = result.get("amount");
			String status = result.get("status");// 订单状态
			String ordertype = result.get("ordertype");// 订单类型
			String fee = result.get("fee");// 商户手续费
			if ("SUCCESS".equals(status)) {// 订单支付成功
				// 调用通知的方法
			}
		}
		return result;
		
	}
	
}
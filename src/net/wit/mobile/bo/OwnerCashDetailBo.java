/**
 * Copyright 2015 Software innovation and R & D center. All rights reserved.
 * File Name: OwnerCashDetailBo.java
 * Encoding UTF-8
 * Version: 0.0.1
 * History:	2015年9月17日
 */
package net.wit.mobile.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author: yangyang.wu
 * @version Revision: 0.0.1
 * @Date：2015年9月17日
 */
public class OwnerCashDetailBo {
   private String id;
   private String cashAmt;
   private String cashDate;
   private String memberBankId;
   private String memberId;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getCashAmt() {
	return cashAmt;
}
public void setCashAmt(String cashAmt) {
	this.cashAmt = cashAmt;
}
public String getCashDate() {
	return cashDate;
}
public void setCashDate(String cashDate) {
	this.cashDate = cashDate;
}
public String getMemberBankId() {
	return memberBankId;
}
public void setMemberBankId(String memberBankId) {
	this.memberBankId = memberBankId;
}
public String getMemberId() {
	return memberId;
}
public void setMemberId(String memberId) {
	this.memberId = memberId;
}
   
   
}

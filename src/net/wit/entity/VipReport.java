package net.wit.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Entity - Vip统计
 * @author Teddy
 * @version 1.0
 */
public class VipReport extends BaseEntity {

	private static final long serialVersionUID = -7519486823153844426L;
	
	/** vip Id */
	private BigInteger vipId ;

	/** vip姓名 */
	private String vipName;

	/** 销售量 */
	private BigDecimal volume;

	/** 销售额 */
	private BigDecimal amount;
	
	public BigInteger getVipId() {
		return vipId;
	}

	public void setVipId(BigInteger vipId) {
		this.vipId = vipId;
	}

	public String getVipName() {
		return vipName;
	}

	public void setVipName(String vipName) {
		this.vipName = vipName;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
    
}
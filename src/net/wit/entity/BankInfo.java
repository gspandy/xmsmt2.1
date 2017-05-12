/**
 *====================================================
 * 文件名称: BankInfo.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年9月4日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName: BankInfo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月4日 下午5:26:38
 */
@Entity
@Table(name = "xx_bank_info")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_bank_info_sequence")
public class BankInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/** 卡号 */
	private String cardNoHead;

	/** 开户行 */
	private String depositBank;

	/**
	 * 银行卡号开头
	 * @return 银行卡号开头
	 */
	@NotBlank
	@JsonProperty
	@Column(nullable = false)
	public String getCardNoHead() {
		return cardNoHead;
	}

	/**
	 * 银行卡号开头
	 * @param type 银行卡号开头
	 */
	public void setCardNoHead(String cardNoHead) {
		this.cardNoHead = cardNoHead;
	}

	/**
	 * 卡号
	 * @return 卡号
	 */
	@NotBlank
	@JsonProperty
	@Column(nullable = false, updatable = false, length = 64)
	public String getDepositBank() {
		return depositBank;
	}

	/**
	 * 卡号
	 * @param type 卡号
	 */
	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}

}

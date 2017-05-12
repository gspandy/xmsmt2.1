package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 实体类 - 短信发送
 * ============================================================================
 * 版权所有 2008-2010 rsico.com,并保留所有权利。
 * ----------------------------------------------------------------------------
 * 提示：在未取得rsico商业授权之前,您不能将本软件应用于商业用途,否则rsico将保留追究的权力。
 * ----------------------------------------------------------------------------
 * 官方网站：http://www.rsico.com
 * ----------------------------------------------------------------------------
 * KEY: SHOPUNION5CAA6FDAF2A5662FADB5F15AD00B2070
 * ============================================================================
 */

@Entity
@Table(name = "xx_smssend")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_smssend_sequence")
public class SmsSend extends BaseEntity {

	private static final long serialVersionUID = -8541323033439515141L;
	public static final int SMS_CONTENT_MAX_LENGTH = 255;// 短信最大长度
	public static final int SMS_MOBILES_MAX_LENGTH = 200;// 一次发送的最大手机号数

	public enum Status {
		wait, send, Error
	}
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 系统 */
		system,

		/** 会员 */
		member
	}
	
	/** 类型 */
	private Type type;
	private String mobiles;// 最机号，最多200个
	private String content;// 发送内容，最长255字符，127个汉字
	private Integer priority;// 发送优先级  1-5
	private String charset;// 字符集 GBK
	private Integer count; //短信总数
	private BigDecimal fee;// 发送费用
	private Status status;// 发送状态 0待发送 1发送成功，其他失败
	private String descr;// 发送状态说明

	private Member member;// 会员
	
	@Lob
	@Column(nullable = false)
	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}
	
	@Column(nullable = false)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(nullable = false)
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	@Column(nullable = false)
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Column(nullable = false, precision = 21, scale = 6)
	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	@Column(nullable = false)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Column(nullable = true)
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}


	@Column(nullable = true)
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	@Column(nullable = false, updatable = false)
	public Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Type type) {
		this.type = type;
	}

	
}
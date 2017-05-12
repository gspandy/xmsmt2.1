package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * <p>Title: 第三方账号绑定</p>
 *
 * <p> Description: </p>
 * <p>Copyright: www.inspur.com</p>
 *
 * <p>Company: www.inspur.com</p>
 *
 * @author chenqifu
 * @version 1.0
 * @2013-7-31
 */

@Entity
@Table(name = "xx_bind_user")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_bind_user_sequence")
public class BindUser extends BaseEntity{

	private static final long serialVersionUID = -2735035962597250149L;
	
	/**
	 * 类型
	 */
	public enum Type {

		/** 新商盟 */
		_xsm,

		/** 19e */
		_19e,
		/** 微信 */
		_wx,
		/** V-BOX */
		_vbox
	}
	
	/**
	 *用户名 
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 类型
	 */
	private Type type;
	
	/**
	 * 绑定的会员
	 */
	private Member member;

	@NotNull
	@Column(nullable=false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.toUpperCase();
	}

	@NotNull
	@Column(nullable=false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotNull
	@Column(nullable=false)
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(nullable=false)
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
	
}

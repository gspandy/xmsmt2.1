package net.wit.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

/**
 * <p>Title: 实名认证 </p>
 *
 * <p> Description: </p>
 * <p>Copyright: www.rsico.cn</p>
 *
 * <p>Company: www.rsico.cn</p>
 *
 * @author zhangsr
 * @version 1.0
 * @2013-7-31
 */

@Entity
@Table(name = "xx_idcard")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_idcard_sequence")
public class Idcard extends BaseEntity{

	private static final long serialVersionUID = -541766727343251441L;
	
	/**
	 * 认证状态
	 */
	public enum AuthStatus {

		/** 未认证 */
		none,

		/** 等待审核 */
		wait,

		/** 认证通过*/
		success,
		
		/** 认证拒绝*/
		fail
	}
	/**
	 * 认证状态
	 */
	@Expose
	private AuthStatus authStatus;
	
	/**
	 * 身份证号
	 */
	@Expose
	private String no;
	
	/**
	 * 发证日期
	 */
	@Expose
	private Date beginDate;

	/**
	 * 有效日期
	 */
	@Expose
	private Date endDate;

	/**
	 * 地址
	 */
	@Expose
	private String address;

	/**
	 * 正面图片
	 */
	@Expose
	private String pathFront;
	
	/**
	 * 反面图片
	 */
	@Expose
	private String pathBack;

	/**
	 * 正面图片
	 */
	private ProductImage imageFront;
	
	/**
	 * 反面图片
	 */
	private ProductImage imageBack;

	public AuthStatus getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(AuthStatus authStatus) {
		this.authStatus = authStatus;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPathFront() {
		return pathFront;
	}

	public void setPathFront(String pathFront) {
		this.pathFront = pathFront;
	}

	public String getPathBack() {
		return pathBack;
	}

	public void setPathBack(String pathBack) {
		this.pathBack = pathBack;
	}

	@Transient
	public ProductImage getImageFront() {
		return imageFront;
	}

	public void setImageFront(ProductImage imageFront) {
		this.imageFront = imageFront;
	}

	@Transient
	public ProductImage getImageBack() {
		return imageBack;
	}

	public void setImageBack(ProductImage imageBack) {
		this.imageBack = imageBack;
	}
	
}

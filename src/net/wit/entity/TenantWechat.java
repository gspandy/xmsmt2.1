/**
 *====================================================
 * 文件名称: TenantWechat.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年9月10日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @ClassName: TenantWechat
 * @Description: 商家微信公众号
 * @author Administrator
 * @date 2014年9月10日 下午5:29:56
 */
@Embeddable
public class TenantWechat implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 微信公众号ID */
	private String appId;

	/** 微信公众号密钥 */
	private String appSecret;

	/** 微信公众号ID */
	@Column(name = "wechat_app_id")
	public String getAppId() {
		return appId;
	}

	/** 微信公众号ID */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/** 微信公众号密钥 */
	@Column(name = "wechat_app_secret")
	public String getAppSecret() {
		return appSecret;
	}

	/** 微信公众号密钥 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

}

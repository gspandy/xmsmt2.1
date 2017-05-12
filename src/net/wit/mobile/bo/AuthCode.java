package net.wit.mobile.bo;

public class AuthCode {



	private String tel;
	
	private String code;
	
	private Long currentTimeMillis;
	
	
	public static final String TEL= "tel";
	
	public static final String CODE= "code";
	
	public static final String CURRENTTIMEMILLIS = "currentTimeMillis";
	
	public AuthCode(String tel, String code, Long currentTimeMillis) {
		super();
		this.tel = tel;
		this.code = code;
		this.currentTimeMillis = currentTimeMillis;
	}

	/**
	 * 
	 */
	public AuthCode() {
		super();
		// TODO Auto-generated constructor stub
	}



	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getCurrentTimeMillis() {
		return currentTimeMillis;
	}

	public void setCurrentTimeMillis(Long currentTimeMillis) {
		this.currentTimeMillis = currentTimeMillis;
	}
}

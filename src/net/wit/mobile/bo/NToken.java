package net.wit.mobile.bo;

import java.io.Serializable;


/**
  *标签表
  */


public class NToken implements Serializable{

	private String ntoken;
	private String memberId; //会员ID
	private String tel;  //电话号码
	private String expiredTime; //token过期时间
	private String isShopKeeper; //是否为店主
	private String tenantId;  //企业ID
	private String recommandId;  //推荐人ID
    private String headImg;  //头像
    private String nickName;  //昵称
    private String ledgerno;  //子账户
    private String realName;  //真实姓名
    
	
	public static final String NTOKEN_KEY= "token";
	public static final String TEL_KEY="tel";
	public static final String USERID_KEY= "memberId";
	public static final String EXPIREDTIME_KEY= "expiredTime";
	public static final String RECOMMANDID= "recommandId";
    public static final String IS_SHOP_KEEPER = "isShopKeeper";
    public static final String TENANT_ID = "tenantId";
    public static final String HEAD_IMG = "headImg";
    public static final String NICKNAME = "nickName";
    public static final String LEDGERNO = "ledgerno";
    public static final String REALNAME = "realName";
	
	private static final long serialVersionUID = -6011241820070393952L;

    public String getShopKeeper() {
        return isShopKeeper;
    }

    public void setShopKeeper(String shopKeeper) {
        isShopKeeper = shopKeeper;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public NToken(String tel,String nickName){
    	this.tel = tel;
    	this.nickName = nickName;
		this.memberId ="";
		this.isShopKeeper = "0";
		this.tenantId = "";
		this.recommandId = "";
		this.headImg = "";
		this.ledgerno = "";
		this.realName="";
    }
    
    /**
     * 初次登陆的构造
     * @param tel
     * @param nickName
     */
    public NToken(String tel,String nickName,String headImage){
    	this.tel = tel;
    	this.nickName = nickName;
		this.memberId ="";
		this.isShopKeeper = "0";
		this.tenantId = "";
		this.recommandId = "";
		this.headImg = headImage;
		this.ledgerno = "";
		this.realName="";
    }
    



    public NToken(String ntoken, String memberId, String tel, String expiredTime,
		String isShopKeeper, String tenantId, String recommandId,
		String headImg, String nickName,String ledgerno,String realName) {
	super();
	this.ntoken = ntoken;
	this.memberId = memberId;
	this.tel = tel;
	this.expiredTime = expiredTime;
	this.isShopKeeper = isShopKeeper;
	this.tenantId = tenantId;
	this.recommandId = recommandId;
	this.headImg = headImg;
	this.nickName = nickName;
	this.ledgerno = ledgerno;
	this.realName=realName;
}

	public NToken(String memberId, String tel,
                  String isShopKeeper, String tenantId,
                  String recommandId, String headImg, String nickName,String ledgerno,String realName) {
        super();
        this.memberId = memberId;
        this.tel = tel;
        this.isShopKeeper = isShopKeeper;
        this.tenantId = tenantId;
        this.recommandId = recommandId;
        this.headImg = headImg;
        this.nickName = nickName;
        this.ledgerno = ledgerno;
        this.realName = realName;
    }


    public NToken(){
    	
    }

	public String getNtoken() {
		return ntoken;
	}




	public void setNtoken(String ntoken) {
		this.ntoken = ntoken;
	}




	public String getMemberId() {
		return memberId;
	}




	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}




	public String getTel() {
		return tel;
	}




	public void setTel(String tel) {
		this.tel = tel;
	}




	public String getExpiredTime() {
		return expiredTime;
	}




	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}




	public String getIsShopKeeper() {
		return isShopKeeper;
	}




	public void setIsShopKeeper(String isShopKeeper) {
		this.isShopKeeper = isShopKeeper;
	}




	public String getTenantId() {
		return tenantId;
	}




	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}




	public String getRecommandId() {
		return recommandId;
	}




	public void setRecommandId(String recommandId) {
		this.recommandId = recommandId;
	}

	public String getLedgerno() {
		return ledgerno;
	}

	public void setLedgerno(String ledgerno) {
		this.ledgerno = ledgerno;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	

	

	
}

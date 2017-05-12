package net.wit.mobile.service;


import net.wit.entity.Member;
import net.wit.entity.TenantShopkeeper;
import net.wit.mobile.bo.NToken;

public interface INTokenBS {
	public NToken get(String keyId) ;
	/**
	 * 是店主的token
	 * @param member
	 * @param ts
	 * @return
	 */
	public String createToken(Member member,TenantShopkeeper ts);
	/**
	 * 不是店主的token
	 * @param member
	 * @return
	 */
	public String createNoShopToken(Member member);
	
	public boolean isVaild(String token);
    
	public boolean logout(String token);

    public String registerMemberWithToken(Member member, NToken token);

    public void becameShopKeeper(NToken nToken, Member member, String tenantId,  String recommandId) throws Exception;
    
    public String registerToken(NToken token);
    
    public void updateToken(NToken token);

}

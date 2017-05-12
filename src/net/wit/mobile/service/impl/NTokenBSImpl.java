package net.wit.mobile.service.impl;


import net.wit.entity.Member;
import net.wit.entity.TenantShopkeeper;
import net.wit.mobile.bo.NToken;
import net.wit.mobile.controller.TicketController;
import net.wit.mobile.dao.INtokenDao;
import net.wit.mobile.service.INTokenBS;
import net.wit.mobile.util.ShortUUID;
import net.wit.service.MemberService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.impl.BaseServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Service("ntokenBS")
public class NTokenBSImpl extends BaseServiceImpl implements INTokenBS {

	@Autowired
	private INtokenDao tokenDao;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TenantShopkeeperService tenantShopkeeperService;
	
	private static final int expireSeconds = 604800;
	private static final String expireTimeFormat = "yyyyMMddHHmmss";
	private Logger log = LoggerFactory.getLogger(NTokenBSImpl.class);
	
	
    public void becameShopKeeper(NToken nToken, Member member, String tenantId,  String recommandId) throws Exception{
    	if(StringUtils.isEmpty(recommandId)){
    		tenantShopkeeperService.beShopKeeper(new Long(tenantId), member);
    	}else{
    		tenantShopkeeperService.beShopKeeper(new Long(tenantId), member, new Long(recommandId));
    	}
    	memberService.update(member);
        nToken.setExpiredTime(getNewExpTime());
        nToken.setTenantId(tenantId);
        nToken.setRecommandId(recommandId);
        tokenDao.update(nToken);
    }

	private String getNewExpTime() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, expireSeconds);
		Date expireTime = cal.getTime();
		return new SimpleDateFormat(expireTimeFormat).format(expireTime);
	}

	@Override
	public NToken get(String keyId) {
		return tokenDao.get(keyId);
	}

    public String registerMemberWithToken(Member member, NToken token) {
        memberService.save(member);
        token.setMemberId(String.valueOf(member.getId()));
        return registerToken(token);
    }

	@Override
	public String createToken(Member member,TenantShopkeeper ts) {
		String memberId = member.getId() == null ? "" : member.getId() + "";
		String tel = member.getMobile();
		String isShopKeeper = ts.getIsShopkeeper().equals(TenantShopkeeper.IsShopkeeper.no)?"0":"1";
		String tenantId = ts.getTenant().getId()==null?"":ts.getTenant().getId()+"";
		String recommandId = ts.getRecommendMember()==null?"":ts.getRecommendMember().getId()+"";
		String headImage = member.getHeadImg()==null?"":member.getHeadImg();
		String nickName  =member.getNickName()==null?"":member.getNickName();
		String ledgerno   =member.getLedgerno()==null?"":member.getLedgerno();
		String realName = member.getName() ==null?"":member.getName();
		NToken token = new NToken(memberId,
				tel,
				isShopKeeper,
				tenantId, 
				recommandId,headImage,nickName,ledgerno,realName);
        return this.registerToken(token);
	}
	
	public String createNoShopToken(Member member){
		NToken token = new NToken(member.getId()+"",
				member.getMobile(),
				"0",
				"", 
				"",
				member.getHeadImg()==null?"":member.getHeadImg(),
				member.getNickName()==null?"":member.getNickName(),
						member.getLedgerno()==null?"":member.getLedgerno(),
								 member.getName() ==null?"":member.getName());
		 return this.registerToken(token);
	}

    public boolean logout(String token) {
        boolean flag = true;
        try {
            tokenDao.remove(token);
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
	
	public boolean isVaild(String token){
		NToken tokenBo = this.tokenDao.get(token);
		if(tokenBo==null)return false;
		SimpleDateFormat sdf = new SimpleDateFormat(expireTimeFormat);
		Date now = new Date();
		try {
			log.error("========="+tokenBo.getExpiredTime()+"");
			if (sdf.parse(tokenBo.getExpiredTime()).compareTo(now)>0){
				tokenBo.setExpiredTime(getNewExpTime());
				log.error("========="+getNewExpTime()+"");
				return  this.tokenDao.update(tokenBo);  //未过期就更新过期时间;
			}else {
				this.tokenDao.remove(token);  //过期删除掉该token信息在 redis 中
				return false;
			}
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public String registerToken(NToken token) {
		String ntoken = ShortUUID.genId();
		token.setNtoken(ntoken);
		token.setExpiredTime(this.getNewExpTime());
		this.tokenDao.add(token);
		return ntoken;
	}
	
	public void updateToken(NToken token){
		NToken ntoken = this.tokenDao.get(token.getNtoken());
		if(ntoken== null){
			this.tokenDao.add(ntoken);
		}else tokenDao.update(ntoken);
	}
	



	

}

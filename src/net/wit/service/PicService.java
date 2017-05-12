package net.wit.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.wit.mobile.bo.NToken;

import org.springframework.web.multipart.MultipartFile;

import net.wit.entity.Member;
import net.wit.entity.Pic;
import net.wit.entity.Tenant;

public interface PicService extends BaseService<Pic, Long>{

	public Pic uploadPic(Long tenantId,String picType,MultipartFile file);
	
	public Pic getPicByPicId(Long picId);
	
	public String getDefaultHeadImage(Member member);

    public Pic uploadImage(NToken nToken ,MultipartFile file);
    /**
     * 店长邀请买家图片
     * @param tenant
     * @return
     */
    public String getShopKeeperInviteImage(Tenant tenant );
    
    public Map<String,Pic> findMap(Tenant tenant);
    /**
     * 邀请函图片
     * @param tenant
     * @return
     */
    public String getTenantInviteImage(Tenant tenant );
    
    /**
     * 券分享页图片
     * @param tenant
     * @return
     */
    public String getTickShareImageByTenant(Tenant tenant );

    /**
     *会员查询邀请函列表图片
     * @param tenant
     * @return
     */
	public String getMemberInvitesImage(Tenant tenant);
	/**
	 * 获取会员开店图片
	 * @return
	 */
	public String getMemberOpenShopImage();
}

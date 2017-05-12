package net.wit.service;

import java.util.Date;
import java.util.List;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Tenant;
import net.wit.entity.TicketCache;
import net.wit.entity.TicketSet;
import net.wit.vo.TicketCacheVO;

public interface TicketCacheService  extends BaseService<TicketCache, Long>{

	/**
	 * 获取某店主还未收取的内购券缓存信息
	 * @param memberId
	 * @return
	 */
    public List<TicketCache>	getNoReceiveTickCacheByMemberId(Long memberId);

    /**
     * 获取某店主已经收取的内购券缓存信息
     * @param memberId
     * @return
     */
    public List<TicketCache>	getReceivedTickCacheByMemberId(Long memberId);
    
    
    /**
     * 单独事务批量提交内购券缓存
     * @param list
     */
    public void batchSubmitTicketCache(List<TicketCache> list);
    
    
    /**
     * 根据内购券发放设置批量发放
     * @param ticketSet
     */
    public void batchGenTicketCacheByTicketSet(TicketSet ticketSet);
    /**
     * 新用户发放内购券
     * @param ticketSet
     * @param memberId
     */
    public void newShopKeeperSendTicketCache(TicketSet ticketSet, Long memberId) ;
    
    /**
     * 选择单用户或多用户发券
     * @param memberIdList
     * @param tenantId
     * @param num
     */
    public void batchSaveTicketCacheByMemberListOnManual(List<Long> memberIdList,Long tenantId,int num);
    
    /**
     * 手动全部发放
     * @param memberIdList
     * @param tenantId
     * @param num
     */
    public void sendTicketCacheToAllShopKeeperOnManual(Long tenantId, int num) ;
    
    /**
     * 获取全部未生成的内购券
     * @param memberId
     */
    public void toReceiveTicket(Long memberId);
    
    public Long getshopkeeperNoUseCount(Long memberId,String receiveStatus);
    /**
     * 判断券缓冲表中是否存在该用户数据
     * @param memberId
     * @return
     */
    public boolean isTicketCacheExist(Long memberId);
/**
 * 根据企业发放所有的ticketCache；
 * @param tenant
 */
	public void batchGenTicketCacheByTicketSet(Tenant tenant);
	   
    /**
     * 发放券历史列表
     * @param tenantId 企业Id
     * @param startDate 开始时间
     * @param endDate	结束时间
     * @param sendModel 发放类型
     * @param name	店长姓名
     * @param pageable 分页
     * @return
     */
    public Page<TicketCacheVO> findTickeCacheByPage(Long tenantId,Date startDate,Date endDate,String sendModel,String name,Pageable pageable); 
    
    /**
     * 发放券历史列表
     * @param tenantId 企业Id
     * @param startDate 开始时间
     * @param endDate	结束时间
     * @param sendModel 发放类型
     * @param name	店长姓名
     * @param pageable 分页
     * @return
     */
    public List<TicketCacheVO> findTickeCacheByList(Long tenantId,Date startDate,Date endDate,String sendModel,String name); 

}

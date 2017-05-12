/**
 *====================================================
 * 文件名称: PromotionMemberDao.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月28日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.dao;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Member;
import net.wit.entity.Promotion;
import net.wit.entity.PromotionMember;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.PromotionMember.Status;

/**
 * @ClassName: PromotionMemberDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月28日 上午10:36:01
 */
public interface PromotionMemberDao extends BaseDao<PromotionMember, Long> {

	Page<PromotionMember> findPage(Promotion promotion, Status status, Pageable pageable);

	Page<PromotionMember> findJoinAuctionPage(Pageable pageable, Member member, Classify classify);
}

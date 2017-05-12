/**
 *====================================================
 * 文件名称: PromotionAuctionService.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年4月25日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.Member;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.Promotion;
import net.wit.entity.Promotion.Classify;
import net.wit.entity.Promotion.Type;
import net.wit.entity.PromotionMember;
import net.wit.entity.PromotionMember.Status;
import net.wit.entity.PromotionProduct;
import net.wit.entity.Receiver;
import net.wit.entity.ShippingMethod;
import net.wit.entity.Tag;

/**
 * @ClassName: PromotionAuctionService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年4月25日 下午3:02:02
 */
public interface PromotionAuctionService extends BaseService<Promotion, Long> {

	/** 提交会员当前出价信息 */
	public void submitAuction(Member member, Promotion promotion, BigDecimal currentPrice, BigDecimal proxyPrice, Receiver receiver) throws Exception;

	/** 取消参与人员参与的拍卖促销 */
	public void cancel(Promotion promotion, Long[] promotionMemberIds) throws Exception;

	/** 生成参与人员拍卖促销订单 */
	public void confirm(Promotion promotion, Long[] promotionMemberIds, ShippingMethod shippingMethod) throws Exception;

	/** 结束该拍卖促销 */
	public void finished(Promotion promotion, ShippingMethod shippingMethod) throws Exception;

	/** 撤销该拍卖促销活动 */
	public void rescind(Promotion promotion) throws Exception;

	public Page<PromotionMember> findPage(Promotion promotion, Status status, Pageable pageable);

	Page<PromotionProduct> findPage(Type type, Promotion promotion, ProductCategory productCategory, Area area, Brand brand, List<Tag> tags, Map<Attribute, String> attributeValue, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, OrderType orderType,
			Boolean periferal, Community community, Pageable pageable);

	public Page<PromotionMember> findJoinAuctionPage(Pageable pageable, Member member, Classify classify);

}

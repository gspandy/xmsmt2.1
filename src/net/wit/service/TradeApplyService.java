/**
 *====================================================
 * 文件名称: TradeApplyService.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年8月19日			Administrator(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.service;

import net.wit.entity.Member;
import net.wit.entity.Refunds;
import net.wit.entity.Returns;
import net.wit.entity.Trade;
import net.wit.entity.TradeApply;

/**
 * @ClassName: TradeApplyService
 * @Description: 子订单申请信息
 * @author Administrator
 * @date 2014年8月19日 下午1:44:50
 */
public interface TradeApplyService extends BaseService<TradeApply, Long> {

	/** 商家拒绝退货申请 */
	public void rejected(TradeApply apply);

	/** 商家同意退款申请 */
	public void refunds(Trade trade,Refunds refunds);

	/** 商家同意换货申请 */
	public void change(TradeApply apply);
	
	/** 商家同意退货申请 */
	public void returns(Member member,Trade trade,Returns returns,Refunds refunds);

}

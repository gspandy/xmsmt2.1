package net.wit.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * <p>Title: 游戏快充 </p>
 *
 * <p> Description: </p>
 * <p>Copyright: www.rsico.cn</p>
 *
 * <p>Company: www.rsico.cn</p>
 *
 * @author chenqifu
 * @version 1.0
 * @2013-7-31
 */

@Entity
@Table(name = "xx_game_price")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_game_price_sequence")
public class GamePrice extends BaseEntity{

	private static final long serialVersionUID = -541766727349251149L;
	
	/**
	 * 分类
	 */
	private int typeId;
	
	/**
	 * 分类名
	 */
	private String typeName;
	
	/**
	 * 业务类型
	 */
	private int cardId;

	/**
	 * 业务名称
	 */
	private String cardName;

	/**
	 * 面值号
	 */
	private int priceId;

	/**
	 * 面值名
	 */
	private String priceName;
	
	/**
	 * 排序
	 */
	private int seqNo;

	/**
	 * 面值金额
	 */
	private BigDecimal denomination;

	/**
	 * 支付费用
	 */
	private BigDecimal fee;

	/**
	 * 成本金额
	 */
	private BigDecimal cost;

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public int getPriceId() {
		return priceId;
	}

	public void setPriceId(int priceId) {
		this.priceId = priceId;
	}

	public String getPriceName() {
		return priceName;
	}

	public void setPriceName(String priceName) {
		this.priceName = priceName;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public BigDecimal getDenomination() {
		return denomination;
	}

	public void setDenomination(BigDecimal denomination) {
		this.denomination = denomination;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
	
}

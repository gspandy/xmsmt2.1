package net.wit.vo;

import net.wit.Page;

import java.math.BigDecimal;

/**
 * Created by app_000 on 2015/10/26.
 */
public class OrderSettlementAdapter {
    Page<OrderSettlementVO> page = null;
    private BigDecimal totalAmount=BigDecimal.ZERO;//订单金额
    private BigDecimal totalSettleCharge=BigDecimal.ZERO;
    

    public BigDecimal getTotalSettleCharge() {
		return totalSettleCharge;
	}

	public void setTotalSettleCharge(BigDecimal totalSettleCharge) {
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00"); 
		if(null==totalSettleCharge)totalSettleCharge=BigDecimal.ZERO;
		this.totalSettleCharge =new BigDecimal(df.format(totalSettleCharge)) ;
	}

	public Page<OrderSettlementVO> getPage() {
        return page;
    }

    public void setPage(Page<OrderSettlementVO> page) {
        this.page = page;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}

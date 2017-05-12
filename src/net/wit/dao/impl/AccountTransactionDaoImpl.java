/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.dao.impl;

import java.util.List;

import net.wit.dao.AccountTransactionDao;
import net.wit.vo.AccountTransactionVO;

import org.springframework.stereotype.Repository;

/**
 * @author rsico Team
 * @version 3.0
 */
@Repository("accountTransactionDaoImpl")
public class AccountTransactionDaoImpl extends BaseDaoImpl<Object, Long> implements AccountTransactionDao{


	
	public List<AccountTransactionVO> findAccountList(Long memberId){
		
//		String strDate = DateUtil.changeDateToStr(date, DateUtil.LINK_DISPLAY_DATE_MONTH);
//		StringBuffer sb = new StringBuffer();
//		sb.append("select sum(os.order_settle_amt) as orderSettleAmt,sum(os.settle_charge) as settleCharge,");
//		sb.append(" owner_id as ownerId from t_order_settlement os");
//		sb.append(" where date_format(finish_Date,'%Y-%m') = '"+strDate+"'");		
//		sb.append(" and status = '"+OrderSettlement.SettlementStatus.settlement.ordinal()+"'");
//		sb.append(" group by owner_id");
//	
//		Query query = entityManager.createNativeQuery(sb.toString());
//		query.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(OrderSettlementVO.class));
//		@SuppressWarnings("unchecked")
//		List<OrderSettlementVO> list = query.getResultList();
//		return list;
			return null;
	}
	
	
}
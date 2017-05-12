package net.wit.service.impl;

import javax.annotation.Resource;

import net.wit.dao.RentFreeDao;
import net.wit.entity.RentFree;
import net.wit.service.RentFreeService;

import org.springframework.stereotype.Service;

/**
 * Service - 企业免租活动
 * @author Teddy
 * @version 3.0
 */
@Service("rentFreeServiceImpl")
public class RentFreeServiceImpl extends BaseServiceImpl<RentFree, Long> implements RentFreeService {

	@Resource(name = "rentFreeDaoImpl")
	private RentFreeDao rentFreeDao;

	@Resource(name = "rentFreeDaoImpl")
	public void setBaseDao(RentFreeDao rentFreeDao) {
		super.setBaseDao(rentFreeDao);
	}


}
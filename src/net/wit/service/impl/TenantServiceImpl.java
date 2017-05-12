package net.wit.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import net.wit.Filter;
import net.wit.Order;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.DeliveryCenterDao;
import net.wit.dao.IdcardDao;
import net.wit.dao.MemberDao;
import net.wit.dao.ReceiverDao;
import net.wit.dao.TenantDao;
import net.wit.entity.Area;
import net.wit.entity.Community;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.Receiver;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.TenantTicket;
import net.wit.entity.Tenant.Status;
import net.wit.entity.TenantCategory;
import net.wit.service.TenantService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: www.insuper.com
 * </p>
 * <p>
 * Company: www.insuper.com
 * </p>
 * @author liumx
 * @version 1.0
 * @date 2013年7月2日15:46:16
 */
@Service("tenantServiceImpl")
public class TenantServiceImpl extends BaseServiceImpl<Tenant, Long>implements TenantService {

	@Resource(name = "tenantDaoImpl")
	private TenantDao tenantDao;

	@Resource(name = "memberDaoImpl")
	private MemberDao memberDao;

	@Resource(name = "idcardDaoImpl")
	private IdcardDao idcardDao;

	@Resource(name = "deliveryCenterDaoImpl")
	private DeliveryCenterDao deliveryCenterDao;

	@Resource(name = "receiverDaoImpl")
	private ReceiverDao receiverDao;

	@Resource(name = "tenantDaoImpl")
	public void setBaseDao(TenantDao tenantDao) {
		super.setBaseDao(tenantDao);
	}

	@Transactional(readOnly = true)
	public Tenant findByCode(String code) {
		return tenantDao.findByCode(code);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(Area area, String name, Tag tag, Integer count) {
		return tenantDao.findList(area, name, tag, count);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(Area area, Location location, BigDecimal distatce, Tag tag, Integer count) {
		return tenantDao.findList(area, location, distatce, tag, count);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders) {
		return tenantDao.findList(tenantCategory, tags, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Area area, Community community, Boolean periferal, Integer count, List<Filter> filters, List<Order> orders) {
		return tenantDao.findList(tenantCategory, tags, area, community, periferal, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return tenantDao.findList(tenantCategory, tags, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(TenantCategory tenantCategory, List<Tag> tags, Area area, Community community, Boolean periferal, Integer count, List<Filter> filters, List<Order> orders, String cacheRegion) {
		return tenantDao.findList(tenantCategory, tags, area, community, periferal, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findList(TenantCategory tenantCategory, Date beginDate, Date endDate, Integer first, Integer count) {
		return tenantDao.findList(tenantCategory, beginDate, endDate, first, count);
	}

	@Transactional(readOnly = true)
	public Page<Tenant> findPage(TenantCategory tenantCategory, List<Tag> tags, Pageable pageable) {
		return tenantDao.findPage(tenantCategory, tags, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable) {
		return tenantDao.findPage(tenantCategorys, tags, area, community, periferal, location, distatce, pageable);
	}

	@Transactional
	public void save(Tenant tenant, Member member) {
		if (member.getIdcard() != null) {
			idcardDao.persist(member.getIdcard());
		}
		tenant.setMember(member);
		member.setTenant(tenant);
		tenantDao.persist(tenant);
		tenantDao.flush();
		memberDao.merge(member);
		DeliveryCenter deliveryCenter = deliveryCenterDao.findDefault(tenant);
		if (deliveryCenter == null) {
			// 新增默认发货地址
			deliveryCenter = new DeliveryCenter();
			Set<DeliveryCenter> deliveryCenters = new HashSet<DeliveryCenter>();
			deliveryCenter.setName(tenant.getName());
			deliveryCenter.setContact(tenant.getLinkman());
			deliveryCenter.setAreaName(tenant.getArea().getFullName());
			deliveryCenter.setAddress(tenant.getAddress());
			deliveryCenter.setZipCode(tenant.getZipcode());
			deliveryCenter.setSn("1");
			deliveryCenter.setPhone(tenant.getTelephone());
			deliveryCenter.setMobile(tenant.getMember().getMobile());
			deliveryCenter.setIsDefault(true);
			deliveryCenter.setArea(tenant.getArea());
			deliveryCenter.setCommunity(tenant.getCommunity());
			deliveryCenter.setTenant(tenant);
			deliveryCenters.add(deliveryCenter);
			tenant.setDeliveryCenters(deliveryCenters);
			deliveryCenterDao.persist(deliveryCenter);
			tenantDao.merge(tenant);
		}

		if (member.getReceivers().isEmpty()) {
			Receiver receiver = new Receiver();
			receiver.setAddress(deliveryCenter.getAddress());
			receiver.setArea(deliveryCenter.getArea());
			receiver.setAreaName(deliveryCenter.getAreaName());
			receiver.setConsignee(tenant.getName());
			receiver.setIsDefault(true);
			receiver.setMember(member);
			receiver.setPhone(deliveryCenter.getPhone());
			receiver.setZipCode(deliveryCenter.getZipCode() == null ? deliveryCenter.getArea().getZipCode() : deliveryCenter.getZipCode());
			if (receiver.getZipCode() == null) {
				receiver.setZipCode("000000");
			}
			receiverDao.persist(receiver);
		}
	}

	@Transactional(readOnly = true)
	public Page<Tenant> findPage(Area area, List<Tag> tags, Pageable pageable) {
		return tenantDao.findPage(area, tags, pageable);
	}

	@Transactional(readOnly = true)
	public List<Tenant> findNew(List<Tag> tags, Integer count) {
		return tenantDao.findNew(tags, count);
	}

	@Transactional(readOnly = true)
	public Page<Tenant> findAgency(Member member, Status status, Pageable pageable) {
		return tenantDao.findAgency(member, status, pageable);
	}

	@Transactional(readOnly = true)
	public long count(Member member, Date startTime, Date endTime, Status status) {
		return tenantDao.count(member, startTime, endTime, status);
	}

	public List<ProductCategoryTenant> findRoots(Tenant tenant, Integer count) {
		return tenantDao.findRoots(tenant, count);
	}

	/**
	 * 计算商家距离经纬度的距离
	 * @param tenant
	 * @param location
	 * @return
	 */
	public BigDecimal calculateDistance(Tenant tenant, Location location) {
		DeliveryCenter deliveryCenter = deliveryCenterDao.findDefault(tenant);
		try {
			if (deliveryCenter != null && deliveryCenter.getLocation() != null) {
				double distance = GetDistance(location.getX().doubleValue(), location.getY().doubleValue(), deliveryCenter.getLocation().getX().doubleValue(), deliveryCenter.getLocation().getY().doubleValue());
				return new BigDecimal(distance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BigDecimal(0);
	}

	private static double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	public Page<Tenant> mobileFindPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Location location, BigDecimal distatce, Pageable pageable) {
		return tenantDao.mobileFindPage(tenantCategorys, tags, area, community, periferal, location, distatce, pageable);
	}

	public Page<Tenant> findPage(Set<TenantCategory> tenantCategorys, List<Tag> tags, Area area, Community community, Boolean periferal, Pageable pageable) {
		return tenantDao.findPage(tenantCategorys, tags, area, community, periferal, pageable);
	}

	@Transactional(readOnly = true)
	public List<Tenant> tenantSelect(String q, Boolean b, int i) {
		return tenantDao.tenantSelect(q, b, i);
	}

	public List<Tenant> findNewest(List<Tag> tags, Integer count) {
		return tenantDao.findNewest(tags, count);
	}

	@Transactional(readOnly = true)
	public Page<Tenant> findPage(Member member, Pageable pageable) {
		return tenantDao.findPage(member, pageable);
	}

	public List<Tenant> getTenantAll() {
		return tenantDao.getTenantAll();
	}

	public List<Tenant> findList(ProductCategory productCategory) {
		return tenantDao.findList(productCategory);
	}

}

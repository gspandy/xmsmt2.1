/*
 * Copyright 2005-2013 rsico. All rights reserved.
 * Support: http://www.rsico.cn
 * License: http://www.rsico.cn/license
 */
package net.wit.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.SpecificationDao;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.Specification;
import net.wit.entity.SpecificationValue;
import net.wit.entity.Tenant;
import net.wit.service.SpecificationService;

/**
 * Service - 规格
 * @author rsico Team
 * @version 3.0
 */
@Service("specificationServiceImpl")
public class SpecificationServiceImpl extends BaseServiceImpl<Specification, Long>implements SpecificationService {

	@Resource(name = "specificationDaoImpl")
	private SpecificationDao specificationDao;

	@Resource(name = "specificationDaoImpl")
	public void setBaseDao(SpecificationDao specificationDao) {
		super.setBaseDao(specificationDao);
	}

	public Page<Specification> findPage(ProductCategory productCategory, Tenant tenant, Pageable pageable) {
		return specificationDao.findPage(productCategory, tenant, pageable);
	}

	public List<Specification> findList(ProductCategory curproductCategory, Tenant tenant) {
		return specificationDao.findList(curproductCategory, tenant);
	}

	public void initTenantSpecification(Tenant tenant) throws Exception {
		if (tenant == null) {
			throw new Exception("商家参数为空");
		}
		List<ProductCategory> productCategories = tenant.getProductCategories();
		for (ProductCategory productCategory : productCategories) {
			List<Specification> list = this.findList(productCategory, null);
			List<Specification> listTemp = new ArrayList<Specification>();
			List<Specification> tenantlist = this.findList(productCategory, tenant);
			for (Specification s : list) { // 去除已经存在的规格
				for (Specification spec : tenantlist) {
					if ((s.getName().equals(spec.getName()))) {
						listTemp.add(s);
						break;
					}
				}

			}
			list.removeAll(listTemp);
			for (Specification s : list) {
				Specification specification = new Specification();
				specification.setProductCategory(productCategory);
				specification.setTenant(tenant);
				specification.setType(s.getType());
				specification.setMemo(s.getMemo());
				specification.setProducts(new HashSet<Product>());
				specification.setName(s.getName());

				List<SpecificationValue> svs = new ArrayList<SpecificationValue>();
				for (SpecificationValue sv : s.getSpecificationValues()) {
					SpecificationValue specificationValue = new SpecificationValue();
					BeanUtils.copyProperties(sv, specificationValue, new String[] { "id", "specification", "products" });
					specificationValue.setSpecification(specification);
					svs.add(specificationValue);
				}
				specification.setSpecificationValues(svs);
				specificationDao.persist(specification);
			}

		}
	}

}
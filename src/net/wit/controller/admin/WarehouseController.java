package net.wit.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Pageable;
import net.wit.Setting;
import net.wit.entity.Admin;
import net.wit.entity.Area;
import net.wit.entity.Attribute;
import net.wit.entity.Bonus;
import net.wit.entity.Brand;
import net.wit.entity.Goods;
import net.wit.entity.Location;
import net.wit.entity.MemberRank;
import net.wit.entity.PackagUnit;
import net.wit.entity.Parameter;
import net.wit.entity.ParameterGroup;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductImage;
import net.wit.entity.Promotion;
import net.wit.entity.Specification;
import net.wit.entity.SpecificationValue;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.Product.OrderType;
import net.wit.entity.Product.PriceType;
import net.wit.service.AdminService;
import net.wit.service.AreaService;
import net.wit.service.BrandService;
import net.wit.service.FileService;
import net.wit.service.GoodsService;
import net.wit.service.MemberRankService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductCategoryTenantService;
import net.wit.service.ProductImageService;
import net.wit.service.ProductService;
import net.wit.service.PromotionService;
import net.wit.service.SpecificationService;
import net.wit.service.SpecificationValueService;
import net.wit.service.TagService;
import net.wit.service.TenantService;
import net.wit.util.SettingUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("warehouseController")
@RequestMapping({ "/admin/warehouse" })
public class WarehouseController extends BaseController {

	@Resource(name = "productServiceImpl")
	private ProductService productService;

	@Resource(name = "productCategoryServiceImpl")
	private ProductCategoryService productCategoryService;

	@Resource(name = "productCategoryTenantServiceImpl")
	private ProductCategoryTenantService productCategoryTenantService;

	@Resource(name = "goodsServiceImpl")
	private GoodsService goodsService;

	@Resource(name = "brandServiceImpl")
	private BrandService brandService;

	@Resource(name = "promotionServiceImpl")
	private PromotionService promotionService;

	@Resource(name = "tagServiceImpl")
	private TagService tagService;

	@Resource(name = "memberRankServiceImpl")
	private MemberRankService memberRankService;

	@Resource(name = "tenantServiceImpl")
	private TenantService tenantService;

	@Resource(name = "productImageServiceImpl")
	private ProductImageService productImageService;

	@Resource(name = "specificationServiceImpl")
	private SpecificationService specificationService;

	@Resource(name = "specificationValueServiceImpl")
	private SpecificationValueService specificationValueService;

	@Resource(name = "fileServiceImpl")
	private FileService fileService;

	@Resource(name = "areaServiceImpl")
	private AreaService areaService;

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = { "/check_sn" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public boolean checkSn(String previousSn, String sn) {
		if (StringUtils.isEmpty(sn)) {
			return false;
		}
		return productService.snUnique(previousSn, sn);
	}

	@RequestMapping(value = { "/parameter_groups" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Set<ParameterGroup> parameterGroups(Long id) {
		ProductCategory productCategory = this.productCategoryService.find(id);
		return productCategory.getParameterGroups();
	}

	@RequestMapping(value = { "/attributes" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	@ResponseBody
	public Set<Attribute> attributes(Long id) {
		ProductCategory productCategory = (ProductCategory) this.productCategoryService.find(id);
		return productCategory.getAttributes();
	}

	@RequestMapping(value = { "/add" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String add(ModelMap model) {
		Tenant tenant = adminService.getCurrent().getTenant();
		model.addAttribute("productCategoryTenantTree", productCategoryTenantService.findTree(tenant));
		model.addAttribute("productCategorys", tenant.getProductCategories());
		model.addAttribute("brands", this.brandService.findList(adminService.getCurrent().getTenant()));
		model.addAttribute("tags", this.tagService.findList(Tag.Type.product));
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("specifications", specificationService.findList(null, tenant));
		model.addAttribute("priceTypes", PriceType.values());
		return "/admin/warehouse/add";
	}

	@RequestMapping(value = { "/save" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String save(Product product, Long productCategoryId, Long productCategoryTenantId, Long brandId, Long[] tagIds, Long[] specificationIds, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Admin admin = adminService.getCurrent();
		ProductImage productImage;
		for (Iterator iterator = product.getProductImages().iterator(); iterator.hasNext();) {
			productImage = (ProductImage) iterator.next();
			if ((productImage == null) || (productImage.isEmpty())) {
				iterator.remove();
			} else {
				if ((productImage.getFile() == null) || (productImage.getFile().isEmpty()) || (this.fileService.isValid(FileInfo.FileType.image, productImage.getFile())))
					continue;
				addFlashMessage(redirectAttributes, Message.error("admin.upload.invalid", new Object[0]));
				return "redirect:add.jhtml";
			}
		}

		product.setProductCategory(this.productCategoryService.find(productCategoryId));
		product.setBrand(this.brandService.find(brandId));
		product.setProductCategoryTenant(productCategoryTenantService.find(productCategoryTenantId));
		product.setTags(new HashSet(this.tagService.findList(tagIds)));
		product.setTenant(admin.getTenant());

		if (!(isValid(product, new Class[0]))) {
			return "/admin/common/error";
		}
		if ((StringUtils.isNotEmpty(product.getSn())) && (this.productService.snExists(product.getSn()))) {
			return "/admin/common/error";
		}
		if (product.getMarketPrice() == null) {
			BigDecimal defaultMarketPrice = calculateDefaultMarketPrice(product.getPrice());
			product.setMarketPrice(defaultMarketPrice);
		}
		if (product.getPoint() == null) {
			long point = calculateDefaultPoint(product.getPrice());
			product.setPoint(Long.valueOf(point));
		}
		product.setFullName(null);
		product.setAllocatedStock(Integer.valueOf(0));
		product.setScore(Float.valueOf(0.0F));
		product.setTotalScore(Long.valueOf(0L));
		product.setScoreCount(Long.valueOf(0L));
		product.setPriority(Long.valueOf(0L));
		product.setHits(Long.valueOf(0L));
		product.setWeekHits(Long.valueOf(0L));
		product.setMonthHits(Long.valueOf(0L));
		product.setSales(Long.valueOf(0L));
		product.setWeekSales(Long.valueOf(0L));
		product.setMonthSales(Long.valueOf(0L));
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		product.setReviews(null);
		product.setConsultations(null);
		product.setFavoriteMembers(null);
		product.setPromotions(null);
		product.setCartItems(null);
		product.setOrderItems(null);
		product.setGiftItems(null);
		product.setProductNotifies(null);
		for (Bonus b : product.getBonuses()) {
			b.setProduct(product);
		}
		for (PackagUnit p : product.getPackagUnits()) {
			p.setProduct(product);
		}

		for (MemberRank memberRank : this.memberRankService.findAll()) {
			String price = request.getParameter("memberPrice_" + memberRank.getId());
			if ((StringUtils.isNotEmpty(price)) && (new BigDecimal(price).compareTo(new BigDecimal(0)) >= 0))
				product.getMemberPrice().put(memberRank, new BigDecimal(price));
			else {
				product.getMemberPrice().remove(memberRank);
			}
		}

		for (ProductImage productImages : product.getProductImages()) {
			this.productImageService.build(productImages);
		}
		Collections.sort(product.getProductImages());
		if ((product.getImage() == null) && (product.getThumbnail() != null)) {
			product.setImage(product.getThumbnail());
		}

		for (ParameterGroup parameterGroup : product.getProductCategory().getParameterGroups()) {
			for (Parameter parameter : parameterGroup.getParameters()) {
				String parameterValue = request.getParameter("parameter_" + parameter.getId());
				if (StringUtils.isNotEmpty(parameterValue))
					product.getParameterValue().put(parameter, parameterValue);
				else {
					product.getParameterValue().remove(parameter);
				}
			}
		}

		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String attributeValue = request.getParameter("attribute_" + attribute.getId());
			if (StringUtils.isNotEmpty(attributeValue))
				product.setAttributeValue(attribute, attributeValue);
			else {
				product.setAttributeValue(attribute, null);
			}
		}

		Goods goods = new Goods();
		List<Product> products = new ArrayList();
		if ((specificationIds != null) && (specificationIds.length > 0)) {
			String[] stocks = request.getParameterValues("stock_" + specificationIds[specificationIds.length - 1]);
			String[] prices = request.getParameterValues("price_" + specificationIds[specificationIds.length - 1]);
			String[] rents = request.getParameterValues("rent_" + specificationIds[specificationIds.length - 1]);
			for (int i = 0; i < specificationIds.length; ++i) {
				Specification specification = this.specificationService.find(specificationIds[i]);
				String[] specificationValueIds = request.getParameterValues("specification_" + specification.getId());
				if ((specificationValueIds != null) && (specificationValueIds.length > 0))
					for (int j = 0; j < specificationValueIds.length; ++j) {
						if (i == 0) {
							if (j == 0) {
								product.setGoods(goods);
								if (StringUtils.isNotBlank(stocks[j + 1])) {
									product.setStock(Integer.valueOf(stocks[j + 1]));
								}
								if (StringUtils.isNotBlank(prices[j + 1])) {
									product.setPrice(new BigDecimal(prices[j + 1]));
								}
								if (StringUtils.isNotBlank(rents[j + 1])) {
									product.setRent(new BigDecimal(rents[j + 1]));
								}
								product.setSpecifications(new HashSet());
								product.setSpecificationValues(new HashSet());
								products.add(product);
							} else {
								Product specificationProduct = new Product();
								BeanUtils.copyProperties(product, specificationProduct);
								specificationProduct.setId(null);
								specificationProduct.setCreateDate(null);
								specificationProduct.setModifyDate(null);
								specificationProduct.setSn(null);
								specificationProduct.setFullName(null);
								specificationProduct.setAllocatedStock(Integer.valueOf(0));
								specificationProduct.setIsList(Boolean.valueOf(false));
								specificationProduct.setScore(Float.valueOf(0.0F));
								specificationProduct.setPriority(Long.valueOf(0L));
								specificationProduct.setTotalScore(Long.valueOf(0L));
								specificationProduct.setScoreCount(Long.valueOf(0L));
								specificationProduct.setHits(Long.valueOf(0L));
								specificationProduct.setWeekHits(Long.valueOf(0L));
								specificationProduct.setMonthHits(Long.valueOf(0L));
								specificationProduct.setSales(Long.valueOf(0L));
								specificationProduct.setWeekSales(Long.valueOf(0L));
								specificationProduct.setMonthSales(Long.valueOf(0L));
								specificationProduct.setWeekHitsDate(new Date());
								specificationProduct.setMonthHitsDate(new Date());
								specificationProduct.setWeekSalesDate(new Date());
								specificationProduct.setMonthSalesDate(new Date());
								specificationProduct.setGoods(goods);
								specificationProduct.setReviews(null);
								specificationProduct.setConsultations(null);
								specificationProduct.setFavoriteMembers(null);
								specificationProduct.setSpecifications(new HashSet());
								specificationProduct.setSpecificationValues(new HashSet());
								specificationProduct.setPromotions(null);
								specificationProduct.setCartItems(null);
								specificationProduct.setOrderItems(null);
								specificationProduct.setGiftItems(null);
								specificationProduct.setProductNotifies(null);
								if (StringUtils.isNotBlank(stocks[j + 1])) {
									specificationProduct.setStock(Integer.valueOf(stocks[j + 1]));
								}
								if (StringUtils.isNotBlank(prices[j + 1])) {
									specificationProduct.setPrice(new BigDecimal(prices[j + 1]));
								}
								if (StringUtils.isNotBlank(rents[j + 1])) {
									specificationProduct.setRent(new BigDecimal(rents[j + 1]));
								}
								specificationProduct.setSpecifications(new HashSet());
								specificationProduct.setSpecificationValues(new HashSet());
								products.add(specificationProduct);
							}
						}
						Product specificationProduct = products.get(j);
						SpecificationValue specificationValue = this.specificationValueService.find(Long.valueOf(specificationValueIds[j]));
						specificationProduct.getSpecifications().add(specification);
						specificationProduct.getSpecificationValues().add(specificationValue);
					}

			}
		} else {
			product.setGoods(goods);
			product.setSpecifications(null);
			product.setSpecificationValues(null);
			products.add(product);
		}
		goods.getProducts().clear();
		goods.getProducts().addAll(products);
		this.goodsService.save(goods);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/edit" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String edit(Long id, ModelMap model) {
		Tenant tenant = adminService.getCurrent().getTenant();
		Product product = productService.find(id);
		model.addAttribute("productCategoryTenantTree", productCategoryTenantService.findTree(tenant));
		model.addAttribute("productCategorys", tenant.getProductCategories());
		model.addAttribute("brands", this.brandService.findList(adminService.getCurrent().getTenant()));
		model.addAttribute("tags", this.tagService.findList(Tag.Type.product));
		model.addAttribute("memberRanks", this.memberRankService.findAll());
		model.addAttribute("specifications", specificationService.findList(product.getProductCategory(), tenant));
		model.addAttribute("product", product);
		model.addAttribute("priceTypes", PriceType.values());
		return "/admin/warehouse/edit";
	}

	@RequestMapping(value = { "/update" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	public String update(Product product, Long productCategoryId, Long productCategoryTenantId, Long brandId, Long[] tagIds, Long[] specificationIds, Long[] specificationProductIds, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		ProductImage productImage;
		for (Iterator iterator = product.getProductImages().iterator(); iterator.hasNext();) {
			productImage = (ProductImage) iterator.next();
			if ((productImage == null) || (productImage.isEmpty())) {
				iterator.remove();
			} else {
				if ((productImage.getFile() == null) || (productImage.getFile().isEmpty()) || (this.fileService.isValid(FileInfo.FileType.image, productImage.getFile())))
					continue;
				addFlashMessage(redirectAttributes, Message.error("admin.upload.invalid", new Object[0]));
				return "redirect:edit.jhtml?id=" + product.getId();
			}
		}

		product.setProductCategory(this.productCategoryService.find(productCategoryId));
		product.setProductCategoryTenant(productCategoryTenantService.find(productCategoryTenantId));
		product.setBrand(this.brandService.find(brandId));
		product.setTags(new HashSet(this.tagService.findList(tagIds)));
		if (!(isValid(product, new Class[0]))) {
			return "/admin/common/error";
		}
		Product pProduct = this.productService.find(product.getId());
		if (pProduct == null) {
			return "/admin/common/error";
		}
		product.setTenant(pProduct.getTenant());
		if ((StringUtils.isNotEmpty(product.getSn())) && (!(this.productService.snUnique(pProduct.getSn(), product.getSn())))) {
			return "/admin/common/error";
		}
		if (product.getMarketPrice() == null) {
			BigDecimal defaultMarketPrice = calculateDefaultMarketPrice(product.getPrice());
			product.setMarketPrice(defaultMarketPrice);
		}
		if (product.getPoint() == null) {
			long point = calculateDefaultPoint(product.getPrice());
			product.setPoint(Long.valueOf(point));
		}

		for (MemberRank memberRank : this.memberRankService.findAll()) {
			String price = request.getParameter("memberPrice_" + memberRank.getId());
			if ((StringUtils.isNotEmpty(price)) && (new BigDecimal(price).compareTo(new BigDecimal(0)) >= 0))
				product.getMemberPrice().put(memberRank, new BigDecimal(price));
			else {
				product.getMemberPrice().remove(memberRank);
			}
		}

		for (Iterator i = product.getProductImages().iterator(); i.hasNext();) {
			productImage = (ProductImage) i.next();
			this.productImageService.build(productImage);
		}
		Collections.sort(product.getProductImages());
		if ((product.getImage() == null) && (product.getThumbnail() != null)) {
			product.setImage(product.getThumbnail());
		}

		for (ParameterGroup parameterGroup : product.getProductCategory().getParameterGroups()) {
			for (Parameter parameter : parameterGroup.getParameters()) {
				String parameterValue = request.getParameter("parameter_" + parameter.getId());
				if (StringUtils.isNotEmpty(parameterValue))
					product.getParameterValue().put(parameter, parameterValue);
				else {
					product.getParameterValue().remove(parameter);
				}
			}
		}

		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String attributeValue = request.getParameter("attribute_" + attribute.getId());
			if (StringUtils.isNotEmpty(attributeValue))
				product.setAttributeValue(attribute, attributeValue);
			else {
				product.setAttributeValue(attribute, null);
			}
		}

		Goods goods = pProduct.getGoods();
		Object products = new ArrayList();
		if ((specificationIds != null) && (specificationIds.length > 0)) {
			String[] stocks = request.getParameterValues("stock_" + specificationIds[specificationIds.length - 1]);
			String[] prices = request.getParameterValues("price_" + specificationIds[specificationIds.length - 1]);
			String[] rents = request.getParameterValues("rent_" + specificationIds[specificationIds.length - 1]);
			for (int i = 0; i < specificationIds.length; ++i) {
				Specification specification = specificationService.find(specificationIds[i]);
				String[] specificationValueIds = request.getParameterValues("specification_" + specification.getId());
				if ((specificationValueIds != null) && (specificationValueIds.length > 0))
					for (int j = 0; j < specificationValueIds.length; ++j) {
						if (i == 0) {
							if (j == 0) {
								BeanUtils.copyProperties(product, pProduct,
										new String[] { "id", "createDate", "modifyDate", "fullName", "allocatedStock", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "priority", "sales", "weekSales", "monthSales", "weekHitsDate",
												"monthHitsDate", "weekSalesDate", "monthSalesDate", "goods", "reviews", "consultations", "favoriteMembers", "specifications", "specificationValues", "promotions", "cartItems", "orderItems", "giftItems",
												"productNotifies", "bonuses", "packagUnits" });
								if (StringUtils.isNotBlank(stocks[j + 1])) {
									pProduct.setStock(Integer.valueOf(stocks[j + 1]));
								}
								if (StringUtils.isNotBlank(prices[j + 1])) {
									pProduct.setPrice(new BigDecimal(prices[j + 1]));
								}
								if (StringUtils.isNotBlank(rents[j + 1])) {
									pProduct.setRent(new BigDecimal(rents[j + 1]));
								}
								pProduct.setSpecifications(new HashSet());
								pProduct.setSpecificationValues(new HashSet());
								((List) products).add(pProduct);
							} else if ((specificationProductIds != null) && (j < specificationProductIds.length)) {
								Product specificationProduct = productService.find(specificationProductIds[j]);
								if (StringUtils.isNotBlank(stocks[j + 1])) {
									specificationProduct.setStock(Integer.valueOf(stocks[j + 1]));
								}
								if (StringUtils.isNotBlank(prices[j + 1])) {
									specificationProduct.setPrice(new BigDecimal(prices[j + 1]));
								}
								if (StringUtils.isNotBlank(rents[j + 1])) {
									specificationProduct.setRent(new BigDecimal(rents[j + 1]));
								}
								if ((specificationProduct == null) || ((specificationProduct.getGoods() != null) && (!(specificationProduct.getGoods().equals(goods))))) {
									return "/admin/common/error";
								}
								specificationProduct.setSpecifications(new HashSet());
								specificationProduct.setSpecificationValues(new HashSet());
								((List) products).add(specificationProduct);
							} else {
								Product specificationProduct = new Product();
								BeanUtils.copyProperties(product, specificationProduct);
								specificationProduct.setId(null);
								specificationProduct.setCreateDate(null);
								specificationProduct.setModifyDate(null);
								specificationProduct.setSn(null);
								specificationProduct.setFullName(null);
								specificationProduct.setAllocatedStock(Integer.valueOf(0));
								specificationProduct.setIsList(Boolean.valueOf(false));
								specificationProduct.setScore(Float.valueOf(0.0F));
								specificationProduct.setTotalScore(Long.valueOf(0L));
								specificationProduct.setScoreCount(Long.valueOf(0L));
								specificationProduct.setHits(Long.valueOf(0L));
								specificationProduct.setWeekHits(Long.valueOf(0L));
								specificationProduct.setMonthHits(Long.valueOf(0L));
								specificationProduct.setSales(Long.valueOf(0L));
								specificationProduct.setWeekSales(Long.valueOf(0L));
								specificationProduct.setMonthSales(Long.valueOf(0L));
								specificationProduct.setWeekHitsDate(new Date());
								specificationProduct.setMonthHitsDate(new Date());
								specificationProduct.setWeekSalesDate(new Date());
								specificationProduct.setMonthSalesDate(new Date());
								specificationProduct.setGoods(goods);
								specificationProduct.setReviews(null);
								specificationProduct.setConsultations(null);
								specificationProduct.setFavoriteMembers(null);
								specificationProduct.setSpecifications(new HashSet());
								specificationProduct.setSpecificationValues(new HashSet());
								specificationProduct.setPromotions(null);
								specificationProduct.setCartItems(null);
								specificationProduct.setOrderItems(null);
								specificationProduct.setGiftItems(null);
								if (StringUtils.isNotBlank(stocks[j + 1])) {
									specificationProduct.setStock(Integer.valueOf(stocks[j + 1]));
								}
								if (StringUtils.isNotBlank(prices[j + 1])) {
									specificationProduct.setPrice(new BigDecimal(prices[j + 1]));
								}
								if (StringUtils.isNotBlank(rents[j + 1])) {
									specificationProduct.setRent(new BigDecimal(rents[j + 1]));
								}
								specificationProduct.setProductNotifies(null);
								specificationProduct.setBonuses(new ArrayList());
								specificationProduct.setPackagUnits(new ArrayList());
								((List) products).add(specificationProduct);
							}
						}

						Product specificationProduct = (Product) ((List) products).get(j);
						SpecificationValue specificationValue = this.specificationValueService.find(Long.valueOf(specificationValueIds[j]));
						specificationProduct.getSpecifications().add(specification);
						specificationProduct.getSpecificationValues().add(specificationValue);
					}
			}
		} else {
			product.setSpecifications(null);
			product.setSpecificationValues(null);
			BeanUtils.copyProperties(product, pProduct, new String[] { "id", "createDate", "modifyDate", "fullName", "allocatedStock", "score", "priority", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales",
					"weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "goods", "reviews", "consultations", "favoriteMembers", "promotions", "cartItems", "orderItems", "giftItems", "productNotifies", "bonuses", "packagUnits" });
			((List) products).add(pProduct);
		}
		pProduct.getBonuses().clear();
		for (Object specification = product.getBonuses().iterator(); ((Iterator) specification).hasNext();) {
			Bonus b = (Bonus) ((Iterator) specification).next();
			b.setProduct(pProduct);
			pProduct.getBonuses().add(b);
		}
		pProduct.getPackagUnits().clear();
		for (Iterator specification = product.getPackagUnits().iterator(); (specification).hasNext();) {
			PackagUnit p = (PackagUnit) (specification).next();
			p.setProduct(pProduct);
			pProduct.getPackagUnits().add(p);
		}

		goods.getProducts().addAll((Collection) products);
		this.goodsService.update(goods);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/list" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String list(Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Location location, BigDecimal distance,
			OrderType orderType, BigDecimal endPrice, BigDecimal startPrice, Pageable pageable, ModelMap model, HttpServletRequest request) {
		 String searchValue = null;
			try {
				searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
				pageable.setSearchValue(searchValue);
			} catch (Exception localException) {
			}
		Area area = this.areaService.getCurrent();
		ProductCategory productCategory = (ProductCategory) this.productCategoryService.find(productCategoryId);
		Brand brand = (Brand) this.brandService.find(brandId);
		Promotion promotion = (Promotion) this.promotionService.find(promotionId);
		List tags = this.tagService.findList(new Long[] { tagId });
		model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
		model.addAttribute("brands", this.brandService.findList(adminService.getCurrent().getTenant()));
		model.addAttribute("promotions", this.promotionService.findAll());
		model.addAttribute("tags", this.tagService.findList(Tag.Type.product));
		model.addAttribute("productCategoryId", productCategoryId);
		model.addAttribute("brandId", brandId);
		model.addAttribute("promotionId", promotionId);
		model.addAttribute("tagId", tagId);
		model.addAttribute("isMarketable", isMarketable);
		model.addAttribute("isList", isList);
		model.addAttribute("isTop", isTop);
		model.addAttribute("isGift", isGift);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("isStockAlert", isStockAlert);
		model.addAttribute("page",
				this.productService.findMyPage(adminService.getCurrent().getTenant(), productCategory, null, brand, promotion, tags, null, startPrice, endPrice, false, isList, isTop, isGift, isOutOfStock, isStockAlert, orderType, pageable));
		return "/admin/warehouse/list";
	}
	
	@RequestMapping(value = { "/isMarketable" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
	public String isMarketable(Long[] ids) {
		List<Product> list=productService.findList(ids);
		for(Product product:list){
			if(!product.getIsMarketable()){
				product.setIsMarketable(true);
				productService.update(product);
			}
		}
		return "redirect:list.jhtml";
	}

	@RequestMapping(value = { "/delete" }, method = { org.springframework.web.bind.annotation.RequestMethod.POST })
	@ResponseBody
	public Message delete(Long[] ids) {
		this.productService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 获取规格
	 */
	@RequestMapping(value = "/specifications", method = RequestMethod.GET)
	public @ResponseBody List<Specification> specifications(Long id) {
		ProductCategory curproductCategory = productCategoryService.find(id);
		Tenant tenant = adminService.getCurrent().getTenant();
		List<Specification> specifications = specificationService.findList(curproductCategory, tenant);
		return specifications;
	}

	private BigDecimal calculateDefaultMarketPrice(BigDecimal price) {
		Setting setting = SettingUtils.get();
		Double defaultMarketPriceScale = setting.getDefaultMarketPriceScale();
		return setting.setScale(price.multiply(new BigDecimal(defaultMarketPriceScale.toString())));
	}

	private long calculateDefaultPoint(BigDecimal price) {
		Setting setting = SettingUtils.get();
		Double defaultPointScale = setting.getDefaultPointScale();
		return price.multiply(new BigDecimal(defaultPointScale.toString())).longValue();
	}
}

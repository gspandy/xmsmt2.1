package net.wit.mobile.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.entity.Attribute;
import net.wit.entity.Brand;
import net.wit.entity.Product;
import net.wit.entity.Product.OrderType;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductCategoryTenant;
import net.wit.entity.ProductImage;
import net.wit.entity.Promotion;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.mobile.bo.ProductListBo;
import net.wit.mobile.service.INTokenBS;
import net.wit.service.GoodsService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductCategoryTenantService;
import net.wit.service.ProductService;
import net.wit.service.TenantService;
import net.wit.util.CacheUtil;
import net.wit.util.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.ld.slf4j.Logger;
import cn.ld.slf4j.LoggerFactory;

/**
 * 客户端商品控制类
 * @author Teddy
 */
@Controller
@RequestMapping(value = "/product")
public class ProductController extends BaseController {

	private Logger log = LoggerFactory.getLogger(AccountController.class);
	
    @Autowired
    private INTokenBS inTokenBS;
    @Autowired
    private ProductService productService;
    
   @Autowired
   private GoodsService goodsService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductCategoryTenantService productCategoryTenantService;
    
    /**
     * 内购店商品列表
     * tenantId：企业ID
     * pageNum：当前页数
     * pageSize：每页显示数
     * category：商品分类
     */
    @RequestMapping(value = "/productList")
    public void productList(HttpServletResponse response, 
    		@RequestParam("tenantId") String tenantId, @RequestParam("pageNumber") String pageNumber,
    		@RequestParam("pageSize") String pageSize, String categoryId, 
    		String categoryTenantId) throws Exception {
    	try {
//    		if (!inTokenBS.isVaild(token)) {
//    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
//    		} else {
    			log.info("请求参数为：" + "[tenantId:" + tenantId + ",pageNumber:" + pageNumber
        				+ ",pageSize:" + pageSize + ",category:" + categoryId + "]");
    			JSONObject resultValue = new JSONObject();
    			Tenant tenant = this.tenantService.find(new Long(tenantId));// 商家
    			ProductCategory productCategory = null;
    			ProductCategoryTenant productCategoryTenant = null;
    			if (categoryId != null && !"".equals(categoryId)) {
    				productCategory = productCategoryService.find(new Long(categoryId));// 产品分类
    			}
    			if (categoryTenantId != null && !"".equals(categoryTenantId)) {
    				productCategoryTenant = this.productCategoryTenantService.find(new Long(categoryTenantId));// 商家分类
    			} 
    			Brand brand = null;// 品牌
    			Promotion promotion = null;// 促销
    			List<Tag> tags = null;
    			Map<Attribute, String> attributeValue = new HashMap<Attribute, String>();
    			BigDecimal startPrice = null;
    			BigDecimal endPrice = null;
    			Boolean isMarketable = true;
    			Boolean isList = true;
    			Boolean isTop = null;
    			Boolean isGift = false;
    			Boolean isOutOfStock = null;
    			Boolean isStockAlert = null;
    			OrderType orderType = OrderType.sortAsc;
    			Pageable pageable = new Pageable();// 分页器
    			pageable.setPageNumber(Integer.parseInt(pageNumber));
    			pageable.setPageSize(Integer.parseInt(pageSize));
    			Page<Product> productList = this.productService.findMyPage(tenant, productCategory, 
    					productCategoryTenant, brand, promotion, tags, attributeValue, startPrice, 
    					endPrice, isMarketable, isList, isTop, isGift, 
    					isOutOfStock, isStockAlert, orderType, pageable);
    			List<ProductListBo> newProductlist=new ArrayList<ProductListBo>();
    	    for(Product product:productList.getContent()){
    	    	if(product==null) continue;
    	    	ProductListBo bo = new ProductListBo();
    	    	bo.setId(product.getId()+"");
    	    	bo.setName(product.getName()+"");
    	    	bo.setFullName(product.getFullName()+"");
    	    	bo.setMarketPrice(product.getMarketPrice());
    	    	bo.setePrice(product.getePrice());
    	    	if(product.getPriceType()==null)
    	    		bo.setPriceType(1);
    	    	else bo.setPriceType(product.getPriceType().ordinal());
    	    	bo.setRent(product.getRent()==null?new BigDecimal(0):product.getRent());
    	    	bo.setPrice(product.getPrice());
    	    	bo.setImage(product.getImage());
    	    	bo.setImageHeight(0);
    	    	bo.setImageWidth(0);
    	    	newProductlist.add(bo);
    	    }
    	    Page<ProductListBo> pageProductlist=new Page<ProductListBo>(newProductlist,productList.getTotal(),productList.getPageable());
          	resultValue.put("productList", JsonUtils.toJson(pageProductlist));// 订单号码
          	this.handleJsonResponse(response, true, "商品列表获取成功", resultValue);
//          }
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    
    
    @RequestMapping(value = "/getRecommendProductList")
    public void getRecommendProductList(HttpServletResponse response, 
    		@RequestParam("tenantId") String tenantId, @RequestParam("pageNumber") String pageNumber,
    		@RequestParam("pageSize") String pageSize, String categoryId, 
    		String categoryTenantId) throws Exception {
    	try {
//    		if (!inTokenBS.isVaild(token)) {
//    			this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
//    		} else {
    			log.info("请求参数为：" + "[tenantId:" + tenantId + ",pageNumber:" + pageNumber
        				+ ",pageSize:" + pageSize + ",category:" + categoryId + "]");
    			JSONObject resultValue = new JSONObject();
    			Tenant tenant = this.tenantService.find(new Long(tenantId));// 商家
    			ProductCategory productCategory = null;
    			ProductCategoryTenant productCategoryTenant = null;
    			if (categoryId != null && !"".equals(categoryId)) {
    				productCategory = productCategoryService.find(new Long(categoryId));// 产品分类
    			}
    			if (categoryTenantId != null && !"".equals(categoryTenantId)) {
    				productCategoryTenant = this.productCategoryTenantService.find(new Long(categoryTenantId));// 商家分类
    			} 
    			Brand brand = null;// 品牌
    			Promotion promotion = null;// 促销
    			List<Tag> tags = null;
    			Map<Attribute, String> attributeValue = new HashMap<Attribute, String>();
    			BigDecimal startPrice = null;
    			BigDecimal endPrice = null;
    			Boolean isMarketable = true;
    			Boolean isList = true;
    			Boolean isTop = null;
    			Boolean isGift = true;
    			Boolean isOutOfStock = null;
    			Boolean isStockAlert = null;
    			OrderType orderType = OrderType.dateDesc;
    			Pageable pageable = new Pageable();// 分页器
    			pageable.setPageNumber(Integer.parseInt(pageNumber));
    			pageable.setPageSize(Integer.parseInt(pageSize));
    			Page<Product> productList = this.productService.findMyPage(tenant, productCategory, 
    					productCategoryTenant, brand, promotion, tags, attributeValue, startPrice, 
    					endPrice, isMarketable, isList, isTop, isGift, 
    					isOutOfStock, isStockAlert, orderType, pageable);
    			List<ProductListBo> newProductlist=new ArrayList<ProductListBo>();
    	    for(Product product:productList.getContent()){
    	    	if(product==null) continue;
    	    	ProductListBo bo = new ProductListBo();
    	    	bo.setId(product.getId()+"");
    	    	bo.setName(product.getName()+"");
    	    	bo.setFullName(product.getFullName()+"");
    	    	bo.setMarketPrice(product.getMarketPrice());
    	    	bo.setePrice(product.getePrice());
    	    	if(product.getPriceType()==null)
    	    		bo.setPriceType(1);
    	    	else bo.setPriceType(product.getPriceType().ordinal());
    	    	bo.setRent(product.getRent()==null?new BigDecimal(0):product.getRent());
    	    	bo.setPrice(product.getPrice());
    	    	bo.setImage(product.getImage());
    	    	bo.setImageHeight(0);
    	    	bo.setImageWidth(0);
    	    	newProductlist.add(bo);
    	    }
    	    Page<ProductListBo> pageProductlist=new Page<ProductListBo>(newProductlist,productList.getTotal(),productList.getPageable());
          	resultValue.put("productList", JsonUtils.toJson(pageProductlist));// 订单号码
          	this.handleJsonResponse(response, true, "商品列表获取成功", resultValue);
//          }
    	} catch (Exception e) {
    		this.handleJsonResponse(response, false, e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    
    
    /**
     * 返回商品详情
     */
    @RequestMapping(value = "/detail")
    public void productDetails(
    		@RequestParam("token") String token, 
    		HttpServletResponse response,@RequestParam("productId") String productId
    		) throws Exception {
    	try {
    		if (!inTokenBS.isVaild(token)) {
				this.handleJsonTokenResponse(response, false, CacheUtil.getParamValueByName("TOKEN_INVALID"));
			} else {
	            if (productId!=null&&!(productId.equals(""))) {
	            	  JSONObject resultValue = new JSONObject();
	            	  resultValue.put("product", JsonUtils.toJson(productService.find(Long.parseLong(productId))));
	            	  this.handleJsonResponse(response, true,"",resultValue );
	            } else {
	            	this.handleJsonTokenResponse(response, false, "未选择商品");
	            }
			}
        } catch (Exception e) {
            this.handleJsonResponse(response, false, e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * 返回商家产品分类
     */
    @RequestMapping(value = "/productCategoryTenant")
    public void productCategoryTenant(
    		HttpServletResponse response,@RequestParam("tenantId") String tenantId
    		) throws Exception {
    	try {
	            if (tenantId!=null&&!(tenantId.equals(""))) {
	            	  JSONObject resultValue = new JSONObject();
	            	  Tenant tenant=new Tenant();
	            	  tenant.setId(Long.parseLong(tenantId));
	            	  resultValue.put("productCategoryTenant", JsonUtils.toJson(productCategoryTenantService.findRoots(tenant)));
	            	  this.handleJsonResponse(response, true,"",resultValue );
	            } else {
	            	this.handleJsonTokenResponse(response, false, "未选择品牌");
	            }
        } catch (Exception e) {
            this.handleJsonResponse(response, false, "查询分类失败");
            e.printStackTrace();
        }
    }
    
    /**
     * 商品详情
     * @author: weihuang.peng
     * @param productId
     * @param model
     * @return
     */
    @RequestMapping("/productDetail")
    public String productDetail(@RequestParam Long productId, Model model) {
    	Product product = productService.find(productId);
    	model.addAttribute("product", product);
    	return "mobile/productDetail";
    }
    @RequestMapping("/productDetail_ios")
    public String productDetail_ios(@RequestParam Long productId, Model model) {
    	Product product = productService.find(productId);
    	model.addAttribute("product", product);
    	return "mobile/productDetail_ios";
    }
}

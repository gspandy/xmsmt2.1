package net.wit.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import jodd.util.URLDecoder;
import net.sf.json.JSONObject;
import net.wit.FileInfo;
import net.wit.Message;
import net.wit.Page;
import net.wit.Pageable;
import net.wit.dao.PicDao;
import net.wit.entity.Admin;
import net.wit.entity.Area;
import net.wit.entity.Brand;
import net.wit.entity.Community;
import net.wit.entity.CompanyNotice;
import net.wit.entity.Location;
import net.wit.entity.Member;
import net.wit.entity.Pic;
import net.wit.entity.Product;
import net.wit.entity.ProductCategory;
import net.wit.entity.ProductImage;
import net.wit.entity.Promotion;
import net.wit.entity.ShareSet;
import net.wit.entity.Tag;
import net.wit.entity.Tenant;
import net.wit.entity.TenantBonusSet;
import net.wit.entity.TenantCategory;
import net.wit.entity.TenantRenovation;
import net.wit.entity.TenantSellCondition;
import net.wit.entity.TenantShopkeeper;
import net.wit.exception.TenantException;
import net.wit.mobile.service.IPushService;
import net.wit.mobile.util.rong.models.TxtMessage;
import net.wit.service.AdminService;
import net.wit.service.AreaService;
import net.wit.service.BrandService;
import net.wit.service.CommunityService;
import net.wit.service.CompanyNoticeService;
import net.wit.service.FileService;
import net.wit.service.MemberService;
import net.wit.service.PicService;
import net.wit.service.ProductCategoryService;
import net.wit.service.ProductService;
import net.wit.service.PromotionService;
import net.wit.service.TagService;
import net.wit.service.TenantBonusSetService;
import net.wit.service.TenantCategoryService;
import net.wit.service.TenantRenovationService;
import net.wit.service.TenantSellConditionService;
import net.wit.service.TenantService;
import net.wit.service.TenantShopkeeperService;
import net.wit.service.impl.ShareSetService;
import net.wit.support.EntitySupport;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminTenantController")
@RequestMapping({"/admin/tenant"})
public class TenantController extends BaseController
{
  private static final int PAGE_SIZE = 10;
  
  private static final String   ANNOUNCEMENT_PREFIX = "tenant_notice_";
  
  @Autowired
  private IPushService pushService;
  
  @Resource(name="productServiceImpl")
  private ProductService productService;
  
  @Resource(name="productCategoryServiceImpl")
  private ProductCategoryService productCategoryService;
  
  @Resource(name="brandServiceImpl")
  private BrandService brandService;
  
  @Resource(name="tenantRenovationServiceImpl")
  private TenantRenovationService tenantRenovationService;
  
  
  @Resource(name="promotionServiceImpl")
  private PromotionService promotionService;

  @Resource(name="tenantServiceImpl")
  private TenantService tenantService;

  @Resource(name="memberServiceImpl")
  private MemberService memberService;

  @Resource(name="areaServiceImpl")
  private AreaService areaService;

  @Resource(name="communityServiceImpl")
  private CommunityService communityService;

  @Resource(name="tenantCategoryServiceImpl")
  private TenantCategoryService tenantCategoryService;

  @Resource(name="tenantSellConditionServiceImpl")
  private TenantSellConditionService tenantSellConditionService;
  
  @Resource(name="fileServiceImpl")
  private FileService fileService;
  
  @Resource(name="picServiceImpl")
  private PicService picService;
  
  @Resource(name="tenantBonusSetServiceImpl")
  private TenantBonusSetService tenantBonusSetService;
  @Resource(name="tagServiceImpl")
  private TagService tagService;
  
  @Resource(name="adminServiceImpl")
  private AdminService adminService;
  
  @Resource(name="companyNoticeServiceImpl")
  private CompanyNoticeService companyNoticeService;
  
  @Resource(name="shareSetServiceImpl")
  private ShareSetService shareSetService;
  
  @Resource(name="picDaoImpl")
  private PicDao picDaoImpl;
  
  
  @Autowired
  private TenantShopkeeperService tenantShopkeeperService;

  @RequestMapping(value={"/get_community"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Map<Long, String> getCommunity(Long areaId)
  {
    Map data = new HashMap();
    Area area = (Area)this.areaService.find(areaId);
    List<Community> communitys = this.communityService.findList(area);
    for (Community community : communitys) {
      data.put(community.getId(), community.getName());
    }
    return data;
  }

  @RequestMapping(value={"/edit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edit(Long id, ModelMap model)
  {
    Tenant tenant = (Tenant)this.tenantService.find(id);
    model.addAttribute("tags", this.tagService.findList(Tag.Type.tenant));
    model.addAttribute("uniontags", this.tagService.findList(Tag.Type.tenantUnion));
    model.addAttribute("tenantCategoryTree", this.tenantCategoryService.findTree());
    model.addAttribute("tenant", tenant);
    model.addAttribute("logoUrl",tenant.getLogo());
    model.addAttribute("community", tenant.getCommunity());
    try
    {
      model.addAttribute("members", this.memberService.findList(tenant));
      model.addAttribute("salesmans", this.memberService.findList(new Tenant(1L)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "admin/tenant/edit";
  }
  //企业分享页
  @RequestMapping(value={"/shareEdit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String shareEdit(ModelMap model)
  { 
	Admin admin = adminService.getCurrent();
	List<ShareSet> shareList=shareSetService.getShareSetByTenant(admin.getTenant());
	Map<String,Pic> map=picService.findMap(admin.getTenant());
	model.addAttribute("shareList",shareList);
	model.addAttribute("ticketShare", map.get("ticketShare"));
	model.addAttribute("tenantInvitation", map.get("tenantInvitation"));
	model.addAttribute("shopkeepInvitation", map.get("shopkeepInvitation"));
    return "admin/tenant/shareEdit";
  }
 
  @RequestMapping(value={"/edits"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String edits(Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Location location, BigDecimal distance, Pageable pageable, ModelMap model, HttpServletRequest request)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    Admin admin = adminService.getCurrent();
    Area area = this.areaService.getCurrent();
    ProductCategory productCategory = (ProductCategory)this.productCategoryService.find(productCategoryId);
    Brand brand = (Brand)this.brandService.find(brandId);
    Promotion promotion = (Promotion)this.promotionService.find(promotionId);
    List tags = this.tagService.findList(new Long[] { tagId });
    List<Product> productList=this.productService.findList(admin.getTenant(), true);
    Tenant tenant = (Tenant)this.tenantService.find(admin.getTenant().getId());
    List<TenantRenovation> list=tenantRenovationService.getTenantRenovationByTenant(admin.getTenant().getId());
    if(list.size()==0){
    	list=null;
    }
    model.addAttribute("tenant", tenant);
    model.addAttribute("sell", tenantSellConditionService.getRegularTenantSellConditionByTenantId((admin.getTenant().getId())));
    model.addAttribute("bonusSet",tenantBonusSetService.getRegularTenantBonusSetByTenantId(admin.getTenant().getId()));
    model.addAttribute("renovationList",list);
    model.addAttribute("productCategoryTree", this.productCategoryService.findTree());
//    model.addAttribute("brands", this.brandService.findList(adminService.getCurrent().getTenant()));
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
    model.addAttribute("productList",productList );
    model.addAttribute("tags", this.tagService.findList(Tag.Type.tenant));
    return "admin/tenant/edits";
  }
  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String add(Long productCategoryId, Long brandId, Long promotionId, Long tagId, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isGift, Boolean isOutOfStock, Boolean isStockAlert, Location location, BigDecimal distance, Pageable pageable, ModelMap model, HttpServletRequest request)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
	  Area area = this.areaService.getCurrent();
	  ProductCategory productCategory = (ProductCategory)this.productCategoryService.find(productCategoryId);
	  Brand brand = (Brand)this.brandService.find(brandId);
	  Promotion promotion = (Promotion)this.promotionService.find(promotionId);
	  List tags = this.tagService.findList(new Long[] { tagId });
	    model.addAttribute("tags", this.tagService.findList(Tag.Type.tenant));
    return "admin/tenant/add";
  }
  
  @RequestMapping(value={"/ajaxAdd"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Map ajaxAdd(String searchValue, Pageable pageable,ModelMap model, HttpServletRequest request)
  {
	  if(searchValue==null)
	  {
		  searchValue="";
	  }
	  searchValue = URLDecoder.decode(searchValue, "UTF-8"); 
	  Admin admin = adminService.getCurrent();
	  Page<Product> productList=new Page<Product>();
	  productList = this.productService.findPage(admin.getTenant().getId(),searchValue, pageable);
//	  Set<Page<Product>> products = new HashSet<Page<Product>>();
//	  products.add(productList);
//	  return productList.getContent();
	  List<Product> list=new ArrayList<Product>();
	  for(Product product:productList.getContent()){
		  list.add(product);
	  }
	  Map map= new HashMap();
	  map.put("productList", list);
	  map.put("pageNumber", productList.getPageNumber());
	  map.put("totalPages", productList.getTotalPages());
	  return map;
  }
  

  @RequestMapping(value={"/list"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String list(Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.tenantService.findPage(pageable));
    return "/admin/tenant/list";
  }
  
  //企业分享页保存
  
  @RequestMapping(value={"/shareSave"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String shareSave(Long ticketShareId,Long tenantInvitationId,Long shopkeepInvitationId,MultipartFile ticketShareFile ,MultipartFile tenantInvitationFile,MultipartFile shopkeepInvitationFile,RedirectAttributes redirectAttributes) throws TenantException
  {
	 Admin admin = adminService.getCurrent();
    if ((ticketShareFile != null) && (!(ticketShareFile.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, ticketShareFile))) {
        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
        return "redirect:shareEdit.jhtml";
      }     
    }
    if ((tenantInvitationFile != null) && (!(tenantInvitationFile.isEmpty()))) {
        if (!(this.fileService.isValid(FileInfo.FileType.image, tenantInvitationFile))) {
          addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
          return "redirect:shareEdit.jhtml";
        }     
      }
    if ((shopkeepInvitationFile != null) && (!(shopkeepInvitationFile.isEmpty()))) {
        if (!(this.fileService.isValid(FileInfo.FileType.image, shopkeepInvitationFile))) {
          addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
          return "redirect:shareEdit.jhtml";
        }     
      }
    Pic ticketSharePic = this.picService.uploadPic(admin.getTenant().getId(), "ticket_share", ticketShareFile);
    Pic tenantInvitationPic = this.picService.uploadPic(admin.getTenant().getId(), "tenant_invitation", tenantInvitationFile);
    Pic shopkeepInvitationPic = this.picService.uploadPic(admin.getTenant().getId(), "shopkeep_invitation", shopkeepInvitationFile);
    if(ticketSharePic!=null&&ticketShareId!=null)
    {
    	picService.delete(ticketShareId);
    }
    if(tenantInvitationPic!=null&&tenantInvitationId!=null)
    {
    	picService.delete(tenantInvitationId);
    }
    if(shopkeepInvitationPic!=null&&shopkeepInvitationId!=null)
    {
    	picService.delete(shopkeepInvitationId);
    }
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:shareEdit.jhtml";
  }
  
  @RequestMapping(value={"/shareSetSave"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String shareSetSave(Long ticketShareTitleId,Long ticketShareId,Long invitationShareTitleId,Long invitaionShareId,String ticketShareTitle,String ticketShare,String invitationShareTitle,String invitaionShare,RedirectAttributes redirectAttributes) throws TenantException
  {
		Admin admin = adminService.getCurrent();
		try {
			ShareSet share1 = new ShareSet();
			share1.setType(ShareSet.Type.ticketShareTitle);
			share1.setId(ticketShareTitleId);
			share1.setContent(ticketShareTitle);
			share1.setTenant(admin.getTenant());
			if (ticketShareTitle != null && ticketShareTitle != "") {
				if(ticketShareTitleId==null){
					shareSetService.save(share1);
				}else{
					shareSetService.update(share1);
				}
			}
			ShareSet share2 = new ShareSet();
			share2.setType(ShareSet.Type.ticketShare);
			share2.setId(ticketShareId);
			share2.setContent(ticketShare);
			share2.setTenant(admin.getTenant());
			if (ticketShare != null && ticketShare != "") {
				
				if(ticketShareId==null){
					shareSetService.save(share2);
				}else{
					shareSetService.update(share2);
				}
			}
			ShareSet share3 = new ShareSet();
			share3.setType(ShareSet.Type.invitationShareTitle);
			share3.setId(invitationShareTitleId);
			share3.setContent(invitationShareTitle);
			share3.setTenant(admin.getTenant());
			if (invitationShareTitle != null && invitationShareTitle != "") {
				
				if(invitationShareTitleId==null){
					shareSetService.save(share3);
				}else{
					shareSetService.update(share3);
				}
			}
			ShareSet share4 = new ShareSet();
			share4.setType(ShareSet.Type.invitaionShare);
			share4.setId(invitaionShareId);
			share4.setContent(invitaionShare);
			share4.setTenant(admin.getTenant());
			if (invitaionShare != null && invitaionShare != "") {
				
				if(invitaionShareId==null){
					shareSetService.save(share4);
				}else{
					shareSetService.update(share4);
				}
			}
		} catch (Exception localException) {
			System.out.print(localException.getMessage());
			addFlashMessage(redirectAttributes, Message.error("部分信息保存失败", new Object[0]));
			return "redirect:shareEdit.jhtml";
		}
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:shareEdit.jhtml";
  }
  
  @RequestMapping(value={"/save"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String save(Tenant tenant, String typeId,Tenant.Status status, Long tenantCategoryId, String shortName, Long[] tagIds, Long[] unionTagIds,  MultipartFile file , MultipartFile imageFile ,MultipartFile openShopFile,MultipartFile shareImageFile,RedirectAttributes redirectAttributes) throws TenantException
  {
    Tenant saveTenant = null;
    String url="redirect:list.jhtml";
    if ((tenant.getSalesman() == null) || (tenant.getSalesman().getId() == null)) {
      tenant.setSalesman(null);
    }
    if (tenant.getFirstRentFreePeriod()==null)
    {
    	tenant.setFirstRentFreePeriod(0);
    }
    if ((tenant.getId() != null) && (tenant.getId().longValue() != 0L))
      saveTenant = (Tenant)this.tenantService.find(tenant.getId());
    else {
      saveTenant = EntitySupport.createInitTenant();
    }
    if(typeId!=null&&typeId.equals("1")){
    	url="redirect:edits.jhtml";
    }
    tenant.setMember(saveTenant.getMember());
    Tenant newtenant=new Tenant();
    newtenant.setProductCategories(saveTenant.getProductCategories());
    BeanUtils.copyProperties(tenant, saveTenant, new String[] { "member", "code", "score", "totalAssistant", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "createDate", "modifyDate", "status", "logo", "licensePhoto" });
    saveTenant.setProductCategories(newtenant.getProductCategories());
    if ((file != null) && (!(file.isEmpty()))) {
      if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
        return url;
      }     
    }
    if ((imageFile != null) && (!(imageFile.isEmpty()))) {
        if (!(this.fileService.isValid(FileInfo.FileType.image, imageFile))) {
          addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
          return url;
        }     
      }
    if ((openShopFile != null) && (!(openShopFile.isEmpty()))) {
        if (!(this.fileService.isValid(FileInfo.FileType.image, openShopFile))) {
          addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
          return url;
        }     
      }
    if ((shareImageFile != null) && (!(shareImageFile.isEmpty()))) {
        if (!(this.fileService.isValid(FileInfo.FileType.image, shareImageFile))) {
          addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
          return url;
        }     
      }
    saveTenant.setTags(new HashSet(this.tagService.findList(tagIds)));
    saveTenant.setUnionTags(new HashSet(this.tagService.findList(unionTagIds)));
    saveTenant.setShortName(shortName);
    saveTenant.setStatus(status);
    Pic pic = this.picService.uploadPic(saveTenant.getId(), "logo", file);
    Pic invationPic = this.picService.uploadPic(saveTenant.getId(), "invation", imageFile);
    Pic opneShowPic = this.picService.uploadPic(saveTenant.getId(), "opneShow", openShopFile);
    Pic shareImagePic = this.picService.uploadPic(saveTenant.getId(), "shareImage", shareImageFile);
    if(pic!=null)
    {
    	saveTenant.setLogoId(pic.getId());
    	saveTenant.setLogo(pic.getLargeUrl());
    }
    if(invationPic!=null)
    {
    	saveTenant.setInvationImageId(invationPic.getId());
    	saveTenant.setInvationImage(invationPic.getLargeUrl());
    } 
    if(opneShowPic!=null)
    {
    	saveTenant.setOpenShopImageId(opneShowPic.getId());
        saveTenant.setOpenShopImage(opneShowPic.getLargeUrl());
    }
    if(shareImagePic!=null)
    {
        saveTenant.setShareImage(shareImagePic.getMediumUrl());
    }
    List<Pic> shareImageList=picDaoImpl.getPicByTenantAndType(saveTenant, "shareImage");
    for(Pic pics:shareImageList){
   	  if(!(pics.getMediumUrl().equals(saveTenant.getShareImage()))){
   		  picService.delete(pics);
   	   }
    }
    List<Pic> logoList=picDaoImpl.getPicByTenantAndType(saveTenant, "logo");
    for(Pic pics:logoList){
   	  if(!(pics.getLargeUrl().equals(saveTenant.getLogo()))){
   		  picService.delete(pics);
   	   }
    }
    List<Pic> invationList=picDaoImpl.getPicByTenantAndType(saveTenant, "invation");
    for(Pic pics:invationList){
   	  if(!(pics.getLargeUrl().equals(saveTenant.getInvationImage()))){
   		  picService.delete(pics);
   	   }
    }
    List<Pic> opneShowList=picDaoImpl.getPicByTenantAndType(saveTenant, "opneShow");
    for(Pic pics:opneShowList){
   	  if(!(pics.getLargeUrl().equals(saveTenant.getOpenShopImage()))){
   		  picService.delete(pics);
   	   }
    }
    if(typeId==null)
    {
    	this.tenantService.save(saveTenant);
    	adminService.initSuperAdmin(saveTenant, tenant.getUsername(), tenant.getPassword());
    }else
    {
    	this.tenantService.update(saveTenant);
    }
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return url;
  }
  
  @RequestMapping(value={"/sellSave"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String sellSave(TenantSellCondition sell,RedirectAttributes redirectAttributes)
  {
	  Admin admin = adminService.getCurrent();
	  sell.setTenantId(admin.getTenant().getId());
	  sell.setApplyEnabled("1");
	  this.tenantSellConditionService.update(sell);
	  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	  return "redirect:edits.jhtml";
	  
  }
  @RequestMapping(value={"/bonusSave"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String bonusSave(TenantBonusSet bonus,RedirectAttributes redirectAttributes)
  {
	  Admin admin = adminService.getCurrent();
	  bonus.setTenantId(admin.getTenant().getId());
	  bonus.setStatus("1");
	  this.tenantBonusSetService.update(bonus);
	  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	  return "redirect:edits.jhtml";
	
	  
  }
  
  @RequestMapping(value={"/renovationSave"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String renovationSave(String image,Long id,String url,String skipType,Long productId,String imageT,Long idT,String urlT,String skipTypeT,Long productIdT,String imageTH,Long idTH,String urlTH,String skipTypeTH,Long productIdTH,Product product,RedirectAttributes redirectAttributes)
  {
	  Admin admin = adminService.getCurrent();
	  
	  int i=0;
	  
	  for(ProductImage productImage:product.getProductImages()){
		  Product newProduct=new Product();
		  MultipartFile file=productImage.getFile();
		  if ((file != null) && (!(file.isEmpty()))) {
		      if (!(this.fileService.isValid(FileInfo.FileType.image, file))) {
		        addFlashMessage(redirectAttributes, Message.error("无效的文件类型", new Object[0]));
		        return "redirect:edits.jhtml";
		      }      
		    }
		  TenantRenovation tenantRenovation=new TenantRenovation();
		  Pic pic = this.picService.uploadPic(admin.getTenant().getId(), "pic", file);
		  if(i==0){
			  tenantRenovation.setId(id);
			  tenantRenovation.setBannerNum(1);
			  tenantRenovation.setUrl(url);
			  tenantRenovation.setSkipType(skipType);
			  if(productId!=null){
				  newProduct.setId(productId);
				  tenantRenovation.setProduct(newProduct);
			  }
			  tenantRenovation.setPicId(image);
		  }
		  if(i==1){
			  tenantRenovation.setId(idT);
			  tenantRenovation.setBannerNum(2);
			  tenantRenovation.setUrl(urlT);
			  tenantRenovation.setSkipType(skipTypeT);
			  if(productIdT!=null){
			  newProduct.setId(productIdT);
              tenantRenovation.setProduct(newProduct);
			  }
			  tenantRenovation.setPicId(imageT);
		  }
		  if(i==2){
			  tenantRenovation.setId(idTH);
			  tenantRenovation.setBannerNum(3);
			  tenantRenovation.setUrl(urlTH);
			  tenantRenovation.setSkipType(skipTypeTH);
			  if(productIdTH!=null){
			  newProduct.setId(productIdTH);
              tenantRenovation.setProduct(newProduct);
			  }
			  tenantRenovation.setPicId(imageTH);
		  }
		  if(pic!=null){
			  tenantRenovation.setPicId(pic.getLargeUrl());
		  }
		  tenantRenovation.setTenantId(admin.getTenant().getId());
		  this.tenantRenovationService.update(tenantRenovation);
		  i++;
	  }
	  
	
	  addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
	  return "redirect:edits.jhtml";
	  
  }

  //
  @RequestMapping(value={"/checkList"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String checkList(Pageable pageable, ModelMap model)
  {
	  String searchValue = null;
		try {
			searchValue=new String(pageable.getSearchValue().getBytes("ISO-8859-1"),"UTF-8");
			pageable.setSearchValue(searchValue);
		} catch (Exception localException) {
		}
    model.addAttribute("page", this.tenantService.findPage(pageable));
    return "/admin/tenant/checkList";
  }

  @RequestMapping(value={"/checkEdit"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String checkEdit(Long id, ModelMap model, RedirectAttributes redirectAttributes)
  {
    Tenant tenant = (Tenant)this.tenantService.find(id);
    Member member = tenant.getMember();
    ResourceBundle bundle = PropertyResourceBundle.getBundle("config");

    model.addAttribute("tags", this.tagService.findList(Tag.Type.tenant));
    model.addAttribute("tenantCategoryTree", this.tenantCategoryService.findTree());
    model.addAttribute("tenant", tenant);
    return "admin/tenant/checkEdit";
  }

  @RequestMapping(value={"/checkSave"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String checkSave(Long id, Tenant tenant, Tenant.Status status, Long tenantCategoryId, long areaId, String shortName, Long[] tagIds, long communityId, RedirectAttributes redirectAttributes)
  {
    Tenant saveTenant = null;
    saveTenant = (Tenant)this.tenantService.find(id);
    Member member = saveTenant.getMember();
    BeanUtils.copyProperties(tenant, saveTenant, new String[] { "code", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "createDate", "modifyDate", "status", "logo", "licensePhoto", "totalAssistant" });
    saveTenant.setTags(new HashSet(this.tagService.findList(tagIds)));
    saveTenant.setArea((Area)this.areaService.find(Long.valueOf(areaId)));
    saveTenant.setShortName(shortName);
    saveTenant.setStatus(status);
    saveTenant.setTenantCategory((TenantCategory)this.tenantCategoryService.find(tenantCategoryId));
    saveTenant.setMember(member);
    saveTenant.setTotalAssistant(Long.valueOf(0L));
    if (communityId != 0L)
      saveTenant.setCommunity((Community)this.communityService.find(Long.valueOf(communityId)));
    else {
      saveTenant.setCommunity(null);
    }
    this.tenantService.save(saveTenant);
    addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
    return "redirect:checkList.jhtml";
  }

  @RequestMapping(value={"/memberlist"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String memberlist(Long id, Integer pageNumber, ModelMap model)
  {
    Tenant tenant = (Tenant)this.tenantService.find(id);

    Pageable pageable = new Pageable(pageNumber, Integer.valueOf(10));
    model.addAttribute("page", this.memberService.findPage(tenant, pageable));
    model.addAttribute("id", id);
    return "admin/tenant/memberlist";
  }

  @RequestMapping(value={"/salesMan"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String salesMan(Long id, ModelMap model)
  {
    Tenant tenant = (Tenant)this.tenantService.find(id);
    model.addAttribute("page", this.memberService.findPage(tenant, null));
    model.addAttribute("id", id);
    return "/admin/tenant/salesMan";
  }

  @RequestMapping(value={"/saveSalesman"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  public String saveSalesman(Long id, Long salesmanId, ModelMap model)
  {
    Member salesman = (Member)this.memberService.find(salesmanId);
    Tenant tenant = (Tenant)this.tenantService.find(id);
    tenant.setSalesman(salesman);
    this.tenantService.save(tenant);
    return "redirect:salesMan.jhtml";
  }

  @RequestMapping(value={"/remove"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Message remove(Long id)
  {
    Tenant tenant = (Tenant)this.tenantService.find(id);
    tenant.setSalesman(null);
    this.tenantService.save(tenant);
    return SUCCESS_MESSAGE;
  }
  
  	/**
	 * 公告界面
	 * @author: weihuang.peng
	 * @return
	 */
	@RequestMapping("/announcement")
	public String showAnnouncement(Pageable pageable, ModelMap model) {
		model.addAttribute("page", this.companyNoticeService.PageALL(adminService.getCurrent().getTenant(),pageable));
		return "admin/tenant/announcement";
	}
	
	/**
	 * 发送公告("给自己发消息是没有用户名的，亲测")
	 * @author: weihuang.peng
	 * @param content
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/send_announcement") 
	public Message sendAnnouncement(@RequestParam String content, @RequestParam String title) {
		Admin admin = adminService.getCurrent();
		String fromUserId = ANNOUNCEMENT_PREFIX+admin.getId();
		pushService.getToken(fromUserId+"", admin.getUsername(), "");
		List<String> toUserIds = new ArrayList<String>();
		List<TenantShopkeeper> list = tenantShopkeeperService.findShopKeeperByTenantId(admin.getTenant().getId());
		for (TenantShopkeeper tenantShopkeeper : list) {
			toUserIds.add(tenantShopkeeper.getMember().getId()+"");
		}
		// 查找企业下所有的店主 TODO
//		TxtMessage msg = new TxtMessage(content, title);
		JSONObject js =new JSONObject();
		js.put("title", title);
		js.put("content", content);
		boolean sdkResult = pushService.publishNoticeMessage(fromUserId, toUserIds, title, js.toString());
		CompanyNotice entity=new CompanyNotice();
		entity.setTitle(title);
		entity.setContent(content);
		entity.setTenant(admin.getTenant());
		companyNoticeService.save(entity);
		if(sdkResult){
			return Message.success("发送成功", sdkResult);
		}
		else return Message.error("发送失败! 可能融云系统连接失败, 请联系平台客服,", sdkResult);
	}
	 @RequestMapping(value={"/delete"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
	  @ResponseBody
	  public Message delete(Long[] ids)
	  {
	    this.companyNoticeService.delete(ids);
	    return SUCCESS_MESSAGE;
	  }
}
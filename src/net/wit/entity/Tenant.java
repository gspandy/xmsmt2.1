package net.wit.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import net.wit.constant.SettingConstant;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.NumericField;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

/**
 * @ClassName: Tenant
 * @Description: 企业实体类
 * @author Administrator
 * @date 2014年8月25日 上午9:42:25
 */
@Entity
@Table(name = "xx_tenant")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_tenant_sequence")
public class Tenant extends BaseEntity {

	private static final long serialVersionUID = -2735033966597250149L;
	
	public static final  Integer IS_DISCOVER = 1;   //是发现中的推荐企业;

	/** 企业类型 */
	public enum TenantType {

		/** 企业单位 */
		enterprise, /** 个体经营 */
		individual, /** 事业单位或团体 */
		organization, /** 个人 */
		personal
	}

	/**
	 * 店铺状态
	 */
	public enum Status {
		/** 未确认 */
		none, /** 已确认 */
		wait, /** 已认证 */
		success, /** 已禁用 */
		fail
	}

	/**
	 * 排序类型
	 */
	public enum OrderType {
		/** 权重排序 */
		weight, /** 置顶降序 */
		topDesc, /** 价格升序 */
		priceAsc, /** 价格降序 */
		priceDesc, /** 销量降序 */
		salesDesc, /** 评分降序 */
		scoreDesc, /** 日期降序 */
		dateDesc
	}

	@Expose
	private String code; // 企业编码

	@Expose
	private String name; // 企业名称

	@Expose
	private String shortName; // 企业简称

	@Expose
	private TenantType tenantType; // 企业类型

	@Expose
	private String licenseCode; // 经营许可证

	private String spell; // 拼音码

	@Expose
	private String legalRepr; // 法人代表

	@Expose
	private String linkman; // 联系人

	@Expose
	private String telephone; // 联系电话

	@Expose
	private String faxes; // 传真

	@Expose
	private String homepage; // 主页

	@Expose
	private String address; // 地址

	@Expose
	private String zipcode; // 邮编

	@Expose
	private String qq;// 企业qq

	private String domain; // 绑定域名

	@Expose
	private Status status; // 状态

	private Member member; // 店主

	private Member salesman; // 企业业务员

	private Community community; // 社区

	private Integer firstRentFreePeriod;  //是否为发现企业.  0 否,1是

	/** 搜索关键词 */
	private String keyword;

	/** 页面标题 */
	private String seoTitle;

	/** 页面关键词 */
	private String seoKeywords;

	/** 页面描述 */
	private String seoDescription;

	/** 安装参考价/最低配送金额 */
	private String price;

	/** 佣金比率 */
	private BigDecimal brokerage;

	/** 商家模版 */
	private String template;

	/** 平均评分 */
	@Expose
	private Float score;

	/** 总评分 */
	@Expose
	private Long totalScore;

	/** 总配送评分 */
	@Expose
	private Long totalAssistant;

	/** 评分数 */
	private Long scoreCount;

	/** 点击数 */
	private Long hits;

	/** 周点击数 */
	private Long weekHits;

	/** 月点击数 */
	private Long monthHits;

	/** 商家摄像头标识列表 */
	private List<String> videos = new ArrayList<String>();

	/** 商家微信公众号 */
	private TenantWechat tenantWechat;

	/** logo */
	@Expose
	private String logo;

	private Long logoId;

	private Long invationImageId;

	private String invationImage;

	private Long openShopImageId;

	private String openShopImage;

	// 超级管理员账户
	private String username;

	// 超级管理员密码
	private String password;

	/** 介绍 */
	@Expose
	private String introduction;

	/** 企业分类 */
	private TenantCategory tenantCategory;

	private Area area; // 区域

	@Expose
	private BigDecimal distatce;

	/** 营业执照 **/
	private String licensePhoto;

	/** 发货点 */
	@Expose
	private Set<DeliveryCenter> deliveryCenters = new HashSet<DeliveryCenter>();

	/** 标签 */
	private Set<Tag> tags = new HashSet<Tag>();

	/** 商家联盟标签 */
	private Set<Tag> unionTags = new HashSet<Tag>();

	/** 文章 */
	private Set<Article> articles = new HashSet<Article>();

	/** 商家促销方案 */
	@Expose
	private Set<Promotion> promotions = new HashSet<Promotion>();

	/** 商家商品分类方案 */
	private Set<ProductCategoryTenant> productCategoryTenants = new HashSet<ProductCategoryTenant>();

	/** 参与申请广告位 */
	private Set<AdPositionTenant> adPositionTenants = new HashSet<AdPositionTenant>();

	/** 关注会员 */
	private Set<Member> favoriteMembers = new HashSet<Member>();

	/** 绑定银行卡 */
	private Set<MemberBank> memberBanks = new HashSet<MemberBank>();

	/** 易宝注册子帐号编号 */
	private String ledgerno;

	/** 分享页图片 */
	private String shareImage;

	/** 商家经营商品分类 */
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();

	public Tenant() {
		super();
	}

	public Tenant(long l) {
		super();
		setId(l);
	}

	/**
	 * 获取营业执照
	 * @return
	 */
	public String getLicensePhoto() {
		return licensePhoto;
	}

	/**
	 * 设置营业执照
	 * @param licensePhoto
	 */
	public void setLicensePhoto(String licensePhoto) {
		this.licensePhoto = licensePhoto;
	}

	/** 商家微信公众号 */
	@Embedded
	public TenantWechat getTenantWechat() {
		return tenantWechat;
	}

	/** 商家微信公众号 */
	public void setTenantWechat(TenantWechat tenantWechat) {
		this.tenantWechat = tenantWechat;
	}

	@JsonProperty
	@Column(nullable = false, length = 255)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty
	@Column(nullable = false, length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取企业分类
	 * @return 企业分类
	 */
	@JsonProperty
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	public TenantCategory getTenantCategory() {
		return tenantCategory;
	}

	/**
	 * 设置企业分类
	 * @param productCategory 企业分类
	 */
	public void setTenantCategory(TenantCategory tenantCategory) {
		this.tenantCategory = tenantCategory;
	}

	@JsonProperty
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Column(length = 255)
	public TenantType getTenantType() {
		return tenantType;
	}

	public void setTenantType(TenantType tenantType) {
		this.tenantType = tenantType;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@JsonProperty
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Column(length = 255)
	public String getLicenseCode() {
		return licenseCode;
	}

	public void setLicenseCode(String licenseCode) {
		this.licenseCode = licenseCode;
	}

	@Column(length = 255)
	public String getSpell() {
		return spell;
	}

	public void setSpell(String spell) {
		this.spell = spell;
	}

	@JsonProperty
	@Column(length = 255)
	public String getLegalRepr() {
		return legalRepr;
	}

	public void setLegalRepr(String legalRepr) {
		this.legalRepr = legalRepr;
	}

	@Column(length = 255)
	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	@JsonProperty
	@Column(length = 255)
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@JsonProperty
	@Column(length = 255)
	public String getFaxes() {
		return faxes;
	}

	public void setFaxes(String faxes) {
		this.faxes = faxes;
	}

	@Column(length = 255)
	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	@JsonProperty
	@Column(length = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(length = 255)
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@JsonProperty
	@Column(length = 20)
	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	/**
	 * 获取logo
	 * @return logo
	 */
	@JsonProperty
	@Length(max = 200)
	public String getLogo() {
		return logo;
	}

	/**
	 * 设置logo
	 * @param logo logo
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * 获取介绍
	 * @return 介绍
	 */
	@Lob
	@JsonProperty
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * 设置介绍
	 * @param introduction 介绍
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	// 获取地区
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Area getArea() {
		return area;
	}

	// 设置地区
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 所属店主
	 * @return 店主
	 */
	@OneToOne(fetch = FetchType.LAZY)
	public Member getMember() {
		return member;
	}

	/**
	 * 设置店主
	 * @param member 店主
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 所属业务员
	 * @return 业务员
	 */
	@OneToOne(fetch = FetchType.LAZY)
	public Member getSalesman() {
		return salesman;
	}

	/**
	 * 设置企业业务员
	 * @param salesman 企业业务员
	 */
	public void setSalesman(Member salesman) {
		this.salesman = salesman;
	}

	/**
	 * 所属社区
	 * @return 社区
	 */
	@JsonProperty
	@ManyToOne(fetch = FetchType.LAZY)
	public Community getCommunity() {
		return community;
	}

	/**
	 * 设置社区
	 * @param community 社区
	 */
	public void setCommunity(Community community) {
		this.community = community;
	}

	/**
	 * 获取标签
	 * @return 标签
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_tenant_tag")
	@OrderBy("order asc")
	public Set<Tag> getTags() {
		return tags;
	}

	/**
	 * 设置标签
	 * @param tags 标签
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * 获取商盟标签
	 * @return 商盟标签
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_tenant_union_tag")
	@OrderBy("order asc")
	public Set<Tag> getUnionTags() {
		return unionTags;
	}

	/**
	 * 设置商盟标签
	 * @param unionTags 商盟标签
	 */
	public void setUnionTags(Set<Tag> unionTags) {
		this.unionTags = unionTags;
	}

	@JsonProperty
	@OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<ProductCategoryTenant> getProductCategoryTenants() {
		return productCategoryTenants;
	}

	/**
	 * 设置商家商品分类
	 * @param promotions 商家商品分类
	 */
	public void setProductCategoryTenants(Set<ProductCategoryTenant> productCategoryTenants) {
		this.productCategoryTenants = productCategoryTenants;
	}

	@OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Promotion> getPromotions() {
		return promotions;
	}

	/**
	 * 设置促销方案
	 * @param promotions 促销方案
	 */
	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取申请商家
	 * @return 申请商家
	 */
	@OneToMany(mappedBy = "adPosition", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<AdPositionTenant> getAdPositionTenants() {
		return adPositionTenants;
	}

	/**
	 * 设置申请商家
	 * @param adPositionTenants 申请商家
	 */
	public void setAdPositionTenants(Set<AdPositionTenant> adPositionTenants) {
		this.adPositionTenants = adPositionTenants;
	}

	/**
	 * 获取搜索关键词
	 * @return 搜索关键词
	 */
	@Field(store = Store.YES, index = Index.TOKENIZED, analyzer = @Analyzer(impl = IKAnalyzer.class) )
	@Length(max = 200)
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置搜索关键词
	 * @param keyword 搜索关键词
	 */
	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.keyword = keyword;
	}

	/**
	 * 获取关注会员
	 * @return 关注商家
	 */
	@ManyToMany(mappedBy = "favoriteTenants", fetch = FetchType.LAZY)
	public Set<Member> getFavoriteMembers() {
		return favoriteMembers;
	}

	/**
	 * 设置关注商家
	 * @param favoriteProducts 关注商家
	 */
	public void setFavoriteMembers(Set<Member> favoriteMembers) {
		this.favoriteMembers = favoriteMembers;
	}

	/**
	 * 获取页面标题
	 * @return 页面标题
	 */
	@Length(max = 200)
	public String getSeoTitle() {
		return seoTitle;
	}

	/**
	 * 设置页面标题
	 * @param seoTitle 页面标题
	 */
	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

	/**
	 * 获取页面关键词
	 * @return 页面关键词
	 */
	@Length(max = 200)
	public String getSeoKeywords() {
		return seoKeywords;
	}

	/**
	 * 设置页面关键词
	 * @param seoKeywords 页面关键词
	 */
	public void setSeoKeywords(String seoKeywords) {
		if (seoKeywords != null) {
			seoKeywords = seoKeywords.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.seoKeywords = seoKeywords;
	}

	/**
	 * 获取页面描述
	 * @return 页面描述
	 */
	@Length(max = 200)
	public String getSeoDescription() {
		return seoDescription;
	}

	/**
	 * 设置页面描述
	 * @param seoDescription 页面描述
	 */
	public void setSeoDescription(String seoDescription) {
		this.seoDescription = seoDescription;
	}

	/**
	 * 安装参考价/最低配送金额
	 * @return 安装参考价/最低配送金额
	 */
	@Length(max = 200)
	public String getPrice() {
		return price;
	}

	/**
	 * 安装参考价/最低配送金额
	 * @param price 安装参考价/最低配送金额
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * 佣金比率
	 * @return 佣金比率
	 */
	@Column(nullable = true, precision = 12, scale = 6)
	public BigDecimal getBrokerage() {
		return brokerage;
	}

	/**
	 * 佣金比率
	 * @param brokerage 佣金比率
	 */
	public void setBrokerage(BigDecimal brokerage) {
		this.brokerage = brokerage;
	}

	/**
	 * 商家模版
	 * @return 商家模版
	 */
	@Column(nullable = true, length = 200)
	public String getTemplate() {
		return template;
	}

	/**
	 * 商家模版
	 * @param template 商家模版
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * 获取评分
	 * @return 评分
	 */
	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@NumericField
	@JsonProperty
	@Column(nullable = false, precision = 12, scale = 6)
	public Float getScore() {
		return score;
	}

	/**
	 * 设置评分
	 * @param score 评分
	 */
	public void setScore(Float score) {
		this.score = score;
	}

	/**
	 * 获取总评分
	 * @return 总评分
	 */
	@Column(nullable = false)
	public Long getTotalScore() {
		return totalScore;
	}

	/**
	 * 设置总评分
	 * @param totalScore 总评分
	 */
	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * 获取总配送评分
	 * @return 总配送评分
	 */
	@JsonProperty
	@Column(nullable = false, columnDefinition = "integer default 0")
	public Long getTotalAssistant() {
		return totalAssistant;
	}

	/**
	 * 设置总配送评分
	 * @param totalScore 总配送评分
	 */
	public void setTotalAssistant(Long totalAssistant) {
		this.totalAssistant = totalAssistant;
	}

	/**
	 * 获取评分数
	 * @return 评分数
	 */
	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@Column(nullable = false)
	public Long getScoreCount() {
		return scoreCount;
	}

	/**
	 * 设置评分数
	 * @param scoreCount 评分数
	 */
	public void setScoreCount(Long scoreCount) {
		this.scoreCount = scoreCount;
	}

	/**
	 * 获取点击数
	 * @return 点击数
	 */
	@Field(store = Store.YES, index = Index.UN_TOKENIZED)
	@Column(nullable = false)
	public Long getHits() {
		return hits;
	}

	/**
	 * 设置点击数
	 * @param hits 点击数
	 */
	public void setHits(Long hits) {
		this.hits = hits;
	}

	/** 商家摄像头标识列表 */
	@ElementCollection
	@CollectionTable(name = "xx_tenant_video")
	public List<String> getVideos() {
		return videos;
	}

	/** 商家摄像头标识列表 */
	public void setVideos(List<String> videos) {
		this.videos = videos;
	}

	/**
	 * 获取周点击数
	 * @return 周点击数
	 */
	@Field(store = Store.YES, index = Index.NO)
	@Column(nullable = false)
	public Long getWeekHits() {
		return weekHits;
	}

	/**
	 * 设置周点击数
	 * @param weekHits 周点击数
	 */
	public void setWeekHits(Long weekHits) {
		this.weekHits = weekHits;
	}

	/**
	 * 获取月点击数
	 * @return 月点击数
	 */
	@Field(store = Store.YES, index = Index.NO)
	@Column(nullable = false)
	public Long getMonthHits() {
		return monthHits;
	}

	/**
	 * 设置月点击数
	 * @param monthHits 月点击数
	 */
	public void setMonthHits(Long monthHits) {
		this.monthHits = monthHits;
	}

	@JsonProperty
	@Transient
	public BigDecimal getDistatce() {
		return distatce;
	}

	public void setDistatce(BigDecimal distatce) {
		this.distatce = distatce;
	}

	/**
	 * 获取发货地址
	 * @return 发货地址
	 */
	@JsonProperty
	@OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<DeliveryCenter> getDeliveryCenters() {
		return deliveryCenters;
	}

	/**
	 * 获取默认发货地址
	 * @return 发货地址
	 */
	@Transient
	public DeliveryCenter getDefaultDeliveryCenter() {
		for (DeliveryCenter deliveryCenter : getDeliveryCenters()) {
			if (deliveryCenter.getIsDefault()) {
				return deliveryCenter;
			}
		}
		return null;
	}

	/**
	 * 设置发货地址
	 * @param deliveryCenters 发货地址
	 */
	public void setDeliveryCenters(Set<DeliveryCenter> deliveryCenters) {
		this.deliveryCenters = deliveryCenters;
	}

	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}

	/** 获取商家公告 */
	@OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<Article> getArticles() {
		return articles;
	}

	/** 诚信积分等级换算 */
	@Transient
	public int[] getCreditLevel() {
		int[] group = new int[] { 0, 0, 0 };
		Long score = getTotalScore();
		if (score == null || score <= 0) {
			return group;
		}
		BigDecimal big = new BigDecimal(score).subtract(new BigDecimal(Math.max(0, -score + 2)));
		int level = new BigDecimal(Math.sqrt(big.add(SettingConstant.scoreParams20).divide(SettingConstant.scoreParams5).doubleValue())).subtract(SettingConstant.scoreParams1).intValue();
		if (level >= 125) {
			group[0] = 5;
			return group;
		}
		group[0] = level / 25;
		group[1] = level % 25;
		if (group[1] / 5 >= 1) {
			group[2] = group[1] % 5;
			group[1] = group[1] / 5;
		} else {
			group[2] = group[1];
			group[1] = 0;
		}
		return group;
	}

	public Integer getFirstRentFreePeriod() {
		return firstRentFreePeriod;
	}

	public void setFirstRentFreePeriod(Integer firstRentFreePeriod) {
		this.firstRentFreePeriod = firstRentFreePeriod;
	}

	public Long getLogoId() {
		return logoId;
	}

	public void setLogoId(Long logoId) {
		this.logoId = logoId;
	}

	public Long getInvationImageId() {
		return invationImageId;
	}

	public void setInvationImageId(Long invationImageId) {
		this.invationImageId = invationImageId;
	}

	public String getInvationImage() {
		return invationImage;
	}

	public void setInvationImage(String invationImage) {
		this.invationImage = invationImage;
	}

	public Long getOpenShopImageId() {
		return openShopImageId;
	}

	public void setOpenShopImageId(Long openShopImageId) {
		this.openShopImageId = openShopImageId;
	}

	public String getOpenShopImage() {
		return openShopImage;
	}

	public void setOpenShopImage(String openShopImage) {
		this.openShopImage = openShopImage;
	}

	@OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Set<MemberBank> getMemberBanks() {
		return memberBanks;
	}

	public void setMemberBanks(Set<MemberBank> memberBanks) {
		this.memberBanks = memberBanks;
	}

	public String getLedgerno() {
		return ledgerno;
	}

	public void setLedgerno(String ledgerno) {
		this.ledgerno = ledgerno;
	}

	@Transient
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Transient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getShareImage() {
		return shareImage;
	}

	public void setShareImage(String shareImage) {
		this.shareImage = shareImage;
	}

	/**
	 * 获取 productCategories
	 * @return productCategories
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "xx_tenant_product_category")
	public List<ProductCategory> getProductCategories() {
		return productCategories;
	}

	/**
	 * 设置 productCategories
	 * @param productCategories productCategories
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

}

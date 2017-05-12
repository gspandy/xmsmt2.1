//package net.wit.entity;
//
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.OneToOne;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "t_service")
//@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_service_sequence")
//public class Service extends BaseEntity{
//	private static final long serialVersionUID = 1L;
//	private String serviceName;//客户名称
//	private String realName;//真实姓名
//	private String serviceImg;//头像
//	private String serviceTel;//电话
//	private Long tenantId;//企业ID
//	private Admin admin;
//	public String getServiceName() {
//		return serviceName;
//	}
//	public void setServiceName(String serviceName) {
//		this.serviceName = serviceName;
//	}
//	public String getRealName() {
//		return realName;
//	}
//	public void setRealName(String realName) {
//		this.realName = realName;
//	}
//	public String getServiceImg() {
//		return serviceImg;
//	}
//	public void setServiceImg(String serviceImg) {
//		this.serviceImg = serviceImg;
//	}
//	public String getServiceTel() {
//		return serviceTel;
//	}
//	public void setServiceTel(String serviceTel) {
//		this.serviceTel = serviceTel;
//	}
//	public Long getTenantId() {
//		return tenantId;
//	}
//	public void setTenantId(Long tenantId) {
//		this.tenantId = tenantId;
//	}
//	@OneToOne(fetch = FetchType.LAZY)
//	public Admin getAdmin() {
//		return admin;
//	}
//	public void setAdmin(Admin admin) {
//		this.admin = admin;
//	}
//	
//	
//  无效实体
//}

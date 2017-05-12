package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "t_vip_level")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_vip_level_sequence")
public class VipLevel  extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**所属企业*/
	private Tenant tenant;

	/**等级名称*/
	private String levelName; 
	/**奖金比例*/
	private Integer bonusLevel;
	/**发券数量*/
	private Integer ticketNum;
	/**邀请人数的条件*/
	private Integer inviteCondition;
	/**备注*/
	private String remark;
	/**是否 默认等级*/
	private Boolean isDefault;
	
	private Integer level;
	
	
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public Integer getTicketNum() {
		return ticketNum;
	}
	public void setTicketNum(Integer ticketNum) {
		this.ticketNum = ticketNum;
	}
	public Integer getInviteCondition() {
		return inviteCondition;
	}
	public void setInviteCondition(Integer inviteCondition) {
		this.inviteCondition = inviteCondition;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getBonusLevel() {
		return bonusLevel;
	}
	public void setBonusLevel(Integer bonusLevel) {
		this.bonusLevel = bonusLevel;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}

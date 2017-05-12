package net.wit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "t_company_notice")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "t_company_notice_sequence")
public class CompanyNotice extends BaseEntity{

	private static final long serialVersionUID = 1L;

	private Tenant tenant;
	 
	private String title;
	 
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(nullable = false, length = 500)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	 
	 
}

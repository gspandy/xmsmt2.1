package net.wit.vo;

import net.wit.entity.Member;
import net.wit.entity.Tenant;
import net.wit.mobile.util.rong.models.SystemMessage;

public class SystemMessageVO {

	private Tenant tenant;
	private Member member;
	private SystemMessage message;
	
	
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public SystemMessage getMessage() {
		return message;
	}
	public void setMessage(SystemMessage message) {
		this.message = message;
	}
}

package net.wit.mobile.util.rong.models;

import net.wit.mobile.util.rong.util.GsonUtil;

//企业系统公告消息
public class NoticeMessage extends Message {

	private String content;
	private String extra;

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	


	public NoticeMessage(String content) {
		this.content = content;
		this.type= "RC:TxtMsg";
		this.extra = "notice";
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return GsonUtil.toJson(this, NoticeMessage.class);
	}
}

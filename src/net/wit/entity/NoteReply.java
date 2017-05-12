package net.wit.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity - 随手记回复
 * @author rsico Team
 * @version 3.0
 */
@Entity
@Table(name = "xx_note_reply")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_note_reply_sequence")
public class NoteReply extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 157134797412828343L;

	/**
	 * 类型
	 */
	public enum Type {

		/** 文本 */
		text,

		/** 图片 */
		picture
	}
	
	/**
	 * 状态
	 */
	public enum Status {

		/** 已发送 */
		send,

		/** 已接收 */
		receive,

		/** 已回复 */
		reply
	}
	
	/** 随手记 */
	private Note note;

	/** 类型 */
	private Type type;
	
	/** 状态 */
	private Status status;

	/** 内容 */
	private String content;

	/**
	 * 获取随手记
	 * @return 随手记
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}
	
	/**
	 * 获取类型
	 * @return 类型
	 */
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * 获取状态
	 * @return 状态
	 */
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * 获取内容
	 * @return 内容
	 */
	@JsonProperty
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}

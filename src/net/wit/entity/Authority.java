package net.wit.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.wit.enums.AuthorityGroup;

/**
 * @ClassName：Authority @Description： 权限
 * @author：Chenlf
 * @date：2015年9月8日 下午11:56:33
 */
@Entity
@Table(name = "xx_authority")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "xx_authority_sequence")
public class Authority extends OrderEntity {

	/**
	 * @Fields serialVersionUID：
	 */
	private static final long serialVersionUID = -3094624713790599989L;

	/** 树路径分隔符 */
	public static final String TREE_PATH_SEPARATOR = ",";

	private AuthorityGroup authorityGroup;

	/** 上级权限 */
	private Authority parent;

	/** 名称 */
	private String name;

	/** 权限码 */
	private String authority;

	/** URL */
	private String url;

	/** 描述 */
	private String description;

	/** 层级 */
	private Integer grade;

	/** 树路径 */
	private String treePath;

	/** 下级权限 */
	private List<Authority> children = new ArrayList<Authority>();

	private List<Role> roles = new ArrayList<Role>();

	/**
	 * 获取 parent
	 * @return parent
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	public Authority getParent() {
		return parent;
	}

	/**
	 * 设置 parent
	 * @param parent parent
	 */
	public void setParent(Authority parent) {
		this.parent = parent;
	}

	/**
	 * 获取 name
	 * @return name
	 */
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * 设置 name
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取 authority
	 * @return authority
	 */
	@Column(nullable = false)
	public String getAuthority() {
		return authority;
	}

	/**
	 * 设置 authority
	 * @param authority authority
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	/**
	 * 获取 children
	 * @return children
	 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
	@OrderBy("order asc")
	public List<Authority> getChildren() {
		return children;
	}

	/**
	 * 设置 children
	 * @param children children
	 */
	public void setChildren(List<Authority> children) {
		this.children = children;
	}

	/**
	 * 获取 treePath
	 * @return treePath
	 */
	public String getTreePath() {
		return treePath;
	}

	/**
	 * 设置 treePath
	 * @param treePath treePath
	 */
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		if (getParent() != null) {
			setTreePath(getParent().getTreePath() + getParent().getId() + TREE_PATH_SEPARATOR);
			String[] split = getTreePath().split(TREE_PATH_SEPARATOR);
			setGrade(split.length - 1);
		} else {
			setTreePath(TREE_PATH_SEPARATOR);
			setGrade(0);
		}
	}

	/**
	 * 更新前处理
	 */
	@PreUpdate
	public void preUpdate() {
		if (getParent() != null) {
			setTreePath(getParent().getTreePath() + getParent().getId() + TREE_PATH_SEPARATOR);
			String[] split = getTreePath().split(TREE_PATH_SEPARATOR);
			setGrade(split.length - 1);
		} else {
			setTreePath(TREE_PATH_SEPARATOR);
			setGrade(0);
		}

	}

	/**
	 * 获取 grade
	 * @return grade
	 */
	public Integer getGrade() {
		return grade;
	}

	/**
	 * 设置 grade
	 * @param grade grade
	 */
	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	/**
	 * 获取 description
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置 description
	 * @param description description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取 authorityGroup
	 * @return authorityGroup
	 */
	public AuthorityGroup getAuthorityGroup() {
		return authorityGroup;
	}

	/**
	 * 设置 authorityGroup
	 * @param authorityGroup authorityGroup
	 */
	public void setAuthorityGroup(AuthorityGroup authorityGroup) {
		this.authorityGroup = authorityGroup;
	}

	/**
	 * 获取 roles
	 * @return roles
	 */
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "authorities", cascade = CascadeType.ALL)
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * 设置 roles
	 * @param roles roles
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * 获取 url
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置 url
	 * @param url url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}

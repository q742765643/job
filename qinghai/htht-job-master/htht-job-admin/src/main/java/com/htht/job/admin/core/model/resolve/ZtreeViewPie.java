package com.htht.job.admin.core.model.resolve;

import java.io.Serializable;

/**
 * ztree树
 */
public class ZtreeViewPie implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6237809780035784312L;

	private String id;

	private String pId;

	private String name;

	private boolean open;

	private boolean checked = false;

	public ZtreeViewPie() {
	}

	public ZtreeViewPie(String id, String pId, String name, boolean open) {
		super();
		this.id = id;
		this.pId = pId;
		this.name = name;
		this.open = open;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}

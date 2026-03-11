package com.persivia.bean;

public class RxCUIInfoBean {
	private String classId;

	private String className;

	private String rxcui;
	
	//Name of Drug that we get from its API
	private String name; 

	public RxCUIInfoBean() {
	}

	public RxCUIInfoBean(String classId, String className, String rxcui, String name) {
		this.classId = classId;
		this.className = className;
		this.rxcui = rxcui;
		this.setName(name);
	}

	
	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getRxcui() {
		return rxcui;
	}

	public void setRxcui(String rxcui) {
		this.rxcui = rxcui;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

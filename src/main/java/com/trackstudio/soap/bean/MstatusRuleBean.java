package com.trackstudio.soap.bean;


/**
 * TODO: comment
 * @author parsentev
 * @since 03.06.2015
 */
public class MstatusRuleBean {
	private String prstatusId;
	private String[] rules;

	public MstatusRuleBean() {
	}

	public MstatusRuleBean(String prstatusId, String[] rules) {
		this.prstatusId = prstatusId;
		this.rules = rules;
	}

	public String getPrstatusId() {
		return prstatusId;
	}

	public void setPrstatusId(String prstatusId) {
		this.prstatusId = prstatusId;
	}

	public String[] getRules() {
		return rules;
	}

	public void setRules(String[] rules) {
		this.rules = rules;
	}
}

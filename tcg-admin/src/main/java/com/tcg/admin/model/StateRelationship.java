package com.tcg.admin.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "WF_STATE_REL")
public class StateRelationship implements Serializable {
	
	private static final long serialVersionUID = -3637051537477454997L;
	
	@Id
	@Column(name = "SYSID")
	private Integer id;
	
	@Column(name = "FROM_STATE_ID")
	private Integer fromState;
	
	@Column(name = "TO_STATE_ID")
	private Integer toState;
	
	@ManyToOne
    @JoinColumn(name = "TO_STATE_ID", referencedColumnName="STATE_ID", insertable = false, updatable = false)
	private State state;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFromState() {
		return fromState;
	}
	public void setFromState(Integer fromState) {
		this.fromState = fromState;
	}
	public Integer getToState() {
		return toState;
	}
	public void setToState(Integer toState) {
		this.toState = toState;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	
	
	
	

}

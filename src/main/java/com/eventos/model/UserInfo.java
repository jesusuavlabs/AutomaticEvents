package com.eventos.model;


import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * Entidad UserInfo para almacenar la información del usuario
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public class UserInfo {

	@Id
	private String id;

	private String name;
	
	private String location;
	
	private Date lastUpdatedDate;

	public UserInfo(){
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String relationshipStatus;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRelationshipStatus() {
		return relationshipStatus;
	}

	public void setRelationshipStatus(String relationshipStatus) {
		this.relationshipStatus = relationshipStatus;
	}

	
	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", name=" + name + ", location=" + location + ", relationshipStatus="
				+ relationshipStatus + ", lastUpdatedDate=" + lastUpdatedDate + "]";
	}

}

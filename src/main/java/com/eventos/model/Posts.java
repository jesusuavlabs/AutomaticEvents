package com.eventos.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * Entidad Posts para almacenar los textos del usuario
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public class Posts {

	@Id
	private String id;
	
	private String post;
	
	private Date createdDate;
	
	private String userId;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UserPost [id=" + id + ", post=" + post + ", createdDate=" + createdDate +", userId=" + userId + "]";
	}
	
}

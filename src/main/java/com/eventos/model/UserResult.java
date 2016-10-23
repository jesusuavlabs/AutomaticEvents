package com.eventos.model;

import java.util.Date;

/**
 * Clase utilizada para transmitir los resultados a la vista
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public class UserResult {

	private String userId;
	
	private String name;
	
	private String location;
	
	private String relationshipStatus;
	
	private Date lastUpdatedDate;
	
	private String KeywordsCultura;

	private String KeywordsDeporte;
	
	private String KeywordsComida;
	
	private String KeywordsCaridad;
	
	private String KeywordsViajar;
	
	private String KeywordsSocial;
	
	private String KeywordsSalud;

	public UserResult(){
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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

	public String getKeywordsCultura() {
		return KeywordsCultura;
	}

	public void setKeywordsCultura(String keywordsCultura) {
		KeywordsCultura = keywordsCultura;
	}

	public String getKeywordsDeporte() {
		return KeywordsDeporte;
	}

	public void setKeywordsDeporte(String keywordsDeporte) {
		KeywordsDeporte = keywordsDeporte;
	}

	public String getKeywordsComida() {
		return KeywordsComida;
	}

	public void setKeywordsComida(String keywordsComida) {
		KeywordsComida = keywordsComida;
	}

	

	public String getKeywordsCaridad() {
		return KeywordsCaridad;
	}

	public void setKeywordsCaridad(String keywordsCaridad) {
		KeywordsCaridad = keywordsCaridad;
	}

	public String getKeywordsViajar() {
		return KeywordsViajar;
	}

	public void setKeywordsViajar(String keywordsViajar) {
		KeywordsViajar = keywordsViajar;
	}

	public String getKeywordsSocial() {
		return KeywordsSocial;
	}

	public void setKeywordsSocial(String keywordsSocial) {
		KeywordsSocial = keywordsSocial;
	}

	public String getKeywordsSalud() {
		return KeywordsSalud;
	}

	public void setKeywordsSalud(String keywordsSalud) {
		KeywordsSalud = keywordsSalud;
	}

	@Override
	public String toString() {
		return "UserResult [userId=" + userId + ", name=" + name + ", location=" + location + ", relationshipStatus="
				+ relationshipStatus + ", lastUpdatedDate=" + lastUpdatedDate + ", KeywordsCultura=" + KeywordsCultura
				+ ", KeywordsDeporte=" + KeywordsDeporte + ", KeywordsComida=" + KeywordsComida + ", KeywordsCaridad="
				+ KeywordsCaridad + ", KeywordsViajar=" + KeywordsViajar + ", KeywordsSocial=" + KeywordsSocial
				+ ", KeywordsSalud=" + KeywordsSalud + "]";
	}


}

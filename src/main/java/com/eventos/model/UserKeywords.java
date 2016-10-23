package com.eventos.model;


import org.springframework.data.annotation.Id;

/**
 * Entidad UserKeywords para almacenar los gustos del usuario
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public class UserKeywords {

	@Id
	private String id;

	private String userId;
	
	private String KeywordsCultura;

	private String KeywordsDeporte;
	
	private String KeywordsComida;
	
	private String KeywordsCaridad;
	
	private String KeywordsViajar;
	
	private String KeywordsSocial;
	
	private String KeywordsSalud;

	public UserKeywords(){
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
		return "UserKeywords [id=" + id + ", userId=" + userId + ", KeywordsCultura=" + KeywordsCultura
				+ ", KeywordsDeporte=" + KeywordsDeporte + ", KeywordsComida=" + KeywordsComida + ", KeywordsCaridad="
				+ KeywordsCaridad + ", KeywordsViajar=" + KeywordsViajar + ", KeywordsSocial=" + KeywordsSocial
				+ ", KeywordsSalud=" + KeywordsSalud + "]";
	}


}

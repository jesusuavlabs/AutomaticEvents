package com.eventos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eventos.model.UserKeywords;

/**
 * Repositorio para los UserKeywords del usuario
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public interface UserKeywordsRepository extends MongoRepository<UserKeywords, String>{
	
	public UserKeywords findByuserId(String userId);
	
	public Long removeByuserId(String userId);

}

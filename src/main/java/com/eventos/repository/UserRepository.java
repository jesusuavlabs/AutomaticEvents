package com.eventos.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.eventos.model.UserInfo;

/**
 * Repositorio para la información del usuario
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public interface UserRepository extends MongoRepository<UserInfo, String>{
	
	public UserInfo findByName(String name);

}

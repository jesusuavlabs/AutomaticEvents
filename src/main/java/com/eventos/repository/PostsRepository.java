package com.eventos.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.eventos.model.Posts;

/**
 * Repositorio para los Posts del usuario
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public interface PostsRepository extends MongoRepository<Posts, String>{
	
	public List<Posts> findByuserId(String userId);
	
	public List<Posts> removeByuserId(String userId);

}

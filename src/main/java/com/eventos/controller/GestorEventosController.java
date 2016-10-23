package com.eventos.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Invitation;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eventos.model.Posts;
import com.eventos.model.UserInfo;
import com.eventos.model.UserKeywords;
import com.eventos.model.UserResult;
import com.eventos.repository.PostsRepository;
import com.eventos.repository.UserKeywordsRepository;
import com.eventos.repository.UserRepository;
import com.eventos.utils.Utilidades;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la gestión de los eventos
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
@Controller
public class GestorEventosController {

    private Facebook facebook;
   
	private ConnectionRepository connectionRepository;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PostsRepository postRepository;
	
	@Autowired
	private UserKeywordsRepository userKeywordsRepository;
	
	@Value("${text.processing.script.location}")
	private String scriptLocation;
	
	
	// Define the logger object for this class
	private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    public GestorEventosController(Facebook facebook, ConnectionRepository connectionRepository) {
        this.facebook = facebook;
		this.connectionRepository = connectionRepository;
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String connectToFacebook(Model model) {
    	        
        //Comprueba si ya esta conectado a facebook
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
        	//No lo esta, pedimos que se conecte a facebook
            return "redirect:/connect/facebook";
        }
		
        return "result";
    }

    @RequestMapping(value="/landingPage", method=RequestMethod.GET)
    public String resetFacebookInfo() {
        return "result";
    }

    @RequestMapping(value="/getInfo", method=RequestMethod.GET)
    @ResponseBody
    public UserResult gettingFacebookInfo() {
    	
    	UserKeywords keywords;
    	UserResult resultado = new UserResult();
    	
        //Comprueba si ya esta conectado a facebook
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            //No lo esta, pedimos que se conecte a facebook
        	resultado.setName("-3");
            return resultado;
        }

        //Obtenemos la información del usuario
        UserInfo user = gettingPersonalInfo();
        //Comprobamos si ya existia el usuario
        UserInfo previousUser = repository.findByName(user.getName());
        if (previousUser != null){
        	log.info("Habia ya informacion almacenada para el nombre especificado");
        	//Obtengo los gustos
        	keywords = userKeywordsRepository.findByuserId(previousUser.getId());
        	if (keywords != null){
	        	resultado.setUserId(previousUser.getId());
	        	resultado.setLocation(user.getLocation());
	        	resultado.setName(user.getName());
	        	resultado.setLastUpdatedDate(previousUser.getLastUpdatedDate());     	
	        	resultado.setKeywordsCaridad(keywords.getKeywordsCaridad());
	        	resultado.setKeywordsComida(keywords.getKeywordsComida());
	        	resultado.setKeywordsCultura(keywords.getKeywordsCultura());
	        	resultado.setKeywordsDeporte(keywords.getKeywordsDeporte());
	        	resultado.setKeywordsSalud(keywords.getKeywordsSalud());
	        	resultado.setKeywordsSocial(keywords.getKeywordsSocial());
	        	resultado.setKeywordsViajar(keywords.getKeywordsViajar());
	        	log.info(resultado.toString());
        	}else{
        		log.info("No hay gustos almacenados");
            	resultado.setName("-1");
        	}
        }else {
        	log.info("No habia informacion previa almacenada");
        	resultado.setName("-1");
        }
            
        return resultado;
    }
    
    
    @RequestMapping(value="/storeInfo", method=RequestMethod.GET)
    @ResponseBody
    public UserResult storeFacebookInfo() {

    	Date lastUpdatedDate = null;
    	UserResult resultado = new UserResult();
    	
        //Comprueba si ya esta conectado a facebook
        if (connectionRepository.findPrimaryConnection(Facebook.class) == null) {
            //No lo esta, pedimos que se conecte a facebook
        	resultado.setName("-3");
            return resultado;
        }

        //Obtenemos la información del usuario
        UserInfo user = gettingPersonalInfo();
        resultado.setName(user.getName());
        resultado.setLocation(user.getLocation());
        resultado.setRelationshipStatus(user.getRelationshipStatus());
        
        //Comprobamos si ya existia el usuario
        UserInfo previousUser = repository.findByName(user.getName());
        if (previousUser != null){
            log.info("Hay informacion previa del usuario: "+ previousUser.getId());
          /*  repository.delete(previousUser);
            List<Posts> posts = postRepository.removeByuserId(previousUser.getId());
            if (posts != null)
                log.info("Se han borrado "+posts.size()+" posts");
            long keywords = ((Long) userKeywordsRepository.removeByuserId(previousUser.getId())).longValue();
            if (keywords > 0)
                log.info("Se han borrado los gustos del usuario");*/
            user.setId(previousUser.getId());
            lastUpdatedDate = previousUser.getLastUpdatedDate();
            log.info("Fecha ultima actualizacion: "+lastUpdatedDate.toString());
        }
        
        repository.save(user);
        //Volvemos a consultar para obtener el nuevo id en BBDD
        user = repository.findByName(user.getName());
        String idUsuario = user.getId();
        log.info("Id de usuario creado o actualizado: "+idUsuario);  
        //Rellenamos el objeto de respuesta con la info que faltaba de BBDD
        resultado.setUserId(idUsuario);
              
        //Obtenemos los posts recuperados de facebook
        gettingPosts(lastUpdatedDate, idUsuario);               
        //Obtenemos los eventos a los que ha asistido
        gettingAttendingEvents(lastUpdatedDate, idUsuario);               
        //Obtenemos la informacion de los albums
        gettingAlbumsInfo(lastUpdatedDate, idUsuario);       
      
        //Comprobamos que se han insertado correctamente
        List<Posts> textos = postRepository.findByuserId(user.getId());
        if (textos != null)
            log.info("Hay "+textos.size()+" posts en BBDD");
        
        //Ponemos la ultima fecha de actualizacion a dia de hoy
        resultado.setLastUpdatedDate(user.getLastUpdatedDate());
        return resultado;
    }
    
    @RequestMapping(value="/dataMining", method=RequestMethod.GET)
    @ResponseBody
    public UserResult getDataMining(@RequestParam("name") String nombre) throws RserveException {
    	
    	RConnection connectionRserve = null;
        UserKeywords keywords = null;  
        UserResult infoFinal = new UserResult();
        boolean resultado = false;
    	
        if (nombre == null || nombre.equals("")){
        	log.error("Error: No se ha recuperado correctamente el nombre del usuario");
        	infoFinal.setName("-3");
            return infoFinal;
        }

        try {
        	if (connectionRserve == null){
        		connectionRserve = new RConnection();
        		log.info("Conexion establecida");
        	}       
        	UserInfo userInfo = repository.findByName(nombre);
        	
        	log.info("Obtenemos el script de R: "+scriptLocation);
        	//connectionRserve.eval("source('D:/TFM/User_info_processing.R')");
        	connectionRserve.eval(scriptLocation);
        	
        	log.info("categorias.personalizadas(\""+userInfo.getId()+"\",\""+nombre+"\")");

        	int isOk = connectionRserve.eval("categorias.personalizadas(\""+userInfo.getId()+"\",\""+nombre+"\")").asInteger();
        	
        	resultado = isOk>0;
	        log.info("Ha ido bien? " + (resultado? "SI":"NO"));   
	        
	        if (resultado){
	        	keywords = userKeywordsRepository.findByuserId(userInfo.getId());   
	        	log.info("keywords obtenido: "+keywords.toString());
	        	infoFinal.setUserId(userInfo.getId());
	        	infoFinal.setName(nombre);
	        	infoFinal.setLocation(userInfo.getLocation());
	        	infoFinal.setLastUpdatedDate(userInfo.getLastUpdatedDate());
	        	
	        	infoFinal.setKeywordsCaridad(keywords.getKeywordsCaridad());
	        	infoFinal.setKeywordsComida(keywords.getKeywordsComida());
	        	infoFinal.setKeywordsCultura(keywords.getKeywordsCultura());
	        	infoFinal.setKeywordsDeporte(keywords.getKeywordsDeporte());
	        	infoFinal.setKeywordsSalud(keywords.getKeywordsSalud());
	        	infoFinal.setKeywordsSocial(keywords.getKeywordsSocial());
	        	infoFinal.setKeywordsViajar(keywords.getKeywordsViajar());
	        }
        	
           
        } catch (RserveException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw e;
        } catch (REXPMismatchException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new RserveException(connectionRserve, e.getMessage());
        } catch (REngineException e) {
        	log.error(e.getMessage());
			e.printStackTrace();
		}
        
        return infoFinal;
    }

    
    private UserInfo gettingPersonalInfo() {
    	UserInfo user =  new UserInfo();       
        
    	//Con la versión 2.8 del API de Facebook da error esta operación
    	//Hay que esperar una nueva versión de Spring Social de Facebook que lo resuelva
        //User usuario = facebook.userOperations().getUserProfile();
        
    	//Establecemos un workaround temporal hasta que se resuelva el bug
        String [] fields = { "id", "first_name", "last_name", "location" };
        User usuario = facebook.fetchObject("me", User.class, fields);
        
        String name = usuario.getFirstName()+ " "+usuario.getLastName();
        name = Utilidades.clean(name);
        log.info("name: "+ name);
        Reference reference = usuario.getLocation();
        String location = reference.getName();
        log.info("location: "+ location);
        String relationshipStatus = usuario.getRelationshipStatus();
        log.info("relationship status: "+ relationshipStatus);

        log.info("personalInfo generado");	
					
		user.setName(name);
		user.setLocation(location);
		user.setLastUpdatedDate(new Date());

    	return user;
    }
    
    private long gettingPosts(Date lastUpdatedDate, String userId) {
    	boolean morePosts = true;
    	PagedList<Post> posts;
    	
    	if (lastUpdatedDate != null){
    		//Obtengo solo los posts nuevos
    		//PagingParameters(Integer limit, Integer offset, Long since, Long until)
            posts = facebook.feedOperations().getPosts(new PagingParameters(10000,0,(lastUpdatedDate.getTime()/1000), null));
    	}else{
    		//Tengo que obtener todos los posts
    		//Obtengo los posts del usuario de manera paginada
            posts = facebook.feedOperations().getPosts();
    	}
    	//log.info("post: "+posts.size());
        
        PagedList<Post> nextPosts = null;
        int page = 1;        
        PagingParameters nextPage = posts.getNextPage();
        
        do{       
	        if (nextPage != null){
	        	nextPosts = facebook.feedOperations().getPosts(nextPage);
	        	if (nextPosts != null && nextPosts.size() > 0){
	        		posts.addAll(nextPosts);
	        	}
	        	nextPage = nextPosts.getNextPage();
	        }else{
	        	morePosts = false;
	        }
	        page++;		        
        }while(morePosts);
        
        if (posts != null && posts.size() > 0){
	        log.info("postTotal: "+posts.size());	        
	        
	        //Recorremos los posts para ir almacenandolos en la BBDD
	        for(Iterator<Post> iter = posts.iterator(); iter.hasNext() ; ){
	        	Post post = (Post) iter.next();
	        	Date fecha = post.getCreatedTime();
	        	String mensaje = post.getMessage();
	        	if (mensaje == null || mensaje.equalsIgnoreCase("")){
	        		mensaje = post.getStory();
	        	}
	        	
	        	mensaje =  Utilidades.clean(mensaje);
	        	//log.info("post limpio: "+mensaje);
	        	
	        	Posts postEntity = new Posts();
	        	postEntity.setPost(mensaje);
	        	postEntity.setUserId(userId);
	        	postEntity.setCreatedDate(fecha);
	        	postRepository.save(postEntity);    	
	        }
	        
	        log.info("posts generados");	
        }else{
        	log.info("No hay posts nuevos o no ha escrito nada");	
        }
	    
        return posts.size();
    }
    
    
    private long gettingAttendingEvents(Date lastUpdatedDate, String userId) {
    	boolean moreEvents = true;
    	PagedList<Invitation> attendingEvents = null;
    	
    	if (lastUpdatedDate != null){
    		//Obtengo solo los eventos nuevos
    		//PagingParameters(Integer limit, Integer offset, Long since, Long until)
    		attendingEvents = facebook.eventOperations().getAttending(new PagingParameters(1000,0,(lastUpdatedDate.getTime()/1000), null));
    	}else{
    		//Tengo que obtener todos los eventos
    		//Obtengo los eventos del usuario de manera paginada
    		attendingEvents = facebook.eventOperations().getAttending();
    	}

        PagedList<Invitation> nextEvents = null;
        int page = 1;  
        //Obtenemos los eventos paginados
        PagingParameters nextPage = attendingEvents.getNextPage();
        //Recorremos todas las paginas para obtener todos los eventos
        do{       
	        if (nextPage != null){
	        	nextEvents = facebook.eventOperations().getAttending(nextPage);
	        	if (nextEvents != null && nextEvents.size() > 0){
	        		attendingEvents.addAll(nextEvents);
	        	}
	        	nextPage = nextEvents.getNextPage();
	        }else{
	        	moreEvents = false;
	        }
	        page++;		        
        }while(moreEvents && page < 200);
        
        
        if (attendingEvents != null && attendingEvents.size() > 0){
        	log.info("eventsTotal: "+attendingEvents.size());	
            
            for(Iterator<Invitation> iter = attendingEvents.iterator(); iter.hasNext() ; ){
            	Invitation event = (Invitation) iter.next();
            	//Date fecha = event.getStartTime();
            	String evento = event.getName();
            	Date fecha = event.getStartTime();
            	if (evento != null && !evento.equalsIgnoreCase("")){
            		evento =  Utilidades.clean(evento);
                	Posts eventoEntity = new Posts();
                	eventoEntity.setPost(evento);
                	eventoEntity.setCreatedDate(fecha);
                	eventoEntity.setUserId(userId);
                	postRepository.save(eventoEntity);
            	}	        	
            }	        
            log.info("eventos generados");	
        }else{
        	log.info("No hay eventos nuevos o no ha asistido a ninguno");	
        }

    	return attendingEvents.size();
    }
    
    private long gettingAlbumsInfo(Date lastUpdatedDate, String userId) {
    	boolean moreAlbums = true;
    	PagedList<Album> albums = null;
    	
    	if (lastUpdatedDate != null){
    		//Obtengo solo los albumes nuevos
    		//PagingParameters(Integer limit, Integer offset, Long since, Long until)
    		albums = facebook.mediaOperations().getAlbums(new PagingParameters(1000,0,(lastUpdatedDate.getTime()/1000), null));
    	}else{
    		//Tengo que obtener todos los albumes
    		//Obtengo los albumes del usuario de manera paginada
    		albums = facebook.mediaOperations().getAlbums();
    	}

        
        PagedList<Album> nextAlbums = null;
        int page = 1;        
        PagingParameters nextPage = albums.getNextPage();
        
        //Obtenemos todas las paginas de albumes
        do{       
	        if (nextPage != null){
	        	nextAlbums = facebook.mediaOperations().getAlbums(nextPage);
	        	if (nextAlbums != null && nextAlbums.size() > 0){
	        		albums.addAll(nextAlbums);
	        	}
	        	nextPage = nextAlbums.getNextPage();
	        }else{
	        	moreAlbums = false;
	        }
	        page++;		       
        }while(moreAlbums && page < 200);
        
        
        if (albums != null && albums.size() > 0){
        	log.info("albumsTotal: "+albums.size());	
            
            for(Iterator<Album> iter = albums.iterator(); iter.hasNext() ; ){
            	Album album = (Album) iter.next();
            	String nombreAlbum = album.getName();
            	Date fecha = album.getCreatedTime();
            	
            	nombreAlbum =  Utilidades.clean(nombreAlbum);
            	Posts albumEntity = new Posts();
            	albumEntity.setPost(nombreAlbum);
            	albumEntity.setCreatedDate(fecha);            	
            	albumEntity.setUserId(userId);
            	postRepository.save(albumEntity);
            }
            
            log.info("Albumes generados");	
        }else{
        	log.info("No hay albumes nuevos o no ha publicado ninguno");	
        }        
		
    	return albums.size();
    }
    
  }

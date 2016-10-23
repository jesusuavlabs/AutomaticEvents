#Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
# de la Universidad Internacional de la Rioja
#@author jesus.aviles
limpiar.texto = function(x)
{
  # remove #
  x = gsub("#", "", x)
  # remove at
  x = gsub("@\\w+", "", x)
  # remove punctuation
  x = gsub("[[:punct:]]", "", x)
  # remove numbers
  #x = gsub("[[:digit:]]", "", x)
  # remove links http
  x = gsub("http\\w+", "", x)
  # remove tabs
  x = gsub("[ |\t]{2,}", " ", x)
  x = gsub("[^[:graph:]]", " ", x) 
  # remove blank spaces at the beginning
  x = gsub("^ ", "", x)
  # remove blank spaces at the end
  x = gsub(" $", "", x)
  # remove more than one blank spaces
  x = gsub("  ", " ", x)
  x = gsub("ã¡", "a", x)
  x = gsub("ã©", "e", x)
  x = gsub("ã³", "o", x)
  x = gsub("ãº", "u", x)
  x = gsub("ã±", "ñ", x)
  x = gsub("ã¨", "e", x)
  x = gsub("ã²", "o", x)
  x = gsub("Ã¡", "a", x)
  x = gsub("Ã©", "e", x)
  x = gsub("Ã�", "i", x)
  x = gsub("Ã³", "o", x)
  x = gsub("Ãº", "u", x)
  
  return(x)
}

analisis.sentimiento = function(frases, positivas, negativas, .progress = 'none'){
  
  library(stringr)
  library(plyr)
  
  scores = laply(frases, function(frase, positivas, negativas){
    #Realizamos una pequeña limpieza del texto
    frase = gsub("[[:punct:]]", "", frase)
    frase = gsub("[[:cntrl:]]", "", frase)
    frase = gsub("\\d+", "", frase)
    frase = str_replace_all(frase,"[^[:graph:]]", " ") 
    
    # Convertimos a minusculas:
    frase = tolower(frase)
    # Dividimos en palabras. 
    lista.palabras = str_split(frase, "\\s+")
    palabras = unlist(lista.palabras)
    
    # comparamos las palabras con los terminos positivos y negativos pasados como argumentos
    pos.matches = match(palabras, positivas)
    neg.matches = match(palabras, negativas)
    
    # match() devuelve la posicion del termino encontrado o NA
    pos.matches = !is.na(pos.matches)
    neg.matches = !is.na(neg.matches)
    
    # Restamos al numero de ocurrencias positivas el numero de ocurrencias negativas:
    score = sum(pos.matches) - sum(neg.matches)
    
    return(score)
    
  }, positivas, negativas, .progress = .progress )
  
  scores.df = data.frame(score = scores, post = frases)
  
  return(scores.df)
}

getPositivePosts = function(posts, id.usuario){
  # Obtenemos un lexicon con palabras positivas y negativas
  lexicon <- read.csv(file='Lexicon.txt', sep='\t')
  # Las descomponemos y separamos
  lexiconpos = subset(lexicon, lexicon[,3] == "pos")
  lexiconneg = subset(lexicon, lexicon[,3] == "neg")
  positivas <- lexiconpos[,1]
  negativas <- lexiconneg[,1]
  
  # Realizamos la llamada a la funcion de analisis de sentimiento
  puntuaciones = analisis.sentimiento(posts, positivas, negativas, .progress = 'text')
  #print("Puntuaciones calculadas")
  #dim(puntuaciones)
  
  par(bg = "white", mar=c(5, 4, 4, 2) + 0.1) ## Establezco un fondo por defecto
  titulo <- paste0(c("D:\\eventos\\src\\main\\webapp\\histograma_", id.usuario, ".jpg"), sep = "", collapse = "")
  jpeg(titulo, width = 480, height = 480, units = "px")
  #histograma con el analisis de sentimiento:
  hist(puntuaciones$score, freq=F, xlab = ""
       , main = "Analisis de sentimiento de los textos analizados"
       , border = "black", col = "skyblue")
  dev.off()
  
  #total evaluation: positive / negative / neutral
  stat <- puntuaciones
  stat <- mutate(stat, sentimiento=ifelse(stat$score > 0, 'positive', ifelse(stat$score < 0, 'negative', 'neutral')))
  score <- stat$sentimiento
  #length(score)
  #length(puntuaciones$post)
  puntuaciones.df <- data.frame(textos=puntuaciones$post,score)
  
  positivePosts <- puntuaciones.df[(puntuaciones.df$score=='positive' | puntuaciones.df$score=='neutral'),]
  #print("Obtenidos Posts positivos")
  #dim(positivePosts)
  
  return(positivePosts$textos)

}

getJsonDocument = function(clave, contenido)
{
  s1 <- "{\'userId\': \""
  s2 <- "\","
  regex <- paste0(c(s1, clave,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsCultura\': \""
  s2 <- "\","
  regex2 <- paste0(c(regex, s1, contenido$KeywordsCultura,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsDeporte\': \""
  s2 <- "\","
  regex <- paste0(c(regex2, s1, contenido$KeywordsDeporte,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsComida\': \""
  s2 <- "\","
  regex2 <- paste0(c(regex, s1, contenido$KeywordsComida,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsCaridad\': \""
  s2 <- "\","
  regex <- paste0(c(regex2, s1, contenido$KeywordsCaridad,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsViajar\': \""
  s2 <- "\","
  regex2 <- paste0(c(regex, s1, contenido$KeywordsViajar,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsSocial\': \""
  s2 <- "\","
  regex <- paste0(c(regex2, s1, contenido$KeywordsSocial,s2), sep = "", collapse = "")
  s1 <- "\'KeywordsSalud\': \""
  s2 <- "\"}"
  regex2 <- paste0(c(regex, s1, contenido$KeywordsSalud,s2), sep = "", collapse = "")
  
  return(regex2)
}


categorias.personalizadas = function(id.usuario, nombre){
  setwd("D:\\TFM")

  
  #En windows es necesario este comando para cargar la libreria rJava a partir de windows 7
  Sys.setenv(JAVA_HOME='C:\\Program Files\\Java\\jre7')
  library(rJava)
  library(RMongo)
  
  library(wordcloud) 
  library(stringr)
  library(tm)
  library(RTextTools)
  library(topicmodels)
  
  #Para conectar via replica set
  mongo <- mongoDbReplicaSetConnect("usersInfo", "mongo1.eventos.es:27027,mongo2.eventos.es:27028")
  #Para acceder se requiere logar con un usuario
  username = "gestorUserInfo"
  password = "springBootFacebook8D"
  authenticated <- dbAuthenticate(mongo, username, password)

  #Definimos palabras clave para cada categoria de eventos
  lwordsCultura <- c("clown", "conferencia", "circo", "bufon", "monologuista", "comico", "comedia", "artista", "teatro", "cabaret", "payaso", "impro", "improvisacion", "microteatro", "humor", "monologo", "concurso", "mic", "comedy", "poesia")
  lwordsDeporte <- c("carrera", "basketbal", "club", "jugador", "set", "aficionado", "anotador", "gol", "nba", "deporte", "futbol", "balon", "patada", "caloria", "juega", "juegan", "entrena", "entrenamiento", "ciclismo", "runner", "running", "footing", "partido")
  lwordsComida <- c("restaurante", "bar", "tapas", "tapeo", "tapa", "queso", "cerveza", "caña", "degustacion", "cafe", "plato", "veggie", "vegano", "comida", "cena", "brunch", "desayuno", "postr", "sano")
  lwordsCaridad <- c("clima", "donativo", "cancer", "ELA", "animal", "protectora", "santuario", "gato", "perro", "perra", "gata", "animalista", "adopcion")
  lwordsViajar <- c("pasear", "tour", "viajar", "ruta", "excursion", "julio", "agosto", "pueblo", "lugar", "paseo", "rural", "vacacion", "natur", "naturaleza", "plan", "escapada", "vistas", "bosque", "mar", "rio", "puent")
  lwordsVidaSocial <- c("noch", "fiesta", "fiestaca", "discoteca", "curso", "pub", "feria", "nocturn", "cañas", "tapeo", "baile", "gin", "amigo", "encuentro")
  lwordsSalud <- c("sano", "relax", "entrena", "yoga", "risa", "retiro", "masaje", "estiramiento", "relajacion", "incienso")
  
  
  #Componemos la query para recuperar los posts del usuario proporcionado
  s1 <- "{\'userId\': \""
  s2 <- "\"}"
  regex <- paste0(c(s1, id.usuario,s2), sep = "", collapse = "")
  #print(regex)
  queryPost <- dbGetQuery(mongo, 'posts', regex, skip=0, limit=6000)
  mongo.posts <- queryPost[]
  
  if (is.data.frame(mongo.posts) && nrow(mongo.posts)==0){
    #print("No devuelve posts la consulta")
    dbDisconnect(mongo)
    return(-1)
  }
  
  #Realizamos un analisis de sentimiento para quedarnos únicamente con las frases positivas o neutras
  posts <- getPositivePosts(mongo.posts$post, id.usuario)
  #length(posts)
  #print(posts)
  
  #Limpiamos el texto de caracteres extraños  
  posts <- limpiar.texto(posts)
  #length(posts)
  
  # Comenzamos con la minería de datos para determinar y clasificar las diferentes categorías
  # Construimos para ello el corpus
  mydata.corpus <- Corpus(VectorSource(posts))
  # Pasamos a minusculas
  mydata.corpus <- tm_map(mydata.corpus, content_transformer(tolower)) 
  # Eliminamos los signos de puntuacion
  mydata.corpus <- tm_map(mydata.corpus, removePunctuation)
  # Dividimos el nombre en palabras para añadir a las stopwords. 
  nombre.palabras <- str_split(nombre, "\\s+")
  nombre.palabras <- unlist(nombre.palabras)
  nombre.palabras <- tolower(nombre.palabras)
  # Eliminamos palabras comunes y añadimos alguna más para eliminar ruido en los resultados
  my_stopwords <- c(stopwords('english'), stopwords('spanish'), 'photo', 'photos', 'album', 'added', 'shared', 'mas', 'hoy', 'dia', nombre.palabras)
  mydata.corpus <- tm_map(mydata.corpus, removeWords, my_stopwords)
  # Quitamos espacios
  mydata.corpus <- tm_map(mydata.corpus, stripWhitespace)
  # Eliminamos numeros
  mydata.corpus <- tm_map(mydata.corpus, removeNumbers)
  #Para la nube de palabras
  mydata.corpus.sinStem <- mydata.corpus
  # Realizamos stemming
  mydata.corpus <- tm_map(mydata.corpus, stemDocument)
  
  #Creamos un documentTermMatrix para la nube de palabras
  dtm <- DocumentTermMatrix(mydata.corpus.sinStem)   

  freq <- colSums(as.matrix(dtm)) # Determinamos la frecuencia de cada palabra   
  dark2 <- brewer.pal(6, "Dark2")   
  titulo <- paste0(c("D:\\eventos\\src\\main\\webapp\\wordcloud_", id.usuario, ".jpg"), sep = "", collapse = "")
  jpeg(titulo, width = 480, height = 480, units = "px")
  wordcloud(names(freq), freq, max.words=50, rot.per=0.2, colors=dark2)  
  dev.off()

  #Determinamos la clasificación LDA en 30 temas
  dtm <- DocumentTermMatrix(mydata.corpus, control=list(minDocFreq=2, minWordLength=2))
  rowTotals <- apply(dtm , 1, sum)       #Obtenemos la suma de las palabras en cada documento
  dtm   <- dtm[rowTotals> 0, ]           #Eliminamos los documentos sin palabras

  lda <- LDA(dtm, 30)
  topics <- terms(lda)
  #El siguiente comando te asocia cada documento analizado a uno de los topicos obtenido
  #topics(lda)
  
  #Los cruzamos con los conjuntos de las diferentes palabras clave de cada categoria
  KeywordsCultura <- length(intersect(lwordsCultura, topics))
  KeywordsDeporte <- length(intersect(lwordsDeporte, topics))
  KeywordsComida <- length(intersect(lwordsComida, topics))
  KeywordsCaridad <- length(intersect(lwordsCaridad, topics))
  KeywordsViajar <- length(intersect(lwordsViajar, topics))
  KeywordsSocial <- length(intersect(lwordsVidaSocial, topics))
  KeywordsSalud <- length(intersect(lwordsSalud, topics))
  
  document.df <- data.frame(KeywordsCultura, KeywordsDeporte, KeywordsComida, KeywordsCaridad, KeywordsViajar, KeywordsSocial, KeywordsSalud) 
  document.df
  
  titulo <- paste0(c("D:\\eventos\\src\\main\\webapp\\Gustos_", id.usuario, ".jpg"), sep = "", collapse = "")
  jpeg(titulo, width = 680, height = 400, units = "px")
  #barplot con los gustos del usuario:
  gustos <- c(KeywordsCultura, KeywordsDeporte, KeywordsComida, KeywordsCaridad, KeywordsViajar, KeywordsSocial, KeywordsSalud)
  categorias <- c("Cultura", "Deporte", "Comida", "Caridad", "Viajar", "Social", "Salud")
  df <- data.frame(categorias=categorias, gustos=gustos)
  #Ordeno para que aparezca encima la más intensa
  df2=df[order(df$gustos),]
  cat<-as.factor(df2$categorias)
  
  barplot(df2$gustos, horiz=T, las=1, 
          names.arg=cat,
          xlab = "Intensidad del gusto de la categoria de acuerdo a la actividad en RRSS")
  dev.off()
  
  #Componemos la query para recuperar si ya hay claves porporcionadas
  s1 <- "{\'userId\': \""
  s2 <- "\"}"
  regex <- paste0(c(s1, id.usuario,s2), sep = "", collapse = "")
  query <- dbGetQuery(mongo, 'userKeywords', regex)
  data_keywords <- query[]
  if (is.data.frame(data_keywords) && nrow(data_keywords)>0){
    remove.document <- getJsonDocument(clave=data_keywords$userId, contenido=data_keywords)
    dbRemoveQuery(mongo, 'userKeywords', remove.document)
  }
  
  document <- getJsonDocument(clave=mongo.posts$userId[1], contenido=document.df)
  #Insertamos documento actualizado
  dbInsertDocument(mongo, "userKeywords", document)
    #print("Insertado resultado correctamente")
  
  #Cerramos conexion mongo
  dbDisconnect(mongo)
  
  return(1)
  
}
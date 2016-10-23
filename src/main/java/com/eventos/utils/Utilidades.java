package com.eventos.utils;

/**
 * Clase de utilidades
 * Trabajo Fin de Master para el Master en Visual Analytics y Big Data 
 * de la Universidad Internacional de la Rioja
 * @author jesus.aviles
 *
 */
public class Utilidades {
  
    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String clean(String input) {
    	if (input != null){
    	    // Cadena de caracteres original a sustituir.
    	    String original = "áàäéèëíìïóòöúùuÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ;";
    	    // Cadena de caracteres ASCII que reemplazarán los originales.
    	    String ascii = "aaaeeeiiiooouuuAAAEEEIIIOOOUUUNcC,";
    	    String output = input;
    	    for (int i=0; i<original.length(); i++) {
    	        // Reemplazamos los caracteres especiales.
    	        output = output.replace(original.charAt(i), ascii.charAt(i));
    	    }//for i
    	    
    	    output = output.replace("\n", "").replace("\r", "");
    	    output = output.replaceAll("\"", " ");
    	    output = output.trim();
    	    return output;
    	}
    	return input;
    }
    
}
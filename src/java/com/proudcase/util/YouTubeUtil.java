/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proudcase.util;

/**
 *
 * @author Michel
 */
public class YouTubeUtil {
    
    // after the v= key comes the hashcode for the video. That is what we want
    private static final String YOUTUBE_KEY = "v=";
    
    // default youtube link
    private static final String YOUTUBE_LINK = "http://www.youtube.com/v/";
    private static final String YOUTUBE_VERSION = "?version=3";
    
    // This converts an typed youtube link into the right form
    public static String convertYouTubeLink(String url) {
        int index = url.indexOf(YOUTUBE_KEY);
        String hashCode = "";
        if (index != -1) {
            index += 2;
            Character oneChar;
            for (int i = index; i < url.length(); i++) {
                oneChar = url.charAt(i);
                // The char is an & so we have to finish here!
                if (oneChar.equals('&')) {
                    break;
                }
                
                // add the char
                hashCode += oneChar;
            }
        }
        
        // now add the hashcode to the normal url
        String newUrl = YOUTUBE_LINK + hashCode + YOUTUBE_VERSION;
        
        return newUrl;
    }
    
}

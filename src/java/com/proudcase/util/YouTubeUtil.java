package com.proudcase.util;

/**
  * Copyright © 03.07.2013 Michel Vocks
  * This file is part of proudcase.

  * proudcase is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * proudcase is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with proudcase.  If not, see <http://www.gnu.org/licenses/>.
  
  * @Author: Michel Vocks
  *
  * @Date: 12.10.2013
  *
  * @Encoding: UTF-8
*/
public class YouTubeUtil {
    
    // after the v= key comes the video id. That is what we want!
    private static final String YOUTUBE_KEY = "v=";
    
    // default youtube link
    private static final String YOUTUBE_LINK = "http://www.youtube.com/v/";
    private static final String YOUTUBE_VERSION = "?version=3";
    
    // youtube thumbnail
    private static final String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_MAXRES = "/maxresdefault.jpg";
    
    // This method converts a typed youtube link into the right form
    public static String convertYouTubeLink(String url) {
        // parse the youtube link with the video id
        String newUrl = YOUTUBE_LINK + getVideoID(url) + YOUTUBE_VERSION;
        
        return newUrl;
    }
    
    // This method parses the URL for the youtube thumbnail 
    public static String getYouTubeThumbnailLink(String url) {
        // parse the thumbnail link
        String thumbnailLink = YOUTUBE_THUMBNAIL_URL + getVideoID(url) + YOUTUBE_THUMBNAIL_MAXRES;
        
        return thumbnailLink;
    }
    
    private static String getVideoID(String url) {
        int index = url.indexOf(YOUTUBE_KEY);
        String videoID = "";
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
                videoID += oneChar;
            }
        }
        
        return videoID;
    }
    
}

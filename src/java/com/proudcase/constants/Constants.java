package com.proudcase.constants;

import java.util.Locale;

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

/**
 * @Author: Michel Vocks
 *
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
public class Constants {
    
    // Number of visibilitys (look proudcase.visibility.EVisibility.java)
    public static final int NUMOFVISIBILITYS = 4;
    
    // Default locale
    public static final Locale DEFAULTLOCALE = Locale.ENGLISH;
    
    // redirect string
    public static final String FACESREDIRECT = "?faces-redirect=true";
    
    // 5 MB maxupload pictures
    public static final int UPLOADSIZELIMIT = 5242880; 
    
    // max images for one showcase
    public static final int MAXIMAGESFORSHOWCASE = 10;
    
    // AUTH key
    public static final String AUTH_KEY = "proudcase.account";
    
    // number of stars for rating
    public static final int NUMBEROFSTARS = 10;
    
    // max number for suggestionlist on every search operation
    public static final int MAXSUGGESTIONS = 10;
    
    // static ICQ url string
    public static final String ICQURL = "http://web.icq.com/whitepages/online?icq=";
    
    // added to the URL, the image which has to be displayed
    public static final String ICQURL_END = "&img=5";
    
    // basePath Path
    // pc: E:\selfmade\crap
    // server: /var/lib/proudcase-images
    public static final String BASEPATH = "E:\\selfmade\\crap";
    
    // folder for images (on edit: don't forget to edit the servlet mapping)
    public static final String IMAGEFOLDER = "images";
    
    // temp folder for uploaded images (on edit: don't forget to edit the servlet mapping)
    public static final String IMAGETEMPFOLDER = "imagetemp";
    
    // folder for secured images
    public static final String IMAGESECUREFOLDER = "imagesecure";
    
    // proudcase maximum chars for input
    public static final int MAXCHARSREMAINING = 250;
    
    // maximum chars for explaintext on output (i.g. searchresult)
    public static final int MAXCHARSEXPLAINTEXT = 300;
    
    // maximum chars for title from showcase
    public static final int MAXCHARSSHOWCASETITLE = 200;
    
    // maximum users to display on top-users view on index.xhtml
    public static final int MAXTOPUSERS = 15;
    
    // proudcase save login cookie name
    public static final String SAVELOGIN_COOKIE_NAME = "proudcase-cookie";
    
    // max size of an image (images are resized after upload)
    public static final int MAX_IMAGE_SIZE = 670; // 670 pixel largest display on index.html
}

package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.mongodb.manager.CategorieManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.SupportedLanguagesManager;
import com.proudcase.persistence.CategorieBean;
import com.proudcase.persistence.LangCategorieBean;
import com.proudcase.persistence.SupportedLanguagesBean;
import com.proudcase.util.VideoUtil;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

/**
  * Copyright © 26.07.2013 Michel Vocks
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
 * @Date: 26.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean(eager = true)
@ApplicationScoped
public class ApplicationBean implements Serializable {
    
    private static List<CategorieBean> categorieList =
            new ArrayList<>();
    transient private CategorieManager categorieManager =
            ManagerFactory.createCategorieManager();
    
    private static List<SupportedLanguagesBean> supportedLanguagesList =
            new ArrayList<>();
    transient private SupportedLanguagesManager supportedLanguagesManager =
            ManagerFactory.createSupportedLanguagesManager();
    
     // ExecutorService for the spawned thread that manage the video encoding
    private static ExecutorService videoEncodingService;

    public ApplicationBean() {
    }
    
    @PostConstruct
    public void applicationInitialized() {
        // get all categories by language
        categorieList = categorieManager.getAllCategories();
        
        // remove the link to the manager to release the connection
        categorieManager = null;
        
        // Get all supported languages
        supportedLanguagesList = supportedLanguagesManager.getAllLanguages();
        
        // remove the link to the manager to release the connection
        supportedLanguagesManager = null;
        
        // Initialize the thread pool for video encoding
        videoEncodingService = Executors.newFixedThreadPool(Constants.MAX_VIDEO_ENCODING_THREADS);
        
        // set it to the video util class
        VideoUtil.videoEncodingService = videoEncodingService;
    }
    
    @PreDestroy
    public void applicationDestroyed() {
        // Shutdown the fixed thread pool for video encoding now!
        videoEncodingService.shutdownNow();
    }
    
    public List<LangCategorieBean> getCategoriesByLocale(Locale locale) {
        List<LangCategorieBean> localeCategorieList = new ArrayList<>();
        
        // iterate through alle categories
        for (CategorieBean singleCategorie : categorieList) {
            // now iterate through all available translations
            boolean foundInLanguage = false;
            for (LangCategorieBean singleLangCategorie : singleCategorie.getLangCategorieList()) {
                if (singleLangCategorie.getLanguage().equals(locale)) {
                    // found in this locale so add the list
                    localeCategorieList.add(singleLangCategorie);
                    foundInLanguage = true;
                    break;
                }
            }
            
            // not found
            if (!foundInLanguage) {
                // iterate all translations again
                for (LangCategorieBean singleLangCategorie : singleCategorie.getLangCategorieList()) {
                    // english is always available
                    if (singleLangCategorie.getLanguage().equals(Locale.ENGLISH)) {
                        localeCategorieList.add(singleLangCategorie);
                        break;
                    }
                }
            }
        }
        
        // return the list
        return localeCategorieList;
    }
    
    public List<SelectItem> getSupportedLanguagesAsSelectItems(Locale locale) {
        List<SelectItem> languagesList = new ArrayList<>();
        
        // iterate through all languages
        for (SupportedLanguagesBean supportedLanguage : supportedLanguagesList) {
            // create a select item
            languagesList.add(new SelectItem(supportedLanguage.getLanguage(),
                    supportedLanguage.getLanguage().getDisplayLanguage(locale)));
        }
        return languagesList;
    }
    
    public String convertBytesToMegaBytes(long bytes) {
        if (bytes > 0) {
            DecimalFormat df = new DecimalFormat("###.##");
            double megaBytes = (double) bytes / 1024 / 1024;
            return df.format(megaBytes);
        }
        return "0.0";
    }

    public List<CategorieBean> getCategorieList() {
        return categorieList;
    }

    public List<SupportedLanguagesBean> getSupportedLanguagesList() {
        return supportedLanguagesList;
    }
}

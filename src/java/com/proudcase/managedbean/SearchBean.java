package com.proudcase.managedbean;

import com.proudcase.constants.ENavigation;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.CategorieBean;
import com.proudcase.persistence.LangCategorieBean;
import com.proudcase.view.ShowcaseViewBean;
import com.proudcase.view.LazySearchModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.bson.types.ObjectId;
import org.primefaces.model.LazyDataModel;

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
@ManagedBean
@ViewScoped
public class SearchBean implements Serializable {
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
    
    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;
    
    private String inputSearch;
    private List<SelectItem> categorieList = new ArrayList<>();;
    private String currentSelection;
    private LazyDataModel<ShowcaseViewBean> lazySearchResultList;
    private final transient ShowcaseManager showcaseManager
            = ManagerFactory.createShowcaseManager();

    public SearchBean() {
    }
    
    @PostConstruct
    public void init() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        
         // add initial selected item
        String label = PropertyReader.getMessageResourceString(fCtx.getApplication()
                .getMessageBundle(), "choicecate", null, sessionBean.getUserLocale());
        categorieList.add(new SelectItem(null, label));
        
        // get all categories
        for (LangCategorieBean categorie : sessionBean.getLocaleCategorieList()) {
            // generate a select item
            SelectItem categorieSelect = new SelectItem(categorie.getCategoriename(), categorie.getCategoriename());
            categorieList.add(categorieSelect);
        }
    }
    
    public void search() {
        // we want to filter for a categorie?
        ObjectId categorieId = filterCategorieID();
        
        // search null?
        if (inputSearch == null) {
            inputSearch = "";
        }
        
        // initiate again our lazy model
        lazySearchResultList = new LazySearchModel(showcaseManager, inputSearch, sessionBean, categorieId);
    }
    
    private ObjectId filterCategorieID() {
        // we want to filter for a categorie?
        if (currentSelection != null) {
            // get the full categorie list and iterate
            for (CategorieBean categorie : applicationBean.getCategorieList()) {
                // iterate through the available translations
                for (LangCategorieBean translatedCategorie : categorie.getLangCategorieList()) {
                    // we found our categorie?
                    if (translatedCategorie.getCategoriename().equals(currentSelection)) {
                        return categorie.getId();
                    }
                }
            }
        }
        return null;
    }
    
    public String searchWithKeywords() {
        return ENavigation.SEARCH.toString();
    }

    public String getInputSearch() {
        return inputSearch;
    }

    public void setInputSearch(String inputSearch) {
        this.inputSearch = inputSearch;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public LazyDataModel<ShowcaseViewBean> getLazySearchResultList() {
        return lazySearchResultList;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public List<SelectItem> getCategorieList() {
        return categorieList;
    }

    public void setCategorieList(List<SelectItem> categorieList) {
        this.categorieList = categorieList;
    }

    public String getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(String currentSelection) {
        this.currentSelection = currentSelection;
    }
}

package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.EPrivileges;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.LangCategorieBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 * Copyright © 08.10.2012 Michel Vocks This file is part of proudcase.
 *
 * proudcase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * proudcase is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * proudcase. If not, see <http://www.gnu.org/licenses/>.
 *
 * /
 *
 **
 * @Author: Michel Vocks
 *
 * @Date: 08.10.2012
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@SessionScoped
public class SessionBean implements Serializable {
    
    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    private Locale userLocale;
    private List<LangCategorieBean> localeCategorieList;
    private boolean showGlobalInfoDialog;
    
    public SessionBean() {
    }

    @PostConstruct
    public void init() {
        // is the user logged in?
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        if (loggedUser != null) {
            setUserLocale(loggedUser.getPreferredLanguage());
        } else {
            // get the locale from the browser
            setUserLocale(userLocale = fCtx.getViewRoot().getLocale());
        }
    }
    
    private void setCategorieList() {
        // get all categories by locale
        localeCategorieList = applicationBean.getCategoriesByLocale(userLocale);
        
        // sort alphabetic
        Collections.sort(localeCategorieList);
    }
    
    public String privilegesText(EPrivileges priv) {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String output;
        
        // This is an admin
        if (priv.equals(EPrivileges.admin)) {
            output = PropertyReader.getMessageResourceString(fCtx.getApplication()
                    .getMessageBundle(), "priv_admin", null, userLocale);
        } else if (priv.equals(EPrivileges.jury)) {
            // jury member
            output = PropertyReader.getMessageResourceString(fCtx.getApplication()
                    .getMessageBundle(), "priv_jury", null, userLocale);
        } else {
            // must be an user
            output = PropertyReader.getMessageResourceString(fCtx.getApplication()
                    .getMessageBundle(), "priv_user", null, userLocale);
        }
        
        // return output
        return output;
    }
    
    public List<SelectItem> getLanguagesAsSelectItem() {
        // return from application bean
        return applicationBean.getSupportedLanguagesAsSelectItems(userLocale);
    }
    
    // Returns a the display name from a locale in the language from the user
    public String localeAsString(Locale returnLocale) {
        return returnLocale.getDisplayLanguage(userLocale);
    }
    
    public Locale getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
        
        // is the user logged in?
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        if (loggedUser != null) {
            // update the locale in our database 
            UserManager userManager = ManagerFactory.createUserManager();
            userManager.updateUserLocale(loggedUser, userLocale);
        }

        // also reload the categories
        setCategorieList();
    }
    
    public SelectItem[] getVisibleNames() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        SelectItem[] visibleList = new SelectItem[Constants.NUMOFVISIBILITYS];

        String label;
        // Add the @all label
        label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "allvisible", null, getUserLocale());
        visibleList[0] = new SelectItem(EVisibility.all, label);

        // Add the @friendsfriends label
        label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "friendsfriendsvisible", null, getUserLocale());
        visibleList[1] = new SelectItem(EVisibility.friendsfriends, label);

        // Add the @friends label
        label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "friendsvisible", null, getUserLocale());
        visibleList[2] = new SelectItem(EVisibility.friends, label);

        // Add the @onlyme label
        label = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), "onlymevisible", null, getUserLocale());
        visibleList[3] = new SelectItem(EVisibility.onlyme, label);

        return visibleList;

    }
    
    public void switchOffGlobalInfoDialog() {
        this.showGlobalInfoDialog = false;
    }

    public boolean isShowGlobalInfoDialog() {
        return showGlobalInfoDialog;
    }

    public void setShowGlobalInfoDialog(boolean showGlobalInfoDialog) {
        this.showGlobalInfoDialog = showGlobalInfoDialog;
    }
    
    public List<LangCategorieBean> getLocaleCategorieList() {
        return localeCategorieList;
    }

    public void setLocaleCategorieList(List<LangCategorieBean> localeCategorieList) {
        this.localeCategorieList = localeCategorieList;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }
}

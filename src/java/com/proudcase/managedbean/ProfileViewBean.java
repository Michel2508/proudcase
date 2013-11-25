package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.util.LanguageTranslationUtil;
import com.proudcase.util.ShowcaseViewTranslator;
import com.proudcase.util.UserRightEstimate;
import com.proudcase.view.ShowcaseViewBean;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.bson.types.ObjectId;

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
public class ProfileViewBean implements Serializable {
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
    
    private UserBean givenUser =
            new UserBean();
    private final transient UserManager userManager = 
            ManagerFactory.createUserManager();
    private final transient ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    
    private String userID;
    private String icqurl;
    private List<ShowcaseViewBean> showcaseViewList;

    public ProfileViewBean() {
    }
    
    public void init() {
        // retrieve the current user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        // we got a real user?
        if (userID != null && ObjectId.isValid(userID)) {
            givenUser = userManager.get(new ObjectId(userID));
        }
        
        // found?
        if (givenUser == null || givenUser.getId() == null) {
            return;
        }
        
        // icqnumber not emptY?
        if (givenUser.getIcqnumber() != null && !givenUser.getIcqnumber().equals("")) {
            // prepare the icq number to display it later in a nice way
            icqurl = Constants.ICQURL + givenUser.getIcqnumber() + Constants.ICQURL_END;
        }
        
        // Get all showcases from this user
        List<ShowcaseBean> allShowcasesByUser = showcaseManager.getAllPublicShowcasesByUser(givenUser);
        
        // Initiate the view list
        showcaseViewList = new ArrayList<>();
        
        // Iterate trough all showcases
        for (ShowcaseBean singleShowcase : allShowcasesByUser) {
            // Has this user the rights to see this showcase?
            if (UserRightEstimate.userHasRights(loggedUser, givenUser, singleShowcase.getVisibility())) {
                // the user has the rights to see this showcase.
                // Get the right text object of the showcase
                ShowcaseTextBean langShowcase = LanguageTranslationUtil.getSpecifiedText(singleShowcase, sessionBean.getUserLocale());
                
                // found something?
                if (langShowcase != null) {
                    // Convert two objects to one view object
                    ShowcaseViewBean showcaseViewObj = ShowcaseViewTranslator.convertShowcaseToShowcaseView(singleShowcase, langShowcase, true);
                    
                    // add it to our view list
                    showcaseViewList.add(showcaseViewObj);
                } 
            }
        }
    }
    
    public boolean isRightsToLook() {
        // retrieve the current user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        // Check if the user has the rights to watch the requested resource
        return UserRightEstimate.userHasRights(loggedUser, givenUser, EVisibility.friends);
    }

    public UserBean getGivenUser() {
        return givenUser;
    }

    public void setGivenUser(UserBean givenUser) {
        this.givenUser = givenUser;
    }

    public String getIcqurl() {
        return icqurl;
    }

    public void setIcqurl(String icqurl) {
        this.icqurl = icqurl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<ShowcaseViewBean> getShowcaseViewList() {
        return showcaseViewList;
    }
}

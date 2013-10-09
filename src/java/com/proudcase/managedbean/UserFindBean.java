package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
  * Copyright © 04.11.2012 Michel Vocks
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
 * @Date: 04.11.2012
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@ViewScoped
public class UserFindBean implements Serializable {
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;

    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    private List<UserBean> suggestionList =
            new ArrayList<>();
    
    public UserFindBean() {
    }

    public List<UserBean> completeFriend(String query) {
        // retrieve the current logged user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        // empty user? wtf?
        if (loggedUser == null || loggedUser.getId() == null) {
            return null;
        }
        
        // retrieve the suggestions
        suggestionList = userManager.getSuggestionList(query);

        // remove our self (if included)
        for (UserBean suggestedUser : suggestionList) {
            if (suggestedUser.getId().equals(loggedUser.getId())) {
                suggestionList.remove(suggestedUser);
                break;
            }
        }
        
        return suggestionList;
    }

    public List<UserBean> getSuggestionList() {
        return suggestionList;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import java.io.Serializable;
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
    transient private UserManager userManager = 
            ManagerFactory.createUserManager();
    private String userID;
    private String icqurl;

    public ProfileViewBean() {
    }
    
    public void init() {
        // got we a true user?
        if (userID != null) {
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
    }
    
    public boolean isRightsToLook() {
        // retrieve the current user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        if (loggedUser == null || loggedUser.getId() == null) {
            return false;
        }
        if (givenUser == null || givenUser.getId() == null) {
            return false;
        }
        
        // check if both users are equal
        if (loggedUser.getId().equals(givenUser.getId())) {
            return true;
        }
        
        // check if both are friends 
        boolean isFriend = userManager.isFriend(loggedUser, givenUser);
        return isFriend;
        
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
}

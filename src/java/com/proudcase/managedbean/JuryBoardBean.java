package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.EPrivileges;
import com.proudcase.mongodb.manager.JuryFeedbackManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.UserBean;
import com.proudcase.view.ShowcaseViewBean;
import com.proudcase.view.LazyJuryBoardModel;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
  
  * @Author: Michel Vocks
  *
  * @Date: 04.09.2013
  *
  * @Encoding: UTF-8
*/
@ManagedBean
@ViewScoped
public class JuryBoardBean implements Serializable {
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
    
    private final transient ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    private final transient JuryFeedbackManager juryFeedbackManager =
            ManagerFactory.createJuryFeedbackManager();
    
    private LazyDataModel<ShowcaseViewBean> lazyJuryBoardModel;
    
    public void init() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);
        
        // is user logged in and a jury member?
        if (loggedUser == null || !loggedUser.getUserPrivs().equals(EPrivileges.jury)) {
            return;
        }
        
        // on every page reload we want a fresh model
        lazyJuryBoardModel = new LazyJuryBoardModel(showcaseManager, juryFeedbackManager, sessionBean, loggedUser);
    }

    public LazyDataModel<ShowcaseViewBean> getLazyJuryBoardModel() {
        return lazyJuryBoardModel;
    }

    public void setLazyJuryBoardModel(LazyDataModel<ShowcaseViewBean> lazyJuryBoardModel) {
        this.lazyJuryBoardModel = lazyJuryBoardModel;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

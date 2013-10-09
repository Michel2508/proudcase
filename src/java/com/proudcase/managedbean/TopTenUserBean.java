package com.proudcase.managedbean;

import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
  * Copyright © 15.07.2013 Michel Vocks
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
 * @Date: 15.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@SessionScoped
public class TopTenUserBean implements Serializable {

    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    private List<UserBean> topTenUsers;
    
    private static final int TOPTEN = 10;
    
    public TopTenUserBean() {
    }
    
    @PostConstruct
    public void init() {
        // load the top ten users
        topTenUsers = userManager.getTopUsersByLimit(TOPTEN);
        
        // if we got nothing
        if (topTenUsers == null) {
            topTenUsers = new ArrayList<>();
        }
    }
    
    public List<UserBean> getTopTenUsers() {
        return topTenUsers;
    }

}

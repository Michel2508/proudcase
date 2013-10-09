package com.proudcase.converter;

import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
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
public class UserConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String submittedValue) {
        if (submittedValue.equals("") || !ObjectId.isValid(submittedValue)) {
            return null;
        } else {
            try {
                // create a usermanager
                UserManager userManager = ManagerFactory.createUserManager();
                
                // find the user
                UserBean foundUser = userManager.get(new ObjectId(submittedValue));

                // found one?
                if (foundUser != null) {
                    // return the user
                    return foundUser;
                }
                
            } catch (NumberFormatException exception) {
                try {
                    throw new ExceptionLogger(exception);
                } catch (ExceptionLogger ex1) {
                }
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object value) {
        if (value == null || value.equals("")) {  
            return "";  
        } else {  
            return String.valueOf(((UserBean) value).getId());  
        }  
    }
}

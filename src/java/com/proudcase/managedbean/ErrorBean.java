package com.proudcase.managedbean;

import com.proudcase.filehandling.PropertyReader;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 * Copyright © 03.07.2013 Michel Vocks This file is part of proudcase.
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
 * @Author: Michel Vocks
 *
 * @Date: 01.09.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@RequestScoped
public class ErrorBean implements Serializable {

    public ErrorBean() {
    }

    public void init() {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // exist already messages
        if (fCtx.getMessageList().isEmpty()) {
            // no so add one
            String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "error_exceptionthrown", null, fCtx.getViewRoot().getLocale());

            // add the error message
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", outputMessage));
        }
    }
}

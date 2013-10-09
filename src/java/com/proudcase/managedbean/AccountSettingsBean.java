package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ImageManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ImageBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.util.ImageUtil;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Copyright © 24.09.2012 Michel Vocks This file is part of proudcase.
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
 * @Date: 24.09.2012
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@ViewScoped
public class AccountSettingsBean implements Serializable {

    private UserBean singleUser;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    transient private UserManager userManager =
            ManagerFactory.createUserManager();

    public AccountSettingsBean() {
    }

    public void loadSettings() {
        // give me the user object
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext()
                .getSessionMap().get(Constants.AUTH_KEY);

        // something wrong here
        if (loggedUser == null) {
            return;
        }

        // set the user
        singleUser = userManager.get(loggedUser.getId());
    }

    public void saveSettings() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String label;

        // save the displayname in a specific format
        char[] formatBuilder;
        if (singleUser.getDisplayname() == null || singleUser.getDisplayname().length() == 0) {
            // ERROR
            label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "nickname_notspecified", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", label));
            return;
        }

        formatBuilder = singleUser.getDisplayname().toLowerCase().toCharArray();
        formatBuilder[0] = Character.toUpperCase(formatBuilder[0]);
        singleUser.setDisplayname(new String(formatBuilder));

        // update the user or throw an error if something went wrong
        if (userManager.save(singleUser) != null) {
            // put the new userobj in our map
            fCtx.getExternalContext().getSessionMap().put(Constants.AUTH_KEY, singleUser);

            label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "settingssaved", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", label));
        } else {
            // ERROR
            label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                    getMessageBundle(), "settingsnotsaved", null, sessionBean.getUserLocale());
            fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", label));
        }
    }

    public void handlePictureUpload(FileUploadEvent event) throws Exception {
        UploadedFile newAvatar = event.getFile();
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // empty?
        if (newAvatar != null && singleUser != null) {
            // save the image to the storage
            ImageBean newImage = ImageUtil.saveImageAsFile(newAvatar, singleUser.getId(), EVisibility.all, false);

            // set the relationship to the user
            singleUser.setAvatar(newImage);

            // save the image in the database
            ImageManager imageManager = ManagerFactory.createImageManager();
            imageManager.save(newImage);

            // save the path to the image in the database
            UserBean user = userManager.updateImagePath(singleUser);
            if (user == null) {
                throw new ExceptionLogger(
                        new RuntimeException("Update imagepath error -> "
                        + singleUser.getId().toString() + ";Image:"
                        + newImage.getRelativeimagepath()));
            } else {
                // everything is fine -> replace the user in the session map
                fCtx.getExternalContext().getSessionMap().put(Constants.AUTH_KEY, user);

                // additionally, update here in this bean the user 
                singleUser = user;

                // add a facesmessage
                String label = PropertyReader.getMessageResourceString(fCtx.getApplication().
                        getMessageBundle(), "avataruploadsuccess", null, sessionBean.getUserLocale());
                fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", label));
            }
        }
    }

    public UserBean getSingleUser() {
        return singleUser;
    }

    public void setSingleUser(UserBean singleUser) {
        this.singleUser = singleUser;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

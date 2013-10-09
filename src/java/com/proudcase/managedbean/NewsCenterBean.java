package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.exclogger.ExceptionLogger;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.ImageManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ProudcaseManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ImageBean;
import com.proudcase.persistence.ProudcaseBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.util.ImageUtil;
import com.proudcase.view.LazyNewsCenterModel;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;

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
public class NewsCenterBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    
    private ProudcaseBean singleProudcase =
            new ProudcaseBean();
    private ImageBean singleImage =
            new ImageBean();
    
    transient private ProudcaseManager proudcaseManager =
            ManagerFactory.createProudcaseManager();
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    transient private ImageManager imageManager = 
            ManagerFactory.createImageManager();
    
    private LazyDataModel<ProudcaseBean> lazyProudcaseList;
    
    @PostConstruct
    public void init() {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user? leave
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // create the lazy object
        lazyProudcaseList = new LazyNewsCenterModel(loggedUser, proudcaseManager, userManager);
    }

    public void createProudcase() throws ExceptionLogger {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // all right here?
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // no image added AND no text written. That is unacceptable!
        if (singleProudcase.getExplaintext() == null && singleImage.getRelativeimagepath() == null) {
            String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                    "imageandtextleft", null, sessionBean.getUserLocale());
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "", outputMessage);
            fCtx.addMessage(null, message);
            return;
        }

        if (singleImage.getRelativeimagepath() != null
                && !singleImage.getRelativeimagepath().equals("")) {
            // determine if the image is secured or not
            boolean secure = true;
            if (singleProudcase.getVisibility().equals(EVisibility.all)) {
                secure = false;
            }
            
            // move our image out of the temp folder
            singleImage.setRelativeimagepath(
                    ImageUtil.moveImageToUserDir(singleImage.getRelativeimagepath(), loggedUser.getId(), secure));
            
            // update the security rule (inherited from the proudcase)
            singleImage.setSecurityRule(singleProudcase.getVisibility());
            
            // save image in the db
            imageManager.save(singleImage);
            
            // add our image to the proudcase
            singleProudcase.setProudcaseImage(singleImage);
        }

        // --- Set all data to our new proudcase ---
        // so add the accountobj to our singleProudcase object
        singleProudcase.setProudcaseOwner(loggedUser);

        // set the create date
        singleProudcase.setCreatedate(new Date());
        
        // save the proudcase in the database
        proudcaseManager.save(singleProudcase);
        
        // remove the old stuff
        singleProudcase = new ProudcaseBean();
        singleImage = new ImageBean();
    }

    public void addInfoMessage() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                "typeinformation", null, sessionBean.getUserLocale());
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "", outputMessage);
        fCtx.addMessage(null, message);
    }

    public void handlePictureUpload(FileUploadEvent event) throws ExceptionLogger {
        // Get the informations from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user?
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // empty file?
        if (event != null && event.getFile() != null) {
            UploadedFile picture = event.getFile();

            // save our image to the temp folder
            singleImage = ImageUtil.saveImageInTemp(picture, loggedUser.getId(), singleProudcase.getVisibility());
        }
    }
    
    public boolean isImageAlreadyUploaded() {
        if (singleImage.getRelativeimagepath() == null) {
            return false;
        }
        return !singleImage.getRelativeimagepath().equals("");
    }
    
    public ProudcaseBean getSingleProudcase() {
        return singleProudcase;
    }

    public void setSingleProudcase(ProudcaseBean singleProudcase) {
        this.singleProudcase = singleProudcase;
    }

    public ImageBean getSingleImage() {
        return singleImage;
    }

    public void setSingleImage(ImageBean singleImage) {
        this.singleImage = singleImage;
    }

    public LazyDataModel<ProudcaseBean> getLazyProudcaseList() {
        return lazyProudcaseList;
    }

    public void setLazyProudcaseList(LazyDataModel<ProudcaseBean> lazyProudcaseList) {
        this.lazyProudcaseList = lazyProudcaseList;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

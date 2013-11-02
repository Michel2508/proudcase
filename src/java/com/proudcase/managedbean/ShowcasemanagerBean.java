package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.ENavigation;
import com.proudcase.constants.EVideoTyp;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.mongodb.manager.FileManager;
import com.proudcase.mongodb.manager.ImageManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.mongodb.manager.VideoLinkManager;
import com.proudcase.persistence.*;
import com.proudcase.util.FileUtil;
import com.proudcase.util.ImageUtil;
import com.proudcase.util.VideoUtil;
import com.proudcase.view.ShowcasemanagerViewBean;
import com.proudcase.visibility.VisibilityLangConverter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * Copyright © 24.07.2013 Michel Vocks This file is part of proudcase.
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
 * @Date: 24.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@ViewScoped
public class ShowcasemanagerBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    private final transient ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    private final transient ImageManager imageManager =
            ManagerFactory.createImageManager();
    private List<ShowcasemanagerViewBean> showcaseViewList;
    private ShowcasemanagerViewBean selectedViewObj;
    private static final String SHOWCASEIDPARAM = "&showcaseid=";

    public ShowcasemanagerBean() {
    }

    public void init() {
        // Get the information from the user
        FacesContext fCtx = FacesContext.getCurrentInstance();
        UserBean loggedUser = (UserBean) fCtx.getExternalContext().getSessionMap().get(Constants.AUTH_KEY);

        // wrong user? leave
        if (loggedUser == null || loggedUser.getId() == null) {
            return;
        }

        // initially load all showcases from the user
        List<ShowcaseBean> showcaseListDB = showcaseManager.getAllShowcasesByUser(loggedUser);

        // we got something?
        if (showcaseListDB == null || showcaseListDB.isEmpty()) {
            return;
        }

        // initiate the view list
        showcaseViewList = new ArrayList<>();

        // Iterate through all showcases
        for (ShowcaseBean singleShowcase : showcaseListDB) {
            // convert every showcase to our view object
            ShowcasemanagerViewBean viewObj = new ShowcasemanagerViewBean();
            viewObj.setShowcaseId(singleShowcase.getId());
            viewObj.setCreatedate(singleShowcase.getCreatedate());

            // Get the visibility from the language file
            String visibilityString = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(), VisibilityLangConverter.convertVisibilityToLangKey(
                    singleShowcase.getVisibility()), null, sessionBean.getUserLocale());
            viewObj.setVisibility(visibilityString);

            // Generate the output for the available languages
            String availableLangs = "";
            for (ShowcaseTextBean showcaseText : singleShowcase.getShowcaseTexts()) {
                availableLangs += showcaseText.getLang().getDisplayLanguage(sessionBean.getUserLocale()) + ", ";
            }

            // add the language string but please remove the last comma
            viewObj.setAvailableLangs(availableLangs.substring(0, availableLangs.length() - 2));

            // calculate the rating
            long showcaseRating = 0;
            if (singleShowcase.getShowcaseRankings() != null) {
                for (ShowcaseRankingBean showcaseRanking : singleShowcase.getShowcaseRankings()) {
                    showcaseRating += showcaseRanking.getRanking();
                }
            }

            // set the rating value
            viewObj.setShowcaseRating(showcaseRating);

            // check if we can find the title in a language that fits to
            // the users language
            ShowcaseTextBean langShowcase = getSpecifiedText(singleShowcase);

            // found something
            if (langShowcase != null) {
                viewObj.setShowcasetitle(langShowcase.getTitle());
            }

            // do we have pictures for the showcase?
            if (singleShowcase.getImageList() != null && !singleShowcase.getImageList().isEmpty()) {
                // sort the images
                Collections.sort(singleShowcase.getImageList());

                // save the first image (frontimage) to our view object
                viewObj.setFrontImage(singleShowcase.getImageList().get(0));
            }

            // add the showcase view to our array
            showcaseViewList.add(viewObj);
        }
    }

    public String editShowcase() {
        if (selectedViewObj != null) {
            // redirect the user to the newshowcase page but initially load the showcase
            return ENavigation.NEWSHOWCASE.toString()
                    + SHOWCASEIDPARAM + selectedViewObj.getShowcaseId().toString();
        }
        return null;
    }

    public String deleteShowcase() {
        // load the full showcase from the database
        ShowcaseBean showcase = showcaseManager.get(selectedViewObj.getShowcaseId());

        // something wrong here get out
        if (showcase == null) {
            return null;
        }

        // delete the pictures 
        if (showcase.getImageList() != null && !showcase.getImageList().isEmpty()) {
            for (ImageBean singleImage : showcase.getImageList()) {
                // delete physically
                ImageUtil.deleteImage(singleImage.getRelativeimagepath());

                // delete database object
                imageManager.deleteById(singleImage.getId());
            }
        }

        // delete the video links
        if (showcase.getVideoLinks() != null && !showcase.getVideoLinks().isEmpty()) {
            // get the videolink manager
            VideoLinkManager videoLinkManager = ManagerFactory.createVideoLinkManager();
            
            for (VideoLinkBean videoLink : showcase.getVideoLinks()) {
                // delete
                videoLinkManager.delete(videoLink);
                
                // is the video self hosted?
                if (videoLink.getVideoTyp().equals(EVideoTyp.SELFHOSTEDVIDEO)) {
                    // remove it from the harddisc
                    VideoUtil.deleteVideo(videoLink);
                }
            }
        }
        
        // delete the files
        if (showcase.getFileList() != null && !showcase.getFileList().isEmpty()) {
            // get the file manager
            FileManager fileManager = ManagerFactory.createFileManager();
            
            for (FileBean file : showcase.getFileList()) {
                // delete
                fileManager.delete(file);
                
                // remove it from the harddisc
                FileUtil.deleteFile(file.getRelativeFilePath());
            }
        }

        // delete the showcase from the database
        showcaseManager.deleteById(showcase.getId());

        // remove the showcase from our view list
        for (ShowcasemanagerViewBean viewObj : showcaseViewList) {
            if (viewObj.getShowcaseId().equals(showcase.getId())) {
                // remove 
                showcaseViewList.remove(viewObj);
                break;
            }
        }

        // print the message
        FacesContext fCtx = FacesContext.getCurrentInstance();
        String outputMessage = PropertyReader.getMessageResourceString(fCtx.getApplication().getMessageBundle(),
                "showcasedelete_success", null, sessionBean.getUserLocale());
        fCtx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, outputMessage));

        return null;
    }

    // this method returns the ShowcaseText object
    // for the client language
    private ShowcaseTextBean getSpecifiedText(ShowcaseBean givenShowcase) {
        // get the language
        Locale clientLanguage = sessionBean.getUserLocale();

        // now we have to check if this showcase has this language supported
        for (ShowcaseTextBean singleShowcaseText : givenShowcase.getShowcaseTexts()) {
            if (singleShowcaseText.getLang().equals(clientLanguage)) {
                // we found the right text. Return the object
                return singleShowcaseText;
            }
        }

        // if we are here then the default language isn't supported
        // so let us check if the owner of the showcase has english support
        for (ShowcaseTextBean singleShowcaseText : givenShowcase.getShowcaseTexts()) {
            if (singleShowcaseText.getLang().equals(Locale.ENGLISH)) {
                return singleShowcaseText;
            }
        }

        // Well. If we are here then english isn't supported nor the default
        // language from the client.
        if (!givenShowcase.getShowcaseTexts().isEmpty()) {
            return givenShowcase.getShowcaseTexts().get(0);
        }
        return null;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<ShowcasemanagerViewBean> getShowcaseViewList() {
        return showcaseViewList;
    }

    public void setShowcaseViewList(List<ShowcasemanagerViewBean> showcaseViewList) {
        this.showcaseViewList = showcaseViewList;
    }

    public ShowcasemanagerViewBean getSelectedViewObj() {
        return selectedViewObj;
    }

    public void setSelectedViewObj(ShowcasemanagerViewBean selectedViewObj) {
        this.selectedViewObj = selectedViewObj;
    }
}

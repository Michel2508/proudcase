package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.ENavigation;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.persistence.VideoLinkBean;
import com.proudcase.util.LanguageTranslationUtil;
import com.proudcase.util.ShowcaseViewTranslator;
import com.proudcase.view.ShowcaseViewBean;
import com.proudcase.view.ShowcaseVideoViewBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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
 * /
 *
 **
 * @Author: Michel Vocks
 *
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@ViewScoped
public class IndexBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    
    private final transient ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    private final transient UserManager userManager =
            ManagerFactory.createUserManager();
    
    private final List<ShowcaseViewBean> topTenShowcases = new ArrayList<>();
    private final List<ShowcaseVideoViewBean> topTenVideoShowcases = new ArrayList<>();
    private List<UserBean> topUsersList;
    private String inputQuery;
    
    @PostConstruct
    public void init() {
        // Load top users
        topUsersList = userManager.getTopUsersByLimit(Constants.MAXTOPUSERS);

        // found nothing?
        if (topUsersList == null) {
            topUsersList = new ArrayList<>();
        }

        // load the top then video showcases
        List<ShowcaseBean> topTenVideoShowcasesFromDB = showcaseManager.getTopTenVideoShowcases();

        // not empty
        if (topTenVideoShowcasesFromDB != null && !topTenVideoShowcasesFromDB.isEmpty()) {
            // Iterate through all showcases
            for (ShowcaseBean singleShowcase : topTenVideoShowcasesFromDB) {
                // convert every showcase to our view object
                ShowcaseVideoViewBean showcaseVideoViewBean = new ShowcaseVideoViewBean();
                showcaseVideoViewBean.setShowcaseID(singleShowcase.getId());

                // add the avatar from the showcase owner
                showcaseVideoViewBean.setOwnerAvatar(singleShowcase.getUserAccount().getAvatar());

                // check if we can find the title and the text in a language that fits to
                // the users language
                ShowcaseTextBean langShowcase = LanguageTranslationUtil.getSpecifiedText(singleShowcase, sessionBean.getUserLocale());

                // found something
                if (langShowcase != null) {
                    showcaseVideoViewBean.setShowcaseTitle(langShowcase.getTitle());
                }

                // we should have videos
                if (singleShowcase.getVideoLinks() != null && !singleShowcase.getVideoLinks().isEmpty()) {
                    // get the first video 
                    VideoLinkBean firstVideoLink = singleShowcase.getVideoLinks().get(0);
                    
                    // save the first video to our object
                    showcaseVideoViewBean.setVideoLink(firstVideoLink);
                }

                // add the showcase view to our array
                topTenVideoShowcases.add(showcaseVideoViewBean);
            }
        }

        // load the top ten showcases
        List<ShowcaseBean> topTenShowcasesFromDB = showcaseManager.getTopTenShowcases();

        // empty?
        if (topTenShowcasesFromDB == null || topTenShowcasesFromDB.isEmpty()) {
            return;
        }

        // Iterate through all showcases
        for (ShowcaseBean singleShowcase : topTenShowcasesFromDB) {
            // check if we can find the text and title in a language that fits to
            // the users language
            ShowcaseTextBean langShowcase = LanguageTranslationUtil.getSpecifiedText(singleShowcase, sessionBean.getUserLocale());

            // found something
            if (langShowcase != null) {
                // Convert two objects to one view obj
                ShowcaseViewBean indexShowcaseView = ShowcaseViewTranslator.convertShowcaseToShowcaseView(singleShowcase, langShowcase, false);
                
                // add the showcase view to our array
                topTenShowcases.add(indexShowcaseView);
            }
        }
    }

    public String searchWithKeywords() {
        // redirect to the search view
        return ENavigation.SEARCH + Constants.FACESREDIRECT
                + "searchQuery=" + inputQuery;
    }
    
    public String redirectToShowcase(ShowcaseVideoViewBean videoViewBean) {
        // redirect to correspending showcase
        return ENavigation.DISPLAYSHOWCASE + videoViewBean.getShowcaseID().toString();
    }
    
    public boolean isShowcaseAvailable() {
        return topTenShowcases != null && topTenShowcases.size() > 0;
    }

    public List<ShowcaseViewBean> getTopTenShowcases() {
        return topTenShowcases;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setInputQuery(String inputQuery) {
        this.inputQuery = inputQuery;
    }

    public String getInputQuery() {
        return inputQuery;
    }

    public List<UserBean> getTopUsersList() {
        return topUsersList;
    }

    public List<ShowcaseVideoViewBean> getTopTenVideoShowcases() {
        return topTenVideoShowcases;
    }
}

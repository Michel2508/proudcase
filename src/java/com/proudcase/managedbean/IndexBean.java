package com.proudcase.managedbean;

import com.proudcase.constants.Constants;
import com.proudcase.constants.ENavigation;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.view.IndexShowcaseViewBean;
import com.proudcase.view.ShowcaseVideoViewBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
    transient private ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    transient private UserManager userManager =
            ManagerFactory.createUserManager();
    private List<IndexShowcaseViewBean> topTenShowcases = new ArrayList<>();
    private List<ShowcaseVideoViewBean> topTenVideoShowcases = new ArrayList<>();
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
                ShowcaseTextBean langShowcase = getSpecifiedText(singleShowcase);

                // found something
                if (langShowcase != null) {
                    showcaseVideoViewBean.setShowcaseTitle(langShowcase.getTitle());

                    // let us reduce the amount of characters on the explaintext
                    String temp = langShowcase.getExplaintext();
                    if (temp.length() > Constants.MAXCHARSEXPLAINTEXT) {
                        temp = langShowcase.getExplaintext().
                                substring(0, Constants.MAXCHARSEXPLAINTEXT);
                        
                        // add three dots at the end
                        temp = temp.concat("...");
                    }

                    // remove any html tag
                    temp = temp.replaceAll("<[^>]*>", "");
                    
                    // set the final text
                    showcaseVideoViewBean.setShowcaseText(temp);
                }

                // we should have videos
                if (singleShowcase.getVideoLinks() != null && !singleShowcase.getVideoLinks().isEmpty()) {
                    // save the first video to our object
                    showcaseVideoViewBean.setVideoURL(singleShowcase.getVideoLinks().get(0).getVideolink());
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
            // convert every showcase to our view object
            IndexShowcaseViewBean indexShowcaseView = new IndexShowcaseViewBean();
            indexShowcaseView.setShowcaseID(singleShowcase.getId());

            // check if we can find the text and title in a language that fits to
            // the users language
            ShowcaseTextBean langShowcase = getSpecifiedText(singleShowcase);

            // found something
            if (langShowcase != null) {
                indexShowcaseView.setShowcaseTitle(langShowcase.getTitle());
                indexShowcaseView.setShowcaseText(langShowcase.getExplaintext());
            }

            // do we have pictures for the showcase?
            if (singleShowcase.getImageList() != null && !singleShowcase.getImageList().isEmpty()) {
                // sort the images
                Collections.sort(singleShowcase.getImageList());

                // save the first image (frontimage) to our view object
                indexShowcaseView.setFrontImage(singleShowcase.getImageList().get(0));
            }

            // add the showcase view to our array
            topTenShowcases.add(indexShowcaseView);
        }
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

    public String searchWithKeywords() {
        // redirect to the search view
        return ENavigation.SEARCH + Constants.FACESREDIRECT
                + "searchQuery=" + inputQuery;
    }

    public boolean isShowcaseAvailable() {
        return topTenShowcases != null && topTenShowcases.size() > 0;
    }

    public List<IndexShowcaseViewBean> getTopTenShowcases() {
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

    public void setTopTenVideoShowcases(List<ShowcaseVideoViewBean> topTenVideoShowcases) {
        this.topTenVideoShowcases = topTenVideoShowcases;
    }
}

package com.proudcase.managedbean;

import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.view.IndexShowcaseViewBean;
import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 * Copyright © 16.07.2013 Michel Vocks This file is part of proudcase.
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
 * @Date: 16.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@SessionScoped
public class TopShowcaseBean implements Serializable {
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;

    transient private ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    private IndexShowcaseViewBean topShowcaseView;

    public TopShowcaseBean() {
    }

    @PostConstruct
    public void init() {
        // get the top showcase
        ShowcaseBean topShowcase = showcaseManager.getTopShowcase();

        // nothing found?
        if (topShowcase == null || topShowcase.getId() == null) {
            return;
        }

        // convert showcase to our view object
        topShowcaseView = new IndexShowcaseViewBean();
        topShowcaseView.setShowcaseID(topShowcase.getId());

        // check if we can find the text and title in a language that fits to
        // the users language
        ShowcaseTextBean langShowcase = getSpecifiedText(topShowcase);

        // found something
        if (langShowcase != null) {
            topShowcaseView.setShowcaseTitle(langShowcase.getTitle());
            topShowcaseView.setShowcaseText(langShowcase.getExplaintext());
        }

        // do we have pictures for the showcase?
        if (topShowcase.getImageList() != null && !topShowcase.getImageList().isEmpty()) {
            // sort the images
            Collections.sort(topShowcase.getImageList());
            
            // save the first image (frontimage) to our view object
            topShowcaseView.setFrontImage(topShowcase.getImageList().get(0));
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

    public IndexShowcaseViewBean getTopShowcaseView() {
        return topShowcaseView;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

package com.proudcase.view;

import com.proudcase.constants.Constants;
import com.proudcase.managedbean.SessionBean;
import com.proudcase.mongodb.manager.JuryFeedbackManager;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.persistence.UserBean;
import java.util.*;
import org.bson.types.ObjectId;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Copyright © 12.10.2012 Michel Vocks This file is part of proudcase.
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
 * @Date: 12.10.2012
 *
 * @Encoding: UTF-8
 */
public class LazyJuryBoardModel extends LazyDataModel<IndexShowcaseViewBean> {

    transient private ShowcaseManager showcaseManager;
    transient private JuryFeedbackManager juryFeedbackManager;
    transient private SessionBean sessionBean;
    private UserBean loggedUser;

    public LazyJuryBoardModel(ShowcaseManager showcaseManager, JuryFeedbackManager juryFeedbackManager, SessionBean sessionBean, UserBean loggedUser) {
        this.showcaseManager = showcaseManager;
        this.juryFeedbackManager = juryFeedbackManager;
        this.sessionBean = sessionBean;
        this.loggedUser = loggedUser;
    }

    @Override
    public List<IndexShowcaseViewBean> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        // get a list of all showcases which the user has already rated
        List<ObjectId> alreadyRatedList = juryFeedbackManager.getAlreadyRatedShowcasesByUser(loggedUser);
        
        // total number of unrated showcases
        this.setRowCount((int) showcaseManager.countAllShowcasesNotRatedByUserAndAlreadyRatedList(loggedUser, alreadyRatedList));

        // get the unrated showcases
        List<ShowcaseBean> unratedShowcases = showcaseManager.getPagingAllShowcasesNotRatedByAlreadyRatedList(alreadyRatedList, first, pageSize);

        // our new view list
        List<IndexShowcaseViewBean> showcaseViewList = new ArrayList<>();

        // iterate through all showcases and convert them to a view object
        for (ShowcaseBean singleShowcase : unratedShowcases) {
            // Create a new view object
            IndexShowcaseViewBean viewObject = new IndexShowcaseViewBean();
            viewObject.setShowcaseID(singleShowcase.getId());

            // check if we can find the text and title in a language that fits to
            // the users language
            ShowcaseTextBean langShowcase = getSpecifiedText(singleShowcase);

            // found something
            if (langShowcase != null) {
                viewObject.setShowcaseTitle(langShowcase.getTitle());

                // let us reduce the amount of characters on the explaintext
                String temp = langShowcase.getExplaintext();
                if (temp.length() > Constants.MAXCHARSEXPLAINTEXT) {
                    temp = langShowcase.getExplaintext().
                            substring(0, Constants.MAXCHARSEXPLAINTEXT);
                }

                // remove any html tag
                temp = temp.replaceAll("<[^>]*>", "");

                viewObject.setShowcaseText(temp);
            }

            // do we have pictures for the showcase?
            if (singleShowcase.getImageList() != null && !singleShowcase.getImageList().isEmpty()) {
                // sort the images
                Collections.sort(singleShowcase.getImageList());
                
                // save the first image (frontimage) to our view object
                viewObject.setFrontImage(singleShowcase.getImageList().get(0));
            }

            // add this obj to our list
            showcaseViewList.add(viewObject);
        }

        // return the result
        return showcaseViewList;
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
}

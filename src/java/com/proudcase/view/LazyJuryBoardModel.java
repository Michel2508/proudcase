package com.proudcase.view;

import com.proudcase.managedbean.SessionBean;
import com.proudcase.mongodb.manager.JuryFeedbackManager;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.util.LanguageTranslationUtil;
import com.proudcase.util.ShowcaseViewTranslator;
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
public class LazyJuryBoardModel extends LazyDataModel<ShowcaseViewBean> {

    private final transient ShowcaseManager showcaseManager;
    private final transient JuryFeedbackManager juryFeedbackManager;
    private final transient SessionBean sessionBean;
    private final UserBean loggedUser;

    public LazyJuryBoardModel(ShowcaseManager showcaseManager, JuryFeedbackManager juryFeedbackManager, SessionBean sessionBean, UserBean loggedUser) {
        this.showcaseManager = showcaseManager;
        this.juryFeedbackManager = juryFeedbackManager;
        this.sessionBean = sessionBean;
        this.loggedUser = loggedUser;
    }

    @Override
    public List<ShowcaseViewBean> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        // get a list of all showcases which the user has already rated
        List<ObjectId> alreadyRatedList = juryFeedbackManager.getAlreadyRatedShowcasesByUser(loggedUser);
        
        // total number of unrated showcases
        this.setRowCount((int) showcaseManager.countAllShowcasesNotRatedByUserAndAlreadyRatedList(loggedUser, alreadyRatedList));

        // get the unrated showcases
        List<ShowcaseBean> unratedShowcases = showcaseManager.getPagingAllShowcasesNotRatedByAlreadyRatedList(alreadyRatedList, first, pageSize);

        // our new view list
        List<ShowcaseViewBean> showcaseViewList = new ArrayList<>();

        // iterate through all showcases and convert them to a view object
        for (ShowcaseBean singleShowcase : unratedShowcases) {
            // check if we can find the text and title in a language that fits to
            // the users language
            ShowcaseTextBean langShowcase = LanguageTranslationUtil.getSpecifiedText(singleShowcase, sessionBean.getUserLocale());

            // found something
            if (langShowcase != null) {
                // Convert both objs to one view object
                ShowcaseViewBean viewObject = ShowcaseViewTranslator.convertShowcaseToShowcaseView(singleShowcase, langShowcase, true);
                
                // add this obj to our list
                showcaseViewList.add(viewObject);
            }

        }

        // return the result
        return showcaseViewList;
    }
}

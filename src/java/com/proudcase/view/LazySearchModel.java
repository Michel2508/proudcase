package com.proudcase.view;

import com.proudcase.managedbean.SessionBean;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
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
public class LazySearchModel extends LazyDataModel<ShowcaseViewBean> {

    private final transient ShowcaseManager showcaseManager;
    private final transient SessionBean sessionBean;
    private final String inputSearch;
    private final ObjectId categorie;

    public LazySearchModel(ShowcaseManager showcaseManager, String inputSearch, SessionBean sessionBean, ObjectId categorie) {
        this.showcaseManager = showcaseManager;
        this.inputSearch = inputSearch;
        this.sessionBean = sessionBean;
        this.categorie = categorie;
    }

    @Override
    public List<ShowcaseViewBean> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        // searching with null is not possible
        if (inputSearch == null) {
            return new ArrayList<>();
        }

        // total number of search results
        this.setRowCount((int) showcaseManager.countAllShowcasesByKeywordAndCat(inputSearch, categorie));

        // get the results
        List<ShowcaseBean> searchResults = showcaseManager.getPagingShowcasesByKeywordAndCat(inputSearch, categorie, first, pageSize);

        // our new view list
        List<ShowcaseViewBean> resultViewList = new ArrayList<>();

        // iterate through all results and convert them to a view object
        for (ShowcaseBean singleShowcase : searchResults) {
            // check if we can find the text and title in a language that fits to
            // the users language
            ShowcaseTextBean langShowcase = LanguageTranslationUtil.getSpecifiedText(singleShowcase, sessionBean.getUserLocale());

            // found something
            if (langShowcase != null) {
                // convert it to a view obj
                ShowcaseViewBean viewObject = ShowcaseViewTranslator.convertShowcaseToShowcaseView(singleShowcase, langShowcase, true);
                
                // add to our list
                resultViewList.add(viewObject);
            }
        }

        // return the result
        return resultViewList;
    }
}

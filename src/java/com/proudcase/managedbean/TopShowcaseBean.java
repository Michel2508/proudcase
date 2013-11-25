package com.proudcase.managedbean;

import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.ShowcaseManager;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.util.LanguageTranslationUtil;
import com.proudcase.util.ShowcaseViewTranslator;
import com.proudcase.view.ShowcaseViewBean;
import java.io.Serializable;
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

    private final transient ShowcaseManager showcaseManager =
            ManagerFactory.createShowcaseManager();
    private ShowcaseViewBean topShowcaseView;

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

        // check if we can find the text and title in a language that fits to
        // the users language
        ShowcaseTextBean langShowcase = LanguageTranslationUtil.getSpecifiedText(topShowcase, sessionBean.getUserLocale());

        // found something
        if (langShowcase != null) {
            // Convert two objects to one view obj
            topShowcaseView = ShowcaseViewTranslator.convertShowcaseToShowcaseView(topShowcase, langShowcase, false);
        }
    }
    
    public ShowcaseViewBean getTopShowcaseView() {
        return topShowcaseView;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

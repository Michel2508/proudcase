package com.proudcase.persistence;

import com.google.code.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.Locale;

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
@Embedded
public class ShowcaseTextBean implements Serializable {

    private String title;
    private String explaintext;
    private Locale lang;
    
    public ShowcaseTextBean() {
    }

    public ShowcaseTextBean(String title, String explaintext, Locale lang) {
        this.title = title;
        this.explaintext = explaintext;
        this.lang = lang;
    }

    public ShowcaseTextBean(Locale lang) {
        this.lang = lang;
    }
    
    public String getExplaintext() {
        return explaintext;
    }

    public void setExplaintext(String explaintext) {
        this.explaintext = explaintext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Locale getLang() {
        return lang;
    }

    public void setLang(Locale lang) {
        this.lang = lang;
    }
    
}

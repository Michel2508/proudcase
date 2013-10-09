package com.proudcase.persistence;

import com.google.code.morphia.annotations.Embedded;
import java.io.Serializable;
import java.util.Locale;

/**
  * Copyright © 26.07.2013 Michel Vocks
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
 * @Date: 26.07.2013
 *
 * @Encoding: UTF-8
 */
@Embedded
public class LangCategorieBean implements Serializable, Comparable<LangCategorieBean> {
    
    private String categoriename;
    private Locale language;

    public LangCategorieBean() {
    }

    public LangCategorieBean(String categoriename, Locale language) {
        this.categoriename = categoriename;
        this.language = language;
    }

    public String getCategoriename() {
        return categoriename;
    }

    public void setCategoriename(String categoriename) {
        this.categoriename = categoriename;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }
    
    @Override
    public int compareTo(LangCategorieBean o) {
        if (o == null) {
            return 0;
        } else {
            return this.categoriename.compareTo(o.categoriename);
        }
    }
}

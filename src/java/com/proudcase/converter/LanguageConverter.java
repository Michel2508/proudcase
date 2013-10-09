package com.proudcase.converter;

import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
  * Copyright © 27.08.2013 Michel Vocks
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
 * @Date: 27.08.2013
 *
 * @Encoding: UTF-8
 */
public class LanguageConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String submittedValue) {
        if (submittedValue == null || submittedValue.equals("")) {
            return null;
        } else {
            // return the locale
            return new Locale(submittedValue);
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object locale) {
        if (locale == null) {
            return null;
        } else {
            return ((Locale) locale).getLanguage();
        }
    }

}

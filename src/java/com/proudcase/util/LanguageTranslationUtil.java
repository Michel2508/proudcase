package com.proudcase.util;

import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
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
  
  * @Author: Michel Vocks
  *
  * @Date: 13.10.2013
  *
  * @Encoding: UTF-8
*/
public class LanguageTranslationUtil {
    
    // this method returns the ShowcaseText object
    // for the client language
    public static ShowcaseTextBean getSpecifiedText(ShowcaseBean givenShowcase, Locale userLocale) {
        // now we have to check if this showcase has this language supported
        for (ShowcaseTextBean singleShowcaseText : givenShowcase.getShowcaseTexts()) {
            if (singleShowcaseText.getLang().equals(userLocale)) {
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

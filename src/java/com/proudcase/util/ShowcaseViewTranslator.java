package com.proudcase.util;

import com.proudcase.constants.Constants;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseTextBean;
import com.proudcase.view.ShowcaseViewBean;
import java.util.Collections;

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
 * @Author: Michel Vocks
 *
 * @Date: 25.11.2013
 *
 * @Encoding: UTF-8
 */
public class ShowcaseViewTranslator {

    /**
     * Converts a showcase obj and the corresponding text obj to view obj
     *
     * @param showcaseBean - The Showcase obj
     * @param showcaseTextBean - The corresponding text obj to the showcase
     * @param shortText - If true, the text will be cutted down to a specific
     * number of character (look at Constants.MAXCHARSEXPLAINTEXT)
     * @return - One view obj.
     */
    public static ShowcaseViewBean convertShowcaseToShowcaseView(ShowcaseBean showcaseBean, ShowcaseTextBean showcaseTextBean, boolean shortText) {
        ShowcaseViewBean returnViewObj = new ShowcaseViewBean();

        // set the id
        returnViewObj.setShowcaseID(showcaseBean.getId());

        // Set the title
        returnViewObj.setShowcaseTitle(showcaseTextBean.getTitle());

        // short text true? Cut the text down to a specific number of chars
        String temp = showcaseTextBean.getExplaintext();
        if (shortText) {
            // let us reduce the amount of characters on the explaintext
            if (temp.length() > Constants.MAXCHARSEXPLAINTEXT) {
                temp = showcaseTextBean.getExplaintext().
                        substring(0, Constants.MAXCHARSEXPLAINTEXT);
            }
        }

        // remove any html tag
        temp = temp.replaceAll("<[^>]*>", "");

        // set the explain text
        returnViewObj.setShowcaseText(temp);

        // do we have pictures for the showcase?
        if (showcaseBean.getImageList() != null && !showcaseBean.getImageList().isEmpty()) {
            // sort the images
            Collections.sort(showcaseBean.getImageList());

            // save the first image (frontimage) to our view object
            returnViewObj.setFrontImage(showcaseBean.getImageList().get(0));
        }

        // finally, return the view obj
        return returnViewObj;
    }

}

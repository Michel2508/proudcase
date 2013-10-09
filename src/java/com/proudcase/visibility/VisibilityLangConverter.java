package com.proudcase.visibility;

import java.io.Serializable;

/**
  * Copyright © 24.07.2013 Michel Vocks
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
 * @Date: 24.07.2013
 *
 * @Encoding: UTF-8
 */
public class VisibilityLangConverter implements Serializable {
    
    private static final String ALLVISIBLE = "allvisible";
    private static final String FRIENDSFRIENDSVISIBLE = "friendsfriendsvisible";
    private static final String FRIENDSVISIBLE = "friendsvisible";
    private static final String ONLYMEVISIBLE = "onlymevisible";
    
    public static String convertVisibilityToLangKey(EVisibility visibility) {
        // All visibility
        if (visibility.equals(EVisibility.all)) {
            return ALLVISIBLE;
        }
        
        // Friends of friends visibility
        if (visibility.equals(EVisibility.friendsfriends)) {
            return FRIENDSFRIENDSVISIBLE;
        }
        
        // Friends visibility
        if (visibility.equals(EVisibility.friends)) {
            return FRIENDSVISIBLE;
        }
        
        // Only me visibility
        if (visibility.equals(EVisibility.onlyme)) {
            return ONLYMEVISIBLE;
        }
        
        return null;
    }

}

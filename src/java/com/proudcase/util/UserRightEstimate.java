package com.proudcase.util;

import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.UserBean;
import com.proudcase.visibility.EVisibility;

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
  * @Date: 25.11.2013
  *
  * @Encoding: UTF-8
*/
public class UserRightEstimate {
    
    /**
     * Estimates if the user has rights to watch/enter/see/whatever with the given visibility from the resource.
     * @param requestUser - User / requester of the resource.
     * @param ownerOfResource - Owner of the resource.
     * @param resourceVisibility - Visibility of the resource.
     * @return - True if the user has rights to request for this resource.
     */
    public static boolean userHasRights(UserBean requestUser, UserBean ownerOfResource, EVisibility resourceVisibility) {
        // Check if the resource is open for everyone
        if (resourceVisibility.equals(EVisibility.all)) {
            return true;
        }

        // Empty user objects are at this step not valid
        if (ownerOfResource == null || requestUser == null) {
            return false;
        }
        
        // Requester and owner are the same person
        if (ownerOfResource.equals(requestUser)) {
            return true;
        }
        
        // Check if the resource is open only for the owner
        if (resourceVisibility.equals(EVisibility.onlyme)) {
            return false;
        }
        
        // We need definitively the user manager
        UserManager userManager = ManagerFactory.createUserManager();
        
        // Only available for friends
        if (resourceVisibility.equals(EVisibility.friends)) {
            // check if both are friends
            if (userManager.isFriend(ownerOfResource, requestUser)) {
                return true;
            } 
        }
        
        // Only friends of friends
        if (resourceVisibility.equals(EVisibility.friendsfriends)) {
            // check if a friend is a friend
            if (userManager.isFriendOfFriend(ownerOfResource, requestUser)) {
                return true;
            }
        }
        
        // If we are here, then the requester has no rights
        return false;
    }

}

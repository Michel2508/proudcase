package com.proudcase.mongodb.manager;

import com.proudcase.mongodb.util.MongoUtil;

/**
  * Copyright © 24.09.2012 Michel Vocks
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
 * @Date: 24.09.2012
 *
 * @Encoding: UTF-8
 */
public final class ManagerFactory {
    
    // get instance from singleton
    private static MongoUtil mongoUtilSingleton = 
            MongoUtil.getMongoUtilInst();
    
    // Create usermanager
    public static UserManager createUserManager() {
        return new UserManager(mongoUtilSingleton.getDatastore());
    } 
    
    // Create categoriemanager
    public static CategorieManager createCategorieManager() {
        return new CategorieManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create messagesmanager
    public static MessagesManager createMessagesManager() {
        return new MessagesManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create proudcasemanager
    public static ProudcaseManager createProudcaseManager() {
        return new ProudcaseManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create showcasemanager
    public static ShowcaseManager createShowcaseManager() {
        return new ShowcaseManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create exceptionmanager
    public static ExceptionManager createExceptionManager() {
        return new ExceptionManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create friendinvitationmanager
    public static FriendInvitationManager createFriendInvitationManager() {
        return new FriendInvitationManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create supportedlanguagesmanager
    public static SupportedLanguagesManager createSupportedLanguagesManager() {
        return new SupportedLanguagesManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create imagemanager
    public static ImageManager createImageManager() {
        return new ImageManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create juryfeedbackmanager
    public static JuryFeedbackManager createJuryFeedbackManager() {
        return new JuryFeedbackManager(mongoUtilSingleton.getDatastore());
    }
    
    // Create videolinkmanager
    public static VideoLinkManager createVideoLinkManager() {
        return new VideoLinkManager(mongoUtilSingleton.getDatastore());
    }
}

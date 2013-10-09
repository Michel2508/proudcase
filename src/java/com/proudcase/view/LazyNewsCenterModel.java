package com.proudcase.view;

import com.google.code.morphia.Key;
import com.proudcase.mongodb.manager.ProudcaseManager;
import com.proudcase.mongodb.manager.UserManager;
import com.proudcase.persistence.ProudcaseBean;
import com.proudcase.persistence.UserBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
  * Copyright © 12.10.2012 Michel Vocks
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
 * @Date: 12.10.2012
 *
 * @Encoding: UTF-8
 */
public class LazyNewsCenterModel extends LazyDataModel<ProudcaseBean> {
    
    private int countedNews;
    transient private ProudcaseManager proudcaseManager;
    private List<Key<UserBean>> friendList = new ArrayList<>();
    private List<Key<UserBean>> friendsOfFriendsList = new ArrayList<>();
    private Key<UserBean> selfUser;
    
    public LazyNewsCenterModel(UserBean loggedUser, ProudcaseManager proudcaseManager, UserManager userManager) {
        this.proudcaseManager = proudcaseManager;
        this.selfUser = new Key<>(UserBean.class, loggedUser.getId());
        
         // get the friends list (fresh from the database)
        List<ObjectId> friendListID = userManager.get(loggedUser.getId()).getFriendRelations();
        
        // it could be possible that the list was never set (never added friends)
        if (friendListID == null) {
            friendListID = new ArrayList<>();
        }
        
        // convert the ids to db keys
        for (ObjectId friendID : friendListID) {
            friendList.add(new Key<>(UserBean.class, friendID));
        }
        
        // get all friends from the friends
        for (ObjectId friendID : friendListID) {
            List<ObjectId> friendsOfFriend = userManager.get(friendID).getFriendRelations();
            
            // has no friends so skip this
            if (friendsOfFriend == null || friendsOfFriend.isEmpty()) {
                continue;
            }
            
            // iterate through all friends from this friend
            for (ObjectId friendOfFriendID : friendsOfFriend) {
                // add the db key to our list
                friendsOfFriendsList.add(new Key<>(UserBean.class, friendOfFriendID));
            }
        }
            
        // we save here overall news for paging
        countedNews = (int) proudcaseManager.countAllProudcase(friendList, friendsOfFriendsList, selfUser);
    }
    
    @Override
    public List<ProudcaseBean> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
         // we save here overall news for paging
        countedNews = (int) proudcaseManager.countAllProudcase(friendList, friendsOfFriendsList, selfUser);
        
        // total number of proudcases set
        this.setRowCount(countedNews);
        
        // return the result
        return proudcaseManager.getPagingProudcase(friendList, friendsOfFriendsList, selfUser, first, pageSize);
    }
    
    public int getCountedNews() {
        return countedNews;
    }
}

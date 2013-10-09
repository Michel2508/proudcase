package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.proudcase.persistence.FriendInvitationBean;
import com.proudcase.persistence.UserBean;
import java.util.List;
import org.bson.types.ObjectId;

/**
  * Copyright © 03.10.2012 Michel Vocks
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
 * @Date: 03.10.2012
 *
 * @Encoding: UTF-8
 */
public class FriendInvitationManager extends BasicDAO<FriendInvitationBean, ObjectId> {
    
    public FriendInvitationManager(Datastore datastore) {
        super(FriendInvitationBean.class, datastore);
    }
    
    public FriendInvitationBean getFriendInvitation(UserBean userSendInvitation, UserBean userGotInvitation) {
        // Query
        Query<FriendInvitationBean> query = ds.createQuery(FriendInvitationBean.class)
                .field("userSendInvitation").equal(userSendInvitation);
        query.field("userGotInvitation").equal(userGotInvitation);
        
        // return the invitation
        return query.get();
    }
    
    public List<FriendInvitationBean> getAllFriendInvitationsForUser(UserBean user) {
        // Query
        Query<FriendInvitationBean> query = ds.createQuery(FriendInvitationBean.class)
                .field("userGotInvitation").equal(user);
        
        // return
        return query.asList();
    }

}

package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.proudcase.persistence.ProudcaseBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.visibility.EVisibility;
import java.util.List;
import org.bson.types.ObjectId;

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
public class ProudcaseManager extends BasicDAO<ProudcaseBean, ObjectId> {
    
    public ProudcaseManager(Datastore datastore) {
        super(ProudcaseBean.class, datastore);
    }
    
    public List<ProudcaseBean> getPagingProudcase(List<Key<UserBean>> friendList, List<Key<UserBean>> friendOfFriendList, Key<UserBean> selfUser, int offset, int maxItems) {
        // Query
        Query<ProudcaseBean> query = ds.createQuery(ProudcaseBean.class)
                .offset(offset).order("-createdate").disableValidation();
        
        // has this user friends?
        if (!friendList.isEmpty()) {
            // he has friends so query for proudcases from friends
            query.or(
                // get all proudcases from friends with the right visibility
                query.and(
                        query.criteria("proudcaseOwner").in(friendList),
                        query.criteria("visibility").notEqual(EVisibility.onlyme)
                ),
                
                // query for proudcases from friends of friends
                query.and(
                       query.criteria("proudcaseOwner").in(friendOfFriendList),
                       query.criteria("visibility").equal(EVisibility.friendsfriends)
                                .or(query.criteria("visibility").equal(EVisibility.all))
                ),
                
                // get all proudcases from the owner of the proudcases
                query.and(
                        query.criteria("proudcaseOwner").equal(selfUser)
                )
                
                // get all proudcases which are open for everyone
                /*query.and(
                        query.criteria("visibility").equal(EVisibility.all)
                )*/
            );
        } else {
            // add the rest criteria
            query.or(
                // get all proudcases from the owner of the proudcases
                query.and(
                        query.criteria("proudcaseOwner").equal(selfUser)
                )
                
                // get all proudcases which are open for everyone
                /*query.and(
                        query.criteria("visibility").equal(EVisibility.all)
                )*/
            );
        }
        
        // enable paging
        query.limit(maxItems);

        // return all proudcases with the relationship to the user
        return query.asList();
    }
    
    public long countAllProudcase(List<Key<UserBean>> friendList, List<Key<UserBean>> friendOfFriendList, Key<UserBean> selfUser) {
        // Query
        Query<ProudcaseBean> query = ds.createQuery(ProudcaseBean.class).disableValidation();
        
         // has this user friends?
        if (!friendList.isEmpty()) {
            // he has friends so query for proudcases from friends
            query.or(
                // get all proudcases from friends with the right visibility
                query.and(
                        query.criteria("proudcaseOwner").in(friendList),
                        query.criteria("visibility").notEqual(EVisibility.onlyme)
                ),
                
                // query for proudcases from friends of friends
                query.and(
                       query.criteria("proudcaseOwner").in(friendOfFriendList),
                       query.criteria("visibility").equal(EVisibility.friendsfriends)
                                .or(query.criteria("visibility").equal(EVisibility.all))
                ),
                
                // get all proudcases from the owner of the proudcases
                query.and(
                        query.criteria("proudcaseOwner").equal(selfUser)
                )
                
                // get all proudcases which are open for everyone
                /*query.and(
                        query.criteria("visibility").equal(EVisibility.all)
                )*/
            );
        } else {
            // add the rest criteria
            query.or(
                // get all proudcases from the owner of the proudcases
                query.and(
                        query.criteria("proudcaseOwner").equal(selfUser)
                )
                
                // get all proudcases which are open for everyone
                /*query.and(
                        query.criteria("visibility").equal(EVisibility.all)
                )*/
            );
        }

        // return result
        return query.countAll();
    }
    
    public List<ProudcaseBean> getPagingProudcaseByVisibility(EVisibility visibility, int offset, int maxItems) {
        // Query 
        Query<ProudcaseBean> query = ds.createQuery(ProudcaseBean.class)
                .field("visibility").equal(visibility).offset(offset).order("-createdate");
        
        // enable paging
        query.limit(maxItems);
        
        // return all proudcases as list
        return query.asList();
    }
}

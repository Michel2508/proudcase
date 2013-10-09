package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.proudcase.persistence.JuryFeedbackBean;
import com.proudcase.persistence.UserBean;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

/**
  * Copyright © 24.08.2013 Michel Vocks
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
 * @Date: 24.08.2013
 *
 * @Encoding: UTF-8
 */
public class JuryFeedbackManager extends BasicDAO<JuryFeedbackBean, ObjectId> {

    public JuryFeedbackManager(Datastore ds) {
        super(JuryFeedbackBean.class, ds);
    }
    
    public List<ObjectId> getAlreadyRatedShowcasesByUser(UserBean user) {
        // Query 
        Query<JuryFeedbackBean> query = ds.createQuery(JuryFeedbackBean.class)
                .field("feedbackOwner").equal(user);
        
        // get all feedbacks
        List<JuryFeedbackBean> allFeedbacksByUserList = query.asList();

        // create a new key list
        List<ObjectId> keyList = new ArrayList<>();
        
        // iterate through all feedbacks
        for (JuryFeedbackBean feedback : allFeedbacksByUserList) {
            // add every key to the list
            keyList.add(feedback.getShowcaseID());
        }
        
        // return list
        return keyList;
    }
}

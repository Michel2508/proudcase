package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.proudcase.persistence.JuryFeedbackBean;
import com.proudcase.persistence.ShowcaseBean;
import com.proudcase.persistence.ShowcaseRankingBean;
import com.proudcase.persistence.UserBean;
import com.proudcase.visibility.EVisibility;
import java.util.List;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;

/**
 * Copyright © 24.09.2012 Michel Vocks This file is part of proudcase.
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
 * /
 *
 **
 * @Author: Michel Vocks
 *
 * @Date: 24.09.2012
 *
 * @Encoding: UTF-8
 */
public class ShowcaseManager extends BasicDAO<ShowcaseBean, ObjectId> {

    public ShowcaseManager(Datastore datastore) {
        super(ShowcaseBean.class, datastore);
    }

    public List<ShowcaseBean> getPagingShowcasesByKeywordAndCat(String inputSearch, ObjectId categorie, int offset, int pageSize) {
        // remove the first and last space
        inputSearch = inputSearch.trim();

        // add the star operator (like search)
        inputSearch += ".*";

        // create the pattern
        Pattern regex = Pattern.compile(inputSearch, Pattern.CASE_INSENSITIVE);

        // inititate the query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("visibility").equal(EVisibility.all).field("showcasepublic").equal(true);

        // query for specific criteria
        query.or(
                query.criteria("showcaseTexts.title").equal(regex));

        // we got a categorie id?
        if (categorie != null) {
            // filter categorie
            query.field("categorieid").equal(categorie);
        }

        // max results
        query.limit(pageSize);

        // offset
        query.offset(offset);

        // return the result
        return query.asList();
    }

    public long countAllShowcasesByKeywordAndCat(String inputSearch, ObjectId categorie) {
        // remove the first and last space
        inputSearch = inputSearch.trim();

        // add the star operator (like search)
        inputSearch += ".*";

        // create the pattern
        Pattern regex = Pattern.compile(inputSearch, Pattern.CASE_INSENSITIVE);

        // inititate the query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("visibility").equal(EVisibility.all).field("showcasepublic").equal(true);

        // query for specific criteria
        query.or(
                query.criteria("showcaseTexts.title").equal(regex));

        // we got a categorie id?
        if (categorie != null) {
            // filter categorie
            query.field("categorieid").equal(categorie);
        }

        // return result
        return query.countAll();
    }

    public List<ShowcaseBean> getTopTenShowcases() {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("visibility").equal(EVisibility.all).field("showcasepublic").equal(true).
                order("-showcaseRankings.ranking");

        // Only 10 results are required
        query.limit(10);

        // retrieve 
        List<ShowcaseBean> topTenShowcases = query
                .asList();

        // return as list
        return topTenShowcases;
    }
    
    public List<ShowcaseBean> getTopTenVideoShowcases() {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("visibility").equal(EVisibility.all).field("showcasepublic").equal(true)
                .order("-showcaseRankings.ranking");
        
        // we only want showcases which have videos
        query.field("videoLinks").exists();
        
        // only 10 results are required
        query.limit(10);
        
        // get
        List<ShowcaseBean> topTenVideoShowcases = query.asList();
        
        // return as list
        return topTenVideoShowcases;
    }

    public ShowcaseBean getTopShowcase() {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("visibility").equal(EVisibility.all).field("showcasepublic").equal(true).
                order("-showcaseRankings.ranking");

        // return only the first one
        return query.get();
    }

    public ShowcaseRankingBean getShowcaseRankingByUser(ObjectId showcaseId, UserBean user) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field(Mapper.ID_KEY).equal(showcaseId);
        query.retrievedFields(true, "showcaseRankings");

        // retrieve
        List<ShowcaseRankingBean> showcaseRankingList = query
                .get().getShowcaseRankings();

        // empty ratings
        if (showcaseRankingList == null || showcaseRankingList.isEmpty()) {
            return null;
        }

        // iterate through all ratings
        for (ShowcaseRankingBean showcaseRankingBean : showcaseRankingList) {
            if (showcaseRankingBean.getUser().equals(user.getId())) {
                return showcaseRankingBean;
            }
        }

        return null;
    }

    public boolean updateOrInsertRanking(ObjectId showcaseId, ShowcaseRankingBean showcaseRankingBean) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field(Mapper.ID_KEY).equal(showcaseId);
        query.field("showcaseRankings.user").equal(showcaseRankingBean.getUser());

        // prepare the updateoperations
        UpdateOperations<ShowcaseBean> ops;
        UpdateResults<ShowcaseBean> updateResult;

        // do we found a bison document?
        if (query.countAll() > 0) {
            // then we want to update it
            ops = ds.createUpdateOperations(ShowcaseBean.class)
                    .disableValidation()
                    .set("showcaseRankings.$", showcaseRankingBean)
                    .enableValidation();
            updateResult = ds.updateFirst(query, ops);
        } else {
            // then we want to insert it
            Query<ShowcaseBean> getShowcase = ds.createQuery(ShowcaseBean.class)
                    .field(Mapper.ID_KEY).equal(showcaseId);
            ops = ds.createUpdateOperations(ShowcaseBean.class)
                    .add("showcaseRankings", showcaseRankingBean);
            updateResult = ds.updateFirst(getShowcase, ops);
        }

        // return true if success
        return (updateResult.getInsertedCount() > 0 || updateResult.getUpdatedCount() > 0);
    }

    public List<ShowcaseBean> getNewShowcasesByLimit(int limit) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("visibility").equal(EVisibility.all).field("showcasepublic").equal(true).
                order("-createdate").limit(limit);

        // return list
        return query.asList();
    }
    
    public List<ShowcaseBean> getAllShowcasesByUser(UserBean user) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("userAccount").equal(user);

        // return as list
        return query.asList();
    }

    public List<ShowcaseBean> getAllShowcasesNotRatedByUserAndAlreadyRatedList(UserBean user, List<ObjectId> alreadyRatedList) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field(Mapper.ID_KEY).notIn(alreadyRatedList).field("showcasepublic").equal(true);

        // return as list
        return query.asList();
    }

    public List<ShowcaseBean> getPagingAllShowcasesNotRatedByAlreadyRatedList(List<ObjectId> alreadyRatedList, int offset, int pageSize) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field("showcasepublic").equal(true);

        // if not empty then filter!
        if (!alreadyRatedList.isEmpty()) {
            query.field(Mapper.ID_KEY).notIn(alreadyRatedList);
        }

        // limit
        query.limit(pageSize);

        // offset
        query.offset(offset);

        // return as list
        return query.asList();
    }

    public long countAllShowcasesNotRatedByUserAndAlreadyRatedList(UserBean user, List<ObjectId> alreadyRatedList) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class).field("showcasepublic").equal(true);
               
        // if not empty then filter!
        if (!alreadyRatedList.isEmpty()) {
            query.field(Mapper.ID_KEY).notIn(alreadyRatedList);
        }
        
        // return number
        return query.countAll();
    }

    public void addFeedbackToShowcase(ObjectId showcaseID, JuryFeedbackBean juryFeedback) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field(Mapper.ID_KEY).equal(showcaseID);

        // prepare the updateoperations
        UpdateOperations<ShowcaseBean> ops = ds.createUpdateOperations(ShowcaseBean.class)
                .add("juryFeedbackList", juryFeedback);

        // execute opdate operation
        ds.update(query, ops);
    }
    
    public void increaseVisitorCounter(ObjectId showcaseID) {
        // Query
        Query<ShowcaseBean> query = ds.createQuery(ShowcaseBean.class)
                .field(Mapper.ID_KEY).equal(showcaseID);
        
        // increase
        UpdateOperations<ShowcaseBean> ops = ds.createUpdateOperations(ShowcaseBean.class)
                .inc("visitorCounter");
        
        // execute
        ds.update(query, ops);
    }
}

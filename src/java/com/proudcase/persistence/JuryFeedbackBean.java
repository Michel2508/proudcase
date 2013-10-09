package com.proudcase.persistence;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PrePersist;
import com.google.code.morphia.annotations.Reference;
import java.io.Serializable;
import java.util.Date;
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
@Entity
public class JuryFeedbackBean implements Serializable {
    
    @Id
    private ObjectId id;
    
    private ObjectId showcaseID;
    
    @Reference
    private UserBean feedbackOwner;
    private String feedback;
    private int rating;
    private Date feedbackDate;

    public JuryFeedbackBean() {
    }

    public JuryFeedbackBean(ObjectId id, ObjectId showcaseID, UserBean feedbackOwner, String feedback, int rating, Date feedbackDate) {
        this.id = id;
        this.showcaseID = showcaseID;
        this.feedbackOwner = feedbackOwner;
        this.feedback = feedback;
        this.rating = rating;
        this.feedbackDate = feedbackDate;
    }
    
    @PrePersist
    void prePersist() {
        feedbackDate = new Date();
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Date getFeedbackDate() {
        return feedbackDate;
    }

    public void setFeedbackDate(Date feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public UserBean getFeedbackOwner() {
        return feedbackOwner;
    }

    public void setFeedbackOwner(UserBean feedbackOwner) {
        this.feedbackOwner = feedbackOwner;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public ObjectId getShowcaseID() {
        return showcaseID;
    }

    public void setShowcaseID(ObjectId showcaseID) {
        this.showcaseID = showcaseID;
    }
}

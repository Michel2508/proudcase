package com.proudcase.persistence;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.PrePersist;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

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

/**
 * @Author: Michel Vocks
 *
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
@Embedded
public class ShowcaseRankingBean implements Serializable {
    
    private ObjectId user;
    private int ranking;
    private Date rankingDate;

    public ShowcaseRankingBean() {
    }

    public ShowcaseRankingBean(ObjectId user, int ranking) {
        this.user = user;
        this.ranking = ranking;
    }
    
    @PrePersist
    void prePersist() {
        rankingDate = new Date();
    }

    public ObjectId getUser() {
        return user;
    }

    public void setUser(ObjectId user) {
        this.user = user;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Date getRankingDate() {
        return rankingDate;
    }

    public void setRankingDate(Date rankingDate) {
        this.rankingDate = rankingDate;
    }
}

package com.proudcase.persistence;

import com.google.code.morphia.annotations.*;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

/**
  * Copyright © 01.10.2012 Michel Vocks
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
 * @Date: 01.10.2012
 *
 * @Encoding: UTF-8
 */
@Entity
public class ProudcaseBean implements Serializable, Comparable<ProudcaseBean> {
    
    @Id
    private ObjectId id;
    
    @Reference
    @Indexed
    private UserBean proudcaseOwner;
    
    @Reference
    private ImageBean proudcaseImage;
    
    private String explaintext;
    private Date createdate;
    private EVisibility visibility;

    public ProudcaseBean() {
        this.createdate = new Date();
        this.visibility = EVisibility.friendsfriends;
    }
    
    public ProudcaseBean(ObjectId id, UserBean proudcaseOwner, ImageBean proudcaseImage, String explaintext, Date createdate, EVisibility visibility) {
        this.id = id;
        this.proudcaseOwner = proudcaseOwner;
        this.proudcaseImage = proudcaseImage;
        this.explaintext = explaintext;
        this.createdate = createdate;
        this.visibility = visibility;
    }

    public ImageBean getProudcaseImage() {
        return proudcaseImage;
    }

    public void setProudcaseImage(ImageBean proudcaseImage) {
        this.proudcaseImage = proudcaseImage;
    }

    public UserBean getProudcaseOwner() {
        return proudcaseOwner;
    }

    public void setProudcaseOwner(UserBean proudcaseOwner) {
        this.proudcaseOwner = proudcaseOwner;
    }

    public String getExplaintext() {
        return explaintext;
    }

    public void setExplaintext(String explaintext) {
        this.explaintext = explaintext;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public EVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(EVisibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public int compareTo(ProudcaseBean o) {
        if (o == null || createdate == null) {
            return 0;
        } else if (createdate.after(o.getCreatedate())) {
            return -1;
        } else if (createdate.before(o.getCreatedate())) {
            return 1;
        } else {
            return 0;
        }
    }
}
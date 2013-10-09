package com.proudcase.view;

import com.proudcase.persistence.ImageBean;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

/**
  * Copyright © 24.07.2013 Michel Vocks
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
 * @Date: 24.07.2013
 *
 * @Encoding: UTF-8
 */
public class ShowcasemanagerViewBean implements Serializable {
    
    private ObjectId showcaseId;
    private String showcasetitle;
    private ImageBean frontImage;
    private Date createdate;
    private String visibility;
    private String availableLangs;
    private long showcaseRating;

    public ShowcasemanagerViewBean() {
    }

    public ShowcasemanagerViewBean(ObjectId showcaseId, String showcasetitle, ImageBean frontImage, Date createdate, String visibility, String availableLangs, long showcaseRating) {
        this.showcaseId = showcaseId;
        this.showcasetitle = showcasetitle;
        this.frontImage = frontImage;
        this.createdate = createdate;
        this.visibility = visibility;
        this.availableLangs = availableLangs;
        this.showcaseRating = showcaseRating;
    }

    public String getAvailableLangs() {
        return availableLangs;
    }

    public void setAvailableLangs(String availableLangs) {
        this.availableLangs = availableLangs;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public ImageBean getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(ImageBean frontImage) {
        this.frontImage = frontImage;
    }

    public ObjectId getShowcaseId() {
        return showcaseId;
    }

    public void setShowcaseId(ObjectId showcaseId) {
        this.showcaseId = showcaseId;
    }

    public long getShowcaseRating() {
        return showcaseRating;
    }

    public void setShowcaseRating(long showcaseRating) {
        this.showcaseRating = showcaseRating;
    }

    public String getShowcasetitle() {
        return showcasetitle;
    }

    public void setShowcasetitle(String showcasetitle) {
        this.showcasetitle = showcasetitle;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

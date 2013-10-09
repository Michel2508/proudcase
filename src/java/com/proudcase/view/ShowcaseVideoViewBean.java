package com.proudcase.view;

import com.proudcase.persistence.ImageBean;
import java.io.Serializable;
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
  
  * @Author: Michel Vocks
  *
  * @Date: 07.10.2013
  *
  * @Encoding: UTF-8
*/
public class ShowcaseVideoViewBean implements Serializable {
    
    private ObjectId showcaseID;
    private String showcaseTitle;
    private String showcaseText;
    private String videoURL;
    private ImageBean ownerAvatar;

    public ShowcaseVideoViewBean() {
    }

    public ShowcaseVideoViewBean(ObjectId showcaseID, String showcaseTitle, String showcaseText, String videoURL, ImageBean ownerAvatar) {
        this.showcaseID = showcaseID;
        this.showcaseTitle = showcaseTitle;
        this.showcaseText = showcaseText;
        this.videoURL = videoURL;
        this.ownerAvatar = ownerAvatar;
    }

    public ObjectId getShowcaseID() {
        return showcaseID;
    }

    public void setShowcaseID(ObjectId showcaseID) {
        this.showcaseID = showcaseID;
    }

    public String getShowcaseTitle() {
        return showcaseTitle;
    }

    public void setShowcaseTitle(String showcaseTitle) {
        this.showcaseTitle = showcaseTitle;
    }

    public String getShowcaseText() {
        return showcaseText;
    }

    public void setShowcaseText(String showcaseText) {
        this.showcaseText = showcaseText;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public ImageBean getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(ImageBean ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }
}

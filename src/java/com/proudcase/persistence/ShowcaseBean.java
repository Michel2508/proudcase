package com.proudcase.persistence;

import com.google.code.morphia.annotations.*;
import com.proudcase.visibility.EVisibility;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
@Entity
public class ShowcaseBean implements Serializable {
    
    @Id
    private ObjectId id;
    
    @Indexed
    private ObjectId categorieid;
    
    private EVisibility visibility;
    private Date createdate;
    private Date lastedited;
    private boolean showcasepublic;
    private boolean approved = false;
    
    @Reference
    private UserBean userAccount;
    
    @Reference
    private List<ImageBean> imageList;
    
    @Reference
    private List<JuryFeedbackBean> juryFeedbackList;
    
    @Embedded
    private List<ShowcaseRankingBean> showcaseRankings;
    
    @Embedded
    private List<ShowcaseTextBean> showcaseTexts;
    
    @Reference
    private List<VideoLinkBean> videoLinks;
    
    @Reference
    private List<FileBean> fileList;
    
    public ShowcaseBean() {
    }

    public ShowcaseBean(ObjectId id, ObjectId categorieid, EVisibility visibility, Date createdate, Date lastedited, boolean showcasepublic, UserBean userAccount, List<ImageBean> imageList, List<JuryFeedbackBean> juryFeedbackList, List<ShowcaseRankingBean> showcaseRankings, List<ShowcaseTextBean> showcaseTexts, List<VideoLinkBean> videoLinks) {
        this.id = id;
        this.categorieid = categorieid;
        this.visibility = visibility;
        this.createdate = createdate;
        this.lastedited = lastedited;
        this.showcasepublic = showcasepublic;
        this.userAccount = userAccount;
        this.imageList = imageList;
        this.juryFeedbackList = juryFeedbackList;
        this.showcaseRankings = showcaseRankings;
        this.showcaseTexts = showcaseTexts;
        this.videoLinks = videoLinks;
    }

    @PrePersist
    void prePersist() {
        // update last edited
        lastedited = new Date();
    }

    public List<ImageBean> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageBean> imageList) {
        this.imageList = imageList;
    }

    public EVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(EVisibility visibility) {
        this.visibility = visibility;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public ObjectId getCategorieid() {
        return categorieid;
    }

    public void setCategorieid(ObjectId categorieid) {
        this.categorieid = categorieid;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UserBean getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserBean userAccount) {
        this.userAccount = userAccount;
    }

    public List<ShowcaseRankingBean> getShowcaseRankings() {
        return showcaseRankings;
    }

    public void setShowcaseRankings(List<ShowcaseRankingBean> showcaseRankings) {
        this.showcaseRankings = showcaseRankings;
    }

    public List<ShowcaseTextBean> getShowcaseTexts() {
        return showcaseTexts;
    }

    public void setShowcaseTexts(List<ShowcaseTextBean> showcaseTexts) {
        this.showcaseTexts = showcaseTexts;
    }

    public List<VideoLinkBean> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(List<VideoLinkBean> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public boolean isShowcasepublic() {
        return showcasepublic;
    }

    public void setShowcasepublic(boolean showcasepublic) {
        this.showcasepublic = showcasepublic;
    }

    public Date getLastedited() {
        return lastedited;
    }

    public void setLastedited(Date lastedited) {
        this.lastedited = lastedited;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<JuryFeedbackBean> getJuryFeedbackList() {
        return juryFeedbackList;
    }

    public void setJuryFeedbackList(List<JuryFeedbackBean> juryFeedbackList) {
        this.juryFeedbackList = juryFeedbackList;
    }

    public List<FileBean> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileBean> fileList) {
        this.fileList = fileList;
    }
}
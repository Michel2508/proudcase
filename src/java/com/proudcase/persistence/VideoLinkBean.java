package com.proudcase.persistence;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.proudcase.constants.EVideoTyp;
import com.proudcase.util.VideoUtil;
import com.proudcase.util.YouTubeUtil;
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

/**
 * @Author: Michel Vocks
 *
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
@Entity
public class VideoLinkBean implements Serializable {
    
    @Id
    private ObjectId id;
    
    private String videolink;
    private ImageBean thumbnail;
    private String youtubeID;
    private EVideoTyp videoTyp;

    public VideoLinkBean() {
    }

    public VideoLinkBean(ObjectId id, String videolink) {
        this.id = id;
        this.videolink = videolink;
    }

    public VideoLinkBean(String videolink) {
        this.videolink = videolink;
    }

    public VideoLinkBean(ObjectId id, String videolink, ImageBean thumbnail) {
        this.id = id;
        this.videolink = videolink;
        this.thumbnail = thumbnail;
    }

    public VideoLinkBean(ObjectId id, String videolink, ImageBean thumbnail, EVideoTyp videoTyp) {
        this.id = id;
        this.videolink = videolink;
        this.thumbnail = thumbnail;
        this.videoTyp = videoTyp;
    }

    public VideoLinkBean(ObjectId id, String videolink, ImageBean thumbnail, String youtubeID, EVideoTyp videoTyp) {
        this.id = id;
        this.videolink = videolink;
        this.thumbnail = thumbnail;
        this.youtubeID = youtubeID;
        this.videoTyp = videoTyp;
    }
    
    public String getVideolinkWithAutoStart() {
        // no typ defined?
        if (videoTyp != null) {
            // just return default video link
            return getVideolink();
        }
        
        // determine typ
        if (videoTyp.equals(EVideoTyp.YOUTUBEVIDEO)) {
            // youtube needs the autoplay tag
            return YouTubeUtil.getYouTubeLinkWithAutoplay(this);
        } else {
            // video is self hosted. So add autostart tag
            return VideoUtil.getVideoURLWithAutostart(videolink);
        }
    }
    
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getVideolink() {
        return videolink;
    }

    public void setVideolink(String videolink) {
        this.videolink = videolink;
    }

    public ImageBean getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageBean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public EVideoTyp getVideoTyp() {
        return videoTyp;
    }

    public void setVideoTyp(EVideoTyp videoTyp) {
        this.videoTyp = videoTyp;
    }

    public String getYoutubeID() {
        return youtubeID;
    }

    public void setYoutubeID(String youtubeID) {
        this.youtubeID = youtubeID;
    }
}

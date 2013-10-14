package com.proudcase.managedbean;

import com.proudcase.constants.EVideoTyp;
import com.proudcase.persistence.VideoLinkBean;
import com.proudcase.util.YouTubeUtil;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

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
  * @Date: 14.10.2013
  *
  * @Encoding: UTF-8
*/
@ManagedBean
@RequestScoped
public class VideoHelperBean implements Serializable {
    
    public boolean isYoutubeVideo(VideoLinkBean videoLink) {
        if (videoLink.getVideoTyp().equals(EVideoTyp.YOUTUBEVIDEO)) {
            return true;
        }
        return false;
    }
    
    public boolean isSelfHostedVideo(VideoLinkBean videoLink) {
        if (videoLink.getVideoTyp().equals(EVideoTyp.SELFHOSTEDVIDEO)) {
            return true;
        }
        return false;
    }
    
    public String getYoutubeURL(VideoLinkBean videoLink) {
        // if it's really a youtube video
        if (videoLink.getVideoTyp().equals(EVideoTyp.YOUTUBEVIDEO)) {
            // return the youtube video url
            return YouTubeUtil.convertYouTubeLink(videoLink);
        }
        return null;
    }
    
    public String getYoutubeThumbnailURL(VideoLinkBean videoLink) {
        // if it's really a youtube video
        if (videoLink.getVideoTyp().equals(EVideoTyp.YOUTUBEVIDEO)) {
            // return the thumbnail url
            return YouTubeUtil.getYouTubeThumbnailLink(videoLink);
        }
        return null;
    }

}

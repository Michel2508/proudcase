package com.proudcase.persistence;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.PrePersist;
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
public class ImageBean implements Serializable, Comparable<ImageBean> {
    
    @Id
    private ObjectId id;
    
    private ObjectId ownerOfImage;
    
    @Indexed
    private String relativeimagepath;
    private String imageName;
    private Date savedate;
    private Integer orderNumber;
    private EVisibility securityRule;
    
    private Integer width;
    private Integer height;

    public ImageBean() {
        this.relativeimagepath = new String();
        this.imageName = "Mr. Anonym";
    }

    public ImageBean(String relativeimagepath, String imageName, Integer orderNumber) {
        this.relativeimagepath = relativeimagepath;
        this.imageName = imageName;
        this.orderNumber = orderNumber;
    }

    public ImageBean(String relativeimagepath, String imageName) {
        this.relativeimagepath = relativeimagepath;
        this.imageName = imageName;
    }

    public ImageBean(String relativeimagepath, String imageName, Date savedate, Integer orderNumber, Integer width, Integer height) {
        this.relativeimagepath = relativeimagepath;
        this.imageName = imageName;
        this.savedate = savedate;
        this.orderNumber = orderNumber;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int compareTo(ImageBean o) {
        if (o == null || o.getOrderNumber() == null || orderNumber == null) {
            return 0;
        } else if (orderNumber < o.getOrderNumber()) {
            return -1;
        } else if (orderNumber > o.getOrderNumber()) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @PrePersist
    void prePersist() {
        // set save date
        savedate = new Date();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getOwnerOfImage() {
        return ownerOfImage;
    }

    public void setOwnerOfImage(ObjectId ownerOfImage) {
        this.ownerOfImage = ownerOfImage;
    }

    public String getRelativeimagepath() {
        return relativeimagepath;
    }

    public void setRelativeimagepath(String relativeimagepath) {
        this.relativeimagepath = relativeimagepath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public EVisibility getSecurityRule() {
        return securityRule;
    }

    public void setSecurityRule(EVisibility securityRule) {
        this.securityRule = securityRule;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Date getSavedate() {
        return savedate;
    }

    public void setSavedate(Date savedate) {
        this.savedate = savedate;
    }
}

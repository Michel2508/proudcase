package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.proudcase.persistence.ImageBean;
import org.bson.types.ObjectId;

/**
  * Copyright © 12.08.2013 Michel Vocks
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
 * @Date: 12.08.2013
 *
 * @Encoding: UTF-8
 */
public class ImageManager extends BasicDAO<ImageBean, ObjectId> {
    
    public ImageManager(Datastore datastore) {
        super(ImageBean.class, datastore);
    }
    
    public ImageBean getImageByRelativeImagePath(String relativeImagePath) {
        // Query 
        Query<ImageBean> query = ds.createQuery(ImageBean.class).field("relativeimagepath")
                .contains(relativeImagePath);
        
        // return
        return query.get();
    }
    
}

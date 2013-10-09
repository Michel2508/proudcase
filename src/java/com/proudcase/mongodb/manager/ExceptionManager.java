package com.proudcase.mongodb.manager;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.proudcase.persistence.ExceptionBean;
import org.bson.types.ObjectId;

/**
  * Copyright © 24.09.2012 Michel Vocks
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
 * @Date: 24.09.2012
 *
 * @Encoding: UTF-8
 */
public class ExceptionManager extends BasicDAO<ExceptionBean, ObjectId> {
    
    public ExceptionManager(Datastore datastore) {
        super(ExceptionBean.class, datastore);
    }

}

package com.proudcase.mongodb.util;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.proudcase.exclogger.ExceptionLogger;

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
public class MongoUtil {
    
    // Connection informations
    private static final String CONNECTION_STRING = "localhost";
    private static final int CONNECTION_PORT = 27017;
    private static final String DATABASE_NAME = "proudcase";
    
    // mongo instance
    private static MongoClient mongoConnection = null;
    
    // morphia
    private static Morphia morphiaSingleton = null;
    
    // datastore
    private static Datastore dataStoreSingleton = null;
    
    // mongoutil instance
    private static final MongoUtil mongoUtil = new MongoUtil();

    private MongoUtil() {
        // extra check if mongoconnection and morphia is really null
        if (mongoConnection == null && morphiaSingleton == null) {
            try {
                // create new connection pool
                mongoConnection = new MongoClient(CONNECTION_STRING, CONNECTION_PORT);

                // set the write concern 
                // Currently, I set the writeconcern to none because
                // I see no really requirement for my application
                mongoConnection.setWriteConcern(WriteConcern.NONE);

                // create the morphia instance & mapping
                morphiaSingleton = new Morphia();
                morphiaSingleton.mapPackage("com.proudcase.persistence");

                // create the datastore
                dataStoreSingleton = morphiaSingleton.createDatastore(
                        mongoConnection, DATABASE_NAME);

                // ensure indexes and capped collections
                dataStoreSingleton.ensureCaps();
                dataStoreSingleton.ensureIndexes();
            } catch (Exception ex) {
                // throw an exception
                try {
                    throw new ExceptionLogger(ex, "error while mongo connection startup!");
                } catch (ExceptionLogger ex2) {
                }
            }
        }
    }
    
    // get util instance
    public static MongoUtil getMongoUtilInst() {
        return mongoUtil;
    }

    // get datastore
    public Datastore getDatastore() {
        return dataStoreSingleton;
    }

}

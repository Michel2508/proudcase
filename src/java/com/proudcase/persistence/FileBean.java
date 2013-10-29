package com.proudcase.persistence;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import org.bson.types.ObjectId;

/**
  * Copyright © 29.10.2013 Michel Vocks
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
  
  * @Author: Michel Vocks (michelvocks@gmail.com)
  *
  * @Date: 29.10.2013
  *
  * @Encoding: UTF-8
*/
@Entity
public class FileBean implements Serializable {
    
    @Id
    private ObjectId id;
    
    private String relativeFilePath;
    private String fileName;
    private long fileSize;
    private String fileFolderID;

    public FileBean() {
    }

    public FileBean(ObjectId id, String relativeFilePath, String fileName, long fileSize, String fileFolderID) {
        this.id = id;
        this.relativeFilePath = relativeFilePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileFolderID = fileFolderID;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getRelativeFilePath() {
        return relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileFolderID() {
        return fileFolderID;
    }

    public void setFileFolderID(String fileFolderID) {
        this.fileFolderID = fileFolderID;
    }
}

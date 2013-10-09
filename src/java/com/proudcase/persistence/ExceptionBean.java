package com.proudcase.persistence;

import com.google.code.morphia.annotations.CappedAt;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import java.util.Date;
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
@Entity(cap = @CappedAt(157286400)) // capped  collection 150mb
public class ExceptionBean implements Serializable {
    
    @Id
    private ObjectId id;
    
    private String exception;
    private String custommessage;
    private Date throwtime;
    private String classname;
    private String methodname;
    private int line;

    public ExceptionBean() {
    }

    public ExceptionBean(ObjectId id, String exception, String custommessage, Date throwtime, String classname, String methodname, int line) {
        this.id = id;
        this.exception = exception;
        this.custommessage = custommessage;
        this.throwtime = throwtime;
        this.classname = classname;
        this.methodname = methodname;
        this.line = line;
    }
    
    @Override
    public String toString() {
        String returnString = "Exception: " + exception + "\n"
                    + "Custom Message: " + custommessage + "\n"
                    + "Time: " + throwtime.toString() + "\n"
                    + "Class: " + classname + "\n"
                    + "Method: " + methodname + "\n"
                    + "Line: " + line;
        return returnString;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getCustommessage() {
        return custommessage;
    }

    public void setCustommessage(String custommessage) {
        this.custommessage = custommessage;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getMethodname() {
        return methodname;
    }

    public void setMethodname(String methodname) {
        this.methodname = methodname;
    }

    public Date getThrowtime() {
        return throwtime;
    }

    public void setThrowtime(Date throwtime) {
        this.throwtime = throwtime;
    }
}

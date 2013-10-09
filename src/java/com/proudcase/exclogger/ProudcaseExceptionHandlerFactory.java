package com.proudcase.exclogger;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
  * Copyright © 20.07.2013 Michel Vocks
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
 * @Date: 20.07.2013
 *
 * @Encoding: UTF-8
 */
public class ProudcaseExceptionHandlerFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    public ProudcaseExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }
    
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = new ProudcaseExceptionHandler(parent.getExceptionHandler());
        return handler;
    }
    

}

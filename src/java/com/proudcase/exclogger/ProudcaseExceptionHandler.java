package com.proudcase.exclogger;

import com.proudcase.constants.ENavigation;
import com.proudcase.filehandling.PropertyReader;
import java.util.Iterator;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * Copyright © 20.07.2013 Michel Vocks This file is part of proudcase.
 *
 * proudcase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * proudcase is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * proudcase. If not, see <http://www.gnu.org/licenses/>.
 *
 * /
 *
 **
 * @Author: Michel Vocks
 *
 * @Date: 20.07.2013
 *
 * @Encoding: UTF-8
 */
public class ProudcaseExceptionHandler extends ExceptionHandlerWrapper {

    private final ExceptionHandler wrapper;

    public ProudcaseExceptionHandler(ExceptionHandler wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapper;
    }

    @Override
    public void handle() throws FacesException {
        // iterate through all queued exceptions
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            // Get the next and the source
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context =
                    (ExceptionQueuedEventContext) event.getSource();

            // get the real exception
            Throwable t = context.getException();
            t.printStackTrace();
            // we need this for navigation
            final FacesContext fc = FacesContext.getCurrentInstance();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();
            
            try {
                String outputMessage = "";
                
                // view expired?
                if (t instanceof ViewExpiredException) {
                    outputMessage = PropertyReader.getMessageResourceString(fc.getApplication().
                    getMessageBundle(), "error_viewexpired", null, fc.getViewRoot().getLocale());
                    
                    // add the error message
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", outputMessage));
                } 
                
                // navigate to error page
                nav.handleNavigation(fc, null, ENavigation.ERROR.toString());
                fc.renderResponse();
            } finally {
                //remove it from queue
                i.remove();
            }
        }
        getWrapped().handle();
    }
}

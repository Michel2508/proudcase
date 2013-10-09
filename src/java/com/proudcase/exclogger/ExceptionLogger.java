package com.proudcase.exclogger;

import com.proudcase.mail.SendMail;
import com.proudcase.mongodb.manager.ExceptionManager;
import com.proudcase.mongodb.manager.ManagerFactory;
import com.proudcase.persistence.ExceptionBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Copyright © 17.07.2013 Michel Vocks This file is part of proudcase.
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
 * @Date: 17.07.2013
 *
 * @Encoding: UTF-8
 */
public class ExceptionLogger extends Exception {

    private static final String DESTMAIL = "wow.mail90@gmx.de";
    private static List<Exception> threwedExceptions =
            Collections.synchronizedList(new ArrayList<Exception>());

    public ExceptionLogger(Exception originalException, String customMessage) {
        handleException(originalException, customMessage);
    }

    public ExceptionLogger(Exception origiException) {
        handleException(origiException, "");
    }

    private void handleException(Exception originalException, String customMessage) {
        boolean sendNowMail = false;

        // the exception was never thrown. So we want to send it via email
        if (!threwedExceptions.isEmpty()) {
            sendNowMail = true;
        } else {
            boolean foundException = false;
            for (int i = 0; i < threwedExceptions.size(); i++) {
                Exception tempException = threwedExceptions.get(i);
                if (tempException.getClass().equals(originalException.getClass())) {
                    foundException = true;
                }
            }
            if (!foundException) {
                threwedExceptions.add(originalException);
                sendNowMail = true;
            }
        }

        if (sendNowMail) {
            StackTraceElement exceptionTrace[] = originalException.getStackTrace();

            // create exceptionbean
            ExceptionBean exceptionBean = new ExceptionBean();
            exceptionBean.setException(originalException.toString());
            exceptionBean.setCustommessage(customMessage);
            exceptionBean.setThrowtime(new Date());
            exceptionBean.setClassname(exceptionTrace[0].getClassName());
            exceptionBean.setMethodname(exceptionTrace[0].getMethodName());
            exceptionBean.setLine(exceptionTrace[0].getLineNumber());
            
            /*try {
                SendMail.sendMail(DESTMAIL, "[P] Exception "
                        + originalException.getClass().getName(), exceptionBean.toString());
            } catch (ExceptionLogger ex) {
            }*/

            // we also save the exception in the database
            ExceptionManager exceptionManager = ManagerFactory.createExceptionManager();
            exceptionManager.save(exceptionBean);
        }
    }
}

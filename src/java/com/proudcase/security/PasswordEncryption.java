package com.proudcase.security;

import com.proudcase.exclogger.ExceptionLogger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
  * Copyright © 03.10.2012 Michel Vocks
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
 * @Date: 03.10.2012
 *
 * @Encoding: UTF-8
 */
public class PasswordEncryption {
    
    public static String generateEncryptedString(String characters) throws ExceptionLogger {
        try {
            // calculate
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(characters.getBytes());
            byte[] result = md5.digest();

            // return the string
            StringBuilder hexString = new StringBuilder();
            for (int i=0; i < result.length; i++) {
                hexString.append(Integer.toHexString(0xFF & result[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
                throw new ExceptionLogger(ex);
        }
    }

}

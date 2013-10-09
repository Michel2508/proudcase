package com.proudcase.constants;

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
public enum ENavigation {
    CONTEXT{
        
        @Override
        public String toString() {
            return "";
        }
    },
    
    INDEX{
        
        @Override
        public String toString() {
            return "/index.xhtml" 
                    + Constants.FACESREDIRECT;
        }
        
    },
    
    NEWSHOWCASE{

        @Override
        public String toString() {
            return "/showcase/newshowcase.xhtml"
                    + Constants.FACESREDIRECT;
        }
        
    },
    
    SHOWCASEMANAGER{
        
        @Override
        public String toString() {
            return "/showcase/showcasemanager.xhtml"
                    + Constants.FACESREDIRECT;
        }
    },
    
    DISPLAYSHOWCASE{
        
        @Override
        public String toString() {
            return "/showcase/displayshowcase.xhtml" + 
                    Constants.FACESREDIRECT + "&showcaseid=";
        }
    },
    
    NEWSCENTER{
        
        @Override
        public String toString() {
            return "/user/newscenter.xhtml";
        }
    },
    
    PROFILESETTINGS{
        
        @Override
        public String toString() {
            return "/user/profilesettings.xhtml";
        }
    },
    
    FRIENDS{
        
        @Override
        public String toString() {
            return "/user/friends.xhtml";
        }
    },
    
    PROFILEVIEW{
        
        @Override
        public String toString() {
            return "/user/profileview.xhtml";
        }
    },
    
    MESSAGES{
        
        @Override
        public String toString() {
            return "/user/messages.xhtml";
        }
    },
    
    SEARCH{
        
        @Override
        public String toString() {
            return "/search.xhtml";
        }
    },
    
    ERROR{
      
        @Override
        public String toString() {
            return "/error.xhtml";
        }
    },
    
    ABOUTUS{
        
        @Override
        public String toString() {
            return "/aboutus.xhtml";
        }
    },
    
    FORGOTPASSWORD {
        
        @Override
        public String toString() {
            return "/forgotpassword.xhtml"
                    + Constants.FACESREDIRECT;
        }
    },
    
    TERMS {
        
        @Override
        public String toString() {
            return "/terms.xhtml"
                    + Constants.FACESREDIRECT;
        }
    },
    
    INFOPAGE {
        
        @Override
        public String toString() {
            return "/pageinfo.xhtml"
                    + Constants.FACESREDIRECT;
        }
    },
    
    JURYBOARD {
        
        @Override
        public String toString() {
            return "/user/juryboard.xhtml";
        }
    }
}

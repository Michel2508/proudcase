package com.proudcase.managedbean;

import com.proudcase.constants.ENavigation;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
  * Copyright © 03.07.2013 Michel Vocks
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
 * @Date: 03.07.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@RequestScoped
public class NavigationBean implements Serializable{

    public NavigationBean() {
    }
    
    public String navigateToIndex() {
        return ENavigation.CONTEXT.toString() + ENavigation.INDEX.toString();
    }
    
    public String navigateToIndexShort() {
        return ENavigation.INDEX.toString();
    }
    
    public String navigateToNewShowcase() {
        return ENavigation.CONTEXT.toString() + ENavigation.NEWSHOWCASE.toString();
    }
    
    public String navigateToProfileView() {
        return ENavigation.CONTEXT.toString() + ENavigation.PROFILEVIEW.toString();
    }
    
    public String navigateToDisplayShowcase() {
        return ENavigation.CONTEXT.toString() + ENavigation.DISPLAYSHOWCASE.toString();
    }
    
    public String navigateToAboutUs() {
        return ENavigation.CONTEXT.toString() + ENavigation.ABOUTUS.toString();
    }
    
    public String navigateToTerms() {
        return ENavigation.CONTEXT.toString() + ENavigation.TERMS.toString();
    }
    
    public String navigateToInfoPage() {
        return ENavigation.CONTEXT.toString() + ENavigation.INFOPAGE.toString();
    }
    
    public String navigateToGetStarted() {
        return ENavigation.CONTEXT.toString() + ENavigation.GETSTARTED.toString();
    }
    
}

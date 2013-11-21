package com.proudcase.constants;

import com.proudcase.managedbean.SessionBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
public class ConstantsBean {
    
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
    
    public ConstantsBean() {
    }

    public String getUPLOADSIZELIMIT() {
        return String.valueOf(Constants.UPLOADSIZELIMIT);
    }
    
    public String getVIDEOMAXUPLOAD() {
        return String.valueOf(Constants.VIDEOMAXUPLOAD);
    }
    
    public String getFILEMAXUPLOAD() {
        return String.valueOf(Constants.FILEMAXUPLOAD);
    }
    
    public String getMAXIMAGESFORSHOWCASE() {
        return String.valueOf(Constants.MAXIMAGESFORSHOWCASE);
    }
    
    public String getNUMBEROFSTARS() {
        return String.valueOf(Constants.NUMBEROFSTARS);
    }
    
    public String getNEWSCENTERCONTEXT() {
        return ENavigation.CONTEXT.toString() + ENavigation.NEWSCENTER.toString();
    }
    
    public String getNEWSCENTER() {
        return ENavigation.NEWSCENTER.toString();
    }
    
    public String getPROFILESETTINGS() {
        return ENavigation.PROFILESETTINGS.toString();
    }
    
    public String getFRIENDS() {
        return ENavigation.FRIENDS.toString();
    }
    
    public String getPROFILEVIEWCONTEXT() {
        return ENavigation.CONTEXT.toString() + ENavigation.PROFILEVIEW.toString();
    }
    
    
    public String getDISPLAYSHOWCASE() {
        return ENavigation.CONTEXT.toString() + ENavigation.DISPLAYSHOWCASE.toString();
    }
    
    public String getSEARCH() {
        return ENavigation.CONTEXT.toString() + ENavigation.SEARCH.toString();
    }
    
    public String getJURYBOARD() {
        return ENavigation.CONTEXT.toString() + ENavigation.JURYBOARD.toString();
    }
    
    public String getNEWSHOWCASE() {
        return ENavigation.CONTEXT.toString() + ENavigation.NEWSHOWCASE.toString();
    }
    
    public String getTIMEZONE() {
        TimeZone currentTimeZone = TimeZone.getDefault();
        return currentTimeZone.getID();
    }
    
    public String getINDEX() {
        return ENavigation.INDEX.toString();
    }
    
    public String getMESSAGES() {
        return ENavigation.MESSAGES.toString();
    }
    
    public String getMAXCHARSREMAINING() {
        return String.valueOf(Constants.MAXCHARSREMAINING);
    }
    
    public String getMAXCHARSSHOWCASETITLE() {
        return String.valueOf(Constants.MAXCHARSSHOWCASETITLE);
    }
    
    public String performDateAsString(Date parseDate) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d. MMMM yyyy H:mm:ss"
                , sessionBean.getUserLocale());
        return format.format(parseDate).toString();
    }
    
    public String getShortDateAsString(Date parseDate) {
        SimpleDateFormat format = new SimpleDateFormat("d. MMMM yyyy"
                , sessionBean.getUserLocale());
        return format.format(parseDate).toString();
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}

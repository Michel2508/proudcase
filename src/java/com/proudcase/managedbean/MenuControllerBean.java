package com.proudcase.managedbean;

import com.proudcase.constants.ENavigation;
import com.proudcase.filehandling.PropertyReader;
import com.proudcase.persistence.LangCategorieBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.model.menu.DefaultMenuColumn;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.DynamicMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 * Copyright © 17.08.2013 Michel Vocks This file is part of proudcase.
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
 * @Date: 17.08.2013
 *
 * @Encoding: UTF-8
 */
@ManagedBean
@SessionScoped
public class MenuControllerBean implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    
    transient private MenuModel menuModel;
    
    // static information
    private static final String IMAGELIB = "images";
    private static final String IMAGEWIDTH = "200";
    private static final String IMAGEHEIGHT = "150";
    private static final String SEARCHPARAM = "?categorieString=";

    public MenuControllerBean() {
    }

    public void generateMenuModel() {
        FacesContext fCtx = FacesContext.getCurrentInstance();

        // create default model
        menuModel = new DynamicMenuModel();

        // create the index menu
        DefaultSubMenu indexMenu = new DefaultSubMenu();
        indexMenu.setIcon("ui-icon-home");
        indexMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_0", null, sessionBean.getUserLocale()));

        // we need a submenu 
        DefaultSubMenu subindexMenu = new DefaultSubMenu();
        subindexMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_0", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstIndColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondIndColumn = new DefaultMenuColumn();

        // Create the menu item
        DefaultMenuItem index1Item = new DefaultMenuItem();
        index1Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_0_1", null, sessionBean.getUserLocale()));
        index1Item.setUrl(ENavigation.INDEX.toString());

        // Create the menu item
        DefaultMenuItem index2Item = new DefaultMenuItem();
        index2Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_0_2", null, sessionBean.getUserLocale()));
        index2Item.setUrl(ENavigation.INFOPAGE.toString());

        // add to the subsubmenu
        subindexMenu.addElement(index1Item);
        subindexMenu.addElement(index2Item);

        // add subsubmenu to column
        firstIndColumn.addElement(subindexMenu);

        // create image component
        GraphicImage indexImage = new GraphicImage();
        indexImage.setLibrary(IMAGELIB);
        indexImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_0_imagename", null, sessionBean.getUserLocale()));
        indexImage.setWidth(IMAGEWIDTH);
        indexImage.setHeight(IMAGEHEIGHT);

        // add image to index
        secondIndColumn.addElement(new DefaultMenuItem(indexImage));

        // add columns to index menu
        indexMenu.addElement(firstIndColumn);
        indexMenu.addElement(secondIndColumn);

        // add index menu
        menuModel.addElement(indexMenu);

        // ----------------- Next Menu ------------------
        // create the categorie menu
        DefaultSubMenu categorieMenu = new DefaultSubMenu();
        categorieMenu.setIcon("ui-icon-tag");
        categorieMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_1", null, sessionBean.getUserLocale()));

        // we need a submenu for the categorie menu
        DefaultSubMenu subKategorieMenu = new DefaultSubMenu();
        subKategorieMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_1", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstCatColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondCatColumn = new DefaultMenuColumn();

        // fill the categorie menu with categories
        for (LangCategorieBean categorie : sessionBean.getLocaleCategorieList()) {
            // Create the menu item
            DefaultMenuItem categorieItem = new DefaultMenuItem();
            categorieItem.setValue(categorie.getCategoriename());
            categorieItem.setUrl(ENavigation.SEARCH.toString() + SEARCHPARAM + categorie.getCategoriename());

            // add to the subsubmenu
            subKategorieMenu.addElement(categorieItem);
        }

        // add subsubmenu to column
        firstCatColumn.addElement(subKategorieMenu);

        // create image component
        GraphicImage categorieImage = new GraphicImage();
        categorieImage.setLibrary(IMAGELIB);
        categorieImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_1_imagename", null, sessionBean.getUserLocale()));
        categorieImage.setWidth(IMAGEWIDTH);
        categorieImage.setHeight(IMAGEHEIGHT);

        // add image to categorie
        secondCatColumn.addElement(new DefaultMenuItem(categorieImage));

        // add columns to categorie menu
        categorieMenu.addElement(firstCatColumn);
        categorieMenu.addElement(secondCatColumn);

        // add categorie menu
        menuModel.addElement(categorieMenu);

        // ----------------- Next Menu ------------------
        // create the showcase menu
        DefaultSubMenu showcaseMenu = new DefaultSubMenu();
        showcaseMenu.setIcon("ui-icon-comment");
        showcaseMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_2", null, sessionBean.getUserLocale()));

        // we need a submenu 
        DefaultSubMenu subShowcaseMenu = new DefaultSubMenu();
        subShowcaseMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_2", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstShowColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondShowColumn = new DefaultMenuColumn();

        // Create the menu item
        DefaultMenuItem showcase1Item = new DefaultMenuItem();
        showcase1Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_2_1", null, sessionBean.getUserLocale()));
        showcase1Item.setUrl(ENavigation.NEWSHOWCASE.toString());

        // Create the menu item
        DefaultMenuItem showcase2Item = new DefaultMenuItem();
        showcase2Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_2_2", null, sessionBean.getUserLocale()));
        showcase2Item.setUrl(ENavigation.SHOWCASEMANAGER.toString());

        // Create the menu item
        DefaultMenuItem showcase3Item = new DefaultMenuItem();
        showcase3Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_2_3", null, sessionBean.getUserLocale()));
        showcase3Item.setUrl(ENavigation.SEARCH.toString());

        // add to the subsubmenu
        subShowcaseMenu.addElement(showcase1Item);
        subShowcaseMenu.addElement(showcase2Item);
        subShowcaseMenu.addElement(showcase3Item);

        // add subsubmenu to column
        firstShowColumn.addElement(subShowcaseMenu);

        // create image component
        GraphicImage showcaseImage = new GraphicImage();
        showcaseImage.setLibrary(IMAGELIB);
        showcaseImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_2_imagename", null, sessionBean.getUserLocale()));
        showcaseImage.setWidth(IMAGEWIDTH);
        showcaseImage.setHeight(IMAGEHEIGHT);

        // add image to categorie
        secondShowColumn.addElement(new DefaultMenuItem(showcaseImage));

        // add columns to categorie menu
        showcaseMenu.addElement(firstShowColumn);
        showcaseMenu.addElement(secondShowColumn);

        // add showcase menu
        menuModel.addElement(showcaseMenu);

        // ----------------- Next Menu ------------------
        // create the ranking menu
        DefaultSubMenu rankingMenu = new DefaultSubMenu();
        rankingMenu.setIcon("ui-icon-star");
        rankingMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_3", null, sessionBean.getUserLocale()));

        // we need a submenu 
        DefaultSubMenu subRankingMenu = new DefaultSubMenu();
        subRankingMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_3", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstRankColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondRankColumn = new DefaultMenuColumn();

        // Create the menu item
        DefaultMenuItem ranking1Item = new DefaultMenuItem();
        ranking1Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_3_1", null, sessionBean.getUserLocale()));
        ranking1Item.setUrl("#");

        // Create the menu item
        DefaultMenuItem ranking2Item = new DefaultMenuItem();
        ranking2Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_3_2", null, sessionBean.getUserLocale()));
        ranking2Item.setUrl("#");

        // add to the subsubmenu
        subRankingMenu.addElement(ranking1Item);
        subRankingMenu.addElement(ranking2Item);

        // add subsubmenu to column
        firstRankColumn.addElement(subRankingMenu);

        // create image component
        GraphicImage rankingImage = new GraphicImage();
        rankingImage.setLibrary(IMAGELIB);
        rankingImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_3_imagename", null, sessionBean.getUserLocale()));
        rankingImage.setWidth(IMAGEWIDTH);
        rankingImage.setHeight(IMAGEHEIGHT);

        // add image to ranking
        secondRankColumn.addElement(new DefaultMenuItem(rankingImage));

        // add columns to ranking menu
        rankingMenu.addElement(firstRankColumn);
        rankingMenu.addElement(secondRankColumn);

        // add ranking menu
        menuModel.addElement(rankingMenu);

        // ----------------- Next Menu ------------------
        // create the member menu
        DefaultSubMenu memberMenu = new DefaultSubMenu();
        memberMenu.setIcon("ui-icon-person");
        memberMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_4", null, sessionBean.getUserLocale()));

        // we need a submenu 
        DefaultSubMenu subMemberMenu = new DefaultSubMenu();
        subMemberMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_4", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstMemColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondMemColumn = new DefaultMenuColumn();

        // Create the menu item
        DefaultMenuItem member1Item = new DefaultMenuItem();
        member1Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_4_1", null, sessionBean.getUserLocale()));
        member1Item.setUrl(ENavigation.FRIENDS.toString());

        // Create the menu item
        DefaultMenuItem member2Item = new DefaultMenuItem();
        member2Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_4_2", null, sessionBean.getUserLocale()));
        member2Item.setUrl(ENavigation.MESSAGES.toString());

        // add to the subsubmenu
        subMemberMenu.addElement(member1Item);
        subMemberMenu.addElement(member2Item);

        // add subsubmenu to column
        firstMemColumn.addElement(subMemberMenu);

        // create image component
        GraphicImage memberImage = new GraphicImage();
        memberImage.setLibrary(IMAGELIB);
        memberImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_4_imagename", null, sessionBean.getUserLocale()));
        memberImage.setWidth(IMAGEWIDTH);
        memberImage.setHeight(IMAGEHEIGHT);

        // add image to ranking
        secondMemColumn.addElement(new DefaultMenuItem(memberImage));

        // add columns to member menu
        memberMenu.addElement(firstMemColumn);
        memberMenu.addElement(secondMemColumn);

        // add member menu
        menuModel.addElement(memberMenu);

        // ----------------- Next Menu ------------------
        // create the account menu
        DefaultSubMenu accountMenu = new DefaultSubMenu();
        accountMenu.setIcon("ui-icon-gear");
        accountMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_5", null, sessionBean.getUserLocale()));

        // we need a submenu 
        DefaultSubMenu subaccountMenu = new DefaultSubMenu();
        subaccountMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_5", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstAccColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondAccColumn = new DefaultMenuColumn();

        // Create the menu item
        DefaultMenuItem account1Item = new DefaultMenuItem();
        account1Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_5_1", null, sessionBean.getUserLocale()));
        account1Item.setUrl(ENavigation.PROFILESETTINGS.toString());

        // Create the menu item
        DefaultMenuItem account2Item = new DefaultMenuItem();
        account2Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_5_2", null, sessionBean.getUserLocale()));
        account2Item.setUrl(ENavigation.FRIENDS.toString());

        // add to the subsubmenu
        subaccountMenu.addElement(account1Item);
        subaccountMenu.addElement(account2Item);

        // add subsubmenu to column
        firstAccColumn.addElement(subaccountMenu);

        // create image component
        GraphicImage accountImage = new GraphicImage();
        accountImage.setLibrary(IMAGELIB);
        accountImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_5_imagename", null, sessionBean.getUserLocale()));
        accountImage.setWidth(IMAGEWIDTH);
        accountImage.setHeight(IMAGEHEIGHT);

        // add image to ranking
        secondAccColumn.addElement(new DefaultMenuItem(accountImage));

        // add columns to account menu
        accountMenu.addElement(firstAccColumn);
        accountMenu.addElement(secondAccColumn);

        // add account menu
        menuModel.addElement(accountMenu);

        // ----------------- Next Menu ------------------
        // create the terms menu
        DefaultSubMenu termsMenu = new DefaultSubMenu();
        termsMenu.setIcon("ui-icon-power");
        termsMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_6", null, sessionBean.getUserLocale()));

        // we need a submenu 
        DefaultSubMenu subtermsMenu = new DefaultSubMenu();
        subtermsMenu.setLabel(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_6", null, sessionBean.getUserLocale()));

        // and two columns
        DefaultMenuColumn firstTermColumn = new DefaultMenuColumn();
        DefaultMenuColumn secondTermColumn = new DefaultMenuColumn();

        // Create the menu item
        DefaultMenuItem terms1Item = new DefaultMenuItem();
        terms1Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_6_1", null, sessionBean.getUserLocale()));
        terms1Item.setUrl(ENavigation.ABOUTUS.toString());

        // Create the menu item
        DefaultMenuItem terms2Item = new DefaultMenuItem();
        terms2Item.setValue(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "submenu_6_2", null, sessionBean.getUserLocale()));
        terms2Item.setUrl(ENavigation.TERMS.toString());

        // add to the subsubmenu
        subtermsMenu.addElement(terms1Item);
        subtermsMenu.addElement(terms2Item);

        // add subsubmenu to column
        firstTermColumn.addElement(subtermsMenu);

        // create image component
        GraphicImage termsImage = new GraphicImage();
        termsImage.setLibrary(IMAGELIB);
        termsImage.setName(PropertyReader.getMessageResourceString(fCtx
                .getApplication().getMessageBundle(), "menutab_6_imagename", null, sessionBean.getUserLocale()));
        termsImage.setWidth(IMAGEWIDTH);
        termsImage.setHeight(IMAGEHEIGHT);

        // add image to ranking
        secondTermColumn.addElement(new DefaultMenuItem(termsImage));

        // add columns to account menu
        termsMenu.addElement(firstTermColumn);
        termsMenu.addElement(secondTermColumn);

        // add account menu
        menuModel.addElement(termsMenu);
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public MenuModel getMenuModel() {
        generateMenuModel();

        return menuModel;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.colstore.file.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import net.colstore.util.NUtil;
import net.colstore.web.mbeans.ColManagerBean;
import net.colstore.web.model.ColList;
import net.colstore.web.model.DbList;

/**
 *
 * @author kishore
 */
public class StorageManager {
    Database db;
    String userName;
    String dbName;
    String tableName;
    List<String> condCols;
    List<String> condColsVal;
    List<String> reqCols;
    List<String> reqColsVal;
    private String dbBasePath;
    public StorageManager(){
        this.setDbBasePath(NUtil.getStringProperty("db.base.path", "/home/kishore/NetBeansProjects/colstore-db/db"));
    }
    public List<String> fetchCondList(List<ColList> clList){
        List<String> lst = new ArrayList<String>();
        for(ColList cl: clList){    
            lst.add(cl.getCol_name());
        }
        return lst;
    }
    public List<String> fetchValList(List<ColList> clList){
        List<String> lst = new ArrayList<String>();
        for(ColList cl: clList){
            lst.add(cl.getCol_value());
        }
        return lst;
    }
    public void insert() throws IOException{
        System.out.println("Inside sm:insert++++++++++++++++++++++++++++++++++");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ColManagerBean colMgrBean = (ColManagerBean) facesContext.getApplication().createValueBinding("#{colManagerBean}").getValue(facesContext);
        userName = dbBasePath+"/"+Integer.toString(colMgrBean.getUserId());
        dbName = colMgrBean.getSelectedDb().getDb_name();
        tableName = colMgrBean.getSelectedTable().getTbl_name();
        db = new Database(userName,dbName,tableName);
        condCols = fetchCondList(colMgrBean.getCondColumnList());
        condColsVal = fetchValList(colMgrBean.getCondColumnList());
        db.addDataThroughRandomAccess(condCols, condColsVal);
        colMgrBean.clearListData();
    }
    public void read() throws IOException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ColManagerBean colMgrBean = (ColManagerBean) facesContext.getApplication().createValueBinding("#{colManagerBean}").getValue(facesContext);
        userName = dbBasePath+"/"+Integer.toString(colMgrBean.getUserId());
        dbName = colMgrBean.getSelectedDb().getDb_name();
        tableName = colMgrBean.getSelectedTable().getTbl_name();
        db = new Database(userName,dbName,tableName);
        condCols = fetchCondList(colMgrBean.getCondColumnList());
        condColsVal = fetchValList(colMgrBean.getCondColumnList());
        reqCols = fetchCondList(colMgrBean.getReqColumnList());
        //System.out.println("CHECKING SIZE:"+reqCols.size());
        //=  db.readData(condCols, condColsVal, reqCols);
        colMgrBean.setOutputDataList(db.readData(condCols, condColsVal, reqCols)); 
        colMgrBean.clearListData();
        for(List<String> l:colMgrBean.getOutputDataList()){
            System.out.println("COLUMN VALUES:");
            for(String s:l){
                System.out.println(s);
            }
        }
    }
    public void update() throws IOException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ColManagerBean colMgrBean = (ColManagerBean) facesContext.getApplication().createValueBinding("#{colManagerBean}").getValue(facesContext);
        userName = dbBasePath+"/"+Integer.toString(colMgrBean.getUserId());
        dbName = colMgrBean.getSelectedDb().getDb_name();
        tableName = colMgrBean.getSelectedTable().getTbl_name();
        db = new Database(userName,dbName,tableName);
        condCols = fetchCondList(colMgrBean.getCondColumnList());
        condColsVal = fetchValList(colMgrBean.getCondColumnList());
        reqCols = fetchCondList(colMgrBean.getReqColumnList());
        reqColsVal = fetchValList(colMgrBean.getReqColumnList());
        db.updateData(condCols, condColsVal, reqCols, reqColsVal);
        colMgrBean.clearListData();
    }
    public void delete() throws IOException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ColManagerBean colMgrBean = (ColManagerBean) facesContext.getApplication().createValueBinding("#{colManagerBean}").getValue(facesContext);
        userName = dbBasePath+"/"+Integer.toString(colMgrBean.getUserId());
        dbName = colMgrBean.getSelectedDb().getDb_name();
        tableName = colMgrBean.getSelectedTable().getTbl_name();
        db = new Database(userName,dbName,tableName);
        condCols = fetchCondList(colMgrBean.getCondColumnList());
        condColsVal = fetchValList(colMgrBean.getCondColumnList());
        db.deleteData(condCols, condColsVal);
        colMgrBean.clearListData();
    }

    public String getDbBasePath() {
        return dbBasePath;
    }

    public void setDbBasePath(String dbBasePath) {
        this.dbBasePath = dbBasePath;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.colstore.file.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;
import javafx.util.Pair;
import javax.faces.context.FacesContext;
import net.colstore.web.mbeans.ColManagerBean;

/**
 *
 * @author kishore
 */
public class Database {
    private String userId;
    private String dbName;
    private String tableName;
    FacesContext facesContext;
    ColManagerBean colMgrBean;

    public Database() {
    }

    
    public Database(String userId, String dbName) {
        this.userId = userId;
        this.dbName = userId+"/"+dbName;
    }
    
    public Database(String userId, String dbName, String tableName) {
        this.userId = userId;
        this.dbName = userId+"/"+dbName;
        this.tableName = this.dbName+"/"+tableName;
        facesContext = FacesContext.getCurrentInstance();
        colMgrBean = (ColManagerBean) facesContext.getApplication().createValueBinding("#{colManagerBean}").getValue(facesContext);
    }
    
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    void createDB(String userId,String dbName){
        //this.dbName = "/home/kishore/NetBeansProjects/databases"+"/"+userId + "/" + dbName;
        File dir = new File(this.dbName);
        //check if db alrady exists
        boolean successful = dir.mkdirs();
        if (!successful)
        {
          // creating the directory failed
          System.out.println("failed trying to create the directory");
        }
    }
    void createTable(String tableName,List<String> cols){
        //this.tableName = this.dbName +"/"+ tableName;
        try{
            File dir = new File(this.tableName);
            if(!dir.exists()){
                boolean success = dir.mkdir();
                if(success){
                    //create cols
                    for(int i=0;i<cols.size();i++){
                        //enclose in try catch
                        File file = new File(this.tableName+"/"+cols.get(i));
                        success = file.createNewFile();
                        if(!success){
                            //throw error
                            System.out.println("col creation failed");
                        }
                    }
                }
            }
            else
                System.out.println("this table name already exists");
        }
        catch(IOException e){
            System.out.println("Exception Occurred:");
	    e.printStackTrace();
        }
    }
    void addDataThroughRandomAccess(List<String> colList,List<String> valueList) throws IOException{
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ColManagerBean colMgrBean = (ColManagerBean) facesContext.getApplication().createValueBinding("#{colManagerBean}").getValue(facesContext);
        FileEditor fe;
        DataType dt;
        SchemaParser sp = new SchemaParser();
        String[] schemaH = tableName.split("/");
        String schema = schemaH[schemaH.length-1];
        sp.parse(tableName+"/"+schema+"_schema.xml");//path of xml schema
        HashMap<String,String> attrMap = sp.getColList();
        if(attrMap.size()==colList.size()){
            for(int i=0;i<colList.size();i++){
                if (attrMap.containsKey(colList.get(i))){
                    String fileName=this.getTableName()+"/"+colList.get(i);
                    fe = new FileEditor(fileName);
                    //dt = new DataType(valueList.get(i));//id as para of constructor
                    dt = new DataType(valueList.get(i),attrMap.get(colList.get(i)));

                    //dt.setDataType(attrMap.get(colList.get(i)));
                    fe.setDt(dt);
                    Long len = fe.file.length();//lenth is property of RandomAccess not fe
                    fe.insertRecord(dt, (len/dt.SIZE)+1);
                }
                else{
                    System.out.println(colList.get(i)+"column doesn't exist");
                }
            }
            colMgrBean.setMsg("successfully inserted");
        }
        else{
            colMgrBean.setMsg("all columns values doesn't exist");
            System.out.println("all columns values doesn't exist");
        }
    }
    void addData(List<String> col,List<String> value) throws IOException{
        for(int i=0;i<col.size();i++){
            File file = new File(this.tableName+"/"+col.get(i));
            if(file.exists()){
                FileWriter fr = new FileWriter(file, true);
                fr.write(value.get(i)+"\n");
                fr.close();
            }
            else
                System.out.println(col.get(i)+" column name doesnt exist");
            //**modify code accordingly**/if any col name doesnt exist, dont write in any file
        }
    }
    List<List<String>> readData(List<String> colsScanned,List<String> valsScanned,List<String> reqCols) throws FileNotFoundException, IOException{
        List<List<String>> dataList = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        
        IdManager idm = new IdManager(this);
        //ids = idm.getIds(colsScanned, valsScanned);
        ids = idm.getIdthroughRandomAccess(colsScanned, valsScanned);
        
        //System.out.println("size:"+ids.size());
        List<Long> idList = new ArrayList<Long>(ids);
        //for (Long x : ids)
        //    idList.add(x);
        //System.out.println(idList.get(0));
        if(ids.size()>0){
            colMgrBean.setMsg(idList.size()+" data found");
            return idm.getValuesthroughRandomAcess(idList, reqCols);
            
        }
        else{
            colMgrBean.setMsg("No data found for given condition");
            return dataList;
            
        }
        
        //return dataList;
    }
    void updateData(List<String> colsScanned,List<String> valsScanned,List<String> reqCols,List<String> reqVals) throws IOException{
        //search in file and find id then update
        Set<Long> ids = new HashSet<>();
        
        IdManager idm = new IdManager(this);
        //ids = idm.getIds(colsScanned, valsScanned);
        ids = idm.getIdthroughRandomAccess(colsScanned, valsScanned);
        List<Long> idList = new ArrayList<Long>(ids);
        //for (Long x : ids)
        //    idList.add(x);
        
        
        //idm.updateVals(idList,reqCols,reqVals);
        if(idList.size()>0){
            idm.updateValthroghRandomAccess(idList, reqCols, reqVals);
            colMgrBean.setMsg(idList.size()+" data updated usccessfully");
        }
        else
            colMgrBean.setMsg("No data updated");
    }
    void deleteData(List<String> colsScanned,List<String> valsScanned)throws IOException{
        //search in file and find id then delete
        Set<Long> ids = new HashSet<>();
        
        IdManager idm = new IdManager(this);
        //ids = idm.getIds(colsScanned, valsScanned);
        ids = idm.getIdthroughRandomAccess(colsScanned, valsScanned);
        List<Long> idList = new ArrayList<Long>(ids);
        //System.out.println("HELLO FROM DELETE OF DATBASE++++++++++SIZE IS"+idList.size());
        //for (Long x : ids)
        //    idList.add(x);
        
        //idm.deleteDatas(idList);
        if(idList.size()>0){
            idm.deleteDataThroughRandomAccess(idList);
            //colMgrBean.setMsg(idList.size()+" data deleted successfully");
        }
        else
            colMgrBean.setMsg("No Data deleted");
    }
    public List<List<String>> readAll() throws IOException{
        List<List<String>> dataList = new ArrayList<>();
        IdManager idm = new IdManager(this);
        dataList=idm.showAllRecords();
        return dataList;
    }
}

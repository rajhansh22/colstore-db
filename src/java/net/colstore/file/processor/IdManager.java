/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.colstore.file.processor;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kishore
 */
public class IdManager {
    private Database db;
    File file;
    BufferedReader br;
    String st="";

    public IdManager(Database db) {
        this.db = db;
    }
    Set<Long> getIdthroughRandomAccess(List<String> colsScanned,List<String> valsScanned) throws FileNotFoundException, IOException{
        Set<Long> idList = new HashSet<>();
        
        FileEditor fe;
        DataType dt;
        SchemaParser sp = new SchemaParser();
        String[] schemaH = db.getTableName().split("/");
        String schema = schemaH[schemaH.length-1];
        sp.parse(db.getTableName()+"/"+schema+"_schema.xml");//path of xml schema
        HashMap<String,String> attrMap = sp.getColList();
        String fileName = db.getTableName()+"/"+colsScanned.get(0);
        if (attrMap.containsKey(colsScanned.get(0))){
            fe=new FileEditor(fileName);
            dt = new DataType("",attrMap.get(colsScanned.get(0)));
            fe.setDt(dt);
            idList = fe.getIds(valsScanned.get(0));
        }
        else{
                System.out.println(valsScanned.get(0)+"column doesn't exist");
        }
        for(int i=1;i<colsScanned.size();i++){
            if (attrMap.containsKey(colsScanned.get(i))){
                fileName = db.getTableName()+"/"+colsScanned.get(i);
                fe=new FileEditor(fileName);
                dt = new DataType("",attrMap.get(colsScanned.get(i)));
                fe.setDt(dt);
                //Set<Long> colIds = fe.getIds(valsScanned.get(i));
                Set<Long> colList = fe.getIdsFromSubset(idList,valsScanned.get(i));
                idList=colList;
                //idList.removeAll(colIds);
            }
            else{
                System.out.println(valsScanned.get(i)+"column doesn't exist");
            }
        }
        
        
        return idList;
    }
    List<List<String>> getValuesthroughRandomAcess(List<Long> idList,List<String> reqCols) throws FileNotFoundException, IOException{
        List<List<String>> dataList = new ArrayList<>();
        FileEditor fe;
        DataType dt;
        SchemaParser sp = new SchemaParser();
        String[] schemaH = db.getTableName().split("/");
        String schema = schemaH[schemaH.length-1];
        sp.parse(db.getTableName()+"/"+schema+"_schema.xml");//path of xml schema
        HashMap<String,String> attrMap = sp.getColList();
        List<String> valList = new ArrayList<>();
        for(String reqCol:reqCols){
            List<String> colValList = new ArrayList<>();
            String fileName = db.getTableName()+"/"+reqCol;
            if (attrMap.containsKey(reqCol)){
                fe = new FileEditor(fileName);
                for(Long id:idList){
                    dt = new DataType(id,attrMap.get(reqCol));
                    fe.setDt(dt);
                    dt = fe.getRecord();
                    //dt=fe.getRecord(id);
                    //dt.setDataType(attrMap.get(reqCol));
                    //fe.setDt(dt);
                    String str="$$$$$";
                    if(!str.equals(dt.getData().trim())){
                        colValList.add(dt.getData());
                        System.out.println("COLUMN DATA VALUE ADDED");
                    }
                }
                dataList.add(colValList);
            }
            else{
                System.out.println(reqCol+"column doesn't exist");
            }
        }
        return dataList;
    }
    void updateValthroghRandomAccess(List<Long> idList,List<String> reqCols,List<String> reqVals) throws FileNotFoundException, IOException{
        FileEditor fe;
        DataType dt;
        SchemaParser sp = new SchemaParser();
        String[] schemaH = db.getTableName().split("/");
        String schema = schemaH[schemaH.length-1];
        sp.parse(db.getTableName()+"/"+schema+"_schema.xml");//path of xml schema
        HashMap<String,String> attrMap = sp.getColList();
        //for(String col:reqCols){
        for(int i=0;i<reqCols.size();i++){
            if (attrMap.containsKey(reqCols.get(i))){
                String fileName =  db.getTableName()+"/"+reqCols.get(i);
                fe = new FileEditor(fileName);
                //write utf or similar to insert at that id pointer
                dt = new DataType(reqVals.get(i),attrMap.get(reqCols.get(i)));
                //dt.setDataType(attrMap.get(reqCols.get(i)));
                fe.setDt(dt);
                for(Long id:idList){
                    fe.insertRecord(dt, id);
                }
            }
            else{
                System.out.println(reqCols.get(i)+"column doesn't exist");
            }
        }
    }
    void deleteDataThroughRandomAccess(List<Long> idList) throws FileNotFoundException, IOException{
        String str="";
        //get all files
        FileEditor fe;
        DataType dt;
        System.out.println("HELLO FROM DELETE OF IDM++++++++++");
        SchemaParser sp = new SchemaParser();
        String[] schemaH = db.getTableName().split("/");
        String schema = schemaH[schemaH.length-1];
        sp.parse(db.getTableName()+"/"+schema+"_schema.xml");//path of xml schema
        HashMap<String,String> attrMap = sp.getColList();
        File folder = new File(db.getTableName());
        File[] listOfFiles = folder.listFiles();
        System.out.println("HELLO FROM RANDOM DEL++++++++++");
        for(File f:listOfFiles){
            String fName=f.getName();
            String fileName=db.getTableName()+"/"+fName;
            System.out.println("PRINTING FILENAME++++++++++"+fileName);
            if (attrMap.containsKey(fName)){
                fe = new FileEditor(fileName);
                if(attrMap.get(fName).equals("INT"))
                    dt = new DataType("-100",attrMap.get(fName));
                else
                    dt = new DataType("$$$$$",attrMap.get(fName));
                fe.setDt(dt);
                for(Long id:idList){
                    //dt=new DataType(id,attrMap.get(fileName));
                    
                    System.out.println("====================++++++idm::data type"+dt.getDataType()+":"+dt.getData());
                    //fe.deleteRecord(dt);
                    fe.insertRecord(dt, id);
                }
            }
            else{
                System.out.println(fileName+" column doesn't exist");
            }
        }
    }
    
    public List<List<String>> showAllRecords() throws IOException {
      FileEditor fe;
      DataType dt;
      List<String> colValList;
      List<List<String>> allData=new ArrayList<>();
      SchemaParser sp = new SchemaParser();
      String[] schemaH = db.getTableName().split("/");
      String schema = schemaH[schemaH.length-1];
      sp.parse(db.getTableName()+"/"+schema+"_schema.xml");//path of xml schema
      HashMap<String,String> attrMap = sp.getColList();
      File folder = new File(db.getTableName());
      File[] listOfFiles = folder.listFiles();
      for(File f:listOfFiles){
          String fName=f.getName();
          String fileName=db.getTableName()+"/"+fName;
          if (attrMap.containsKey(fName)){
            fe = new FileEditor(fileName);
            if(attrMap.get(fName)=="INT")
                    dt = new DataType("-100",attrMap.get(fName));
                else
                    dt = new DataType("$$$$$",attrMap.get(fName));
            fe.setDt(dt);
            colValList = fe.showAllRecords();
            allData.add(colValList);
          }
          else{
              System.out.println(fileName+" column doesn't exist");
          }
      }
      return allData;
   }
    
    Set<Long> getIds(List<String> colsScanned,List<String> valsScanned) throws FileNotFoundException, IOException{
        //List<Long> idList = new ArrayList<>();
        Set<Long> idList = new HashSet<>();
        File folder = new File(db.getTableName());
        File file = new File(folder+"/"+colsScanned.get(0));
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st="";
        String valReq=valsScanned.get(0);
        int id=1;
        while ((st = br.readLine()) != null) {
          //String val=st.split(":")[1].trim();
          String val=st.trim();
          if(val.equals(valReq)){
              //idList.add(Long.parseLong(st.split(":")[0].trim()));
              idList.add(Long.valueOf(id));
          }
          id++;
        }
        id=1;
        for(int i=1;i<colsScanned.size();i++){
            String colName=colsScanned.get(i);
            file = new File(folder+"/"+colName);
            br = new BufferedReader(new FileReader(file));
            st="";
            valReq=valsScanned.get(i);
            Set<Long> colIds = new HashSet<>();
            while ((st = br.readLine()) != null) {
              //String val=st.split(":")[1].trim();
              String val=st.trim();
              if(val.equals(valReq)){
                  //colIds.add(Long.parseLong(st.split(":")[0].trim()));
                  colIds.add(Long.valueOf(id));
              }
              id++;
            }
            //remove ids
            idList.removeAll(colIds);
        }
        return idList;
    }
    List<List<String>> getValues(List<Long> idList,List<String> reqCols) throws FileNotFoundException, IOException{
        List<List<String>> dataList = new ArrayList<>();
        List<String> valList = new ArrayList<>();
        for(String reqCol:reqCols){
            List<String> colValList = new ArrayList<>();
            File folder = new File(db.getTableName());
            file = new File(folder+"/"+reqCol);
            br = new BufferedReader(new FileReader(file));
            st="";
            int id=1;
            while ((st = br.readLine()) != null) {
                int ptr=0;
                Iterator<Long> itr = idList.iterator();
                //Long id=Long.parseLong(st.split(":")[0].trim());
                Long idL=Long.valueOf(id);
                if(idL.equals(idList.get(ptr))){
                    idList.get(ptr);
                    //String val=st.split(":")[1].trim();
                    String val=st.trim();
                    colValList.add(val);
                    ptr++;
                }
                id++;
            }
            dataList.add(colValList);
        }
        return dataList;
    }
    void updateVals(List<Long> idList,List<String> reqCols,List<String> reqVals) throws FileNotFoundException, IOException{
        for(String col:reqCols){
            File folder = new File(db.getTableName());
            file = new File(folder+"/"+col);
            br = new BufferedReader(new FileReader(file));
            st="";
            String colData="";
            Long currId=new Long(1);
            while ((st = br.readLine()) != null) {
                //Long currId=Long.parseLong(st.split(":")[0].trim());
                
                for(int i=0;i<reqCols.size();i++){
                    Long id = idList.get(i);
                    if(!currId.equals(id)){
                        //colData+=st;
                        //colData+="\n";
                        //colData+=st.split(":")[0].trim();
                        colData+=st.trim();
                        //colData+=":";
                        //colData+=st.split(":")[1].trim();
                        colData+="\n";
                    }
                    else{
                        //colData+=st.split(":")[0].trim();
                        //colData+=":";
                        colData+=reqVals.get(i);
                        colData+="\n";
                    }
                }
                currId++;
            }
            //overwrite file
            FileWriter fr = new FileWriter(file, false);//not appending
            fr.write(colData);
            fr.close();
        }
    }
    void deleteDatas(List<Long> idList) throws FileNotFoundException, IOException{
        String str="";
        //get all files
        File folder = new File(db.getTableName());
        File[] listOfFiles = folder.listFiles();
        for(File file:listOfFiles){
            br = new BufferedReader(new FileReader(file));
            st="";
            String colData="";
            Long currId=new Long(1);
            while ((st = br.readLine()) != null) {
                //Long currId=Long.parseLong(st.split(":")[0].trim());
                
                for(Long id:idList){
                    if(!currId.equals(id)){
                        //colData+=st.split(":")[0].trim();
                        //colData+=":";
                        //colData+=st.split(":")[1].trim();
                        colData+=st.trim();
                        colData+="\n";
                        //colData+=st.split(":")[1].trim();
                    }
                }
                currId++;
            }
            FileWriter fr = new FileWriter(file, false);//not appending
            fr.write(colData);
            fr.close();
        }
        
    }
}

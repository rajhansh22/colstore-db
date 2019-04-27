/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.colstore.file.processor;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author kishore
 */
public class FileEditor {
   RandomAccessFile file;
   DataType dt;

   public FileEditor(String fileString)
         throws IOException {
      file = new RandomAccessFile(fileString, "rw");
   }

    public void setDt(DataType dt) {
        this.dt = dt;
    }
   
   
   public void close() throws IOException {
      if (file != null)
         file.close();
   }
   
   public Set<Long> getIds(String val){
       //given a file(column name) and value for that col
       Set<Long> idSet=new HashSet<Long>();
       //DataType record = new DataType();
       try {
         file.seek(0);
         while (true) {
            System.out.println("pointer before :"+file.getFilePointer());
            //do {
            //System.out.println("inside loop");
               dt.readFromFile(file);
            //} while (record.getId() == 0);
            //store all record in a list
            //System.out.println("value of record :"+record.getData()+":"+record.getData().length());
            System.out.println("pointer after :"+file.getFilePointer());
            if(val.equals(dt.getData().trim())){
                System.out.println("inside condition++++++++++");
                idSet.add((file.getFilePointer())/dt.SIZE);
                System.out.println("adding id :"+(file.getFilePointer())/dt.SIZE);
            }
            //System.out.println(record.toString());
            if(file.getFilePointer()==file.length())
                break;
         }
      } catch (EOFException ex1) {
         System.err.println("end of file error");
      } catch (IOException ex2) {
         System.err.println("error reading file");
      }
       return idSet;
   }
   
   public DataType getRecord() throws IOException {
       Long id=dt.getId();
      //DataType record = new DataType();
      if (id < 1)
         throw new IllegalArgumentException("invalid ID!!");
         //System.out.println((id - 1) * dt.SIZE);
         file.seek((id - 1) * dt.SIZE);
         dt.readFromFile(file);
         return dt;
   }

   public void insertRecord(DataType record,Long id)
         throws IllegalArgumentException, IOException {
         file.seek((id - 1) * record.SIZE);//get id as parameter
         record.writeToFile(file);
   }

   public void updateRecord(DataType record,Long id)
         throws IllegalArgumentException, IOException {
         file.seek((id - 1) * record.SIZE);
         record.writeToFile(file);
   }

   public void deleteRecord(DataType record)
         throws IllegalArgumentException, IOException {
         file.seek((record.getId() - 1) * record.SIZE);
         record = new DataType("$$$$$","STRING");
         //System.out.println("deleting data::"+record.getData()+":"+record.SIZE);
         record.writeToFile(file);
   }

   /*public void showAllRecords() {
      List<String> allData=new ArrayList<String>();
      DataType record = new DataType();
      try {
         file.seek(0);
         while (true) {
            do {
               record.readFromFile(file);
            } while (record.getId() == 0L);
            //store all record in a list
            allData.add("EVERY_DATA");
            System.out.println(record.toString());
         }
      } catch (EOFException ex1) {
         return;
      } catch (IOException ex2) {
         System.err.println("error reading file");
      }
   }*/
}
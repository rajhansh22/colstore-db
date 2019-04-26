/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.colstore.file.processor;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author kishore
 */
public class DataType {

    private Long id;
    private String dataType;
    private int dataLen=15;
    private String data;
    //set size a/c to size of data type in database
    public int SIZE;
    void calSize(){
       
       if(dataType.trim().equalsIgnoreCase("Integer")){
           SIZE=Integer.BYTES;
       }
       else if(dataType.trim().equalsIgnoreCase("Double")){
           SIZE=Double.BYTES;
       }
       else if(dataType.trim().equalsIgnoreCase("Long")){
           SIZE=Long.BYTES;
       }
       else if(dataType.trim().equalsIgnoreCase("Float")){
           SIZE=Float.BYTES;
       }
       else if(dataType.trim().equalsIgnoreCase("Short")){
           SIZE=Short.BYTES;
       }
       else if(dataType.trim().equalsIgnoreCase("String")){
           SIZE = Character.BYTES * dataLen;
       }
       else{
           SIZE = Character.BYTES * 15;
       }
    }
   public DataType() {
       calSize();
   }
   public DataType(long id) {
      this.id=id;
      calSize();
   }
   public DataType(long id,String dataType) {
      this.id=id;
      this.dataType=dataType;
      calSize();
   }
   public DataType(String data) {
      this.data=data;
      calSize();
  }
   public DataType(String data,String dataType) {
      this.data=data;
      this.dataType=dataType;
      calSize();
  }
   public DataType(String data,Long id) {
      this.data=data;
      this.id=id;
      calSize();
      //set size of data type here
   }
   
   public void setId(Long id){
       this.id=id;
   }
   public Long getId(){
       return id;
   }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDataType(String dataType){
        this.dataType=dataType;
    }
    
   
   public void readFromFile(RandomAccessFile file)
      throws IOException {
    String str=readString(file);//return it or set it somewhere
    this.data=str;
    this.id=file.getFilePointer();
   }

   public void writeToFile(RandomAccessFile file)
      throws IOException {
    writeString(file,data);//pass the string to be printed or integer if its integer
   }

   private String readString(RandomAccessFile file)
      throws IOException {
      char[] s = new char[15];
      for (int i = 0; i < s.length; i++)
         s[i] = file.readChar();
      return new String(s).replace('\0', ' ');
   }

   private void writeString(RandomAccessFile file, String s)
      throws IOException {
      StringBuffer buffer = null;
      if (s != null)
         buffer = new StringBuffer(s);
      else
         buffer = new StringBuffer(15);
         buffer.setLength(15);
         file.writeChars(buffer.toString());
      }

}
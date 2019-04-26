package net.colstore.file.processor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class SchemaParser {
    private String tableName;
    private String dbName;
    //private List<Pair<String,String>> colList=null;
    private HashMap<String,String> colList;

    public String getTableName() {
        return tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public HashMap<String,String> getColList() {
        return colList;
    }

    public void parse(String fileName) {

      try {
        File inputFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        
        colList = new HashMap<String,String>();
        Node dbDetails = doc.getElementsByTagName("DBDetails").item(0);
        Node tbDetails = doc.getElementsByTagName("table:Table_Details").item(0);
         
        Element tbDetailsEle = (Element)tbDetails;
        Node tbName = tbDetailsEle.getElementsByTagName("table:name").item(0);
        Node colDetails = tbDetailsEle.getElementsByTagName("column:Column_Details").item(0);
	NodeList attrList = ((Element)colDetails).getElementsByTagName("column:attributes");
	/*SCHEMA FORMAT
	<column:attributes>
		<column:attribute_name>Id</column:attribute_name>	
		<column:dataType>INT</column:dataType>		
	</column:attributes>
	<column:attributes>
		<column:attribute_name>userName</column:attribute_name>	
		<column:dataType>STRING</column:dataType>		
	</column:attributes>
	<column:attributes>
		<column:attribute_name>password</column:attribute_name>	
		<column:dataType>STRING</column:dataType>		
	</column:attributes>
	<column:attributes>
		<column:attribute_name>status</column:attribute_name>	
		<column:dataType>INT</column:dataType>		
	</column:attributes>
         */
         
         
         
         for (int temp = 0; temp < attrList.getLength(); temp++) {
            Node nNode = attrList.item(temp);
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               String colName = eElement.getElementsByTagName("column:attribute_name").item(0).getTextContent();
               String colDataType = eElement.getElementsByTagName("column:dataType").item(0).getTextContent();
               //Pair<String,String> attr = new Pair<String,String> (colName,colDataType);
               colList.put(colName,colDataType);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

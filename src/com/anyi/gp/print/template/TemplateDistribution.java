/*
 * Created on 2007-2-4
 *
 */
package com.anyi.gp.print.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.print.util.PrintTPLLoader;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;
import com.anyi.gp.util.StringTools;

/**
 * @author zhangyw
 *
 */
public class TemplateDistribution {
    
    public TemplateDistribution(){
        
    }
    
    public static boolean  distributeTemplate(String userId, String coCode, String tplCode, String isredo, String dbtpl){
        return (new TemplateDistribution())._distributeTemplate(userId, coCode, tplCode, isredo, dbtpl);
    }

    public boolean  _distributeTemplate(String userId, String coCode, String tplCode, String isredo, String dbtpl){
        if(distributeTemplateFile(coCode, tplCode, isredo, dbtpl)) {
            return insertTemplate(userId, coCode, dbtpl); 
        }
        else{
            return false;
        }
    }    
    
    public boolean distributeTemplateFile(String coCode, String tplCode, String isredo, String dbtpl){
        String filePath = PrintTPLLoader.getJasperReportPath();
        String vaTplCode[] = null;
        if("true".equals(isredo))
            vaTplCode = StringTools.splitA(tplCode, ',');
        else
            vaTplCode = StringTools.splitA(dbtpl, ',');
        for(int i=0, j=vaTplCode.length; i < j; i++){
            distributeTemplateFile(filePath, vaTplCode[i], coCode);
        }
        return true;
    }
    
    public void distributeTemplateFile(String filePath, String tplCode, String coCode){
        if (!filePath.endsWith(File.separator)) {
          filePath += File.separator;
        }
        String fname = filePath + tplCode;
        String newFname = filePath + tplCode + "_" + coCode;
        copyTemplate(fname+".html", newFname+".html");
        copyTemplate(fname+".xml", newFname+".xml");
        copyTemplate(fname+".jasper", newFname+".jasper");
    }
    
    public boolean copyTemplate(String fname, String newFname){
        try {
            File file = new File(fname);
            if(!file.exists()){
                return false;
            }
            FileInputStream input=new FileInputStream(fname); 
            FileOutputStream output=new FileOutputStream(newFname); 
            byte[] b=new byte[1024*8]; 
            int len; 
            while((len=input.read(b))!=-1){ 
                output.write(b,0,len); 
            } 
            output.flush(); 
            output.close(); 
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } 
        return true;
    }
    
    public boolean insertTemplate(String userId, String coCode, String tplCode){
        Delta delta = getTemplateInfo(tplCode);
        insertTemplate(userId, coCode, delta);
        return true;
    }
    
    public boolean insertTemplate(String userId, String coCode, Delta delta){
        TableData tableData = null;
        List list = new ArrayList();
        String sql;
        String PRN_COMPO_ID;
        String PRN_TPL_JPCODE;
        String PRN_TPL_NAME;
        String PRN_TPL_OUTTYPE;
        String PRN_TPL_REPORTTYPE;
        String PRN_TPL_FIXROWCOUNT;
        Iterator iterator = delta.iterator();
        while(iterator.hasNext()){
            tableData = (TableData)iterator.next();
            PRN_COMPO_ID = tableData.getFieldValue("PRN_COMPO_ID");
            PRN_TPL_JPCODE = tableData.getFieldValue("PRN_TPL_JPCODE");
            PRN_TPL_NAME = tableData.getFieldValue("PRN_TPL_NAME");
            PRN_TPL_OUTTYPE = tableData.getFieldValue("PRN_TPL_OUTTYPE");
            PRN_TPL_REPORTTYPE = tableData.getFieldValue("PRN_TPL_REPORTTYPE");
            PRN_TPL_FIXROWCOUNT = tableData.getFieldValue("PRN_TPL_FIXROWCOUNT");
            sql = "INSERT INTO AS_PRINT_JASPERTEMP VALUES('" + PRN_COMPO_ID + "','";
            sql += PRN_TPL_JPCODE + "_" + coCode + "','";
            sql += PRN_TPL_NAME + "',";
            sql += PRN_TPL_OUTTYPE + ",'";
            sql += PRN_TPL_REPORTTYPE + "',";
            sql += PRN_TPL_FIXROWCOUNT + ",1.0,'";
            sql += userId + "',sysdate,'";
            sql += coCode + "')";
            list.add(sql);
        }
        executeBatchSQL(list);
        return true;
    }
    
    public Delta getTemplateInfo(String tplCode){
        tplCode = StringTools.replaceAll(tplCode, "," , "','");
        tplCode = "'" + tplCode + "'";
        String sql = "SELECT * FROM AS_PRINT_JASPERTEMP WHERE PRN_TPL_JPCODE in (" + tplCode + ")";
        Delta result = null;
        Connection myConn = null;
        Statement sm = null;
        ResultSet rs = null;
        try {
            myConn = DAOFactory.getInstance().getConnection();
	          sm = myConn.createStatement();
	          rs = sm.executeQuery(sql);
	          result = new Delta(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DBHelper.closeConnection(myConn, sm, null);
        }
        return result;
    }
    
    public void executeBatchSQL(List sqls) {
        Connection conn = null;
        Statement stmt = null;
        try {
          conn = DAOFactory.getInstance().getConnection();
          conn.setAutoCommit(false);
          stmt = conn.createStatement();
          for (Iterator i = sqls.iterator(); i.hasNext();) {
            String s = i.next().toString();
            stmt.addBatch(s);
          }
          stmt.executeBatch();
          conn.commit();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        } finally {
        	DBHelper.closeConnection(conn, stmt, null);
        }
    }
}

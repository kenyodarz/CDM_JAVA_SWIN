/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.ConexionBD;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author nelson.castiblanco
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException {
        
        ConexionBD con = new ConexionBD();
        con.conectar();
        
        XSSFWorkbook wb = new XSSFWorkbook("Libro1.xlsx");
        XSSFSheet hoja = wb.getSheetAt(0);
        for(int i=0; i<=hoja.getLastRowNum(); i++){                                   
            String sql = "select numeroserie, iddespacho from transformador where numeroserie='"+hoja.getRow(i).getCell(1).getStringCellValue().replace("*", "")+"' order by 2 desc";
            ResultSet rs = con.CONSULTAR(sql);
            if(rs.next()){
                if(con.GUARDAR("update transformador set iddespacho=688 where numeroserie='"+hoja.getRow(i).getCell(1).getStringCellValue().replace("*", "")+"' and iddespacho="+rs.getInt("iddespacho"))){
                    
                }                
            }            
            
        }
    }
    
}

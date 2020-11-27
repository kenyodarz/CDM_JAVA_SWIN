/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JButtonIntoJTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author AUXPLANTA
 */
public class BotonEnColumna implements TableCellRenderer{

    public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
    Component renderer = null;
    JButton boton = new JButton();
    
    public BotonEnColumna(){
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);        
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setBackground(Color.red);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {                
        
        boton.setText(""+(row+1));
//        if(hasFocus){
//            table.setRowSelectionInterval(row, row);
//            table.setColumnSelectionInterval(0, table.getColumnCount()-1);            
//        }        
        return boton;
    }
    
}

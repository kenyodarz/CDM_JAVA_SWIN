package ColorRowJTable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author AUXPLANTA
 */
public  class ColorRowInJTable extends DefaultTableCellRenderer{    
    
    @Override
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
        
        Component component = super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);                      
        component.setForeground(Color.black);
        
        if ( (row%2)!=0 ){            
            component.setBackground(new Color(231,243,253,255));            
        }else{
            component.setBackground(Color.white);
        }
        
//        if(isSelected){
//            component.setBackground(new Color(51,122,183));
//            component.setForeground(Color.white);
//        }
        if(isSelected){
            component.setBackground(new Color(198,198,198));
            ((JLabel)component).setBorder(new LineBorder(new Color(33,115,70), 2, false));
        } 

      return component;
   }
    
}

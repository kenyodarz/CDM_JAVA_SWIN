package modelo;

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
public class ColorDespachos extends DefaultTableCellRenderer{

    Color vigente = new Color(138,224,132);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
        component.setForeground(Color.black);
        
        if( (row%2)!=0 ){
            component.setBackground(new Color(231,243,253,255));
        }else{
            component.setBackground(Color.white);
        }
        
        if(null != table.getModel().getValueAt(row, 2)){
            component.setBackground(vigente);
            component.setForeground(Color.black);
        }
        
        if(isSelected){
            component.setBackground(new Color(198,198,198));
            ((JLabel)component).setBorder(new LineBorder(new Color(33,115,70), 1, false));
        }
        
        if(column==8 || column == 9){
            if(table.getModel().getValueAt(row, 8)!=table.getModel().getValueAt(row, 9)){
                component.setBackground(new Color(210, 71, 38));
                component.setForeground(Color.white);
            }
        }else{
                if ( (row%2)!=0 ){component.setBackground(new Color(231,243,253,255));}else{component.setBackground(Color.white);}
            }
        
        return component;
    }
    
    
    
}

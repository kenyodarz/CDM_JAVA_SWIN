/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JTextFieldIntoJTable;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author AUXPLANTA
 * SIRVE PARA DIBUJAR EL COMPONENTE JTEXTFIELD EN LA CELDA DEL JTABLE
 */
public class JTextField_TableCellRenderer extends JTextField implements TableCellRenderer{

    public JTextField_TableCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setText(""+value);

        return this;
    }
    
}

package JComboBoxIntoJTable;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author AUXPLANTA
 */
public class JComboBoxEnColumnaJTable extends JComboBox implements TableCellRenderer{

    public JComboBoxEnColumnaJTable(Object[] items) {
        super(items);
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
        setSelectedItem(value);

        return this;
    }
    
}

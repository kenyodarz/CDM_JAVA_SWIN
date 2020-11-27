package DragRowJTableTransferHandler;

import Dialogos.Tensiones;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionBD;

/**
 *
 * @author AUXPLANTA
 */
public class DragRowJTableTransferHandler extends TransferHandler {
    
   private final DataFlavor localObjectFlavor = new ActivationDataFlavor(Integer.class, "application/x-java-Integer;class=java.lang.Integer", "Integer Row Index");
   private JTable           table             = null;

   public DragRowJTableTransferHandler(JTable table) {
      this.table = table;
   }

   @Override
   protected Transferable createTransferable(JComponent c) {
      assert (c == table);
      return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());
   }

   @Override
   public boolean canImport(TransferHandler.TransferSupport info) {
      boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
      table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
      return b;
   }

   @Override
   public int getSourceActions(JComponent c) {
      return TransferHandler.COPY_OR_MOVE;
   }

   @Override
   public boolean importData(TransferHandler.TransferSupport info) {
      JTable target = (JTable) info.getComponent();
      JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
      int index = dl.getRow();
      int max = table.getModel().getRowCount();
      if (index < 0 || index > max)
         index = max;
      target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      try {
         Integer rowFrom = (Integer) info.getTransferable().getTransferData(localObjectFlavor);
         if (rowFrom != -1 && rowFrom != index){
            
            int posicionFilaOriginal = rowFrom;
            int posicionFilaNUeva = index;
            
            Object idFilaOriginal = table.getValueAt(posicionFilaOriginal, 0);
            Object idFilaNueva = table.getValueAt(posicionFilaNUeva, 0);
            
             if(new ConexionBD().GUARDAR("UPDATE tensiones SET id='"+(posicionFilaNUeva+1)+"' WHERE id='"+idFilaOriginal+"' AND primario='"+table.getValueAt(posicionFilaOriginal, 1)+"' AND secundario='"+table.getValueAt(posicionFilaOriginal, 2)+"' AND terciario='"+table.getValueAt(posicionFilaOriginal, 3)+"' ")){
                if(new ConexionBD().GUARDAR("UPDATE tensiones SET id='"+(posicionFilaOriginal+1)+"' WHERE id='"+idFilaNueva+"' AND primario='"+table.getValueAt(posicionFilaNUeva, 1)+"' AND secundario='"+table.getValueAt(posicionFilaNUeva, 2)+"' AND terciario='"+table.getValueAt(posicionFilaNUeva, 3)+"' ")){
                    ((DefaultTableModel)table.getModel()).moveRow(rowFrom, rowFrom, index);            
                    Tensiones.cargar();
                }
             }                        
            if (index > rowFrom)
               index--;
            target.getSelectionModel().addSelectionInterval(index, index);
            return true;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

   @Override
   protected void exportDone(JComponent c, Transferable t, int act) {
      if ((act == TransferHandler.MOVE) || (act == TransferHandler.NONE)) {
         table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

}
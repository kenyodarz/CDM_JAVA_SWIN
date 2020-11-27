package Dialogos;

import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author AUXPLANTA
 */
public class DialogoTrafosRepetidos extends javax.swing.JDialog {

    public DefaultTableModel ModeloRepetidos;
    private int IDTRAFO = -1;
    
    public DialogoTrafosRepetidos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        String data[][] = {};
        String col[] = {"ITEM","LOTE","SERIE","N° EMPRESA","CLIENTE"};
        ModeloRepetidos = new DefaultTableModel(data, col){
            @Override
            public boolean isCellEditable(int row, int column){                
                return false;                
            }
        };
        jTable1.setModel(ModeloRepetidos);
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable1.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
    }

    public void CargarDatos(ResultSet rs){
        try {            
            while(rs.next()){
                ModeloRepetidos.addRow(new Object[]{
                    rs.getInt("idtransformador"),
                    rs.getString("lote"),
                    rs.getString("numeroserie"),
                    rs.getString("numeroempresa"),
                    rs.getString("nombrecliente")                    
                });
            }
            new JTableAutoResizeColumn.TableColumnAdjuster(jTable1).adjustColumns();
            jTable1.setRowSelectionInterval(0, 0);
            jTable1.setColumnSelectionInterval(0, jTable1.getColumnCount()-1);
        } catch (SQLException ex) {
            Logger.getLogger(DialogoTrafosRepetidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object getT(int col){        
        return jTable1.getValueAt(jTable1.getSelectedRow(), col);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jTable1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setGridColor(new java.awt.Color(227, 227, 227));
        jTable1.setRowHeight(22);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTable1KeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyTyped
        if(evt.getKeyChar()==10){
            setIDTRAFO((int)getT(0));
            dispose();
        }else if(evt.getKeyChar()==27){
            dispose();
        }
    }//GEN-LAST:event_jTable1KeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DialogoTrafosRepetidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogoTrafosRepetidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogoTrafosRepetidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoTrafosRepetidos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogoTrafosRepetidos dialog = new DialogoTrafosRepetidos(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    public int getIDTRAFO() {
        return IDTRAFO;
    }

    public void setIDTRAFO(int IDTRAFO) {
        this.IDTRAFO = IDTRAFO;
    }
}

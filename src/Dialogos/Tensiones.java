package Dialogos;

import DragRowJTableTransferHandler.DragRowJTableTransferHandler;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionBD;

/**
 *
 * @author AUXPLANTA
 */
public class Tensiones extends javax.swing.JDialog {
    
    static final ConexionBD conexion = new ConexionBD();
    
    public Tensiones(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        initComponents();
//        ResultSet rs = Conexion2.CONSULTAR("SELECT DISTINCT(tension) FROM datosentrada");
//        try {
//            String tension[] = null;
//            while(rs.next()){
//                if(rs.getString("tension").length()>5){
//                    tension = rs.getString("tension").split("/");
////                    if(tension.length==2){
////                        tension[2] = "0";
////                    }
//                    System.out.println("ANALIZANDO TENSUOn. "+rs.getString("tension"));
//                    if(new ConexionBD().GUARDAR("INSERT INTO tensiones (primario,secundario,terciario) VALUES ('"+tension[0]+"','"+tension[1]+"','"+((tension.length==2)?"0":tension[2])+"') ")){
//
//                    }
//                }                
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Tensiones.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        btnGuardar.addActionListener((ActionEvent e) -> {
            String pri = p.getText().trim();
            String sec = s.getText().trim();
            String ter = t.getText().trim();
            if(new ConexionBD().GUARDAR("INSERT INTO tensiones (primario,secundario,terciario) VALUES ('"+pri+"','"+sec+"','"+ter+"') ")){
                p.setText(null);s.setText(null);t.setText(null);
                cargar();
            }
        });
        cargar();
    }
    
    static DefaultTableModel model;
    public static void cargar(){
        try {
            String data[][]={};
            String col[]={"ID","PRIMARIA","SECUNDARIA","TERCIARIA"};
            model = new DefaultTableModel(data, col);
            jTable1.setModel(model);
            jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//            jTable1.setCellSelectionEnabled(true);
            jTable1.setTransferHandler(new DragRowJTableTransferHandler(jTable1));
            
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT * FROM tensiones ORDER BY id ASC");
            while(rs.next()){
                model.addRow(new String[]{""+rs.getInt("id"),rs.getString("primario"),rs.getString("secundario"),rs.getString("terciario")});
            }
            conexion.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(Tensiones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        p = new CompuChiqui.JTextFieldPopup();
        s = new CompuChiqui.JTextFieldPopup();
        t = new CompuChiqui.JTextFieldPopup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnGuardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        p.setPlaceholder("Primaria");

        s.setPlaceholder("Secundaria");

        t.setPlaceholder("Terciaria");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Primaria", "Secundaria", "Terciaria"
            }
        ));
        jTable1.setDragEnabled(true);
        jTable1.setDropMode(javax.swing.DropMode.INSERT_ROWS);
        jScrollPane1.setViewportView(jTable1);

        btnGuardar.setText("Guardar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(p, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(s, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(t, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(s, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(t, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tensiones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Tensiones dialog = new Tensiones(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnGuardar;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JTable jTable1;
    private CompuChiqui.JTextFieldPopup p;
    private CompuChiqui.JTextFieldPopup s;
    private CompuChiqui.JTextFieldPopup t;
    // End of variables declaration//GEN-END:variables
}

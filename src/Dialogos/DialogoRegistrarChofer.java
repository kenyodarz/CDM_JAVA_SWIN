package Dialogos;

import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modelo.ConexionBD;

public class DialogoRegistrarChofer extends javax.swing.JDialog{

    private DefaultTableModel model;
    public int con = 0;
    
    ConexionBD conexion = new ConexionBD();
    
    TableRowSorter rowSorter;
    
    TableColumnAdjuster ajustarColumna;
  
    public DialogoRegistrarChofer(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        initComponents();
        
        ajustarColumna = new TableColumnAdjuster(TablaListaConductores);
        
        Cargar("");     
    }
    
    public void Cargar(String t){
        TablaListaConductores.setRowSorter(null);
        String data[][]={};
        String col[]={"N°","CEDULA N°","NOMBRE"};
        model = new DefaultTableModel(data, col);
        TablaListaConductores.setModel(model);
        TablaListaConductores.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        String sql = "SELECT * FROM conductor ORDER BY nombreconductor";
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(sql);
        try {
            while(rs.next()){
                model.insertRow(rs.getRow()-1, new Object[]{
                    rs.getInt("idconductor"),
                    rs.getString("cedulaconductor"),
                    rs.getString("nombreconductor")
                });                
            }
            
            rowSorter = new TableRowSorter(model);
            TablaListaConductores.setRowSorter(rowSorter);
            
            TablaListaConductores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            TablaListaConductores.setCellSelectionEnabled(true);
            TablaListaConductores.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

            ajustarColumna.adjustColumns();

            TablaListaConductores.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
            
            model.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if(e.getType() == TableModelEvent.UPDATE){
                        String id = model.getValueAt(e.getFirstRow(), 0).toString();
                        String cedula = model.getValueAt(e.getFirstRow(), 0).toString();
                        String nombre = model.getValueAt(e.getFirstRow(), 0).toString();
                        if(conexion.GUARDAR("UPDATE conductor SET cedulaconductor='"+cedula+"' , nombreconductor='"+nombre+"' WHERE idconductor='"+id+"' ")){
                            
                        }
                    }
                }
            });                        
        } catch (SQLException ex) {
            Logger.getLogger(DialogoRegistrarChofer.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR AL CARGAR LOS DATOS DE LOS CONDUCTORES EN LA TABLA\n"+ex, "ERROR", JOptionPane.ERROR_MESSAGE);
        }finally{
            conexion.CERRAR();
        }
    }
    
    void habilitarCajas(boolean estado){
        cjcedula.setEnabled(estado);
        cjnombre.setEnabled(estado);
        cjcedula.grabFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnNuevo = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnguardar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaListaConductores = new javax.swing.JTable();
        cjbuscar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cjcedula = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cjnombre = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Conductores");

        jToolBar1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setRollover(true);

        btnNuevo.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.setFocusable(false);
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNuevo);
        jToolBar1.add(jSeparator1);

        btnguardar.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        btnguardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnguardar.setText("Guardar");
        btnguardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnguardar);
        jToolBar1.add(jSeparator2);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        jButton2.setText("Borrar");
        jButton2.setToolTipText("Eliminar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        TablaListaConductores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(TablaListaConductores);

        cjbuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjbuscarKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel4.setText("Buscar:");
        jLabel4.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel1.setText("Cedula N°:");
        jLabel1.setToolTipText("");

        cjcedula.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel2.setText("Nombre:");
        jLabel2.setToolTipText("");

        cjnombre.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjnombre))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjbuscar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cjcedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cjnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cjbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
        if(!cjcedula.getText().isEmpty()){
            if(!cjnombre.getText().isEmpty()){
                    String cedula = cjcedula.getText().toUpperCase(), nombre = cjnombre.getText().toUpperCase();
                    if(conexion.GUARDAR("INSERT INTO conductor (cedulaconductor,nombreconductor) VALUES ('"+cedula+"','"+nombre+"') ")){
                        JOptionPane.showMessageDialog(null, "CONDUCTOR REGISTRADO CON EXITO","REGISTRO EXITOSO",JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/bien.png")));
                        cjcedula.setText(null);cjnombre.setText(null);
                        Cargar("");
                        habilitarCajas(false);
                    }
            }else{JOptionPane.showMessageDialog(null, "EL NOMBRE SE ENCUENTRA VACIO","CAMPO NOMBRE VACIO",JOptionPane.ERROR_MESSAGE);}
        }else{JOptionPane.showMessageDialog(null, "LA CEDULA SE ENCUENTRA VACIA","CAMPO CEDULA VACIO",JOptionPane.ERROR_MESSAGE);}
    }//GEN-LAST:event_btnguardarActionPerformed

    private void cjbuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjbuscarKeyReleased
        rowSorter.setRowFilter(RowFilter.regexFilter(cjbuscar.getText().toUpperCase(), 2));
    }//GEN-LAST:event_cjbuscarKeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    try{
        if(TablaListaConductores.getSelectedRow() >= 0){
            if(JOptionPane.showConfirmDialog(this, "Seguro que desea eliminar el conductor seleccionado ? ","Confirme",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION && 
                    conexion.GUARDAR("DELETE FROM conductor WHERE idconductor="+model.getValueAt(TablaListaConductores.getSelectedRow(), 0)+" ")){
                JOptionPane.showMessageDialog(null, "CONDUCTOR ELIMINADO CON EXITO","CONDUCTOR HA SIDO ELIMINADO",JOptionPane.INFORMATION_MESSAGE,new ImageIcon(getClass().getResource("/recursos/images/bien.png")));
                Cargar("");
            }
        }else{
            JOptionPane.showMessageDialog(null, "SELECCIONE EL CONDUCTOR QUE DESEA ELIMINAR","SELECCIONE CONDUCTOR",JOptionPane.INFORMATION_MESSAGE);
        }
    }catch(Exception e){
        JOptionPane.showMessageDialog(null, "OCURRIO UN ERROR INESPERADO\nINTENTE NUEVAMENTE O CONTACTE CON EL ADMINISTRADOR DEL SISTEMA\n"+e,"ERROR",JOptionPane.ERROR_MESSAGE);
        Logger.getLogger(DialogoRegistrarChofer.class.getName()).log(Level.SEVERE, null, e);
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        habilitarCajas(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

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
            java.util.logging.Logger.getLogger(DialogoRegistrarChofer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarChofer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarChofer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarChofer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogoRegistrarChofer dialog = new DialogoRegistrarChofer(new javax.swing.JFrame(), true);
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
    private javax.swing.JTable TablaListaConductores;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnguardar;
    private javax.swing.JTextField cjbuscar;
    private javax.swing.JTextField cjcedula;
    private javax.swing.JTextField cjnombre;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}

package Dialogos;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.ConexionBD;

public class DialogoRegistrarMarcaDeTransformadores extends javax.swing.JDialog {

    private DefaultTableModel model;
    int con = 0;

    ConexionBD conexion = new ConexionBD();

    public DialogoRegistrarMarcaDeTransformadores(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Cargar("");
        cjmarcatransf.grabFocus();
    }

    public void Cargar(String t) {
        try {
            String data[][] = {};
            String col[] = {"ID", "MARCA"};
            model = new DefaultTableModel(data, col) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            TablaMarcas.setModel(model);
            String sql = "SELECT * FROM marcatransformador WHERE marca_marcatransformador ILIKE '%" + t + "%' ORDER BY id_marcatransformador DESC";
//            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR(sql);

                while (rs.next()) {
                    model.insertRow(con, new Object[]{});
                    model.setValueAt(rs.getInt(1), con, 0);
                    model.setValueAt(rs.getString(2), con, 1);
                }
        } catch (Exception ex) {
            Logger.getLogger(DialogoRegistrarMarcaDeTransformadores.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void M(String m, Icon i) {
        JOptionPane.showMessageDialog(this, m, "Aviso", JOptionPane.INFORMATION_MESSAGE, i);
    }

    public void Guardar() {
        if (!cjmarcatransf.getText().isEmpty()) {
            String nombre = cjmarcatransf.getText().trim().toUpperCase();
            String GuardarNombre = "INSERT INTO marcatransformador (marca_marcatransformador) VALUES ('" + nombre + "') ";
            if (JOptionPane.showConfirmDialog(this, "Seguro desea guardar la nueva marca de transformador ?") == JOptionPane.YES_OPTION) {
                if (conexion.GUARDAR(GuardarNombre)) {
                    modelo.Metodos.M("Registrado Exitosamente", "bien.png");
                    cjmarcatransf.setText(null);
                    Cargar("");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SubMenuTabla = new javax.swing.JPopupMenu();
        EliminarTipoDeTransformador = new javax.swing.JMenuItem();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        TablaMarcas = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cjmarcatransf = new javax.swing.JTextField();
        cjbuscarmarcatransf = new javax.swing.JTextField();

        EliminarTipoDeTransformador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        EliminarTipoDeTransformador.setText("Eliminar");
        EliminarTipoDeTransformador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarTipoDeTransformadorActionPerformed(evt);
            }
        });
        SubMenuTabla.add(EliminarTipoDeTransformador);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Marca De Transformadores");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Ingrese una marca de transformador"));

        TablaMarcas.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        TablaMarcas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        TablaMarcas.setRowHeight(25);
        TablaMarcas.setShowHorizontalLines(false);
        TablaMarcas.setShowVerticalLines(false);
        TablaMarcas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaMarcasMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(TablaMarcas);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Buscar:");

        cjmarcatransf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjmarcatransfKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjmarcatransfKeyTyped(evt);
            }
        });

        cjbuscarmarcatransf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjbuscarmarcatransfKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjbuscarmarcatransf)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9))
                    .addComponent(cjmarcatransf))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cjmarcatransf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cjbuscarmarcatransf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void TablaMarcasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaMarcasMouseClicked
        if (TablaMarcas.getSelectedRow() != -1) {
            TablaMarcas.setComponentPopupMenu(SubMenuTabla);
        }
    }//GEN-LAST:event_TablaMarcasMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Cargar("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void EliminarTipoDeTransformadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarTipoDeTransformadorActionPerformed
        if (TablaMarcas.getSelectedRow() != -1) {
            String sql = "DELETE FROM marcatransformador WHERE id_marcatransformador='" + TablaMarcas.getValueAt(TablaMarcas.getSelectedRow(), 0) + "' ";
            if (JOptionPane.showConfirmDialog(this, "Desea eliminar el registro seleccionado", "Marca de Transformador: " + TablaMarcas.getValueAt(TablaMarcas.getSelectedRow(), 1), JOptionPane.DEFAULT_OPTION) == JOptionPane.YES_OPTION) {
                if (conexion.GUARDAR(sql)) {
                    modelo.Metodos.M("Registro eliminado", "bien.png");
                }
                Cargar("");
            }
        }
    }//GEN-LAST:event_EliminarTipoDeTransformadorActionPerformed

    private void cjmarcatransfKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjmarcatransfKeyTyped
        if (evt.getKeyChar() == 10) {
            Guardar();
        }
    }//GEN-LAST:event_cjmarcatransfKeyTyped

    private void cjbuscarmarcatransfKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjbuscarmarcatransfKeyTyped
        Cargar(cjbuscarmarcatransf.getText());
    }//GEN-LAST:event_cjbuscarmarcatransfKeyTyped

    private void cjmarcatransfKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjmarcatransfKeyPressed
        if (evt.getKeyCode() == 27) {
            dispose();
        }
    }//GEN-LAST:event_cjmarcatransfKeyPressed

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
            java.util.logging.Logger.getLogger(DialogoRegistrarMarcaDeTransformadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarMarcaDeTransformadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarMarcaDeTransformadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoRegistrarMarcaDeTransformadores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogoRegistrarMarcaDeTransformadores dialog = new DialogoRegistrarMarcaDeTransformadores(new javax.swing.JFrame(), true);
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
    private javax.swing.JMenuItem EliminarTipoDeTransformador;
    private javax.swing.JPopupMenu SubMenuTabla;
    private javax.swing.JTable TablaMarcas;
    private javax.swing.JTextField cjbuscarmarcatransf;
    private javax.swing.JTextField cjmarcatransf;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane6;
    // End of variables declaration//GEN-END:variables
}

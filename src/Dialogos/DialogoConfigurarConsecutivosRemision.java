package Dialogos;

import modelo.Metodos;
import java.sql.ResultSet;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import modelo.ConexionBD;

public class DialogoConfigurarConsecutivosRemision extends javax.swing.JDialog {   
    
    DefaultListModel modelo = new DefaultListModel();
    String tipo = null;
    
    ConexionBD conexion = new ConexionBD();
            
    public DialogoConfigurarConsecutivosRemision(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
            
        Cargar();
    }

    void Cargar(){
        try{
            modelo = new DefaultListModel();
            String sql = " SELECT numero_remision FROM remision WHERE tipo_remision='"+combo_tiporemision.getSelectedItem()+"' ";
            sql += ((combo_empresa.getSelectedItem().toString().equals("CDM"))?
                    "AND (empresa_remision='CDM' OR empresa_remision='MEDIDORES') ":
                    "AND empresa_remision='"+combo_empresa.getSelectedItem().toString()+"' ");
            sql += " ORDER BY numero_remision DESC ";
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR(sql);
            while(rs.next()){
                modelo.addElement(rs.getString("numero_remision"));
            }             
            listas.setModel(modelo); 
            rs = conexion.CONSULTAR(" SELECT last_value FROM "+getTipo());
            rs.next();
            spinerRemision.setValue(rs.getInt("last_value"));

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "ERROR AL CARGAR LOS CONSECUTIVOS DE LAS REMISIONES.\n"+e);
            java.util.logging.Logger.getLogger(DialogoConfigurarConsecutivosRemision.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }finally{
            conexion.CERRAR();
        }
    }
     
     String getTipo(){
         String tipo = null;
         switch(combo_tiporemision.getSelectedIndex()){
                case 0: 
                    switch(combo_empresa.getSelectedIndex()){
                        case 0: tipo = "cdmretorno"; break;
                        case 1: tipo = tipo = "consorcioretorno"; break;                            
                    }
                    break;
                case 1: 
                    switch(combo_empresa.getSelectedIndex()){
                        case 0: tipo = "cdmsinretorno"; break;
                        case 1: tipo = tipo = "consorciosinretorno"; break;
                    }
                    break;                    
            }
         return tipo;
     }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        combo_tiporemision = new javax.swing.JComboBox();
        lblimagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listas = new javax.swing.JList();
        spinerRemision = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        combo_empresa = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel1.setText("Tipo de remision:");

        combo_tiporemision.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        combo_tiporemision.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CON RETORNO", "SIN RETORNO" }));
        combo_tiporemision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_tiporemisionActionPerformed(evt);
            }
        });

        lblimagen.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblimagen.setText("Consecutivo Actual:");

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel2.setText("Ultimos Registrados:");

        jScrollPane1.setViewportView(listas);

        spinerRemision.setModel(new javax.swing.SpinnerNumberModel());
        spinerRemision.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinerRemisionStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel3.setText("Empresa:");

        combo_empresa.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        combo_empresa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CDM", "CONSORCIO" }));
        combo_empresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_empresaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblimagen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinerRemision))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(combo_empresa, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(combo_tiporemision, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(combo_tiporemision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(combo_empresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblimagen)
                    .addComponent(spinerRemision, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void combo_tiporemisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_tiporemisionActionPerformed
        Cargar();
    }//GEN-LAST:event_combo_tiporemisionActionPerformed

    private void spinerRemisionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinerRemisionStateChanged
        try{
            conexion.conectar();
            if(conexion.CONSULTAR(" SELECT setval('"+getTipo()+"', '"+Integer.parseInt(spinerRemision.getValue().toString().replace(".", ""))+"') ").next()){
                lblimagen.setIcon(new ImageIcon(getClass().getResource("/recursos/images/bien_pequeno.png")));
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR AJUSTAR LOS CONSECUTIVOS DE REMISION\n"+e);
            java.util.logging.Logger.getLogger(DialogoConfigurarConsecutivosRemision.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }finally{
            lblimagen.setIcon(null);
            lblimagen.setText("Consecutivo Actual:");
            conexion.CERRAR();
        }
    }//GEN-LAST:event_spinerRemisionStateChanged

    private void combo_empresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_empresaActionPerformed
        Cargar();        
    }//GEN-LAST:event_combo_empresaActionPerformed

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
            java.util.logging.Logger.getLogger(DialogoConfigurarConsecutivosRemision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DialogoConfigurarConsecutivosRemision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DialogoConfigurarConsecutivosRemision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DialogoConfigurarConsecutivosRemision.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogoConfigurarConsecutivosRemision dialog = new DialogoConfigurarConsecutivosRemision(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox combo_empresa;
    private javax.swing.JComboBox combo_tiporemision;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblimagen;
    private javax.swing.JList listas;
    private javax.swing.JSpinner spinerRemision;
    // End of variables declaration//GEN-END:variables
}

package Dialogos;

import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.ConexionBD;

/**
 *
 * @author AUXPLANTA
 */
public class PersonalDeFirmas extends javax.swing.JDialog {

    ConexionBD con = new ConexionBD();
    boolean CARGADO = false;
    
    public PersonalDeFirmas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargarFirmas();
    }
    
    final void cargarFirmas(){
        jComboBox1.removeAllItems();
        con.conectar();
        ResultSet rs = con.CONSULTAR("SELECT * FROM personal");
        try {
            while(rs.next()){
                jComboBox1.addItem(new Personal(
                        rs.getInt("idpersonal"), 
                        rs.getString("nombre"), 
                        rs.getString("cargo"), 
                        rs.getBytes("firma")));
            }
            CARGADO = true;
        } catch (SQLException ex) {
            Logger.getLogger(PersonalDeFirmas.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        panelfirma = new compuchiqui.JPanelCamara();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        panelfirma.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelfirmaLayout = new javax.swing.GroupLayout(panelfirma);
        panelfirma.setLayout(panelfirmaLayout);
        panelfirmaLayout.setHorizontalGroup(
            panelfirmaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelfirmaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelfirmaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelfirmaLayout.setVerticalGroup(
            panelfirmaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelfirmaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, 0, 380, Short.MAX_VALUE)
                    .addComponent(panelfirma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelfirma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if(CARGADO){
            if(evt.getStateChange() == ItemEvent.SELECTED){
                Personal p = (Personal) evt.getItem();
                if(p.getFirma() != null){
                    panelfirma.setImagen(modelo.Metodos.byteToBufferedImage(p.getFirma()));
                }else{
                    panelfirma.setImagen(null);
                    modelo.Metodos.M("NO TIENE FIRMA REGISTRADA", "advertencia.png");
                }
            }            
        }        
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(con.GUARDAR("UPDATE personal SET firma=null WHERE idpersonal="+jComboBox1.getItemAt(jComboBox1.getSelectedIndex()).getId()+" ")){
            cargarFirmas();
            modelo.Metodos.M("SE HA ELIMINADO LA FIRMA A LA PERSONA SELECCIONADA.", "bien.png");
            panelfirma.setImagen(null);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         try {
            Personal p = jComboBox1.getItemAt(jComboBox1.getSelectedIndex());
            int id = p.getId();
            Image imagefirma = (BufferedImage) panelfirma.getImagen();
            BufferedImage bfimagefirma = modelo.Metodos.imageToBufferedImage(imagefirma);
            byte[] bytefirma = modelo.Metodos.BufferedImageToByteArray(bfimagefirma);
            if(imagefirma != null){
                Connection conex = con.conectar();
                PreparedStatement pst = conex.prepareStatement("UPDATE personal SET firma=? WHERE idpersonal="+id+" ");
                InputStream is =  new  ByteArrayInputStream(bytefirma);
                pst.setBinaryStream(1, is, bytefirma.length);
                if(pst.executeUpdate()>0){
                    modelo.Metodos.M("LA FIRMA HA SIDO ACTUALIZADA.", "bien.png");
                }
            }else{
                modelo.Metodos.M("NO SE HA INSERTADO UNA IMAGEN.", "error.png");
            }            

        } catch (IOException | SQLException e) {
            
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    class Personal{

        private int id;
        private String nombre;
        private String cargo;
        private byte[] firma;
        
        public Personal(int id, String nombre, String cargo, byte[] firma) {
            this.id = id;
            this.nombre = nombre;
            this.cargo = cargo;
            this.firma = firma;
        }        

        @Override
        public String toString() {
            return nombre + " - " + cargo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCargo() {
            return cargo;
        }

        public void setCargo(String cargo) {
            this.cargo = cargo;
        }

        public byte[] getFirma() {
            return firma;
        }

        public void setFirma(byte[] firma) {
            this.firma = firma;
        }
    }
    
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
            java.util.logging.Logger.getLogger(PersonalDeFirmas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PersonalDeFirmas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PersonalDeFirmas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PersonalDeFirmas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PersonalDeFirmas dialog = new PersonalDeFirmas(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<Personal> jComboBox1;
    private compuchiqui.JPanelCamara panelfirma;
    // End of variables declaration//GEN-END:variables
}

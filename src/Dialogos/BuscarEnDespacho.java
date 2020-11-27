package Dialogos;

import javax.swing.JTable;
import view.DespachoARemision;

public class BuscarEnDespacho extends javax.swing.JDialog {
    
    /* DECLARACION DE VARIABLES NO MODIFICAR */
    private JTable tabla; 
    int col = 0;
    int row = 0;
    
    //SE CARGA EL MODAL
    public BuscarEnDespacho(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cjbuscar.grabFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cjbuscar = new CompuChiqui.JTextFieldPopup();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel1.setText("Ingrese el valor a buscar:");

        cjbuscar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjbuscar.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N

        jButton1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/buscar.png"))); // NOI18N
        jButton1.setText("Buscar siguiente");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cjbuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cjbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /* EVENTO LANZADO AL PRESIONAR EL BOTON "Buscar Siguiente" */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        /* DECLARACION DE VARIBLES LOCALES , ¡¡¡NO MODIFIFICAR!!! */
        String texto = cjbuscar.getText().trim();//REMOVEMOS EL ESPACIO EXTRA DESPUES DEL TEXTO INGRESADO
        boolean esta = false;
        
        /* SI EL CAMPO NO ESTA VACIO O ES DIFERENTE DE NULL ENTRA EN LA CONDICIONAL */
        if(!cjbuscar.getText().isEmpty() || cjbuscar.getText() != null){
            /* INICIAMOS EN LA FILA '0' E ITERANDO CADA FILA */
            for (int i = row; i < tabla.getRowCount(); i++){//ENVIAMOS UN MENSAJE A LA CONSOLA
                System.out.print("Fila: "+i+" ->");
                /* INICIAMOS EN LA COLUMNA '0' E ITERANDO CADA COLUMNA */
                for (int j = col; j < tabla.getColumnCount(); j++){
                    /* OBTENEMOS EL OBJETO EN LA POSICION (i: fila y j: columna */
                    Object value = tabla.getValueAt(i, j);
                    /* SE EVALUA SI EL OBJETO ES DIFERENTE DE NULO E IGUAL AL TEXTO OBTENIDO DE BOTON */
                    if(null!=value&&texto.equals(value)){
                        modelo.Metodos.M("SI", "bien.png");
                        esta = true;                        
                        ((DespachoARemision)getOwner()).tabla.setColumnSelectionInterval(j, j);
                        col = (j==tabla.getColumnCount()-1)?0:j+1;
                        break;
                    }
                    System.out.println(" Col: "+j+" ->");
                }
                if(esta){
                    ((DespachoARemision)getOwner()).tabla.setRowSelectionInterval(i, i);
                    if(i==tabla.getRowCount()-1){
                        row = 0;
                    }else{
                        row = i;
                    }
                    row = (col==tabla.getColumnCount()&&i==tabla.getRowCount()-1)?0:row+1;
                    System.out.println("----------------Col = "+col+" Row: "+row);
                    break;
                }                
            }            
        }else{
            cjbuscar.grabFocus();
        }                
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(BuscarEnDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BuscarEnDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BuscarEnDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BuscarEnDespacho.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                BuscarEnDespacho dialog = new BuscarEnDespacho(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private CompuChiqui.JTextFieldPopup cjbuscar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    public JTable getTabla() {
        return tabla;
    }

    public void setTabla(JTable tabla) {
        this.tabla = tabla;
        
    }
}

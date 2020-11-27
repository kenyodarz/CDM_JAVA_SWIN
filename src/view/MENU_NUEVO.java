package view;

import Dialogos.DialogoRegistrarHerramienta;
import paneles.PanelControl;
import paneles.PanelRemisiones;
import paneles.PanelDespachos;
import javax.swing.ImageIcon;

public class MENU_NUEVO extends javax.swing.JFrame{
    
    //Declaracion de los Paneles - No modificar
    paneles.PanelLotes2 panelLotes = new paneles.PanelLotes2();
    paneles.PanelDespachos panelDespachos = new PanelDespachos();
    paneles.PanelRemisiones panelRemisiones = new PanelRemisiones();
    paneles.PanelControl panelControl = new PanelControl();
    // Fin de la Declaracion de los Paneles

    /** CONSTRUCTOR DE LA CLASE */
    public MENU_NUEVO(){
        initComponents();        
        
        //Iniciar la Ventama Maximizada
        setExtendedState(MAXIMIZED_BOTH);
        
        //Asignacion de nombres mostrados en la Vista
        panelLotes.setName("Lotes");
        panelDespachos.setName("Despachos");
        panelRemisiones.setName("Remisiones");
        panelControl.setName("Control Total");
        
        //Indexacion de los diferentes paneles en el JTabbedPane
        todo.add(panelLotes, 0);
        todo.add(panelDespachos, 1);
        todo.add(panelRemisiones, 2);
        todo.add(panelControl, 3);
        
        //Asinacion de iconos 
        todo.setIconAt(0, new ImageIcon(getClass().getResource("/recursos/images/lote.png")));
        todo.setIconAt(1, new ImageIcon(getClass().getResource("/recursos/images/despachos.png")));
        todo.setIconAt(2, new ImageIcon(getClass().getResource("/recursos/images/remisiones.png")));
        todo.setIconAt(3, new ImageIcon(getClass().getResource("/recursos/images/control.png")));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        todo = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        menuNuevo = new javax.swing.JMenu();
        menuNuevoLote = new javax.swing.JMenuItem();
        menuRemisiones = new javax.swing.JMenu();
        menuRemisionCDM = new javax.swing.JMenu();
        conRetornoCDM = new javax.swing.JMenuItem();
        sinRetornoCDM = new javax.swing.JMenuItem();
        menuRemisionConsorcio = new javax.swing.JMenu();
        conRetornoConsorcio = new javax.swing.JMenuItem();
        sinRetonroConsorcio = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SOFTWARE CDM");
        setIconImage(new ImageIcon(getClass().getResource("/recursos/images/logo.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        todo.setFont(new java.awt.Font("Enter Sansman", 1, 14)); // NOI18N

        menuArchivo.setText("Archivo");
        menuArchivo.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        menuNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/add.png"))); // NOI18N
        menuNuevo.setText("Nuevo");
        menuNuevo.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N

        menuNuevoLote.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        menuNuevoLote.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        menuNuevoLote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        menuNuevoLote.setText("Lote");
        menuNuevoLote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNuevoLoteActionPerformed(evt);
            }
        });
        menuNuevo.add(menuNuevoLote);

        menuRemisiones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        menuRemisiones.setText("Remision");
        menuRemisiones.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N

        menuRemisionCDM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        menuRemisionCDM.setText("CDM");
        menuRemisionCDM.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N

        conRetornoCDM.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        conRetornoCDM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        conRetornoCDM.setText("CON RETORNO");
        conRetornoCDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conRetornoCDMActionPerformed(evt);
            }
        });
        menuRemisionCDM.add(conRetornoCDM);

        sinRetornoCDM.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        sinRetornoCDM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        sinRetornoCDM.setText("SIN RETORNO");
        sinRetornoCDM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sinRetornoCDMActionPerformed(evt);
            }
        });
        menuRemisionCDM.add(sinRetornoCDM);

        menuRemisiones.add(menuRemisionCDM);

        menuRemisionConsorcio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        menuRemisionConsorcio.setText("CDM SERVICIOS");
        menuRemisionConsorcio.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N

        conRetornoConsorcio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        conRetornoConsorcio.setText("CON RETORNO");
        conRetornoConsorcio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conRetornoConsorcioActionPerformed(evt);
            }
        });
        menuRemisionConsorcio.add(conRetornoConsorcio);

        sinRetonroConsorcio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/mini logo.png"))); // NOI18N
        sinRetonroConsorcio.setText("SIN RETORNO");
        sinRetonroConsorcio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sinRetonroConsorcioActionPerformed(evt);
            }
        });
        menuRemisionConsorcio.add(sinRetonroConsorcio);

        menuRemisiones.add(menuRemisionConsorcio);

        menuNuevo.add(menuRemisiones);

        menuArchivo.add(menuNuevo);

        jMenuBar1.add(menuArchivo);

        jMenu1.setText("Editar");
        jMenu1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/user.png"))); // NOI18N
        jMenuItem1.setText("Clientes");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/cliente.png"))); // NOI18N
        jMenuItem2.setText("Conductores");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Herramientas CDM SERVICIOS");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(todo, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(todo, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    /**
     * METODO QUE CARGA LA VENTADA Y LA INICIALIZA EJECUTANDO LAS ACCIONES DE CARGUE DE CADA PANEL
     */
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        (new Thread(){
            @Override
            public void run(){
                panelLotes.btnCargarLotes.doClick();
                panelDespachos.btnCargarDespachos.doClick();
                panelRemisiones.btnCargarRemisiones.doClick();
                panelControl.btnRefrescar.doClick();
            }
        }).start();        
    }//GEN-LAST:event_formWindowOpened
    /** METODO QUE CARGA MENU DE ENTRADA DE TRANSFROMADORES */
    private void menuNuevoLoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNuevoLoteActionPerformed
        EntradaDeTrafos et = new EntradaDeTrafos();
        et.setVisible(true);
    }//GEN-LAST:event_menuNuevoLoteActionPerformed
    /** METODO QUE MUESTRA EL DIALOGO PARA REGISTAR CLIENTES */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        Dialogos.DialogoRegistrarCliente drcli = new Dialogos.DialogoRegistrarCliente(this, rootPaneCheckingEnabled);
        drcli.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    /** METODO QUE MUESTRA EL DIALOGO PARA REGISTAR CHOFERES */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        Dialogos.DialogoRegistrarChofer drch = new Dialogos.DialogoRegistrarChofer(this, rootPaneCheckingEnabled);
        drch.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    /** METODO PARA MOSTRAR EL DIALOGO CON LAS REMISIONES CDM CON RETORNO */
    private void conRetornoCDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conRetornoCDMActionPerformed
        REMISIONESCDM rp = new REMISIONESCDM();
        rp.setCONSECUTIVO_EMPRESA("cdmretorno");
        rp.setTIPO("CON RETORNO");
        rp.setREPORTE("REMISIONCDM");
        rp.mostrarConsecutivoActual();
        rp.setVisible(true);
    }//GEN-LAST:event_conRetornoCDMActionPerformed
    /** METODO PARA MOSTRAR EL DIALOGO CON LAS REMISIONES CDM SIN RETORNO */
    private void sinRetornoCDMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sinRetornoCDMActionPerformed
        REMISIONESCDM rp = new REMISIONESCDM();
        rp.setCONSECUTIVO_EMPRESA("cdmsinretorno");
        rp.setTIPO("SIN RETORNO");
        rp.setREPORTE("REMISIONCDM");
        rp.mostrarConsecutivoActual();
        rp.setVisible(true);
    }//GEN-LAST:event_sinRetornoCDMActionPerformed
    /** METOCO PARA CARGAR DIALOGO DE REMISIONES DEL CONSORCIO SIN RETORNO */
    private void conRetornoConsorcioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conRetornoConsorcioActionPerformed
        REMISIONESCDM rp = new REMISIONESCDM();
        rp.setCONSECUTIVO_EMPRESA("consorcioretorno");
        rp.setTIPO("CON RETORNO");
        rp.setREPORTE("CONSORCIO_HERRAMIENTAS");
        rp.mostrarConsecutivoActual();
        rp.comboempresa.setSelectedItem("CONSORCIO");
        rp.setVisible(true);
        // ESTA LINEA NO SE EJECUTA ------->  rp.cargarTablaHerramientas();
    }//GEN-LAST:event_conRetornoConsorcioActionPerformed
    /** METOODO PARA CARGAR EL DIALOGO DE REMISIONES DEL CONSORCIO CON RETORNO */
    private void sinRetonroConsorcioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sinRetonroConsorcioActionPerformed
        REMISIONESCDM rp = new REMISIONESCDM();
        rp.setCONSECUTIVO_EMPRESA("consorciosinretorno");
        rp.setTIPO("SIN RETORNO");
        rp.setREPORTE("CONSORCIO_HERRAMIENTAS");
        rp.mostrarConsecutivoActual();
        rp.comboempresa.setSelectedItem("CONSORCIO");
        rp.setVisible(true);
    }//GEN-LAST:event_sinRetonroConsorcioActionPerformed
    /** METODO QUE CARGA EL DIALOGO REGISTRAR HERRAMIENTAS */
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        DialogoRegistrarHerramienta drh = new DialogoRegistrarHerramienta(this, false);
        drh.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * @param args the command line arguments
     */
    // METODO MAIN PENDIENDE ESTUDIO @JMINA
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
            java.util.logging.Logger.getLogger(MENU_NUEVO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MENU_NUEVO().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem conRetornoCDM;
    private javax.swing.JMenuItem conRetornoConsorcio;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenu menuNuevo;
    private javax.swing.JMenuItem menuNuevoLote;
    private javax.swing.JMenu menuRemisionCDM;
    private javax.swing.JMenu menuRemisionConsorcio;
    private javax.swing.JMenu menuRemisiones;
    private javax.swing.JMenuItem sinRetonroConsorcio;
    private javax.swing.JMenuItem sinRetornoCDM;
    private javax.swing.JTabbedPane todo;
    // End of variables declaration//GEN-END:variables
    
}

package Dialogos;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.ConexionBD;

public class Login extends javax.swing.JDialog {

    
    /** DECLARACION DE VARIABLES - NO MODIFICAR */
    Point inicial;
    boolean NOESTA = false;
    //VARIABLE SIN USO DESCARGADA PARA AHORRAR MEMORIA ---->  private String EQUIPO = "";
    
    /** CONSTRUCTOR DE LA CLASE
     * @constructor
     * @param parent
     * @param modal */
    public Login(java.awt.Frame parent, boolean modal){
        super(parent, modal);
        initComponents();               
        /** DECLARACION DE VARIABLE LOCAL */
        final ConexionBD conexion = new ConexionBD();
        /** INVOCACION DEL METODO DE LA VARIABLE LOCAL */
        conexion.conectar();
        
        /** EJECUCION DEL BLOQUE 'try/catch' PENDIENTE LA CREACION DEL 'finally' */
        try {
            /** CONSULTA A LA BASE DE DATOS PARA OBTENER SI EL EQUIPO ESTA REGISTRADO EN LA BD */
            ResultSet rs = conexion.CONSULTAR("SELECT * FROM usuario WHERE pc='"+Inet4Address.getLocalHost().getHostName()+"' ");        
            /** CONDICIONAL QUE EVALUA EL CONJUNTO DE RESULTADO PARA DETERMINAR SI EL EQUIPO ESTA EN LA BD O NO. */
            if(rs.next()){
                /** DE ENCONTRARSE EN LA BASE DE DATOS DE CAMBIA EL VALOS DEL BOOLEANO A 'true' */
                NOESTA = true;
                /** SI EL USUARIO MARCO LA OPCION RECORDAR SE RECUPERAN LOS DATOS DE LA BD Y SE CARGAN EN EL JTEXT */
                if(rs.getBoolean("recordar")){
                    cjusuario.setText(rs.getString("nombreusuario"));
                    cjpass.setText(rs.getString("pass"));
                }
            }
            /** AUNQUE LA VARIABLE ESTA INICIALIZADA COMO UN 'false' SE DEJA EL 'else' POR CONVENCION */
            else{
                NOESTA = false;
            }
        } catch (UnknownHostException | SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.ERROR(ex, "ERROR AL CARGAR LOS DATOS DE LA SESION.");
        }
        /** EXPRECIONES LAMBA QUE DICTAN EL COMPORTAMIENTO DE LOS BOTONES ENTRAR Y SALIR */
        btnEntrar.addActionListener((ActionEvent e) ->{
            btnEntrar.setEnabled(false);
            entrar();
        });
        btnSalir.addActionListener((ActionEvent e) ->{
            System.exit(0);
        });
        
    }
    
    /**
     * METODO QEU AUNTENTICA AL USUARIO Y PERMITE INGRESAR
     */
    public void entrar(){
        /** INICIALIZACION DEL BLOQUE 'try/catch/finally */
        try{
            /** DECLARACION E INICIALIZACION DE LAS VARIABLES LOCALES */
            String usuario = cjusuario.getText().trim();
            String pass = cjpass.getText();
            final ConexionBD conexion = new ConexionBD();
            conexion.conectar();
            /** CONSULTA A LA BASE DE DATOS SI EL USUARIO Y COTRASEÑA SON VALIDOS */
            ResultSet rs = conexion.CONSULTAR("SELECT * FROM usuario WHERE nombreusuario='"+usuario+"' AND pass='"+pass+"' ");
            /** BLOQUE DE CONDICIONES PARA GUARDAR EL INICIO DE SESION */
            if(rs.next()){
                modelo.Sesion sesion = modelo.Sesion.getConfigurador(rs.getString("nombreusuario"), rs.getInt("idusuario"));
                if(checkRecordar.isSelected()){
                    if(!NOESTA){
                        if(conexion.GUARDAR("INSERT INTO usuario (nombreusuario,pass,pc,recordar) "
                                + "VALUES ('"+usuario+"','"+pass+"','"+Inet4Address.getLocalHost().getHostName()+"','true') ")){
                    
                        }
                    }                    
                    if(conexion.GUARDAR("UPDATE usuario SET recordar='true' WHERE nombreusuario='"+usuario+"' AND pass='"+pass+"' ")){
                        
                    }
                }
                dispose();
            }
            conexion.CERRAR();
        }catch(UnknownHostException | SQLException e){
            modelo.Metodos.M("ERROR AL INICIAR SESION\n"+e, "error.png");
        }finally{
            btnEntrar.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cjusuario = new CompuChiqui.JTextFieldPopup();
        cjpass = new javax.swing.JPasswordField();
        jToolBar1 = new javax.swing.JToolBar();
        checkRecordar = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnEntrar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnSalir = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Iniciar sesion");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(255, 255, 255));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        cjusuario.setCampodetexto(cjpass);
        cjusuario.setPlaceholder("Usuario");

        cjpass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjpassKeyTyped(evt);
            }
        });

        jToolBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBar1.setFloatable(false);

        checkRecordar.setForeground(new java.awt.Color(85, 85, 85));
        checkRecordar.setText("Recordar en éste equipo");
        checkRecordar.setContentAreaFilled(false);
        checkRecordar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkRecordarItemStateChanged(evt);
            }
        });
        jToolBar1.add(checkRecordar);
        jToolBar1.add(jSeparator1);

        btnEntrar.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnEntrar.setForeground(new java.awt.Color(85, 85, 85));
        btnEntrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/lock.png"))); // NOI18N
        btnEntrar.setText("Entrar");
        btnEntrar.setBorderPainted(false);
        jToolBar1.add(btnEntrar);
        jToolBar1.add(jSeparator2);

        btnSalir.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(85, 85, 85));
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.setBorderPainted(false);
        jToolBar1.add(btnSalir);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Usuario:");
        jLabel1.setToolTipText("");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Contraseña:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cjpass)
                            .addComponent(cjusuario, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cjusuario, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cjpass, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cjpassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjpassKeyTyped
        if(evt.getKeyChar()==10){
            entrar();
        }
    }//GEN-LAST:event_cjpassKeyTyped

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        inicial = evt.getPoint();
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - inicial.x, this.getLocation().y + evt.getY() - inicial.y);
    }//GEN-LAST:event_formMouseDragged

    private void checkRecordarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkRecordarItemStateChanged
        if(evt.getStateChange()==1){//SELECCIONADO
            
        }else if(evt.getStateChange()==2){//DESELECCIONADO
            
        }
    }//GEN-LAST:event_checkRecordarItemStateChanged
  
    /**
     * OTRO METODO MAIN INECESARIO
    public static void main(String args[]) {               
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Login dialog = new Login(new javax.swing.JFrame(), true);
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
    */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JCheckBox checkRecordar;
    private javax.swing.JPasswordField cjpass;
    private CompuChiqui.JTextFieldPopup cjusuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}

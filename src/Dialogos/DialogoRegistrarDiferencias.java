package Dialogos;

import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import modelo.ConexionBD;

/**
 *
 * @author AUXPLANTA
 */
public final class DialogoRegistrarDiferencias extends javax.swing.JDialog {

    DefaultTableModel modelo;
    Class columClass[] = {JButton.class, Integer.class, String.class};
    ArrayList<Integer> listadif;
    public int IDENTRADA = 0;
    public boolean ACTUALIZANDO = false;
    
    TableColumnAdjuster ajustarColumna;
    
    static final ConexionBD conexion = new ConexionBD();
    
    public DialogoRegistrarDiferencias(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();                             
        
        CargarInterfazTabla();
        ajustarColumna = new TableColumnAdjuster(tabladiferencias);
        
        setSize(Toolkit.getDefaultToolkit().getScreenSize().width-20,  getHeight());
        setLocation(10, getY());
    }
    
    void CargarInterfazTabla(){
        String data[][]={};
        String col[] = { "ITEM", "N°", "DESCRIPCION"};
        modelo = new DefaultTableModel(data, col){
            @Override
            public Class<?> getColumnClass(int column){
                return columClass[column];
            }
            @Override
            public boolean isCellEditable(int row, int column){
                return (column>1);
            }
        };
        
        tabladiferencias.setDefaultRenderer(Integer.class, new ColorRowJTable.ColorRowInJTable());
        tabladiferencias.setDefaultRenderer(JButton.class, new JButtonIntoJTable.BotonEnColumna());
        tabladiferencias.setDefaultRenderer(String.class, new RowHeightCellRenderer());
        
        tabladiferencias.setModel(modelo);
                
        tabladiferencias.setSurrendersFocusOnKeystroke(true);
        
        tabladiferencias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);                
        tabladiferencias.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
        tabladiferencias.setCellSelectionEnabled(true);    
        
        tabladiferencias.getColumnModel().addColumnModelListener(cl);
        tabladiferencias.getTableHeader().addMouseListener(cl);
        
        tabladiferencias.getColumnModel().getColumn(0).setMaxWidth(60);
        tabladiferencias.getColumnModel().getColumn(1).setMaxWidth(60);
        tabladiferencias.getColumnModel().getColumn(2).setMinWidth(1000);
        
        modelo.addTableModelListener((TableModelEvent e) -> {
            if(e.getType() == TableModelEvent.INSERT){
                System.out.println("FILA INSERTADA");
            }
            if(e.getType() == TableModelEvent.DELETE){
                System.out.println("FILA ELIMINADA");
            }
            if(e.getType() == TableModelEvent.UPDATE){
                if(e.getColumn()==2 && ACTUALIZANDO && !tabladiferencias.getValueAt(e.getFirstRow(), 1).toString().isEmpty()){
                    if(listadif.contains(Integer.parseInt(tabladiferencias.getValueAt(e.getFirstRow(), 1).toString())) && new ConexionBD().GUARDAR("UPDATE diferenciasentrada SET descripcion='"+tabladiferencias.getValueAt(e.getFirstRow(), 2)+"' WHERE identrada='"+IDENTRADA+"' AND iddiferencia='"+tabladiferencias.getValueAt(e.getFirstRow(), 1)+"' ")){
                        
                    }
                }
            }
        });
    }
    
    public void CargarDatos(int ID){
        try {
            IDENTRADA = ID;
            conexion.conectar();
            ResultSet rs = conexion.CONSULTAR("SELECT * FROM diferenciasentrada WHERE identrada='"+ID+"' ORDER BY iddiferencia ASC ");
            int con = 0;
            listadif = new ArrayList<>();
            while(rs.next()){
                listadif.add(rs.getInt("iddiferencia"));
                modelo.insertRow(con, new Object[]{});
                modelo.setValueAt(rs.getInt("iddiferencia"), con, 1);
                modelo.setValueAt(rs.getString("descripcion"), con, 2);
                con++;
            }
            TableColumn c = tabladiferencias.getColumnModel().getColumn(2);
            updateRowHeights(2, c.getWidth(), tabladiferencias);
            conexion.CERRAR();
            ACTUALIZANDO = true;
//            ajustarColumna.adjustColumns();
        }catch (SQLException ex){
            Logger.getLogger(DialogoRegistrarDiferencias.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "ERROR AL CARGAR LAS DIFERENCIAS \n"+ex);
        }
    }
    
    void Agregar(){
        modelo.addRow(new Object[]{new JButton(),"",""});
    }
    
    public class RowHeightCellRenderer extends JTextArea implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus,int row, int column) {
            setEditable(false);
            setLineWrap(true);
            setWrapStyleWord(true);           
        
            setForeground(Color.black);

            if ( (row%2)!=0 ){            
                setBackground(new Color(231,243,253,255));            
            }else{
                setBackground(Color.white);
            }

            if(isSelected){
                setBackground(new Color(51,122,183));
                setForeground(Color.white);
            }

            setText(value.toString());
            return this;
        }
    }
    
    public static void updateRowHeights(int column, int width, JTable table){
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
            Dimension d = comp.getPreferredSize();
            comp.setSize(new Dimension(width, d.height));
            d = comp.getPreferredSize();
            rowHeight = Math.max(rowHeight, d.height);
            table.setRowHeight(row, rowHeight);
        }
    }
    
    ColumnListener cl = new ColumnListener(){

            @Override
            public void columnMoved(int oldLocation, int newLocation) {
            }

            @Override
            public void columnResized(int column, int newWidth) {
                System.out.println("Redimensionando");
                System.out.println(newWidth);
                updateRowHeights(column, newWidth, tabladiferencias);
            }

    };        
    
    abstract class ColumnListener extends MouseAdapter implements TableColumnModelListener {

        private int oldIndex = -1;
        private int newIndex = -1;
        private boolean dragging = false;

        private boolean resizing = false;
        private int resizingColumn = -1;
        private int oldWidth = -1;

        @Override
        public void mousePressed(MouseEvent e) {
            
            if(e.getSource() instanceof JTableHeader) {
                System.out.println(e.getSource());
                JTableHeader header = (JTableHeader)e.getSource();
                TableColumn tc = header.getResizingColumn();
                if(tc != null){
                    resizing = true;
                    JTable table = header.getTable();
                    resizingColumn = table.convertColumnIndexToView( tc.getModelIndex());
                    oldWidth = tc.getPreferredWidth();
                }else{
                    resizingColumn = -1;
                    oldWidth = -1;
                }
            }   
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // column moved
            if(dragging && oldIndex != newIndex) {
                columnMoved(oldIndex, newIndex);
            }
            dragging = false;
            oldIndex = -1;
            newIndex = -1;

            // column resized
            if(resizing) {
                if(e.getSource() instanceof JTableHeader) {
                    JTableHeader header = (JTableHeader)e.getSource();
                    TableColumn tc = header.getColumnModel().getColumn(resizingColumn);
                    if(tc != null) {
                        int newWidth = tc.getPreferredWidth();
                        if(newWidth != oldWidth) {
                            columnResized(resizingColumn, newWidth);
                        }
                    }
                }   
            }
            resizing = false;
            resizingColumn = -1;
            oldWidth = -1;
        }

        @Override
        public void columnAdded(TableColumnModelEvent e) {      
        }

        @Override
        public void columnRemoved(TableColumnModelEvent e) {        
        }

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            // capture dragging
            dragging = true;
            if(oldIndex == -1){
                oldIndex = e.getFromIndex();
            }

            newIndex = e.getToIndex();  
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
        }

        @Override
        public void columnSelectionChanged(ListSelectionEvent e) {
        }

        public abstract void columnMoved(int oldLocation, int newLocation);
        public abstract void columnResized(int column, int newWidth);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnAgregarFila = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnEliminar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnGuardar = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabladiferencias = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAgregarFila.setBackground(new java.awt.Color(255, 255, 255));
        btnAgregarFila.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/row.png"))); // NOI18N
        btnAgregarFila.setToolTipText("Agregar fila");
        btnAgregarFila.setFocusable(false);
        btnAgregarFila.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAgregarFila.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAgregarFila.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFilaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAgregarFila);
        jToolBar1.add(jSeparator1);

        btnEliminar.setBackground(new java.awt.Color(255, 255, 255));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        btnEliminar.setToolTipText("Eliminar");
        btnEliminar.setFocusable(false);
        btnEliminar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEliminar);
        jToolBar1.add(jSeparator2);

        btnGuardar.setBackground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.setFocusable(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGuardar);
        jToolBar1.add(jSeparator3);

        tabladiferencias.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tabladiferencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ITEM", "N°", "DESCRIPCION"
            }
        ));
        tabladiferencias.setRowHeight(22);
        tabladiferencias.setSelectionBackground(new java.awt.Color(51, 122, 183));
        jScrollPane1.setViewportView(tabladiferencias);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarFilaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilaActionPerformed
        Agregar();
    }//GEN-LAST:event_btnAgregarFilaActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try{
            (new Thread(){
                public void run(){
                    String GUARDA = "INSERT INTO diferenciasentrada (identrada,descripcion) VALUES \n";
                    boolean GUARDAR = false;
                    for(int i=0; i<tabladiferencias.getRowCount(); i++){
                        if(!tabladiferencias.getValueAt(i, 2).toString().equals("")){
                            if(listadif.isEmpty()){
                                GUARDA += "( '"+IDENTRADA+"' ,  ";
                                GUARDA += " '"+tabladiferencias.getValueAt(i, 2)+"' ),\n";
                                GUARDAR = true;
                            }else{
                                if(tabladiferencias.getValueAt(i, 1).toString().isEmpty() || !listadif.contains(Integer.parseInt(tabladiferencias.getValueAt(i, 1).toString()))){
                                    GUARDA += "( '"+IDENTRADA+"' ,  ";
                                    GUARDA += " '"+tabladiferencias.getValueAt(i, 2)+"' ),\n";
                                    GUARDAR = true;
                                }
                            }
                        }
                    }
                    GUARDA = GUARDA.substring(0, GUARDA.length()-2);
                    if(GUARDAR && new ConexionBD().GUARDAR(GUARDA)){
                        tabladiferencias.setRowSelectionInterval(0, tabladiferencias.getRowCount()-1);
                        ACTUALIZANDO = false;            
                        int filas[] = tabladiferencias.getSelectedRows();
                        for(int i=filas.length-1; i>=0; i--){
                            modelo.removeRow(filas[i]);
                            validate();repaint();
                        }
                        CargarDatos(IDENTRADA);
                        
                        ACTUALIZANDO = true;
                    }
                }
            }).start();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "NO SE PUDO COMPLETAR "+e);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        try{
            int filas[] = tabladiferencias.getSelectedRows();
            for(int i=filas.length-1; i>=0; i--){
                if(new ConexionBD().GUARDAR("DELETE FROM diferenciasentrada WHERE identrada='"+IDENTRADA+"' AND iddiferencia='"+tabladiferencias.getValueAt(filas[i], 1)+"' ")){
                    modelo.removeRow(filas[i]);
                }                
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "ERROR AL ELIMINAR LAS FILAS DE LA TABLA\n"+e);
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

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
            java.util.logging.Logger.getLogger(DialogoRegistrarDiferencias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DialogoRegistrarDiferencias dialog = new DialogoRegistrarDiferencias(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAgregarFila;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTable tabladiferencias;
    // End of variables declaration//GEN-END:variables
}

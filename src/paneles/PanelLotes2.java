package paneles;

import CopyPasteJTable.ExcelAdapter;
import JTableAutoResizeColumn.TableColumnAdjuster;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import modelo.Cliente;
import modelo.ConexionBD;
import modelo.CustomTableModel;
import modelo.Metodos;
import view.EntradaDeTrafos;
import view.PrepararDespacho;

public class PanelLotes2 extends javax.swing.JPanel {

    CustomTableModel modeloTabla;

    LinkedList<RowFilter<Object, Object>> filtros = new LinkedList<>();
    List<String> nombreFiltros = new ArrayList<>();
    TableRowSorter rowSorter;

    TableColumnAdjuster ajustarColumna;

    ConexionBD conexion = new ConexionBD();
    String[] cols = new String[]{"ITEM", "ENTREGADOS", "PENDIENTES",
        "CLIENTE", "REP.", "LOTE", "CIUDAD", "CONTRATO",
        "RECEPCION", "REGISTRO"};

    public PanelLotes2() {
        initComponents();

        //CREA UNA INSTANCIA DE LA CLASE QUE AJUSTA EL ANCHO DE LAS COLUMNAS.
        ajustarColumna = new TableColumnAdjuster(tablaLotes);
        ExcelAdapter excelAdapter = new CopyPasteJTable.ExcelAdapter(tablaLotes);

        //AGREGA LA PERSONALIZACION DEL COMBOBOX
        comboColumnas.setUI(JComboBoxColor.JComboBoxColor.createUI(comboColumnas));
        comboDistintos.setUI(JComboBoxColor.JComboBoxColor.createUI(comboDistintos));

        //MUESTRA EL CONTENIDO DEL COMBOBOX AL MAXIMO ANCHO DEL ITEM MAS LARGO
        comboColumnas.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
        comboDistintos.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));

        tablaLotes.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                lblFilasSeleccionadas.setText("Columnas: " + tablaLotes.getSelectedColumnCount() + 
                        " Filas: " + tablaLotes.getSelectedRowCount() + " Total filas: " + tablaLotes.getRowCount());
            }
        });
    }

    public void cargarListaLotes() {
        
        /**SE CREA LA TABLA QUE EVENTUALMENTE RECIBIRA LOS DATOS, y SE USA UNA SENTENCIA 'try' PARA PREVEEER EXCEPCION*/
        try {
            tablaLotes.setRowSorter(null);
            modeloTabla = new CustomTableModel(
                    new String[][]{},
                    cols,
                    tablaLotes,
                    new Class[]{Integer.class, Integer.class, Integer.class,
                        String.class, String.class, String.class, String.class, String.class,
                        LocalDate.class, LocalDateTime.class},
                    new Boolean[]{false, false, false,
                        false, false, false, false, false,
                        false, false}
            );
            
            /**CONSULTA SQL A LA BASE DE DATOS*/
            String sql = "select count(t.idremision) as entregados, COUNT(t.*)-count(t.idremision) as pendientes, \n"
                    + "e.identrada, c.nombrecliente, ciu.nombreciudad, e.lote, e.fecharecepcion::date, e.fecharegistrado,\n"
                    + "e.contrato, e.representante from entrada e\n"
                    + "inner join cliente c on c.idcliente=e.idcliente \n"
                    + "inner join ciudad ciu on ciu.idciudad=e.idciudad \n"
                    + "left join transformador t on t.identrada=e.identrada\n"
                    + "group by c.idcliente, ciu.idciudad, e.identrada \n"
                    + "ORDER BY fecharecepcion DESC, fecharegistrado DESC;";
            /**CONEXION A LA BASE DE DATOS*/
            conexion.conectar();
            /**CONJUNTO DE RESULTADOS QUE CONTIENE LOS VALORES ENVIADOS POR LA BD A TRAVEZ DE LA CLASE CONEXION*/
            ResultSet rs = conexion.CONSULTAR(sql);
            
            /**SE RECORRE EL CONJUNTO DE RESULTADOS AGREGANDO CADA RESULTADO A UNA FILA EN LA TABLA MUTABLE*/
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getInt("identrada"),
                    rs.getInt("entregados"),
                    rs.getInt("pendientes"),
                    rs.getString("nombrecliente"),
                    rs.getString("representante"),
                    rs.getString("lote"),
                    rs.getString("nombreciudad"),
                    rs.getString("contrato"),
                    rs.getDate("fecharecepcion").toLocalDate(),
                    rs.getTimestamp("fecharegistrado").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
                });
            }
            tablaLotes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tablaLotes.setCellSelectionEnabled(true);
            tablaLotes.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");
            tablaLotes.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());
            rowSorter = new TableRowSorter(modeloTabla);
            tablaLotes.setRowSorter(rowSorter);

            lblFilasSeleccionadas.setText("Columnas: " + tablaLotes.getSelectedColumnCount() + " Filas: " + tablaLotes.getSelectedRowCount() + " Total filas: " + tablaLotes.getRowCount());

            /**
             * 
             * CODIGO VIEJO NO UTILIZADO
             * 
             * frameLotes.tablaLotes.setDefaultRenderer(Object.class, new ColorRowJTable.ColorRowInJTable());
             * setTitle("LOTES REGISTRADOS: "+model.Lote.getTotalLotes());
            */
        
        /** 'catch' DEL 'try' INDICANDO QUE HACER CON LA EXCEPCION GENERADA POR EL BLOQUE 'try/catch' */    
        } catch (SQLException ex) {
            Metodos.ERROR(ex, "OCURRIO UN ERROR AL CARGAR LA TABLA CON LA LISTA DE LOS LOTES");
            Logger.getLogger(PanelLotes2.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        /** FINALICACION DE LOS RECURSOS DEL PROGRAMA PARA EVITAR UNA EXPLOCION DEL MISMO */
        finally {
            ajustarColumna.adjustColumns();
            comboColumnas.addItem("TODOS");
            for (String col : cols) {
                comboColumnas.addItem(col);
            }
        }
    }

    
    /** LIST PARA OBTENER ELEMENTOS NO REPEDITOS DE UNA LISTA */
    void distintos(int col) {
        List<String> datos = new ArrayList<>();
        for (int i = 0; i < tablaLotes.getRowCount(); i++) {
            datos.add("" + tablaLotes.getValueAt(i, col));
        }
        datos = datos.stream().distinct().collect(Collectors.toList());
        comboDistintos.removeAllItems();
        Collections.sort(datos);
        datos.stream().forEach(d -> {
            comboDistintos.addItem(d);
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        subMenuTablaLotes = new javax.swing.JPopupMenu();
        subMenuAbrirLote = new javax.swing.JMenuItem();
        subMenuDarPorTerminado = new javax.swing.JMenuItem();
        subMenuPrepararDespacho = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        comboColumnas = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        comboDistintos = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cjBuscar = new javax.swing.JTextField();
        btnFiltro = new javax.swing.JButton();
        btnQuitarFiltro = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnGenerarExcel = new javax.swing.JButton();
        btnCargarLotes = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaLotes = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        barraProgreso = new javax.swing.JProgressBar();
        lblFilasSeleccionadas = new javax.swing.JLabel();

        subMenuAbrirLote.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        subMenuAbrirLote.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/ver.png"))); // NOI18N
        subMenuAbrirLote.setText("Abrir");
        subMenuAbrirLote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuAbrirLoteActionPerformed(evt);
            }
        });
        subMenuTablaLotes.add(subMenuAbrirLote);

        subMenuDarPorTerminado.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        subMenuDarPorTerminado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/finalizar.png"))); // NOI18N
        subMenuDarPorTerminado.setText("Dar por terminado");
        subMenuDarPorTerminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuDarPorTerminadoActionPerformed(evt);
            }
        });
        subMenuTablaLotes.add(subMenuDarPorTerminado);

        subMenuPrepararDespacho.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        subMenuPrepararDespacho.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/preparardespacho.png"))); // NOI18N
        subMenuPrepararDespacho.setText("Preparar despacho");
        subMenuPrepararDespacho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuPrepararDespachoActionPerformed(evt);
            }
        });
        subMenuTablaLotes.add(subMenuPrepararDespacho);

        jToolBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel2.setText("Columna:");
        jToolBar1.add(jLabel2);

        comboColumnas.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboColumnas.setMaximumRowCount(25);
        comboColumnas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboColumnasItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboColumnas);
        jToolBar1.add(jSeparator2);

        comboDistintos.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        comboDistintos.setForeground(new java.awt.Color(255, 255, 255));
        comboDistintos.setMaximumRowCount(15);
        comboDistintos.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboDistintosItemStateChanged(evt);
            }
        });
        jToolBar1.add(comboDistintos);

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel3.setText("Buscar:");
        jToolBar1.add(jLabel3);
        jToolBar1.add(cjBuscar);

        btnFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/filtro.png"))); // NOI18N
        btnFiltro.setToolTipText("Crear filtro");
        btnFiltro.setFocusable(false);
        btnFiltro.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnFiltro.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFiltro);

        btnQuitarFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/filtromenos.png"))); // NOI18N
        btnQuitarFiltro.setToolTipText("Remover filtro");
        btnQuitarFiltro.setFocusable(false);
        btnQuitarFiltro.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnQuitarFiltro.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnQuitarFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarFiltroActionPerformed(evt);
            }
        });
        jToolBar1.add(btnQuitarFiltro);
        jToolBar1.add(jSeparator3);

        btnGenerarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        btnGenerarExcel.setToolTipText("Exportar a excel");
        btnGenerarExcel.setFocusable(false);
        btnGenerarExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGenerarExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGenerarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarExcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGenerarExcel);

        btnCargarLotes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/actualizar.png"))); // NOI18N
        btnCargarLotes.setToolTipText("Actualizar lista de lotes");
        btnCargarLotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarLotesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCargarLotes);

        tablaLotes.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        tablaLotes.setForeground(new java.awt.Color(70, 70, 70));
        tablaLotes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "LOTES"
            }
        ));
        tablaLotes.setGridColor(new java.awt.Color(227, 227, 227));
        tablaLotes.setOpaque(false);
        tablaLotes.setRowHeight(25);
        tablaLotes.setSelectionBackground(new java.awt.Color(51, 122, 183));
        tablaLotes.getTableHeader().setReorderingAllowed(false);
        tablaLotes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaLotesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaLotes);
        tablaLotes.getAccessibleContext().setAccessibleName("Lotes");

        jToolBar2.setFloatable(false);

        barraProgreso.setStringPainted(true);
        jToolBar2.add(barraProgreso);

        lblFilasSeleccionadas.setFont(new java.awt.Font("Enter Sansman", 1, 12)); // NOI18N
        lblFilasSeleccionadas.setToolTipText("");
        jToolBar2.add(lblFilasSeleccionadas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void subMenuAbrirLoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuAbrirLoteActionPerformed
        /** SE PREPARA EL PROGRAMA PARA RECIBIR A UNA NUEVA ENTRADA DE LOTES DE TRANSFORMADORES */
        EntradaDeTrafos et = new EntradaDeTrafos();
        et.setTitle("LOTE " + tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 2) + " " 
                + tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 1));
        et.setIDENTRADA(Integer.parseInt(tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 0).toString()));
        et.cargarEncabezadoEntrada();
        et.cargarTablaDeTransformadores(et.checkOrdenar.isSelected());
        et.setVisible(true);
    }//GEN-LAST:event_subMenuAbrirLoteActionPerformed

    private void subMenuDarPorTerminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuDarPorTerminadoActionPerformed
        /**INICO DE BLOQUE 'try/catch'*/
        try {
            /**VALIDADCION SI LA PERSONA QUE DESEA CERRAR EL BLOQUE ESTA USANDO EL EQUIPO DEL ALMACEN*/
            if (Inet4Address.getLocalHost().getHostName().equals("ALMACEN") 
                    || Inet4Address.getLocalHost().getHostName().equals("PROGRAMADOR")) {
                String idEntrada = tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 0).toString();
                /**
                 *
                 * CODIGO VIEJO NO UTILIZADO
                 * 
                 * boolean estado = Boolean.parseBoolean(tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 13).toString());
                 * if(estado){
                 * JOptionPane.showMessageDialog(this, "EL LOTE YA SE ENCUENTRA VERIFICADO.", "ITEM SIN NUMERO DE SERIE", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/recursos/images/advertencia.png")));
                 * }else{
                 */
                
                /**
                 * SI ANIDADOS EL PRMIERO CONFORMA CON EL USUARIO SI DESEA TERMINAR EL LOTE Y EL SEGUNDO VALIDA SI LA CONSULTA
                 * SE REALISO CORRECTAMENTE Y HACE VUELVE A LISTAR LOS LOSTES
                 */
                if (JOptionPane.showConfirmDialog(this, "¿Confirma que desea dar por terminado el lote?") == JOptionPane.YES_OPTION) {
                    if (new ConexionBD().GUARDAR("UPDATE entrada SET estado='TRUE' , fechaliberado='" + new java.util.Date() + "' WHERE identrada='" + idEntrada + "' ")) {
                        btnCargarLotes.doClick();
                    }
                }
            /** SINO DE LA PRIMERA CONDICIONAL */
            } else {
                modelo.Metodos.M("SOLO EL PERSONAL DE ALMACEN PUEDE DAR POR TERMINADO EL LOTE.", "advertencia.png");
            }
        /** 
         * DOS 'catch' PARA LA SENTENCIA 'try'
         * EL PRIMERO SALTA SI NO SE LOGRA IDENTIFICAR EL NOMBRE DEL EQUIPO
         * EL SEGUNDO SALTA POR UN ERROR GENERAL
         * PENDIENTE CREAR LA SENTENCA 'finally'
         */
        } catch (UnknownHostException ex) {
            Logger.getLogger(PanelLotes2.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.ERROR(ex, "ERROR AL VERIFICAR EL NOMBRE DEL EQUIPO.");
        } catch (HeadlessException ex) {
            Logger.getLogger(PanelLotes2.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.ERROR(ex, "ERROR");
        }
    }//GEN-LAST:event_subMenuDarPorTerminadoActionPerformed

    private void subMenuPrepararDespachoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuPrepararDespachoActionPerformed
        /** PREPARACION DEL DESPACHO */
        
        /** PRIMERO OBTENEMOS EL ID DEL LOTE A DESPACHAR */
        int IDENTRADA = (int) tablaLotes.getValueAt(tablaLotes.getSelectedRow(), 0);
        
        /** CONEXION A LA BD A TRAVEZ DE LA CLASE CONEXION ENVIANDO EN LA CONSULTA DEL ID OBTENIDO ANTERIORMENTE*/
        conexion.conectar();
        ResultSet rs = conexion.CONSULTAR(" SELECT * FROM entrada INNER JOIN cliente c USING(idcliente) WHERE identrada=" 
                + IDENTRADA + "");
        /** INICIO DEL BLOQUE 'try/catch' */
        try {
            /** SE VALIDA MEDIANTE UN SI EL CONJUNTO DE RESULTADOS Y SE ENVIA A LA CLASE PrepararDespacho LOS VALORES */
            if (rs.next()) {
                PrepararDespacho pd = new PrepararDespacho();
                pd.setTitle("LOTE: " + rs.getString("lote") + " de " + rs.getString("nombrecliente"));
                pd.setIDENTRADA(rs.getInt("identrada"));
                pd.setIDCLIENTE(rs.getInt("idcliente"));
                pd.cargarIntefazTabla();
                pd.cargarComboDespachos();
                pd.comboClientes.getModel().setSelectedItem(new Cliente(rs.getInt("idcliente"), rs.getString("nombrecliente"), rs.getString("nitcliente")));
                pd.setVisible(true);
            }
           
        } catch (SQLException ex) {
            Logger.getLogger(PanelLotes2.class.getName()).log(Level.SEVERE, null, ex);
        }
        /** FINALIZACION DE RECURSOS */
        finally {
            conexion.CERRAR();
        }
    }//GEN-LAST:event_subMenuPrepararDespachoActionPerformed

    private void btnCargarLotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarLotesActionPerformed
        /** INVOCACION DEL METODO QUE CARGA O ACTUALIZA LA LISTA DE LOTES */
        cargarListaLotes();
    }//GEN-LAST:event_btnCargarLotesActionPerformed

    private void tablaLotesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaLotesMouseClicked
        /** ACCIONES A REALIZAR CON EL BOTON DERECHO DEL MOUSE */
        /** UN CLIC DEL MOUSE SELECCIONA UN VALOR EN LA TABLA */
        if (SwingUtilities.isRightMouseButton(evt)) {
            tablaLotes.setRowSelectionInterval(tablaLotes.rowAtPoint(evt.getPoint()), tablaLotes.rowAtPoint(evt.getPoint()));
            tablaLotes.setColumnSelectionInterval(0, tablaLotes.getColumnCount() - 1);
            subMenuTablaLotes.show(tablaLotes, evt.getPoint().x, evt.getPoint().y);
        }
        /** DOBLE CLIC EN UN VALOR ABRE EL MENU DE LOTE DE ESE REGISTRO */
        if (evt.getClickCount() == 2) {
            subMenuAbrirLote.doClick();
        }
    }//GEN-LAST:event_tablaLotesMouseClicked

    private void btnGenerarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarExcelActionPerformed
        /** GENERAR EL REPORTE EN EXCEL */
        modelo.Metodos.JTableToExcel(tablaLotes, btnGenerarExcel);
    }//GEN-LAST:event_btnGenerarExcelActionPerformed

    private void btnFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroActionPerformed
        /** ACCIONES PARA EL FILTRADO DE COLUMNAS E INVOCACION DEL METODO PARA AJUSTAR LAS COLUMNAS */
        if (comboColumnas.getSelectedIndex() > 0) {
            filtros.add(RowFilter.regexFilter(cjBuscar.getText().toUpperCase(), (comboColumnas.getSelectedIndex() - 1)));
            nombreFiltros.add(cjBuscar.getText().toUpperCase());
            rowSorter.setRowFilter(RowFilter.andFilter(filtros));
            btnFiltro.setText("" + filtros.size());
        } else if (comboColumnas.getSelectedIndex() == 0) {
            if (!cjBuscar.getText().isEmpty()) {
                rowSorter.setRowFilter(RowFilter.regexFilter(cjBuscar.getText().toUpperCase()));
                btnFiltro.setText(cjBuscar.getText());
            }
        }
        ajustarColumna.adjustColumns();
    }//GEN-LAST:event_btnFiltroActionPerformed

    private void btnQuitarFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarFiltroActionPerformed
        /** ACCIONES PARA REMOVER CUALQUIER FILTRADO DE COLUMNAS E INVOCACION DEL METODO PARA AJUSTAR LAS COLUMNAS */
        if (comboColumnas.getSelectedIndex() == 0) {
            filtros.clear();
            rowSorter.setRowFilter(RowFilter.regexFilter(""));
            btnFiltro.setText("");
            cjBuscar.setText("");
        } else {
            String[] buttons = new String[nombreFiltros.size()];
            for (int i = 0; i < nombreFiltros.size(); i++) {
                buttons[i] = nombreFiltros.get(i);
            }
            int r = JOptionPane.showOptionDialog(this, "Seleccione", "Mensaje", 1, 1, null, buttons, null);
            if (r > -1) {
                filtros.remove(r);
                nombreFiltros.remove(r);
                btnFiltro.setText("" + filtros.size());
                rowSorter.setRowFilter(RowFilter.andFilter(filtros));
            }
        }
        ajustarColumna.adjustColumns();
    }//GEN-LAST:event_btnQuitarFiltroActionPerformed

    private void comboColumnasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboColumnasItemStateChanged
        /** CONDICIONALES USADOS PARA REMOVER CUALQUIER ITEM NO NECESARIO DEL FILTRADO */
        if (comboColumnas.getSelectedIndex() > 0) {
            if (comboColumnas.getSelectedIndex() == 0) {
                comboDistintos.removeAllItems();
                filtros.clear();
            } else {
                distintos((comboColumnas.getSelectedIndex() - 1));
            }
        } else if (comboColumnas.getSelectedIndex() == 0) {
            comboDistintos.removeAllItems();
            cjBuscar.setText("");
        }
    }//GEN-LAST:event_comboColumnasItemStateChanged

    private void comboDistintosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboDistintosItemStateChanged
        /** CONDICIONAL QUE BUSCA UNA CADENA DE TEXTO EN LA TABLA */
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            cjBuscar.setText(comboDistintos.getSelectedItem().toString());
        }
    }//GEN-LAST:event_comboDistintosItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JProgressBar barraProgreso;
    public javax.swing.JButton btnCargarLotes;
    public javax.swing.JButton btnFiltro;
    public javax.swing.JButton btnGenerarExcel;
    public javax.swing.JButton btnQuitarFiltro;
    private javax.swing.JTextField cjBuscar;
    public javax.swing.JComboBox<String> comboColumnas;
    public javax.swing.JComboBox<String> comboDistintos;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblFilasSeleccionadas;
    public javax.swing.JMenuItem subMenuAbrirLote;
    public javax.swing.JMenuItem subMenuDarPorTerminado;
    public javax.swing.JMenuItem subMenuPrepararDespacho;
    public javax.swing.JPopupMenu subMenuTablaLotes;
    public javax.swing.JTable tablaLotes;
    // End of variables declaration//GEN-END:variables

}

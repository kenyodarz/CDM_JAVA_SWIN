package view;

import Dialogos.DialogoTrafosRepetidos;
import JTableAutoResizeColumn.TableColumnAdjuster;
import modelo.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PROTOS extends javax.swing.JFrame {

    /*
     * DECLARACION DE VARIABLES - NO MODIFICAR
     */
    private String ESTADO_TRAFO = null;
    private boolean ACTUALIZANDO = false;
    ConexionBD conex = new ConexionBD();
    private int IDTRAFO = -1, IDPROTOCOLO = -1;
    TableModelListener listenerTablaUno;
    Hilofases alertas;
    modelo.Sesion sesion = modelo.Sesion.getConfigurador(null, -1);

    CustomTableModel modeloTabla;
    TableRowSorter rowSorter;
    RowFilter<TableModel, Object> compoundRowFilter;

    //private int idUsuario = 1;
    TableColumnAdjuster ajustarColumna;

    /*
     * CONSTRUCTOR
     */
    public PROTOS() {
        initComponents();
        cjef2.setVisible(false);
        /*
          INICIALIZACION DE LA VENTANA MAXIMIZADA
         */
        //setExtendedState(Frame.MAXIMIZED_BOTH);
        this.setExtendedState(this.getExtendedState() | PROTOS.MAXIMIZED_BOTH);

        /*
          OBTENCION DE LA FECHA ACTUAL Y CARGADA EN EL CAMPO EN CUESTION
         */
        cjfechasalida.setDate(new java.util.Date());
        /*
          SE CREA UNA CONSULTA PARA GENERAR EL CONSECUTIVO, EN LA BASE DE DATOS
          SE GUARDA EL CODIGO DEL ULTIMO PROTOCOLO GENERADO A ESE NUMERO SE LE
          AGREGA "A-"{{RESULTADO DE LA BD}}"-'yy'"
         */
        cjprotocolo.setText("A-" + modelo.Metodos.getConsecutivoRemision("protocolo", false)
                + "-" + new SimpleDateFormat("yy").format(new java.util.Date()));

        /*
         * INVOCACION DEL METODO QUE HABILITA LOS CAMPOS
         * NOTA: SE CONVIRTIO EL METODO EN FINAL PARA EVITAR ERRORES INESPERADOS
         */
        habilitarCampos((comboFase.getSelectedIndex() == 0));

        /*
         * SE AJUSTA LAS COLIMNAS A LA TABLA
         */
        ajustarColumna = new TableColumnAdjuster(tablaProtocolos);

        /*
         * INTANCIACION DEL METODO QUE GENERA LA TABLA PROTOCOLOS 
         * NOTA: SE CONVIRTIO EL METODO EN FINAL PARA EVITAR ERRORES INESPERADOS
         */
        cargarProtocolos();

        /*
         * SE INICIALIZA EL COMBO CLIENTE SIN SELECCION
         * idCliente =>-1,
         * nombreCliente => 'Selecciones...',
         * nitCliente => null.
         */
        comboCliente.addItem(new Cliente(-1, "Seleccione...", null));

        /*
         * SE CARGAN LOS VALORES EN EL COMBOBOX DEL CLIENTE USANDO EL MODELO
         */
        modelo.Cliente.cargarComboNombreClientes(comboCliente);

        //lINEA INHABILITADA TEMPORALMENTE POR ESTAR DOBLE.
        //comboCliente.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));

        /*
         * INICIALIZACION Y MUESTREO DEL COMBOBOX CON TODOS LOS DATOS DEL LA TABLA CLIENTES
         */
        comboCliente.setUI(JComboBoxColor.JComboBoxColor.createUI(comboCliente));
        comboCliente.addPopupMenuListener(new JComboBoxFullText.BoundsPopupMenuListener(true, false));
    }

    /* METODO PARA HALLAR LA TENSION EN SERIE CON VALORES DEFINIDOS */
    void HallarTensionSerie() {
        /* INICIALIZACION DE VARIABLES LOCALES */
        String tensionserie, nba;
        /* AL SER VALORES ESTRICTAMENTE FIJOS Y DEACUERDO A LAS CIRCUNSTANCIAS SITUACIONALES
         *  SE CREA UN GRUPO DE CONDICIONALES
         *  PARA REALIZAR ESTA TAREAS
         *  VOLTAJE PRIMARIO
         */
        tensionserie = (7000 <= cjvp.getInt() && cjvp.getInt() <= 15000)
                ? "15" : (16000 <= cjvp.getInt() && cjvp.getInt() <= 25000)
                ? "25" : (26000 <= cjvp.getInt() && cjvp.getInt() <= 38000)
                ? "38" : (39000 <= cjvp.getInt() && cjvp.getInt() <= 52000)
                ? "52" : (cjvp.getInt() <= 1200)
                ? "1.2" : "0";
        nba = (7000 <= cjvp.getInt() && cjvp.getInt() <= 15000)
                ? "95" : (16000 <= cjvp.getInt() && cjvp.getInt() <= 25000)
                ? "125" : (26000 <= cjvp.getInt() && cjvp.getInt() <= 38000)
                ? "200" : (39000 <= cjvp.getInt() && cjvp.getInt() <= 52000)
                ? "250" : (cjvp.getInt() <= 1200)
                ? "30" : "0";
        /* VOLTAJE SECUNDARIO */
        tensionserie += (cjvs.getInt() > 1200) ? "/15" : "/1.2";

        nba += (cjvs.getInt() <= 1200)
                ? "/30" : (7000 <= cjvs.getInt() && cjvs.getInt() <= 15000)
                ? "/95" : "0";

        /* ENVIO DE LOS RESULTADOS A LA VISTA */
        cjtensionSerie.setText(tensionserie);
        cjnba.setText(nba);
    }

    /* METODO PARA HALLAR CONEXION Y POLARIDAD
     *  SON VALORES FIJOS DEACUERDO A CIERTAS SITUACIONES
     *
     */
    public void HallarConexionYPolaridad() {
        /*
         *  INICIO DE SI ANIDADOS:
         *  CONDICIONAL DE PRIMER NIVEL VALIDA LA FASE
         */
        if (comboFase.getSelectedIndex() == 0) {
            /*
             * CONDICIONAL DE SEGUNDO OBTIENE EL VALOR DEACUERDO AL VALOR PRIMARIO
             */
            comboGrupoConexion.setSelectedItem((cjvp.getInt() <= 8000) ? "Ii6" : "Ii0");
            /*
             * CONDICIONAL DE SEGUNDO NIVEL OBTIENE EL VALOR DEACUERDO AL VALOR PRIMARIO
             */
            comboPolaridad.setSelectedIndex((cjvp.getInt() <= 8000) ? 1 : 0);
        } /*
         *ELSE DEL PRIMER SI Y CONDICIONAL DEL SIGUIENTE VALOR
         */ else if (comboFase.getSelectedIndex() == 1) {
            comboGrupoConexion.setSelectedItem("DYn5");
            comboPolaridad.setSelectedIndex(0);
        }
    }

    /*
     * METODO PARA HALLAR CORRIENTE 
     * INICIO DE BLOQUE 'try/catch'
     */
    void HallarCorrientes() {
        try {
            cji1.setText(String.valueOf(QD(
                    ((cjkva.getDouble() * 1000) / ((comboFase.getSelectedIndex() == 0) ? 1 : Math.sqrt(3))) / cjvp.getInt(), 2)));
            cji2.setText(String.valueOf(QD(
                    ((cjkva.getDouble() * 1000) / ((comboFase.getSelectedIndex() == 0) ? 1 : Math.sqrt(3))) / cjvs.getInt(), 2)));
        } catch (Exception e) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /* METODO PARA CALCULAR EL PROMEDIO DE CORRIENTES */
    void HallarPromedioCorrientes() {
        /* DEPENDIENDO DE LA FASE SE HACE EL CALCULO */
        if (comboFase.getSelectedIndex() == 0) {
            //SE ENVIAN VALORES AL METODO QD (n: POTENCIA, d: EXPONENTE)
            cjpromedioi.setText(String.valueOf(QD((cjiu.getDouble() / cji2.getDouble()) * 100, 2)));
        } else {
            cjpromedioi.setText(String.valueOf(QD((((cjiu.getDouble() + cjiv.getDouble() + cjiw.getDouble()) / 3) / cji2.getDouble()) * 100, 2)));
        }
    }

    /* METODO PARA CALCULAR EL PROMEDIO DE RESISTENCIAS */
    void HallarPromedioResistencias() {
        /* DEPENDIENDO DE LA FASE DE HACE EL CALCULO */
        if (comboFase.getSelectedIndex() == 0) {
            cjproresalta.setText("" + cjuv.getDouble());
            cjproresbaja.setText("" + cjxy.getDouble());
        } else {
            //SE ENVIAN VALORES AL METODO QD (n: POTENCIA, d: EXPONENTE)
            cjproresalta.setText("" + QD((cjuv.getDouble() + cjwu.getDouble() + cjvw.getDouble()) / 3, 2));
            cjproresbaja.setText("" + QD((cjxy.getDouble() + cjyz.getDouble() + cjzx.getDouble()) / 3, 2));
        }
    }

    /* METODO I2R 
     * RETORNA LA SUMA DEL PRODUCTO DEL PROMEDIOALTA POR EL CUADRADO DE I1 SOBRE 1_000 
     * Y EL PRODUCTO DEL PROMEDIOBAJA POR EL CUADRADO DE I2 SOBRE 1_000
     * EN CASO QUE LA FASE SEA DIFERENTE DE 0 SE APLICA 1.5 AL RESULTADO
     * TAMBIEN SE MUESTRA EL RESULTADO EN LA CASILLA I2R
     */
    double I2R() {
        /* DEPENDIENDO DE LA FASE DE APLICA LA FORMULA */
        if (comboFase.getSelectedIndex() == 0) {
            cji2r.setText("" + QD(((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble())
                    + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000))), 2));
            return ((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble())
                    + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000)));
        } else {
            cji2r.setText("" + QD(1.5 * ((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble())
                    + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000))), 2));
            return 1.5 * ((Math.pow(cji1.getDouble(), 2) * cjproresalta.getDouble())
                    + (Math.pow(cji2.getDouble(), 2) * (cjproresbaja.getDouble() / 1000)));
        }
    }


    /* METODO I2R85
     * MUESTRA EN LA CASILLA I2RA85 EL VALOR REDONDEADO DEL PRODUCTO DEL METODO I2R
     * POR EL EL METODO K
     * TAMBIEN RETORNA EL RESULTADO DE LA OPERACION
     */
    double I2R85() {
        cji2ra85.setText(String.valueOf(QD(I2R() * K(), 1)));
        return I2R() * K();
    }

    /* METODO R
     * RETORNA EL PCUMEDIDO SOBRE 10 VECES EL KVA
     */
    double R() {
        return cjpcumedido.getDouble() / (10 * cjkva.getDouble());
    }

    /* METODO R85
     * RETORNA EL PRODUCTO DEL METODO R POR EL METODO K
     */
    double R85() {
        return R() * K();
    }

    /* METODO Z
     * MUESTRA LA IMPEDANCIA DEL CUBO DEL PRODUCTO DE VCC(nombre provisional) POR EL VALOR PRIMARIO SOBRE 100
     * TAMBIEN RETORNA EL VALOR DEL PRODUCTO DE VCC(nombre provisional) POR EL VALOR PRIMARIO SOBRE 100
     */
    double Z() {
        cjimpedancia.setText("" + QD((cjvcc.getDouble() / cjvp.getDouble()) * 100, 3));
        return (cjvcc.getDouble() / cjvp.getDouble()) * 100;
    }

    /* METODO Z85
     * MUESTRA LA IMPEDANCIA85 CALCULADA DE LA RAIZ CUADRADA DE LA SUMA DE LOS METOS R80 Y X AL CUADRADO
     */
    public double Z85() {
        double Z85 = Math.sqrt((Math.pow(R85(), 2)) + (Math.pow(X(), 2)));
        cjimpedancia85.setText(String.valueOf(QD(Z85, 2)));
        return Z85;
    }

    /* METODO X
     * RETORNA LA RAIZ CUADRADA DE LA DIFERENCIA DE LOS METODOS Z Y R AL CUADRADO
     */
    double X() {
        return Math.sqrt((Math.pow(Z(), 2)) - (Math.pow(R(), 2)));
    }

    /*
     * METODO PARA OBTENER EL KC DEPENDIENDO DE LOS MATERIALES USADOS EN LA ALTA Y BAJA SON VALORES FIJOS.
     * 234.5 SI ES COBRE COBRE
     * 225 SI ES ALUMINIO ALUMINIO
     * 229 SI ES COMBINADO
     */
    double getkc() {
        return (comboMaterialAlta.getSelectedItem().toString().equalsIgnoreCase("COBRE") && comboMaterialBaja.getSelectedItem().equals("COBRE"))
                ? 234.5
                : (comboMaterialAlta.getSelectedItem().toString().equalsIgnoreCase("ALUMINIO") && comboMaterialBaja.getSelectedItem().equals("ALUMINIO"))
                ? 225
                : 229;
    }

    /*
     * METODO K
     *
     * CALCULA EL VALOR DE LA VARIABLE K EL CUAL ES EL RESULTADO DEL METODO KC MAS UN VALOR FIJO
     * SOBRE LA SUMATORIA DEL METODO KC CON LA TEMPERATURA
     * 
     */
    double K() {
        double K = 0;
        if (comboRefrigeracion.getSelectedIndex() < 2) {
            K = (getkc() + 85) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
        } else if (comboRefrigeracion.getSelectedIndex() == 2) {
            switch (comboClaseAislamiento.getSelectedIndex()) {
                case 1:
                    K = (getkc() + 75) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 2:
                    K = (getkc() + 85) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 3:
                    K = (getkc() + 100) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 4:
                    K = (+getkc() + 120) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
                case 5:
                    K = (getkc() + 145) / (getkc() + Double.parseDouble(cjtemperatura.getText()));
                    break;
            }
        }
        return K;
    }

    /* METODO QUE CARGA LAS TABLAS*/
    public void CargarTablas() {
        /* INICIO DE BLOQUE 'try/catch' */
        try {
            /* REMOVEMOS EL LISTENER DE LA TABLA YA QUE VAMOS A CAMBIAR LODS VALORES DE LA MISMA */
            tablaUno.getModel().removeTableModelListener(listenerTablaUno);
            /* REALIZAMOS CINCO ITERACIONES  */
            for (int i = 0; i < 5; i++) {
                tablaUno.setValueAt("Posic: " + (i + 1), i, 0);
                tablaUno.setValueAt((ACTUALIZANDO) ? tablaUno.getValueAt(i, 2) : 0, i, 2);
                tablaUno.setValueAt((ACTUALIZANDO) ? tablaUno.getValueAt(i, 3) : 0, i, 3);
                tablaUno.setValueAt((ACTUALIZANDO) ? tablaUno.getValueAt(i, 4) : 0, i, 4);
            }
            /* DECLARACION DE VARIABLE LOCAL FACTOR */
            double factor = 1.0;
            tablaUno.setValueAt(Math.round(cjvp.getInt() * factor), conmutador.getSelectedIndex(), 1);
            for (int i = conmutador.getSelectedIndex() + 1; i < conmutador.getItemCount(); i++) {
                //ME PARO UNA FILA DESPUES DE DONDE VA LA POSICION DEL CONMUTADOR
                factor = factor - (factor * 0.025);
                System.out.println("HACIA ABAJO FACTOR ES: " + factor);
                tablaUno.setValueAt(Math.round(cjvp.getInt() * factor), i, 1);
            }
            /* REINICIO DE LA VARIABLE FACTOR */
            factor = 1.0;
            System.out.println("SELECCION ES " + conmutador.getSelectedIndex() + " -> " + (conmutador.getSelectedIndex() - 1));
            for (int i = conmutador.getSelectedIndex() - 1; i >= 0; i--) {
                //ME PARO UNA FILA ANTES DE DONDE VA LA POSICION DEL CONMUTADOR
                factor = factor + (factor * 0.025);
                System.out.println("HACIA ARRIBA FACTOR ES: " + factor);
                tablaUno.setValueAt(Math.round(cjvp.getInt() * factor), i, 1);
            }

            /* INVOCACION DE LOS METODOS SELECTIONMODE Y CELLSELECTIONENABLE */
            tablaUno.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tablaUno.setCellSelectionEnabled(true);

            /* SE DIBUJA LA SEGUNDA TABLA USANDO LOS VALORES DE LA PRIMERA TABLA */
            for (int i = 0; i < tablaUno.getRowCount(); i++) {
                tablaDos.setValueAt(QD((Integer.parseInt(tablaUno.getValueAt(i, 1).toString()) * ((comboFase.getSelectedIndex() == 0) ? 1 : Math.sqrt(3))) / cjvs.getInt(), 3), i, 0);
                tablaDos.setValueAt(QD((Integer.parseInt(tablaUno.getValueAt(i, 1).toString()) * ((comboFase.getSelectedIndex() == 0) ? 1 : Math.sqrt(3))) / cjvs.getInt(), 3) * 0.995, i, 1);
                tablaDos.setValueAt(QD((Integer.parseInt(tablaUno.getValueAt(i, 1).toString()) * ((comboFase.getSelectedIndex() == 0) ? 1 : Math.sqrt(3))) / cjvs.getInt(), 3) * 1.005, i, 2);
            }
            /* INVOCACION DE LOS METODOS SELECTIONMODE Y CELLSELECTIONENABLE */
            tablaDos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tablaDos.setCellSelectionEnabled(true);

            /* EXPESION LAMDA QUE DISPARA LAS ALERTAS EN LA TABLA 1 */
            listenerTablaUno = (TableModelEvent e) -> {
                if (e.getType() == TableModelEvent.UPDATE) {
                    if (e.getColumn() > 1) {
                        if (comboFase.getSelectedIndex() == 0 && e.getColumn() == 2 || comboFase.getSelectedIndex() == 1 && e.getColumn() > 2) {
                            if (alertas == null || alertas.getState() == java.lang.Thread.State.TERMINATED) {
                                alertas = new Hilofases(e.getFirstRow(), e.getColumn());
                                alertas.start();
                            }
                        }
                    }
                }
            };
            /* SE AGREGA EL MODELO LISTENER A LA TABLA UNO */
            tablaUno.getModel().addTableModelListener(listenerTablaUno);
        } catch (NumberFormatException e) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    /* METODO QD
     * RECIVE DOS NUMEROS n Y d DONDE MULTIPLICA n POR 10 A LA d 
     * SOBRE 10 A LA d*/
    double QD(double n, double d) {
        return Math.round(n * Math.pow(10, d)) / Math.pow(10, d);
    }

    /* METODO VOID QUE HABILITA LOS CAMPOS PARA SI EDICION */
    final void habilitarCampos(boolean ver) {
        cjwu.setEnabled(ver);
        cjvw.setEnabled(ver);
        cjyz.setEnabled(ver);
        cjzx.setEnabled(ver);
        cjiv.setEnabled(ver);
        cjiw.setEnabled(ver);
    }

    /* METODO PCU85
     * OBTIENE EL VALOR DEL PCU85 DE RESULTADO DE PCUPROMEDIO MENOS I2R SOBRE K MAS I2R85
     * IMPRIME EL VALOR REDONDEADO EN LA VISTA Y RETORNA EL RESULTADO.
     */
    double PCU85() {
        cjpcua85.setText(String.valueOf(QD(((cjpcumedido.getDouble() - I2R()) / K()) + I2R85(), 1)));
        return QD(((cjpcumedido.getDouble() - I2R()) / K()) + I2R85(), 1);
    }

    /* METODO QUE CALCULA Y MUESTRA EL VALOR DE REG */
    public void HallarReg() {
        /* LINEA ELIMINADA YA QUE NO SE USA EL VALOR ASIGNADO */
        //double REG = QD(Math.sqrt(R85() + Math.pow(X(), 2) + (200 * R85() * 0.8) + (200 * X() * 0.6) + 10000) - 100, 2);
        double REG;
        REG = Math.pow(R85(), 2) + Math.pow(X(), 2) + 200 * R85() * 0.8 + 200 * X() * 0.6 + 10000;
        REG = Math.sqrt(REG);
        REG = REG - 100;
        REG = QD(REG, 2);
        cjreg.setText(String.valueOf(REG));
    }

    /* METODO QUE CALCULA Y MUESTRA EL VALOR DE EF */
    void HallarEf() {
        /* BASICAMENTE ES 0.8 POR EL KVA POR 10 A LA 5 SOBRE 0.8 POR EL KVA POR 10 A LA 3 MAS 
         * EL PROMEDIO MAS EL PCU85*/
        double eficiencia , eficiencia2, eficiencia3, tf;
        final double k = 0.5;
        int vp, fase ;
        vp = cjvp.getInt();
        btnGuardar.setEnabled(true);

        fase = Integer.parseInt(comboFase.getSelectedItem().toString());
        String servicio, minima, tabla;
        minima = null;
        servicio = comboServicio.getSelectedItem().toString();
        if ("NUEVO".equals(servicio)){
            tabla = (vp <= 15000) ?
                    /* EN CASO DE SER POSITIVO LO ANTERIOR SE PROCEDE A EVALUAR SI ES MONOFASICO O TRIFASICO */
                    (fase == 1) ? "monofasiconuevo" : "trifasiconuevo"  :
                    /* EN CASO SER MAYOR A 15000 SE AGREGA LA CONDICION QUE SEA MENOR O IGUAL A 35000 */
                    (vp > 15000 && vp <= 35000) ?
                            /* EN CASO DE SER POSITIVO LO ANTERIOR SE PROCEDE A EVALUAR SI ES MONOFASICO O TRIFASICO */
                            (fase == 1) ? "monofasiconuevoserie35" : "trifasiconuevoserie35"
                            /* EN CASO QUE EL VALOR SUPERE LOS 35000 SE ENVIA UN VALOR NULO */
                            : null;

            tf =
                    (comboMaterialAlta.getSelectedItem().toString().equalsIgnoreCase("COBRE") && comboMaterialBaja.getSelectedItem().equals("COBRE"))
                            ? 0.91
                            : (comboMaterialAlta.getSelectedItem().toString().equalsIgnoreCase("ALUMINIO") && comboMaterialBaja.getSelectedItem().equals("ALUMINIO"))
                            ? 0.90
                            : 0.90;
            eficiencia = (k * cjkva.getDouble()*1000);
            eficiencia2 = ((eficiencia) + cjpomedido.getDouble() + ((Math.pow(k,2))*tf*PCU85()) );
            eficiencia3 = QD((eficiencia/eficiencia2)*100,2);
            double kva = getKva(tabla, cjkva.getDouble());
            System.out.println(kva);
            String sql = "SELECT * FROM " + tabla + " WHERE kva=" + kva;
            conex.conectar();
            ResultSet rs = conex.CONSULTAR(sql);
            try {

                if (rs.next()){
                    System.out.println(eficiencia);
                    System.out.println(eficiencia2);
                    System.out.println(eficiencia3);
                    minima = (eficiencia3 >= rs.getDouble("a") ? "A" :
                            (eficiencia3 < rs.getDouble("a") && eficiencia3 >= rs.getDouble("b") ) ? "B" :
                                    (eficiencia3 < rs.getDouble("b") && eficiencia3 >= rs.getDouble("c") ) ? "C" :
                                            (eficiencia3 < rs.getDouble("c") && eficiencia3 >= rs.getDouble("d") ) ? "D" : null);
                }
                if (minima==null){
                    JOptionPane.showMessageDialog(null, "No Cumple con los Estandares Minimos de Eficiencia");
                    btnGuardar.setEnabled(false);
                }

            } catch (SQLException e) {
                 Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, e);
            }
            cjef2.setVisible(true);
            cjef.setText(String.valueOf(eficiencia3));
            cjef2.setText(String.valueOf(minima));
            System.out.println(cjef2.getText().getClass());
        }else{
        cjef.setText(String.valueOf(QD((0.8 * cjkva.getDouble() * Math.pow(10, 5))
                / (0.8 * cjkva.getDouble() * Math.pow(10, 3) + cjpomedido.getDouble() + PCU85()), 2)));
    }
        }

    /* METODO QUE CARGA LOS VALORES*/
    void cargarValores() {

        /* DECLARACION DE VARIABLES LOCALES NO MODIFICAR */
        String servicio;
        String tabla;
        int ano;
        int vp;
        int fase;

        /* INICIALIZACION DE VARIABLES LOCALES */
        tabla = null;
        ano = cjano.getInt();
        vp = cjvp.getInt();
        fase = Integer.parseInt(comboFase.getSelectedItem().toString());
        servicio = comboServicio.getSelectedItem().toString();

        System.out.println("***************SERVICIO ES: " + servicio + "*************");

        /* CONDICIONALES PARA HALLAR EL TIPO DE TRANSFORMACOR*/
        /* DEPENDIENDO DEL ACEITE */
        if (comboAceite.getSelectedItem().equals("SECO")) {
            //CONDICIONAL SI EL VALOR PRIMARIO ES MENOR O IGUAL A 15000
            tabla = (vp <= 15000) ? "trifasicosecoserie1212" : "trifasicosecoserie1512";
        }
        /* SE EVALUA OTRA CONDICION EN CASO DE QUE LA ANTERIOR SEA IGUAL EN ESTE CASO DEPENDE DEL TIPO DE SERVICIO */
        /* SI EL VALOR DEL SERVICIO ES 'NUEVO' O 'RECONSTRUIDO' O EL CLIENTE SEA 'EMP' SE PROCEDE */
        else if ("NUEVO".equals(servicio) || "RECONSTRUIDO".equals(servicio) || cjcliente.getText().equals("EMPRESAS PUBLICAS DE MEDELLIN S.A E.S.P")) {
            /* CONDICIONALES CON OPERADOR TERNEARIO */
            /* SI VALOR PRIMARIO ES MENOR O IGUAL A 15000 */
            tabla = (vp <= 15000) ? 
                    /* EN CASO DE SER POSITIVO LO ANTERIOR SE PROCEDE A EVALUAR SI ES MONOFASICO O TRIFASICO */
                    (fase == 1) ? "monofasiconuevo" : "trifasiconuevo"  :
                    /* EN CASO SER MAYOR A 15000 SE AGREGA LA CONDICION QUE SEA MENOR O IGUAL A 35000 */
                    (vp > 15000 && vp <= 35000) ? 
                    /* EN CASO DE SER POSITIVO LO ANTERIOR SE PROCEDE A EVALUAR SI ES MONOFASICO O TRIFASICO */
                    (fase == 1) ? "monofasiconuevoserie35" : "trifasiconuevoserie35" 
                    /* EN CASO QUE EL VALOR SUPERE LOS 35000 SE ENVIA UN VALOR NULO */
                    : null;
        }
        /* *SINO* SE EVALUA SI EL VALOR DEL SERVICIO ES 'REPARACION' */
        else if (servicio.equals("REPARACION")) {
            /* EVALUAMOS *SI* EL VALOR PRIMARIO ES MENOR O IGUAL A 15000 */
            if (vp <= 15000) {
                /* ENTRADOS EN EL CONDICIONAL EVALUAMOS LA FASES */
                tabla = (fase == 1) ?
                        //SI ES FASE 1 EVALUAMOS EL AÑO SI ES ANTES O DESPUES DE 1996
                        (ano < 1996) ? "monofasicoantesde1996" : "monofasicodespuesde1996" : 
                        //SI NO ES FASE 1 EVALUAMOS IGUALMENTE SI ES ANTES O DESPUES DE 1996
                        (ano < 1996) ? "trifasicoantesde1996" : "trifasicodespuesde1996";
            }
            /* *SINO* EVALUAMOS SI EL VALOR PRIMARIO ES MAYOR A 15000 Y MENOR O IGUAL A 35000 */
            else if (vp > 15000 && vp <= 35000) {
                /* ENTRADOS EN EL CONDICIONAL EVALUAMOS LAS FASES */
                tabla = (fase == 1) ? 
                        //SI ES FASE 1 EVALUAMOS EL AÑO SI ES ANTES O DESPUES DE 1996
                        (ano <= 1996) ? "monofasicoantesde1996serie35" : "monofasicodespuesde1996serie35" :
                        //SI NO ES FASE 1 EVALUAMOS IGUALMENTE SI ES ANTES O DESPUES DE 1996
                        (ano < 1996) ? "trifasicoantesde1996serie35" : "trifasicodespuesde1996serie46";
            }
            /* *SINO* EVALUALMOS SI EL VOLPATE PRIMARIO ES MAYOR A 35000 Y MENOR O IGUAL QUE 46000 Y SEA DE FASE 3 */
            else if (vp > 35000 && vp <= 46000 && fase == 3) {
                tabla = "trifasicodespuesde1996serie46";
            }
        /* SI EL VALOR DEL SERVICIO ES 'MANTENIMIENTO' */    
        } else if (servicio.equals("MANTENIMIENTO")) {
            /* ENTRADOS EN EL CONDICIONAL EVALUAMOS SI EL ESTADO DEL TRANSFORMADOR ES REPARADO */
            tabla = ESTADO_TRAFO.equals("REPARADO") ?
                    /* SI EL ESTADO ES REPARADO EVALUAMOS SI EL AÑO ES MENOR A 1996 */
                    (ano < 1996) ? 
                    /* SI EL AÑO ES MENOR A 1996 EVALUAMOS SI ES MONOFASICO */
                    (fase == 1) ? "monofasicoantesde1996" : "trifasicoantesde1996" : 
                    /* SI EL AÑO ES MAYOR A 1996 IGUAL EVALUAMOS SI ES MONOFASICO */
                    (fase == 1) ? "monofasicodespuesde1996" : "trifasicodespuesde1996" :
                    /* SI EL ESTADO NO ES REPARADO NO EVALUAMOS EL AÑO SOLO SI ES MONOFASICO */
                    (fase == 1) ? "monofasiconuevo" : "trifasiconuevo";
        }
        /* APESAR DE LO EVALUADO ANTERIORMENTE SE EVALUA SI TIENE CIERTO TIPO DE REFIGERACION */
        if (comboRefrigeracion.getSelectedIndex() == 2) {
            /* ENTRADO EN EL CONDICIONAL SE EVALUA SI ES TRIFASICO Y EL VALOR PRIMARIO OCILA ENTRE 1200 Y 15000 */
            tabla = (fase == 3 && (vp > 1200 && vp <= 15000)) ? "trifasicosecoserie1512" : "trifasicosecoserie1212";
        }
        /* OBTENIDO EL TIPO DE TRANSFORMADOR SE OBTIENE EL KVA */
        double kva = getKva(tabla, cjkva.getDouble());
        System.out.println("************BUSCANDO VALORES DE TABLA************");
        /* OBTENIDO EL KVA PROCEDEMOS A USARLO PARA TRAER VALORES A MOSTRAR */
        String sql = "SELECT * FROM " + tabla + " WHERE kva=" + kva;
        conex.conectar();
        ResultSet rs = conex.CONSULTAR(sql);
        /* INICIO BLOQUE 'try/catch/finally'*/
        try {
            /* Método que mueve el cursor una fila dentro del ResultSet. 
             * Inicialmente el cursor se sitúa antes de la primera fila. 
             * Cuando el cursor se posiciona después de la última fila el método devuelve false. 
             */
            if (rs.next()) {
                /* EN TRADO EN EL METODO SE CARGA LOS VALORES PREDETERMINADO IO PO */
                cjiogarantizado.setText("" + rs.getDouble("io"));
                cjpogarantizado.setText("" + rs.getDouble("po"));
                /* BLOQUE SWITCH QUE EVALUA EL TIPO DE AISLAMIENTO PARA DAR EL VALOR DE PCU */
                switch (comboClaseAislamiento.getSelectedIndex()) {
                    case 0:
                        cjpcugarantizado.setText("" + rs.getInt("pc"));
                        break;
                    case 1:
                        cjpcugarantizado.setText("" + rs.getInt("pc75"));
                        break;
                    case 2:
                        cjpcugarantizado.setText("" + rs.getInt("pc85"));
                        break;
                    case 3:
                        cjpcugarantizado.setText("" + rs.getInt("pc100"));
                        break;
                    case 4:
                        cjpcugarantizado.setText("" + rs.getInt("pc120"));
                        break;
                    case 5:
                        cjpcugarantizado.setText("" + rs.getInt("pc145"));
                        break;
                }
                /* SE CARGA EL VALOR PREDETERMINADO DE IMPEDANCIA */
                cjimpedanciagarantizado.setText("" + rs.getDouble("uz"));
            }
        // CATCH PARA EVITAR QUE EL PROGRAMA SALGA EN CASO DE ALGUN ERROR Y SOLO LO MUESTRE EN CONSOLA
        } catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR EN LA BUSQUEDA DE LOS VALORES EN LA TABLA " + tabla + " CON EL KVA " + kva);
        }
        // FINALIZACION DE LOS RECURSOS
        finally {
            conex.CERRAR();
        }
    }
    
    
    /* METODO QUE OBTIENE EL KVA DE UNA TABLA */
    public static double getKva(String tabla, double KVAdigitada) {
        /* DECLARACION E INICIALIZACION DE VARIABLE LOCAL */
        double kva = 0;
        /* CREACION DE SENTENCIA SQL */
        String sql = "select * from " + tabla + " ORDER BY kva ASC";
        /* INICIALIZACION DE LA CONEXION A LA BASE DE DATOS */
        ConexionBD conex = new ConexionBD();
        conex.conectar();
        /* OBTENCION DEL CONJUNTO DE RESULTADOS DE LA BASE DE DATOS */
        ResultSet rs = conex.CONSULTAR(sql);
        /* DECLARACION E INICIALIZACION DE VARIABLE BOOLEANA */
        boolean esta = false;
        /* INICIO DE BLOQUE 'try/catch/finally' */
        try {
            /* DECLARACION DE VARIABLE LOCAL */ 
            double BD, auxiliar = 0;
            /* INICIO DE CICLO QUE RECORRE EL CONJUNTO DE RESULTADOS */
            while (rs.next()) {
                System.out.println(rs.getDouble(1));
                /* OBTENEMOS EL KVA EN ESTA ITERACION */
                BD = rs.getDouble("kva");
                /* CONDICIONAL SI QUE EVALUA SI EL KVA INGRESADO ESTA EN LA BASE DE DATOS 
                 * SI LA CONDICION ES VERDADERA AGRADA EL VALOR DEL KVA DE LA BD A KVA INGRESADO
                 * SE CAMBIA EL VALOR DEL BOOLEANO A 'true' Y SE UNA LA SENTENCIA 'break' PARA
                 * SALIR DEL CICLO Y NO EVALUAR MAS
                 */
                if (BD == KVAdigitada) {
                    kva = BD;
                    esta = true;
                    break;
                } 
                /* *SINO* SI KVA DE LA BASE DE DATOS ES MEYOR QUE EL INGRESADO GUARDA EL VALOR
                 * EN LA VARIABLE AUXILIAR
                 */
                else if (BD < KVAdigitada) {
                    auxiliar = BD;
                }
                /* *SINO* SI EL KVA DIGITADO ES MENOR QUE LA SUMA DE LA VARIABLE AUXILIAR Y EL
                 * KVA DE LA BASE DE DATOS SOBRE DOS. SE IGUALA EL KVA AL VALOR DE LA VARIABLE
                 * AUXILIAR SE ENVIA UN 'TRUE AL BOLEANO Y SE EJECUTA EL 'break''
                 */
                else if (KVAdigitada < ((auxiliar + BD) / 2)) {
                    kva = auxiliar;
                    esta = true;
                    break;
                }
                /* *SINO* SE ENVIA EL KVA DE LA BD Y SE SALE DEL CICLO */
                else {
                    kva = BD;
                    esta = true;
                    break;
                }
            }
            /* INVOCACION DE VENTANA EN CASO QUE NO SE ENCUENTE EL KVA DIGITADO EN LA PLACA. */
            if (!esta) {
                JOptionPane.showMessageDialog(null, "NO SE ENCONTRO LA POTENCIA DE " + KVAdigitada + " EN LA TABLA " + tabla);
            }
        }
        /* ENVIO DEL ERROR AL LOG DE LA CONSOLA */
        catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al buscar el kva mas adecuado\n" + ex);
        }
        /* FINALIZACION DE RECURSOS */
        finally {
            conex.CERRAR();
        }
        return kva;
    }
    
    
    
    /* METODO QUE NOS PERMITE CARGAR LAS MEDIDAS EN LOS CAMPO DE LA TABLA */
    void cargarMedidas() {
        /* OBTENEMOS EL KVA DE LA TABA */
        double kva = cjkva.getDouble();
        /* OBTENEMOS EL ANCHO SEGUN SU FASE 
         * ENTRAMOS EN DOS SERIES DE SI ANIDADOS DEPENDIENDO DEL LA BASE
         */
        cjancho.setText((comboFase.getSelectedIndex() == 0) ?
                /* ENTRADOS EN LA CONCIDONAL SI ES VERDADERA ESCOGEMOS EL TRANSFORMADOR MONOFASICO */
                ((kva == 3) ? "250" : 
                        (kva == 5) ? "280" : 
                                (kva == 10) ? "320" : 
                                        (kva == 15) ? "350" : 
                                                (kva == 25) ? "380" : 
                                                        (kva == 37.5) ? "420" : 
                                                                (kva == 50) ? "450" : 
                                                                        (kva == 75) ? "480" : "0") :
                /* SI ES FALSA IGUAL EVALUAMOS EL KVA DEL TRIFASICO */
                ((kva == 15) ? "350" : 
                        (kva == 30) ? "500" : 
                                (kva == 45) ? "600" : 
                                        (kva == 75) ? "620" : 
                                                (kva == 112.5) ? "700" : 
                                                        (kva == 150) ? "750" : 
                                                                (kva == 225) ? "800" : "0"));
        /* OBTENEMOS EL LARGO SEGUN SU FASE 
         * ENTRAMOS EN DOS SERIES DE SI ANIDADOS DEPENDIENDO DEL LA BASE
         */
        cjlargo.setText((comboFase.getSelectedIndex() == 0) ?
                /* ENTRADOS EN LA CONCIDONAL SI ES VERDADERA ESCOGEMOS EL TRANSFORMADOR MONOFASICO */
                ((kva == 3) ? "250" : 
                        (kva == 5) ? "280" : 
                                (kva == 10) ? "320" : 
                                        (kva == 15) ? "350" : 
                                                (kva == 25) ? "380" : 
                                                        (kva == 37.5) ? "420" : 
                                                                (kva == 50) ? "450" : 
                                                                        (kva == 75) ? "480" : "0") : 
                /* SI ES FALSA IGUAL EVALUAMOS EL KVA DEL TRIFASICO */
                ((kva == 15) ? "280" : 
                        (kva == 30) ? "320" : 
                                (kva == 45) ? "350" : 
                                        (kva == 75) ? "380" : 
                                                (kva == 112.5) ? "430" : 
                                                        (kva == 150) ? "480" : 
                                                                (kva == 225) ? "520" : "0"));
        /* OBTENEMOS EL ALTO SEGUN SU FASE 
         * ENTRAMOS EN DOS SERIES DE SI ANIDADOS DEPENDIENDO DEL LA FASE
         */
        cjalto.setText((comboFase.getSelectedIndex() == 0) ? 
                /* ENTRADOS EN LA CONCIDONAL SI ES VERDADERA ESCOGEMOS EL TRANSFORMADOR MONOFASICO */
                ((kva == 3) ? "450" : 
                        (kva == 5) ? "500" : 
                                (kva == 10) ? "550" : 
                                        (kva == 15) ? "550" : 
                                                (kva == 25) ? "550" : 
                                                        (kva == 37.5) ? "600" : 
                                                                (kva == 50) ? "650" : 
                                                                        (kva == 75) ? "700" : "0") : 
                /* SI ES FALSA IGUAL EVALUAMOS EL KVA DEL TRIFASICO */
                ((kva == 15) ? "500" : 
                        (kva == 30) ? "500" : 
                                (kva == 45) ? "550" : 
                                        (kva == 75) ? "600" : 
                                                (kva == 112.5) ? "650" : 
                                                        (kva == 150) ? "700" : 
                                                                (kva == 225) ? "750" : "0"));
        /* OBTENEMOS LOS ELEMENTOS SEGUN SU FASE 
         * ENTRAMOS EN DOS SERIES DE SI ANIDADOS DEPENDIENDO DEL LA FASE
         */
        cjelementos.setText((comboFase.getSelectedIndex() == 0) ?
                /* ENTRADOS EN LA CONCIDONAL SI ES VERDADERA ESCOGEMOS EL TRANSFORMADOR MONOFASICO */
                ((kva == 50) ? "6" : 
                        (kva == 75) ? "8" : "0") :
                /* SI ES FALSA IGUAL EVALUAMOS EL KVA DEL TRIFASICO */
                ((kva == 75) ? "6" : 
                        (kva == 112.5) ? "10" : 
                                (kva == 150) ? "14" : 
                                        (kva == 225) ? "18" : "0"));
        /* OBTENEMOS EL LARGO ELEMENTOS SEGUN SU FASE 
         * ENTRAMOS EN DOS SERIES DE SI ANIDADOS DEPENDIENDO DEL LA FASE
         */
        cjlargoelemento.setText((comboFase.getSelectedIndex() == 0) ?
                /* ENTRADOS EN LA CONCIDONAL SI ES VERDADERA ESCOGEMOS EL TRANSFORMADOR MONOFASICO */
                ((kva == 50 || kva == 75) ? "300" : "0") :
                /* SI ES FALSA IGUAL EVALUAMOS EL KVA DEL TRIFASICO */
                ((kva == 75) ? "300" : 
                        (kva == 112.5) ? "380" : 
                                (kva == 150) ? "300" : 
                                        (kva == 225) ? "300" : "0"));
        /* OBTENEMOS EL ALTO ELEMENTOS SEGUN SU FASE 
         * ENTRAMOS EN DOS SERIES DE SI ANIDADOS DEPENDIENDO DEL LA FASE
         */
        cjaltoelemento.setText((comboFase.getSelectedIndex() == 0) ? 
                /* ENTRADOS EN LA CONCIDONAL SI ES VERDADERA ESCOGEMOS EL TRANSFORMADOR MONOFASICO */
                ((kva == 50) ? "480" : 
                        (kva == 75) ? "480" : "0") : 
                /* SI ES FALSA IGUAL EVALUAMOS EL KVA DEL TRIFASICO */
                ((kva == 75) ? "480" : 
                        (kva == 112.5) ? "380" : 
                                (kva == 150) ? "480" : 
                                        (kva == 225) ? "480" : "0"));
        /* SELECCIONAMOS EL COLOR VERDE O GRIS SI ES PARA EPM O NO */
        cjcolor.setText((cjcliente.getText().equals("EMPRESAS PUBLICAS DE MEDELLIN S.A E.S.P")) ? "VERDE" : "GRIS");
    }
    /* METODO T 
     * OBTIENE EL VALOR DE UN REGISTRI EN UNA DELDA EN LA TABLA 1
     */
    Object getT(int r, int col) {
        return "'" + tablaUno.getValueAt(r, col) + "'";
    }
    
    /* METODO QUE ENVIA LOS DATOS DEL PROTOCOLO A LA BASE DE DATOS */
    void guardarProtocolo() {
        (new Thread() {
            @Override
            public void run() {
                String GUARDAR;
                if (!ACTUALIZANDO) {
                    /* SE ENVIA A LA BASE DE DATOS LOS VALORES QUE SE QUIERAN CREAR */
                    GUARDAR = "INSERT INTO public.protocolos(\n"
                            + "            idtransformador, codigo, frecuencia, refrigeracion, \n"
                            + "            tensionserie, nba, calentamientodevanado, claseaislamiento, alturadiseno, \n"
                            + "            derivacionprimaria, i1, i2, temperaturadeensayo, conmutador, \n"
                            + "            liquidoaislante, referenciadeaceite, tensionderuptura, metodo, \n"
                            + "            tiemporalt, tensiondeprueba, atcontrabt, \n"
                            + "            atcontratierra, btcontratierra, grupodeconexion, polaridad, punou, \n"
                            + "            punov, punow, pdosu, pdosv, pdosw, ptresu, ptresv, ptresw, pcuatrou, \n"
                            + "            pcuatrov, pcuatrow, pcincou, pcincov, pcincow, resuv, resvw, \n"
                            + "            reswu, proresuno, materialconductoralta, resxy, resyz, reszx, \n"
                            + "            proresdos, materialconductorbaja, iu, iv, iw, promedioi, iogarantizado, \n"
                            + "            pomedido, pogarantizado, vcc, pcu, pcua85, pcugarantizado, i2r, \n"
                            + "            i2ra85, impedancia, impedancia85, impedanciagarantizada, reg, \n"
                            + "            ef, largotanque, anchotanque, altotanque, color, espesor, radiadores, \n"
                            + "            largoradiador, altoradiador, observaciones, fechalaboratorio, \n"
                            + "            fechaderegistro, estadoservicio, garantia, idusuario, btcontraatytierra, atcontrabtytierra, niveleficiencia)\n"
                            + "    VALUES (" + IDTRAFO + ", '" + cjprotocolo.getText() + "', '" + comboFrecuencia.getSelectedItem() + "', '" + comboRefrigeracion.getSelectedItem() + "', \n"
                            + "            '" + cjtensionSerie.getText() + "', '" + cjnba.getText() + "', '" + cjcalentamientodevanado.getText() + "', '" + comboClaseAislamiento.getSelectedItem() + "', '" + cjaltdiseno.getText() + "', \n"
                            + "            '" + comboDerivacion.getSelectedItem() + "', '" + cji1.getText() + "', '" + cji2.getText() + "', '" + cjtemperatura.getText() + "', '" + conmutador.getSelectedItem() + "', \n"
                            + "            '" + comboAceite.getSelectedItem() + "', '" + comboReferenciaAceite.getSelectedItem() + "', '" + cjRuptura.getText() + "', '" + cjmetodo.getText() + "', \n"
                            + "            '" + cjtiemporalt.getText() + "', '" + comboTensionPrueba.getSelectedItem() + "', '" + cjATcontraBT.getText() + "', \n"
                            + "            '" + cjATcontraTierra.getText() + "', '" + cjBTcontraTierra.getText() + "', '" + comboGrupoConexion.getSelectedItem() + "', '" + comboPolaridad.getSelectedItem() + "', \n"
                            + "            " + getT(0, 2) + ", " + getT(0, 3) + ", " + getT(0, 4) + ",  \n"
                            + "            " + getT(1, 2) + ", " + getT(1, 3) + ", " + getT(1, 4) + ",  \n"
                            + "            " + getT(2, 2) + ", " + getT(2, 3) + ", " + getT(2, 4) + ",  \n"
                            + "            " + getT(3, 2) + ", " + getT(3, 3) + ", " + getT(3, 4) + ",  \n"
                            + "            " + getT(4, 2) + ", " + getT(4, 3) + ", " + getT(4, 4) + ",  \n"
                            + "            '" + cjuv.getDouble() + "', '" + cjvw.getDouble() + "','" + cjwu.getDouble() + "', '" + cjproresalta.getText() + "',  \n"
                            + "            '" + comboMaterialAlta.getSelectedItem() + "', '" + cjxy.getDouble() + "', '" + cjyz.getDouble() + "', '" + cjzx.getDouble() + "','" + cjproresbaja.getText() + "', '" + comboMaterialBaja.getSelectedItem() + "', "
                            + " '" + cjiu.getDouble() + "', '" + cjiv.getDouble() + "', '" + cjiw.getDouble() + "', '" + cjpromedioi.getText() + "', '" + cjiogarantizado.getText() + "','" + cjpomedido.getText() + "', '" + cjpogarantizado.getText() + "', "
                            + " '" + cjvcc.getText() + "', '" + cjpcumedido.getText() + "', '" + cjpcua85.getText() + "', '" + cjpcugarantizado.getText() + "', '" + cji2r.getText() + "','" + cji2ra85.getText() + "', '" + cjimpedancia.getText() + "', "
                            + " '" + cjimpedancia85.getText() + "', '" + cjimpedanciagarantizado.getText() + "', '" + cjreg.getText() + "', \n"
                            + "            '" + cjef.getText() + "', '" + cjlargo.getText() + "', '" + cjancho.getText() + "', '" + cjalto.getText() + "', '" + cjcolor.getText() + "', '" + cjespesor.getText() + "', '" + cjelementos.getText() + "', \n"
                            + "            '" + cjlargoelemento.getText() + "', '" + cjaltoelemento.getText() + "', '" + cjobservaciones.getText() + "', '" + cjfechasalida.getDate() + "', \n"
                            + "            '" + new java.util.Date() + "', '" + ESTADO_TRAFO + "' , '" + checkGarantia.isSelected() + "' , " + sesion.getIdUsuario() + ", " + cjBTcontraATyTierra.getText() + ", " + cjATcontraBTyTierra.getText() + ",'" + cjef2.getText() + "')";
                } else {
                    /* *SINO* SE ENVIAN A LA BASE DE DATOS */
                    GUARDAR = "UPDATE public.protocolos SET\n"
                            + "            frecuencia='" + comboFrecuencia.getSelectedItem() + "', refrigeracion='" + comboRefrigeracion.getSelectedItem() + "', \n"
                            + "            tensionserie='" + cjtensionSerie.getText() + "', nba='" + cjnba.getText() + "', calentamientodevanado='" + cjcalentamientodevanado.getText() + "', claseaislamiento='" + comboClaseAislamiento.getSelectedItem() + "', alturadiseno='" + cjaltdiseno.getText() + "', \n"
                            + "            derivacionprimaria='" + comboDerivacion.getSelectedItem() + "', i1='" + cji1.getText() + "', i2='" + cji2.getText() + "', temperaturadeensayo='" + cjtemperatura.getText() + "', conmutador='" + conmutador.getSelectedItem() + "', \n"
                            + "            liquidoaislante='" + comboAceite.getSelectedItem() + "', referenciadeaceite='" + comboReferenciaAceite.getSelectedItem() + "', tensionderuptura='" + cjRuptura.getText() + "', metodo='" + cjmetodo.getText() + "', \n"
                            + "            tiemporalt='" + cjtiemporalt.getText() + "', tensiondeprueba='" + comboTensionPrueba.getSelectedItem() + "', atcontrabt='" + cjATcontraBT.getText() + "', \n"
                            + "            atcontratierra='" + cjATcontraTierra.getText() + "', btcontratierra='" + cjBTcontraTierra.getText() + "', grupodeconexion='" + comboGrupoConexion.getSelectedItem() + "', polaridad='" + comboPolaridad.getSelectedItem() + "', punou=" + getT(0, 2) + ", \n"
                            + "            punov=" + getT(0, 3) + ", punow=" + getT(0, 4) + ", pdosu=" + getT(1, 2) + ", pdosv=" + getT(1, 3) + ", pdosw=" + getT(1, 4) + ", ptresu=" + getT(2, 2) + ", ptresv=" + getT(2, 3) + ", ptresw=" + getT(2, 4) + ", pcuatrou=" + getT(3, 2) + ", \n"
                            + "            pcuatrov=" + getT(3, 3) + ", pcuatrow=" + getT(3, 4) + ", pcincou=" + getT(4, 2) + ", pcincov=" + getT(4, 3) + ", pcincow=" + getT(4, 4) + ", resuv='" + cjuv.getDouble() + "', resvw='" + cjvw.getDouble() + "', \n"
                            + "            reswu='" + cjwu.getDouble() + "', proresuno='" + cjproresalta.getText() + "', materialconductoralta='" + comboMaterialAlta.getSelectedItem() + "', resxy='" + cjxy.getDouble() + "', resyz='" + cjyz.getDouble() + "', reszx='" + cjzx.getDouble() + "', \n"
                            + "            proresdos='" + cjproresbaja.getText() + "', materialconductorbaja='" + comboMaterialBaja.getSelectedItem() + "', iu='" + cjiu.getDouble() + "', iv='" + cjiv.getDouble() + "', iw='" + cjiw.getDouble() + "', promedioi='" + cjpromedioi.getText() + "', iogarantizado='" + cjiogarantizado.getText() + "', \n"
                            + "            pomedido='" + cjpomedido.getText() + "', pogarantizado='" + cjpogarantizado.getText() + "', vcc='" + cjvcc.getText() + "', pcu='" + cjpcumedido.getText() + "', pcua85='" + cjpcua85.getText() + "', pcugarantizado='" + cjpcugarantizado.getText() + "', i2r='" + cji2r.getText() + "', \n"
                            + "            i2ra85='" + cji2ra85.getText() + "', impedancia='" + cjimpedancia.getText() + "', impedancia85='" + cjimpedancia85.getText() + "', impedanciagarantizada='" + cjimpedanciagarantizado.getText() + "', reg='" + cjreg.getText() + "', \n"
                            + "            ef='" + cjef.getText() + "', largotanque='" + cjlargo.getText() + "', anchotanque='" + cjancho.getText() + "', altotanque='" + cjalto.getText() + "', color='" + cjcolor.getText() + "', espesor='" + cjespesor.getText() + "', radiadores='" + cjelementos.getText() + "', \n"
                            + "            largoradiador='" + cjlargoelemento.getText() + "', altoradiador='" + cjaltoelemento.getText() + "', observaciones='" + cjobservaciones.getText() + "', fechalaboratorio='" + cjfechasalida.getDate() + "', \n"
                            + "            estadoservicio='" + ESTADO_TRAFO + "' , garantia='" + checkGarantia.isSelected() + "' , idusuario=" + sesion.getIdUsuario() + ", btcontraatytierra=" + cjBTcontraATyTierra.getText() + " , atcontrabtytierra=" + cjATcontraBTyTierra.getText() + " , niveleficiencia= '" + cjef2.getText() + "' WHERE idprotocolo=" + IDPROTOCOLO + " ";
                }
                /* ENVIAMOS EL SQL A LA METODO QUE LA ENVIARA A LA BASE DE DATOS */
                if (conex.GUARDAR(GUARDAR)) {
                    /* INVOCAMOS UN AVISO QUE NOS DICE EL SI SE REGISTRO O ACTUALIZO UN PROTOCOLO */
                    modelo.Metodos.M("PROTOCOLO " + ((ACTUALIZANDO) ? "ACTUALIZADO" : "REGISTRADO"), "bien.png");
                    /* BLOQUE try/catch/finally QUE CREA Y GUARDA EL REPORTE EN PDF */
                    try {
                        btnGuardar.setEnabled(false);
                        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/gif.gif")));
                        JasperReport reporte;
                        String servicio = String.valueOf((comboServicio.getSelectedItem().toString()));
                        if ("NUEVO".equals(servicio) || "FABRICACION".equals(servicio)){
                            System.out.println("Verdad");
                            reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PROTOCOLO_NUEVO.jasper").toString()));
                        }else {
                            System.out.println("Falso");
                            reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PROTOCOLO.jasper").toString()));
                        }
                        //JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PROTOCOLO.jasper").toString()));
                        Map<String, Object> p = new HashMap<>();
                        p.put("IDPROTOCOLO", (ACTUALIZANDO) ? IDPROTOCOLO : modelo.Metodos.getUltimoID("protocolos", "idprotocolo"));
                        JasperPrint jasperprint = JasperFillManager.fillReport(reporte, p, conex.conectar());
                        if (mostrarProtocolo.isSelected()) {
                            JasperViewer.viewReport(jasperprint, false);
                        }
                        //314 415 5422
                        String protocolo = cjprotocolo.getText();
                        String cliente = cjcliente.getText().replace("/", "");
                        limpiar();
                        cjprotocolo.setText("A-" + modelo.Metodos.getConsecutivoRemision("protocolo", false) + "-" + new SimpleDateFormat("yy").format(new java.util.Date()));
                        JasperExportManager.exportReportToPdfFile(
                                jasperprint,
                                System.getProperties().getProperty("user.dir") + "\\PROTOCOLOS PDF\\" + protocolo + "_" + cliente + ".pdf");

//                        if(!ACTUALIZANDO){
//                            cjprotocolo.setText("A-"+modelo.Metodos.getConsecutivoRemision("protocolo", true)+"-"+new SimpleDateFormat("yy").format(new java.util.Date()));
//                        }else{
//                        }
                    /* catch PARA LOS DIFERENTES ERRORES */
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
                        modelo.Metodos.escribirFichero(ex);
                        modelo.Metodos.ERROR(ex, "ERROR AL GENERAR EL PROTOCOLO");
                    } catch (MalformedURLException | JRException ex) {
                        Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
                        modelo.Metodos.ERROR(ex, "ERROR AL EXPORTAR Y GUARDAR EL ARCHIVO PDF EN LA CARPTEA 'PROTOCOLOS PDF'\nVERIFIQUE QUE EL NOMBRE DEL CLIENTE NO CONTENGA PUNTOS NI CARACTERES ESPECIALES.");
                    /* FINALIZACION DE RECURSOS */
                    } finally {
                        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/Guardar.png")));
                        btnGuardar.setEnabled(true);
                    }
                }
            }
        }).start();
    }

    /* METODO QUE CARGA UN RESULTADO DE LA BASE DE DATOS, CARGA Y LO MUESTRA EN LA VISTA */
    void abrirProtocolo() {
        /* CONEXION A L BD */
        conex.conectar();
        /* CONJUNTO DE RESULTADOS */
        ResultSet rs = conex.CONSULTAR("SELECT * FROM protocolos INNER JOIN transformador t USING(idtransformador) "
                + "INNER JOIN entrada e ON t.identrada=e.identrada INNER JOIN cliente c ON c.idcliente=e.idcliente WHERE idprotocolo=" + IDPROTOCOLO);
        /* BLOQUE 'try/catch' */
        try {
            /* RECORRIDO DEL CONJUNTO DE RESULTADOS */
            if (rs.next()) {
                /* CARGUE DE LOS VALORES EN LOS CAMPOS DE LA VISTA */
                ESTADO_TRAFO = rs.getString("estadoservicio");
                comboServicio.setSelectedItem(rs.getString("serviciosalida"));
                ACTUALIZANDO = true;
                cjserie.setText(rs.getString("numeroserie"));
                cjprotocolo.setText(rs.getString("codigo"));
                cjcliente.setText(rs.getString("nombrecliente"));
                cjlote.setText(rs.getString("lote"));
                cjempresa.setText(rs.getString("numeroempresa"));
                cjmarca.setText(rs.getString("marca"));
                cjkva.setText(rs.getString("kvasalida"));
                comboFase.setSelectedItem(rs.getString("fase"));
                cjano.setText(rs.getString("ano"));
                cjvp.setText(rs.getString("tps"));
                cjvs.setText(rs.getString("tss"));
                cjtensionBT.setText(String.valueOf(rs.getInt("tss") * 2));
                cjTensionBT2.setText(rs.getString("tss"));
                cjmasa.setText(rs.getString("peso"));
                cjaceite.setText(rs.getString("aceite"));
                CargarTablas();
                habilitarCampos(rs.getString("fase").equals("3"));
                cjcliente.setCaretPosition(0);
                comboFrecuencia.setSelectedItem(rs.getString("frecuencia"));
                comboRefrigeracion.setSelectedItem(rs.getString("refrigeracion"));
                cjtensionSerie.setText(rs.getString("tensionserie"));
                cjnba.setText(rs.getString("nba"));
                cjcalentamientodevanado.setText(rs.getString("calentamientodevanado"));
                comboClaseAislamiento.setSelectedItem(rs.getString("claseaislamiento"));
                cjaltdiseno.setText(rs.getString("alturadiseno"));
                cji1.setText("" + rs.getDouble("i1"));
                cji2.setText(rs.getString("i2"));
                comboDerivacion.setSelectedItem(rs.getString("derivacionprimaria"));
                cjtemperatura.setText(rs.getString("temperaturadeensayo"));
                conmutador.setSelectedItem(rs.getString("conmutador"));
                comboAceite.setSelectedItem(rs.getString("liquidoaislante"));
                comboReferenciaAceite.setSelectedItem(rs.getString("referenciadeaceite"));
                cjRuptura.setText(rs.getString("tensionderuptura"));
                cjmetodo.setText(rs.getString("metodo"));
                cjtiemporalt.setText(rs.getString("tiemporalt"));
                comboTensionPrueba.setSelectedItem(rs.getString("tensiondeprueba"));
                cjATcontraBT.setText(rs.getString("atcontrabt"));
                cjATcontraTierra.setText(rs.getString("atcontratierra"));
                cjBTcontraTierra.setText(rs.getString("btcontratierra"));
                comboGrupoConexion.setSelectedItem(rs.getString("grupodeconexion"));
                comboPolaridad.setSelectedItem(rs.getString("polaridad"));
                tablaUno.setValueAt(rs.getDouble("punou"), 0, 2);
                tablaUno.setValueAt(rs.getDouble("punov"), 0, 3);
                tablaUno.setValueAt(rs.getDouble("punow"), 0, 4);
                tablaUno.setValueAt(rs.getDouble("pdosu"), 1, 2);
                tablaUno.setValueAt(rs.getDouble("pdosv"), 1, 3);
                tablaUno.setValueAt(rs.getDouble("pdosw"), 1, 4);
                tablaUno.setValueAt(rs.getDouble("ptresu"), 2, 2);
                tablaUno.setValueAt(rs.getDouble("ptresv"), 2, 3);
                tablaUno.setValueAt(rs.getDouble("ptresw"), 2, 4);
                tablaUno.setValueAt(rs.getDouble("pcuatrou"), 3, 2);
                tablaUno.setValueAt(rs.getDouble("pcuatrov"), 3, 3);
                tablaUno.setValueAt(rs.getDouble("pcuatrow"), 3, 4);
                tablaUno.setValueAt(rs.getDouble("pcincou"), 4, 2);
                tablaUno.setValueAt(rs.getDouble("pcincov"), 4, 3);
                tablaUno.setValueAt(rs.getDouble("pcincow"), 4, 4);
                cjuv.setText("" + rs.getDouble("resuv"));
                cjvw.setText("" + rs.getDouble("resvw"));
                cjwu.setText("" + rs.getDouble("reswu"));
                cjproresalta.setText("" + rs.getDouble("proresuno"));
                comboMaterialAlta.setSelectedItem(rs.getString("materialconductoralta"));
                cjxy.setText("" + rs.getDouble("resxy"));
                cjyz.setText("" + rs.getDouble("resyz"));
                cjzx.setText("" + rs.getDouble("reszx"));
                cjproresbaja.setText("" + rs.getDouble("proresdos"));
                comboMaterialBaja.setSelectedItem(rs.getString("materialconductorbaja"));
                cjtensionBT.setText(rs.getString("tss"));
                cjiu.setText("" + rs.getDouble("iu"));
                cjiv.setText("" + rs.getDouble("iv"));
                cjiw.setText("" + rs.getDouble("iw"));
                cjpromedioi.setText("" + rs.getDouble("promedioi"));
                cjiogarantizado.setText("" + rs.getDouble("iogarantizado"));
                cjpomedido.setText(rs.getString("pomedido"));
                cjpogarantizado.setText("" + rs.getDouble("pogarantizado"));
                cjvcc.setText(rs.getString("vcc"));
                cjpcumedido.setText(rs.getString("pcu"));
                cjpcua85.setText("" + rs.getDouble("pcua85"));
                cjpcugarantizado.setText(rs.getString("pcugarantizado"));
                cji2r.setText("" + rs.getDouble("i2r"));
                cji2ra85.setText("" + rs.getDouble("i2ra85"));
                cjimpedancia.setText("" + rs.getDouble("impedancia"));
                cjimpedancia85.setText("" + rs.getDouble("impedancia85"));
                cjimpedanciagarantizado.setText("" + rs.getDouble("impedanciagarantizada"));
                cjreg.setText(rs.getString("reg"));
                cjef.setText("" + rs.getDouble("ef"));
                cjef2.setText(rs.getString("niveleficiencia"));
                cjobservaciones.setText(rs.getString("observaciones"));
                cjfechasalida.setDate(rs.getDate("fechalaboratorio"));
                cjlargo.setText(rs.getString("largotanque"));
                cjancho.setText(rs.getString("anchotanque"));
                cjalto.setText(rs.getString("altotanque"));
                cjcolor.setText(rs.getString("color"));
                cjespesor.setText(rs.getString("espesor"));
                cjelementos.setText(rs.getString("radiadores"));
                cjlargoelemento.setText(rs.getString("largoradiador"));
                cjaltoelemento.setText(rs.getString("altoradiador"));
                jTabbedPane1.setSelectedIndex(0);
            }
        }
        /* CAPTURA DEL ERROR EN LA SENTENCIA SQL */
        catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /* METODO QUE LIMPIA LOS VALORES DE LA VISTA COLOCANDO OTROS VALORES POR DEFECTO  */
    void limpiar() {
        ESTADO_TRAFO = null;
        Component c = jTabbedPane1.getComponentAt(0);
        JPanel p = (JPanel) c;
        for (Component com : p.getComponents()) {
            if (com instanceof JPanel) {
                JPanel panel = (JPanel) com;
                for (Component txt : panel.getComponents()) {
                    if (txt instanceof JTextField) {
                        ((JTextField) txt).setText("");
                    }
                }
            }
        }
        cjcalentamientodevanado.setText("65");
        cjtemperatura.setText("30");
        cjRuptura.setText("40");
        cjmetodo.setText("ASTM 877");
        cjBTcontraATyTierra.setText("10");
        cjATcontraBTyTierra.setText("34.5");
        cjtiempoaplicado.setText("60");
        cjFrecuenciaInducida.setText("414");
        cjtiempoInducido.setText("17");
        cjespesor.setText("110");
        cjobservaciones.setText("");
        cjtiemporalt.setText("60");
        btnGuardar.setIcon(new ImageIcon(getClass().getResource("/recursos/images/Guardar.png")));
        btnGuardar.setEnabled(true);
        checkGarantia.setSelected(false);
    }
    
    /* METODO FINAL QUE NO PUEDE SER ANUADO "OVERRIDE"  */
    final void cargarProtocolos() {
        modeloTabla = new CustomTableModel(
                new Object[][]{},
                modelo.PROTOCOLO.getColumnNames(),
                tablaProtocolos,
                modelo.PROTOCOLO.getColumnClass(),
                modelo.PROTOCOLO.getColumnEditables());
        modelo.PROTOCOLO.cargarProtocolos(modeloTabla);

        tablaProtocolos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaProtocolos.setCellSelectionEnabled(true);
        tablaProtocolos.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selectNextColumnCell");

        ajustarColumna.adjustColumns();
        tablaProtocolos.getColumnModel().getColumn(0).setCellRenderer(new JButtonIntoJTable.BotonEnColumna());

        rowSorter = new TableRowSorter(modeloTabla);
        tablaProtocolos.setRowSorter(rowSorter);
    }

    /* METOODO QUE BUSCA Y FILTRA LA TABLA DE PROTOCOLOS */
    void buscarProtocolo() {
        RowFilter<TableModel, Object> serie = RowFilter.regexFilter(cjbuscarPorSerie.getText().toUpperCase(), 3);
        RowFilter<TableModel, Object> cliente = RowFilter.regexFilter((comboCliente.getSelectedIndex() > 0) ? comboCliente.getSelectedItem().toString() : "", 2);
        RowFilter<TableModel, Object> lote = RowFilter.regexFilter(cjBuscarPorLote.getText(), 9);
        RowFilter<TableModel, Object> marca = RowFilter.regexFilter(cjBuscarPorMarca.getText().toUpperCase(), 5);
        RowFilter<TableModel, Object> despacho = RowFilter.regexFilter(cjdespacho.getText().toUpperCase(), 11);
        List<RowFilter<TableModel, Object>> filters = new ArrayList<>();
        filters.add(serie);
        filters.add(cliente);
        filters.add(lote);
        filters.add(marca);
        filters.add(despacho);
        compoundRowFilter = RowFilter.andFilter(filters);
        rowSorter.setRowFilter(compoundRowFilter);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        menuProtocolos = new javax.swing.JPopupMenu();
        subMenuAbrirProtocolo = new javax.swing.JMenuItem();
        subMenuEliminar = new javax.swing.JMenuItem();
        cjef1 = new CompuChiqui.JTextFieldPopup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panelInformacionGeneral = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cjprotocolo = new CompuChiqui.JTextFieldPopup();
        jLabel80 = new javax.swing.JLabel();
        cjserie = new CompuChiqui.JTextFieldPopup();
        jLabel4 = new javax.swing.JLabel();
        cjempresa = new CompuChiqui.JTextFieldPopup();
        jLabel5 = new javax.swing.JLabel();
        cjmarca = new CompuChiqui.JTextFieldPopup();
        jLabel6 = new javax.swing.JLabel();
        cjkva = new CompuChiqui.JTextFieldPopup();
        jLabel8 = new javax.swing.JLabel();
        comboFase = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cjano = new CompuChiqui.JTextFieldPopup();
        jLabel9 = new javax.swing.JLabel();
        cjvp = new CompuChiqui.JTextFieldPopup();
        jLabel10 = new javax.swing.JLabel();
        cjvs = new CompuChiqui.JTextFieldPopup();
        jLabel1 = new javax.swing.JLabel();
        comboServicio = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        comboFrecuencia = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        comboRefrigeracion = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        cjtensionSerie = new CompuChiqui.JTextFieldPopup();
        jLabel13 = new javax.swing.JLabel();
        cjnba = new CompuChiqui.JTextFieldPopup();
        jLabel14 = new javax.swing.JLabel();
        cjcalentamientodevanado = new CompuChiqui.JTextFieldPopup();
        jLabel15 = new javax.swing.JLabel();
        comboClaseAislamiento = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        cjaltdiseno = new CompuChiqui.JTextFieldPopup();
        jLabel17 = new javax.swing.JLabel();
        cji1 = new CompuChiqui.JTextFieldPopup();
        jLabel18 = new javax.swing.JLabel();
        cji2 = new CompuChiqui.JTextFieldPopup();
        jLabel19 = new javax.swing.JLabel();
        comboDerivacion = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        cjtemperatura = new CompuChiqui.JTextFieldPopup();
        jLabel30 = new javax.swing.JLabel();
        conmutador = new javax.swing.JComboBox<>();
        panelLiquidoAislante = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        comboAceite = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        comboReferenciaAceite = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        cjRuptura = new CompuChiqui.JTextFieldPopup();
        jLabel23 = new javax.swing.JLabel();
        cjmetodo = new CompuChiqui.JTextFieldPopup();
        panelResistenciaAislamiento = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        cjtiemporalt = new CompuChiqui.JTextFieldPopup();
        jLabel25 = new javax.swing.JLabel();
        comboTensionPrueba = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        cjATcontraBT = new CompuChiqui.JTextFieldPopup();
        jLabel27 = new javax.swing.JLabel();
        cjATcontraTierra = new CompuChiqui.JTextFieldPopup();
        jLabel28 = new javax.swing.JLabel();
        cjBTcontraTierra = new CompuChiqui.JTextFieldPopup();
        panelRelacionTransformacion = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        comboGrupoConexion = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        comboPolaridad = new javax.swing.JComboBox<>();
        panelResistenciaTerminales = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        cjuv = new CompuChiqui.JTextFieldPopup();
        jLabel36 = new javax.swing.JLabel();
        cjxy = new CompuChiqui.JTextFieldPopup();
        jLabel37 = new javax.swing.JLabel();
        cjwu = new CompuChiqui.JTextFieldPopup();
        jLabel38 = new javax.swing.JLabel();
        cjyz = new CompuChiqui.JTextFieldPopup();
        jLabel39 = new javax.swing.JLabel();
        cjvw = new CompuChiqui.JTextFieldPopup();
        jLabel40 = new javax.swing.JLabel();
        cjzx = new CompuChiqui.JTextFieldPopup();
        jLabel41 = new javax.swing.JLabel();
        cjproresalta = new CompuChiqui.JTextFieldPopup();
        jLabel42 = new javax.swing.JLabel();
        cjproresbaja = new CompuChiqui.JTextFieldPopup();
        jLabel43 = new javax.swing.JLabel();
        comboMaterialAlta = new javax.swing.JComboBox<>();
        jLabel44 = new javax.swing.JLabel();
        comboMaterialBaja = new javax.swing.JComboBox<>();
        panelEnsayoAislamiento = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        cjBTcontraATyTierra = new CompuChiqui.JTextFieldPopup();
        jLabel46 = new javax.swing.JLabel();
        cjATcontraBTyTierra = new CompuChiqui.JTextFieldPopup();
        jLabel47 = new javax.swing.JLabel();
        cjtiempoaplicado = new CompuChiqui.JTextFieldPopup();
        jLabel48 = new javax.swing.JLabel();
        cjtensionBT = new CompuChiqui.JTextFieldPopup();
        jLabel49 = new javax.swing.JLabel();
        cjFrecuenciaInducida = new CompuChiqui.JTextFieldPopup();
        jLabel50 = new javax.swing.JLabel();
        cjtiempoInducido = new CompuChiqui.JTextFieldPopup();
        panelEnsayosinCarga = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        cjTensionBT2 = new CompuChiqui.JTextFieldPopup();
        jLabel52 = new javax.swing.JLabel();
        cjiu = new CompuChiqui.JTextFieldPopup();
        jLabel53 = new javax.swing.JLabel();
        cjiv = new CompuChiqui.JTextFieldPopup();
        jLabel54 = new javax.swing.JLabel();
        cjiw = new CompuChiqui.JTextFieldPopup();
        jLabel55 = new javax.swing.JLabel();
        cjpromedioi = new CompuChiqui.JTextFieldPopup();
        jLabel56 = new javax.swing.JLabel();
        cjiogarantizado = new CompuChiqui.JTextFieldPopup();
        jLabel57 = new javax.swing.JLabel();
        cjpomedido = new CompuChiqui.JTextFieldPopup();
        jLabel58 = new javax.swing.JLabel();
        cjpogarantizado = new CompuChiqui.JTextFieldPopup();
        jPanel9 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        cjvcc = new CompuChiqui.JTextFieldPopup();
        jLabel60 = new javax.swing.JLabel();
        cjpcumedido = new CompuChiqui.JTextFieldPopup();
        jLabel61 = new javax.swing.JLabel();
        cjpcua85 = new CompuChiqui.JTextFieldPopup();
        jLabel62 = new javax.swing.JLabel();
        cjpcugarantizado = new CompuChiqui.JTextFieldPopup();
        jLabel63 = new javax.swing.JLabel();
        cji2r = new CompuChiqui.JTextFieldPopup();
        jLabel64 = new javax.swing.JLabel();
        cji2ra85 = new CompuChiqui.JTextFieldPopup();
        jLabel65 = new javax.swing.JLabel();
        cjimpedancia = new CompuChiqui.JTextFieldPopup();
        jLabel66 = new javax.swing.JLabel();
        cjimpedancia85 = new CompuChiqui.JTextFieldPopup();
        jLabel67 = new javax.swing.JLabel();
        cjimpedanciagarantizado = new CompuChiqui.JTextFieldPopup();
        jPanel10 = new javax.swing.JPanel();
        jLabel78 = new javax.swing.JLabel();
        cjreg = new CompuChiqui.JTextFieldPopup();
        jLabel79 = new javax.swing.JLabel();
        cjef = new CompuChiqui.JTextFieldPopup();
        cjef2 = new CompuChiqui.JTextFieldPopup();
        jPanel12 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        cjmasa = new CompuChiqui.JTextFieldPopup();
        jLabel69 = new javax.swing.JLabel();
        cjaceite = new CompuChiqui.JTextFieldPopup();
        jLabel70 = new javax.swing.JLabel();
        cjlargo = new CompuChiqui.JTextFieldPopup();
        jLabel71 = new javax.swing.JLabel();
        cjancho = new CompuChiqui.JTextFieldPopup();
        jLabel72 = new javax.swing.JLabel();
        cjalto = new CompuChiqui.JTextFieldPopup();
        jLabel73 = new javax.swing.JLabel();
        cjcolor = new CompuChiqui.JTextFieldPopup();
        jLabel74 = new javax.swing.JLabel();
        cjespesor = new CompuChiqui.JTextFieldPopup();
        jLabel75 = new javax.swing.JLabel();
        cjelementos = new CompuChiqui.JTextFieldPopup();
        jLabel76 = new javax.swing.JLabel();
        cjlargoelemento = new CompuChiqui.JTextFieldPopup();
        jLabel77 = new javax.swing.JLabel();
        cjaltoelemento = new CompuChiqui.JTextFieldPopup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaUno = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDos = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        cjobservaciones = new CompuChiqui.JCTextArea();
        cjcliente = new CompuChiqui.JTextFieldPopup();
        cjlote = new CompuChiqui.JTextFieldPopup();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jToolBar2 = new javax.swing.JToolBar();
        checkGarantia = new javax.swing.JCheckBox();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnGuardar = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        cjfechasalida = new com.toedter.calendar.JDateChooser();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaProtocolos = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel15 = new javax.swing.JPanel();
        jLabel81 = new javax.swing.JLabel();
        cjbuscarPorSerie = new CompuChiqui.JTextFieldPopup();
        jLabel82 = new javax.swing.JLabel();
        comboCliente = new javax.swing.JComboBox<>();
        jLabel85 = new javax.swing.JLabel();
        comboContrato = new javax.swing.JComboBox<>();
        jLabel83 = new javax.swing.JLabel();
        cjBuscarPorLote = new CompuChiqui.JTextFieldPopup();
        jLabel86 = new javax.swing.JLabel();
        cjdespacho = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        cjBuscarPorMarca = new javax.swing.JTextField();
        btnGenerarExcel = new javax.swing.JButton();
        btnExportarPdfs = new javax.swing.JButton();
        btnImprimirHojasDeLaboratorio = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        subMenuItemRecalcular = new javax.swing.JMenuItem();
        mostrarProtocolo = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        subMenuAbrirProtocolo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/ver.png"))); // NOI18N
        subMenuAbrirProtocolo.setText("Abrir");
        subMenuAbrirProtocolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuAbrirProtocoloActionPerformed(evt);
            }
        });
        menuProtocolos.add(subMenuAbrirProtocolo);

        subMenuEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Borrar.png"))); // NOI18N
        subMenuEliminar.setText("Eliminar");
        subMenuEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuEliminarActionPerformed(evt);
            }
        });
        menuProtocolos.add(subMenuEliminar);

        cjef1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjef1.setPreferredSize(new java.awt.Dimension(100, 20));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N

        panelInformacionGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informacion General", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelInformacionGeneral.setLayout(new java.awt.GridLayout(22, 2, 0, 2));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Protocolo N°:");
        panelInformacionGeneral.add(jLabel3);

        cjprotocolo.setEditable(false);
        cjprotocolo.setBackground(new java.awt.Color(255, 255, 255));
        cjprotocolo.setForeground(new java.awt.Color(0, 102, 255));
        cjprotocolo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjprotocolo.setCampodetexto(cjATcontraBT);
        cjprotocolo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        cjprotocolo.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjprotocolo);

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel80.setText("Serie N°:");
        panelInformacionGeneral.add(jLabel80);

        cjserie.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjserie.setCampodetexto(cjATcontraBT);
        cjserie.setPreferredSize(new java.awt.Dimension(100, 20));
        cjserie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjserieKeyPressed(evt);
            }
        });
        panelInformacionGeneral.add(cjserie);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("N° Empresa:");
        panelInformacionGeneral.add(jLabel4);

        cjempresa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjempresa.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjempresa);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Marca:");
        panelInformacionGeneral.add(jLabel5);

        cjmarca.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjmarca.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjmarca);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("KVA:");
        panelInformacionGeneral.add(jLabel6);

        cjkva.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjkva.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjkva);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Fases:");
        panelInformacionGeneral.add(jLabel8);

        comboFase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "3" }));
        comboFase.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboFaseItemStateChanged(evt);
            }
        });
        comboFase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFaseActionPerformed(evt);
            }
        });
        panelInformacionGeneral.add(comboFase);
        comboFase.getAccessibleContext().setAccessibleDescription("");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Año:");
        panelInformacionGeneral.add(jLabel7);

        cjano.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjano.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjano);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Volt. Primario(V):");
        panelInformacionGeneral.add(jLabel9);

        cjvp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvp.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjvp);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Volt. Secund.(V):");
        panelInformacionGeneral.add(jLabel10);

        cjvs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvs.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjvs);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Servicio:");
        panelInformacionGeneral.add(jLabel1);

        comboServicio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "NUEVO", "RECONSTRUIDO", "REPARACION", "MANTENIMIENTO" }));
        comboServicio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboServicioItemStateChanged(evt);
            }
        });
        panelInformacionGeneral.add(comboServicio);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Frecuencia(Hz):");
        panelInformacionGeneral.add(jLabel2);

        comboFrecuencia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "60", "50" }));
        panelInformacionGeneral.add(comboFrecuencia);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Refrigeracion:");
        panelInformacionGeneral.add(jLabel11);

        comboRefrigeracion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ONAN", "ONAF", "AN" }));
        panelInformacionGeneral.add(comboRefrigeracion);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Tension Serie:");
        panelInformacionGeneral.add(jLabel12);

        cjtensionSerie.setEditable(false);
        cjtensionSerie.setBackground(new java.awt.Color(255, 255, 255));
        cjtensionSerie.setForeground(new java.awt.Color(0, 102, 255));
        cjtensionSerie.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelInformacionGeneral.add(cjtensionSerie);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("NBA AT/BT:");
        panelInformacionGeneral.add(jLabel13);

        cjnba.setEditable(false);
        cjnba.setBackground(new java.awt.Color(255, 255, 255));
        cjnba.setForeground(new java.awt.Color(0, 102, 255));
        cjnba.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelInformacionGeneral.add(cjnba);

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Calent. Devn:");
        panelInformacionGeneral.add(jLabel14);

        cjcalentamientodevanado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjcalentamientodevanado.setText("65");
        panelInformacionGeneral.add(cjcalentamientodevanado);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Clase Aislam.:");
        panelInformacionGeneral.add(jLabel15);

        comboClaseAislamiento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ao", "A:(75°C)", "E:(85°C)", "B:(100°C)", "F:(120°C)", "H:(145°C)" }));
        comboClaseAislamiento.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClaseAislamientoItemStateChanged(evt);
            }
        });
        comboClaseAislamiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboClaseAislamientoActionPerformed(evt);
            }
        });
        panelInformacionGeneral.add(comboClaseAislamiento);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Alt Diseño:");
        panelInformacionGeneral.add(jLabel16);

        cjaltdiseno.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjaltdiseno.setText("1000 MSNM");
        panelInformacionGeneral.add(cjaltdiseno);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("I1:");
        panelInformacionGeneral.add(jLabel17);

        cji1.setEditable(false);
        cji1.setBackground(new java.awt.Color(255, 255, 255));
        cji1.setForeground(new java.awt.Color(0, 102, 255));
        cji1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelInformacionGeneral.add(cji1);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("I2:");
        panelInformacionGeneral.add(jLabel18);

        cji2.setEditable(false);
        cji2.setBackground(new java.awt.Color(255, 255, 255));
        cji2.setForeground(new java.awt.Color(0, 102, 255));
        cji2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        panelInformacionGeneral.add(cji2);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Deriv. Prim:");
        panelInformacionGeneral.add(jLabel19);

        comboDerivacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "(-4)*2.5%", "(+1-3)*2.5%", "(+2-2)*2.5%", "(+3-1)*2.5%", "(+4)*2.5%" }));
        comboDerivacion.setSelectedIndex(1);
        panelInformacionGeneral.add(comboDerivacion);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Temperatura:");
        panelInformacionGeneral.add(jLabel29);

        cjtemperatura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtemperatura.setText("30");
        cjtemperatura.setPreferredSize(new java.awt.Dimension(100, 20));
        panelInformacionGeneral.add(cjtemperatura);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Conmutador:");
        panelInformacionGeneral.add(jLabel30);

        conmutador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5" }));
        conmutador.setSelectedIndex(1);
        conmutador.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                conmutadorItemStateChanged(evt);
            }
        });
        panelInformacionGeneral.add(conmutador);

        panelLiquidoAislante.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "1) Liquido Aislante", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelLiquidoAislante.setToolTipText("Liquido Aislante");
        panelLiquidoAislante.setLayout(new java.awt.GridLayout(4, 2, 0, 2));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Aceite:");
        panelLiquidoAislante.add(jLabel20);

        comboAceite.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MINERAL", "VEGETAL", "REGENERADO", "SECO" }));
        panelLiquidoAislante.add(comboAceite);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Referencia:");
        panelLiquidoAislante.add(jLabel21);

        comboReferenciaAceite.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HYVOLT", "LUB TROIL TIPO II", "FR3", "EPM", "INHIBIDO TIPO II", "NYTRO IZAR II", "ADV-1601", "N/A" }));
        panelLiquidoAislante.add(comboReferenciaAceite);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Ruptura(kv):");
        panelLiquidoAislante.add(jLabel22);

        cjRuptura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjRuptura.setText("40");
        cjRuptura.setPreferredSize(new java.awt.Dimension(100, 20));
        panelLiquidoAislante.add(cjRuptura);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Metodo:");
        panelLiquidoAislante.add(jLabel23);

        cjmetodo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjmetodo.setText("ASTM 877");
        cjmetodo.setPreferredSize(new java.awt.Dimension(100, 20));
        panelLiquidoAislante.add(cjmetodo);

        panelResistenciaAislamiento.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2) Resist. Aislamiento:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelResistenciaAislamiento.setToolTipText("Resistencia de Aislamiento");
        panelResistenciaAislamiento.setLayout(new java.awt.GridLayout(5, 2, 0, 2));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Tiempo:");
        panelResistenciaAislamiento.add(jLabel24);

        cjtiemporalt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtiemporalt.setText("60");
        cjtiemporalt.setPreferredSize(new java.awt.Dimension(100, 20));
        panelResistenciaAislamiento.add(cjtiemporalt);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Tens. Prueba:");
        panelResistenciaAislamiento.add(jLabel25);

        comboTensionPrueba.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5000/500", "5000/5000", "500/500" }));
        panelResistenciaAislamiento.add(comboTensionPrueba);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("AT contra BT(GΩ):");
        panelResistenciaAislamiento.add(jLabel26);

        cjATcontraBT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjATcontraBT.setAbajo(cjATcontraBTyTierra);
        cjATcontraBT.setArriba(comboTensionPrueba);
        cjATcontraBT.setCampodetexto(cjATcontraTierra);
        cjATcontraBT.setPreferredSize(new java.awt.Dimension(100, 20));
        cjATcontraBT.setValidar(true);
        panelResistenciaAislamiento.add(cjATcontraBT);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("AT contra Tierra(GΩ):");
        panelResistenciaAislamiento.add(jLabel27);

        cjATcontraTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjATcontraTierra.setAbajo(null);
        cjATcontraTierra.setArriba(cjATcontraBT);
        cjATcontraTierra.setCampodetexto(cjBTcontraTierra);
        cjATcontraTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        cjATcontraTierra.setValidar(true);
        panelResistenciaAislamiento.add(cjATcontraTierra);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel28.setText("BT contra Tierra(GΩ):");
        panelResistenciaAislamiento.add(jLabel28);

        cjBTcontraTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjBTcontraTierra.setCampodetexto(null);
        cjBTcontraTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        cjBTcontraTierra.setValidar(true);
        cjBTcontraTierra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjBTcontraTierraKeyPressed(evt);
            }
        });
        panelResistenciaAislamiento.add(cjBTcontraTierra);

        panelRelacionTransformacion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "3) Relacion de Transformacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelRelacionTransformacion.setToolTipText("Relacion de Transformacion");
        panelRelacionTransformacion.setLayout(new java.awt.GridLayout(2, 2, 0, 2));

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Grupo de Conexion:");
        panelRelacionTransformacion.add(jLabel31);

        comboGrupoConexion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ii6", "Ii0", "DYn5", "DYn11", "Yy0", "Yy6" }));
        panelRelacionTransformacion.add(comboGrupoConexion);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Polaridad:");
        panelRelacionTransformacion.add(jLabel32);

        comboPolaridad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SUSTRACTIVA", "ADITIVA", "NO APLICA" }));
        panelRelacionTransformacion.add(comboPolaridad);

        panelResistenciaTerminales.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "4) Resistencia Entre Terminales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelResistenciaTerminales.setToolTipText("Resistencia Entre Terminales");
        panelResistenciaTerminales.setLayout(new java.awt.GridLayout(5, 4, 0, 2));

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("U-V(Ω):");
        panelResistenciaTerminales.add(jLabel35);

        cjuv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjuv.setAbajo(cjwu);
        cjuv.setCampodetexto(null);
        cjuv.setPreferredSize(new java.awt.Dimension(100, 20));
        cjuv.setValidar(true);
        cjuv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjuvKeyPressed(evt);
            }
        });
        panelResistenciaTerminales.add(cjuv);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("X-Y(mΩ):");
        panelResistenciaTerminales.add(jLabel36);

        cjxy.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjxy.setAbajo(cjyz);
        cjxy.setCampodetexto(null);
        cjxy.setPreferredSize(new java.awt.Dimension(100, 20));
        cjxy.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjxyKeyReleased(evt);
            }
        });
        panelResistenciaTerminales.add(cjxy);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("V-W(Ω):");
        panelResistenciaTerminales.add(jLabel37);

        cjwu.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjwu.setAbajo(cjvw);
        cjwu.setArriba(cjuv);
        cjwu.setCampodetexto(cjvw);
        cjwu.setPreferredSize(new java.awt.Dimension(100, 20));
        cjwu.setValidar(true);
        panelResistenciaTerminales.add(cjwu);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Y-Z(mΩ):");
        panelResistenciaTerminales.add(jLabel38);

        cjyz.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjyz.setAbajo(cjvcc);
        cjyz.setArriba(cjxy);
        cjyz.setCampodetexto(cjzx);
        cjyz.setPreferredSize(new java.awt.Dimension(100, 20));
        cjyz.setValidar(true);
        panelResistenciaTerminales.add(cjyz);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("W-U(Ω):");
        panelResistenciaTerminales.add(jLabel39);

        cjvw.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvw.setArriba(cjwu);
        cjvw.setCampodetexto(cjxy);
        cjvw.setPreferredSize(new java.awt.Dimension(100, 20));
        cjvw.setValidar(true);
        panelResistenciaTerminales.add(cjvw);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel40.setText("Z-X(mΩ):");
        panelResistenciaTerminales.add(jLabel40);

        cjzx.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjzx.setArriba(cjyz);
        cjzx.setCampodetexto(cjiu);
        cjzx.setPreferredSize(new java.awt.Dimension(100, 20));
        cjzx.setValidar(true);
        panelResistenciaTerminales.add(cjzx);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel41.setText("Promedio:");
        panelResistenciaTerminales.add(jLabel41);

        cjproresalta.setForeground(new java.awt.Color(0, 102, 255));
        cjproresalta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjproresalta.setPreferredSize(new java.awt.Dimension(100, 20));
        panelResistenciaTerminales.add(cjproresalta);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel42.setText("Promedio:");
        panelResistenciaTerminales.add(jLabel42);

        cjproresbaja.setForeground(new java.awt.Color(0, 102, 255));
        cjproresbaja.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjproresbaja.setPreferredSize(new java.awt.Dimension(100, 20));
        panelResistenciaTerminales.add(cjproresbaja);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Material:");
        panelResistenciaTerminales.add(jLabel43);

        comboMaterialAlta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "COBRE", "ALUMINIO" }));
        panelResistenciaTerminales.add(comboMaterialAlta);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Material:");
        panelResistenciaTerminales.add(jLabel44);

        comboMaterialBaja.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "COBRE", "ALUMINIO" }));
        panelResistenciaTerminales.add(comboMaterialBaja);

        panelEnsayoAislamiento.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "5) Ensayo de Aislamiento", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelEnsayoAislamiento.setToolTipText("Resistencia Entre Terminales");
        panelEnsayoAislamiento.setLayout(new java.awt.GridLayout(6, 2, 0, 2));

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("BT contra AT y Tierra:");
        panelEnsayoAislamiento.add(jLabel45);

        cjBTcontraATyTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjBTcontraATyTierra.setText("10");
        cjBTcontraATyTierra.setCampodetexto(cjATcontraBTyTierra);
        cjBTcontraATyTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayoAislamiento.add(cjBTcontraATyTierra);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("AT contra BT y TierraKv:");
        panelEnsayoAislamiento.add(jLabel46);

        cjATcontraBTyTierra.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjATcontraBTyTierra.setText("34.5");
        cjATcontraBTyTierra.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayoAislamiento.add(cjATcontraBTyTierra);

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Tiempo de Prueba(s):");
        panelEnsayoAislamiento.add(jLabel47);

        cjtiempoaplicado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtiempoaplicado.setText("60");
        cjtiempoaplicado.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayoAislamiento.add(cjtiempoaplicado);

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel48.setText("Tension BT:");
        panelEnsayoAislamiento.add(jLabel48);

        cjtensionBT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtensionBT.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayoAislamiento.add(cjtensionBT);

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("Frecuencia (Hz):");
        panelEnsayoAislamiento.add(jLabel49);

        cjFrecuenciaInducida.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjFrecuenciaInducida.setText("414");
        cjFrecuenciaInducida.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayoAislamiento.add(cjFrecuenciaInducida);

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel50.setText("Timpo de Prueba:");
        panelEnsayoAislamiento.add(jLabel50);

        cjtiempoInducido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjtiempoInducido.setText("17");
        cjtiempoInducido.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayoAislamiento.add(cjtiempoInducido);

        panelEnsayosinCarga.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "6) Ensayo Sin Carga", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        panelEnsayosinCarga.setToolTipText("Resistencia Entre Terminales");
        panelEnsayosinCarga.setLayout(new java.awt.GridLayout(8, 2, 0, 2));

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Tension BT:");
        panelEnsayosinCarga.add(jLabel51);

        cjTensionBT2.setForeground(new java.awt.Color(0, 102, 255));
        cjTensionBT2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjTensionBT2.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayosinCarga.add(cjTensionBT2);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("Iu(Amp):");
        panelEnsayosinCarga.add(jLabel52);

        cjiu.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiu.setAbajo(cjiv);
        cjiu.setCampodetexto(null);
        cjiu.setPreferredSize(new java.awt.Dimension(100, 20));
        cjiu.setValidar(true);
        cjiu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cjiuKeyPressed(evt);
            }
        });
        panelEnsayosinCarga.add(cjiu);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel53.setText("Iv(Amp):");
        panelEnsayosinCarga.add(jLabel53);

        cjiv.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiv.setAbajo(cji2ra85);
        cjiv.setArriba(cjiu);
        cjiv.setCampodetexto(cjiw);
        cjiv.setPreferredSize(new java.awt.Dimension(100, 20));
        cjiv.setValidar(true);
        panelEnsayosinCarga.add(cjiv);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel54.setText("Iw(Amp):");
        panelEnsayosinCarga.add(jLabel54);

        cjiw.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiw.setAbajo(cjpomedido);
        cjiw.setArriba(cjiv);
        cjiw.setCampodetexto(cjpomedido);
        cjiw.setPreferredSize(new java.awt.Dimension(100, 20));
        cjiw.setValidar(true);
        panelEnsayosinCarga.add(cjiw);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Promedio I(%):");
        panelEnsayosinCarga.add(jLabel55);

        cjpromedioi.setForeground(new java.awt.Color(0, 102, 255));
        cjpromedioi.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpromedioi.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayosinCarga.add(cjpromedioi);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel56.setText("Io Garantizado:");
        panelEnsayosinCarga.add(jLabel56);

        cjiogarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjiogarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjiogarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        panelEnsayosinCarga.add(cjiogarantizado);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel57.setText("Po Medido:");
        panelEnsayosinCarga.add(jLabel57);

        cjpomedido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpomedido.setCampodetexto(null);
        cjpomedido.setPreferredSize(new java.awt.Dimension(100, 20));
        cjpomedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjpomedidoKeyTyped(evt);
            }
        });
        panelEnsayosinCarga.add(cjpomedido);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel58.setText("Po Garantizado:");
        panelEnsayosinCarga.add(jLabel58);

        cjpogarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjpogarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpogarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        cjpogarantizado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cjpogarantizadoActionPerformed(evt);
            }
        });
        panelEnsayosinCarga.add(cjpogarantizado);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "7) Ensayo De Corto Circuito", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel9.setToolTipText("Resistencia Entre Terminales");
        jPanel9.setLayout(new java.awt.GridLayout(9, 2, 0, 2));

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel59.setText("Vcc(V):");
        jPanel9.add(jLabel59);

        cjvcc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjvcc.setArriba(cjpomedido);
        cjvcc.setCampodetexto(cjpcumedido);
        cjvcc.setPreferredSize(new java.awt.Dimension(100, 20));
        cjvcc.setValidar(true);
        jPanel9.add(cjvcc);

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel60.setText("Pcc Medido(W):");
        jPanel9.add(jLabel60);

        cjpcumedido.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpcumedido.setCampodetexto(null);
        cjpcumedido.setPreferredSize(new java.awt.Dimension(100, 20));
        cjpcumedido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cjpcumedidoKeyTyped(evt);
            }
        });
        jPanel9.add(cjpcumedido);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel61.setText("Pcc a 85°(W):");
        jPanel9.add(jLabel61);

        cjpcua85.setForeground(new java.awt.Color(0, 102, 255));
        cjpcua85.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpcua85.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjpcua85);

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel62.setText("Pcc Garantizado:");
        jPanel9.add(jLabel62);

        cjpcugarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjpcugarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjpcugarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjpcugarantizado);

        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel63.setText("I2r(W):");
        jPanel9.add(jLabel63);

        cji2r.setForeground(new java.awt.Color(0, 102, 255));
        cji2r.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cji2r.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cji2r);

        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel64.setText("I2r a 85°(W):");
        jPanel9.add(jLabel64);

        cji2ra85.setForeground(new java.awt.Color(0, 102, 255));
        cji2ra85.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cji2ra85.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cji2ra85);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel65.setText("Impedancia Z (%):");
        jPanel9.add(jLabel65);

        cjimpedancia.setForeground(new java.awt.Color(0, 102, 255));
        cjimpedancia.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjimpedancia.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjimpedancia);

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel66.setText("Impedancia 85° Z (%):");
        jPanel9.add(jLabel66);

        cjimpedancia85.setForeground(new java.awt.Color(0, 102, 255));
        cjimpedancia85.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjimpedancia85.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjimpedancia85);

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel67.setText("Impendancia Garantizado:");
        jPanel9.add(jLabel67);

        cjimpedanciagarantizado.setForeground(new java.awt.Color(0, 102, 255));
        cjimpedanciagarantizado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjimpedanciagarantizado.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel9.add(cjimpedanciagarantizado);

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel10.setToolTipText("Resistencia Entre Terminales");
        jPanel10.setLayout(new java.awt.GridLayout(1, 4, 0, 2));

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel78.setText("8) Regulacion:");
        jPanel10.add(jLabel78);

        cjreg.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjreg.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel10.add(cjreg);

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel79.setText("9) Eficiencia:");
        jPanel10.add(jLabel79);

        cjef.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjef.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel10.add(cjef);

        cjef2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjef2.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel10.add(cjef2);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "10) Caracteristicas Mecanicas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel12.setToolTipText("Resistencia Entre Terminales");
        jPanel12.setLayout(new java.awt.GridLayout(10, 2, 0, 2));

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel68.setText("Masa(Kg):");
        jPanel12.add(jLabel68);

        cjmasa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjmasa.setCampodetexto(cjaceite);
        cjmasa.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjmasa);

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel69.setText("Aceite(L):");
        jPanel12.add(jLabel69);

        cjaceite.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjaceite.setCampodetexto(cjlargo);
        cjaceite.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjaceite);

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel70.setText("Largo(mm):");
        jPanel12.add(jLabel70);

        cjlargo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjlargo.setCampodetexto(cjancho);
        cjlargo.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjlargo);

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel71.setText("Ancho(mm):");
        jPanel12.add(jLabel71);

        cjancho.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjancho.setCampodetexto(cjalto);
        cjancho.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjancho);

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel72.setText("Alto(mm):");
        jPanel12.add(jLabel72);

        cjalto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjalto.setCampodetexto(cjcolor);
        cjalto.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjalto);

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel73.setText("Color:");
        jPanel12.add(jLabel73);

        cjcolor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjcolor.setCampodetexto(cjespesor);
        cjcolor.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjcolor);

        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel74.setText("Espesor(µ):");
        jPanel12.add(jLabel74);

        cjespesor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjespesor.setText("110");
        cjespesor.setCampodetexto(cjelementos);
        cjespesor.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjespesor);

        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel75.setText("N° Elementos:");
        jPanel12.add(jLabel75);

        cjelementos.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjelementos.setCampodetexto(cjlargoelemento);
        cjelementos.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjelementos);

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel76.setText("Largo Elemento(mm):");
        jPanel12.add(jLabel76);

        cjlargoelemento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjlargoelemento.setCampodetexto(cjaltoelemento);
        cjlargoelemento.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjlargoelemento);

        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel77.setText("Ancho Elemento(mm):");
        jPanel12.add(jLabel77);

        cjaltoelemento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjaltoelemento.setPreferredSize(new java.awt.Dimension(100, 20));
        jPanel12.add(cjaltoelemento);

        tablaUno.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "POSICION", "TENSION", "FASE U", "FASE V", "FASE W"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaUno.setGridColor(new java.awt.Color(227, 227, 227));
        tablaUno.setRowHeight(20);
        tablaUno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tablaUnoKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(tablaUno);

        tablaDos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NOMINAL", "MINIMA", "MAXIMA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaDos.setGridColor(new java.awt.Color(227, 227, 227));
        tablaDos.setRowHeight(20);
        jScrollPane2.setViewportView(tablaDos);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Observaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel11.setToolTipText("Resistencia Entre Terminales");

        cjobservaciones.setColumns(20);
        cjobservaciones.setRows(5);
        cjobservaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjobservacionesKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(cjobservaciones);

        cjcliente.setEditable(false);
        cjcliente.setBackground(new java.awt.Color(255, 255, 255));
        cjcliente.setForeground(new java.awt.Color(0, 102, 255));
        cjcliente.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjcliente.setPreferredSize(new java.awt.Dimension(100, 20));

        cjlote.setEditable(false);
        cjlote.setBackground(new java.awt.Color(255, 255, 255));
        cjlote.setForeground(new java.awt.Color(0, 102, 255));
        cjlote.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cjlote.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel33.setText("Cliente:");

        jLabel34.setText("Lote:");

        jToolBar2.setFloatable(false);

        checkGarantia.setText("Garantia");
        checkGarantia.setFocusable(false);
        checkGarantia.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        checkGarantia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(checkGarantia);
        jToolBar2.add(jSeparator6);

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/Guardar.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar2.add(btnGuardar);
        jToolBar2.add(jSeparator5);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/nuevodocumento.png"))); // NOI18N
        jButton2.setToolTipText("Nuevo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cjcliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(30, 30, 30)
                        .addComponent(cjlote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cjcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cjlote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fecha Salida Laboratorio", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Enter Sansman", 0, 10))); // NOI18N
        jPanel13.setToolTipText("Resistencia Entre Terminales");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cjfechasalida, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cjfechasalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelInformacionGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(panelLiquidoAislante, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(panelResistenciaAislamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(panelResistenciaTerminales, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(panelEnsayoAislamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelEnsayosinCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelRelacionTransformacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelInformacionGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(panelRelacionTransformacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(panelEnsayosinCarga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(panelLiquidoAislante, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(panelResistenciaAislamiento, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(6, 6, 6)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panelEnsayoAislamiento, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                        .addComponent(panelResistenciaTerminales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Datos", jPanel1);

        tablaProtocolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PROTOCOLO N°", "CLIENTE", "SERIE", "N° EMPRESA", "MARCA", "POTENCIA", "FASE", "TENSION", "LOTE", "REMISION", "DESPACHO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaProtocolos.setGridColor(new java.awt.Color(227, 227, 227));
        tablaProtocolos.setRowHeight(25);
        tablaProtocolos.getTableHeader().setReorderingAllowed(false);
        tablaProtocolos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProtocolosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tablaProtocolos);

        jProgressBar1.setStringPainted(true);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscar"));
        jPanel15.setLayout(new java.awt.GridBagLayout());

        jLabel81.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel81.setText("Serie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel15.add(jLabel81, gridBagConstraints);

        cjbuscarPorSerie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjbuscarPorSerieKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(cjbuscarPorSerie, gridBagConstraints);

        jLabel82.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel82.setText("Cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel15.add(jLabel82, gridBagConstraints);

        comboCliente.setMaximumRowCount(20);
        comboCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboClienteItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(comboCliente, gridBagConstraints);

        jLabel85.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel85.setText("Contrato:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel15.add(jLabel85, gridBagConstraints);

        comboContrato.setMaximumRowCount(20);
        comboContrato.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboContratoItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(comboContrato, gridBagConstraints);

        jLabel83.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel83.setText("Lote:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel15.add(jLabel83, gridBagConstraints);

        cjBuscarPorLote.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarPorLoteKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel15.add(cjBuscarPorLote, gridBagConstraints);

        jLabel86.setText("Despacho:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel15.add(jLabel86, gridBagConstraints);

        cjdespacho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjdespachoKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel15.add(cjdespacho, gridBagConstraints);

        jLabel84.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel84.setText("Marca:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel15.add(jLabel84, gridBagConstraints);

        cjBuscarPorMarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cjBuscarPorMarcaKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel15.add(cjBuscarPorMarca, gridBagConstraints);

        btnGenerarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        btnGenerarExcel.setFocusable(false);
        btnGenerarExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGenerarExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGenerarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarExcelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(btnGenerarExcel, gridBagConstraints);

        btnExportarPdfs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/pdf.png"))); // NOI18N
        btnExportarPdfs.setFocusable(false);
        btnExportarPdfs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportarPdfs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportarPdfs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarPdfsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(btnExportarPdfs, gridBagConstraints);

        btnImprimirHojasDeLaboratorio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/imprimir.png"))); // NOI18N
        btnImprimirHojasDeLaboratorio.setFocusable(false);
        btnImprimirHojasDeLaboratorio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimirHojasDeLaboratorio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimirHojasDeLaboratorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirHojasDeLaboratorioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(btnImprimirHojasDeLaboratorio, gridBagConstraints);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1259, Short.MAX_VALUE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Protocolos", jPanel14);

        jMenu1.setText("Archivo");
        jMenu1.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");
        jMenu2.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N

        subMenuItemRecalcular.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
        subMenuItemRecalcular.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        subMenuItemRecalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/calculadora.png"))); // NOI18N
        subMenuItemRecalcular.setText("Recalcular");
        subMenuItemRecalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subMenuItemRecalcularActionPerformed(evt);
            }
        });
        jMenu2.add(subMenuItemRecalcular);

        mostrarProtocolo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mostrarProtocolo.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        mostrarProtocolo.setSelected(true);
        mostrarProtocolo.setText("Mostrar protocolo al imprimir");
        mostrarProtocolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarProtocoloActionPerformed(evt);
            }
        });
        jMenu2.add(mostrarProtocolo);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Exportar");
        jMenu3.setFont(new java.awt.Font("Ebrima", 1, 12)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Ebrima", 0, 12)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/images/excel.png"))); // NOI18N
        jMenuItem1.setText("Detalles de transformador");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cjserieKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjserieKeyPressed
        /* EVENTO QUE PERMITE LA BUSQUEDA POR NUMERO DE SERIE */
        if (evt.getKeyCode() == 10) {
            try {
                boolean mostrar = false;
                ResultSet rs;
                conex.conectar();
                rs = conex.CONSULTAR("SELECT numeroserie, count(*) FROM transformador t WHERE t.numeroserie='" + cjserie.getText() + "' GROUP BY numeroserie ");
                int total;
                if (rs.next()) {
                    total = rs.getInt("count");
                } else {
                    modelo.Metodos.M("NO SE ENCONTRÒ EL NUMERO DE SERIE DIGITADO", "advertencia.png");
                    return;
                }
                rs = conex.CONSULTAR("SELECT * FROM entrada e INNER JOIN transformador t USING(identrada) INNER JOIN cliente c USING (idcliente) WHERE t.numeroserie='" + cjserie.getText().trim() + "'");
                if (total == 1) {
                    mostrar = rs.next();
                } else if (total > 1) {
                    DialogoTrafosRepetidos trafos = new DialogoTrafosRepetidos(this, rootPaneCheckingEnabled);
                    trafos.CargarDatos(rs);
                    trafos.setVisible(true);
                    IDTRAFO = trafos.getIDTRAFO();
                    rs = conex.CONSULTAR("SELECT * FROM entrada e INNER JOIN transformador t USING(identrada) INNER JOIN cliente c USING (idcliente) WHERE t.idtransformador='" + trafos.getIDTRAFO() + "'");
                    mostrar = rs.next();
                }
                if (mostrar) {
                    IDTRAFO = rs.getInt("idtransformador");
                    cjcliente.setText(rs.getString("nombrecliente"));
                    cjlote.setText(rs.getString("lote"));
                    cjempresa.setText(rs.getString("numeroempresa"));
                    cjmarca.setText(rs.getString("marca"));
                    cjkva.setText(rs.getString("kvasalida"));
                    comboFase.setSelectedItem(rs.getString("fase"));
                    cjano.setText(rs.getString("ano"));
                    cjvp.setText(rs.getString("tps"));
                    cjvs.setText(rs.getString("tss"));
                    cjtensionBT.setText(String.valueOf(rs.getInt("tss") * 2));
                    cjTensionBT2.setText(rs.getString("tss"));
                    comboServicio.setSelectedItem(rs.getString("serviciosalida"));
                    cjmasa.setText(rs.getString("peso"));
                    cjaceite.setText(rs.getString("aceite"));
                    CargarTablas();
                    habilitarCampos(rs.getString("fase").equals("3"));
                    cjcliente.setCaretPosition(0);
                    subMenuItemRecalcular.doClick();
                }
            } catch (SQLException ex) {
                modelo.Metodos.M("ERROR AL BUSCAR EL NUMERO DE SERIE\n" + ex, "error.png");
                Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                conex.CERRAR();
            }
        }
    }//GEN-LAST:event_cjserieKeyPressed

    private void subMenuItemRecalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuItemRecalcularActionPerformed
        /* EVENTO QUE INVOCA TODOS LOS METODOS ENCANGADOS DEL CALCULO Y MUESTREO DE LA BASE DE DATOS */
        cargarValores();
        HallarTensionSerie();
        HallarConexionYPolaridad();
        HallarCorrientes();
//        CargarTablas();
        HallarPromedioCorrientes();
        HallarPromedioResistencias();
        I2R();
        I2R85();
        Z85();
        HallarReg();
        HallarEf();
        cargarMedidas();
    }//GEN-LAST:event_subMenuItemRecalcularActionPerformed
    /* ESTE EVENTO SE DISPARA CON CADA SALTO DE LINEA */
    private void tablaUnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tablaUnoKeyTyped
        if (evt.getKeyChar() == 10) {
            if (comboFase.getSelectedIndex() == 0) {
                if (tablaUno.getSelectedRow() == 0 && tablaUno.getSelectedColumn() == 3) {
                    cjuv.grabFocus();
                }
            } else {
                if (tablaUno.getSelectedRow() == 0 && tablaUno.getSelectedColumn() == 0) {
                    cjuv.grabFocus();
                }
            }
        }
    }//GEN-LAST:event_tablaUnoKeyTyped
    /* ESTE EVENTO SE DISPARA CON CADA SALTO DE LINEA Y UV ESTE VACIO */
    private void cjuvKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjuvKeyPressed
        if (evt.getKeyCode() == 10 && !cjuv.getText().isEmpty()) {
            if (comboFase.getSelectedIndex() == 0) {
                cjxy.grabFocus();
            } else {
                cjwu.grabFocus();
            }
        }
    }//GEN-LAST:event_cjuvKeyPressed
    /* ESTE EVENTO SE DISPARA CON CADA SALTO DE LINEA Y CJ ESTE VACIO */
    private void cjxyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjxyKeyReleased
        if (evt.getKeyCode() == 10 && !cjxy.getText().isEmpty()) {
            if (comboFase.getSelectedIndex() == 0) {
                cjiu.grabFocus();
            } else {
                cjyz.grabFocus();
            }
        }
    }//GEN-LAST:event_cjxyKeyReleased
    /* ESTE EVENTO SE DISPARA SI SE COLOCA SIN SELECCION EL JCOMBO DE LA FASE */
    private void comboFaseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboFaseItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            habilitarCampos((comboFase.getSelectedIndex() == 1));
            subMenuItemRecalcular.doClick();
        }
    }//GEN-LAST:event_comboFaseItemStateChanged
    /* ESTE EVENTO SE DISPARA CON CADA SALTO DE LINEA */
    private void cjBTcontraTierraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBTcontraTierraKeyPressed
        if (evt.getKeyCode() == 10) {
            tablaUno.setRowSelectionInterval(0, 0);
            tablaUno.setColumnSelectionInterval(2, 2);
            tablaUno.grabFocus();
        }
    }//GEN-LAST:event_cjBTcontraTierraKeyPressed
    /* ESTE EVENTO SE DISPARA CON CADA SALTO DE LINEA */
    private void cjiuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjiuKeyPressed
        if (evt.getKeyCode() == 10) {
            if (comboFase.getSelectedIndex() == 0) {
                cjpomedido.grabFocus();
            } else {
                cjiv.grabFocus();
            }
        }
    }//GEN-LAST:event_cjiuKeyPressed
    /* ESTE EVENTO SE DISPARA CON CADA CAMBIO DE ESTADO EN EL SERVICIO */
    private void comboServicioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboServicioItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            if (!ACTUALIZANDO && "MANTENIMIENTO".equals(comboServicio.getSelectedItem().toString()) && ESTADO_TRAFO == null) {
                while (true) {
                    int n = JOptionPane.showOptionDialog(this, "SELECCIONE EL ESTADO DEL TRANSFORMADOR", "Seleccione una opcion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, modelo.Metodos.getIcon("advertencia.png"), new Object[]{"ORIGINAL", "REPARADO"}, "ORIGINAL");
                    if (n >= 0) {
                        ESTADO_TRAFO = (n == 0) ? "ORIGINAL" : "REPARADO";
                        break;
                    }
                }
            } else {
                ESTADO_TRAFO = comboServicio.getSelectedItem().toString();
            }
        }
    }//GEN-LAST:event_comboServicioItemStateChanged
    /* EVENTO AL PRESIONAR EL BTN GUARDAR */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarProtocolo();
    }//GEN-LAST:event_btnGuardarActionPerformed
    /* EVENTO AL PRESIONAR EL BTN GENERAR EXCEL */
    private void btnGenerarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarExcelActionPerformed
        modelo.Metodos.JTableToExcel(tablaProtocolos, btnGenerarExcel);
    }//GEN-LAST:event_btnGenerarExcelActionPerformed
    /* "KEYEVENT" EVENTO QUE SE SE DISPARA CON CADA PULSACION DEL TECLADO */
    private void cjbuscarPorSerieKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjbuscarPorSerieKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjbuscarPorSerieKeyReleased
    /* EVENTO QUE SE DISPARA CON EL CLIC DEL TECLADO */
    private void tablaProtocolosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProtocolosMouseClicked
        /* PRIMER EVENTOS SON LOS PASOS QUE SE HACEN AL HACER CLIC SENCILLO CON EL MOUSE */
        if (SwingUtilities.isRightMouseButton(evt)) {
            tablaProtocolos.setRowSelectionInterval(tablaProtocolos.rowAtPoint(evt.getPoint()), tablaProtocolos.rowAtPoint(evt.getPoint()));
            tablaProtocolos.setColumnSelectionInterval(tablaProtocolos.columnAtPoint(evt.getPoint()), tablaProtocolos.columnAtPoint(evt.getPoint()));
            menuProtocolos.show(tablaProtocolos, evt.getPoint().x, evt.getPoint().y);
            IDPROTOCOLO = (int) tablaProtocolos.getValueAt(tablaProtocolos.getSelectedRow(), 0);
            int IDBUSQUEDA = tablaProtocolos.columnAtPoint(evt.getPoint());
        }
        /* ACCIONES QUE SE HACEN AL HACER DOBLE CLIC CON EL MOUSE ABRIENDO EL PROTOCOLO SELECCIONADO */
        if (evt.getClickCount() == 2) {
            IDPROTOCOLO = (int) tablaProtocolos.getValueAt(tablaProtocolos.getSelectedRow(), 0);
            abrirProtocolo();
        }
    }//GEN-LAST:event_tablaProtocolosMouseClicked
    /* EVENTO QUE SE DISPARA CON CADA PULSACION DEL TECLADO */
    private void cjBuscarPorMarcaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarPorMarcaKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjBuscarPorMarcaKeyReleased
    /* EVENTO QUE SE DISPARA CON CADA PULSACION DEL TECLADO */
    private void cjBuscarPorLoteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjBuscarPorLoteKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjBuscarPorLoteKeyReleased
    /* EVENTO DEL MENU QUE ABRE UN PROTOCOLO */
    private void subMenuAbrirProtocoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuAbrirProtocoloActionPerformed
        abrirProtocolo();
    }//GEN-LAST:event_subMenuAbrirProtocoloActionPerformed
    /* EVENTO DEL MENU QUE ELIMINA UN PROTOCOLO DE LA BASE DE DATOS */
    private void subMenuEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subMenuEliminarActionPerformed
        if (JOptionPane.showConfirmDialog(rootPane, "Desea eliminar el protocolo " + tablaProtocolos.getValueAt(tablaProtocolos.getSelectedRow(), 1) + "?") == JOptionPane.YES_OPTION) {
            if (conex.GUARDAR("DELETE FROM protocolos WHERE idprotocolo=" + IDPROTOCOLO)) {
                modeloTabla.removeRow(tablaProtocolos.getSelectedRow());
                //modelo.Metodos.M("EL PROTOCOLO HA SIDO ELIMINADO", "bien.png");                
            }
        }
    }//GEN-LAST:event_subMenuEliminarActionPerformed
    /* EVENTO QUE INVOCA AL METODO LIMPIAR */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        limpiar();
    }//GEN-LAST:event_jButton2ActionPerformed
    /* EVENTO QUE SE DISPARA CON CADA CAMBIO DE ESTADO DE COMUTADOR */
    private void conmutadorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_conmutadorItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            CargarTablas();
            comboDerivacion.setSelectedIndex(conmutador.getSelectedIndex());
        }
    }//GEN-LAST:event_conmutadorItemStateChanged
    /* EVENTO QUE SE DISPARA CON EL SALTO DE LINEA */
    private void cjobservacionesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjobservacionesKeyReleased
        if (evt.getKeyChar() == 10) {
            cjfechasalida.grabFocus();
        }
    }//GEN-LAST:event_cjobservacionesKeyReleased
    /* EVENTO QUE SE DISPARA CON EL SALTO DE LINEA */
    private void cjpcumedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjpcumedidoKeyTyped
        if (evt.getKeyChar() == 10) {
            subMenuItemRecalcular.doClick();
            if (cjpcumedido.getDouble() < cji2r.getDouble()) {
                modelo.Metodos.M("Las perdidas en el cobre son menores a las I²R.!", "advertencia.png");
                cjpcumedido.setBorder(new LineBorder(new Color(209, 72, 54), 2));
                cji2r.setBorder(new LineBorder(new Color(209, 72, 54), 2));
            } else {
                cjobservaciones.grabFocus();
            }
        }
    }//GEN-LAST:event_cjpcumedidoKeyTyped
    /* EVENTO QUE SE DISPARA CON EL SALTO DE LINEA */
    private void cjpomedidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjpomedidoKeyTyped
        if (evt.getKeyChar() == 10) {
            if (cjpomedido.getDouble() > cjpogarantizado.getDouble()) {
                modelo.Metodos.M("Las Po Medidas con mayores a las garantizadas.!!", "advertencia.png");
                cjpogarantizado.setBorder(new LineBorder(new Color(209, 72, 54), 2));
                cjpomedido.setBorder(new LineBorder(new Color(209, 72, 54), 2));
            } else {
                cjvcc.grabFocus();
            }
        }
    }//GEN-LAST:event_cjpomedidoKeyTyped
    /* EVENTO QUE SE DISPARA CON EL SALTO DE LINEA */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        /* ESTE METODO GENERA EL REPORTE EN EXCEL DE LA TABLA */
        ProgressMonitor pm = new ProgressMonitor(this, "Generando excel", "", 0, 0);
        int idcliente = comboCliente.getItemAt(comboCliente.getSelectedIndex()).getIdCliente();

        String sql = " SELECT count(*)\n"
                + "FROM entrada e \n"
                + "INNER JOIN transformador t ON e.identrada=t.identrada\n"
                + "LEFT JOIN despacho d ON d.iddespacho=t.iddespacho\n"
                + "LEFT JOIN remision r ON r.idremision=t.idremision\n"
                + "LEFT JOIN protocolos p ON p.idtransformador=t.idtransformador\n"
                + "WHERE e.idcliente=" + idcliente + " " + ((!cjBuscarPorLote.getText().isEmpty()) ? " AND e.lote='" + cjBuscarPorLote.getText().trim() + "' " : "");
        conex.conectar();
        ResultSet rs1 = conex.CONSULTAR(sql);

        try {
            rs1.next();
            pm.setMaximum(rs1.getInt("count"));
        } catch (SQLException ex) {
            Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
        }

        conex.conectar();
        (new Thread() {
            @Override
            public void run() {
                try {
                    String sql1 = " SELECT p.codigo, t.numeroserie, t.marca, t.kvasalida, t.fase, t.ano, t.tps, t.tss, p.i1, p.i2, p.proresuno, p.proresdos, p.pomedido, p.iu, p.iv, p.iw, \n"
                            + "p.promedioi, p.pogarantizado, p.pcu, p.vcc, p.temperaturadeensayo, p.i2r, p.i2ra85, p.pcua85, p.impedancia85, p.pcugarantizado, p.reg, p.atcontrabt, \n"
                            + "p.atcontratierra, p.btcontratierra, p.grupodeconexion, punou, pdosu, ptresu, pcuatrou, pcincou, punov, pdosv, ptresv, pcuatrov, pcincov, punow, pdosw, ptresw, pcuatrow, pcincow,\n"
                            + "p.anchotanque, p.largotanque, altotanque, t.serviciosalida, to_char(p.fechaderegistro, 'DD Mon YYYY'), ('') as vencegarantia, p.liquidoaislante, t.aceite, p.color, t.peso, \n"
                            + "e.lote, e.op, t.numeroempresa\n"
                            + "FROM entrada e \n"
                            + "INNER JOIN transformador t ON e.identrada=t.identrada\n"
                            + "LEFT JOIN despacho d ON d.iddespacho=t.iddespacho\n"
                            + "LEFT JOIN remision r ON r.idremision=t.idremision\n"
                            + "LEFT JOIN protocolos p ON p.idtransformador=t.idtransformador\n"
                            + "WHERE e.idcliente=" + idcliente + "\n "
                            + ((!cjBuscarPorLote.getText().isEmpty()) ? " AND e.lote='" + cjBuscarPorLote.getText().trim() + "' " : "")
                            + "ORDER BY e.identrada, fase, kvasalida, marca, item ";
                    ResultSet rs = conex.CONSULTAR(sql1);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(new File("PLANTILLAS EXCEL//CARACTERISTICAS DE PROTOCOLOS.xlsx")));
                    XSSFSheet hoja = wb.getSheetAt(0);
                    XSSFRow fila;
                    int filas = 4;
                    while (rs.next()) {
                        pm.setProgress(rs.getRow());
                        fila = hoja.createRow(filas);
                        for (int i = 0; i < rsmd.getColumnCount(); i++) {
                            if (rs.getObject((i + 1)) instanceof Double) {
                                fila.createCell(i, XSSFCell.CELL_TYPE_STRING).setCellValue(QD(rs.getDouble((i + 1)), 3));
                            } else if (rs.getObject((i + 1)) instanceof Integer) {
                                fila.createCell(i, XSSFCell.CELL_TYPE_NUMERIC).setCellValue(rs.getInt((i + 1)));
                            } else {
                                fila.createCell(i, XSSFCell.CELL_TYPE_STRING).setCellValue(rs.getString((i + 1)));
                            }
                        }
                        filas++;
                    }
                    for (int j = 0; j < rsmd.getColumnCount(); j++) {
                        wb.getSheetAt(0).autoSizeColumn(j);
                    }
                    File f = File.createTempFile("PROTOCOLOS", ".xlsx");
                    try (OutputStream out = new FileOutputStream(f)) {
                        wb.write(out);
                    }
                    Desktop.getDesktop().open(f);
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(PROTOS.class.getName()).log(Level.SEVERE, null, ex);
                    modelo.Metodos.ERROR(ex, "ERROR AL GENERAR EL REPORTE");
                }
            }
        }).start();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    /* EVENTO QUE SE DISPARA CON CADA CAMBIO DE ITEM */
    private void comboClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClienteItemStateChanged
        if (evt.getStateChange() == ItemEvent.DESELECTED) {
            buscarProtocolo();
        }
    }//GEN-LAST:event_comboClienteItemStateChanged
    /* EVENTO QUE SE DISPARA POR CADA CAMBIO DE ITEM */
    private void comboClaseAislamientoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboClaseAislamientoItemStateChanged
        // CASOS POR CADA POSICION DEL ITEM
        switch (comboClaseAislamiento.getSelectedIndex()) {
            case 0:
                jLabel61.setText("Pcc A°(W):");
                break;
            case 1:
                jLabel61.setText("Pcc a 75°(W):");
                break;
            case 2:
                jLabel61.setText("Pcc a 85°(W):");
                break;
            case 3:
                jLabel61.setText("Pcc a 100°(W):");
                break;
            case 4:
                jLabel61.setText("Pcc a 120°(W):");
                break;
            case 5:
                jLabel61.setText("Pcc a 145°(W):");
                break;
        }
    }//GEN-LAST:event_comboClaseAislamientoItemStateChanged

    private void mostrarProtocoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarProtocoloActionPerformed
        
    }//GEN-LAST:event_mostrarProtocoloActionPerformed

    private void comboContratoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboContratoItemStateChanged
        
    }//GEN-LAST:event_comboContratoItemStateChanged
    /* EVENTO Y FUNCIONES DEL BOTON EXPORTAR A EXCEL */
    private void btnExportarPdfsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarPdfsActionPerformed

        if (JOptionPane.showConfirmDialog(this, "Se van a generar " + tablaProtocolos.getRowCount() + " protocolos en PDF, ¿Desea continuar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos PDF", "pdf");
            fc.setFileFilter(filter);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int res = fc.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                (new Thread() {
                    @Override
                    public void run() {
                        tablaProtocolos.setColumnSelectionInterval(3, 3);
                        try {
                            for (int i = 0; i < tablaProtocolos.getRowCount(); i++) {
                                tablaProtocolos.setRowSelectionInterval(i, i);
                                tablaProtocolos.scrollRectToVisible(new Rectangle(tablaProtocolos.getCellRect(i, 3, true)));
                                repaint();
                                JasperReport reporte;
                                String servicio = String.valueOf((tablaProtocolos.getValueAt(i, 12)));
                                if ("NUEVO".equals(servicio) || "FABRICACION".equals(servicio)){
                                    System.out.println("Verdad");
                                    reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PROTOCOLO_NUEVO.jasper").toString()));
                                }else {
                                    System.out.println("Falso");
                                    reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/PROTOCOLO.jasper").toString()));
                                }

                                Map<String, Object> p = new HashMap<>();
                                p.put("IDPROTOCOLO", tablaProtocolos.getValueAt(i, 0));
                                JasperPrint jasperprint = JasperFillManager.fillReport(reporte, p, conex.conectar());
                                JasperExportManager.exportReportToPdfFile(jasperprint, new File(fc.getSelectedFile(), tablaProtocolos.getValueAt(i, 3) + ".pdf").toString());
                            }
                            JOptionPane.showMessageDialog(null, "TERMINADO");
                            Desktop.getDesktop().open(fc.getSelectedFile());
                        } catch (HeadlessException | IOException | JRException e) {
                            JOptionPane.showMessageDialog(null, "ERROR EL GENERAR LOS PDF\n" + e);
                        }
                    }
                }).start();
            }
        }

    }//GEN-LAST:event_btnExportarPdfsActionPerformed
    /* EVENTO QUE SE DISPARA CON CADA PULSACION DEL TECLADO */
    private void cjdespachoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cjdespachoKeyReleased
        buscarProtocolo();
    }//GEN-LAST:event_cjdespachoKeyReleased
    /* EVENTO DEL BOTON PARA IMPRIMIR LAS HOJAS DEL LABORATORIO */
    private void btnImprimirHojasDeLaboratorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirHojasDeLaboratorioActionPerformed

        try {

            JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(this.getClass().getResource("/REPORTES/TARJETA_ALMACEN.jasper").toString()));
            java.util.List<modelo.Transformador> lista = new ArrayList<>();

            int rows[] = tablaProtocolos.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(null, "SELECCIONE AL MENOS UN REGISTRO DE LA TABLA\n");
                return;
            }
            for (int i = rows.length - 1; i >= 0; i--) {

                Transformador t = new Transformador();
                t.setNumeroempresa(tablaProtocolos.getValueAt(rows[i], 4).toString());
                t.setNumeroserie(tablaProtocolos.getValueAt(rows[i], 3).toString());
                t.setMarca(tablaProtocolos.getValueAt(rows[i], 5).toString());
                t.setFase((int) tablaProtocolos.getValueAt(rows[i], 7));
                t.setKvasalida(Double.parseDouble(tablaProtocolos.getValueAt(rows[i], 6).toString()));
                String tension[] = (tablaProtocolos.getValueAt(rows[i], 8).toString().split("/"));
                t.setTps(Integer.parseInt(tension[0]));
                t.setTss(Integer.parseInt(tension[1]));
                t.setTts(Integer.parseInt(tension[2]));
                t.setServiciosalida(tablaProtocolos.getValueAt(rows[i], 12).toString());

                Cliente cliente = new Cliente();
                cliente.setNombreCliente(tablaProtocolos.getValueAt(rows[i], 2).toString());

                Lote lote = new Lote();
                lote.setContrato(tablaProtocolos.getValueAt(rows[i], 15).toString());
                lote.setOp(Integer.parseInt(tablaProtocolos.getValueAt(rows[i], 14).toString()));
                lote.setCliente(cliente);

                t.setLote(lote);

                lista.add(t);
            }

            JasperPrint jp = JasperFillManager.fillReport(reporte, null, new JRBeanCollectionDataSource(lista));
            new JasperViewer(jp, false).setVisible(true);

        } catch (HeadlessException | NumberFormatException | MalformedURLException | JRException e) {
            Logger.getLogger(PROTOS.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "ERROR EL GENERAR LOS PDF\n" + e);
        }

    }//GEN-LAST:event_btnImprimirHojasDeLaboratorioActionPerformed

    private void comboClaseAislamientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboClaseAislamientoActionPerformed
        
    }//GEN-LAST:event_comboClaseAislamientoActionPerformed

    private void comboFaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFaseActionPerformed
        
    }//GEN-LAST:event_comboFaseActionPerformed

    private void cjpogarantizadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cjpogarantizadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cjpogarantizadoActionPerformed

    //public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
    //    try {
    //        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
    //            if ("Windows".equals(info.getName())) {
    //                javax.swing.UIManager.setLookAndFeel(info.getClassName());
    //                break;
    //            }
    //        }
    //    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
    //        java.util.logging.Logger.getLogger(PROTOS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    //    }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
    //    java.awt.EventQueue.invokeLater(() -> {
    //        new PROTOS().setVisible(true);
    //    });
    //}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportarPdfs;
    private javax.swing.JButton btnGenerarExcel;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnImprimirHojasDeLaboratorio;
    private javax.swing.JCheckBox checkGarantia;
    private CompuChiqui.JTextFieldPopup cjATcontraBT;
    private CompuChiqui.JTextFieldPopup cjATcontraBTyTierra;
    private CompuChiqui.JTextFieldPopup cjATcontraTierra;
    private CompuChiqui.JTextFieldPopup cjBTcontraATyTierra;
    private CompuChiqui.JTextFieldPopup cjBTcontraTierra;
    private CompuChiqui.JTextFieldPopup cjBuscarPorLote;
    private javax.swing.JTextField cjBuscarPorMarca;
    private CompuChiqui.JTextFieldPopup cjFrecuenciaInducida;
    private CompuChiqui.JTextFieldPopup cjRuptura;
    private CompuChiqui.JTextFieldPopup cjTensionBT2;
    private CompuChiqui.JTextFieldPopup cjaceite;
    private CompuChiqui.JTextFieldPopup cjaltdiseno;
    private CompuChiqui.JTextFieldPopup cjalto;
    private CompuChiqui.JTextFieldPopup cjaltoelemento;
    private CompuChiqui.JTextFieldPopup cjancho;
    private CompuChiqui.JTextFieldPopup cjano;
    private CompuChiqui.JTextFieldPopup cjbuscarPorSerie;
    private CompuChiqui.JTextFieldPopup cjcalentamientodevanado;
    private CompuChiqui.JTextFieldPopup cjcliente;
    private CompuChiqui.JTextFieldPopup cjcolor;
    private javax.swing.JTextField cjdespacho;
    private CompuChiqui.JTextFieldPopup cjef;
    private CompuChiqui.JTextFieldPopup cjef1;
    private CompuChiqui.JTextFieldPopup cjef2;
    private CompuChiqui.JTextFieldPopup cjelementos;
    private CompuChiqui.JTextFieldPopup cjempresa;
    private CompuChiqui.JTextFieldPopup cjespesor;
    private com.toedter.calendar.JDateChooser cjfechasalida;
    private CompuChiqui.JTextFieldPopup cji1;
    private CompuChiqui.JTextFieldPopup cji2;
    private CompuChiqui.JTextFieldPopup cji2r;
    private CompuChiqui.JTextFieldPopup cji2ra85;
    private CompuChiqui.JTextFieldPopup cjimpedancia;
    private CompuChiqui.JTextFieldPopup cjimpedancia85;
    private CompuChiqui.JTextFieldPopup cjimpedanciagarantizado;
    private CompuChiqui.JTextFieldPopup cjiogarantizado;
    private CompuChiqui.JTextFieldPopup cjiu;
    private CompuChiqui.JTextFieldPopup cjiv;
    private CompuChiqui.JTextFieldPopup cjiw;
    private CompuChiqui.JTextFieldPopup cjkva;
    private CompuChiqui.JTextFieldPopup cjlargo;
    private CompuChiqui.JTextFieldPopup cjlargoelemento;
    private CompuChiqui.JTextFieldPopup cjlote;
    private CompuChiqui.JTextFieldPopup cjmarca;
    private CompuChiqui.JTextFieldPopup cjmasa;
    private CompuChiqui.JTextFieldPopup cjmetodo;
    private CompuChiqui.JTextFieldPopup cjnba;
    private CompuChiqui.JCTextArea cjobservaciones;
    private CompuChiqui.JTextFieldPopup cjpcua85;
    private CompuChiqui.JTextFieldPopup cjpcugarantizado;
    private CompuChiqui.JTextFieldPopup cjpcumedido;
    private CompuChiqui.JTextFieldPopup cjpogarantizado;
    private CompuChiqui.JTextFieldPopup cjpomedido;
    private CompuChiqui.JTextFieldPopup cjpromedioi;
    private CompuChiqui.JTextFieldPopup cjproresalta;
    private CompuChiqui.JTextFieldPopup cjproresbaja;
    private CompuChiqui.JTextFieldPopup cjprotocolo;
    private CompuChiqui.JTextFieldPopup cjreg;
    private CompuChiqui.JTextFieldPopup cjserie;
    private CompuChiqui.JTextFieldPopup cjtemperatura;
    private CompuChiqui.JTextFieldPopup cjtensionBT;
    private CompuChiqui.JTextFieldPopup cjtensionSerie;
    private CompuChiqui.JTextFieldPopup cjtiempoInducido;
    private CompuChiqui.JTextFieldPopup cjtiempoaplicado;
    private CompuChiqui.JTextFieldPopup cjtiemporalt;
    private CompuChiqui.JTextFieldPopup cjuv;
    private CompuChiqui.JTextFieldPopup cjvcc;
    private CompuChiqui.JTextFieldPopup cjvp;
    private CompuChiqui.JTextFieldPopup cjvs;
    private CompuChiqui.JTextFieldPopup cjvw;
    private CompuChiqui.JTextFieldPopup cjwu;
    private CompuChiqui.JTextFieldPopup cjxy;
    private CompuChiqui.JTextFieldPopup cjyz;
    private CompuChiqui.JTextFieldPopup cjzx;
    private javax.swing.JComboBox<String> comboAceite;
    private javax.swing.JComboBox<String> comboClaseAislamiento;
    public javax.swing.JComboBox<Cliente> comboCliente;
    public javax.swing.JComboBox<Cliente> comboContrato;
    private javax.swing.JComboBox<String> comboDerivacion;
    private javax.swing.JComboBox<String> comboFase;
    private javax.swing.JComboBox<String> comboFrecuencia;
    private javax.swing.JComboBox<String> comboGrupoConexion;
    private javax.swing.JComboBox<String> comboMaterialAlta;
    private javax.swing.JComboBox<String> comboMaterialBaja;
    private javax.swing.JComboBox<String> comboPolaridad;
    private javax.swing.JComboBox<String> comboReferenciaAceite;
    private javax.swing.JComboBox<String> comboRefrigeracion;
    private javax.swing.JComboBox<String> comboServicio;
    private javax.swing.JComboBox<String> comboTensionPrueba;
    private javax.swing.JComboBox<String> conmutador;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPopupMenu menuProtocolos;
    private javax.swing.JCheckBoxMenuItem mostrarProtocolo;
    private javax.swing.JPanel panelEnsayoAislamiento;
    private javax.swing.JPanel panelEnsayosinCarga;
    private javax.swing.JPanel panelInformacionGeneral;
    private javax.swing.JPanel panelLiquidoAislante;
    private javax.swing.JPanel panelRelacionTransformacion;
    private javax.swing.JPanel panelResistenciaAislamiento;
    private javax.swing.JPanel panelResistenciaTerminales;
    private javax.swing.JMenuItem subMenuAbrirProtocolo;
    private javax.swing.JMenuItem subMenuEliminar;
    private javax.swing.JMenuItem subMenuItemRecalcular;
    private javax.swing.JTable tablaDos;
    private javax.swing.JTable tablaProtocolos;
    private javax.swing.JTable tablaUno;
    // End of variables declaration//GEN-END:variables

    /* DECLARACION DE CLASE ANIDADA NO ESTATICA PRIVADA NO HEREDIRARIA */
    private class Hilofases extends Thread {
        /* DECLARACION DE VARIABLES QUE REPRESENTAN FILAS Y COLUMNAS */
        private int fila = -1;
        private int col = -1;

        /* CONSTRUCTOR DE LA CLASE */
        public Hilofases(int row, int col) {
            this.fila = row;
            this.col = col;
        }
        
        
        /* METODO RUN SOBRECRITO DE LA CLASE THREAD USANDO PARA HACER SECUENCIAS */
        @Override
        public void run() {
            while (true) {
                System.out.println("CORRIENDO HILO");
                double fase = Double.parseDouble(tablaUno.getValueAt(getFila(), getCol()).toString());
                double minimo = Double.parseDouble(tablaDos.getValueAt(getFila(), 1).toString());
                double maximo = Double.parseDouble(tablaDos.getValueAt(getFila(), 2).toString());
                if (fase < minimo || fase > maximo) {
                    try {
                        JTextField fileName = new JTextField(String.valueOf(fase));
                        Object[] message = {"INGRESE UN NUEVO VALOR.\n(MIN = " + minimo + " MAX = " + maximo + ")", fileName};
                        int n = JOptionPane.showOptionDialog(rootPane, message, "VALOR FUERA DE RANGO", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, modelo.Metodos.getIcon("advertencia.png"), new Object[]{"ACEPTAR", "OMITIR ERROR"}, "ACEPTAR");
                        if (n == 1) {
                            break;
                        } else if (n == 0) {
                            if (Double.parseDouble(fileName.getText()) >= minimo && Double.parseDouble(fileName.getText()) <= maximo) {
                                tablaUno.setValueAt(Double.parseDouble(fileName.getText()), getFila(), getCol());
                                break;
                            }
                        }
                    } catch (HeadlessException | NumberFormatException ex) {
                        modelo.Metodos.M("ERROR\n" + ex, "error.png");
                    }
                } else {
                    break;
                }
            }
        }

        public int getFila() {
            return fila;
        }

        public void setFila(int fila) {
            this.fila = fila;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

    }

}

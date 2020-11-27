package modelo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControlTotal {
    
    private static boolean FINISH = false;
    
    public static String[] getColumnNames(){
        return new String[]{
            "ID",
            "CLIENTE",
            "LOTE",
            "SERIE",
            "N° EMPRESA",
            "FASE",
            "MARCA",
            "KVA ENT",
            "KVA SAL",
            "TENSION ENT",
            "TENSION SAL",
            "SERV ENT",
            "SERV SAL",
            "ESTADO",
            "REMISION",
            "DESPACHO",
            "RECEPCION",
            "CIUDAD",
            "CONTRATO",
            "O.P.",
            "CENTRO DE COSTOS"            
        };
    }
    
    public static Boolean[] getColumnEditables(){
        return new Boolean[]{
            false,//
            false,//
            false,//
            false,//
            false,//
            false,
            false,//
            false,//
            false,//
            false,
            false,//
            false,//
            false,//
            false,//
            false,//            
            false,//
            false,
            false,//
            false,//
            false,//
            false
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,//"ID°",
            String.class,//"CLIENTE",
            String.class,//"LOTE",
            String.class,//"SERIE",            
            String.class,//"No EMPRESA
            Integer.class,//FASE
            String.class,//MARCA
            Double.class,//KVA ENT
            Double.class,//KVA SAL
            String.class,//TENSION ENT
            String.class,//TENSION SAL
            String.class,//SERV ENT
            String.class,//SERV SAL
            String.class,//ESTADO
            String.class,//REMISION
            String.class,//DESPACHO
            String.class,//FECHA RECEPCION
            String.class,//CIUDAD
            String.class,//CONTRATO
            Integer.class,//OP
            String.class//CENTRO DE COSTOS
        };
    }
    
    public static void cargarTrafos(DefaultTableModel modelo, javax.swing.JProgressBar barra){        
        modelo.ConexionBD con = new ConexionBD();
        try {
            con.conectar();

            ResultSet rs = con.CONSULTAR("SELECT count(*) FROM entrada e INNER JOIN transformador t USING(identrada) INNER JOIN cliente cli USING (idcliente) INNER JOIN ciudad ciu USING (idciudad) LEFT JOIN despacho d USING(iddespacho) LEFT JOIN remision r USING(idremision)");
            rs.next();
            barra.setMaximum(rs.getInt("count"));

            rs = con.CONSULTAR("SELECT e.identrada, cli.nombrecliente, e.lote, t.numeroserie,\n" +
                    "t.numeroempresa, t.fase, t.marca, t.kvaentrada, t.kvasalida, t.servicioentrada, t.serviciosalida, "+ 
                    "(t.tpe || '-' || t.tse || '-' || t.tte) as tensionentrada, \n" +
                    "(t.tps || '-' || t.tss || '-' || t.tts) as tensionsalida, t.estado, r.numero_remision,\n" +
                    "d.nodespacho, e.fecharecepcion, ciu.nombreciudad, e.contrato, e.op, e.centrodecostos\n" +
                    "FROM entrada e INNER JOIN transformador t USING(identrada)\n" + 
                    "INNER JOIN cliente cli USING (idcliente)\n" +
                    "INNER JOIN ciudad ciu USING (idciudad) \n" +
                    "LEFT JOIN despacho d USING(iddespacho)\n" +
                    "LEFT JOIN remision r USING(idremision)\n" +
                    "ORDER BY e.idcliente, e.fecharecepcion");
            ResultSetMetaData rsm = rs.getMetaData();

            while(rs.next()){
                barra.setValue(rs.getRow());
                modelo.addRow(new Object[]{
                    rs.getInt("identrada"),
                    rs.getString("nombrecliente"),
                    rs.getString("lote"),
                    rs.getString("numeroserie"),
                    rs.getString("numeroempresa"),
                    rs.getInt("fase"),
                    rs.getString("marca"),
                    rs.getDouble("kvaentrada"),
                    rs.getDouble("kvasalida"),
                    rs.getString("tensionentrada"),
                    rs.getString("tensionsalida"),
                    rs.getString("servicioentrada"),
                    rs.getString("serviciosalida"),
                    rs.getString("estado"),
                    rs.getString("numero_remision"),
                    rs.getString("nodespacho"),
                    new SimpleDateFormat("EEE, d MMM yyyy").format(rs.getDate("fecharecepcion")),
                    rs.getString("nombreciudad"),
                    rs.getString("contrato"),
                    rs.getInt("op"),
                    rs.getString("centrodecostos")
                });
            }
            setFINISH(true);
            barra.setValue(0);                    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ocurrio un error al cargar la lista de transformadores.\n"+e, "Error", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Despacho.class.getResource("/recursos/images/error.png")));
            Logger.getLogger(Despacho.class.getName()).log(Level.SEVERE, null, e);
        }finally{
            con.CERRAR();
        }           
    }

    public static boolean isFINISH() {
        return FINISH;
    }

    public static void setFINISH(boolean aFINISH) {
        FINISH = aFINISH;
    }

    
}

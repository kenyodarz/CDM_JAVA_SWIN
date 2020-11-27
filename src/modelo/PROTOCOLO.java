package modelo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class PROTOCOLO {
    public static String[] getColumnNames(){
        return new String[]{
            "Nº",
            "PROTOCOLO Nº",
            "CLIENTE",
            "N° SERIE",
            "N° EMPRESA",
            "MARCA",
            "KVA",
            "FASE",
            "TENSION",
            "LOTE",
            "REMISON",
            "DESPACHO",
            "SERVICIO",
            "ELABORÓ",
            "O.P",
            "CONTRATO"
        };
    }
    
    public static Boolean[] getColumnEditables(){
        return new Boolean[]{
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            Double.class,
            Integer.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class
        };
    }
    
    public static void cargarProtocolos(DefaultTableModel model){
        ConexionBD conex = new ConexionBD();
        conex.conectar();
        ResultSet rs = conex.CONSULTAR("SELECT p.idprotocolo, p.codigo, p.fechaderegistro, u.nombreusuario, r.numero_remision, e.identrada, e.op, e.contrato, cli.nombrecliente, e.lote, t.numeroserie, t.numeroempresa, t.fase, t.marca, t.kvasalida, (tps || '/' || tss || '/' || tts) as tension, t.serviciosalida, \n" +
                "d.nodespacho \n" +
                "FROM entrada e \n" +
                "INNER JOIN usuario u USING(idusuario)\n" +
                "INNER JOIN transformador t USING(identrada)\n" +
                "INNER JOIN cliente cli USING (idcliente)\n" +
                "INNER JOIN ciudad ciu USING (idciudad) \n" +
                "LEFT JOIN despacho d USING(iddespacho)\n" +
                "LEFT JOIN remision r USING(idremision)\n" +
                "RIGHT JOIN protocolos p ON p.idtransformador=t.idtransformador\n" +
                "ORDER BY p.idprotocolo DESC");
        try {
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("idprotocolo"),
                    rs.getString("codigo"),
                    rs.getString("nombrecliente"),
                    rs.getString("numeroserie"),
                    rs.getString("numeroempresa"),
                    rs.getString("marca"),
                    rs.getString("kvasalida"),
                    rs.getInt("fase"),
                    rs.getString("tension"),
                    rs.getString("lote"),
                    rs.getString("numero_remision"),
                    rs.getString("nodespacho"),
                    rs.getString("serviciosalida"),
                    rs.getString("nombreusuario"),
                    rs.getString("op"),
                    rs.getString("contrato")
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(PROTOCOLO.class.getName()).log(Level.SEVERE, null, ex);
            modelo.Metodos.ERROR(ex, "ERROR AL CARGAR LA TABLA DE PROTOCOLOS.");
        }finally{
            conex.CERRAR();
        }
    }
}

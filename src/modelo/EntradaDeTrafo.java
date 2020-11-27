package modelo;

public class EntradaDeTrafo{
            
    public static String[] getColumnNames(){
        return new String[]{
            "ID",
            "ITEM°",
            "REMISION",
            "N° EMPRESA",
            "N° SEIRE",
            "MARCA",
            "KVA",
            "FASE",
            "TENSION",
            "A.T",
            "B.T",
            "H.A",
            "H.B",
            "INT",
            "EXT",
            "HERRAJE",
            "AÑO",
            "PESO",
            "ACEITE",
            "OBSERVACION",
            "SERVICIO",
            "TIPO",            
        };
    }
    
    public static Boolean[] getColumnEditables(){
        return new Boolean[]{
            true,
            true,//"N°",0
            true,//"REMISION",1
            true,//"N° EMPRESA",2
            true,//"N° SEIRE",3
            true,//"MARCA",4
            true,//"KVA",5
            true,//"FASE",6
            true,//"TENSION P",7
            true,//"A.T",10
            true,//"B.T",11
            true,//"H.A",12
            true,//"H.B",13
            true,//"INT",14
            true,//"EXT",15
            true,//"HERRAJE",16
            true,//"AÑO",17
            true,//"PESO",18
            true,//"ACEITE",19
            true,//"OBSERVACION",20
            true,//"SERVICIO",21
            true,//"TIPO",22      
        };
    }
    
    public static Class[] getColumnClass(){
        return new Class[]{
            Integer.class,//ID
            Integer.class,//"ITEM°",
            String.class,//"REMISION",
            String.class,//"N° EMPRESA",
            String.class,//"N° SEIRE",
            String.class,//"MARCA",
            Double.class,//"KVA",
            Integer.class,//"FASE",
            String.class,//"TENSION P",
            Integer.class,//"A.T",
            Integer.class,//"B.T",
            Integer.class,//"H.A",
            Integer.class,//"H.B",
            Boolean.class,//"INT",
            Boolean.class,//"EXT",
            String.class,//"HERRAJE",
            Integer.class,//"AÑO",
            Integer.class,//"PESO",
            Integer.class,//"ACEITE",
            String.class,//"OBSERVACION",
            String.class,//"SERVICIO",
            String.class//"TIPO",                        
        };
    }
    
}

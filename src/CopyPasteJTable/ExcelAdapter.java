package CopyPasteJTable;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelAdapter extends JFrame implements ActionListener{
    private String rowstring,value;
    private Clipboard system;
    private StringSelection stsel;
    private JTable jTable1 ;
   
    public ExcelAdapter(JTable myJTable){
        jTable1 = myJTable;
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
        KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK,false);
        KeyStroke undo = KeyStroke.getKeyStroke(KeyEvent.VK_Z,ActionEvent.CTRL_MASK,false);
        KeyStroke redo = KeyStroke.getKeyStroke(KeyEvent.VK_Y,ActionEvent.CTRL_MASK,false);
        
        jTable1.registerKeyboardAction(this,"Copy",copy,JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this,"Paste",paste,JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this,"Cut",cut,JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this,"Undo",undo,JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this,"Redo",redo,JComponent.WHEN_FOCUSED);
        
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }
    
    public JTable getJTable() {
        return jTable1;
    }
    
    public void setJTable(JTable jTable1) {
        this.jTable1=jTable1;
    }
    
    public void Copy(final boolean cut){
        Thread t = new Thread(){
            public void run(){
                StringBuffer sbf=new StringBuffer();
                try{
                    if(jTable1.getSelectedRow() >= 0){
                        int filas_seleccionadas[] = jTable1.getSelectedRows();
                        int columnas_seleccionadas[] = jTable1.getSelectedColumns();
                        StringBuffer contenido = new StringBuffer();
                        for(int i=0; i<filas_seleccionadas.length; i++){
                            for(int j=0; j<columnas_seleccionadas.length; j++){
                                contenido.append(jTable1.getValueAt(filas_seleccionadas[i], columnas_seleccionadas[j])).append("\t");
                                if(cut){jTable1.setValueAt("", filas_seleccionadas[i], columnas_seleccionadas[j]);repaint();validate();}
                            }
                            contenido.append("\n");                
                        }
                        stsel  = new StringSelection(contenido.toString());
                        system = Toolkit.getDefaultToolkit().getSystemClipboard();
                        system.setContents(stsel,stsel);
                    }else{
                        JOptionPane.showMessageDialog(null, "NO HAZ SELECCIONADA NADA PARA COPIAR");
                    }
                }catch(Exception e){JOptionPane.showMessageDialog(null, "OCURRIO UN ERROR AL COPIAR LOS ARCHIVOS\n"+e);Logger.getLogger(ExcelAdapter.class.getName()).log(Level.SEVERE, null, e);}
            }
        };t.start();
//        (new Thread(){
//            public void run(){
//                
//                StringBuffer sbf =new StringBuffer();
//                int numcols = jTable1.getSelectedColumnCount();
//                int numrows = jTable1.getSelectedRowCount();
//                int[] rowsselected = jTable1.getSelectedRows();
//                int[] colsselected = jTable1.getSelectedColumns();
//                
////                if (!((numrows-1==rowsselected[rowsselected.length-1]-rowsselected[0] && 
////                        numrows==rowsselected.length) &&
////                        (numcols-1==colsselected[colsselected.length-1]-colsselected[0] &&
////                        numcols==colsselected.length))){
////                    JOptionPane.showMessageDialog(null, "Copia de seleccion invalida","Invalid Copy Selection",JOptionPane.ERROR_MESSAGE);
////                    return;
////                }
//
//                int filas[] = jTable1.getSelectedRows();
//                int cols[] = jTable1.getSelectedColumns();
//                
//                if(filas.length > 0 && cols.length>0){
//                    for(int i=filas[0]; i<=filas[filas.length-1]; i++){
//                        for(int j=cols[0]; j<=cols[cols.length-1]; j++){
//                            sbf.append(jTable1.getValueAt(i,j));
//                            if(j!=cols[cols.length-1])
//                                sbf.append("\t");                            
//                            if(cut){jTable1.setValueAt("", i, j);
//                            repaint();validate();}
//                        }
//                        sbf.append("\n");
//                    }
//                }
//
////                for (int i=0;i<numrows;i++){
////                    for (int j=0;j<numcols;j++){
////                        sbf.append(jTable1.getValueAt(rowsselected[i],colsselected[j]));
////                        if(cut){jTable1.setValueAt("", rowsselected[i], colsselected[j]);repaint();validate();}
////                        if(j==1)
////                            sbf.append("");
////                        else if(j<numcols-1)
////                            sbf.append("\t");
////                    }
////                    sbf.append("\n");
////                }
//
//                stsel  = new StringSelection(sbf.toString());
//                system = Toolkit.getDefaultToolkit().getSystemClipboard();
//                system.setContents(stsel,stsel);
//            }
//        }).start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getActionCommand().compareTo("Copy")==0){
            Copy(false);
        }
        
        if (e.getActionCommand().compareTo("Paste")==0){
            (new Thread(){
                public void run(){
                    int startRow=(jTable1.getSelectedRows())[0];
                    int startCol=(jTable1.getSelectedColumns())[0];
                    try{
                        String trstring= (String)(system.getContents(this).getTransferData(DataFlavor.stringFlavor));
                        StringTokenizer st1=new StringTokenizer(trstring,"\n");
                        for(int i=0;st1.hasMoreTokens();i++){
                            rowstring=st1.nextToken();
                            StringTokenizer st2=new StringTokenizer(rowstring,"\t");
                            for(int j=0;st2.hasMoreTokens();j++){
                                value=(String)st2.nextToken();
                                if (startRow+i< jTable1.getRowCount()  && startCol+j< jTable1.getColumnCount())
                                    jTable1.setValueAt(value,startRow+i,startCol+j);
                                    System.out.println("Putting "+ value+"atrow="+startRow+i+"column="+startCol+j);
                            }
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }).start();            
        }
        
        if (e.getActionCommand().compareTo("Cut")==0){
            Copy(true);
        }
        
        if (e.getActionCommand().compareTo("Undo")==0){
            
        }
        
        if (e.getActionCommand().compareTo("Redo")==0){
            
        }
        
    }
}

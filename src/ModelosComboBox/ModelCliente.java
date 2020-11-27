package ModelosComboBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import modelo.Cliente;

public class ModelCliente extends AbstractListModel implements ComboBoxModel{

    modelo.ConexionBD con = new modelo.ConexionBD();
        
    ArrayList<modelo.Cliente> lista=new ArrayList();
    
    Cliente selection = null;
    
    public ModelCliente() {

        con.conectar();
        ResultSet rs = con.CONSULTAR("SELECT * FROM cliente ORDER BY nombrecliente");
        try {
            while(rs.next()){
                lista.add(new Cliente(
                        rs.getInt("idcliente"), 
                        rs.getString("nombrecliente"), 
                        rs.getString("nitcliente")));
                System.out.println(rs.getInt("idcliente")+"\t"+rs.getString("nombrecliente")+"\t"+rs.getString("nitcliente"));
            }
        } catch (SQLException ex) {
            
        }
        
    }   
    
    @Override
    public int getSize() {
        return lista.size();
    }

    @Override
    public Object getElementAt(int index) {
        return lista.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = (Cliente) anItem;
    }

    @Override
    public Cliente getSelectedItem() {
        return selection;
    }
    
}

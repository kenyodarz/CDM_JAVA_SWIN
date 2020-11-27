package JComboBoxColor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
/**
 * @web http://www.jc-mouse.net/
 * @author Mouse
 */
public class JComboBoxColor extends BasicComboBoxUI{
    
    private ImageIcon icono =  new ImageIcon(getClass().getResource("/recursos/images/item.png"));
    private Color red = new Color(209,72,54,200);
    private Color azul_cdm = new Color(51,122,183,255);
    private Color negro_claro = new Color(70,70,70);
        
    public static ComboBoxUI createUI(JComponent c) {
        return new JComboBoxColor();
    }
    
//    @Override 
//    protected JButton createArrowButton() {
//        BasicArrowButton basicArrowButton = new BasicArrowButton(BasicArrowButton.SOUTH, //Direccion de la flecha
//                Color.WHITE, //Color de fondo
//                new Color(130,7,7),//sombra
//                new Color(130,7,7),//darkShadow
//                Color.WHITE //highlight
//                );         
//        //se quita el efecto 3d del boton, sombra y darkShadow no se aplican 
//        basicArrowButton.setBorder(BorderFactory.createLineBorder(red,2));
//        basicArrowButton.setContentAreaFilled(false);        
//        return basicArrowButton;
//    }        

    //Se puede usar un JButton para usar un icono personalizado en lugar del arrow
      
    @Override 
    protected JButton createArrowButton() {                 
        JButton button = new JButton();        
        //se quita el efecto 3d del boton, sombra y darkShadow no se aplican 
        button.setText("");
        button.setBorder(BorderFactory.createLineBorder(red,1));
        button.setContentAreaFilled(false);
        button.setIcon(new ImageIcon(getClass().getResource("/recursos/images/preparardespacho.png")));
        return button;
    }      
    
   
    @Override
    public void paintCurrentValueBackground(Graphics g,Rectangle bounds,boolean hasFocus){
        g.setColor( red );            
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
      
    //Pinta los items
    @Override
    protected ListCellRenderer createRenderer(){
        return new DefaultListCellRenderer() {            
            @Override
            public Component getListCellRendererComponent(JList list,Object value,int index, boolean isSelected,boolean cellHasFocus) {

                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                list.setSelectionBackground(red);
                if (isSelected){
                    setBackground( red );
                    setForeground(Color.WHITE);
                }else{
                    setBackground( Color.WHITE );            
                    setForeground(azul_cdm );
                }
                if(index!=-1){
                    setIcon(icono);
                }
                return this;
            }
        };
    }
}

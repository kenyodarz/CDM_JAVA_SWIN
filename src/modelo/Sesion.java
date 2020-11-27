package modelo;

public class Sesion {
 
    private String nombre;
    private int idUsuario;
    
    private static Sesion miconfigurador;
 
    public static Sesion getConfigurador(String nombre, int idUsuario) {
        if (miconfigurador==null) {
            miconfigurador=new Sesion(nombre,idUsuario);
        }
        return miconfigurador;
    }
    
    @Override
    public Sesion clone(){
        try {
            throw new CloneNotSupportedException();
        } catch (CloneNotSupportedException ex) {
            System.out.println("No se puede clonar un objeto de la clase SoyUnico");
        }
        return null; 
    }

    private Sesion(String nombre, int idUsuario) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
 
        
}
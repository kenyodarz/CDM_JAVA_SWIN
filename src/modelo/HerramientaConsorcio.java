package modelo;

public class HerramientaConsorcio {

    private int idHerramienta;
    private String nombreHerramienta;
    private String codigoHerramienta;
    
    public HerramientaConsorcio(int idHerramienta, String nombreHerramienta, String codigoHerramienta) {
        this.idHerramienta = idHerramienta;
        this.nombreHerramienta = nombreHerramienta;
        this.codigoHerramienta = codigoHerramienta;
    }
    
    public int getIdHerramienta() {
        return idHerramienta;
    }

    public void setIdHerramienta(int idHerramienta) {
        this.idHerramienta = idHerramienta;
    }

    public String getNombreHerramienta() {
        return nombreHerramienta;
    }

    public void setNombreHerramienta(String nombreHerramienta) {
        this.nombreHerramienta = nombreHerramienta;
    }

    public String getCodigoHerramienta() {
        return codigoHerramienta;
    }

    public void setCodigoHerramienta(String codigoHerramienta) {
        this.codigoHerramienta = codigoHerramienta;
    }
    
    @Override
    public String toString(){
        return nombreHerramienta+" "+codigoHerramienta;
    }
    
}

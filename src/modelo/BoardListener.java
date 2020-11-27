package modelo;

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

public class BoardListener implements ClipboardOwner {

    // Método que recupera una cadena del portapapeles.
    public String getClipboard() {
        // Obtenemos el contenido del portapapeles del sistema.
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            // Comprobamos que la información sea de tipo cadena, lo recuperamos y lo devolvemos.
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String) t.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException | IOException e) {
        }
        // Si lo copiado no es un texto, devolvemos null
        return null;
    }

    // Método que inserta en el portapapeles una cadena.
    public void setClipboard(String str) {
        StringSelection ss = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, this);
    }

    // Necesario para poder implementar la interfaz ClipboardOwner. Para nosotros no tendrá uso, pero es obligatorio "implementarlo".
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        
    }
}

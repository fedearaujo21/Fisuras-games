package juego;

import java.awt.*;

public class Lemming extends Panel {

    private final String rutaImagen = 

    public Lemming(){
        //this.add(limon);
        //limon.setBounds(0,0,10,10);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (imagen != null) {
            // Dibuja la imagen ocupando todo el tamaño del panel
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }

}

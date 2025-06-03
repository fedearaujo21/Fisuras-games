package lemmings.modelo;

import java.awt.Graphics; //
import java.awt.image.BufferedImage; //

public class Mapa{
    private BufferedImage terreno;
    private boolean[][] colision;
    private int ancho, alto;
    private final int colorFondoVacio;

    public Mapa(BufferedImage imagen, int colorFondoVacio) { //
        this.terreno = imagen;
        this.ancho = imagen.getWidth();
        this.alto = imagen.getHeight();
        this.colision = new boolean[ancho][alto];
        this.colorFondoVacio = colorFondoVacio; // Se inicializa con el valor pasado

        // Inicializar matriz de colisiones:
        // Cualquier píxel que NO sea el colorFondoVacio se considera terreno (colisión).
        for (int x = 0; x < ancho; x++) { //
            for (int y = 0; y < alto; y++) { //
                int pixel = terreno.getRGB(x, y); //
                colision[x][y] = (pixel != this.colorFondoVacio); // Usa el campo de instancia
            }
        }
    }

    public boolean hayColision(int x, int y) { //
        if (x < 0 || x >= ancho || y < 0 || y >= alto) //
            return true; //
        return colision[x][y]; //
    }

    public void eliminarPix(int x, int y) { //
        if (x >= 0 && x < ancho && y >= 0 && y < alto) { //
            terreno.setRGB(x, y, this.colorFondoVacio); // Usa el campo de instancia
            colision[x][y] = false; //
        }
    }

    public void agregarPix(int x, int y) { //
        if (x >= 0 && x < ancho && y >= 0 && y < alto) { //
            // Puedes definir un color por defecto para el terreno construido.
            // Asegúrate de que este color NO sea el colorFondoVacio.
            terreno.setRGB(x, y, 0xFF000000); // Negro opaco como ejemplo de terreno construido
            colision[x][y] = true; //
        }
    }

    public void dibujar(Graphics g) { //
        g.drawImage(terreno, 0, 0, null); //
    }

    public BufferedImage getTerreno() { //
        return terreno; //
    }

    public int getAncho() { //
        return ancho; //
    }

    public int getAlto() { //
        return alto; //
    }
}
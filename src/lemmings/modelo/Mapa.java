package lemmings.modelo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Mapa {
    private BufferedImage terreno;
    private boolean[][] colision;
    private int ancho, alto;
    private final int colorFondoVacio;
    private boolean colisionesActivas = true;

    public Mapa(BufferedImage imagen, int colorFondoVacio) {
        this.terreno = imagen;
        this.ancho = imagen.getWidth();
        this.alto = imagen.getHeight();
        this.colision = new boolean[ancho][alto];
        this.colorFondoVacio = colorFondoVacio;

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                int pixel = terreno.getRGB(x, y);
                colision[x][y] = (pixel != colorFondoVacio);
            }
        }
    }

    public boolean hayColision(int x, int y) {
        if (x < 0 || x >= ancho || y < 0 || y >= alto)
            return true;
        return colision[x][y];
    }

    public void eliminarPix(int x, int y) {
        if (x >= 0 && x < ancho && y >= 0 && y < alto) {
            terreno.setRGB(x, y, this.colorFondoVacio);
            colision[x][y] = false;
        }
    }

    public void agregarPix(int x, int y) {
        if (x >= 0 && x < ancho && y >= 0 && y < alto) {
            terreno.setRGB(x, y, 0xFF8B4513); // Negro opaco como ejemplo de terreno construido
            colision[x][y] = true;
        }
    }

    public void limpiarArea(int xInicio, int yInicio, int anchoArea, int altoArea, int colorParaLimpiar) {
        for (int x = xInicio; x < xInicio + anchoArea; x++) {
            for (int y = yInicio; y < yInicio + altoArea; y++) {
                if (x >= 0 && x < this.ancho && y >= 0 && y < this.alto) {
                    terreno.setRGB(x, y, colorParaLimpiar); // Pinta el píxel con el color de vacío
                    colision[x][y] = false; // Establece ese píxel como NO colisionable
                }
            }
        }
    }



    public void activarColisiones(boolean estado) {
        this.colisionesActivas = estado;
    }

    public void dibujar(Graphics g) {
        g.drawImage(terreno, 0, 0, null);
    }

    public BufferedImage getTerreno() {
        return terreno;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }
}

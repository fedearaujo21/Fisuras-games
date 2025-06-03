package lemmings.modelo;

import java.awt.*; // Para Graphics y Color

public class Salida {
    private int x, y;
    private int ancho; // Ancho del área de salida
    private int alto;  // Alto del área de salida

    public Salida(int x, int y, int ancho, int alto) { // Ahora recibe ancho y alto
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    // Método para dibujar la salida (un simple rectángulo por ahora)
    public void dibujar(Graphics g) {
        g.setColor(Color.BLUE); // Color azul para la salida
        g.drawRect(x, y, ancho, alto); // Dibuja el contorno con ancho y alto
        g.drawString("SALIDA", x + 5, y + alto / 2); // Texto para identificar
    }

    // Mtodo para verificar si un Lemming ha alcanzado la salida
    public boolean haAlcanzado(Lemming lemming) {
        return lemming.getX() < this.x + this.ancho &&        // El Lemming no está completamente a la derecha de la salida
                lemming.getX() + lemming.getLemmingWidth() > this.x && // El Lemming no está completamente a la izquierda de la salida
                lemming.getY() < this.y + this.alto &&        // El Lemming no está completamente debajo de la salida
                lemming.getY() + lemming.getLemmingHeight() > this.y;  // El Lemming no está completamente encima de la salida
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; } // Ahora se necesitan estos getters
    public int getAlto() { return alto; }   // Ahora se necesitan estos getters
}

package lemmings.modelo;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BotonHabilidad {
    private int x, y, ancho, alto;
    private BufferedImage icono;
    private String nombre; // solo para debug o mostrar texto

    public BotonHabilidad(int x, int y, int ancho, int alto, BufferedImage icono, String nombre) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.icono = icono;
        this.nombre = nombre;
    }

    public void dibujar(Graphics g, boolean seleccionado) {
        // Fondo del botón (oscurecido si está seleccionado)
        g.setColor(seleccionado ? Color.DARK_GRAY : Color.LIGHT_GRAY);
        g.fillRect(x, y, ancho, alto);

        // Borde exterior siempre gris oscuro
        g.setColor(Color.GRAY);
        g.drawRect(x, y, ancho, alto);


        // Parte superior: cuadrado blanco centrado (contador)
        int ladoContador = ancho / 2; // cuadrado más chico
        int contadorX = x + (ancho - ladoContador) / 2;
        int contadorY = y + 4;

        g.setColor(Color.WHITE);
        g.fillRect(contadorX, contadorY, ladoContador, ladoContador);

        // Texto "∞" centrado dentro del cuadrado
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int textoX = contadorX + (ladoContador - fm.stringWidth("∞")) / 2;
        int textoY = contadorY + (ladoContador + fm.getAscent()) / 2 - 2;
        g.drawString("∞", textoX, textoY);


        // Parte inferior: ícono
        g.drawImage(icono, x + 2, y + ladoContador + 8, ancho - 4, alto - ladoContador - 10, null);

    }



    public boolean contienePunto(int px, int py) {
        return px >= x && px <= x + ancho && py >= y && py <= y + alto;
    }

    public String getNombre() {
        return nombre;
    }
}
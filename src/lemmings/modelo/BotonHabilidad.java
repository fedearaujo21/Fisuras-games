package lemmings.modelo;

import lemmings.modelo.Nivel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BotonHabilidad {
    private int x, y, ancho, alto;
    private BufferedImage icono;
    private String nombre; // solo para debug o mostrar texto
    private Stock stock;

    public BotonHabilidad(int x, int y, int ancho, int alto, BufferedImage icono, String nombre, Stock stock) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.icono = icono;
        this.nombre = nombre;
        this.stock = stock;
    }


    public void dibujar(Graphics g, boolean seleccionado) {
        g.setColor(seleccionado ? Color.DARK_GRAY : Color.LIGHT_GRAY);
        g.fillRect(x, y, ancho, alto);

        g.setColor(Color.GRAY);
        g.drawRect(x, y, ancho, alto);

        int ladoContador = ancho / 2;
        int contadorX = x + (ancho - ladoContador) / 2;
        int contadorY = y + 4;

        g.setColor(Color.WHITE);
        g.fillRect(contadorX, contadorY, ladoContador, ladoContador);

        // Parte del texto dentro del contador
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        String texto;

        if (nombre.equals("Mas")) {
            texto = String.valueOf(Nivel.getFrecuenciaSpawn()); // dinámico
        } else if (nombre.equals("Menos")) {
            texto = "50"; // fijo
        } else {
            // ← caso normal: habilidades con stock
            int cantidad = stock.getCantidad(nombre);
            texto = (cantidad < 0) ? "∞" : String.valueOf(cantidad);
        }

        int textoX = contadorX + (ladoContador - fm.stringWidth(texto)) / 2;
        int textoY = contadorY + (ladoContador + fm.getAscent()) / 2 - 2;
        g.drawString(texto, textoX, textoY);

        // Dibuja el ícono
        g.drawImage(icono, x + 2, y + ladoContador + 8, ancho - 4, alto - ladoContador - 10, null);
    }





    public boolean contienePunto(int px, int py) {
        return px >= x && px <= x + ancho && py >= y && py <= y + alto;
    }

    public String getNombre() {
        return nombre;
    }
}
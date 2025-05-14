package juego;

import java.awt.*;
import java.util.Random;

public class Pelota {
    private int x, y;
    private int tamaño = 12;
    private int velocidadX = 4, velocidadY = 4;

    public Pelota(int x, int y) {
        this.x = x;
        this.y = y;
        direccionAleatoria();
    }

    public void actualizar(Paleta j1, Paleta j2) {
        x += velocidadX;
        y += velocidadY;

        // Rebote contra los bordes superior/inferior
        if (y <= 0 || y + tamaño >= 600) {
            velocidadY *= -1;
        }

        // Rebote con las paletas
        if (getRect().intersects(j1.getRect()) || getRect().intersects(j2.getRect())) {
            velocidadX *= -1;
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, tamaño, tamaño);
    }

    public void reiniciar() {
        x = 400 - tamaño / 2;
        y = 300 - tamaño / 2;
        direccionAleatoria();
    }

    private void direccionAleatoria() {
        Random r = new Random();
        velocidadX = r.nextBoolean() ? 4 : -4;
        velocidadY = r.nextBoolean() ? 4 : -4;
    }

    public int getX() {
        return x;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, tamaño, tamaño);
    }
}

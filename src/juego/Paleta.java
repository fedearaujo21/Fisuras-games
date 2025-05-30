package juego;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Paleta {
    private int x, y;
    private int ancho = 10, alto = 80;
    private int velocidad = 5;
    private boolean subiendo = false;
    private boolean bajando = false;

    private int teclaArriba;
    private int teclaAbajo;

    public Paleta(int x, int y, int teclaArriba, int teclaAbajo) {
        this.x = x;
        this.y = y;
        this.teclaArriba = teclaArriba;
        this.teclaAbajo = teclaAbajo;
    }

    public void actualizar() {
        if (subiendo && y > 0) {
            y -= velocidad;
        } else if (bajando && y + alto + Config.frameSuperior < 600) {
            y += velocidad;
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, ancho, alto);
    }

    public void teclaPresionada(KeyEvent e) {
        if (e.getKeyCode() == teclaArriba) {
            subiendo = true;
        } else if (e.getKeyCode() == teclaAbajo) {
            bajando = true;
        }
    }

    public void teclaSoltada(KeyEvent e) {
        if (e.getKeyCode() == teclaArriba) {
            subiendo = false;
        } else if (e.getKeyCode() == teclaAbajo) {
            bajando = false;
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, ancho, alto);
    }
    public boolean getSubiendo(){return subiendo;}
    public boolean getBajando(){return bajando;}
}

package pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Paleta {
    private int x, y;
    private int ancho = 10, alto = 80;
    private int velocidad = 5;
    private boolean subiendo = false;
    private boolean bajando = false;
    private boolean paletaDerecha;

    private int teclaArriba;
    private int teclaAbajo;

    public Paleta(int x, int y, int teclaArriba, int teclaAbajo, int multiplicador, boolean derecha) {
        this.x = x;
        this.y = y;
        this.teclaArriba = teclaArriba;
        this.teclaAbajo = teclaAbajo;
        this.velocidad *= (multiplicador + 1)/2;
        this.paletaDerecha = derecha;

        Assets.init(); // carga las imagenes necesarias para las paletas
    }

    public void actualizar() {
        if (subiendo && y > 0) {
            y -= velocidad;
        } else if (bajando && y + alto + Config.frameSuperior < 600) {
            y += velocidad;
        }
    }

    public void dibujar(Graphics g) {
        if ("original" == Config.skin){
            g.setColor(Color.WHITE);
            g.fillRect(x, y, ancho, alto);
        }else if ("tropical" == Config.skin) {
            Graphics2D g2d = (Graphics2D) g;

            if (paletaDerecha)
                g2d.drawImage(Assets.bananaDerecha, x, y, ancho + 20, alto, null);
            else
                g2d.drawImage(Assets.bananaIzquierda, x - 20, y, ancho + 20, alto, null);
        }else if ("techno" == Config.skin){
            Graphics2D g2d = (Graphics2D) g;

            if (paletaDerecha)
                g2d.drawImage(Assets.bladeDerecha, x - 10, y, ancho + 20, alto, null);
            else
                g2d.drawImage(Assets.bladeIzquierda, x -10, y, ancho + 20, alto, null);
        }
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

    // movimiento de cpu
    public void setCpuSubiendo(boolean input){this.subiendo = input;};
    public void setCpuBajando(boolean input){this.bajando = input;};
    public int getY(){return this.y;}
}

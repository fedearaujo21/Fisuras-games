package lemmings.modelo;

import java.awt.*;

public class Lemming {
    private int x, y;
    private int direccion = 1;
    private Mapa mapa;
    private int lemmingWidth = 6;
    private int lemmingHeight = 9;
    private long tiempoCreacion;
    private static final int MAX_STEP_HEIGHT = 9;

    public Lemming(int x, int y, Mapa mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
        this.tiempoCreacion = System.currentTimeMillis();
    }

    public void caminar(){
        long ahora = System.currentTimeMillis();
        boolean colisionTemporalmenteDesactivada = (ahora - tiempoCreacion) < 500;

        // Verificamos si hay suelo debajo
        boolean haySueloDebajo = false;
        for (int i = 0; i < lemmingWidth; i++) {
            if (!colisionTemporalmenteDesactivada && mapa.hayColision(x + i, y + lemmingHeight)) {
                haySueloDebajo = true;
                break;
            }
        }

        if (!haySueloDebajo) {
            y += 1;
            return;
        }

        // Movimiento horizontal o subir pendiente
        int siguienteX = x + direccion;
        boolean puedeAvanzar = false;
        int y_ajustado = y;

        for (int step = 0; step <= MAX_STEP_HEIGHT; step++) {
            boolean colisionHorizontal = false;
            for (int i = 0; i < lemmingHeight; i++) {
                if (!colisionTemporalmenteDesactivada && (
                        mapa.hayColision(siguienteX, y - step + i) ||
                                mapa.hayColision(siguienteX + lemmingWidth - 1, y - step + i))) {
                    colisionHorizontal = true;
                    break;
                }
            }

            boolean hayTerrenoDebajoEnSiguientePos = false;
            for (int i = 0; i < lemmingWidth; i++) {
                if (!colisionTemporalmenteDesactivada &&
                        mapa.hayColision(siguienteX + i, y - step + lemmingHeight)) {
                    hayTerrenoDebajoEnSiguientePos = true;
                    break;
                }
            }

            if (!colisionHorizontal && hayTerrenoDebajoEnSiguientePos) {
                puedeAvanzar = true;
                y_ajustado = y - step;
                break;
            }
        }

        if (puedeAvanzar) {
            x = siguienteX;
            y = y_ajustado;
        } else {
            direccion *= -1;
        }

        // Asegurarse de que no flote
        while (!(!colisionTemporalmenteDesactivada && mapa.hayColision(x + lemmingWidth / 2, y + lemmingHeight))) {
            y += 1;
            if (y >= mapa.getAlto()) break;
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, lemmingWidth, lemmingHeight);
    }
}

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

    private Habilidad habilidadActiva;
    private int ticksHabilidad = 0;
    private static final int MAX_FALL_ADJUST = 5;
    private static final int TICKS_POR_ACCION_HABILIDAD = 5;

    public Lemming(int x, int y, Mapa mapa) {
        this.x = x;
        this.y = y;
        this.mapa = mapa;
        this.tiempoCreacion = System.currentTimeMillis();
    }

    public void caminar(){
        long ahora = System.currentTimeMillis();
        boolean colisionTemporalmenteDesactivada = (ahora - tiempoCreacion) < 500;

        if (habilidadActiva != null) {
            ticksHabilidad++;
            if (ticksHabilidad % TICKS_POR_ACCION_HABILIDAD == 0) {
                if (habilidadActiva instanceof HabilidadMinero) {
                    ((HabilidadMinero) habilidadActiva).excavar(this);
                }
            }
        }

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
        if (habilidadActiva == null || !(habilidadActiva instanceof HabilidadMinero)) {
            int siguienteX = x + direccion;
            int nuevaY = y;
            boolean puedeAvanzar = false;

            for (int step = 0; step <= MAX_STEP_HEIGHT; step++) {
                int yCandidatoCabeza = y - step;
                int yCandidatoPies = y - step + lemmingHeight;

                boolean colisionHorizontalEnCandidato = false;
                // Revisa toda la altura del Lemming para colisión horizontal
                int checkXEdge = siguienteX + (direccion == 1 ? (lemmingWidth - 1) : 0);
                for (int i = 0; i < lemmingHeight; i++) {
                    if (checkXEdge >= 0 && checkXEdge < mapa.getAncho() && yCandidatoCabeza + i >= 0 && yCandidatoCabeza + i < mapa.getAlto()) {
                        if (mapa.hayColision(checkXEdge, yCandidatoCabeza + i)) {
                            colisionHorizontalEnCandidato = true;
                            break;
                        }
                    } else { // Si está fuera del mapa, es como una pared
                        colisionHorizontalEnCandidato = true;
                        break;
                    }
                }

                boolean hayTerrenoDebajoEnSiguientePos = false;
                for (int i = 0; i < lemmingWidth; i++) {
                    int checkX = siguienteX + i;
                    if (checkX >= 0 && checkX < mapa.getAncho() && yCandidatoPies >= 0 && yCandidatoPies < mapa.getAlto()) {
                        if (mapa.hayColision(checkX, yCandidatoPies)) {
                            hayTerrenoDebajoEnSiguientePos = true;
                            break;
                        }
                    }
                }

                if (!colisionHorizontalEnCandidato && hayTerrenoDebajoEnSiguientePos) {
                    puedeAvanzar = true;
                    nuevaY = yCandidatoCabeza;
                    break;
                }
            }

            if (puedeAvanzar) {
                x = siguienteX;
                y = nuevaY;
            } else {
                direccion *= -1;
            }
        }

        // Asegurarse de que no flote
        for (int fallStep = 0; fallStep < MAX_FALL_ADJUST; fallStep++) {
            boolean sigueCayendo = false;
            for (int i = 0; i < lemmingWidth; i++) {
                int checkX = x + i;
                int checkY = y + lemmingHeight;

                if (checkX >= 0 && checkX < mapa.getAncho() && checkY >= 0 && checkY < mapa.getAlto()) {
                    if (!mapa.hayColision(checkX, checkY)) {
                        sigueCayendo = true;
                        break;
                    }
                } else {
                    sigueCayendo = true;
                    break;
                }
            }

            if (sigueCayendo) {
                y += 1;
            } else {
                break;
            }

            if (y + lemmingHeight >= mapa.getAlto()) {
                break;
            }
        }
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, lemmingWidth, lemmingHeight);
        if (habilidadActiva != null) {
            if (habilidadActiva instanceof HabilidadMinero) {
                g.setColor(Color.RED);
                g.fillOval(x + lemmingWidth / 4, y + lemmingHeight / 4, lemmingWidth / 2, lemmingHeight / 2);
            }
        }
    }

    public Habilidad getHabilidadActiva() { return habilidadActiva; }
    public void setHabilidadActiva(Habilidad habilidadActiva) {
        this.habilidadActiva = habilidadActiva;
        this.ticksHabilidad = 0;
    }
    public void desactivarHabilidad() {
        this.habilidadActiva = null;
        this.ticksHabilidad = 0;
    }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getDireccion() { return direccion; }
    public int getLemmingWidth() { return lemmingWidth; }
    public int getLemmingHeight() { return lemmingHeight; }
}

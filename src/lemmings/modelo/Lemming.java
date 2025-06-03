package lemmings.modelo;

import java.awt.*;

public class Lemming {

    private int x, y;
    private int direccion = 1;
    private Mapa mapa;
    private int lemmingWidth = 10;
    private int lemmingHeight = 14;
    private long tiempoCreacion;
    private boolean fueUsado = false;
    private static final int MAX_STEP_HEIGHT = 12;

    private Habilidad habilidadActiva;
    private int ticksHabilidad = 0;
    private static final int MAX_FALL_ADJUST = 5;
    private static final int TICKS_POR_ACCION_HABILIDAD = 5;

    private static final int TICKS_POR_MOVIMIENTO_NORMAL = 2;
    private int ticksMovimientoNormal = 0;

    private int ticksEnAire = 0; // Contador de ticks que el Lemming lleva en el aire
    private static final int UMBRAL_CAIDA_MINERO = 10;

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
            if (x + i >= 0 && x + i < mapa.getAncho() && y + lemmingHeight >= 0 && y + lemmingHeight < mapa.getAlto()) {
                if (!colisionTemporalmenteDesactivada && mapa.hayColision(x + i, y + lemmingHeight)) {
                    haySueloDebajo = true;
                    break;
                }
            } else { // Si está fuera del mapa por abajo, es como si cayera al infinito
                haySueloDebajo = false; // Asumimos que cae si llega al borde inferior del mapa
                break;
            }
        }

        if (!haySueloDebajo) {
            y += 1;
            // Si el Lemming está en el aire (cayendo), incrementamos el contador
            ticksEnAire++;
            // Si la habilidad activa es Minero y ha caído más allá del umbral, la desactiva
            if (habilidadActiva instanceof HabilidadMinero && ticksEnAire > UMBRAL_CAIDA_MINERO) {
                desactivarHabilidad(); // El minero deja de excavar cuando cae una distancia
            }
            return; // Si está cayendo, no se mueve horizontalmente ni activa habilidades (ya hizo su acción vertical)
        } else {
            // Si hay suelo, el Lemming no está cayendo, reinicia el contador de ticks en el aire.
            ticksEnAire = 0;
        }

        if (habilidadActiva != null) {
            ticksHabilidad++;
            if (ticksHabilidad >= TICKS_POR_ACCION_HABILIDAD) {

                if (habilidadActiva instanceof HabilidadMinero) {
                    ((HabilidadMinero) habilidadActiva).excavar(this);
                }
                // Añadir más habilidades aquí (ej. HabilidadParacaidas, HabilidadEscalador, etc.)

                ticksHabilidad = 0;
            }
            // Después de ejecutar una habilidad que controla el movimiento (como minero),
            // el Lemming no debería ejecutar el movimiento horizontal normal.
            // PERO si la habilidad no controla el movimiento (ej. paracaídas),
            // el Lemming seguiría con su movimiento normal.
            // Para el minero, retornamos para que no intente mover horizontalmente.
            if (habilidadActiva instanceof HabilidadMinero) {
                return;
            }
        }

        // Movimiento horizontal o subir pendiente
        if (habilidadActiva == null) {

            ticksMovimientoNormal++;
            if (ticksMovimientoNormal < TICKS_POR_MOVIMIENTO_NORMAL) {
                return; // No se mueve horizontalmente aún, espera más ticks
            }
            ticksMovimientoNormal = 0;

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
        if (habilidadActiva instanceof HabilidadMinero) {
            g.setColor(Color.RED); // El Lemming se vuelve rojo
        } else {
            g.setColor(Color.GREEN); // Color normal del Lemming
        }
        g.fillRect(x, y, lemmingWidth, lemmingHeight);

    }

    public Habilidad getHabilidadActiva() { return habilidadActiva; }
    public void setHabilidadActiva(Habilidad habilidadActiva) {
        if(fueUsado == false) {
            this.habilidadActiva = habilidadActiva;
            this.ticksHabilidad = 0;
        }
    }
    public void desactivarHabilidad() {
        this.habilidadActiva = null;
        this.ticksHabilidad = 0;
    }
    public void setFueUsado(boolean fueUsado){
        this.fueUsado = fueUsado;
    };

    public boolean getFueUsado(){
        return this.fueUsado;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getDireccion() { return direccion; }
    public int getLemmingWidth() { return lemmingWidth; }
    public int getLemmingHeight() { return lemmingHeight; }
}

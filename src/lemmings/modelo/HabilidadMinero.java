package lemmings.modelo;

public class HabilidadMinero extends Habilidad {

    private static final int ANCHO_EXCAVACION = 16;
    private static final int ALTO_EXCAVACION = 2; // Profundidad de excavación por tick

    private Mapa mapa;

    public HabilidadMinero(Mapa mapa) {
        super("Minero", 1, -1);
        this.mapa = mapa;
    }

    @Override
    public boolean activar(Lemming lemming) {
        if (lemming.getHabilidadActiva() != null) {
            System.out.println("El Lemming ya tiene una habilidad activa.");
            return false;
        }

        boolean hayTerrenoDebajo = false;
        for (int i = 0; i < lemming.getLemmingWidth(); i++) {
            // Asegurarse de que hay colisión para activar la habilidad
            if (lemming.getX() + i >= 0 && lemming.getX() + i < mapa.getAncho() &&
                    lemming.getY() + lemming.getLemmingHeight() >= 0 && lemming.getY() + lemming.getLemmingHeight() < mapa.getAlto()) {
                if (mapa.hayColision(lemming.getX() + i, lemming.getY() + lemming.getLemmingHeight())) {
                    hayTerrenoDebajo = true;
                    break;
                }
            }
        }
        if (!hayTerrenoDebajo) {
            System.out.println("El Lemming no tiene terreno debajo para minar.");
            return false;
        }

        lemming.setHabilidadActiva(this);
        System.out.println("Habilidad Minero activada en el Lemming (excava hacia abajo).");
        return true;
    }

    public void excavar(Lemming lemming) {
        int xInicioBorrado = lemming.getX();
        int yInicioBorrado = lemming.getY() + lemming.getLemmingHeight();

        for (int dx = 0; dx < ANCHO_EXCAVACION; dx++) {
            for (int dy = 0; dy < ALTO_EXCAVACION; dy++) {
                int pixelX = xInicioBorrado + dx;
                int pixelY = yInicioBorrado + dy;

                if (pixelX >= 0 && pixelX < mapa.getAncho() && pixelY >= 0 && pixelY < mapa.getAlto()) {
                    mapa.eliminarPix(pixelX, pixelY);
                }
            }
        }

        // --- Movimiento del Lemming ---
        // El Lemming se mueve hacia abajo al ritmo de la excavación
        lemming.setY(lemming.getY() + 1); // Baja 1 píxel por cada "tick" de excavación

        // --- Condición para finalizar la habilidad ---
        // Miramos 'ALTO_EXCAVACION' píxeles por debajo de la nueva posición del Lemming
        // para ver si aún hay terreno por excavar.
        boolean puedeSeguirCavando = false;
        // checkY debe ser la fila más baja del bloque que se excavará en el *siguiente* ciclo
        int checkYParaSiguientePaso = lemming.getY() + lemming.getLemmingHeight() + ALTO_EXCAVACION -1;

        for (int px = 0; px < lemming.getLemmingWidth(); px++) {
            int checkX = lemming.getX() + px; // Revisa en el ancho del lemming
            if (checkX >= 0 && checkX < mapa.getAncho() && checkYParaSiguientePaso >= 0 && checkYParaSiguientePaso < mapa.getAlto()) {
                if (mapa.hayColision(checkX, checkYParaSiguientePaso)) {
                    puedeSeguirCavando = true;
                    break;
                }
            }
            // Si la posición de comprobación ya está fuera del mapa por abajo, no hay nada más que cavar
            if (checkYParaSiguientePaso >= mapa.getAlto()) {
                puedeSeguirCavando = false;
                break;
            }
        }

        if (!puedeSeguirCavando) {
            lemming.desactivarHabilidad();
            System.out.println("Minero: Habilidad desactivada (no hay más terreno o llegó al fondo).");
        }
    }
}
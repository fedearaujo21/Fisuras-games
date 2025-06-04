package lemmings.modelo;

public class HabilidadRomper extends Habilidad{

    private Mapa mapa;
    private static final int ANCHO_EXCAVACION = 16;
    private static final int ALTO_EXCAVACION = 5; // Profundidad de excavación por tick

    public HabilidadRomper(Mapa mapa) {
        super("Romper", 5, -1);
        this.mapa = mapa;
    }

    public boolean activar(Lemming lemming){
        if (lemming.getHabilidadActiva() != null) {
            System.out.println("El Lemming ya tiene una habilidad activa.");
            return false;
        }

        boolean hayTerrenoDebajo = false;
        for (int i = 0; i < lemming.getLemmingWidth(); i++) {
            // Asegurarse que el lemming tiene donde caminar para seguir rompiendo
            if (lemming.getX() + i >= 0 && lemming.getX() + i < mapa.getAncho() &&
                    lemming.getY() + lemming.getLemmingHeight() >= 0 && lemming.getY() + lemming.getLemmingHeight() < mapa.getAlto()) {
                if (mapa.hayColision(lemming.getX() + i, lemming.getY() + lemming.getLemmingHeight())) {
                    hayTerrenoDebajo = true;
                    break;
                }
            }
        }

        if (!hayTerrenoDebajo) {
            System.out.println("El Lemming no tiene donde caminar.");
            return false;
        }

        lemming.setHabilidadActiva(this);
        System.out.println("Habilidad Romper activada");
        return true;
    }

    public void romper(Lemming lemming) {
        int xInicioBorrado = lemming.getX() + (lemming.getDireccion() * lemming.getLemmingWidth()); // empieza al borde del lemming
        int yInicioBorrado = lemming.getY();

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
        lemming.setX(lemming.getX() + 1); // Avanza 1 píxel a la derecha por cada tick

        // Verificación de si puede seguir Rompiendo
        boolean puedeSeguirRompiendo = false;
        int checkX = lemming.getX() + (lemming.getDireccion() * lemming.getLemmingWidth()) + ANCHO_EXCAVACION - 1;

        for (int py = 0; py < lemming.getLemmingHeight(); py++) {
            int checkY = lemming.getY() + py;
            if (checkX >= 0 && checkX < mapa.getAncho() && checkY >= 0 && checkY < mapa.getAlto()) {
                if (mapa.hayColision(checkX, checkY)) {
                    puedeSeguirRompiendo = true;
                    break;
                }
            }
            if (checkX >= mapa.getAncho()) {
                puedeSeguirRompiendo = false;
                break;
            }
        }

        if (!puedeSeguirRompiendo) {
            lemming.desactivarHabilidad();
            System.out.println("Romper: Habilidad desactivada (no hay más terreno o llegó al borde).");
        }
    }
}

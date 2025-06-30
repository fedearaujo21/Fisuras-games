package lemmings.modelo;

public class HabilidadConstructor extends Habilidad {

    private Mapa mapa; // Necesitamos el mapa para modificar el terreno
    private static final int MAX_BLOQUES = 20; // Número de bloques que puede construir (ajusta según la longitud deseada de la rampa)
    private static final int ANCHO_PASO = 6;  // Más ancho para escalón sólido
    private static final int ALTURA_PASO = 2; // No muy alto para que puedan subir  // bloques verticales por paso // Ancho de cada escalón (en píxeles, generalmente 1)

    public HabilidadConstructor(Mapa mapa) {
        super("Constructor", 40, MAX_BLOQUES); // Coste 1, usos MAX_BLOQUES
        this.mapa = mapa;
    }

    @Override
    public boolean activar(Lemming lemming) {
        if (lemming.getEstado() != Lemming.EstadoLemming.CAMINANDO) {
            System.out.println("El Lemming debe estar caminando para activar la habilidad Constructor.");
            return false;
        }
        if (lemming.getHabilidadActiva() != null) {
            System.out.println("El Lemming ya tiene una habilidad activa, no puede ser Escalador.");
            return false;
        }
        lemming.setHabilidadActiva(this);
        lemming.resetBloquesConstruidos(); // Reinicia el contador de bloques construidos del lemming
        System.out.println("Habilidad 'Constructor' activada en Lemming.");
        return true;
    }

    public void construir(Lemming lemming) {
        int bloques = lemming.getBloquesConstruidos();
        int dir = lemming.getDireccion();

        // Coordenadas base del bloque a construir
        int xInicio = lemming.getX() + dir * lemming.getLemmingWidth();
        int yInicio = lemming.getY() + lemming.getLemmingHeight() - 1;

        // Construir un bloque sólido: rectángulo de ANCHO_PASO x ALTURA_PASO
        for (int dx = 0; dx < ANCHO_PASO; dx++) {
            for (int dy = 0; dy < ALTURA_PASO; dy++) {
                int x = xInicio + dir * dx;
                int y = yInicio - dy;

                if (x >= 0 && x < mapa.getAncho() && y >= 0 && y < mapa.getAlto()) {
                    if (!mapa.hayColision(x, y)) {
                        mapa.agregarPix(x, y);
                    }
                } else {
                    lemming.desactivarHabilidad();
                    lemming.setEstado(Lemming.EstadoLemming.CAMINANDO);
                    return;
                }
            }
        }

        // Mover lemming al siguiente escalón
        lemming.setX(lemming.getX() + dir * ANCHO_PASO);
        lemming.setY(lemming.getY() - ALTURA_PASO);

        // Actualizar estado
        lemming.aumentarBloquesConstruidos();
        this.decrementarUso();

        if (lemming.getBloquesConstruidos() >= MAX_BLOQUES || getUsosRestantes() <= 0) {
            lemming.desactivarHabilidad();
            lemming.setEstado(Lemming.EstadoLemming.CAMINANDO);
        }
    }

}

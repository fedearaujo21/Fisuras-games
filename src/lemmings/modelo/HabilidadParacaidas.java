package lemmings.modelo;

public class HabilidadParacaidas extends Habilidad {

    public HabilidadParacaidas() {
        super("Paracaidas", 0, 0);
    }

    @Override
    public boolean activar(Lemming lemming) {
        lemming.setHabilidadActiva(this);

        if (lemming.getEstado() == Lemming.EstadoLemming.CAYENDO) {
            aplicarSiCayendo(lemming, lemming.getTicksEnAire());
        }

        return true;
    }

    public void aplicar(Lemming lemming) {
        // No se usa directamente
    }

    public void aplicarSiCayendo(Lemming lemming, int ticksEnAire) {
        if (lemming.getEstado() == Lemming.EstadoLemming.CAYENDO) {
            if (ticksEnAire % 2 == 0) {
                lemming.incrementarY(); // caída más lenta
            }
            lemming.setFrameParacaidas();
        } else {
            // Ya no está cayendo: desactivar
            lemming.desactivarHabilidad();
        }
    }
}

package lemmings.modelo;

public class HabilidadBloqueador extends Habilidad{

    public HabilidadBloqueador() {
        super("Bloqueador", 1, -1);
    }


    @Override
    public boolean activar(Lemming lemming) {
        if (lemming.getHabilidadActiva() != null) {
            //lemming.setEstado(Lemming.EstadoLemming.BLOQUEANDO);
            System.out.println("El Lemming ya tiene una habilidad activa, no puede ser Bloqueador.");
            return false;
        }

        if(lemming.getEstado() != Lemming.EstadoLemming.CAMINANDO){
            System.out.println("El lemming debe estar caminando");
            return false;
        }

        lemming.setHabilidadActiva(this);
        System.out.println("Habilidad 'Bloqueador' activada en Lemming.");
        return true;
    }

}

package lemmings.modelo;

public class HabilidadBloqueador extends Habilidad{

    public HabilidadBloqueador() {
        super("Bloqueador", 1, -1);
    }


    @Override
    public boolean activar(Lemming lemming) {
        if(lemming.getEstado() != Lemming.EstadoLemming.CAMINANDO){
            System.out.println("El lemming debe estar caminando");
            return false;
        }

        lemming.setHabilidadActiva(this);
        System.out.println("Habilidad 'Bloqueador' activada en Lemming.");
        return true;
    }

}

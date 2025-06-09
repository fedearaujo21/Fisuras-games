package lemmings.modelo;

import lemmings.control.AudioPlayer;

public class HabilidadBloqueador extends Habilidad{
    private AudioPlayer musicaBloqueador;

    public HabilidadBloqueador() {
        super("Bloqueador", 1, -1);
    }

    @Override
    public boolean activar(Lemming lemming) {
        if(lemming.getEstado() != Lemming.EstadoLemming.CAMINANDO){
            System.out.println("El lemming debe estar caminando");
            return false;
        }
        musicaBloqueador = new AudioPlayer("/lemmings/recursos/SonidoBloqueador.wav");
        musicaBloqueador.play();
        lemming.setHabilidadActiva(this);
        System.out.println("Habilidad 'Bloqueador' activada en Lemming.");
        return true;
    }

}

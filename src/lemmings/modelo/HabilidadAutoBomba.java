package lemmings.modelo;

import lemmings.control.AudioPlayer;

public class HabilidadAutoBomba extends Habilidad {
    private Mapa mapa;
    private AudioPlayer musicaAutoBomba = new AudioPlayer("/lemmings/recursos/SonidoAutoBomba.wav");
    public HabilidadAutoBomba(Mapa mapa) {
        super("AutoBomba", 1, -1); // Nombre corregido
        this.mapa = mapa;
    }

    @Override
    public boolean activar(Lemming lemming) {
        if (lemming.getEstado() != Lemming.EstadoLemming.CAMINANDO && lemming.getEstado() != Lemming.EstadoLemming.BLOQUEANDO){
            System.out.println("El lemming debe estar caminando o bloqueando para usar AutoBomba.");
            return false;
        }
        lemming.setHabilidadActiva(this);
        musicaAutoBomba.play();
        System.out.println("Habilidad 'AutoBomba' activada en Lemming.");
        return true;
    }

    public void explotar(int centroX, int centroY, int radio) {
        for (int y = -radio; y <= radio; y++) {
            for (int x = -radio; x <= radio; x++) {
                if (x * x + y * y <= radio * radio) { // círculo
                    int posX = centroX + x;
                    int posY = centroY + y + 15;
                    if (posX >= 0 && posX < mapa.getAncho() && posY >= 0 && posY < mapa.getAlto()) {
                        mapa.eliminarPix(posX, posY); // elimina el píxel y actualiza la colisión
                    }
                }
            }
        }
    }
}

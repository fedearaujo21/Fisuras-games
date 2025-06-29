package lemmings.modelo;

import java.util.ArrayList;
import java.util.List;

public class GestorNiveles {

    public static List<NivelInfo> getTodosLosNiveles() {
        List<NivelInfo> niveles = new ArrayList<>();

        niveles.add(new NivelInfo(
                1,
                "Nivel 1",
                "/lemmings/recursos/Nivel1.png",
                "/lemmings/recursos/MusicaNivel1.wav",
                "/lemmings/recursos/Nivel1View.png"  // ← así
        ));

        niveles.add(new NivelInfo(
                2,
                "Nivel 2",
                "/lemmings/recursos/Nivel2.png",
                "/lemmings/recursos/MusicaNivel2.wav",
                "/lemmings/recursos/Nivel2View.png"
        ));


        niveles.add(new NivelInfo(
                3,
                "Nivel 3",
                "/lemmings/recursos/Nivel3.png",
                "/lemmings/recursos/MusicaNivel3.wav",
                "/lemmings/recursos/Nivel3View.png"
        ));

        niveles.add(new NivelInfo(
                4,
                "Nivel 4",
                "/lemmings/recursos/Nivel4.png",
                "/lemmings/recursos/MusicaNivel4.wav",
                "/lemmings/recursos/Nivel4View.png"
        ));



        return niveles;
    }

    public static NivelInfo getNivelInfoPorNumero(int numero) {
        return getTodosLosNiveles().stream()
                .filter(n -> n.getNumero() == numero)
                .findFirst()
                .orElse(null);
    }
}

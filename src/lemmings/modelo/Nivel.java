package lemmings.modelo;

import java.awt.Graphics; //
import java.awt.image.BufferedImage; //
import java.util.ArrayList; //
import java.util.List; //

public class Nivel {
    private int nivelNum;
    private int tiempo;
    private String nombre;
    private int cantidadLem;
    private Mapa mapa;
    private List<Lemming> lemmings;
    private Entrada entrada;

    // El color de fondo del vacío para este nivel
    private int colorFondoNivel; // Nuevo campo para el color de fondo específico del nivel

    // El constructor de Nivel ahora puede recibir el color de fondo,
    // o puedes definirlo internamente basado en 'nivelNum'
    public Nivel (int nivelNum, String nombre, BufferedImage mapaImagen){ //
        this.nivelNum = nivelNum; //
        this.nombre = nombre; //
        this.tiempo = 60; //
        this.cantidadLem = 2; //

        // **AQUÍ SE DEFINE EL COLOR DE FONDO PARA ESTE NIVEL**
        switch (nivelNum) { //
            case 1:
                this.colorFondoNivel = 0xFF000000; // El color #010001 para el Nivel 1
                break;
            // case 2:
            //    this.colorFondoNivel = 0xFFABCDEF;
            //    break;
            default:
                this.colorFondoNivel = 0xFF000000;
        }

        // Se pasa el color de fondo al constructor del Mapa
        this.mapa = new Mapa(mapaImagen, this.colorFondoNivel); //
        this.lemmings = new ArrayList<>(); //
        this.entrada = new Entrada(420,40); //

        for(int i=0; i<cantidadLem;i++){
            lemmings.add(entrada.spawnear(this.mapa));
        } //
    }

    public void actualizar(){ //
        for(Lemming l: lemmings){ //
            l.caminar(); //
        }
    }

    public void dibujar(Graphics g){ //
        mapa.dibujar(g); //
        for (Lemming l : lemmings) { //
            l.dibujar(g); //
        }
    }

    public void reiniciar() { //
        // Implementar reinicio más adelante
    }

    public void pausar() { //
        // Implementar pausa más adelante
    }

    public void completarNVL() { //
        // Lógica cuando se cumple el objetivo
    }

    // Puedes añadir getters si necesitas acceder a la información del nivel desde fuera
    public Mapa getMapa() {
        return mapa;
    }
}

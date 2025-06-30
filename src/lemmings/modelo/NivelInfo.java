package lemmings.modelo;

import java.util.HashMap;
import java.util.Map;

public class NivelInfo {
    private int numero;
    private String nombre;
    private String rutaImagen;
    private String rutaMusica;
    private String rutaVista;

    private Entrada entrada;
    private Salida salida;
    private Stock stockHabilidades;
    private long tiempo;
    private int frecuenciaSpawn;
    private int cantidadLem;
    private int objetivoLemmings;

    public NivelInfo(int numero, String nombre, String rutaImagen, String rutaMusica, String rutaVista,
                     Entrada entrada, Salida salida, Stock stockHabilidades,
                     long tiempo, int frecuenciaSpawn, int cantidadLem, int objetivoLemmings) {
        this.numero = numero;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.rutaMusica = rutaMusica;
        this.rutaVista = rutaVista;
        this.entrada = entrada;
        this.salida = salida;
        this.stockHabilidades = stockHabilidades;
        this.tiempo = tiempo;
        this.frecuenciaSpawn = frecuenciaSpawn;
        this.cantidadLem = cantidadLem;
        this.objetivoLemmings = objetivoLemmings;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public String getRutaMusica() {
        return rutaMusica;
    }

    public String getRutaVista() {
        return rutaVista;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public Salida getSalida() {
        return salida;
    }

    public Stock getStock() {
        return stockHabilidades;
    }

    public long getTiempo() {
        return tiempo;
    }

    public int getFrecuenciaSpawn() {
        return frecuenciaSpawn;
    }

    public int getCantidadLem() {
        return cantidadLem;
    }

    public int getObjetivoLemmings() {
        return objetivoLemmings;
    }
}

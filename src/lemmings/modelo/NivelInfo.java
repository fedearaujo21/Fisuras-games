package lemmings.modelo;

public class NivelInfo {
    private int numero;
    private String nombre;
    private String rutaImagen;
    private String rutaMusica;
    private String rutaVista;

    public NivelInfo(int numero, String nombre, String rutaImagen, String rutaMusica, String rutaVista) {
        this.numero = numero;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.rutaMusica = rutaMusica;
        this.rutaVista = rutaVista;
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

}

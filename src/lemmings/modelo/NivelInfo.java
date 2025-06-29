package lemmings.modelo;

public class NivelInfo {
    private int numero;
    private String nombre;
    private String rutaImagen;
    private String rutaMusica;
    private String rutaVistaPrevia;

    public NivelInfo(int numero, String nombre, String rutaImagen, String rutaMusica, String rutaVistaPrevia) {
        this.numero = numero;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.rutaMusica = rutaMusica;
        this.rutaVistaPrevia = rutaVistaPrevia;
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

    public String getRutaVistaPrevia() {
        return rutaVistaPrevia;
    }
}

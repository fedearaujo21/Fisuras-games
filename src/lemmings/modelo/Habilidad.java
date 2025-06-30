package lemmings.modelo;

public abstract class Habilidad {
    protected int tiempoRestante;
    protected int usosRestantes;
    protected String nombre;

    public Habilidad(String nombre, int usosIniciales, int tiempoInicial) {
        this.nombre = nombre;
        this.usosRestantes = usosIniciales;
        this.tiempoRestante = tiempoInicial;
    }

    public abstract boolean activar(Lemming lemming);

    protected void decrementarUso() {
        if (usosRestantes > 0) {
            usosRestantes--;
        }
    }

    public String getNombre() { return nombre; }
    public int getUsosRestantes() { return usosRestantes; }
    public int getTiempoRestante() { return tiempoRestante; }
    public boolean tieneUsos() { return usosRestantes > 0; }
    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; }
    public void setUsosRestantes(int usosRestantes) { this.usosRestantes = usosRestantes; }
}
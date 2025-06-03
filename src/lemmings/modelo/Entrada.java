package lemmings.modelo;

public class Entrada {
    private int x, y;

    public Entrada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Lemming spawnear(Mapa mapa) {
        return new Lemming(x, y, mapa);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

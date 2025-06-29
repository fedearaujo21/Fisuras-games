package lemmings.control;

public class Escalador {
    public static double escalaX = 1.0;
    public static double escalaY = 1.0;

    public static int escalarX(int x) {
        return (int)(x * escalaX);
    }

    public static int escalarY(int y) {
        return (int)(y * escalaY);
    }

    public static int desescalarX(int xReal) {
        return (int)(xReal / escalaX);
    }

    public static int desescalarY(int yReal) {
        return (int)(yReal / escalaY);
    }

    public static void inicializarEscalas(int anchoReal, int altoReal) {
        escalaX = anchoReal / 800.0;
        escalaY = altoReal / 600.0;
    }
}
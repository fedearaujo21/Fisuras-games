package lemmings.modelo;

public class HabilidadMinero extends Habilidad{
    private static final int ANCHO_EXCAVACION = 8;
    private static final int ALTO_EXCAVACION = 8;

    private Mapa mapa;

    public HabilidadMinero(Mapa mapa){
        super("Minero",1,-1);
        this.mapa = mapa;
    }

    @Override
    public boolean activar(Lemming lemming){
        if(lemming.getHabilidadActiva() != null){
            System.out.println("El Lemming ya tiene una habilidad activa.");
            return false;
        }

        lemming.setHabilidadActiva(this);
        System.out.println("Habilidad Minero activada en el Lemming.");
        return true;
    }

    public void excavar(Lemming lemming) {
        int xExcavacionInicial = lemming.getX() + (lemming.getDireccion() == 1 ? lemming.getLemmingWidth() : -ANCHO_EXCAVACION);
        int yExcavacionInicial = lemming.getY();

        for (int dx = 0; dx < ANCHO_EXCAVACION; dx++) {
            for (int dy = 0; dy < ALTO_EXCAVACION; dy++) {
                int pixelX = xExcavacionInicial + (lemming.getDireccion() * dx);
                int pixelY = yExcavacionInicial + dy;

                if (pixelX >= 0 && pixelX < mapa.getAncho() && pixelY >= 0 && pixelY < mapa.getAlto()) {
                    mapa.eliminarPix(pixelX, pixelY);
                }
            }
        }

        lemming.setX(lemming.getX() + lemming.getDireccion());

        // Verificar si hay terreno delante para seguir cavando
        boolean puedeSeguirCavando = false;
        int proximoPixelX = lemming.getX() + (lemming.getDireccion() == 1 ? lemming.getLemmingWidth() : -1);
        int proximoPixelY = lemming.getY() + lemming.getLemmingHeight() / 2;

        if (proximoPixelX >= 0 && proximoPixelX < mapa.getAncho() && proximoPixelY >= 0 && proximoPixelY < mapa.getAlto()) {
            if (mapa.hayColision(proximoPixelX, proximoPixelY)) {
                puedeSeguirCavando = true;
            }
        }
        if (!puedeSeguirCavando) {
            lemming.desactivarHabilidad();
        }
    }
}

package lemmings.modelo;

import lemmings.control.AudioPlayer;

import java.awt.Rectangle;

public class HabilidadKameHameHa extends Habilidad {
    private Mapa mapa;
    private static final int RADIO_EXPLOSION = 30; // Radio del área a destruir
    private static final int DURACION_CARGA_TICKS = 100; // Tiempo para cargar la habilidad
    private static final int DURACION_VIAJE_TICKS = 100; // Tiempo que tarda el rayo en "viajar"
    private static final int DURACION_TOTAL_KAMEHAMEHA_TICKS = DURACION_CARGA_TICKS + DURACION_VIAJE_TICKS;
    private AudioPlayer musicaKameHameHa;

    private int ticksActivo = 0;
    private int puntoImpactoX; // Donde impactará el rayo
    private int puntoImpactoY;
    private boolean impactoSucedido = false; // Bandera para saber si ya impactó

    public HabilidadKameHameHa(Mapa mapa) {
        super("KameHameHa", 1, DURACION_TOTAL_KAMEHAMEHA_TICKS); // Un uso, duración total
        this.mapa = mapa;
    }

    @Override
    public boolean activar(Lemming lemming) {
        if (lemming.getHabilidadActiva() != null) {
            System.out.println("El Lemming ya tiene una habilidad activa.");
            return false;
        }
        lemming.setHabilidadActiva(this);
        lemming.setEstado(Lemming.EstadoLemming.KAMEHAMEHA_CARGANDO);
        musicaKameHameHa = new AudioPlayer("/lemmings/recursos/SonidoKameHameHa.wav");
        musicaKameHameHa.play();
        this.ticksActivo = 0; // Reiniciar el contador de ticks al activar
        this.impactoSucedido = false;
        return true;
    }

    public void aplicarKameHameHa(Lemming lemming) {
        ticksActivo++;
        // Fase de Carga
        if (ticksActivo <= DURACION_CARGA_TICKS) {
            // El lemming está cargando, no hace daño aún.
            // Puedes ajustar la animación del Lemming aquí si tienes frames de carga.
            lemming.setEstado(Lemming.EstadoLemming.KAMEHAMEHA_CARGANDO);
        }
        // Fase de Viaje del Rayo y Impacto
        else if (ticksActivo <= DURACION_TOTAL_KAMEHAMEHA_TICKS) {
            lemming.setEstado(Lemming.EstadoLemming.KAMEHAMEHA_DISPARANDO); // Estado disparando
            if (!impactoSucedido) {
                // Calcular el punto de impacto solo una vez
                calcularPuntoImpacto(lemming);
                impactoSucedido = true;
            }

            // Aquí podrías interpolar el origen del rayo para el dibujo
            // (no rompe píxeles hasta el impacto, solo dibuja el rayo viajando)
        }
        // Fin de la Habilidad (Impacto y desactivación)
        if (ticksActivo == DURACION_TOTAL_KAMEHAMEHA_TICKS) {
            // Realizar el impacto de destrucción
            destruirAreaCircular(puntoImpactoX, puntoImpactoY, RADIO_EXPLOSION);
            lemming.desactivarHabilidad();
            lemming.setEstado(Lemming.EstadoLemming.CAMINANDO); // Vuelve a caminar al finalizar
        }
    }

    private void calcularPuntoImpacto(Lemming lemming) {
        // Encontrar la primera colisión en la dirección del lemming
        int currentX = lemming.getX() + (lemming.getLemmingWidth() / 2); // Centro del lemming
        int currentY = lemming.getY() + (lemming.getLemmingHeight() / 2);

        int deltaX = lemming.getDireccion(); // 1 para derecha, -1 para izquierda
        int maxDist = mapa.getAncho(); // Distancia máxima de búsqueda

        puntoImpactoX = currentX;
        puntoImpactoY = currentY;

        for (int i = 0; i < maxDist; i++) {
            int nextX = currentX + (i * deltaX);

            // Asegurarse de que no nos salimos del mapa
            if (nextX < 0 || nextX >= mapa.getAncho()) {
                break;
            }

            // Verificar colisión en un rango vertical para dar un "grosor" al rayo
            boolean foundCollision = false;
            for (int yOffset = -2; yOffset <= 2; yOffset++) { // Rango vertical para detección de colisión
                int checkY = currentY + yOffset;
                if (checkY >= 0 && checkY < mapa.getAlto() && mapa.hayColision(nextX, checkY)) {
                    puntoImpactoX = nextX;
                    puntoImpactoY = checkY; // El punto de impacto es donde colisiona
                    foundCollision = true;
                    break;
                }
            }
            if (foundCollision) {
                break;
            }
        }
    }


    private void destruirAreaCircular(int centerX, int centerY, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance <= radius) {
                    mapa.eliminarPix(x, y);
                }
            }
        }
    }

    // Métodos para el dibujo del rayo viajando (se usarán en Lemming.dibujar)
    public int getTicksActivo() {
        return ticksActivo;
    }

    public int getPuntoImpactoX() {
        return puntoImpactoX;
    }

    public int getPuntoImpactoY() {
        return puntoImpactoY;
    }

    public int getDuracionCargaTicks() {
        return DURACION_CARGA_TICKS;
    }

    public int getDuracionViajeTicks() {
        return DURACION_VIAJE_TICKS;
    }
}
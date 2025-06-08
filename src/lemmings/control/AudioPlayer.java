package lemmings.control;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {

    private Clip clip; // Para la reproducción de clips de audio (sonidos cortos o música en bucle)
    private FloatControl gainControl; // Para controlar el volumen

    public AudioPlayer(String filePath) {
        try {
            // Cargar el recurso de audio desde el classpath
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) {
                System.err.println("Archivo de audio no encontrado: " + filePath);
                return;
            }

            // Usar BufferedInputStream para que el audio se pueda leer repetidamente si es necesario (para loops)
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Obtener el control de volumen si está disponible
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el archivo de audio: " + filePath + " - " + e.getMessage());
        }
    }

    public void play() {
        if (clip != null) {
            clip.stop(); // Detener si ya está reproduciéndose
            clip.setFramePosition(0); // Reiniciar al principio
            clip.start(); // Iniciar la reproducción
        }
    }

    public void loop() {
        if (clip != null) {
            clip.stop(); // Detener si ya está reproduciéndose
            clip.setFramePosition(0); // Reiniciar al principio
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Reproducir en bucle infinito
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop(); // Detener la reproducción
        }
    }

    public void close() {
        if (clip != null) {
            clip.close(); // Liberar los recursos del clip
        }
    }

    // cambia el volumen de forma porcentual
    public void setVolume(int volumen) {
        if (volumen < 0 || volumen > 100) {
            throw new IllegalArgumentException("El volumen debe estar entre 0 y 100");
        }

        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float min = control.getMinimum(); // volumen más bajo en dB (ej: -80.0)
            float max = control.getMaximum(); // volumen más alto en dB (ej: 6.0)

            if (volumen == 0) {
                control.setValue(min); // silencio total
            } else {
                // Convertimos el volumen de 0–100 a un valor dB
                float gain = (float) (Math.log10(volumen / 100.0) * 20.0);
                control.setValue(Math.max(gain, min));
            }
        } else {
            System.err.println("Algo exploto en el control de volumen");
        }
    }
}
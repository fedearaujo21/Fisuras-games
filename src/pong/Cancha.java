package pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import lemmings.control.DataManager;

public class Cancha extends JPanel {
    private Pelota pelota;
    private Paleta jugador1;
    private Paleta jugador2;

    private int puntajeJ1 = 0;
    private int puntajeJ2 = 0;

    private boolean juegoTerminado = false;
    private String mensajeGanador = "";

    private JButton botonReiniciar;
    private JTextField campoNombre;
    private int puntos;
    private int contador = 0;
    private JLabel[] posicion = new JLabel[10];
    private boolean rankingMostrado = false;

    public Cancha() {
        setBackground(Color.BLACK);
        setLayout(null); // seguimos usando null layout como querías

        // Crear objetos (esto depende de tus clases)
        jugador1 = new Paleta(30, 250, Config.teclaArribaJugador1, Config.teclaAbajoJugador1, 1, false);
        jugador2 = new Paleta(750, 250, Config.teclaArribaJugador2, Config.teclaAbajoJugador2, Config.multiplicadorDificultad, true);
        pelota = new Pelota(390, 290);

        // Botón
        if (Config.singleMode)
            botonReiniciar = new JButton("Ranking");
        else
            botonReiniciar = new JButton("Reiniciar");

        botonReiniciar.setBounds(350, 350, 120, 30);
        botonReiniciar.setVisible(false);
        add(botonReiniciar);

        // Campo de texto
        campoNombre = new JTextField(4);
        campoNombre.setBounds(350, 280, 100, 25);
        campoNombre.setVisible(false);
        add(campoNombre);

        botonReiniciar.addActionListener(e -> {
            if (botonReiniciar.getText().equals("Ranking")) {
                botonReiniciar.setText("Reiniciar");
                //campoNombre.setVisible(true);


                System.out.println("entrando al if");

                String nombre = campoNombre.getText();
                puntos = (puntajeJ1 - puntajeJ2) * 1000 * Config.multiplicadorDificultad;
                DataManager.insert("Pong", nombre, puntos);
                System.out.println("puntos cargados, mostrando");

                campoNombre.setVisible(false);
                // Mostrar ranking
                mensajeGanador = "";
                for (String i : DataManager.getRanking("Pong")) {
                    posicion[contador] = new JLabel((contador + 1) + "    " + i);
                    posicion[contador].setForeground(Color.WHITE);
                    posicion[contador].setFont(new Font("Arial", Font.BOLD, 16));
                    posicion[contador].setBounds(300, 50 + contador * 30, 300, 25);
                    add(posicion[contador]);
                    posicion[contador].setVisible(true);
                    contador++;
                }
                revalidate();
                repaint();
            } else {
                campoNombre.setVisible(false);
                // Reiniciar juego
                if (Config.singleMode)
                    botonReiniciar.setText("Ranking");
                else
                    botonReiniciar.setText("Reiniciar");

                for (JLabel label : posicion) {
                    if (label != null) label.setVisible(false);
                }
                puntajeJ1 = 0;
                puntajeJ2 = 0;
                juegoTerminado = false;
                pelota.reiniciar();
                botonReiniciar.setVisible(false);
                contador = 0;

                rankingMostrado = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Línea del medio
        g.setColor(Color.WHITE);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

        // Puntajes
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString(String.valueOf(puntajeJ1), getWidth() / 2 - 60, 50);
        g.drawString(String.valueOf(puntajeJ2), getWidth() / 2 + 30, 50);

        // Mensaje de fin
        if (juegoTerminado) {
            g.setColor(Color.RED);
            g.drawString(mensajeGanador, getWidth() / 2 - 150, getHeight() / 2);
        }

        // Dibujar tus elementos
        jugador1.dibujar(g);
        jugador2.dibujar(g);
        pelota.dibujar(g);
    }


    public void actualizar() {
        if (juegoTerminado) return;

        jugador1.actualizar();

//cpu
        if(Config.singleMode){
            if(pelota.getY() < jugador2.getY())
                jugador2.setCpuSubiendo(true);
            else{
                if(pelota.getY() > jugador2.getY()) {
                    jugador2.setCpuSubiendo(false);
                    jugador2.setCpuBajando(true);
                }else
                    jugador2.setCpuSubiendo(false);
            }
            jugador2.actualizar();
        }else
            jugador2.actualizar();

        pelota.actualizar(jugador1, jugador2);

        if (pelota.getX() < 0) {
            puntajeJ2++;
            pelota.reiniciar();
        }

        if (pelota.getX() > getWidth()) {
            puntajeJ1++;
            pelota.reiniciar();
        }

        if (puntajeJ1 == 10) {
            juegoTerminado = true;
            mensajeGanador = "¡Jugador 1 ganó!";
            if (!rankingMostrado){
                botonReiniciar.setVisible(true);
                campoNombre.setVisible(true);
                campoNombre.requestFocus();
                rankingMostrado = true;
            }
        }

        if (puntajeJ2 == 10) {
            juegoTerminado = true;
            mensajeGanador = "¡Jugador 2 ganó!";
            if (!rankingMostrado){
                botonReiniciar.setVisible(true);
                campoNombre.setVisible(true);
                campoNombre.requestFocus();
                rankingMostrado = true;
            }
        }

        repaint();
    }

    public void teclaPresionada(KeyEvent e) {
        jugador1.teclaPresionada(e);
        jugador2.teclaPresionada(e);
    }

    public void teclaSoltada(KeyEvent e) {
        jugador1.teclaSoltada(e);
        jugador2.teclaSoltada(e);
    }
}

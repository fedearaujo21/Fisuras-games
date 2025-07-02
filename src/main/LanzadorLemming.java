package main;

import lemmings.modelo.Config;
import lemmings.vista.PanelLemmings;
import javax.swing.*;
import java.awt.*;

public class LanzadorLemming {

    private static JPanel cardPanel;
    private static CardLayout cardLayout;

    public static void iniciar(JFrame ventana) {

        ventana.getContentPane().removeAll();
        ventana.repaint();
        ventana.revalidate();
        ventana.setLayout(new BorderLayout());

        ventana.setTitle("Configuración del Juego - Lemmings Z");
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Selector de nivel
        String[] niveles = {"Nivel 1", "Nivel 2", "Nivel 3", "Nivel 4"};
        JComboBox<String> selectorNivel = new JComboBox<>(niveles);

        // Slide
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(crearSlide("recursos/Nivel2View.png"), "Nivel 2");
        cardPanel.add(crearSlide("recursos/Nivel1View.png"), "Nivel 1");
        cardPanel.add(crearSlide("recursos/Nivel3View.png"), "Nivel 3");
        cardPanel.add(crearSlide("recursos/Nivel4View.png"), "Nivel 4");

        ventana.add(cardPanel, BorderLayout.CENTER);

        selectorNivel.addActionListener(e -> {
            String nivelSeleccionado = (String) selectorNivel.getSelectedItem();
            cardLayout.show(cardPanel, nivelSeleccionado);
        });

        // Mute
        JPanel mutePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox muteCheck = new JCheckBox("Mute");
        mutePanel.add(muteCheck);

        // Volumen
        JPanel volumenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volumenPanel.add(new JLabel("Volumen:"));
        JSlider volumenSlider = new JSlider(0, 100, 50);
        volumenSlider.setMajorTickSpacing(25);
        volumenSlider.setPaintTicks(true);
        volumenSlider.setPaintLabels(true);
        volumenPanel.add(volumenSlider);

        // Pantalla completa
        JPanel pantallaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox chkPantallaCompleta = new JCheckBox("Pantalla completa");
        pantallaPanel.add(chkPantallaCompleta);

        // Botón aceptar
        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aceptarButton.addActionListener(e -> {
            Config.mute = muteCheck.isSelected();
            Config.volumen = volumenSlider.getValue();
            Config.pantallaCompleta = chkPantallaCompleta.isSelected();

            ventana.dispose(); // cerrar la ventana de configuración

            JFrame juegoFrame = new JFrame("Lemmings Z");
            juegoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            if (Config.pantallaCompleta) {
                juegoFrame.dispose();
                juegoFrame.setUndecorated(true);
                juegoFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                juegoFrame.setSize(800, 600);
                juegoFrame.setResizable(false);
                juegoFrame.setLocationRelativeTo(null);
            }

            PanelLemmings juego = new PanelLemmings(selectorNivel.getSelectedIndex() + 1, juegoFrame);
            juegoFrame.setContentPane(juego);
            juegoFrame.setVisible(true);
        });

        panelPrincipal.add(selectorNivel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(mutePanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(volumenPanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(pantallaPanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(aceptarButton);

        ventana.add(panelPrincipal, BorderLayout.SOUTH);
        ventana.setVisible(true);
    }

    private static JPanel crearSlide(String rutaImagen) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        ImageIcon icono = new ImageIcon(rutaImagen);
        label.setIcon(icono);
        panel.add(label);
        return panel;
    }
}


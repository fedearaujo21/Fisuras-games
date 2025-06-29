package main;

import lemmings.modelo.Config;
import lemmings.modelo.GestorNiveles;
import lemmings.modelo.NivelInfo;
import lemmings.vista.PanelLemmings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LanzadorLemming {

    private static JPanel cardPanel;
    private static CardLayout cardLayout;

    public static void iniciar(JFrame ventana) {
        ventana.getContentPane().removeAll(); // Limpiar contenido previo
        ventana.repaint();
        ventana.revalidate();
        ventana.setLayout(new BorderLayout());

        ventana.setTitle("Configuración del Juego - Lemmings Z");
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null); // Centrar ventana

        // Panel principal con layout vertical
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // padding

        // Obtener niveles disponibles
        List<NivelInfo> niveles = GestorNiveles.getTodosLosNiveles();

        // Crear ComboBox dinámico
        String[] nombresNiveles = niveles.stream().map(NivelInfo::getNombre).toArray(String[]::new);
        JComboBox<String> selectorNivel = new JComboBox<>(nombresNiveles);

        // Crear el CardLayout con slides
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        for (NivelInfo nivel : niveles) {
            cardPanel.add(crearSlide(nivel.getRutaVistaPrevia()), nivel.getNombre());
        }

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

        // Botón Aceptar
        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        aceptarButton.addActionListener(e -> {
            Config.mute = muteCheck.isSelected();
            Config.volumen = volumenSlider.getValue();
            Config.pantallaCompleta = chkPantallaCompleta.isSelected();

            ventana.dispose(); // cerrar configuración

            JFrame juegoFrame = new JFrame("Lemmings Z");
            juegoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            if (Config.pantallaCompleta) {
                juegoFrame.dispose();
                juegoFrame.setUndecorated(true);
                juegoFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                juegoFrame.setSize(800, 500);
                juegoFrame.setResizable(false);
                juegoFrame.setLocationRelativeTo(null);
            }

            // Obtener el nivel seleccionado
            int index = selectorNivel.getSelectedIndex();
            PanelLemmings juego = new PanelLemmings(niveles.get(index).getNumero());
            juegoFrame.setContentPane(juego);
            juegoFrame.setVisible(true);
        });

        // Agregar al panel principal
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

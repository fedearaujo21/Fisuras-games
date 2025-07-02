package main;

import lemmings.modelo.GestorNiveles;
import lemmings.modelo.Config;
import lemmings.modelo.NivelInfo;
import lemmings.vista.PanelLemmings;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class LanzadorLemming {

    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    private static List<NivelInfo> nivelesInfo;   // cache para conocer nº real de nivel

    public static void iniciar(JFrame ventana) {

        /*----------- Limpieza y configuración de la ventana -----------*/
        ventana.getContentPane().removeAll();
        ventana.repaint();
        ventana.revalidate();
        ventana.setLayout(new BorderLayout());

        ventana.setTitle("Configuración del Juego - Lemmings Z");
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);

        /*----------- Cargar niveles desde la BD -----------*/
        GestorNiveles gestor = new GestorNiveles();
        nivelesInfo = gestor.getTodos();   // SELECT * FROM niveles ORDER BY numero

        if (nivelesInfo.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No se encontraron niveles en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /*----------- Combo-box con nombres de nivel -----------*/
        String[] nombresNivel = nivelesInfo.stream()
                .map(n -> "Nivel " + n.getNumero())
                .toArray(String[]::new);
        JComboBox<String> selectorNivel = new JComboBox<>(nombresNivel);

        /*----------- Carrusel de miniaturas -----------*/
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        for (NivelInfo n : nivelesInfo) {
            cardPanel.add(crearSlide(n.getThumbnail()), "Nivel " + n.getNumero());
        }
        ventana.add(cardPanel, BorderLayout.CENTER);

        selectorNivel.addActionListener(e -> {
            String nivelSeleccionado = (String) selectorNivel.getSelectedItem();
            cardLayout.show(cardPanel, nivelSeleccionado);
        });

        /*----------- Controles de audio y pantalla -----------*/
        JCheckBox muteCheck = new JCheckBox("Mute");
        JSlider volumenSlider = new JSlider(0, 100, 50);
        volumenSlider.setMajorTickSpacing(25);
        volumenSlider.setPaintTicks(true);
        volumenSlider.setPaintLabels(true);

        JCheckBox chkPantallaCompleta = new JCheckBox("Pantalla completa");

        /*----------- Botón aceptar -----------*/
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
                juegoFrame.setUndecorated(true);
                juegoFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                juegoFrame.setSize(800, 600);
                juegoFrame.setResizable(false);
                juegoFrame.setLocationRelativeTo(null);
            }

            /* número de nivel real según selección */
            int idx = selectorNivel.getSelectedIndex();
            int numeroNivel = nivelesInfo.get(idx).getNumero();

            PanelLemmings juego = new PanelLemmings(numeroNivel, juegoFrame);
            juegoFrame.setContentPane(juego);
            juegoFrame.setVisible(true);
        });

        /*----------- Panel inferior con configuraciones -----------*/
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Añadir componentes al panel
        panelPrincipal.add(selectorNivel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel mutePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mutePanel.add(muteCheck);
        panelPrincipal.add(mutePanel);

        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel volumenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volumenPanel.add(new JLabel("Volumen:"));
        volumenPanel.add(volumenSlider);
        panelPrincipal.add(volumenPanel);

        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pantallaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pantallaPanel.add(chkPantallaCompleta);
        panelPrincipal.add(pantallaPanel);

        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(aceptarButton);

        ventana.add(panelPrincipal, BorderLayout.SOUTH);
        ventana.setVisible(true);
    }

    /*------------------------------------------------------------------*/
    /** Crea un JLabel con la miniatura; muestra texto de error si no se encuentra. */
    private static JPanel crearSlide(String rutaImagen) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel();

        URL imgUrl = LanzadorLemming.class.getResource(rutaImagen);
        if (imgUrl != null) {
            label.setIcon(new ImageIcon(imgUrl));
        } else {
            label.setText("Imagen no encontrada: " + rutaImagen);
            System.err.println("No se encontró la imagen: " + rutaImagen);
        }

        panel.add(label);
        return panel;
    }
}

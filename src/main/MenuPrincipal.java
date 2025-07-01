package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends Panel {
    private final JFrame ventana;
    private JTextField buscador;
    private List<CardJuego> cards;
    private JPanel contenedorJuegos;
    private JScrollPane scrollPane;

    public MenuPrincipal(JFrame ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        // aca el logo de fisuras
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/lemmings/recursos/logoFisuras.png"));
        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        logo.setIcon(new ImageIcon(imagenEscalada));
        logo.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(logo, BorderLayout.NORTH);
        // aca hacemos el panel para poner buscador
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(Color.BLACK);
        buscador = new JTextField();
        buscador.setPreferredSize(new Dimension(300, 30));
        buscador.setMaximumSize(new Dimension(300, 30));
        buscador.setFont(new Font("Arial", Font.PLAIN, 14));
        // hacemos el buscador
        JPanel panelBuscador = new JPanel(new FlowLayout());
        panelBuscador.setBackground(Color.BLACK);
        panelBuscador.add(buscador);
        panelCentro.add(panelBuscador, BorderLayout.NORTH);
        // hacemos el contenedor para las portadas de los juegos
        contenedorJuegos = new JPanel();
        contenedorJuegos.setBackground(Color.BLACK);
        contenedorJuegos.setLayout(new BoxLayout(contenedorJuegos, BoxLayout.X_AXIS));
        // hacemos el scroll por si hay mas de 3 juegos
        scrollPane = new JScrollPane(contenedorJuegos, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        scrollPane.setBorder(null);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        panelCentro.add(scrollPane, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);
        // aca hacemos el footer con nuestro nombre
        JLabel footer = new JLabel("Desarrollado por Fisuras Team", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(footer, BorderLayout.SOUTH);
        // hacemos las portadas para cada juego
        cards = new ArrayList<>();
        cards.add(new CardJuego("Lemmings Z", "/lemmings/recursos/previewLemmings.png", () -> LanzadorLemming.iniciar(ventana)));
        cards.add(new CardJuego("Pong", "/lemmings/recursos/previewPong.png", () -> new LanzadorPong(ventana)));
        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));
//        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));
//        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));
//        cards.add(new CardJuego("POO Game", "/lemmings/recursos/previewPOO.png", () -> LanzadorPoo.iniciar(ventana)));

        mostrarCards(cards);

        // Buscador en vivo
        buscador.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        // Ventana final
        ventana.setTitle("Fisuras Games");
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setContentPane(this);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void filtrar() {
        String texto = buscador.getText().trim().toLowerCase();
        List<CardJuego> filtradas = new ArrayList<>();

        for (CardJuego card : cards) {
            if (card.getNombre().toLowerCase().contains(texto)) {
                filtradas.add(card);
            }
        }

        mostrarCards(filtradas);
    }

    private void mostrarCards(List<CardJuego> lista) {
        contenedorJuegos.removeAll();

        for (CardJuego card : lista) {
            contenedorJuegos.add(wrapCard(card));
        }

        contenedorJuegos.revalidate();
        contenedorJuegos.repaint();

        // Ocultar scroll si entran en pantalla
        SwingUtilities.invokeLater(() -> {
            scrollPane.getHorizontalScrollBar().setVisible(lista.size() > 3);
        });
    }

    private Component wrapCard(CardJuego card) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.BLACK);
        wrapper.setBorder(new EmptyBorder(10, 10, 20, 10));
        wrapper.setPreferredSize(new Dimension(200, 270));
        wrapper.setMaximumSize(new Dimension(200, 270));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }
}

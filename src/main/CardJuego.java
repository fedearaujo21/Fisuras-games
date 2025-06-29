package main;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class CardJuego extends JPanel {
    public CardJuego(String nombre, String rutaPreview, Runnable accion) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, 250));
        setBackground(Color.BLACK);
        setBorder(new LineBorder(Color.YELLOW, 2));

        // Imagen del preview del juego
        JLabel preview = new JLabel();
        preview.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource(rutaPreview));
        Image imagen = iconoOriginal.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
        preview.setIcon(new ImageIcon(imagen));
        add(preview, BorderLayout.CENTER);

        // Botón "Jugar"
        JButton btn = new JButton("Jugar " + nombre);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setForeground(Color.BLACK);
        btn.addActionListener(e -> accion.run());
        add(btn, BorderLayout.SOUTH);
    }
}

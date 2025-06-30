package lemmings.modelo;

import java.sql.*;
import java.util.*;

public class GestorNiveles {

    public static NivelInfo getNivelInfoPorNumero(int numero) {
        String url = "jdbc:sqlite:data/lemmings.db";
        NivelInfo nivelInfo = null;

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM niveles WHERE numero = ?");
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String rutaImagen = rs.getString("rutaImagen");
                String rutaMusica = rs.getString("rutaMusica");
                String vista = rs.getString("rutaVista");

                long tiempo = rs.getLong("tiempo");
                int frecuencia = rs.getInt("frecuenciaSpawn");
                int cantidadLem = rs.getInt("cantidadLem");
                int objetivo = rs.getInt("objetivoLemmings");

                int entradaX = rs.getInt("entradaX");
                int entradaY = rs.getInt("entradaY");

                int salidaX = rs.getInt("salidaX");
                int salidaY = rs.getInt("salidaY");
                int salidaAncho = rs.getInt("salidaAncho");
                int salidaAlto = rs.getInt("salidaAlto");

                Map<String, Integer> habilidades = new HashMap<>();

                // Traer habilidades asociadas
                PreparedStatement stmtHab = conn.prepareStatement("SELECT * FROM habilidades WHERE nivel = ?");
                stmtHab.setInt(1, numero);
                ResultSet rsHab = stmtHab.executeQuery();
                while (rsHab.next()) {
                    String nombreHab = rsHab.getString("habilidad");
                    int cantidad = rsHab.getInt("cantidad");
                    habilidades.put(nombreHab, cantidad);
                }

                Entrada entrada = new Entrada(entradaX, entradaY);
                Salida salida = new Salida(salidaX, salidaY, salidaAncho, salidaAlto);
                Stock stock = new Stock();
                for (Map.Entry<String, Integer> entry : habilidades.entrySet()) {
                    stock.anadirHabilidad(entry.getKey(), entry.getValue());
                }

                nivelInfo = new NivelInfo(numero, nombre, rutaImagen, rutaMusica, vista,
                        entrada, salida, stock,
                        tiempo, frecuencia, cantidadLem, objetivo);


                rsHab.close();
                stmtHab.close();
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println("Error al leer nivel de la base de datos: " + e.getMessage());
        }

        return nivelInfo;
    }
}

package lemmings.modelo;

import java.sql.*;
import java.util.*;

public class GestorNiveles {

    private static final String URL_BD = "jdbc:sqlite:data/lemmings.db";

    /*---------------------------------*
     | 1) Obtener lista de todos los niveles
     *---------------------------------*/
    public List<NivelInfo> getTodos() {
        List<NivelInfo> niveles = new ArrayList<>();
        String sql = "SELECT * FROM niveles ORDER BY numero";

        try (Connection c  = DriverManager.getConnection(URL_BD);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                niveles.add(buildNivelInfo(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al leer niveles: " + e.getMessage());
        }
        return niveles;
    }

    /*---------------------------------*
     | 2) Obtener un nivel por número
     *---------------------------------*/
    public static NivelInfo getNivelInfoPorNumero(int numero) {
        String sql = "SELECT * FROM niveles WHERE numero = ?";
        try (Connection c = DriverManager.getConnection(URL_BD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, numero);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildNivelInfo(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al leer nivel: " + e.getMessage());
        }
        return null;
    }

    /*---------------------------------*
     | 3) Helper: arma NivelInfo desde ResultSet
     *---------------------------------*/
    private static NivelInfo buildNivelInfo(ResultSet rs) throws SQLException {

        /* columnas tal cual existen */
        int    numero          = rs.getInt   ("numero");
        String nombre          = rs.getString("nombre");
        String rutaImagen      = rs.getString("rutaImagen");
        String rutaMusica      = rs.getString("rutaMusica");
        String rutaVista       = rs.getString("rutaVista");
        String thumbnail       = rs.getString("thumbnail");

        long   tiempo          = rs.getLong  ("tiempo");
        int    frecuencia      = rs.getInt   ("frecuenciaSpawn");
        int    cantidadLem     = rs.getInt   ("cantidadLem");
        int    objetivo        = rs.getInt   ("objetivoLemmings");

        /* -------- Parseo de entrada y salida -------- */
        Entrada entrada = parseEntrada(rs.getString("entrada"));  // "x,y"
        Salida  salida  = parseSalida (rs.getString("salida"));   // "x,y,ancho,alto"

        /* -------- Parseo de habilidades -------- */
        Stock stock = parseHabilidades(rs.getString("habilidades")); // "Minero:10,Paracaidas:5"

        /* -------- Crear objeto -------- */
        return new NivelInfo(
                numero, nombre, rutaImagen, rutaMusica, rutaVista, thumbnail,
                entrada, salida, stock,
                tiempo, frecuencia, cantidadLem, objetivo
        );
    }

    /*================== Helpers de parseo ==================*/

    /** "x,y"  -> new Entrada(x,y) */
    private static Entrada parseEntrada(String txt) {
        String[] partes = txt.split("\\s*,\\s*");
        int x = Integer.parseInt(partes[0]);
        int y = Integer.parseInt(partes[1]);
        return new Entrada(x, y);
    }

    /** "x,y,a,b" -> new Salida(x,y,a,b) */
    private static Salida parseSalida(String txt) {
        String[] p = txt.split("\\s*,\\s*");
        int x = Integer.parseInt(p[0]);
        int y = Integer.parseInt(p[1]);
        int w = Integer.parseInt(p[2]);
        int h = Integer.parseInt(p[3]);
        return new Salida(x, y, w, h);
    }

    /**
     * "Minero:10,Paracaidas:5" -> Stock con esas cantidades
     * (las claves deben coincidir con nombres de clase Habilidad)
     */
    private static Stock parseHabilidades(String txt) {
        Stock s = new Stock();
        if (txt == null || txt.isBlank()) return s;

        String[] pares = txt.split("\\s*,\\s*");
        for (String par : pares) {
            String[] kv = par.split("\\s*:\\s*");
            if (kv.length == 2) {
                s.anadirHabilidad(kv[0], Integer.parseInt(kv[1]));
            }
        }
        return s;
    }
}


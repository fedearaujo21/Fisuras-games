package lemmings.modelo;

import java.util.HashMap;
import java.util.Map;

public class Stock {
    private Map<String, Integer> habilidadesDisponibles;

    public Stock() {
        this.habilidadesDisponibles = new HashMap<>();
    }

    public void añadirHabilidad(String nombreHabilidad, int cantidad) {
        habilidadesDisponibles.put(nombreHabilidad, cantidad);
    }

    public int getCantidad(String nombreHabilidad) {
        return habilidadesDisponibles.getOrDefault(nombreHabilidad, 0);
    }

    public boolean consumirHabilidad(String nombreHabilidad) {
        if (habilidadesDisponibles.containsKey(nombreHabilidad)) {
            int cantidad = habilidadesDisponibles.get(nombreHabilidad);
            if (cantidad > 0) {
                habilidadesDisponibles.put(nombreHabilidad, cantidad - 1);
                return true;
            }
        }
        return false;
    }
}
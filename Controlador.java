package mvcgenerala;

import javax.swing.JButton;
import java.lang.reflect.Field;

public class Controlador {
    private modelo modelo;
    private Vista vista;

    public Controlador(modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;

        // Intentamos conectar el botón de cerrar (nombre tal como está en la Vista)
        conectarBotonCerrar("btnCerrarJuego");
        // si quisieras ademas conectar otros botones por reflexión, podés agregar más llamadas
    }

    /**
     * Busca por nombre de campo en la Vista y, si es un JButton, le agrega
     * un ActionListener que cierra la ventana y termina la JVM.
     */
    private void conectarBotonCerrar(String nombreCampo) {
        try {
            Field f = vista.getClass().getDeclaredField(nombreCampo);
            f.setAccessible(true);
            Object o = f.get(vista);
            if (o instanceof JButton) {
                JButton btn = (JButton) o;
                btn.addActionListener(e -> {
                    // Primero cerramos la ventana de la Vista (dispose)
                    try { vista.dispose(); } catch (Exception ex) { /* ignorar */ }
                    // Luego terminamos la aplicación
                    System.exit(0);
                });
            }
        } catch (NoSuchFieldException nsfe) {
            // Si no existe ese nombre, intentamos algunos nombres alternativos comunes
            String[] alternativos = {"btnCerrar", "btnSalir", "btnCerrarJuego", "btnClose"};
            for (String alt : alternativos) {
                if (alt.equals(nombreCampo)) continue;
                try {
                    Field f = vista.getClass().getDeclaredField(alt);
                    f.setAccessible(true);
                    Object o = f.get(vista);
                    if (o instanceof JButton) {
                        JButton btn = (JButton) o;
                        btn.addActionListener(e -> {
                            try { vista.dispose(); } catch (Exception ex) {}
                            System.exit(0);
                        });
                        return;
                    }
                } catch (Exception ex) {
                    // seguir probando otros nombres
                }
            }
            // si no encontramos nada, no hacemos nada (no rompemos la app)
        } catch (Exception e) {
            // no queremos que una excepción de reflexión rompa el arranque;
            // solo la informamos por consola.
            e.printStackTrace();
        }
    }

    // ---------- API pública (opcional, para que la Vista use el controlador) ----------
    public void guardarNombre(String nombre) {
        modelo.setNombre(nombre);
    }

    public String obtenerNombre() {
        return modelo.getNombre();
    }

    public void tirarDados(boolean[] retenidos) {
        modelo.tirar(retenidos);
    }

    public int[] getDados() {
        return modelo.getDados();
    }

    public void pasarTurno() {
        modelo.pasarTurno();
        if (modelo.getTurno() == 2) {
            modelo.jugarTurnoCPU();
            // si querés que el turno vuelva automaticamente:
            modelo.pasarTurno();
        }
    }

    public void reiniciarJuego() {
        modelo.reiniciar();
    }

    public void salirJuego() {
        // método disponible por si la Vista llama al controlador
        try { vista.dispose(); } catch (Exception ex) {}
        System.exit(0);
    }
}

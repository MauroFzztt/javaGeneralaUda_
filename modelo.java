package mvcgenerala;

import java.util.Arrays;
import java.util.Random;

public class modelo {

    private final Random rng = new Random();
    private String nombre = "";
    private int[] dados = new int[6]; // 6 dados, acorde a la Vista
    private int turno = 1; // 1 = jugador humano, 2 = CPU

    // Puntajes internos opcionales (no obligatorios si la Vista los guarda en textfields)
    // [jugadorIndex][categoriaIndex], jugadorIndex 1=humano 2=cpu
    private int[][] puntajes = new int[3][11]; 

    public modelo() {
        reiniciar();
    }

    // ---------------- Nombre ----------------
    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre : "";
    }
    public String getNombre() { return nombre; }

    // ---------------- Dados ----------------
    /**
     * Tira los dados que NO están retenidos. El array retenidos debe tener tamaño 6.
     * Si retenidos==null entonces tira todos los dados.
     */
    public int[] tirar(boolean[] retenidos) {
        if (retenidos != null && retenidos.length != 6) {
            throw new IllegalArgumentException("El arreglo retenidos debe tener longitud 6");
        }
        for (int i = 0; i < 6; i++) {
            if (retenidos == null || !retenidos[i]) {
                dados[i] = rng.nextInt(6) + 1;
            }
        }
        return Arrays.copyOf(dados, dados.length);
    }

    public int[] getDados() {
        return Arrays.copyOf(dados, dados.length);
    }

    public void setDados(int[] nuevos) {
        if (nuevos == null || nuevos.length != 6) throw new IllegalArgumentException("6 dados requeridos");
        this.dados = Arrays.copyOf(nuevos, 6);
    }

    // ---------------- Turnos / CPU ----------------
    public int getTurno() { return turno; }
    public void pasarTurno() { turno = (turno == 1) ? 2 : 1; }

    /**
     * Lógica simple de CPU: tira (todos los) dados, evalúa categorías libres y anota la de mayor puntaje.
     */
    public void jugarTurnoCPU() {
        // La CPU hace una tirada (podés mejorar para respetar 3 tiradas/retenciones)
        tirar(null);

        int mejorCat = -1;
        int mejorP = -1;
        for (int cat = 0; cat < puntajes[2].length; cat++) {
            if (puntajes[2][cat] == 0) { // categoría libre
                int p = calcularPuntaje(cat, dados);
                if (p > mejorP) {
                    mejorP = p;
                    mejorCat = cat;
                }
            }
        }
        if (mejorCat != -1) {
            puntajes[2][mejorCat] = mejorP;
        }
    }

    // ---------------- Puntuaciones ----------------
    // Índices de categorías arbitrarios (puedes mapear con los textos que usa tu Vista)
    // 0..5 = caras 1..6, 6=escalera, 7=full, 8=poker, 9=generala, 10=doble generala (ejemplo)
    private int calcularPuntaje(int categoria, int[] dados) {
        int[] conteo = contarTodas(dados);
        switch (categoria) {
            case 0: return conteo[1] * 1;
            case 1: return conteo[2] * 2;
            case 2: return conteo[3] * 3;
            case 3: return conteo[4] * 4;
            case 4: return conteo[5] * 5;
            case 5: return conteo[6] * 6;
            case 6: return esEscalera(dados) ? 25 : 0;
            case 7: return esFull(conteo) ? 30 : 0;
            case 8: return esPoker(conteo) ? 40 : 0;
            case 9: return esGenerala(conteo) ? 50 : 0;
            case 10: return esGenerala(conteo) ? 100 : 0;
            default: return 0;
        }
    }

    public int[] contarTodas(int[] arr) {
        int[] c = new int[7];
        for (int x : arr) if (x >= 1 && x <= 6) c[x]++;
        return c;
    }

    public boolean esGenerala(int[] conteo) {
        for (int i = 1; i <= 6; i++) if (conteo[i] == 5 || conteo[i] == 6) return true;
        return false;
    }

    public boolean esPoker(int[] conteo) {
        for (int i = 1; i <= 6; i++) if (conteo[i] >= 4) return true;
        return false;
    }

    public boolean esFull(int[] conteo) {
        boolean tres = false, dos = false;
        for (int i = 1; i <= 6; i++) {
            if (conteo[i] == 3) tres = true;
            if (conteo[i] == 2) dos = true;
        }
        return tres && dos;
    }

    public boolean esEscalera(int[] arr) {
        boolean[] seen = new boolean[7];
        for (int d : arr) if (d >= 1 && d <= 6) seen[d] = true;
        boolean e1 = seen[1] && seen[2] && seen[3] && seen[4] && seen[5];
        boolean e2 = seen[2] && seen[3] && seen[4] && seen[5] && seen[6];
        return e1 || e2;
    }

    // ---------------- Reiniciar ----------------
    public void reiniciar() {
        Arrays.fill(dados, 0);
        for (int j = 0; j < puntajes.length; j++) Arrays.fill(puntajes[j], 0);
        turno = 1;
        nombre = "";
    }

    // ---------------- Util ----------------
    public static int parseOrZero(String s) {
        if (s == null) return 0;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return 0; }
    }
}

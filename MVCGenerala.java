package mvcgenerala;

public class MVCGenerala {

    public static void main(String[] args) {
        modelo modelo = new modelo();          // crear modelo
        Vista vista = new Vista();             // crear vista
        Controlador controlador = new Controlador(modelo, vista); // vincular controlador
        vista.setVisible(true);                // mostrar la ventana
    }
}

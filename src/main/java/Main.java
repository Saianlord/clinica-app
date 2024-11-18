import estructuraDatos.Cola;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Cola pila = new Cola();

        while (true) {
            String[] opciones = {"Cargar citas", "Listar citas", "Buscar cita por ID", "Sacar cita", "Salir"};
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Selecciona una opción:",
                    "Gestión de Citas",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            switch (seleccion) {
                case 0:
                    pila.cargarCitas();
                    break;

                case 1:
                    pila.listarNodos();
                    break;

                case 2:
                    try {
                        String inputId = JOptionPane.showInputDialog("Ingrese el ID de la cita a buscar:");
                        if (inputId != null) {
                            int id = Integer.parseInt(inputId);
                            pila.buscarCitaPorId(id);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID válido.");
                    }
                    break;

                case 3:
                    try {
                        pila.sacarNodo();
                        JOptionPane.showMessageDialog(null, "Cita atendida correctamente.");
                    } catch (IllegalStateException e) {
                        JOptionPane.showMessageDialog(null, "No hay citas para atender.");
                    }
                    break;

                case 4:
                    JOptionPane.showMessageDialog(null, "¡Hasta luego!");
                    System.exit(0);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Operación cancelada.");
                    System.exit(0);
            }
        }
    }
}

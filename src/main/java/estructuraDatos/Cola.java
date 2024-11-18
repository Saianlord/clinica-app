package estructuraDatos;

import modelos.Cita;
import repositorios.CitaRepositorio;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Cola {

    private Nodo cima;
    private Nodo cola;
    private final CitaRepositorio crepo = new CitaRepositorio();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Cola() {
        this.cima = null;
        this.cola = null;
    }

    public boolean esVacia() {
        return cima == null;
    }

    public void cargarCitas() {
        try {
            // Reiniciar la cola antes de cargar nuevas citas
            cima = null;
            cola = null;

            List<Cita> citas = crepo.porFecha(LocalDate.parse("18-11-2024", dateFormatter));
            if (!citas.isEmpty()) {
                for (Cita cita : citas) {
                    Nodo nuevoNodo = new Nodo(cita);
                    if (esVacia()) {
                        cima = nuevoNodo;
                        cola = nuevoNodo;
                    } else {
                        cola.setSiguiente(nuevoNodo);
                        cola = nuevoNodo;
                    }
                }
                JOptionPane.showMessageDialog(null, "Citas cargadas exitosamente. Total: " + citas.size());
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron citas para la fecha indicada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void listarNodos() {
        if (esVacia()) {
            JOptionPane.showMessageDialog(null, "La cola está vacía.");
            return;
        }

        StringBuilder listado = new StringBuilder();
        listado.append(String.format("%-5s %-20s %-20s %-12s %-8s\n",
                "ID", "Paciente", "Odontólogo", "Fecha", "Hora"));
        listado.append("--------------------------------------------------------------\n");

        Nodo actual = cima;
        while (actual != null) {
            listado.append(actual.getCita().toString()).append("\n");
            actual = actual.getSiguiente();
        }

        JOptionPane.showMessageDialog(null, listado.toString());
    }



    public Cita sacarNodo() {
        if (esVacia()) {
            throw new IllegalStateException("La pila está vacía, no hay nodos para sacar.");
        }

        Cita cita = cima.getCita();
        cima = cima.getSiguiente();
        if (cima == null) {
            cola = null;
        }
        return cita;
    }

    public void buscarCitaPorId(int id) {
        if (esVacia()) {
            JOptionPane.showMessageDialog(null, "La cola está vacía.");
            return;
        }

        Nodo actual = cima;
        while (actual != null) {
            if (actual.getCita().getId() == id) {
                // Construir la tabla con formato
                StringBuilder resultado = new StringBuilder();
                resultado.append(String.format("%-5s %-20s %-20s %-12s %-8s\n",
                        "ID", "Paciente", "Odontólogo", "Fecha", "Hora"));
                resultado.append("--------------------------------------------------------------\n");
                resultado.append(actual.getCita().toString()).append("\n");

                JOptionPane.showMessageDialog(null, resultado.toString());
                return;
            }
            actual = actual.getSiguiente();
        }

        JOptionPane.showMessageDialog(null, "No se encontró ninguna cita con el ID proporcionado.");
    }

    //este método usa CitaService para cargar las citas
        //Se puede llamar desde el menu de citas en caso de que una cita se haya reagendado, asi ya no aparecerá

    public void atenderCita(){
        /*Debería primero devolver los fields:
        odontologo_id
        paciente_id
        recepcionista_id
        hora_inicio
        hora_final
        observaciones
        total_pago (monto de la cita)

        para que el recepcionista llene la información.

        llama al método gestionarItems()
         */
    }

    private void gestionarItems(){
        /*
        Primero se crea  una nueva factura sin el método de pago
        De esta manera se puede agregar el id de factura al producto o servicio, si se requiere.
        Pregunta si el paciente adquirirá un producto o servicio, si sí,
        entonces interactúa con el Item service para obtener una lista de los productos disponibles y enseñarlos al recepcionista
        al introducir el id del producto, se pregunta la cantidad, se utiliza un hash map de tipo String n el que el key es el item id, y el value tiene el formato
        (cantidad, total)
        después se pregunta si se quiera añadir otro.
        Si sí, se repite el proceso, y si el id ya esta en el map, se hace un split del value y se incrementa el primer valor (cantidad)
        si sí se hizo compra de productos, se llama a calcular total y se pasa por parámetro la Factura y el Map, y el metodo modifica el total de la factura.
        (Si no se adquieren productos, solo se llama al método calcularTotal y se pasa la Factura
        por último se llama a facturar y se le pasa el map (si hay) y la factura
         */
    }

    private void calcularTotal(){
        /*
        Primero se obtiene la factura y Map generados en el método anterior, y se le pide al recepcionista
        Si hay items en el Map, se calcula el total de esos items
        Este metodo toma la factura y le agrega el 13%.

         */
    }

    private void facturar(){
        /*

        Se pide el método de pago y se agrega a la factura.

        Se pide confirmación de si desea generar la factura, si sí, entonces

        se llama al ItemsService y se le pasa el Map para persistir los items_factura

        se llama al factura service y se le pasa la factura para persistirla

        Por último este método llama al método sacarNodo()

         */
    }

}

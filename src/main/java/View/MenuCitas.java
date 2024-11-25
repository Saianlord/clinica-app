package View;

import estructuraDatos.Cola;
import modelos.Cita;
import modelos.Usuario;
import servicios.CitaServicio;
import servicios.UsuarioServicio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MenuCitas {

    private final CitaServicio cServicio;
    private final UsuarioServicio uServicio;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final Cola cola;




    public MenuCitas(CitaServicio cServicio, UsuarioServicio uServicio, Cola cola) {
        this.cServicio = cServicio;
        this.uServicio = uServicio;
        this.cola = cola;
    }

    public void mostrarMenu(){
        JFrame frame = new JFrame("Menú de Citas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10)); 
        panel.setPreferredSize(new Dimension(300, 400));

        String[] opciones = {"Nueva cita", "Actualizar cita", "Cancelar cita", "Consultar cita", "Atender citas de hoy", "Volver"};

        for (String opcion : opciones) {
            JButton boton = new JButton(opcion);
            boton.setBackground(Color.CYAN);
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 16));
            boton.setFocusPainted(false);

            boton.addActionListener(e -> {
                frame.dispose();
                procesarSeleccion(opcion);
            });

            panel.add(boton);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void procesarSeleccion(String seleccion) {
        switch (seleccion) {
            case "Nueva cita":
                citaForm(false);
                break;

            case "Actualizar cita":
                citaForm(true);
                break;

            case "Cancelar cita":
                cancelarCita();
                break;

            case "Consultar cita":
                consultarCita();
                break;

            case "Atender citas de hoy":
                atenderCitasDeHoy();
                break;

            case "Volver":
                break;

            default:
                break;
        }
    }

    private void citaForm(boolean esActualizar) {
        Cita cita = new Cita();

        if (esActualizar) {
            JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
            JTextField tfId = new JTextField();
            JTextField tfPaciente = new JTextField();

            panelBusqueda.add(new JLabel("ID de la Cita:"));
            panelBusqueda.add(tfId);
            panelBusqueda.add(new JLabel("Nombre del Paciente:"));
            panelBusqueda.add(tfPaciente);

            int optionBusqueda = JOptionPane.showConfirmDialog(
                    null,
                    panelBusqueda,
                    "Buscar Cita para Actualizar",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
                mostrarMenu();
                return;
            }

            if (optionBusqueda == JOptionPane.OK_OPTION) {
                String idTexto = tfId.getText().trim();
                String nombrePaciente = tfPaciente.getText().trim();

                try {
                    if (!idTexto.isEmpty()) {
                        long id = Long.parseLong(idTexto);
                        cita = cServicio.porId(id);

                        if (cita == null) {
                            mostrarError("No se encontró una cita con el ID proporcionado.");
                            citaForm(esActualizar);
                            return;
                        }
                    } else if (!nombrePaciente.isEmpty()) {
                        cita = cServicio.porPaciente(nombrePaciente);
                        if (cita == null) {
                            mostrarError("No se encontró una cita con el paciente proporcionado.");
                            citaForm(esActualizar);
                            return;
                        }
                    } else {
                        mostrarError("Debe ingresar el ID o el Nombre del Paciente.");
                        citaForm(esActualizar);
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("El ID debe ser un número válido.");
                    citaForm(esActualizar);
                    return;
                } catch (SQLException e) {
                    mostrarError("Error al buscar la cita: " + e.getMessage());
                    citaForm(esActualizar);
                    return;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField tfOdontologo = new JTextField(cita.getOdontologo() != null ? cita.getOdontologo().getNombre() : "");
        JTextField tfPaciente = new JTextField(cita.getPaciente() != null ? cita.getPaciente().getNombre() : "");
        JTextField tfRecepcionista = new JTextField(cita.getRecepcionista() != null ? cita.getRecepcionista().getNombre() : "");
        JTextField tfMotivo = new JTextField(cita.getMotivo() != null ? cita.getMotivo() : "");

        JTextField tfFechaAtencion = new JTextField(
                cita.getFechaAtencion() != null ? cita.getFechaAtencion().format(dateFormatter) : ""
        );

        JTextField tfHoraInicial = new JTextField(
                cita.getHoraInicial() != null ? cita.getHoraInicial().format(timeFormatter) : ""
        );

        panel.add(new JLabel("Odontólogo:"));
        panel.add(tfOdontologo);
        panel.add(new JLabel("Paciente:"));
        panel.add(tfPaciente);
        panel.add(new JLabel("Recepcionista:"));
        panel.add(tfRecepcionista);
        panel.add(new JLabel("Motivo:"));
        panel.add(tfMotivo);
        panel.add(new JLabel("Fecha Atención:"));
        panel.add(tfFechaAtencion);
        panel.add(new JLabel("Hora Inicial:"));
        panel.add(tfHoraInicial);

        int option = JOptionPane.showConfirmDialog(
                null,
                panel,
                esActualizar ? "Actualizar Cita" : "Nueva Cita",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (option == JOptionPane.OK_OPTION) {
            String odontologo = tfOdontologo.getText().trim();
            String paciente = tfPaciente.getText().trim();
            String recepcionista = tfRecepcionista.getText().trim();
            String motivo = tfMotivo.getText().trim();
            String fechaAtencion = tfFechaAtencion.getText().trim();
            String horaInicial = tfHoraInicial.getText().trim();

            if (odontologo.isEmpty() || paciente.isEmpty() || recepcionista.isEmpty() || motivo.isEmpty() || fechaAtencion.isEmpty() || horaInicial.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                citaForm(esActualizar);
            } else {
                try {
                    Usuario odontologoUsuario = uServicio.porNombre(odontologo, 1);
                    Usuario pacienteUsuario = uServicio.porNombre(paciente, 2);
                    Usuario recepcionistaUsuario = uServicio.porNombre(recepcionista, 3);

                    if (odontologoUsuario != null && pacienteUsuario != null && recepcionistaUsuario != null) {
                        cita.setOdontologo(odontologoUsuario);
                        cita.setPaciente(pacienteUsuario);
                        cita.setRecepcionista(recepcionistaUsuario);
                        cita.setMotivo(motivo);
                        cita.setFechaAtencion(LocalDate.parse(fechaAtencion, dateFormatter));
                        cita.setHoraInicial(LocalTime.parse(horaInicial, timeFormatter));

                        if (esActualizar) {
                            cServicio.guardarCita(cita);
                            JOptionPane.showMessageDialog(null, "Cita actualizada correctamente.");
                        } else {
                            cServicio.guardarCita(cita);
                            JOptionPane.showMessageDialog(null, "Cita creada correctamente.");
                        }
                    } else {
                        mostrarError("Uno de los usuarios no existe.");
                    }
                } catch (SQLException e) {
                    mostrarError("Se produjo un error al guardar la cita: " + e.getMessage());
                }
            }
        }

        mostrarMenu();
    }

    private void cancelarCita() {
        JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
        JTextField tfId = new JTextField();
        JTextField tfPaciente = new JTextField();

        panelBusqueda.add(new JLabel("ID de la Cita:"));
        panelBusqueda.add(tfId);
        panelBusqueda.add(new JLabel("Nombre del Paciente:"));
        panelBusqueda.add(tfPaciente);

        int optionBusqueda = JOptionPane.showConfirmDialog(
                null,
                panelBusqueda,
                "Cancelar Cita",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (optionBusqueda == JOptionPane.OK_OPTION) {
            String idTexto = tfId.getText().trim();
            String nombrePaciente = tfPaciente.getText().trim();

            try {
                Cita cita;
                if (!idTexto.isEmpty()) {
                    long id = Long.parseLong(idTexto);
                    cita = cServicio.porId(id);

                    if (cita == null) {
                        mostrarError("No se encontró una cita con el ID proporcionado.");
                        cancelarCita();
                        return;
                    }
                } else if (!nombrePaciente.isEmpty()) {
                    cita = cServicio.porPaciente(nombrePaciente);
                    if (cita == null) {
                        mostrarError("No se encontró una cita con el paciente proporcionado.");
                        cancelarCita();
                        return;
                    }
                } else {
                    mostrarError("Debe ingresar el ID o el Nombre del Paciente.");
                    cancelarCita();
                    return;
                }

                JPanel panelCita = new JPanel(new GridLayout(6, 2));
                JLabel lblOdontologo = new JLabel(cita.getOdontologo() != null ? cita.getOdontologo().getNombre() : "");
                JLabel lblPaciente = new JLabel(cita.getPaciente() != null ? cita.getPaciente().getNombre() : "");
                JLabel lblRecepcionista = new JLabel(cita.getRecepcionista() != null ? cita.getRecepcionista().getNombre() : "");
                JLabel lblMotivo = new JLabel(cita.getMotivo() != null ? cita.getMotivo() : "");
                JLabel lblFechaAtencion = new JLabel(cita.getFechaAtencion() != null ? cita.getFechaAtencion().format(dateFormatter) : "");
                JLabel lblHoraInicial = new JLabel(cita.getHoraInicial() != null ? cita.getHoraInicial().format(timeFormatter) : "");

                panelCita.add(new JLabel("Odontólogo:"));
                panelCita.add(lblOdontologo);
                panelCita.add(new JLabel("Paciente:"));
                panelCita.add(lblPaciente);
                panelCita.add(new JLabel("Recepcionista:"));
                panelCita.add(lblRecepcionista);
                panelCita.add(new JLabel("Motivo:"));
                panelCita.add(lblMotivo);
                panelCita.add(new JLabel("Fecha Atención:"));
                panelCita.add(lblFechaAtencion);
                panelCita.add(new JLabel("Hora Inicial:"));
                panelCita.add(lblHoraInicial);

                int optionMostrarCita = JOptionPane.showConfirmDialog(
                        null,
                        panelCita,
                        "Confirmar Cancelación",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (optionMostrarCita == JOptionPane.CANCEL_OPTION) {
                    mostrarMenu();
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "¿Seguro que deseas cancelar la cita?",
                        "Confirmar Cancelación",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    cServicio.eliminarCita(cita.getId());
                    JOptionPane.showMessageDialog(null, "Cita cancelada correctamente.");
                }
            } catch (NumberFormatException e) {
                mostrarError("El ID debe ser un número válido.");
                cancelarCita();
            } catch (SQLException e) {
                mostrarError("Error al cancelar la cita: " + e.getMessage());
            }
        }

        mostrarMenu();
    }


    private void consultarCita() {
        JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
        JTextField tfId = new JTextField();
        JTextField tfPaciente = new JTextField();

        panelBusqueda.add(new JLabel("ID de la Cita:"));
        panelBusqueda.add(tfId);
        panelBusqueda.add(new JLabel("Nombre del Paciente:"));
        panelBusqueda.add(tfPaciente);

        int optionBusqueda = JOptionPane.showConfirmDialog(
                null,
                panelBusqueda,
                "Consultar Cita",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (optionBusqueda == JOptionPane.OK_OPTION) {
            String idTexto = tfId.getText().trim();
            String nombrePaciente = tfPaciente.getText().trim();

            try {
                Cita cita;
                if (!idTexto.isEmpty()) {
                    long id = Long.parseLong(idTexto);
                    cita = cServicio.porId(id);

                    if (cita == null) {
                        mostrarError("No se encontró una cita con el ID proporcionado.");
                        consultarCita();
                        return;
                    }
                } else if (!nombrePaciente.isEmpty()) {
                    cita = cServicio.porPaciente(nombrePaciente);
                    if (cita == null) {
                        mostrarError("No se encontró una cita con el paciente proporcionado.");
                        consultarCita();
                        return;
                    }
                } else {
                    mostrarError("Debe ingresar el ID o el Nombre del Paciente.");
                    consultarCita();
                    return;
                }

                JPanel panelCita = new JPanel(new GridLayout(6, 2));
                JLabel lblOdontologo = new JLabel(cita.getOdontologo() != null ? cita.getOdontologo().getNombre() : "");
                JLabel lblPaciente = new JLabel(cita.getPaciente() != null ? cita.getPaciente().getNombre() : "");
                JLabel lblRecepcionista = new JLabel(cita.getRecepcionista() != null ? cita.getRecepcionista().getNombre() : "");
                JLabel lblMotivo = new JLabel(cita.getMotivo() != null ? cita.getMotivo() : "");
                JLabel lblFechaAtencion = new JLabel(cita.getFechaAtencion() != null ? cita.getFechaAtencion().format(dateFormatter) : "");
                JLabel lblHoraInicial = new JLabel(cita.getHoraInicial() != null ? cita.getHoraInicial().format(timeFormatter) : "");

                panelCita.add(new JLabel("Odontólogo:"));
                panelCita.add(lblOdontologo);
                panelCita.add(new JLabel("Paciente:"));
                panelCita.add(lblPaciente);
                panelCita.add(new JLabel("Recepcionista:"));
                panelCita.add(lblRecepcionista);
                panelCita.add(new JLabel("Motivo:"));
                panelCita.add(lblMotivo);
                panelCita.add(new JLabel("Fecha Atención:"));
                panelCita.add(lblFechaAtencion);
                panelCita.add(new JLabel("Hora Inicial:"));
                panelCita.add(lblHoraInicial);

                int optionMostrarCita = JOptionPane.showConfirmDialog(
                        null,
                        panelCita,
                        "Consultar Cita",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (optionMostrarCita == JOptionPane.CANCEL_OPTION) {
                    mostrarMenu();  
                    return;
                }

                mostrarMenu();

            } catch (NumberFormatException e) {
                mostrarError("El ID debe ser un número válido.");
                consultarCita();
            } catch (SQLException e) {
                mostrarError("Error al consultar la cita: " + e.getMessage());
            }
        }
    }


    private void atenderCitasDeHoy() {
        cola.listarNodos();
    }

    private void mostrarError(String mensaje) {
        JPanel errorPanel = new JPanel();
        errorPanel.add(new JLabel(mensaje));
        JOptionPane.showMessageDialog(null, errorPanel, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

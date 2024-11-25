package View;

import modelos.Usuario;
import servicios.UsuarioServicio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MenuUsuarios {
    private final UsuarioServicio uService;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public MenuUsuarios(UsuarioServicio uService) {
        this.uService = uService;
    }

    public void mostrarMenu() {
        JFrame frame = new JFrame("Menú de Usuarios");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        panel.setPreferredSize(new Dimension(300, 400));

        String[] opciones = {"Administrar pacientes", "Administrar odontólogos", "Volver"};

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
            case "Administrar pacientes":
                mostrarSubMenuPacientes();
                break;

            case "Administrar odontólogos":
                mostrarSubMenuOdontologos();
                break;

            case "Volver":
                return;

            default:
                break;
        }
    }

    private void mostrarSubMenuPacientes() {
        JFrame frame = new JFrame("Submenú Pacientes");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        String[] opciones = {"Nuevo paciente", "Actualizar registro de paciente", "Cambiar estado de paciente", "Volver"};

        for (String opcion : opciones) {
            JButton boton = new JButton(opcion);
            boton.setBackground(Color.CYAN);
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 16));
            boton.setFocusPainted(false);

            boton.addActionListener(e -> {
                frame.dispose();
                if (!opcion.equals("Volver")) {
                    procesarOpcionPaciente(opcion);
                } else {
                    mostrarMenu();
                }
            });

            panel.add(boton);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void mostrarSubMenuOdontologos() {
        JFrame frame = new JFrame("Submenú Odontólogos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        String[] opciones = {"Nuevo odontólogo", "Actualizar registro de odontólogo", "Cambiar estado de odontólogo", "Volver"};

        for (String opcion : opciones) {
            JButton boton = new JButton(opcion);
            boton.setBackground(Color.CYAN);
            boton.setForeground(Color.BLACK);
            boton.setFont(new Font("Arial", Font.BOLD, 16));
            boton.setFocusPainted(false);

            boton.addActionListener(e -> {
                frame.dispose();
                if (!opcion.equals("Volver")) {
                    procesarOpcionOdontologo(opcion);
                } else {
                    mostrarMenu();
                }
            });

            panel.add(boton);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void procesarOpcionPaciente(String opcion) {
        switch (opcion) {
            case "Nuevo paciente":
                usuarioForm(false, 2);
                break;
            case "Actualizar registro de paciente":
                usuarioForm(true, 2);
                break;
            case "Cambiar estado de paciente":
                cambiarEstadoUsuario(2);
                break;
            default:
                break;
        }
    }

    private void procesarOpcionOdontologo(String opcion) {
        switch (opcion) {
            case "Nuevo odontólogo":
                usuarioForm(false,1);
                break;
            case "Actualizar registro de odontólogo":
                usuarioForm(true,1);
                break;
            case "Cambiar estado de odontólogo":
                cambiarEstadoUsuario(1);
                break;
            default:
                break;
        }
    }

    private void usuarioForm(boolean esActualizar, int tipo) {
        Usuario usuario = new Usuario();

        if (esActualizar) {
            JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
            JTextField tfId = new JTextField();
            JTextField tfNombre = new JTextField();

            panelBusqueda.add(new JLabel("ID del Usuario:"));
            panelBusqueda.add(tfId);
            panelBusqueda.add(new JLabel("Nombre del Usuario:"));
            panelBusqueda.add(tfNombre);

            int optionBusqueda = JOptionPane.showConfirmDialog(
                    null,
                    panelBusqueda,
                    "Buscar Usuario",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
                if (tipo == 2) {
                    mostrarSubMenuPacientes();
                } else {
                    mostrarSubMenuOdontologos();
                }
                return;
            }

            if (optionBusqueda == JOptionPane.OK_OPTION) {
                String idTexto = tfId.getText().trim();
                String nombre = tfNombre.getText().trim();

                try {
                    if (!idTexto.isEmpty()) {
                        long id = Long.parseLong(idTexto);
                        usuario = uService.porId(id, tipo);

                        if (usuario == null) {
                            mostrarError("No se encontró un usuario con el ID proporcionado.", tipo);
                            usuarioForm(esActualizar, tipo);
                            return;
                        }
                    } else if (!nombre.isEmpty()) {
                        usuario = uService.porNombre(nombre, tipo);

                        if (usuario == null) {
                            mostrarError("No se encontró un usuario con el nombre proporcionado.", tipo);
                            usuarioForm(esActualizar, tipo);
                            return;
                        }
                    } else {
                        mostrarError("Debe ingresar el ID o el Nombre del usuario.", tipo);
                        usuarioForm(esActualizar, tipo);
                        return;
                    }
                } catch (NumberFormatException e) {
                    mostrarError("El ID debe ser un número válido.", tipo);
                    usuarioForm(esActualizar, tipo);
                    return;
                } catch (SQLException e) {
                    mostrarError("Error al buscar el usuario: " + e.getMessage(), tipo);
                    usuarioForm(esActualizar, tipo);
                    return;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField tfNombre = new JTextField(usuario.getNombre() != null ? usuario.getNombre() : "");
        JTextField tfApellido = new JTextField(usuario.getApellido() != null ? usuario.getApellido() : "");
        JTextField tfTelefono = new JTextField(usuario.getTelefono() != null ? usuario.getTelefono() : "");
        JTextField tfFechaNacimiento = new JTextField(
                usuario.getFechaNacimiento() != null ? usuario.getFechaNacimiento().format(dateFormatter) : ""
        );

        panel.add(new JLabel("Nombre:"));
        panel.add(tfNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(tfApellido);
        panel.add(new JLabel("Teléfono:"));
        panel.add(tfTelefono);
        panel.add(new JLabel("Fecha de nacimiento (dd-mm-yyyy):"));
        panel.add(tfFechaNacimiento);

        int option = JOptionPane.showConfirmDialog(
                null,
                panel,
                esActualizar ? "Actualizar Usuario" : "Nuevo Usuario",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.CANCEL_OPTION) {
            if (tipo == 2) {
                mostrarSubMenuPacientes();
            } else {
                mostrarSubMenuOdontologos();
            }
            return;
        }

        if (option == JOptionPane.OK_OPTION) {
            String nombre = tfNombre.getText().trim();
            String apellido = tfApellido.getText().trim();
            String telefono = tfTelefono.getText().trim();
            String fechaNacimiento = tfFechaNacimiento.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || fechaNacimiento.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.");
                usuarioForm(esActualizar, tipo);
            } else {
                usuario.setNombre(nombre);
                usuario.setApellido(apellido);
                usuario.setTelefono(telefono);
                try {
                    usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento, dateFormatter));
                } catch (Exception e) {
                    mostrarError("La fecha de nacimiento debe tener el formato dd-mm-yyyy.", tipo);
                    usuarioForm(esActualizar, tipo);
                    return;
                }

                if (!esActualizar) {
                    usuario.setTipo(tipo);
                }

                try {
                    uService.guardarUsuario(usuario);
                    JOptionPane.showMessageDialog(null, esActualizar ? "Usuario actualizado correctamente." : "Usuario creado correctamente.");
                } catch (SQLException e) {
                    mostrarError("Se produjo un error al guardar el usuario: " + e.getMessage(), tipo);
                }
            }
        }

        if (tipo == 2) {
            mostrarSubMenuPacientes();
        } else {
            mostrarSubMenuOdontologos();
        }
    }

    private void cambiarEstadoUsuario(int tipo) {
        JPanel panelBusqueda = new JPanel(new GridLayout(2, 2));
        JTextField tfId = new JTextField();
        JTextField tfNombre = new JTextField();

        panelBusqueda.add(new JLabel("ID del Usuario:"));
        panelBusqueda.add(tfId);
        panelBusqueda.add(new JLabel("Nombre del Usuario:"));
        panelBusqueda.add(tfNombre);

        int optionBusqueda = JOptionPane.showConfirmDialog(
                null,
                panelBusqueda,
                "Buscar Usuario",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (optionBusqueda == JOptionPane.CANCEL_OPTION) {
            mostrarMenu();
            return;
        }

        if (optionBusqueda == JOptionPane.OK_OPTION) {
            String idTexto = tfId.getText().trim();
            String nombre = tfNombre.getText().trim();
            Usuario usuario;

            try {
                if (!idTexto.isEmpty()) {
                    long id = Long.parseLong(idTexto);
                    usuario = uService.porId(id, tipo);

                    if (usuario == null) {
                        mostrarError("No se encontró un usuario con el ID proporcionado.", tipo);
                        return;
                    }
                } else if (!nombre.isEmpty()) {
                    usuario = uService.porNombre(nombre, tipo);

                    if (usuario == null) {
                        mostrarError("No se encontró un usuario con el nombre proporcionado.", tipo);
                        return;
                    }
                } else {
                    mostrarError("Debe ingresar el ID o el Nombre del usuario.", tipo);
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarError("El ID debe ser un número válido.", tipo);
                return;
            } catch (SQLException e) {
                mostrarError("Error al buscar el usuario: " + e.getMessage(), tipo);
                return;
            }

            JPanel panelEstado = new JPanel(new GridLayout(5, 2));
            panelEstado.add(new JLabel("Nombre:"));
            panelEstado.add(new JLabel(usuario.getNombre()));
            panelEstado.add(new JLabel("Apellido:"));
            panelEstado.add(new JLabel(usuario.getApellido()));
            panelEstado.add(new JLabel("Fecha de Registro: "));
            panelEstado.add(new JLabel(usuario.getFechaRegistro().toString()));
            panelEstado.add(new JLabel("Estado actual:"));

            String[] estados = {"Activo", "Inactivo"};
            JComboBox<String> cbEstado = new JComboBox<>(estados);
            cbEstado.setSelectedItem(usuario.getActivo() == 1 ? "Activo" : "Inactivo");
            panelEstado.add(cbEstado);

            int optionEstado = JOptionPane.showConfirmDialog(
                    null,
                    panelEstado,
                    "Cambiar Estado del Usuario",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (optionEstado == JOptionPane.CANCEL_OPTION) {
                mostrarMenu();
                return;
            }

            if (optionEstado == JOptionPane.OK_OPTION) {
                boolean nuevoEstado = cbEstado.getSelectedItem().equals("Activo");
                usuario.setActivo(nuevoEstado ? 1 : 0);

                try {
                    uService.guardarUsuario(usuario);
                    JOptionPane.showMessageDialog(null, "Estado del usuario actualizado correctamente.");
                } catch (SQLException e) {
                    mostrarError("Se produjo un error al actualizar el estado: " + e.getMessage(), tipo);
                }
            }
        }

        mostrarMenu();
    }


    private void mostrarError(String mensaje, int tipo) {
        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new BorderLayout());

        JLabel errorLabel = new JLabel("<html><font color='red'>" + mensaje + "</font></html>");
        errorPanel.add(errorLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, errorPanel, "Error", JOptionPane.ERROR_MESSAGE);

        if (tipo == 2) {
            mostrarSubMenuPacientes();
        } else {
            mostrarSubMenuOdontologos();
        }
    }
}

package modelos;

import java.time.LocalDate;

public class Paciente extends Usuario{
    public Paciente(String id, String nombre, String apellido, String telefono, LocalDate fechaNacimiento) {
        super(id, nombre, apellido, telefono, fechaNacimiento);
    }
}

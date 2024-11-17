package modelos;

import java.time.LocalDate;

public class Paciente extends Usuario{
    public Paciente(long id, String nombre, String apellido, String telefono, LocalDate fechaNacimiento) {
        super(id,2, nombre, apellido, telefono, fechaNacimiento);
    }

    public Paciente() {
    }
}

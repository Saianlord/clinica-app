package modelos;

import java.time.LocalDate;

public class Odontologo extends Usuario{
    public Odontologo(long id, String nombre, String apellido, String telefono, LocalDate fechaNacimiento) {
        super(id,1, nombre, apellido, telefono, fechaNacimiento);
    }

    public Odontologo() {
    }
}

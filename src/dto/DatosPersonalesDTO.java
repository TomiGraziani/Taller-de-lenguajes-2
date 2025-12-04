package dto;

public class DatosPersonalesDTO {
    private final String nombre;
    private final String apellido;
    private final long dni;

    public DatosPersonalesDTO(String nombre, String apellido, long dni) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public long getDni() {
        return dni;
    }
}

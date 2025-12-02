package vista.dto;

import modelo.Audiovisual;

public class ReseniaDTO {
    private final String nombreUsuario;
    private final String contrasenia;
    private final int calificacion;
    private final String comentario;
    private final Audiovisual seleccionada;

    public ReseniaDTO(String nombreUsuario, String contrasenia, int calificacion, String comentario, Audiovisual seleccionada) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.seleccionada = seleccionada;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public Audiovisual getSeleccionada() {
        return seleccionada;
    }
}

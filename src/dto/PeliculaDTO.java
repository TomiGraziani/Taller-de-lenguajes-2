package dto;

public class PeliculaDTO {
    private final String titulo;
    private final String director;
    private final String resumen;
    private final double duracion;
    private final String genero;

    public PeliculaDTO(String titulo, String director, String resumen, double duracion, String genero) {
        this.titulo = titulo;
        this.director = director;
        this.resumen = resumen;
        this.duracion = duracion;
        this.genero = genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDirector() {
        return director;
    }

    public String getResumen() {
        return resumen;
    }

    public double getDuracion() {
        return duracion;
    }

    public String getGenero() {
        return genero;
    }
}

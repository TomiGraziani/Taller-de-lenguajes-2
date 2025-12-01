package modelo;

import enumerativo.Genero;
import enumerativo.TipoContenido;

public class Pelicula extends Audiovisual {
    private double ratingPromedio;
    private int anio;
    private String poster;

    public Pelicula(int id, String titulo, String sinopsis, String director, Genero genero,
                    double duracion, double ratingPromedio, int anio, String poster) {
        super(id, titulo, sinopsis, director, genero, duracion, TipoContenido.PELICULA);
        this.ratingPromedio = ratingPromedio;
        this.anio = anio;
        this.poster = poster;
    }

    public double getRatingPromedio() {
        return ratingPromedio;
    }

    public void setRatingPromedio(double ratingPromedio) {
        this.ratingPromedio = ratingPromedio;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}

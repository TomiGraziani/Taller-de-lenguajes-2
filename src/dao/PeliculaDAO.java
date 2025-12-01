package dao;

import java.util.List;

import modelo.Pelicula;

public interface PeliculaDAO {

    void insertar(String titulo, String director, String resumen, double duracion, String genero);

    void insertar(String titulo, String director, String resumen, double duracion, String genero,
                  double ratingPromedio, int anio, String poster);

    Pelicula buscarPorTitulo(String titulo);

    List<Pelicula> listarTodas();
}

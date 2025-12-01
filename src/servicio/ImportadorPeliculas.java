package servicio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dao.implementacion.PeliculaDAOjdbc;
import enumerativo.Genero;
import excepcion.CargaArchivoException;
import modelo.Pelicula;

public class ImportadorPeliculas {

    private final PeliculaDAOjdbc peliculaDAO;
    private final List<Pelicula> cachePeliculas = new ArrayList<>();
    private final Random random = new Random();

    public ImportadorPeliculas(PeliculaDAOjdbc peliculaDAO) {
        this.peliculaDAO = peliculaDAO;
    }

    public synchronized List<Pelicula> cargarPeliculas(Path csvPath) throws CargaArchivoException {
        if (!cachePeliculas.isEmpty()) {
            return cachePeliculas;
        }

        try {
            Map<String, Integer> indices = new HashMap<>();
            try (BufferedReader reader = abrirLectorCsv(csvPath)) {
                String header = reader.readLine();
                if (header == null) {
                    throw new CargaArchivoException("El archivo CSV está vacío");
                }
                String[] columnas = separar(header);
                for (int i = 0; i < columnas.length; i++) {
                    indices.put(columnas[i].trim().toLowerCase(), i);
                }

                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] datos = separar(linea);
                    if (datos.length < columnas.length) {
                        continue;
                    }

                    String titulo = obtener(datos, indices, "title");
                    String resumen = obtener(datos, indices, "overview");
                    String director = obtener(datos, indices, "director");
                    String generoCsv = obtener(datos, indices, "genres");
                    String fecha = obtener(datos, indices, "release_date");
                    String rating = obtener(datos, indices, "vote_average");
                    String duracion = obtener(datos, indices, "runtime");
                    String poster = obtener(datos, indices, "poster_url");

                    if (titulo == null || titulo.isBlank()) {
                        continue;
                    }

                    if (peliculaDAO.buscarPorTitulo(titulo) != null) {
                        continue;
                    }

                    Genero genero = mapearGenero(generoCsv);
                    double duracionMin = duracion != null && !duracion.isBlank() ? Double.parseDouble(duracion) : 0.0;
                    double ratingPromedio = rating != null && !rating.isBlank() ? Double.parseDouble(rating) : 0.0;
                    int anio = obtenerAnio(fecha);

                    peliculaDAO.insertar(titulo, director == null ? "" : director, resumen == null ? "" : resumen,
                            duracionMin, genero.name(), ratingPromedio, anio, poster == null ? "" : poster);
                }
            }

            cachePeliculas.addAll(peliculaDAO.listarTodas());
            cachePeliculas.sort(Comparator.comparingDouble(Pelicula::getRatingPromedio).reversed());
            return cachePeliculas;

        } catch (IOException | RuntimeException e) {
            throw new CargaArchivoException("No se pudo importar el archivo de películas", e);
        }
    }

    public synchronized List<Pelicula> obtenerTopDiez(Path csvPath) throws CargaArchivoException {
        List<Pelicula> peliculas = cargarPeliculas(csvPath);
        return peliculas.subList(0, Math.min(10, peliculas.size()));
    }

    private BufferedReader abrirLectorCsv(Path csvPath) throws IOException {
        if (Files.exists(csvPath)) {
            return Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
        }

        InputStream recurso = getClass().getClassLoader()
                .getResourceAsStream(csvPath.getFileName().toString());
        if (recurso != null) {
            return new BufferedReader(new InputStreamReader(recurso, StandardCharsets.UTF_8));
        }

        throw new IOException("No se encontró el archivo CSV: " + csvPath.toAbsolutePath());
    }

    public synchronized List<Pelicula> obtenerDiezAleatoriasNoCalificadas(Path csvPath, List<Integer> idsYaCalificados)
            throws CargaArchivoException {
        List<Pelicula> peliculas = new ArrayList<>(cargarPeliculas(csvPath));
        peliculas.removeIf(p -> idsYaCalificados.contains(p.getId()));
        Collections.shuffle(peliculas, random);
        if (peliculas.isEmpty()) {
            peliculas = new ArrayList<>(cachePeliculas);
        }
        return peliculas.subList(0, Math.min(10, peliculas.size()));
    }

    private String[] separar(String linea) {
        return linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private String obtener(String[] datos, Map<String, Integer> indices, String clave) {
        Integer indice = indices.get(clave);
        if (indice != null && indice < datos.length) {
            return datos[indice].replace("\"", "").trim();
        }
        return null;
    }

    private int obtenerAnio(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return 0;
        }
        try {
            return LocalDate.parse(fecha).getYear();
        } catch (Exception e) {
            return 0;
        }
    }

    private Genero mapearGenero(String generoCsv) {
        if (generoCsv == null || generoCsv.isBlank()) {
            return Genero.DRAMA;
        }
        String genero = generoCsv.split("\\|")[0].trim().toUpperCase();
        try {
            return Genero.valueOf(genero);
        } catch (IllegalArgumentException e) {
            switch (genero) {
                case "ACTION" -> { return Genero.ACCION; }
                case "COMEDY" -> { return Genero.COMEDIA; }
                case "HORROR", "THRILLER" -> { return Genero.TERROR; }
                case "DOCUMENTARY" -> { return Genero.DOCUMENTAL; }
                default -> { return Genero.DRAMA; }
            }
        }
    }
}

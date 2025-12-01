package dao.implementacion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBD;
import dao.PeliculaDAO;
import enumerativo.Genero;
import modelo.Pelicula;

public class PeliculaDAOjdbc implements PeliculaDAO{
        public void insertar(String titulo, String director, String resumen, double duracion, String genero) {
        insertar(titulo, director, resumen, duracion, genero, 0.0, 0, "");
    }

    public void insertar(String titulo, String director, String resumen, double duracion, String genero,
                          double ratingPromedio, int anio, String poster) {
        String sql = "INSERT INTO PELICULA (GENERO, TITULO, RESUMEN, DIRECTOR, DURACION, RATING_PROMEDIO, ANIO, POSTER) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
                PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);

            ps.setString(1, genero);
            ps.setString(2, titulo);
            ps.setString(3, resumen);
            ps.setString(4, director);
            ps.setDouble(5, duracion);
            ps.setDouble(6, ratingPromedio);
            ps.setInt(7, anio);
            ps.setString(8, poster);
            ps.executeUpdate();
            System.out.println("✅ Película guardada correctamente.");

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar película: " + e.getMessage());
        }
    }

    public Pelicula buscarPorTitulo(String titulo) {
        String sql = "SELECT * FROM PELICULA WHERE TITULO = ?";
        try {
            PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
            ps.setString(1, titulo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Genero genero = Genero.valueOf(rs.getString("GENERO").toUpperCase());
                return new Pelicula(
                        rs.getInt("ID"),
                        rs.getString("TITULO"),
                        rs.getString("RESUMEN"),
                        rs.getString("DIRECTOR"),
                        genero,
                        rs.getDouble("DURACION"),
                        rs.getDouble("RATING_PROMEDIO"),
                        rs.getInt("ANIO"),
                        rs.getString("POSTER")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar película: " + e.getMessage());
        }
        return null;
    }

    public List<Pelicula> listarTodas() {
        List<Pelicula> lista = new ArrayList<Pelicula>();
        String sql = "SELECT * FROM PELICULA";
        try {
                Statement st = ConexionBD.getInstancia().getConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Genero genero = Genero.valueOf(rs.getString("GENERO").toUpperCase());
                Pelicula p = new Pelicula(
                        rs.getInt("ID"),
                        rs.getString("TITULO"),
                        rs.getString("RESUMEN"),
                        rs.getString("DIRECTOR"),
                        genero,
                        rs.getDouble("DURACION"),
                        rs.getDouble("RATING_PROMEDIO"),
                        rs.getInt("ANIO"),
                        rs.getString("POSTER")
                );
                lista.add(p);
            }
            st.close();

        } catch (SQLException e) {
            System.out.println("❌ Error al listar películas: " + e.getMessage());
        }
        return lista;
    }
}

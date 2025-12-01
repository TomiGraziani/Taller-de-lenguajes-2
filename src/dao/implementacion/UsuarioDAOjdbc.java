package dao.implementacion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexion.ConexionBD;
import dao.UsuarioDAO;
import modelo.DatosPersonales;
import modelo.Usuario;

public class UsuarioDAOjdbc implements UsuarioDAO{

        public void insertar(String nombreUsuario, String email, String contrasenia, int idDP) {
        String sql = "INSERT INTO USUARIO (NOMBRE_USUARIO, EMAIL, CONTRASENIA, ID_DATOS_PERSONALES) VALUES (?, ?, ?, ?)";
        try {
                PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);

                ps.setString(1, nombreUsuario);
                ps.setString(2, email);
                ps.setString(3, contrasenia);
                ps.setInt(4, idDP);
                ps.executeUpdate();
            System.out.println("✅ Usuario guardado correctamente.");

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar usuario: " + e.getMessage());
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<Usuario>();
        String sql = """
            SELECT U.ID, U.NOMBRE_USUARIO, U.EMAIL, U.CONTRASENIA,
                   D.ID AS DP_ID, D.NOMBRES, D.APELLIDO, D.DNI
            FROM USUARIO U
            JOIN DATOS_PERSONALES D ON U.ID_DATOS_PERSONALES = D.ID
            """;
        try {
                Statement sent = ConexionBD.getInstancia().getConexion().createStatement();
                ResultSet resul = sent.executeQuery(sql);

            while (resul.next()) {
                DatosPersonales dp = new DatosPersonales(
                                resul.getInt("DP_ID"),
                                resul.getString("NOMBRES"),
                                resul.getString("APELLIDO"),
                                resul.getLong("DNI")
                );

                Usuario u = new Usuario(
                                resul.getInt("DP_ID"),
                                dp,
                                resul.getString("NOMBRE_USUARIO"),
                                resul.getString("EMAIL"),
                                resul.getString("CONTRASENIA")

                );

                lista.add(u);
            }
            sent.close();

        } catch (SQLException e) {
            System.out.println("❌ Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public Usuario buscarPorCredenciales(String email, String contrasenia) {
        String sql = """
            SELECT U.ID, U.NOMBRE_USUARIO, U.EMAIL, U.CONTRASENIA,
                   D.ID AS DP_ID, D.NOMBRES, D.APELLIDO, D.DNI
            FROM USUARIO U
            JOIN DATOS_PERSONALES D ON U.ID_DATOS_PERSONALES = D.ID
            WHERE U.EMAIL = ? AND U.CONTRASENIA = ?
        """;
        try {
                PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, contrasenia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                DatosPersonales dp = new DatosPersonales(
                        rs.getInt("DP_ID"),
                        rs.getString("NOMBRES"),
                        rs.getString("APELLIDO"),
                        rs.getInt("DNI")
                );
                return new Usuario(
                        rs.getInt("ID"),
                        dp,
                        rs.getString("NOMBRE_USUARIO"),
                        rs.getString("EMAIL"),
                        rs.getString("CONTRASENIA")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL = ?";
        try {
            PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("ID"), null, rs.getString("NOMBRE_USUARIO"), rs.getString("EMAIL"), rs.getString("CONTRASENIA"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar email: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM USUARIO WHERE NOMBRE_USUARIO = ?";
        try {
            PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql);
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("ID"), null, rs.getString("NOMBRE_USUARIO"), rs.getString("EMAIL"), rs.getString("CONTRASENIA"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar nombre de usuario: " + e.getMessage());
        }
        return null;
    }
}

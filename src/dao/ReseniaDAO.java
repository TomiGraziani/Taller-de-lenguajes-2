package dao;

import java.util.List;

import modelo.Resenia;

public interface ReseniaDAO {

    void insertar(int calificaion, String comentario, int aprobado, String fechaHora, int idU, int idP);

    List<Resenia> listarNoAprobadas();

    void aprobar(int idResenia);

    List<Resenia> listarPorUsuario(int idUsuario);

    boolean existeResenia(int idUsuario, int idPelicula);
}

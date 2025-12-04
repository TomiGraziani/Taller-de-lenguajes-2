package main;
import conexion.ConexionBD;
import controlador.PlataformaController;
import dao.FactoryDAO;
import servicio.PlataformaService;
import tablas.TablasBD;
import vista.ConsolaView;

import java.sql.Connection;

public class Main {
        public static void main(String[] args) {
                Connection cx = ConexionBD.getInstancia().getConexion();
                TablasBD tablas = new TablasBD();
                FactoryDAO factory = new FactoryDAO();
                PlataformaService servicio = new PlataformaService(factory);
                ConsolaView vista = new ConsolaView();
                PlataformaController controlador = new PlataformaController(servicio, vista);

                try {
                        tablas.crearTablas(cx);
                        controlador.iniciar();
                } catch(Exception e) {
                        System.out.println("Error: "+ e);
                } finally {
                        ConexionBD.getInstancia().desconectar();
                        System.out.println("Fin del programa.");
                }
        }
}

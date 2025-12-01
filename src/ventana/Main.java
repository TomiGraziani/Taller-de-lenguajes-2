package ventana;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import conexion.ConexionBD;
import dao.FactoryDAO;
import modelo.Usuario;
import tablas.TablasBD;

import java.sql.Connection;

public class Main extends JFrame{

        private static final long serialVersionUID = 1L;

        private FactoryDAO factory;

        private Panel panelDer;
        private JPanel panelIzq;
        private CardLayout cardLayout;

        public Main(FactoryDAO factory) {
                this.factory = factory;
                cargarFuenteRoboto();
                inicializarComponentes();
        }

        public void inicializarComponentes() {
                setLayout(new GridBagLayout());

                panelDer = new Panel();
                panelIzq = new JPanel();
                // Configuramos el CardLayout
                cardLayout = new CardLayout();
                panelIzq.setLayout(cardLayout);

        // Agregamos las ventanas al contenedor
                panelIzq.add(new PanelLogin(this, this.factory.getUsuarioDAO()), "LOGIN");
                panelIzq.add(new PanelRegistro(this, this.factory.getDatosDAO(), this.factory.getUsuarioDAO()), "REGISTRO");


        GridBagConstraints c = new GridBagConstraints();

                // ===== Panel Izquierdo (60%) =====
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.6;     // 60% del espacio horizontal
        c.weighty = 1.0;     // ocupa toda la altura
        c.fill = GridBagConstraints.BOTH;
        add(panelDer, c);

        // ===== Panel Derecho (40%) =====
        c.gridx = 1;
        c.weightx = 0.4;     // 40% del espacio horizontal
        add(panelIzq, c);


                // Configuración final de la ventana
                setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana
        setVisible(true);
        }

        public void mostrarLogin() {
        cardLayout.show(panelIzq, "LOGIN");
    }

    public void mostrarRegistro() {
        cardLayout.show(panelIzq, "REGISTRO");
    }

    public void abrirPlataforma(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPlataforma(usuario, factory.getPeliculaDAO(), factory.getReseniaDAO()).setVisible(true);
            dispose();
        });
    }

    private void cargarFuenteRoboto() {
        try {
            Font roboto = Font.createFont(
                    Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/fuente/Roboto-Regular.ttf")
            ).deriveFont(14f);

            UIManager.put("Button.font", roboto);
            UIManager.put("Label.font", roboto);
            UIManager.put("TextField.font", roboto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        // Inicializar la BD
        Connection cx = ConexionBD.getInstancia().getConexion();
        TablasBD tablas = new TablasBD();
        FactoryDAO factory = new FactoryDAO();

        try {
            tablas.crearTablas(cx);
        } catch(Exception e) {
            System.out.println("Error: "+ e);
        }

        // Lanzar la GUI
        SwingUtilities.invokeLater(() -> {
            new Main(factory).setVisible(true);
        });

        // Al finalizar todo el programa, registrás un shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cerrando conexión...");
            ConexionBD.getInstancia().desconectar();
        }));
    }

}

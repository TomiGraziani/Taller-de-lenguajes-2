package ventana;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import dao.implementacion.ReseniaDAOjdbc;
import dao.implementacion.PeliculaDAOjdbc;
import excepcion.ConsultaApiException;
import excepcion.DatosIncompletosException;
import modelo.Pelicula;
import modelo.Usuario;
import servicio.ConsultaOMDbService;
import servicio.ImportadorPeliculas;
import servicio.ConsultaOMDbService.ResultadoOMDb;

public class VentanaPlataforma extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Usuario usuario;
    private final PeliculaDAOjdbc peliculaDAO;
    private final ReseniaDAOjdbc reseniaDAO;
    private final ImportadorPeliculas importador;
    private final ConsultaOMDbService omdbService = new ConsultaOMDbService();
    private final java.nio.file.Path csvPath = java.nio.file.Paths.get("src/movies_database.csv");

    private JTable tablaPeliculas;
    private DefaultTableModel modeloTabla;
    private CardLayout layoutCentro;
    private JPanel contenedorCentro;
    private JLabel estadoLabel;
    private JTextField campoBusqueda;
    private JTextArea areaResultadoBusqueda;

    public VentanaPlataforma(Usuario usuario, PeliculaDAOjdbc peliculaDAO, ReseniaDAOjdbc reseniaDAO) {
        this.usuario = usuario;
        this.peliculaDAO = peliculaDAO;
        this.reseniaDAO = reseniaDAO;
        this.importador = new ImportadorPeliculas(peliculaDAO);
        inicializarUI();
        cargarPeliculas();
    }

    private void inicializarUI() {
        setTitle("Plataforma de Streaming - Bienvenida");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        JLabel lblBienvenida = new JLabel("Bienvenido/a, " + usuario.getNombreUsuario());
        lblBienvenida.setFont(new Font("Roboto", Font.BOLD, 16));

        JLabel lblEmail = new JLabel(usuario.getEmail());
        lblEmail.setForeground(Color.DARK_GRAY);

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new Main(new dao.FactoryDAO()).setVisible(true));
        });

        campoBusqueda = new JTextField(20);

        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
        panel.add(lblBienvenida, c);
        c.gridy = 1;
        panel.add(lblEmail, c);

        c.gridx = 1; c.gridy = 0; c.gridheight = 2; c.anchor = GridBagConstraints.CENTER;
        panel.add(crearPanelBusqueda(), c);

        c.gridx = 2; c.gridy = 0; c.gridheight = 2; c.anchor = GridBagConstraints.EAST;
        panel.add(btnCerrarSesion, c);

        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);

        JLabel lblBuscar = new JLabel("Buscar película (OMDb):");
        areaResultadoBusqueda = new JTextArea(4, 28);
        areaResultadoBusqueda.setEditable(false);
        areaResultadoBusqueda.setLineWrap(true);
        areaResultadoBusqueda.setWrapStyleWord(true);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarOmdb());

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        panel.add(lblBuscar, c);
        c.gridy = 1; c.gridwidth = 1;
        panel.add(campoBusqueda, c);
        c.gridx = 1;
        panel.add(btnBuscar, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        panel.add(new JScrollPane(areaResultadoBusqueda), c);

        return panel;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());

        layoutCentro = new CardLayout();
        contenedorCentro = new JPanel(layoutCentro);

        JPanel panelLoading = new JPanel(new BorderLayout());
        panelLoading.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        estadoLabel = new JLabel("Un momento por favor... cargando películas", JLabel.CENTER);
        estadoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        panelLoading.add(estadoLabel, BorderLayout.CENTER);

        JPanel panelTabla = new JPanel(new BorderLayout());
        modeloTabla = new DefaultTableModel(new Object[]{"Título", "Género", "Año", "Rating", "Poster"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPeliculas = new JTable(modeloTabla);
        tablaPeliculas.setRowHeight(28);
        panelTabla.add(new JScrollPane(tablaPeliculas), BorderLayout.CENTER);

        JButton btnCalificar = new JButton("Calificar selección");
        btnCalificar.addActionListener(e -> calificarSeleccion());
        panelTabla.add(btnCalificar, BorderLayout.SOUTH);

        contenedorCentro.add(panelLoading, "LOADING");
        contenedorCentro.add(panelTabla, "TABLA");

        cuerpo.add(contenedorCentro, BorderLayout.CENTER);
        return cuerpo;
    }

    private void cargarPeliculas() {
        layoutCentro.show(contenedorCentro, "LOADING");
        SwingWorker<List<Pelicula>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Pelicula> doInBackground() throws Exception {
                List<Integer> calificadas = reseniaDAO.listarPorUsuario(usuario.getId()).stream()
                        .map(r -> r.getIdPelicula()).collect(Collectors.toList());
                if (calificadas.isEmpty()) {
                    return importador.obtenerTopDiez(csvPath);
                }
                return importador.obtenerDiezAleatoriasNoCalificadas(csvPath, calificadas);
            }

            @Override
           
            protected void done() {
                try {
                    List<Pelicula> pelis = get();
                    poblarTabla(pelis);
                    layoutCentro.show(contenedorCentro, "TABLA");
                } catch (Exception e) {
                    e.printStackTrace();  // ⬅️ ESTO ES OBLIGATORIO
                    estadoLabel.setText("Error al cargar películas: " + e.getMessage());
                }
            }

        };
        worker.execute();
    }

    private void poblarTabla(List<Pelicula> peliculas) {
        modeloTabla.setRowCount(0);
        try {
            for (Pelicula p : peliculas) {
                modeloTabla.addRow(new Object[]{
                    p.getTitulo(),
                    p.getGenero(),
                    p.getAnio(),
                    p.getRatingPromedio(),
                    p.getPoster()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();  // ⬅️ ESTO ES LO QUE NECESITAMOS
        }
    }


    private void calificarSeleccion() {
        int fila = tablaPeliculas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una película para calificar");
            return;
        }

        String titulo = (String) modeloTabla.getValueAt(fila, 0);
        Pelicula pelicula = peliculaDAO.buscarPorTitulo(titulo);
        if (pelicula == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la película seleccionada");
            return;
        }

        if (reseniaDAO.existeResenia(usuario.getId(), pelicula.getId())) {
            JOptionPane.showMessageDialog(this, "Ya calificó esta película");
            return;
        }

        mostrarDialogoCalificacion(pelicula);
    }

    private void mostrarDialogoCalificacion(Pelicula pelicula) {
        JDialog dialogo = new JDialog(this, "Calificar " + pelicula.getTitulo(), true);
        dialogo.setSize(new Dimension(400, 300));
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.gridx = 0; c.gridy = 0;
        centro.add(new JLabel("Puntaje (1-10):"), c);
        c.gridx = 1;
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        centro.add(spinner, c);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        centro.add(new JLabel("Comentario:"), c);
        c.gridy = 2;
        JTextArea area = new JTextArea(5, 25);
        centro.add(new JScrollPane(area), c);

        dialogo.add(centro, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(ev -> {
            try {
                String comentario = area.getText().trim();
                int calificacion = (int) spinner.getValue();
                if (comentario.isEmpty()) {
                    throw new DatosIncompletosException("Debe ingresar un comentario");
                }
                String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                reseniaDAO.insertar(calificacion, comentario, 0, fecha, usuario.getId(), pelicula.getId());
                JOptionPane.showMessageDialog(dialogo, "Calificación guardada");
                dialogo.dispose();
            } catch (DatosIncompletosException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage());
            }
        });
        dialogo.add(btnGuardar, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private void buscarOmdb() {
        String titulo = campoBusqueda.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un título a buscar");
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private ResultadoOMDb resultado;
            private Exception error;
            @Override
            protected Void doInBackground() {
                try {
                    resultado = omdbService.consultarPelicula(titulo);
                } catch (ConsultaApiException e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    areaResultadoBusqueda.setText(error.getMessage());
                } else if (resultado != null) {
                    areaResultadoBusqueda.setText("Título: " + resultado.titulo() + "\n" +
                            "Año: " + resultado.anio() + "\n" +
                            "Sinopsis: " + resultado.sinopsis());
                }
            }
        };
        worker.execute();
    }
}

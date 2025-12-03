package ventana;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import dao.implementacion.ReseniaDAOjdbc;
import dao.implementacion.PeliculaDAOjdbc;
import excepcion.ConsultaApiException;
import excepcion.DatosIncompletosException;
import modelo.Pelicula;
import modelo.Usuario;
import servicio.ConsultaOMDbService;
import servicio.ImportadorPeliculas;
import servicio.ConsultaOMDbService.ResultadoOMDb;
import enumerativo.Genero;

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
    private JLabel iconoCarga;
    private JProgressBar barraProgreso;
    private JTextField campoBusqueda;
    private ImageIcon posterPlaceholder;
    private final Set<Integer> peliculasCalificadas = new HashSet<>();
    private final Map<String, Pelicula> peliculasPorTitulo = new HashMap<>();

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel izquierda = new JPanel(new GridBagLayout());
        izquierda.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.WEST;

        JLabel lblBienvenida = new JLabel("Bienvenido/a a la Plataforma de Streaming");
        lblBienvenida.setFont(new Font("Roboto", Font.BOLD, 20));
        JLabel lblSubtitulo = new JLabel("Seguro viste alguna de estas películas, cuéntanos qué te parecieron.");
        lblSubtitulo.setForeground(Color.DARK_GRAY);

        c.gridx = 0; c.gridy = 0;
        izquierda.add(lblBienvenida, c);
        c.gridy = 1;
        izquierda.add(lblSubtitulo, c);

        panel.add(izquierda, BorderLayout.WEST);

        panel.add(crearPanelBusqueda(), BorderLayout.CENTER);
        panel.add(crearPanelUsuario(), BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBuscar = new JLabel("Buscar película (OMDb):");

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarOmdb());

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        panel.add(lblBuscar, c);
        c.gridy = 1; c.gridwidth = 1; c.weightx = 1.0;
        campoBusqueda = new JTextField(20);
        panel.add(campoBusqueda, c);
        c.gridx = 1; c.weightx = 0;
        panel.add(btnBuscar, c);

        return panel;
    }

    private JPanel crearPanelUsuario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0; c.gridy = 0;

        JLabel lblNombre = new JLabel(usuario.getNombreUsuario());
        lblNombre.setFont(new Font("Roboto", Font.BOLD, 14));
        JLabel lblEmail = new JLabel(usuario.getEmail());
        lblEmail.setForeground(Color.DARK_GRAY);

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new Main(new dao.FactoryDAO()).setVisible(true));
        });

        panel.add(lblNombre, c);
        c.gridy = 1;
        panel.add(lblEmail, c);
        c.gridy = 2;
        panel.add(btnCerrarSesion, c);
        return panel;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 10));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        layoutCentro = new CardLayout();
        contenedorCentro = new JPanel(layoutCentro);

        JPanel panelLoading = new JPanel(new BorderLayout());
        panelLoading.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JPanel contenidoLoading = new JPanel();
        contenidoLoading.setOpaque(false);
        contenidoLoading.setLayout(new BoxLayout(contenidoLoading, BoxLayout.Y_AXIS));

        iconoCarga = new JLabel(crearIconoCarga());
        iconoCarga.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconoCarga.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        estadoLabel = new JLabel("Un momento por favor... cargando películas", JLabel.CENTER);
        estadoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        estadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("🔒 Iniciando sesión y preparando tu catálogo", JLabel.CENTER);
        loginLabel.setFont(new Font("Roboto", Font.PLAIN, 15));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        barraProgreso = new JProgressBar();
        barraProgreso.setIndeterminate(true);
        barraProgreso.setStringPainted(true);
        barraProgreso.setString("Preparando recomendaciones...");
        barraProgreso.setForeground(new Color(0, 139, 139));
        barraProgreso.setPreferredSize(new Dimension(320, 24));
        barraProgreso.setBorder(BorderFactory.createEmptyBorder(12, 40, 0, 40));
        barraProgreso.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenidoLoading.add(Box.createVerticalGlue());
        contenidoLoading.add(iconoCarga);
        contenidoLoading.add(estadoLabel);
        contenidoLoading.add(Box.createVerticalStrut(8));
        contenidoLoading.add(loginLabel);
        contenidoLoading.add(Box.createVerticalStrut(12));
        contenidoLoading.add(barraProgreso);
        contenidoLoading.add(Box.createVerticalGlue());
        panelLoading.add(contenidoLoading, BorderLayout.CENTER);

        JPanel panelTabla = new JPanel(new BorderLayout());
        modeloTabla = new DefaultTableModel(new Object[]{"Poster", "Título", "Género", "Resumen", "Acción"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column != 4) return false;
                Object value = getValueAt(row, column);
                if (value instanceof AccionCalificar accion) {
                    return accion.habilitado();
                }
                return true;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> ImageIcon.class;
                    case 2 -> Genero.class;
                    case 4 -> JButton.class;
                    default -> String.class;
                };
            }
        };
        tablaPeliculas = new JTable(modeloTabla);
        tablaPeliculas.setRowHeight(140);
        tablaPeliculas.setShowGrid(false);
        tablaPeliculas.setFillsViewportHeight(true);
        tablaPeliculas.setIntercellSpacing(new Dimension(10, 10));
        habilitarOrdenamientoTabla();
        configurarColumnasTabla();
        panelTabla.add(new JScrollPane(tablaPeliculas), BorderLayout.CENTER);

        contenedorCentro.add(panelLoading, "LOADING");
        contenedorCentro.add(panelTabla, "TABLA");
        cuerpo.add(crearDescripcionInicial(), BorderLayout.NORTH);
        cuerpo.add(contenedorCentro, BorderLayout.CENTER);
        return cuerpo;
    }

    private JComponent crearDescripcionInicial() {
        JTextArea area = new JTextArea("Califica tus películas favoritas y ordénalas por título o género usando las cabeceras de la tabla.");
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setFont(new Font("Roboto", Font.PLAIN, 13));
        return area;
    }

    private void configurarColumnasTabla() {
        TableColumn posterColumn = tablaPeliculas.getColumnModel().getColumn(0);
        posterColumn.setPreferredWidth(120);
        posterColumn.setCellRenderer(new PosterRenderer());

        TableColumn tituloColumn = tablaPeliculas.getColumnModel().getColumn(1);
        tituloColumn.setPreferredWidth(200);

        TableColumn generoColumn = tablaPeliculas.getColumnModel().getColumn(2);
        generoColumn.setPreferredWidth(100);
        generoColumn.setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
                return comp;
            }
        });

        TableColumn resumenColumn = tablaPeliculas.getColumnModel().getColumn(3);
        resumenColumn.setPreferredWidth(380);
        resumenColumn.setCellRenderer(new MultiLineRenderer());

        TableColumn accionColumn = tablaPeliculas.getColumnModel().getColumn(4);
        accionColumn.setPreferredWidth(120);
        accionColumn.setCellRenderer(new ButtonRenderer());
        accionColumn.setCellEditor(new ButtonEditor());
    }

    private void habilitarOrdenamientoTabla() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        sorter.setSortable(0, false);
        sorter.setSortable(3, false);
        sorter.setSortable(4, false);
        sorter.setComparator(2, Comparator.comparing(Genero::name, String.CASE_INSENSITIVE_ORDER));
        tablaPeliculas.setRowSorter(sorter);
    }

    private void cargarPeliculas() {
        layoutCentro.show(contenedorCentro, "LOADING");
        SwingWorker<List<Pelicula>, Void> worker = new SwingWorker<>() {
            private List<Integer> calificadas;
            @Override
            protected List<Pelicula> doInBackground() throws Exception {
                calificadas = reseniaDAO.listarPorUsuario(usuario.getId()).stream()
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
                    peliculasCalificadas.clear();
                    if (calificadas != null) {
                        peliculasCalificadas.addAll(calificadas);
                    }
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
        peliculasPorTitulo.clear();
        if (posterPlaceholder == null) {
            posterPlaceholder = crearPlaceholder();
        }
        try {
            for (Pelicula p : peliculas) {
                peliculasPorTitulo.put(p.getTitulo(), p);
                boolean yaCalificada = peliculasCalificadas.contains(p.getId());
                modeloTabla.addRow(new Object[]{
                    cargarPoster(p.getPoster()),
                    p.getTitulo(),
                    p.getGenero(),
                    resumir(p.getSinopsis()),
                    new AccionCalificar("Calificar", !yaCalificada)
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
        calificarFila(fila);
    }

    private void calificarFila(int fila) {
        int filaModelo = tablaPeliculas.convertRowIndexToModel(fila);
        String titulo = (String) modeloTabla.getValueAt(filaModelo, 1);
        Pelicula pelicula = peliculasPorTitulo.getOrDefault(titulo, peliculaDAO.buscarPorTitulo(titulo));
        if (pelicula == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la película seleccionada");
            return;
        }

        if (peliculasCalificadas.contains(pelicula.getId()) || reseniaDAO.existeResenia(usuario.getId(), pelicula.getId())) {
            JOptionPane.showMessageDialog(this, "Ya calificó esta película");
            return;
        }

        mostrarDialogoCalificacion(pelicula, filaModelo);
    }

    private void mostrarDialogoCalificacion(Pelicula pelicula, int filaTabla) {
        JDialog dialogo = new JDialog(this, "Calificar " + pelicula.getTitulo(), true);
        dialogo.setSize(new Dimension(500, 380));
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JLabel titulo = new JLabel(pelicula.getTitulo());
        titulo.setFont(new Font("Roboto", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));
        dialogo.add(titulo, BorderLayout.NORTH);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.putClientProperty("calificacion", 0);

        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
        centro.add(new JLabel("Calificación:"), c);
        c.gridx = 1;
        JPanel panelEstrellas = crearSelectorEstrellas(btnGuardar);
        centro.add(panelEstrellas, c);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        centro.add(new JLabel("Comentario:"), c);
        c.gridy = 2;
        JTextArea area = new JTextArea(6, 32);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        centro.add(new JScrollPane(area), c);

        dialogo.add(centro, BorderLayout.CENTER);

        btnGuardar.addActionListener(ev -> {
            try {
                String comentario = area.getText().trim();
                int calificacion = (int) btnGuardar.getClientProperty("calificacion");
                if (comentario.isEmpty()) {
                    throw new DatosIncompletosException("Debe ingresar un comentario");
                }
                if (calificacion == 0) {
                    throw new DatosIncompletosException("Seleccione una calificación");
                }
                String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                reseniaDAO.insertar(calificacion, comentario, 0, fecha, usuario.getId(), pelicula.getId());
                peliculasCalificadas.add(pelicula.getId());
                modeloTabla.setValueAt(new AccionCalificar("Calificar", false), filaTabla, 4);
                JOptionPane.showMessageDialog(dialogo, "Calificación guardada");
                dialogo.dispose();
            } catch (DatosIncompletosException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage());
            }
        });

        JPanel pie = new JPanel();
        pie.add(btnGuardar);
        dialogo.add(pie, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private JPanel crearSelectorEstrellas(JButton btnGuardar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JLabel[] estrellas = new JLabel[5];

        for (int i = 0; i < estrellas.length; i++) {
            JLabel lbl = new JLabel("☆");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 28));
            lbl.setForeground(new Color(0, 139, 139));
            int valor = i + 1;
            lbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    btnGuardar.putClientProperty("calificacion", valor);
                    actualizarEstrellas(estrellas, valor);
                }
            });
            estrellas[i] = lbl;
            panel.add(lbl);
        }

        actualizarEstrellas(estrellas, 0);
        return panel;
    }

    private void actualizarEstrellas(JLabel[] estrellas, int seleccion) {
        for (int i = 0; i < estrellas.length; i++) {
            boolean activa = i < seleccion;
            estrellas[i].setText(activa ? "★" : "☆");
            estrellas[i].setForeground(activa ? new Color(255, 193, 7) : new Color(0, 139, 139));
        }
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
                mostrarVentanaResultado(titulo, resultado, error);
            }
        };
        worker.execute();
    }

    private void mostrarVentanaResultado(String tituloBuscado, ResultadoOMDb resultado, Exception error) {
        boolean encontrado = error == null && resultado != null;
        String titulo = encontrado ? resultado.titulo() : tituloBuscado;
        String anio = encontrado ? resultado.anio() : "no se encuentra disponible";
        String resumen = encontrado ? resultado.sinopsis() : "no se encuentra disponible";

        JDialog dialogo = new JDialog(this, "Plataforma de Streaming - Información", true);
        dialogo.setSize(new Dimension(460, 320));
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 10, 6, 10);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
        JLabel lblTitulo = new JLabel("Título de la Película");
        lblTitulo.setFont(new Font("Roboto", Font.BOLD, 16));
        contenido.add(lblTitulo, c);

        c.gridy = 1;
        JLabel lblNombre = new JLabel(titulo);
        lblNombre.setFont(new Font("Roboto", Font.PLAIN, 14));
        contenido.add(lblNombre, c);

        c.gridy = 2;
        contenido.add(new JLabel("Año: " + anio), c);

        c.gridy = 3;
        contenido.add(new JLabel("Resumen:"), c);
        c.gridy = 4;
        JTextArea area = new JTextArea(5, 28);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setText(resumen);
        contenido.add(new JScrollPane(area), c);

        dialogo.add(contenido, BorderLayout.CENTER);

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.addActionListener(e -> dialogo.dispose());
        JPanel pie = new JPanel();
        pie.add(btnContinuar);
        dialogo.add(pie, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private ImageIcon cargarPoster(String urlPoster) {
        try {
            if (urlPoster == null || urlPoster.isBlank()) {
                return posterPlaceholder;
            }
            Image imagen = ImageIO.read(new URL(urlPoster));
            if (imagen == null) {
                return posterPlaceholder;
            }
            Image escalada = imagen.getScaledInstance(90, 120, Image.SCALE_SMOOTH);
            return new ImageIcon(escalada);
        } catch (Exception e) {
            return posterPlaceholder;
        }
    }

    private ImageIcon crearIconoCarga() {
        int tamano = 140;
        BufferedImage img = new BufferedImage(tamano, tamano, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(0, 139, 139));
        g.drawArc(16, 16, tamano - 32, tamano - 32, 0, 300);
        g.setColor(new Color(0, 139, 139, 90));
        g.drawArc(16, 16, tamano - 32, tamano - 32, 310, 50);
        g.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon crearPlaceholder() {
        int ancho = 90;
        int alto = 120;
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(235, 235, 235));
        g.fillRect(0, 0, ancho, alto);
        g.setColor(new Color(180, 180, 180));
        g.drawRect(2, 2, ancho - 4, alto - 4);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Sin poster", 12, alto / 2);
        g.dispose();
        return new ImageIcon(img);
    }

    private String resumir(String texto) {
        if (texto == null) {
            return "Sin resumen";
        }
        int limite = 240;
        if (texto.length() <= limite) {
            return texto;
        }
        return texto.substring(0, limite) + "...";
    }

    private record AccionCalificar(String texto, boolean habilitado) {}

    private class PosterRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(CENTER);
            label.setVerticalAlignment(CENTER);
            label.setIcon(value instanceof ImageIcon ? (ImageIcon) value : posterPlaceholder);
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return label;
        }
    }

    private class MultiLineRenderer extends JTextArea implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        public MultiLineRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Roboto", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.DARK_GRAY);
            }
            int preferredHeight = getPreferredSize().height + 20;
            if (table.getRowHeight(row) < preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }
            return this;
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Roboto", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            AccionCalificar accion = value instanceof AccionCalificar
                    ? (AccionCalificar) value
                    : new AccionCalificar(value == null ? "" : value.toString(), true);
            setText(accion.texto());
            setForeground(Color.WHITE);
            if (accion.habilitado()) {
                setBackground(new Color(0, 139, 139));
                setEnabled(true);
            } else {
                setBackground(new Color(128, 128, 128, 200));
                setEnabled(false);
            }
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
        private final JButton button;
        private int rowIndex;

        public ButtonEditor() {
            super(new JTextField());
            setClickCountToStart(1);
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 139, 139));
            button.addActionListener(e -> {
                if (!button.isEnabled()) {
                    return;
                }
                fireEditingStopped();
                calificarFila(rowIndex);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            AccionCalificar accion = value instanceof AccionCalificar
                    ? (AccionCalificar) value
                    : new AccionCalificar(value == null ? "" : value.toString(), true);
            button.setText(accion.texto());
            button.setEnabled(accion.habilitado());
            button.setBackground(accion.habilitado() ? new Color(0, 139, 139) : new Color(128, 128, 128, 200));
            rowIndex = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}

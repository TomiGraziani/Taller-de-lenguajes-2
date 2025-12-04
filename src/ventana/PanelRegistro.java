package ventana;
import javax.swing.*;

import java.awt.*;

public class PanelRegistro extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel norte;
	private JPanel centro;
	private JPanel sur;

	private JLabel titulo;
	private JLabel label;
    private JButton btnVolver;

    public PanelRegistro() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        norte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centro = new FormularioRegistro();
        sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    	titulo = new JLabel("Formulario de Registro");
    	titulo.setFont(new Font("Roboto", Font.BOLD, 26));
    	titulo.setForeground(new Color(30, 30, 30));
    	
    	label = new JLabel("¿Ya te habías registrado?");
    	
    	btnVolver = new JButton("Volver");
    	btnVolver.setBorderPainted(false);
    	btnVolver.setOpaque(false);
    	btnVolver.setContentAreaFilled(false);
    	btnVolver.setForeground(Color.BLUE);
    	btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	btnVolver.setFocusPainted(false);

    	norte.add(titulo);
    	sur.add(label);
    	sur.add(btnVolver);

    	add(norte, BorderLayout.NORTH);
    	add(centro, BorderLayout.CENTER);
    	add(sur, BorderLayout.SOUTH);
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }

    public FormularioRegistro getFormularioRegistro() {
        return (FormularioRegistro) centro;
    }

    public void cambiarColorVolver(Color color) {
        btnVolver.setForeground(color);
    }
}

package ventana;
import javax.swing.*;

import java.awt.*;

public class PanelLogin extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JPanel norte;
	private JPanel centro;
	private JPanel sur;

	private JLabel titulo;
	private JLabel label;
    private JButton btnRegistrar;

    public PanelLogin() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        norte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centro = new FormularioLogin();
        sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    	titulo = new JLabel("Inicio de sesion");
    	titulo.setFont(new Font("Roboto", Font.BOLD, 26));
    	titulo.setForeground(new Color(30, 30, 30));
    	
    	label = new JLabel("¿Aun no sos usuario?");
    	
    	btnRegistrar = new JButton("Registrate");
    	btnRegistrar.setBorderPainted(false);
    	btnRegistrar.setOpaque(false);
    	btnRegistrar.setContentAreaFilled(false);
    	btnRegistrar.setForeground(Color.BLUE);
    	btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    	btnRegistrar.setFocusPainted(false);

    	norte.add(titulo);
    	sur.add(label);
    	sur.add(btnRegistrar);

    	add(norte, BorderLayout.NORTH);
    	add(centro, BorderLayout.CENTER);
    	add(sur, BorderLayout.SOUTH);
    }

    public JButton getBtnRegistrar() {
        return btnRegistrar;
    }

    public FormularioLogin getFormularioLogin() {
        return (FormularioLogin) centro;
    }

    public void cambiarColorRegistrar(Color color) {
        btnRegistrar.setForeground(color);
    }
}
    
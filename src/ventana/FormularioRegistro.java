package ventana;

import java.awt.*;

import javax.swing.*;

import componente.CampoValidable;
import dto.RegistroFormularioDTO;
import validacion.*;

public class FormularioRegistro extends JPanel {
	private static final long serialVersionUID = 1L;

	private JLabel labelNombre;
	private JLabel labelApellido;
	private JLabel labelDNI;
    private JLabel labelNombreUsuario;
    private JLabel labelEmail;
    private JLabel labelContrasenia;
    
	private JTextField campoNombre;
	private JTextField campoApellido;
	private JTextField campoDNI;
    private JTextField campoNombreUsuario;
    private JTextField campoEmail;
    private JPasswordField campoContrasenia;
    
    private JLabel errorNombre;
    private JLabel errorApellido;
    private JLabel errorDNI;
    private JLabel errorNombreUsuario;
    private JLabel errorEmail;
    private JLabel errorContrasenia;
    
    private final Color colorError = new Color(237, 64, 64);
    
    private JButton btnRegistrar;

    public FormularioRegistro() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
    	labelNombre = new JLabel("Nombre:");
    	labelApellido = new JLabel("Apellido:");
    	labelDNI = new JLabel("DNI:");
    	labelNombreUsuario = new JLabel("Nombre de Usuario:");
        labelEmail = new JLabel("Email:");
        labelContrasenia = new JLabel("Contraseña:");
        
        labelNombre.setHorizontalAlignment(SwingConstants.RIGHT);
        labelApellido.setHorizontalAlignment(SwingConstants.RIGHT);
        labelDNI.setHorizontalAlignment(SwingConstants.RIGHT);
        labelNombreUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
        labelEmail.setHorizontalAlignment(SwingConstants.RIGHT);
        labelContrasenia.setHorizontalAlignment(SwingConstants.RIGHT);
        
    	campoNombre = new JTextField(15);
    	campoApellido = new JTextField(15);
    	campoDNI = new JTextField(15);
    	campoNombreUsuario = new JTextField(15);
    	campoEmail = new JTextField(15);
        campoContrasenia = new JPasswordField(15);
        
        errorNombre = new JLabel();
        errorApellido = new JLabel();
        errorDNI = new JLabel();
        errorNombreUsuario = new JLabel();
        errorEmail = new JLabel();
        errorContrasenia = new JLabel();

        //Color de tipofrafia
        errorNombre.setForeground(colorError);
        errorApellido.setForeground(colorError);
        errorDNI.setForeground(colorError);
        errorNombreUsuario.setForeground(colorError);
        errorEmail.setForeground(colorError);
        errorContrasenia.setForeground(colorError);
        
        //Tamaño de tipografia
        errorNombre.setFont(errorNombre.getFont().deriveFont(12f));
        errorApellido.setFont(errorApellido.getFont().deriveFont(12f));
        errorDNI.setFont(errorDNI.getFont().deriveFont(12f));
        errorNombreUsuario.setFont(errorNombreUsuario.getFont().deriveFont(12f));
        errorEmail.setFont(errorEmail.getFont().deriveFont(12f));
        errorContrasenia.setFont(errorContrasenia.getFont().deriveFont(12f));
        
        new CampoValidable(campoNombre, errorNombre, new ValidadorNombre(), colorError);
        new CampoValidable(campoApellido, errorApellido, new ValidadorNombre(), colorError);
        new CampoValidable(campoDNI, errorDNI, new ValidadorDNI(), colorError);
        new CampoValidable(campoNombreUsuario, errorNombreUsuario, new ValidadorNombre(), colorError);
        new CampoValidable(campoEmail, errorEmail, new ValidadorEmail(), colorError);
        new CampoValidable(campoContrasenia, errorContrasenia, new ValidadorContrasenia(), colorError);

        btnRegistrar = new JButton("Crear cuenta");
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;

        // Campos
        fila = agregarCampo(c, fila, labelNombre, campoNombre, errorNombre);
        fila = agregarCampo(c, fila, labelApellido, campoApellido, errorApellido);
        fila = agregarCampo(c, fila, labelDNI, campoDNI, errorDNI);
        fila = agregarCampo(c, fila, labelNombreUsuario, campoNombreUsuario, errorNombreUsuario);
        fila = agregarCampo(c, fila, labelEmail, campoEmail, errorEmail);
        fila = agregarCampo(c, fila, labelContrasenia, campoContrasenia, errorContrasenia);

        // Boton
        c.gridx = 1; c.gridy = fila;
        c.weightx = 0;
        add(btnRegistrar, c);
    }

    private int agregarCampo(GridBagConstraints c, int fila,
    		JLabel label, JComponent input, JLabel errorLabel) {

        // LABEL
    	c.insets = new Insets(6, 60, 6, 6);
    	c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        add(label, c);

        // INPUT
        c.insets = new Insets(6, 6, 6, 150);
        c.gridx = 1;
        c.weightx = 1.0;
        add(input, c);

        // ERROR LABEL
        fila++;
        c.gridx = 1;
        c.gridy = fila;
        add(errorLabel, c);

        return fila + 1;
    }

    public void limpiarCampos() {
        campoNombre.setText("");
        campoApellido.setText("");
        campoDNI.setText("");
        campoNombreUsuario.setText("");
        campoEmail.setText("");
        campoContrasenia.setText("");
    }

    public JButton getBtnRegistrar() {
        return btnRegistrar;
    }

    public RegistroFormularioDTO obtenerDatosRegistro() {
        return new RegistroFormularioDTO(
                campoNombre.getText().trim(),
                campoApellido.getText().trim(),
                campoDNI.getText().trim(),
                campoNombreUsuario.getText().trim(),
                campoEmail.getText().trim(),
                new String(campoContrasenia.getPassword())
        );
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}

package es.studium.AdivinaNumero;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteJuego extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    DataInputStream fentrada;
    DataOutputStream fsalida;
    Socket socket;
    String nombre;
    int numero;
    static JTextField mensaje = new JTextField();
    private JScrollPane scrollpane;
    static JTextArea textarea;
    JButton boton = new JButton("Enviar");
    JButton desconectar = new JButton("Salir");
    boolean repetir = true;

    public ClienteJuego(Socket socket, String nombre, int numero) {
        // Prepara la pantalla. Se recibe el socket creado, el nombre del cliente y el número secreto
        super(" Conexión del cliente chat: " + nombre);
        setLayout(null);
        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        textarea = new JTextArea();
        scrollpane = new JScrollPane(textarea);
        scrollpane.setBounds(10, 50, 400, 300);
        add(scrollpane);
        boton.setBounds(420, 10, 100, 30);
        add(boton);
        desconectar.setBounds(420, 50, 100, 30);
        add(desconectar);
        textarea.setEditable(false);
        boton.addActionListener(this);
        this.getRootPane().setDefaultButton(boton);
        desconectar.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.socket = socket;
        this.nombre = nombre;
        this.numero = numero; // Guarda el número secreto
        try {
            fentrada = new DataInputStream(socket.getInputStream());
            fsalida = new DataOutputStream(socket.getOutputStream());
            String texto = "SERVIDOR> Entra en el chat... " + nombre;
            fsalida.writeUTF(texto);
        } catch (IOException ex) {
            System.out.println("Error de E/S");
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        int puerto = 44444;
        // Genera un número aleatorio para el cliente
        int numeroSecreto = (int) (Math.random() * 100) + 1;
        String nombre = JOptionPane.showInputDialog("Introduce tu nombre de Jugador: ");
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", puerto);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(),
                    "<<Mensaje de Error:1>>", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if (!nombre.trim().equals("")) {
            ClienteJuego cliente = new ClienteJuego(socket, nombre, numeroSecreto); // Pasa el número secreto al cliente
            cliente.setBounds(0, 0, 540, 400);
            cliente.setVisible(true);
            cliente.ejecutar();
        } else {
            System.out.println("El nombre está vacío...");
        }
    }

    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == boton) {
            try {
                int intento = Integer.parseInt(mensaje.getText());
                fsalida.writeInt(intento);
                mensaje.setText("");
                if (intento == numero) {
                    String mensajeAdivinado = "¡Felicidades " + nombre + "! Has adivinado el número.";
                    fsalida.writeUTF(mensajeAdivinado);
                    repetir = false; // Terminar el bucle
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, ingresa un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == desconectar) {
            String texto = "SERVIDOR> Abandona el chat... " + nombre;
            try {
                fsalida.writeUTF(texto);
                fsalida.writeInt(-1); // Envía un valor especial para indicar al servidor que el cliente se desconecta
                repetir = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void ejecutar() {
        String texto = "";
        while (repetir) {
            try {
                texto = fentrada.readUTF();
                textarea.append(texto + "\n");
            } catch (IOException ex)
            {
                JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:2>>", JOptionPane.ERROR_MESSAGE);
                repetir = false;
            }
        }
        try {
            socket.close();
            System.exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

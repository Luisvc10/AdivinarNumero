package es.studium.AdivinaNumero;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServidorJuego extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    static ServerSocket servidor;
    static final int PUERTO = 44444;
    static int CONEXIONES = 0;
    static int ACTUALES = 0;
    static int MAXIMO = 15;
    static JTextField mensaje = new JTextField("");
    static JTextField mensaje2 = new JTextField("");
    private JScrollPane scrollpane1;
    static JTextArea textarea;
    JButton salir = new JButton("Salir");
    static Socket[] tabla = new Socket[MAXIMO];
    static int numeroSecreto;

    public ServidorJuego() {
        super("VENTANA DEL SERVIDOR DEL JUEGO");
        setLayout(null);
        mensaje.setBounds(10, 10, 400, 30);
        add(mensaje);
        mensaje2.setBounds(10, 348, 400, 30);
        add(mensaje2);
        mensaje.setEditable(false);
        textarea = new JTextArea();
        scrollpane1 = new JScrollPane(textarea);
        scrollpane1.setBounds(10, 50, 400, 300);
        add(scrollpane1);
        salir.setBounds(420, 10, 100, 30);
        add(salir);
        textarea.setEditable(false);
        salir.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        generarNumeroSecreto();
        mensaje.setText("Esperando conexiones...");
    }

    public static void main(String args[]) throws Exception {
        servidor = new ServerSocket(PUERTO);
        System.out.println("Servidor iniciado...");
        ServidorJuego pantalla = new ServidorJuego();
        pantalla.setBounds(0, 0, 540, 450);
        pantalla.setVisible(true);
        mensaje.setText("Número de conexiones actuales: " + 0);

        while (CONEXIONES < MAXIMO) {
            Socket socket;
            try {
                socket = servidor.accept();
            } catch (SocketException se) {
                // Sale por aquí si pulsamos el botón salir
                break;
            }
            tabla[CONEXIONES] = socket;
            CONEXIONES++;
            mensaje.setText("Número de conexiones actuales: " + CONEXIONES);
            ACTUALES++;
            HiloServidor hilo = new HiloServidor(socket, numeroSecreto);
            hilo.start();
        }
        // Si se alcanzan 15 conexiones o se pulsa el botón Salir,
        // el programa se sale del bucle.
        // Al pulsar Salir se cierra el ServerSocket
        // lo que provoca una excepción (SocketException)
        // en la sentencia accept(), la excepción se captura
        // y se ejecuta un break para salir del bucle
        if (!servidor.isClosed()) {
            try {
                mensaje2.setForeground(Color.red);
                mensaje2.setText("Máximo Nº de conexiones establecidas: " + CONEXIONES);
                servidor.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Servidor finalizado...");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == salir) {
            try {
                servidor.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }

    private static void generarNumeroSecreto() {
        numeroSecreto = (int) (Math.random() * 100) + 1;
        System.out.println("Número secreto generado: " + numeroSecreto);
    }

    public static boolean compararNumero(int numero) {
        return numero == numeroSecreto;
    }
    
    public static void actualizarTextarea(String mensaje) {
        textarea.append(mensaje + "\n");
    }
    
    public static void detenerServidor() {
        try {
            servidor.close();
            System.out.println("Servidor finalizado...");
            mensaje.setText("Servidor finalizado...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

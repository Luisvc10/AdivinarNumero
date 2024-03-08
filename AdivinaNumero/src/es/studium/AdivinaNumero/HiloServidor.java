package es.studium.AdivinaNumero;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread {
    DataInputStream fentrada;
    Socket socket;
    int numeroSecreto;
    boolean fin = false;

    public HiloServidor(Socket socket, int numeroSecreto) {
        this.socket = socket;
        this.numeroSecreto = numeroSecreto;
        try {
            fentrada = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error de E/S");
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String nombreCliente = fentrada.readUTF();
            DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
            fsalida.writeUTF("¡Bienvenido al juego de adivinar el número!");

            while (!fin) {
                int intento = fentrada.readInt();
                if (intento == numeroSecreto) {
                    fsalida.writeUTF("¡Felicidades " + nombreCliente + "! ¡Has adivinado el número secreto!");
                    ServidorJuego.actualizarTextarea("¡Felicidades " + nombreCliente + "! ¡Has adivinado el número secreto!");
                    break;
                } else if (intento < numeroSecreto) {
                    fsalida.writeUTF("El número secreto es mayor que " + intento);
                } else {
                    fsalida.writeUTF("El número secreto es menor que " + intento);
                }
                
                // Envía el mensaje al textarea del servidor
                ServidorJuego.actualizarTextarea(nombreCliente + ": " + intento);
                
                // Espera 3 segundos antes de la siguiente respuesta
                Thread.sleep(3000);
            }

            socket.close();
            ServidorJuego.detenerServidor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package btchat.app;

/**
 * Created by Juan Francisco on 6/04/14.
 */

// Imports Java.
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Imports Android
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class CommsThread extends Thread {

    final BluetoothSocket bluetoothSocket;
    final InputStream inputStream;
    final OutputStream outputStream;

    public CommsThread(BluetoothSocket socket){
        bluetoothSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            // Creamos los objetos InputStream y OutputStream.
            // Son necesarios para leer y escribir a través de los Socket.
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();

        } catch (IOException e){
            Log.d("CommsThread", e.getLocalizedMessage());
        }
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    public void run(){
        //Buffer de almacenamiento para el Stream.
        byte[] buffer = new byte[1024];

        // Bytes procedentes de read()
        int bytes;

        // Se sigue escuchando InputStream hasta que tenga lugar una excepción
        while(true){
            try {
                // Lectura de InputStream
                bytes = inputStream.read(buffer);

                // Actualizar la interfaz de usuario de la actividad principal.
                MainActivity.UIupdater.obtainMessage(0, bytes, -1, buffer).sendToTarget();
            } catch (IOException e){
                Log.d("CommsThread", e.getLocalizedMessage());
                break;
            }
        }
    }

    // Método llamado desde MainActivity para enviar datos al dispositivo remoto.
    public void write(String str){
        try {
            outputStream.write(str.getBytes());
        } catch(IOException e){
            Log.d("CommsThread", e.getLocalizedMessage());
        }
    }

    // Método llamado desde MainActivity para cerrar la conexión.
    public void cancel(){
        try{
            bluetoothSocket.close();
        } catch (IOException e){
            Log.d("CommsThread", e.getLocalizedMessage());
        }
    }
}

package btchat.app;

/**
 * Created by Juan Francisco on 6/04/14.
 */

// Imports Java.
import java.io.IOException;
import java.util.UUID;

//Imports Android.
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ServerThread extends Thread{

    //Server Socket.
    private final BluetoothServerSocket bluetoothServerSocket;

    public ServerThread(BluetoothAdapter bluetoothAdapter){
        BluetoothServerSocket tmp = null;
        try {
            // Cliente y servidor con mismo UUID
            tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothApp", UUID.fromString(MainActivity.UUID));
        } catch (IOException e){
            Log.d("ServerThread", e.getLocalizedMessage());
        }
        bluetoothServerSocket = tmp;
    }

    public void run(){
        BluetoothSocket socket = null;

        // Se sigue escuchando hasta que tenga lugar una excepción o se devuelva un socket.
        while (true){
            try {
                socket = bluetoothServerSocket.accept();
            } catch (IOException e){
                Log.d("ServerThread", e.getLocalizedMessage());
                break;
            }
            // Si se acepta una conexión.
            if(socket != null){
                // Se crea un hilo de ejecución independiente para escuchar los datos entrantes.
                CommsThread commsThread = new CommsThread(socket);
                commsThread.run();
            }
        }
    }

    public void cancel(){
        try {
            bluetoothServerSocket.close();
        } catch (IOException e){
            Log.d("ServerThread", e.getLocalizedMessage());
        }
    }
}

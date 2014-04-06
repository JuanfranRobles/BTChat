package btchat.app;

/**
 * Created by Juan Francisco on 6/04/14.
 */

//Imports Java.
import java.io.IOException;
import java.util.UUID;

//Imports Android.
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectToServerThread extends Thread{

    public CommsThread commsThread;
    public BluetoothSocket bluetoothSocket;
    public BluetoothAdapter bluetoothAdapter;

    public ConnectToServerThread(BluetoothDevice device, BluetoothAdapter btAdapter){

        BluetoothSocket tmp = null;
        bluetoothAdapter = btAdapter;
        //Se obtiene un objeto BluetoothSocket para conectar con el dispositivo Bluetooth.
        try {
            // EL identificador UUID debe ser el mismo en el cliente y en el servidor.
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MainActivity.UUID));
        } catch (IOException e){
            Log.d("ConnectToServerThread", e.getLocalizedMessage());
        }
        bluetoothSocket = tmp;
    }

    public void run(){
        // Cancelar el proceso de búsqueda porque ralentiza la conexión
        bluetoothAdapter.cancelDiscovery();
        try {
            //Utilizar el socket para conectar el dispositivo. Estará bloqueado hasta que se complete
            //con éxito o surja una excepción.
            bluetoothSocket.connect();

            // Crear el hilo de ejecución para el canal de comunicación.
            commsThread = new CommsThread(bluetoothSocket);
            commsThread.start();
        } catch (IOException connectException){
            // SI no es capaz de establecer la conexión se cierra el socket y se abandona
            try{
                bluetoothSocket.close();
            } catch (IOException closeException){
                closeException.getLocalizedMessage();
            }
            return;
        }
    }

    public void cancel(){
        try{
            bluetoothSocket.close();
            if(commsThread != null){
                commsThread.cancel();
            }
        } catch (IOException e){
            Log.d("ConnectToServerThread", e.getLocalizedMessage());
        }
    }
}

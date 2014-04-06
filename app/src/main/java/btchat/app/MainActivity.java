package btchat.app;

//Librerias Importadas.
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import java.io.IOException;
import java.util.ArrayList;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

    public final static String UUID = "3606f360-e4df-11e0-9572-0800200c9a66";

    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver discoverDevicesReceiver;
    BroadcastReceiver discoveryFinishedReceiver;

    // Para almacenar todos los tispositivos encontrados //
    ArrayList<BluetoothDevice> discoveredDevices;
    ArrayList<String> discoveredDevicesnames;

    static TextView txtData;
    EditText txtMessage;

    // Ejecución para el socket del servidor //
    ServerThread serverThread;

    // Hilo de ejecución para conectar con el socket del cliente //
    ConnectToServerThread connectToServerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Iniciar los ArrayList y el adaptador Bluetooth. //
        discoveredDevices = new ArrayList<BluetoothDevice>();
        discoveredDevicesnames = new ArrayList<String>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Inicialización de los textView para mostrar los mensajes recibidos. //
        txtData = (TextView) findViewById(R.id.txtData);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

    }

    // Para permitir que se pueda detectar nuestro dispositivo... //
    public void MakeDiscoverable(View view){

        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(i);

    }

    // Para buscar dispositivos Bluetooth. //
    private void DiscoveringDeviices(){

        if(discoverDevicesReceiver == null){
            discoverDevicesReceiver = new BroadcastReceiver() {
                //Se inicia cuando se encuentra un nuevo dispositivo. //
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    // Se encuentra un dispositivo. //
                    if(BluetoothDevice.ACTION_FOUND.equals(action)){
                        //se obtiene el objeto BluetoothDevice del Intent //
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        // Añade el nombre y la dirección a un array
                        // de adaptadores para mostrarlos en una vista ListView
                        // sólo añadirá el dispositivo si no se encontrase previamente en la lista.
                        if(!discoveredDevicesnames.contains(device)){
                            // Añade el dispositivo //
                            discoveredDevices.add(device);

                            // añade el nombre del dispositivo para utilizarlo con ListView.
                            discoveredDevicesnames.add(device.getName());

                            // muestra los elementos de ListView
                            setListAdapter(new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    discoveredDevicesnames));
                        }
                    }
                }
            };
        }

        if(discoveryFinishedReceiver == null){
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // activar listview cuando finalice la búsqueda.
                    // unos 12 segundos.
                    getListView().setEnabled(true);
                    Toast.makeText(getBaseContext(),
                            "Bísqueda finalizada. Seleccione un dispositivo para comenzar a conversar",
                            Toast.LENGTH_LONG).show();
                    unregisterReceiver(discoveryFinishedReceiver);
                }
            };
        }

        // registro de receptores de difusión //
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(discoverDevicesReceiver, filter1);
        registerReceiver(discoveryFinishedReceiver, filter2);

        // desactivar ListView cuando la búsqueda está en proceso.
        getListView().setEnabled(false);
        Toast.makeText(getBaseContext(),
                "Búsqueda en proceso... por favor, espere...",
                Toast.LENGTH_LONG).show();
        bluetoothAdapter.startDiscovery();
    }

    // buscar otros Dispositivos Bluetooth //
    public void DiscoverDevices(View view){

        // Buscar otros dispositivos...
        DiscoveringDeviices();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

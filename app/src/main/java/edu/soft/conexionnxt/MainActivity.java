package edu.soft.conexionnxt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    private BluetoothAdapter bAdapter;


    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;
    private DataOutputStream nxtDos=null;
    private BluetoothSocket connSock = null;
    private  ArrayList list=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bAdapter=BluetoothAdapter.getDefaultAdapter();

        if(!bAdapter.isEnabled()) {
            // bAdapter.enable();

            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }



    }

   /* public void write2(String s) throws IOException {
        outputStream.write(s.getBytes());
    }*/

     public void btBuscar(View v) {
         lv = (ListView)findViewById(R.id.listView);




         pairedDevices = bAdapter.getBondedDevices();

         list = new ArrayList();
         BluetoothDevice nxtDevice=null;
         for(BluetoothDevice bt : pairedDevices) {
             if (bt.getName().equals("NXT")) {
                // list.add("->"+bt.getName() + "\n" + bt.getAddress() + bt.getUuids());
                list.add(bt.getName() + "\n" + bt.getAddress());

                 nxtDevice=bt;
             }
         }

          ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
         lv.setAdapter(adapter);

         //final String nxt1 = "00:16:53:09:17:36";
         try {
                   connSock = nxtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                   connSock.connect();
                   nxtDos = new DataOutputStream(connSock.getOutputStream());
                   Toast toast = Toast.makeText(this, "Connection OK", Toast.LENGTH_LONG);
                   toast.show();

         } catch (IOException e) {
             adapter = null;
             lv.setAdapter(adapter);
             Toast toast=Toast.makeText(this, "Problem at creating a connection", Toast.LENGTH_LONG);
             toast.show();
         }
     }

    public void moverAdelante(View v) {
        sendNXTCommand(1);


    }
    public void btAtras(View v) {
        sendNXTCommand(2);


    }
    public void disconnectBT(View v) {
        try{
            if (connSock!=null) {
                final ArrayAdapter adapter = null;
                lv.setAdapter(adapter);
                sendNXTCommand(99);
                connSock.close();
                connSock = null;
            }
        } catch (IOException e) {
            Toast toast=Toast.makeText(this, "Problem at closing the connection", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void sendNXTCommand(int command) {
        if (nxtDos==null) {
            return;
        }
        try{
            nxtDos.writeInt(command);
            nxtDos.flush();

        } catch (IOException e) {
            Toast toast=Toast.makeText(this, "Problem at sending  command", Toast.LENGTH_LONG);
            toast.show();
        }

    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            bAdapter.disable();
            return true;
        }


        if (id == R.id.activar_bt) {
            bAdapter.enable();
            return true;
        }

        if (id==R.id.salir) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

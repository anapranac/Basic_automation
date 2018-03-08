package com.example.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    public static final int BLUE_ONE = 1;
    public static final String PREFERENCES = "preferences";
    public static final String MAC_ID = "mac_id";
    public static final String SPLIT = "____";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final Handler mHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
        }

    };

    String[] blue_list = null;
    private BluetoothAdapter btAdapter;
    private BlueActivity mBlueActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mBlueActivity = new BlueActivity(this, mHandler);


        Switch switch_1 = findViewById(R.id.switch_1);
        Switch switch_2 = findViewById(R.id.switch_2);
        Switch switch_3 = findViewById(R.id.switch_3);
        Switch switch_4 = findViewById(R.id.switch_4);
        Switch switch_5 = findViewById(R.id.switch_5);
        Switch switch_6 = findViewById(R.id.switch_6);
        Switch switch_7 = findViewById(R.id.switch_7);
        Switch switch_8 = findViewById(R.id.switch_8);

        switch_1.setOnCheckedChangeListener(this);
        switch_2.setOnCheckedChangeListener(this);
        switch_3.setOnCheckedChangeListener(this);
        switch_4.setOnCheckedChangeListener(this);
        switch_5.setOnCheckedChangeListener(this);
        switch_6.setOnCheckedChangeListener(this);
        switch_7.setOnCheckedChangeListener(this);
        switch_8.setOnCheckedChangeListener(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckBTState();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void Connect_bluetooth(View view) {
        final SharedPreferences settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        if (settings.contains(MAC_ID)) {
            String address1 = settings.getString(MAC_ID, null);
            boolean on = ((ToggleButton) view).isChecked();
            if (on) {
                mBlueActivity.connect(address1);
                Toast.makeText(this, "Connecting to " + address1, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Disconnecting from " + address1, Toast.LENGTH_LONG).show();
                mBlueActivity.stop1();
            }
        } else {
            set_mac_id(new View(this));

        }

    }

    public void set_mac_id(View view) {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        ArrayList<String> names = new ArrayList<>();
        for (BluetoothDevice bt : pairedDevices) {
            names.add(bt.getName() + SPLIT + bt.getAddress());
        }
        blue_list = new String[names.size()];
        blue_list = names.toArray(blue_list);
        mac_list();
    }

    public void mac_list() {
        final SharedPreferences settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        new AlertDialog.Builder(this)
                .setTitle("yoo")
                .setCancelable(true)
                .setItems(blue_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] address = blue_list[which].split(SPLIT);
                        settings.edit().putString(MAC_ID, address[1]).apply();
                        Toast.makeText(MainActivity.this, "Saving this " + address[0] + " Device at address " + address[1], Toast.LENGTH_LONG).show();

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //   finish();


                    }
                })
                .show();


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlueActivity.stop1();
    }

    private void CheckBTState() {
        if (btAdapter == null) {
            AlertBox("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (!btAdapter.isEnabled()) {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    public void AlertBox(final String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message + " Press OK to exit...")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //   finish();
                        title.length();

                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.switch_1:
                if (isChecked) {
                    mBlueActivity.SwitchOn(1);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_2:
                if (isChecked) {
                    mBlueActivity.SwitchOn(2);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_3:
                if (isChecked) {
                    mBlueActivity.SwitchOn(3);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_4:
                if (isChecked) {
                    mBlueActivity.SwitchOn(4);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_5:
                if (isChecked) {
                    mBlueActivity.SwitchOn(5);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_6:
                if (isChecked) {
                    mBlueActivity.SwitchOn(6);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_7:
                if (isChecked) {
                    mBlueActivity.SwitchOn(7);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
            case R.id.switch_8:
                if (isChecked) {
                    mBlueActivity.SwitchOn(8);
                } else {
                    mBlueActivity.SwitchOff();
                }
                break;
        }
    }
}

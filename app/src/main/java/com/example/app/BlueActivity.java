package com.example.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BlueActivity {
    private static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    private static final int STATE_CONNECTED = 3;  // now connected to a remote device
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;
    private static final String NAME = "BluetoothChat";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    byte[] buffer1 = {1, 2, 3, 4, 5};
    int bytes1 = 1;
    private ConnectThread mCT;
    private ConnectedThread mCdT;
    private SQLiteDatabase db;
    private int mState;
    private BluetoothSocket BTST;
    private BluetoothDevice BTDV;
    private MainActivity Main = null;

    BlueActivity(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        Main = new MainActivity();


    }

    /*    public void mac_list(View v){
            pairedDevices = BluetoothAdapter.getBondedDevices();
            ArrayList list = new ArrayList();

            for(BluetoothDevice bt : pairedDevices)
                list.add(bt.getName());

            final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
            lv1.setAdapter(adapter);
        }
    */
    public synchronized int getState() {
        return mState;
    }

    private synchronized void setState(int state) {
        mState = state;
    }


    synchronized void connect(String address) {
        if (mState == STATE_CONNECTING) {
            if (mCT != null) {
                mCT.cancel();
                mCT = null;
            }
        }
        if (mCdT != null) {
            mCdT.cancel();
            mCdT = null;
        }
        mCT = new ConnectThread(address);
        mCT.start();
        setState(STATE_CONNECTING);
    }


    private synchronized void connected() {
        if (mState == STATE_CONNECTING) {
            if (mCT != null) {
                mCT.cancel();
                mCT = null;
            }
        }
        if (mCdT != null) {
            mCdT.cancel();
            mCdT = null;
        }

        mCdT = new ConnectedThread();
        mCdT.start();

        setState(STATE_CONNECTED);
    }


    synchronized void stop1() {
        if (D) Log.d(TAG, "stop");
        if (mCT != null) {
            mCT.cancel();
            mCT = null;
        }
        if (mCdT != null) {
            mCdT.cancel();
            mCdT = null;
        }
        setState(STATE_NONE);

    }

    private void write(String out) {

        ConnectedThread r;
        byte[] send = out.getBytes();
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mCdT;
        }
        r.write(send);
    }

    void SwitchOn(int duty) {
        String helsnd = "H" + duty + "X";
        write(helsnd);
    }

    void SwitchOff() {
        write("H000Y");
    }

    private void connectionFailed() {
        mHandler.obtainMessage(5, 1, -1, buffer1).sendToTarget();
    }

    private void connectionLost() {
        mHandler.obtainMessage(5, 1, -1, buffer1).sendToTarget();
    }

    private class ConnectThread extends Thread {
        ConnectThread(String address) {
            BluetoothDevice device = mAdapter.getRemoteDevice(address);
            BTDV = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            BTST = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            mAdapter.cancelDiscovery();
            try {
                BTST.connect();
            } catch (IOException e) {
                connectionFailed();

                try {
                    BTST.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }
            synchronized (BlueActivity.this) {
                mCT = null;
            }
            connected();
        }

        void cancel() {
            try {
                BTST.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        ConnectedThread() {
            Log.d(TAG, "create ConnectedThread");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = BTST.getInputStream();
                tmpOut = BTST.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MainActivity.BLUE_ONE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }

        }

        void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                BTST.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}

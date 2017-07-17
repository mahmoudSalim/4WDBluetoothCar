package com.softwaresolutions.a4wdbluetoothcar.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.softwaresolutions.a4wdbluetoothcar.R;
import com.softwaresolutions.a4wdbluetoothcar.fragments.JoyStickFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Define Attributes
    public static final String RESET = "C";

    public static final String AUTO = "A";

    public static final String FORWARD = "F";
    public static final String FORWARD_RIGHT = "I";
    public static final String RIGHT = "R";
    public static final String BACKWORD_RIGHT = "J";
    public static final String BACKWORD = "B";
    public static final String BACKWORD_LEFT = "H";
    public static final String LEFT = "L";
    public static final String FORWARD_LEFT = "G";

    public static final String BREAK = " ";

    public static final String STOP = "0";
    public static final String GEAR_1 = "1";
    public static final String GEAR_2 = "2";
    public static final String GEAR_3 = "3";
    public static final String GEAR_4 = "4";
    public static final String GEAR_5 = "5";

    public static final char NO_STATE_VER = '0';
    public static final char NO_STATE_HOR = '5';
    public static final char INC_Y = '8';
    public static final char INC_X = '6';
    public static final char DEC_Y = '2';
    public static final char DEC_X = '4';

    private static final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;

    private static BluetoothSocket socket;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static boolean deviceConnected = false;

    // Accelerometer Attriputes
    private Sensor mAccelerometer;
    private SensorManager sensorManager;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private float delta_x, delta_y, delta_z;

    private int x = 0, y = 0;
    private char stateVer = NO_STATE_VER;
    private char stateHor = NO_STATE_HOR;
    private boolean breakState = false;

    private Switch start;
    private Spinner devicesSpinner;
    private CheckBox auto, hAcc, vAcc;
    private Button up, down, left, right, breakBtn, oneHand;
    private RadioGroup gearsGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        start = (Switch) findViewById(R.id.switch_start);

        devicesSpinner = (Spinner) findViewById(R.id.spinner_devices);

        auto = (CheckBox) findViewById(R.id.CBox_auto);
        hAcc = (CheckBox) findViewById(R.id.CBox_hor_acc);
        vAcc = (CheckBox) findViewById(R.id.CBox_ver_acc);

        up = (Button) findViewById(R.id.up_btn);
        down = (Button) findViewById(R.id.down_btn);
        left = (Button) findViewById(R.id.left_btn);
        right = (Button) findViewById(R.id.right_btn);
        breakBtn = (Button) findViewById(R.id.break_btn);
        oneHand = (Button) findViewById(R.id.one_hand_btn);

        gearsGroup = (RadioGroup) findViewById(R.id.gears_group);

        //OnTouchListener code for the forward button (button long press)
        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) //MotionEvent.ACTION_DOWN is when you hold a button down
                {
                    y++;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) //MotionEvent.ACTION_UP is when you release a button
                {
                    y--;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                }
                return false;
            }

        });

        //OnTouchListener code for the reverse button (button long press)
        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    y--;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    y++;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                }
                return false;
            }
        });

        //OnTouchListener code for the right button (button long press)
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x++;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    x--;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                }
                return false;
            }
        });

        //OnTouchListener code for the left button (button long press)
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x--;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    x++;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                }
                return false;
            }
        });

        breakBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    breakState = true;
                    sendData(BREAK);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    breakState = false;
                    sendData(giveDirection());
                }
                return false;
            }
        });

        oneHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vAcc.setChecked(false);
                hAcc.setChecked(false);
                gearsGroup.clearCheck();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                JoyStickFragment fragment = new JoyStickFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.app_layout, fragment).addToBackStack(null).commit();

            }
        });

        gearsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String command = STOP;
                if (checkedId == R.id.gear_one) {
                    command = GEAR_1;
                } else if (checkedId == R.id.gear_two) {
                    command = GEAR_2;
                } else if (checkedId == R.id.gear_three) {
                    command = GEAR_3;
                } else if (checkedId == R.id.gear_four) {
                    command = GEAR_4;
                } else if (checkedId == R.id.gear_five) {
                    command = GEAR_5;
                }
                sendData(command);
            }
        });

        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Object selectedItem = devicesSpinner.getSelectedItem();
                    if (selectedItem == null) {
                        Toast.makeText(getApplicationContext(), "Select A Device", Toast.LENGTH_SHORT).show();
                        start.setChecked(false);
                    } else if (deviceConnected || BTconnect((String) selectedItem)) {
                        setUiEnabled(true);
                        deviceConnected = true;
                    } else {
                        start.setChecked(false);
                    }
                } else {
                    if (deviceConnected) {
                        try {
                            vAcc.setChecked(false);
                            hAcc.setChecked(false);
                            gearsGroup.clearCheck();
                            sendData(RESET);
                            outputStream.close();
                            inputStream.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setUiEnabled(false);
                        deviceConnected = false;
                    }
                }
            }
        });

        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sendData(AUTO);
                } else {
                    sendData(STOP);
                }
                setUiEnabled(true);
            }
        });

        vAcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && stateVer != NO_STATE_VER) {
                    if (stateVer == INC_Y) {
                        y--;
                    } else if (stateVer == DEC_Y) {
                        y++;
                    }

                    stateVer = NO_STATE_VER;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                }
            }
        });

        hAcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && stateHor != NO_STATE_HOR) {
                    if (stateHor == INC_X) {
                        x--;
                    } else if (stateHor == DEC_X) {
                        x++;
                    }

                    stateHor = NO_STATE_HOR;
                    if (!breakState) {
                        sendData(giveDirection());
                    }
                }
            }
        });

        // Initializing Accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        setUiEnabled(false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTinit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xVal = sensorEvent.values[0];
            float yVal = sensorEvent.values[1];
            float zVal = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();


            // update readings evrey 100ms
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                //float speed = Math.abs(xVal + yVal + zVal - last_x - last_y - last_z) / diffTime * 10000;

//                if (speed > SHAKE_THRESHOLD) {
//
//                }

                delta_x = xVal;
                delta_y = yVal;
                delta_z = zVal;

                delta_x = (delta_x < 0.1 && delta_x > -0.1) ? 0 : delta_x;
                delta_y = (delta_y < 0.1 && delta_y > -0.1) ? 0 : delta_y;
                delta_z = (delta_z < 0.1 && delta_z > -0.1) ? 0 : delta_z;

                if (vAcc.isChecked()) {
                    if (delta_x == 0 && stateVer != NO_STATE_VER) {

                        if (stateVer == INC_Y) {
                            y--;
                        } else if (stateVer == DEC_Y) {
                            y++;
                        }

                        stateVer = NO_STATE_VER;
                        if (!breakState) {
                            sendData(giveDirection());
                        }
                    } else if (delta_x < 0 && stateVer != INC_Y) {

                        if (stateVer == DEC_Y) {
                            y++;
                        }

                        stateVer = INC_Y;
                        y++;
                        if (!breakState) {
                            sendData(giveDirection());
                        }
                    } else if (delta_x > 0 && stateVer != DEC_Y) {

                        if (stateVer == INC_Y) {
                            y--;
                        }

                        stateVer = DEC_Y;
                        y--;
                        if (!breakState) {
                            sendData(giveDirection());
                        }
                    }
                }
                if (hAcc.isChecked()) {
                    if (delta_y == 0 && stateHor != NO_STATE_HOR) {

                        if (stateHor == INC_X) {
                            x--;
                        } else if (stateHor == DEC_X) {
                            x++;
                        }

                        stateHor = NO_STATE_HOR;
                        if (!breakState) {
                            sendData(giveDirection());
                        }
                    } else if (delta_y > 0 && stateHor != INC_X) {

                        if (stateHor == DEC_X) {
                            x++;
                        }

                        stateHor = INC_X;
                        x++;
                        if (!breakState) {
                            sendData(giveDirection());
                        }
                    } else if (delta_y < 0 && stateHor != DEC_X) {

                        if (stateHor == INC_X) {
                            x--;
                        }

                        stateHor = DEC_X;
                        x--;
                        if (!breakState) {
                            sendData(giveDirection());
                        }
                    }
                }

                last_x = xVal;
                last_y = yVal;
                last_z = zVal;

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    private String giveDirection() {
        if (x == 0 && y > 0) {
            return FORWARD;
        } else if (x > 0 && y > 0) {
            return FORWARD_RIGHT;
        } else if (x > 0 && y == 0) {
            return RIGHT;
        } else if (x > 0 && y < 0) {
            return BACKWORD_RIGHT;
        } else if (x == 0 && y < 0) {
            return BACKWORD;
        } else if (x < 0 && y < 0) {
            return BACKWORD_LEFT;
        } else if (x < 0 && y == 0) {
            return LEFT;
        } else if (x < 0 && y > 0) {
            return FORWARD_LEFT;
        }
        return STOP;
    }

    private void setUiEnabled(boolean bool) {
        devicesSpinner.setEnabled(!bool);
        auto.setEnabled(bool);

        if (auto.isChecked()) {
            bool = false;
        }

        hAcc.setEnabled(bool);
        vAcc.setEnabled(bool);

        int visibility = (bool) ? View.VISIBLE : View.INVISIBLE;

        up.setVisibility(visibility);
        down.setVisibility(visibility);
        left.setVisibility(visibility);
        right.setVisibility(visibility);
        breakBtn.setVisibility(visibility);
        oneHand.setVisibility(visibility);
        gearsGroup.setVisibility(visibility);
    }

    private void BTinit() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't Support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
        } else {

            while (!bluetoothAdapter.isEnabled()) {
                Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableAdapter, 0);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

            if (!bondedDevices.isEmpty()) {
                ArrayList<String> devicesList = new ArrayList<>();
                for (BluetoothDevice iterator : bondedDevices) {
                    devicesList.add(iterator.getName());
                }
                ArrayAdapter<String> devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, devicesList);
                devicesSpinner.setAdapter(devicesAdapter);
            }

        }
    }

    private boolean BTconnect(String deviceName) {
        boolean found = false;
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if (!bondedDevices.isEmpty()) {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getName().equals(deviceName)) {
                    device = iterator; //device is an object of type BluetoothDevice
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed To Connect", Toast.LENGTH_SHORT).show();
        }
        return connected;
    }

    public boolean sendData(String command) {
        try {
            outputStream.write(command.getBytes()); //transmits the value of command to the bluetooth module
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}

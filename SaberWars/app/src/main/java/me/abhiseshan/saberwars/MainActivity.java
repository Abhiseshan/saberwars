package me.abhiseshan.saberwars;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.opengl.Matrix;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Thread clientThread;
    Thread serverThread;

    Button onButton;
    Boolean isLightSaberOn;
    Boolean currentLightSaberStatus;
    String IP = "192.168.173.98";
    Button resetButton;

    SensorManager sensorManager;
    Sensor linearAcc;
    Sensor gravitySensor;
    Sensor magFieldSensor;

    TextView accTV;
    TextView gyroTV;
    TextView linearTV;
    TextView gravityTV;

    float[] gyroValues;
    float[] gravity;
    float[] linear_acceleration;
    float[] geomagnetic;
    float[] grv;

    MediaPlayer saberSwing1;
    MediaPlayer saberSound;
    MediaPlayer saberStrike;

    float dz;
    float oldZ;

    boolean isSwingComplete;
    boolean reset;

    float []distance;
    float []orientation;
    long oldTime;

    String playerNo, playerName;
    int saberColor;

    Boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dz = 0;
        oldTime = 0;
        isSwingComplete = true;

        reset = false;
        first = true;

        saberColor = getIntent().getIntExtra("saberType", 1);
        playerNo = getIntent().getStringExtra("playerNumber");
        playerName = getIntent().getStringExtra("playerName");

        accTV = (TextView) findViewById(R.id.accelerometerTV);
        linearTV = (TextView) findViewById(R.id.lienarTV);
        gravityTV = (TextView) findViewById(R.id.gravityTV);
        gyroTV = (TextView) findViewById(R.id.gyroTV);

        resetButton = (Button) findViewById(R.id.resetButton);

        gyroValues = new float[3];
        gravity = new float[3];
        linear_acceleration = new float[4];
        geomagnetic = new float[3];
        grv = new float[3];
        distance = new float[3];
        orientation = new float[3];

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        isLightSaberOn = false;
        currentLightSaberStatus = false;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        linearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance[0] = 0;
                distance[1] = 0;
                distance[2] = 0;
            }
        });

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.d("This IP", ip);

        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = new Socket(IP.trim(), 5000);
                        DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                        //Log.d("values","values," + playerNo + "," + distance[0] + "," + distance[1] + "," + distance[2] + "," + orientation[0] + "," + orientation[1] + "," + orientation[2]);

                        if (first){
                            DOS.write(("name,"+ playerNo + "," + playerName).getBytes());
                            DOS.write(("saberColor,"+ playerNo + "," + saberColor).getBytes());
                            first = false;
                        }

                        if (isLightSaberOn != currentLightSaberStatus) {
                            currentLightSaberStatus = isLightSaberOn;
                            DOS.write(("saber,"+ playerNo + "," + ((isLightSaberOn)?"1":"0")).getBytes());
                        } else
                            DOS.write(("values," + playerNo + "," + distance[0] + "," + distance[1] + "," + distance[2] + "," + orientation[0] + "," + orientation[1] + "," + orientation[2]).getBytes());

                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        ServerSocket socket = new ServerSocket(5000);
                        Socket clientSocket = socket.accept();       //This is blocking. It will wait.
                        DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());
                        byte [] bytes = new byte[100];
                        int bytesno = DIS.read(bytes);
                        Log.d("Data", " " + bytesno);
                        String result = new String(bytes, "UTF-8");
                        //String result2 = result.substring(0, bytesno);
                        Log.d("Data result", result.trim());
                        clientSocket.close();
                        parseReceivedData(result);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        onButton = (Button) findViewById(R.id.onButton);

        final MediaPlayer saberOn = MediaPlayer.create(this, R.raw.saberon);
        final MediaPlayer saberOff = MediaPlayer.create(this, R.raw.saberoff);
        saberSwing1 = MediaPlayer.create(this, R.raw.saberswing1);
        saberStrike = MediaPlayer.create(this, R.raw.saberclash);

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLightSaberOn) {
                    saberOn.start();
                    saberSound = MediaPlayer.create(getApplicationContext(), R.raw.sabersound);
                    saberSound.setLooping(true);
                    saberSound.start();
                } else {
                    saberSound.release();
                    saberOff.start();
                }
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
                isLightSaberOn = !isLightSaberOn;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magFieldSensor, SensorManager.SENSOR_DELAY_FASTEST);
        clientThread.start();
        serverThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        final float alpha = (float) 0.8;

        if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
            grv[0] = event.values[0];
            grv[1] = event.values[1];
            grv[2] = event.values[2];
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        }

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];
            linear_acceleration[3] = 0;
            onSaberMovementZ(linear_acceleration[2]);
        }

        if (mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic[0] = event.values[0];
            geomagnetic[1] = event.values[1];
            geomagnetic[2] = event.values[2];
        }

        float[] trueAcceleration = new float[4];
        float[] R = new float[16];
        float[] RINV = new float[16];
        float[] I = new float[16];

        SensorManager.getRotationMatrix(R, I, grv, geomagnetic);
        Matrix.invertM(RINV, 0, R, 0);
        Matrix.multiplyMV(trueAcceleration, 0, RINV, 0, linear_acceleration, 0);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        linearTV.setText("X: " + df.format(trueAcceleration[0]) + " Y: " + df.format(trueAcceleration[1]) + " Z: " + df.format(trueAcceleration[2] + 9.8));

        long dt;

        //Distance Calculation
        if (oldTime == 0) {
            oldTime = System.currentTimeMillis();
        }
        dt = System.currentTimeMillis() - oldTime;
        oldTime = System.currentTimeMillis();


        float zactual = trueAcceleration[2] + 9.8f;

        //Log.d("ZZZZZ", "Z: " + zactual);

        if (Math.abs(trueAcceleration[0])> 0.1)
            distance[0] += (0.5 * trueAcceleration[0] * (dt * dt) * Math.pow(10, -6));
        if (Math.abs(trueAcceleration[1])> 0.1)
            distance[1] += (0.5 * trueAcceleration[1] * (dt * dt) * Math.pow(10, -6));
        if (Math.abs(zactual)> 0.01)
            distance[2] += (0.5 * zactual * (dt * dt) * Math.pow(10, -6));

        //Getting orientation
        SensorManager.getRotationMatrix(R, I, grv, geomagnetic);
        float orient[] = new float[3];
        SensorManager.getOrientation(R, orient);
        orientation[0] = (float) Math.toDegrees(orient[0]); // orientation contains: azimut, pitch and roll
        orientation[1] = (float) Math.toDegrees(orient[1]);
        orientation[2] = (float) Math.toDegrees(orient[2]);

        //Write to screen
        DecimalFormat df2= new DecimalFormat();
        df2.setMaximumFractionDigits(4);
        accTV.setText("X: " + orientation[0] + " Y: " + orientation[1] + " Z: " + orientation[2]);
        gravityTV.setText("X: " + df2.format(distance[0] * 100) + " Y: " + df2.format(distance[1]*100) + " Z: " + df2.format(distance[2]*100));
        gyroTV.setText("Time = " + dt);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void onSaberMovementZ(float z) {
        if (Math.abs(oldZ - z) > 6 && isLightSaberOn) {
            if (!saberSwing1.isPlaying()) {
                saberSound.release();
                saberSwing1.start();
                saberSound = MediaPlayer.create(getApplicationContext(), R.raw.sabersound);
                saberSound.setLooping(true);
                saberSound.start();
            }
            oldZ = z;
        }
    }

    private void parseReceivedData(String data){
        switch(data){
            case "gameover":
                if(isLightSaberOn) {
                    saberSound.release();
                }
                serverThread.interrupt();
                clientThread.interrupt();
                finish();
                break;
            case "strike":
                if (isLightSaberOn) {
                    saberSound.release();
                    saberStrike.start();
                    saberSound = MediaPlayer.create(getApplicationContext(), R.raw.sabersound);
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(300);
                }
                break;
            default:
                Log.d("Unspecified data", data);
        }
    }
}
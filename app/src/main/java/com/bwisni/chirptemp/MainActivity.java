package com.bwisni.chirptemp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    public static final char DEGREE = '\u00B0';
    public static final int CALC_REQUEST_CODE = 0;
    public static final int MILLISECONDS_F = 14000;
    public static final int MILLISECONDS_C = 25000;

    // Fields
    private TextView temperatureText;

    //private EditText chirpsInput;
    //private EditText secondsInput;

    private Button chirpButton;
    private Button resetButton;
    private Button recordButton;
    private Button playButton;

    private Chronometer chronometer;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private String mFileName;

    private TextView tempToggle;
    private TextView countdownText;
    private TextView title;

    // Stored values
    private long numChirps;
    private long numSeconds;
    private int temperature;

    private boolean inFahrenheit = true;
    private boolean chronometerRunning = false;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup local storage filename
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/chirps.3gp";

        // Get the external interface components
        temperatureText = (TextView) findViewById(R.id.temperature);
        title = (TextView) findViewById(R.id.title);


        chirpButton = (Button) findViewById(R.id.chirpButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        recordButton = (Button) findViewById(R.id.recordButton);
        playButton = (Button) findViewById(R.id.playButton);

        chronometer = (Chronometer) findViewById(R.id.chrono);

        tempToggle = (TextView) findViewById(R.id.scaleToggle);
        countdownText = (TextView) findViewById(R.id.chronoText);

        // Init values
        setChirps(0);
        setSeconds(14); // 14 seconds used by default for Fahrenheit calculation
        setTemperature(0);
        setInFahrenheit(true);
        setChronometerRunning(false);

        // Init chronometer
        //chronometer.setText("%s");
        //chronometer.setFormat("SS");

        // Chirp button pressed
        chirpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (numChirps == 0) {
                    // Set seconds for calculation
                    if (isInFahrenheit()) {
                        setSeconds(14);
                    } else {
                        setSeconds(25);
                    }

                    // Start chronometer when the button is pressed for the first time
                    startChronometer();
                }

                setNumChirps(getNumChirps() + 1);
                //setChirps(getNumChirps());
            }
        });

        // Reset button
        resetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Restore all values to their default
                setChirps(0);
                setTemperature(0);

                calculateTemperature();

                stopChronometer();
                chronometer.setBase(SystemClock.elapsedRealtime());

                // Re-enable Chirp button
                chirpButton.setEnabled(true);
            }
        });

        // Record button
        recordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Restore all values to their default
                setChirps(0);
                setTemperature(0);


                stopChronometer();
                chronometer.setBase(SystemClock.elapsedRealtime());


                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordButton.setText("Stop");
                } else {
                    recordButton.setText("Record");
                }

                mStartRecording = !mStartRecording;

                // Re-enable Chirp button
                chirpButton.setEnabled(false);
            }
        });

        // play button
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    playButton.setText("Stop");
                } else {
                    playButton.setText("Play");
                }

                mStartPlaying = !mStartPlaying;

                // Re-enable Chirp button
                chirpButton.setEnabled(false);
            }
        });

        tempToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (isInFahrenheit()) {

                    temperatureText.setTextColor(getResources().getColor(android.R.color.holo_red_light));

                    tempToggle.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    tempToggle.setText("C");

                    setInFahrenheit(false);

                } else {

                    temperatureText.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

                    tempToggle.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    tempToggle.setText("F");

                    setInFahrenheit(true);
                }

                calculateTemperature();
            }


        });


        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer arg0) {
                // Find out how many milliseconds have passed
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

                if (isInFahrenheit()) {
                    // Stop the timer, disable the Chirp button, and calculate the temperature once we reach 14 seconds
                    if (elapsedMillis >= MILLISECONDS_F) {
                        stopChronometer();
                        calculateTemperature();
                    }

                    setCountdownText(elapsedMillis, MILLISECONDS_F);

                } else {
                    if (elapsedMillis >= MILLISECONDS_C) {
                        stopChronometer();
                        calculateTemperature();
                    }

                    setCountdownText(elapsedMillis, MILLISECONDS_C);
                }



            }

        });
    }

    // Builds formatted SpannableString showing remaining seconds and milliseconds,
    // then assigns it to the countdown TextView
    private void setCountdownText(long elapsedMillis, int baseMillis) {
        int secondsLeft = (int) (baseMillis - elapsedMillis) / 1000;
        int millisecondsLeft = (int) (baseMillis - elapsedMillis - secondsLeft * 1000) / 100;

        String ssLeft = Long.toString(secondsLeft);
        SpannableStringBuilder ss = new SpannableStringBuilder(ssLeft);

        ss.append(".").append(Long.toString(millisecondsLeft));
        // Set milliseconds to appear in smaller font. Strings added to the beginning are EXCLUDED,
        // any appended after are INCLUDED in the formatting
        ss.setSpan(new RelativeSizeSpan(0.75f), ssLeft.length(), ss.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        countdownText.setText(ss);
    }

    private void openGraphActivity() {
        // Create intent to open up our Graph Activity
        Intent intent = new Intent(getApplicationContext(), GraphActivity.class);

        // Add extras (values to be shared)
        intent.putExtra("temperature", getTemperature());
        intent.putExtra("numChirps", getNumChirps());
        intent.putExtra("numSeconds", getNumSeconds());
        intent.putExtra("isF", isInFahrenheit());

        calculateTemperature();

        startActivity(intent);
    }

    private void openCalcActivity() {
        // Create intent to open up our Graph Activity
        Intent intent = new Intent(getApplicationContext(),
                CalculatorActivity.class);

        // Add extras (values to be shared)
        intent.putExtra("temperature", getTemperature());
        intent.putExtra("numChirps", getNumChirps());
        intent.putExtra("numSeconds", getNumSeconds());
        intent.putExtra("isF", isInFahrenheit());

        calculateTemperature();

        startActivityForResult(intent, CALC_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CALC_REQUEST_CODE && resultCode == RESULT_OK && intent != null) {
            // Return from calculator popup, recalculate
            setNumSeconds(intent.getLongExtra("numSeconds", 0));
            setNumChirps(intent.getIntExtra("numChirps", 0));
            calculateTemperature();
            chirpButton.setEnabled(false);
        }
    }

    private void startChronometer() {
        startChronometer(SystemClock.elapsedRealtime());
    }

    private void startChronometer(long base) {
        setChronometerRunning(true);
        chronometer.setBase(base);

        title.setText("Time Remaining");
        temperatureText.setVisibility(View.GONE);
        countdownText.setVisibility(View.VISIBLE);

        tempToggle.setVisibility(View.GONE);
        tempToggle.setEnabled(false);

        chronometer.start();
    }

    private void stopChronometer() {
        chronometer.stop();
        setChronometerRunning(false);

        calculateTemperature();

        countdownText.setVisibility(View.GONE);
        temperatureText.setVisibility(View.VISIBLE);
        title.setText("Temperature");

        tempToggle.setVisibility(View.VISIBLE);
        tempToggle.setEnabled(true);

        chirpButton.setEnabled(false);
    }

    protected void calculateTemperature() {
        double temp;
        double chirps = getNumChirps();
        double seconds = getNumSeconds();

        if(chirps <= 0 || seconds <= 0){
            temp = 0;
        }
        else {
            if(inFahrenheit){
                temp = chirps/(seconds/14) + 40;
            }
            else {
                temp = (chirps / (seconds/25))/3 + 4;
            }
        }

        // Cast to an int to remove decimals
        setTemperature((int) temp);

        Log.v(TAG, "Calculated temperature (exact): "+temp);
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Play prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Rec prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    protected void setChirps(int c) {
        // Update value
        setNumChirps(c);

        // Convert numChirps to a string and update text
        //chirpsInput.setText(""+numChirps);
    }

    protected void setSeconds(int s) {
        setNumSeconds(s);

        //secondsInput.setText(""+numSeconds);
    }

    public int getTemperature() {
        return temperature;
    }

    // Set both the temperature value and the text on-screen
    protected void setTemperature(int t) {
        temperature = t;
        String ttxt = Integer.toString(t);
        ttxt += DEGREE;
        temperatureText.setText(ttxt);
    }


    public long getNumChirps() {
        return numChirps;
    }

    private void setNumChirps(long numChirps) {
        this.numChirps = numChirps;
    }

    public long getNumSeconds() {
        return numSeconds;
    }

    private void setNumSeconds(long numSeconds) {
        this.numSeconds = numSeconds;
    }

    public boolean isInFahrenheit() {
        return inFahrenheit;
    }

    private void setInFahrenheit(boolean inFahrenheit) {
        this.inFahrenheit = inFahrenheit;
    }

    public boolean isChronometerRunning() {
        return chronometerRunning;
    }

    private void setChronometerRunning(boolean chronometerStarted) {
        this.chronometerRunning = chronometerStarted;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(getClass().getSimpleName(), "onSaveInstanceState");

        // Put temperature, button, and chronometer data in bundle
        outState.putInt("temperature", getTemperature());
        outState.putBoolean("inF", isInFahrenheit());

        outState.putBoolean("chronoRunning", isChronometerRunning());
        outState.putLong("chronoBase", chronometer.getBase());
        outState.putString("tempText", temperatureText.getText().toString());

        outState.putBoolean("chirpButtonEnabled", chirpButton.isEnabled());

        // Save parent state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");

        // Restore parent state
        super.onRestoreInstanceState(savedInstanceState);

        // If bundle is not null
        if(savedInstanceState!=null)
        {
            // Restore temperature
            setTemperature(savedInstanceState.getInt("temperature"));
            Log.i(TAG, ("" + savedInstanceState.getInt("temperature")));
            setInFahrenheit(savedInstanceState.getBoolean("inF"));

            // Keep the chronometer running, else keep its value
            if(savedInstanceState.getBoolean("chronoRunning")){
                startChronometer(savedInstanceState.getLong("chronoBase"));
                temperatureText.setVisibility(View.GONE);
                countdownText.setVisibility(View.VISIBLE);
            }
            else{
                temperatureText.setText(savedInstanceState.getString("tempText"));
                temperatureText.setVisibility(View.VISIBLE);
                countdownText.setVisibility(View.GONE);
            }

            // Check if the chirp button is disabled
            if(!savedInstanceState.getBoolean("chirpButtonEnabled")){
                chirpButton.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_graph:
                openGraphActivity();
                return true;
            case R.id.action_calc:
                openCalcActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

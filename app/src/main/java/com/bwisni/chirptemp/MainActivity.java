package com.bwisni.chirptemp;

import android.os.Bundle;
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


public class MainActivity extends Activity {

    public static final int CALC_REQUEST_CODE = 0;
    public static final char DEGREE = '\u00B0';
    public static final int MILLISECONDS_F = 14000;
    public static final int MILLISECONDS_C = 25000;

    // Fields
    private TextView temperatureText;
    
    //private EditText chirpsInput;
    //private EditText secondsInput;
    
    private Button chirpButton;
    private Button resetButton;

    private Chronometer chronometer;

    private TextView tempToggle;
    private TextView countdownText;
    
    // Stored values
    private long numChirps;
    private long numSeconds;
    private int temperature;

    private boolean inFahrenheit = true;
    private boolean chronometerRunning = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Get the external interface components
        temperatureText = (TextView) findViewById(R.id.temperature);
        
        chirpButton = (Button) findViewById(R.id.chirpButton);
        resetButton = (Button) findViewById(R.id.resetButton);

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

        /*
        // Calculate button
        calcButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                calculateTemperature();
            }

        });


        // Fahrenheit radio button selected
        fRadio.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                setInFahrenheit(true);
                //setSeconds(14);
                temperatureText.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                calculateTemperature();
            }
        });
        
        // Celsius radio button selected
        cRadio.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                setInFahrenheit(false);
                //setSeconds(25);
                temperatureText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                calculateTemperature();
            }
        });*/


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

        /*
        // Number of chirps changed or manually inputted
        chirpsInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                String chirps = chirpsInput.getText().toString();
                
                // Only parse the value if its not empty (avoids NumberFormatException)
                if(chirps.equals("")){
                    setNumChirps(0);
                }
                else {
                    setNumChirps(Integer.parseInt(chirps));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
                // Do nothing
                
            }
        
        });
        
        // Number of seconds changed or manually inputted
        secondsInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // Goggles
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                String secs = secondsInput.getText().toString();
                
                if(secs.equals("")){
                    setNumSeconds(0);
                }
                else {
                    setNumSeconds(Integer.parseInt(secs));
                }
                
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
                // Do nothing
            }
        
        });
        */

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
        // any appended after are INCLUDED
        ss.setSpan(new RelativeSizeSpan(0.75f), ssLeft.length(), ss.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //ss.append("s");

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
                temp = (chirps/(seconds/25))/3 + 4;
            }
        }
        
        // Cast to an int to remove decimals
        setTemperature((int) temp);
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
        String ttxt = Integer.toString(t) + DEGREE;
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
        outState.putString("countdownText", chronometer.getText().toString());
        
        outState.putBoolean("chirpButtonEnabled", chirpButton.isEnabled());
        
        // Save parent state
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onRestoreInstanceState");

        // Restore parent state
        super.onRestoreInstanceState(savedInstanceState);

        // If bundle is not null
        if(savedInstanceState!=null)
        {
            // Restore temperature
            setTemperature(savedInstanceState.getInt("temperature"));
            setInFahrenheit(savedInstanceState.getBoolean("inF"));
            
            // Keep the chronometer running, else keep its value
            if(savedInstanceState.getBoolean("chronoRunning")){
                startChronometer(savedInstanceState.getLong("chronoBase"));
            }
            else{
                chronometer.setText(savedInstanceState.getString("countdownText"));
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

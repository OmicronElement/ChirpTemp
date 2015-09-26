package com.bwisni.chirptemp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CalculatorActivity extends Activity {

    private Button calcButton;
    private EditText chirps;
    private EditText seconds;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        //final Intent intent = getIntent();

        chirps = (EditText) findViewById(R.id.chirpsInput);
        seconds = (EditText) findViewById(R.id.secondsInput);

        calcButton = (Button) findViewById(R.id.calcButton);


        calcButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // int chirps = intent.getIntExtra("numChirps", 0);
                // int seconds = intent.getIntExtra("numSeconds", 0);

                if(chirps.getText().toString().equals(""))
                    chirps.setText("0");

                if(seconds.getText().toString().equals(""))
                    seconds.setText("0");

                // Send data back to Main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("numChirps", Integer.parseInt(chirps.getText().toString()));
                intent.putExtra("numSeconds", Double.parseDouble(seconds.getText().toString()));
                setResult(RESULT_OK, intent);
                finish();
            }

        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculator, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

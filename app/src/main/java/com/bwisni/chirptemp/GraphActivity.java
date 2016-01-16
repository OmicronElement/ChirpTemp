package com.bwisni.chirptemp;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class GraphActivity extends Activity {
	
	private TextView graphTemp;
	private TextView graphChirps;
	private TextView graphSecs;
	
	private GraphView graph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		graphTemp = (TextView) findViewById(R.id.graphTemp);
		graphChirps = (TextView) findViewById(R.id.graphChirps);
		graphSecs = (TextView) findViewById(R.id.title);
		
		graph = (GraphView) findViewById(R.id.graphView);
		
		int temperature = getIntent().getIntExtra("temperature", 0);
		int numChirps = getIntent().getIntExtra("numChirps", 0);
		double numSeconds = getIntent().getDoubleExtra("numSeconds", 0);
		boolean isF = getIntent().getBooleanExtra("isF", true);
		
		String temp = "Temperature: "+temperature;
		if(isF){
			temp = temp + MainActivity.DEGREE + "F";
		}
		else{
			temp = temp + MainActivity.DEGREE + "C";
		}
		
		graphTemp.setText(temp);
		graphChirps.setText("Chirps: "+numChirps);
		graphSecs.setText("Seconds: "+numSeconds);
		
		graph.setNumSeconds(numSeconds);
		graph.setNumChirps(numChirps);
		graph.setTemperature(temperature);
		graph.invalidate();
	}

}

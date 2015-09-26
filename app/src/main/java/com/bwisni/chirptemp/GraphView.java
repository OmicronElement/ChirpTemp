package com.bwisni.chirptemp;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;


public class GraphView extends View {

	private Paint secondsPaint;
	private Paint chirpsPaint;
	private Paint temperaturePaint;
	private Paint textPaint;
	
	private double numSeconds;
	private int numChirps;
	private int temperature;
	

	public GraphView(Context context) {
		super(context);
		init();
	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();

        // Find the largest value
        double largest = numSeconds;
        if (numChirps > largest)
            largest = numChirps;
        if (temperature > largest)
            largest = temperature;

        int step = width / 5;

        // Draw bar graph

        double secsHeight = ((double) numSeconds / largest) * (height - 50);


        canvas.drawRect(0, 0, step, (int) secsHeight, secondsPaint);
        canvas.drawText("" + numSeconds, step / 2, (int) secsHeight + 50, secondsPaint);

        double chirpsHeight = ((double) numChirps / largest) * (height - 50);

        canvas.drawRect(step * 2, 0, step * 3, (int) chirpsHeight, chirpsPaint);
        canvas.drawText("" + numChirps, step * 2 + (step / 2), (int) chirpsHeight + 50, chirpsPaint);

        double tempHeight = ((double) temperature / largest) * (height - 50);

        canvas.drawRect(step * 4, 0, step * 5, (int) tempHeight, temperaturePaint);

        canvas.drawText("" + temperature + MainActivity.DEGREE, step * 4 + (step / 2), (int) tempHeight + 50, temperaturePaint);
    }

	private void init(){
		secondsPaint = new Paint(Paint.DITHER_FLAG);
		secondsPaint.setStyle(Paint.Style.FILL);
		secondsPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        secondsPaint.setTextSize(50);
        secondsPaint.setTextAlign(Align.CENTER);

		chirpsPaint = new Paint(Paint.DITHER_FLAG);
		chirpsPaint.setStyle(Paint.Style.FILL);
		chirpsPaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        chirpsPaint.setTextSize(50);
        chirpsPaint.setTextAlign(Align.CENTER);
		
		temperaturePaint = new Paint(Paint.DITHER_FLAG);
		temperaturePaint.setStyle(Paint.Style.FILL);
		temperaturePaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        temperaturePaint.setTextSize(50);
        temperaturePaint.setTextAlign(Align.CENTER);
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(50);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setColor(Color.BLACK);
	}

	public double getNumSeconds() {
		return numSeconds;
	}

	public void setNumSeconds(double numSeconds) {
		this.numSeconds = numSeconds;
	}

	public int getNumChirps() {
		return numChirps;
	}

	public void setNumChirps(int numChirps) {
		this.numChirps = numChirps;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

}


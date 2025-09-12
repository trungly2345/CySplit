package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;
import android.media.MediaPlayer;

public class CounterActivity extends AppCompatActivity {

    private TextView numberTxt; // define number textview variable
    private ImageButton increaseBtn; // define increase button variable
    private Button backBtn;     // define back button variable

    private Button resetBtn;

    private int counter = 0;    // counter variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        /* initialize UI elements */
        numberTxt = findViewById(R.id.number);
        increaseBtn = findViewById(R.id.counter_increase_btn);
        backBtn = findViewById(R.id.counter_back_btn);
        resetBtn = findViewById(R.id.reset_btn);
        MediaPlayer mp = MediaPlayer.create(CounterActivity.this, R.raw.cookie_sound);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            counter = extras.getInt("NUM");
        }

        numberTxt.setText("Snickerdoodles: " + counter);


        /* when increase btn is pressed, counter++, reset number textview */
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++counter;
                numberTxt.setText("Snickerdoodles: " + counter);
                mp.start();

                increaseBtn.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                        .withEndAction(() -> increaseBtn.animate().scaleX(1f)
                                .scaleY(1f).setDuration(100)).start();


            }
        });

        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.putExtra("NUM", counter);  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                counter = 0;
                numberTxt.setText("Snickerdoodles: " + counter);
                numberTxt.animate().rotationBy(360f).setDuration(300).start();
            }
        });

    }
}
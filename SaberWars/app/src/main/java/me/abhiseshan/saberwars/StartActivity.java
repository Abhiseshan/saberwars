package me.abhiseshan.saberwars;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {

    Button player1, player2, start;
    String player;
    int saber;
    EditText playerNameEditText;

    ImageView saber1;
    ImageView saber2;
    ImageView saber3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        player1 = (Button) findViewById(R.id.player1);
        player2 = (Button) findViewById(R.id.player2);
        start = (Button) findViewById(R.id.startButton);

        saber1 = (ImageView) findViewById(R.id.saber1);
        saber2 = (ImageView) findViewById(R.id.saber2);
        saber3 = (ImageView) findViewById(R.id.saber3);

        //Setting default stuff
        saber = 1;
        saber2.setColorFilter(R.color.grey);
        saber3.setColorFilter(R.color.grey);
        player1.setBackgroundColor(getResources().getColor(R.color.button_pressed));
        player2.setBackgroundColor(getResources().getColor(R.color.button_normal));

        player = "P1";

        saber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saber1.clearColorFilter();
                saber = 0;
                saber2.setColorFilter(R.color.grey);
                saber3.setColorFilter(R.color.grey);
            }
        });

        saber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saber2.clearColorFilter();
                saber = 1;
                saber1.setColorFilter(R.color.grey);
                saber3.setColorFilter(R.color.grey);
            }
        });

        saber3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saber3.clearColorFilter();
                saber = 2;
                saber2.setColorFilter(R.color.grey);
                saber1.setColorFilter(R.color.grey);
            }
        });

        playerNameEditText = (EditText) findViewById(R.id.nameEditText);

        player1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = "P1";
                player1.setBackgroundColor(getResources().getColor(R.color.button_pressed));
                player2.setBackgroundColor(getResources().getColor(R.color.button_normal));
            }
        });

        player2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player = "P2";
                player1.setBackgroundColor(getResources().getColor(R.color.button_normal));
                player2.setBackgroundColor(getResources().getColor(R.color.button_pressed));
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("playerName", ((playerNameEditText.getText().toString()).equals(""))?playerNameEditText.getText():"player1");
                intent.putExtra("playerNumber", player);
                intent.putExtra("saberType", saber);
                startActivity(intent);
            }
        });
    }

}

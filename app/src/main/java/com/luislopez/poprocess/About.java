package com.luislopez.poprocess;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class About extends Activity {

    protected Button buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        buy = (Button) findViewById(R.id.buy);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //On app Billing, Todavía no tengo cuenta en Google Play para probarla,
                //proximamente se implementará
            }
        });

    }



}

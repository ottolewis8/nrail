package com.android.anorthenrailway.activity.debug;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.anorthenrailway.ActivityTickets;
import com.android.anorthenrailway.R;
import com.android.anorthenrailway.TicketFactory;
import com.android.anorthenrailway.TicketInformation;
import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.io.File;

public class ActivityDebug extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityRoot.rootOwner = this; // Important
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_debug);
        setup();
    }



    private void setup() {
        Button btn2 = findViewById(R.id.button2);
        String a = Environment.getExternalStorageDirectory().getAbsolutePath();

        TicketInformation t = TicketFactory.getDefaultTicket();
        //TicketFactory.save(t);
        TicketFactory.load("default");

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ActivityDebug.this, ActivityTickets.class));

                TicketFactory.getCustomFileNames();


                TicketInformation t = TicketFactory.getDefaultTicket();

                TicketFactory.save(t);
            }
        });
    }
}

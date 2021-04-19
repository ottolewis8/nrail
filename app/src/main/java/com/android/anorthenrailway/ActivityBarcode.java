package com.android.anorthenrailway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityBarcode extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        setup();
    }

    /*@Override
    public void onBackPressed() {
        if (true) {
            // Do nothing
            this.finishAffinity();
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onResume () {
        super.onResume();
        setup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setup() {
        Button btn = findViewById(R.id.aBarcodeBarcodeBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityBarcode.this, ActivityTickets.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = null;

        TicketInformation ticket = TicketFactory.activeTicket;
        TicketJourney journey = TicketFactory.activeOutTicket ?
                ticket.outJourney.get(TicketFactory.activeOutIndex) :
                ticket.rtnJourney.get(TicketFactory.activeRtnIndex);

        if (TicketFactory.activeOutTicket) {
            ((TextView)findViewById(R.id.aBarcodeDepartNameTxt)).setText(ticket.outStationName);
            ((TextView)findViewById(R.id.aBarcodeArriveNameTxt)).setText(ticket.rtnStationName);
        } else {
            ((TextView)findViewById(R.id.aBarcodeDepartNameTxt)).setText(ticket.rtnStationName);
            ((TextView)findViewById(R.id.aBarcodeArriveNameTxt)).setText(ticket.outStationName);
        }
        ((TextView)findViewById(R.id.aBarcodeDepartTimeTxt)).setText(journey.outTime);
        ((TextView)findViewById(R.id.aBarcodeArriveTimeTxt)).setText(journey.rtnTime);
        ((TextView)findViewById(R.id.aBarcodeTicketTopTxt)).setText("Activated: " + TicketFactory.activeTicket.getActivatedTimeStr());
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 04);
        format = new SimpleDateFormat("E MMM dd YYYY HH:mm");
        ((TextView)findViewById(R.id.aBarcodeBtmValueTxt5)).setText(format.format(cal.getTime()));
        ((TextView)findViewById(R.id.aBarcodeBtmValueTxt3)).setText(journey.price);
    }
}

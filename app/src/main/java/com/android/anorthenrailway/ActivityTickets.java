package com.android.anorthenrailway;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityTickets extends AppCompatActivity {
    private boolean moveRight = true;

    private int opacity = 0;
    private boolean opacityInc = true;

    private Timer timerUI = new Timer();
    private TimerTask updateUITask = new UpdateMovingTime();

    public static TicketInformation ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
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
    public void onPause() {
        super.onPause();
        try {
            timerUI.cancel();
        } catch (Exception e) {}
    }

    @Override
    public void onResume() {
        super.onResume();
        setup();
        try {
            timerUI = new Timer();
            updateUITask = new UpdateMovingTime();
            timerUI.scheduleAtFixedRate(updateUITask, 0, 50);
        } catch (Exception e) {}
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

    public void updateUI() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) findViewById(R.id.aTicketsMovingTimeTxt);

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                int moveAmmount = size.x / 150;
                int w = size.x / 24 * 11;
                w = w + (w % moveAmmount);

                int padding = tv.getPaddingLeft();
                padding = moveRight ? padding + moveAmmount : padding - moveAmmount;
                if (padding == 0) {
                    moveRight = true;
                }
                else if (padding >= w)
                {
                    moveRight = false;
                }
                tv.setPadding(padding, 0, 0, 0);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                tv.setText(format.format(cal.getTime()));

                opacity = opacityInc ? opacity + 1 : opacity - 1;
                if (opacity == 7) {
                    opacityInc = true;
                }
                else if (opacity == 15) {
                    opacityInc = false;
                }
                String op = Integer.toHexString(opacity) + Integer.toHexString(opacity);
                tv = (TextView) findViewById(R.id.aTicketsLeftColour);
                tv.setBackgroundColor(Color.parseColor("#" + op + "b248d9"));
                tv = (TextView) findViewById(R.id.aTicketsCentreColour);
                tv.setBackgroundColor(Color.parseColor("#" + op + "00aabf"));
                tv = (TextView) findViewById(R.id.aTicketsRightColour);
                tv.setBackgroundColor(Color.parseColor("#" + op + "0a1dff"));
            }
        });
    }

    private void setup() {
        Button btn = findViewById(R.id.aTicketsTicketBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityTickets.this, ActivityBarcode.class));
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
            ((TextView)findViewById(R.id.aTicketsDepartNameTxt)).setText(ticket.outStationName);
            ((TextView)findViewById(R.id.aTicketsArriveNameTxt)).setText(ticket.rtnStationName);
            ((TextView)findViewById(R.id.aTicketsTxt3)).setText("OUT (2-Part Retrun)");
            String tmp = ticket.outStationName + "\n        " + ticket.rtnStationName;
            ((TextView)findViewById(R.id.aTicketsTxt5)).setText(tmp);
        } else {
            ((TextView)findViewById(R.id.aTicketsDepartNameTxt)).setText(ticket.rtnStationName);
            ((TextView)findViewById(R.id.aTicketsArriveNameTxt)).setText(ticket.outStationName);
            ((TextView)findViewById(R.id.aTicketsTxt3)).setText("RTN (2-Part Retrun)");
            String tmp = ticket.rtnStationName + "\n        " + ticket.outStationName;
            ((TextView)findViewById(R.id.aTicketsTxt5)).setText(tmp);
        }
        ((TextView)findViewById(R.id.aTicketsDepartTimeTxt)).setText(journey.outTime);
        ((TextView)findViewById(R.id.aTicketsArriveTimeTxt)).setText(journey.rtnTime);

        format = new SimpleDateFormat("E dd MMM YYYY");
        ((TextView)findViewById(R.id.aTicketsTxt7)).setText("Travel on: "+ format.format(cal.getTime()));

        ((TextView)findViewById(R.id.aTicketsTicketTopTxt)).setText("Activated: " + ticket.getActivatedTimeStr());
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 04);
        format = new SimpleDateFormat("E MMM dd YYYY HH:mm");
        ((TextView)findViewById(R.id.aTicketsBtmValueTxt3)).setText(format.format(cal.getTime()));
        cal.set(Calendar.MINUTE, 11);
        ((TextView)findViewById(R.id.aTicketsBtmValueTxt4)).setText(format.format(cal.getTime()));
        cal = Calendar.getInstance();
        ((TextView)findViewById(R.id.aTicketsBtmValueTxt5)).setText(format.format(cal.getTime()));
        ((TextView)findViewById(R.id.aTicketsTxt8)).setText("Price: " + journey.price);
    }

    class UpdateMovingTime extends TimerTask {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        }
    }


}

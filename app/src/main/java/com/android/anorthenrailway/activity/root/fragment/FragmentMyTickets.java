package com.android.anorthenrailway.activity.root.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.anorthenrailway.R;
import com.android.anorthenrailway.TicketFactory;
import com.android.anorthenrailway.TicketInformation;
import com.android.anorthenrailway.TicketJourney;
import com.android.anorthenrailway.activity.ActivityEnum;
import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMyTickets.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMyTickets#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMyTickets extends Fragment {

    private OnFragmentInteractionListener mListener;

    public FragmentMyTickets() {
        init();
    }

    private void init() {
        TicketFactory.loadSettings();
        final AppCompatActivity owner = ActivityRoot.rootOwner;
        final Button outTab = ((Button)owner.findViewById(R.id.aMyTicketsOutboundTabBTN));
        final Button retTab = ((Button)owner.findViewById(R.id.aMyTicketsReturnTabBTN));

        if (outTab == null) return;

        // Outbound Tab
        outTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketFactory.activeOutTicket = true;
                TicketFactory.saveSettings();
                updateUI();
            }
        });

        // Return Tab
        retTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketFactory.activeOutTicket = false;
                TicketFactory.saveSettings();
                updateUI();
            }
        });

        ((Button)owner.findViewById(R.id.aMyTicketsViewTicketsBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketFactory.activeOutTicket = TicketFactory.activeOutTicket;
                ((ActivityRoot)ActivityRoot.rootOwner).changeActivity(ActivityEnum.TICKETS);
            }
        });

        updateUI();
    }

    private void updateTabStyles(boolean active) {
        final AppCompatActivity owner = ActivityRoot.rootOwner;
        final Button outTab = ((Button)owner.findViewById(R.id.aMyTicketsOutboundTabBTN));
        final Button retTab = ((Button)owner.findViewById(R.id.aMyTicketsReturnTabBTN));
        if (active) {
            outTab.setBackground(ContextCompat.getDrawable(owner, R.drawable.fake_tab_button_focused_left_my_tickets));
            outTab.setTextColor(ContextCompat.getColor(owner, R.color.colourTextDark));
            retTab.setBackground(ContextCompat.getDrawable(owner, R.drawable.fake_tab_button_nfocused_my_tickets));
            retTab.setTextColor(ContextCompat.getColor(owner, R.color.colourTextLight));
        } else {
            outTab.setBackground(ContextCompat.getDrawable(owner, R.drawable.fake_tab_button_nfocused_my_tickets_alt));
            outTab.setTextColor(ContextCompat.getColor(owner, R.color.colourTextLight));
            retTab.setBackground(ContextCompat.getDrawable(owner, R.drawable.fake_tab_button_focused_right_my_tickets));
            retTab.setTextColor(ContextCompat.getColor(owner, R.color.colourTextDark));
        }
    }

    private void updateUI() {
        final AppCompatActivity owner = ActivityRoot.rootOwner;
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM YYYY");

        ((TextView)owner.findViewById(R.id.aMyTicketsTravelDateTXT)).setText(format.format(Calendar.getInstance().getTime()));

        TicketInformation ticket = TicketFactory.activeTicket;
        TicketJourney journey = TicketFactory.activeOutTicket ?
                ticket.outJourney.get(TicketFactory.activeOutIndex) :
                ticket.rtnJourney.get(TicketFactory.activeRtnIndex);

        ((TextView)owner.findViewById(R.id.aMyTicketsOutTimeTXT)).setText(journey.outTime);
        ((TextView)owner.findViewById(R.id.aMyTicketsRtnTimeTXT)).setText(journey.rtnTime);

        if (TicketFactory.activeOutTicket) {
            ((TextView)owner.findViewById(R.id.aMyTicketsDestinationNameTXT)).setText(ticket.outStationName);
            ((TextView)owner.findViewById(R.id.aMyTicketsOutNameTXT)).setText(ticket.outStationName);
            ((TextView)owner.findViewById(R.id.aMyTicketsRtnNameTXT)).setText(ticket.rtnStationName);
        }
        else {
            ((TextView)owner.findViewById(R.id.aMyTicketsDestinationNameTXT)).setText(ticket.rtnStationName);
            ((TextView)owner.findViewById(R.id.aMyTicketsOutNameTXT)).setText(ticket.rtnStationName);
            ((TextView)owner.findViewById(R.id.aMyTicketsRtnNameTXT)).setText(ticket.outStationName);
        }

        String duration = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date outTime = sdf.parse(journey.outTime);
            Date rtnTime = sdf.parse(journey.rtnTime);
            long diff = rtnTime.getTime() - outTime.getTime();
            long hours = diff / (1000 * 60 * 60);
            diff = diff - (hours * (1000 * 60 * 60));
            long minutes = diff / (1000 * 60);
            if (hours != 0) duration += Math.abs(hours) + "h ";
            duration += minutes + "m";
        } catch (Exception e) {
            duration = "43mˌ";
        }
        ((TextView)owner.findViewById(R.id.aMyTicketsDurationTXT)).setText(duration);
        updateTabStyles(TicketFactory.activeOutTicket);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMore.
     */
    public static FragmentMyTickets newInstance() {
        FragmentMyTickets fragment = new FragmentMyTickets();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

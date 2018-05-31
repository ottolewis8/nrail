package com.android.anorthenrailway;

import android.widget.Toast;

import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TicketInformation {

    public boolean activeisOut = false;

    public String groupName = "groupName";
    public String outStationName = "outStationName";
    public String rtnStationName = "rtnStationName";
    public List<TicketJourney> outJourney = new ArrayList<TicketJourney>();
    public List<TicketJourney> rtnJourney = new ArrayList<TicketJourney>();

    public String getActivatedTimeStr() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 11);
        SimpleDateFormat format = new SimpleDateFormat("d MMM YYYY HH:mm");
        return format.format(cal.getTime());
    }

    public static List<String> serialise(TicketInformation ticket) {
        List<String> list = new ArrayList<String>();
        list.add(GROUP_NAME + EQUALS + ticket.groupName);
        list.add(OUT_STATION_NAME + EQUALS + ticket.outStationName);
        list.add(RTN_STATION_NAME + EQUALS + ticket.rtnStationName);

        for (TicketJourney j : ticket.outJourney) {
            String tmp = OUT_JOURNEY + EQUALS;
            tmp += OUT_TIME + EQUALS + j.outTime;
            tmp += SEPARATOR + RTN_TIME + EQUALS + j.rtnTime;
            tmp += SEPARATOR + PRICE + EQUALS + j.price;
            list.add(tmp);
        }

        for (TicketJourney j : ticket.rtnJourney) {
            String tmp = RTN_JOURNEY + EQUALS;
            tmp += OUT_TIME + EQUALS + j.outTime;
            tmp += SEPARATOR + RTN_TIME + EQUALS + j.rtnTime;
            tmp += SEPARATOR + PRICE + EQUALS + j.price;
            list.add(tmp);
        }
        return list;
    }

    public static TicketInformation deserialise(List<String> list) {
        TicketInformation ticket = new TicketInformation();
        for (String line: list) {
            int index = line.indexOf(EQUALS);
            String key = line.substring(0, index);
            String value = line.substring(index + 1, line.length());
            switch (key) {
                case GROUP_NAME:
                    ticket.groupName = value;
                    break;

                case OUT_STATION_NAME:
                    ticket.outStationName = value;
                    break;

                case RTN_STATION_NAME:
                    ticket.rtnStationName = value;
                    break;

                case OUT_JOURNEY:
                case RTN_JOURNEY:
                    String[] keys = value.split(SEPARATOR);
                    TicketJourney journey = new TicketJourney();
                    for (String subLine : keys) {
                        index = subLine.indexOf(EQUALS);
                        String subKey = subLine.substring(0, index);
                        value = subLine.substring(index + 1, subLine.length());
                        switch (subKey) {
                            case OUT_TIME:
                                journey.outTime = value;
                                break;

                            case RTN_TIME:
                                journey.rtnTime = value;
                                break;

                            case PRICE:
                                journey.price = value;
                                break;
                        }
                    }
                    if (key.equals(OUT_JOURNEY)) {
                        ticket.outJourney.add(journey);
                    } else {
                        ticket.rtnJourney.add(journey);
                    }
                    break;

                default:
                    Toast.makeText(ActivityRoot.rootOwner, "Unknown deserialise key: " + key, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
       return ticket;
    }

    public List<String> getOutJourneyStringList() {
        List<String> list = new ArrayList<String>();
        for (TicketJourney i: outJourney) {
            list.add(i.outTime + " - " + i.rtnTime);
        }
        return list;
    }

    public List<String> getRtnJourneyStringList() {
        List<String> list = new ArrayList<String>();
        for (TicketJourney i: rtnJourney) {
            list.add(i.outTime + " - " + i.rtnTime);
        }
        return list;
    }

    private final static String SEPARATOR = ",";
    private final static String EQUALS = "=";
    private final static String GROUP_NAME = "groupName";
    private final static String OUT_STATION_NAME = "outStationName";
    private final static String RTN_STATION_NAME = "rtnStationNam";
    private final static String OUT_JOURNEY = "outJourney";
    private final static String RTN_JOURNEY = "rtnJourney";
    private final static String OUT_TIME = "outTime";
    private final static String RTN_TIME = "rtnTime";
    private final static String PRICE = "price";
}

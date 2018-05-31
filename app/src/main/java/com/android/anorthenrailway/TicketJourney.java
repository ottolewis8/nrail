package com.android.anorthenrailway;

public class TicketJourney {

    public String outTime = "outTime";
    public String rtnTime = "rtnTime";
    public String price = "price";

    public static TicketJourney getDefault()
    {
        TicketJourney journey = new TicketJourney();
        journey.outTime = "00:00";
        journey.rtnTime = "00:00";
        journey.price = "£00.00";
        return journey;
    }
}

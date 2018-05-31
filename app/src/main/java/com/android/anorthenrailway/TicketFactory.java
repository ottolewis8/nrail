package com.android.anorthenrailway;

import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TicketFactory {

    public static TicketInformation activeTicket = getDefaultTicket();
    public static int activeOutIndex;
    public static int activeRtnIndex;
    public static boolean activeOutTicket;

    private static final String DEFAULT_NAME = "default";
    // TODO crash on first ever run with empty rootPath
    private static final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "nrail" + File.separator; //ActivityRoot.rootOwner.getDataDir().getAbsoluteFile() + File.separator;
    private static final String ticketPath = rootPath + "tickets" + File.separator;
    private static final String settingsFilePath = rootPath + "settings";

    public static TicketInformation getDefaultTicket() {
        TicketInformation ticket = new TicketInformation();
        ticket.groupName = DEFAULT_NAME;
        ticket.outStationName = "Depart Station";
        ticket.rtnStationName = "Arrive Station";
        ticket.outJourney.add(TicketJourney.getDefault());
        ticket.rtnJourney.add(TicketJourney.getDefault());
        return ticket;
    }

    public static void setActiveTicket(TicketInformation ticket, int outIndex, int rtnIndex) {
        activeTicket = ticket;
        activeOutIndex = outIndex;
        activeRtnIndex = rtnIndex;
    }

    private final static String ACTIVE_TICKET = "activeTicket";
    private final static String ACTIVE_OUT_STR = "activeOutIndex";
    private final static String ACTIVE_RTN_STR = "activeRtnIndex";
    private final static String ACTIVE_OUT_TICKET = "activeOutTicket";

    public static boolean saveSettings() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFilePath));
            HashMap<String, String> sData = new HashMap<String, String>();

            sData.put(ACTIVE_TICKET, activeTicket.groupName);
            sData.put(ACTIVE_OUT_STR, String.valueOf(activeOutIndex));
            sData.put(ACTIVE_RTN_STR, String.valueOf(activeRtnIndex));
            sData.put(ACTIVE_OUT_TICKET, String.valueOf(activeOutTicket));

            oos.writeObject(sData);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean loadSettings()
    {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settingsFilePath));
            HashMap<String, String> sData = ( HashMap<String, String>) ois.readObject();
            ois.close();

            String tmp = sData.get(ACTIVE_OUT_STR);
            if (tmp == null) activeOutIndex = 0;
            else activeOutIndex = Integer.valueOf(tmp);

            tmp = sData.get(ACTIVE_RTN_STR);
            if (tmp == null) activeRtnIndex = 0;
            else activeRtnIndex = Integer.valueOf(tmp);

            tmp = sData.get(ACTIVE_TICKET);
            if (tmp == null) {
                activeTicket = getDefaultTicket();
            }
            else {
                activeTicket = load(tmp);
            }

            activeOutTicket = Boolean.parseBoolean(sData.get(ACTIVE_OUT_TICKET));

            return true;
        } catch (Exception ex) {
            if (ex instanceof FileNotFoundException) {
                setActiveTicket(getDefaultTicket(), 0, 0);
                return true;
            }
            else {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static List<String> getCustomFileNames() {
        File folder = new File(ticketPath);
        File[] listOfFiles = folder.listFiles();

        boolean foundDefault = false;

        List<String> ret = new ArrayList<String>();
        if (listOfFiles == null) return ret;
        for (int i = 0; i < listOfFiles.length; i++) {
            ret.add(listOfFiles[i].getName());
            if (listOfFiles[i].getName().equals(DEFAULT_NAME)) foundDefault = true;
        }

        if((ret.size() == 0) || (!foundDefault)) {
            ret.add(DEFAULT_NAME);
            TicketFactory.save(TicketFactory.getDefaultTicket());
        }
        return ret;
    }

    public static boolean rename(String oldName, String newName) {
        try {
            TicketInformation ticket = load(oldName);
            ticket.groupName = newName;
            File existingFile = new File(ticketPath + oldName);
            existingFile.delete();
            save(ticket);
            getCustomFileNames();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean save(TicketInformation ticket)
    {
        if (ticket == null) return false;
        try {
            try { new File(ticketPath).mkdirs(); } catch (Exception f) {}
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ticketPath + ticket.groupName));
            if (ticket.outJourney.size() == 0) ticket.outJourney.add(new TicketJourney());
            if (ticket.rtnJourney.size() == 0) ticket.rtnJourney.add(new TicketJourney());
            List<String> sData = TicketInformation.serialise(ticket);
            oos.writeObject(sData);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static TicketInformation load(String name)
    {
        try {
            try { new File(ticketPath).mkdirs(); } catch (Exception f) {}
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ticketPath + name));
            List<String> sData = (List<String>) ois.readObject();
            ois.close();
            TicketInformation ticket = TicketInformation.deserialise(sData);
            return ticket;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean delete(String name)
    {
        try {
            File file = new File(ticketPath + name);
            file.delete();
            if (name.equals(DEFAULT_NAME)) save(getDefaultTicket());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

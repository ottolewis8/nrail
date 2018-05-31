package com.android.anorthenrailway.activity.root.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.BaseKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.anorthenrailway.BuildConfig;
import com.android.anorthenrailway.R;
import com.android.anorthenrailway.TicketFactory;
import com.android.anorthenrailway.TicketInformation;
import com.android.anorthenrailway.TicketJourney;
import com.android.anorthenrailway.activity.root.ActivityRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMore.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMore#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMore extends Fragment {

    private OnFragmentInteractionListener mListener;

    public FragmentMore() {
        try {
            init();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean editButtonToggle = true;
    boolean onCreate = true;

    private void init() {
        final AppCompatActivity owner = ActivityRoot.rootOwner;

        // Custom selection spinner
        final Spinner customSpinner = ((Spinner) owner.findViewById(R.id.aMoreCustomFileSP));
        if (customSpinner == null) return;
        onCreate = true;

        Thread thread = new Thread("tick") {
            public void run(){
                try {
                    Thread.sleep(1000);
                    onCreate = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        updateGroupSpinnerAdapter(TicketFactory.getCustomFileNames(), R.id.aMoreCustomFileSP);
        int selectedIndex = TicketFactory.getCustomFileNames().indexOf(TicketFactory.activeTicket.groupName);
        customSpinner.setSelection(selectedIndex);

        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = customSpinner.getSelectedItem().toString();
                if (!name.equals("")) {
                    TicketInformation ticket;
                    ticket = TicketFactory.load(name);
                    if (ticket != null) {
                        ((EditText) owner.findViewById(R.id.aMoreCustomOutNameET)).setText(ticket.outStationName);
                        ((EditText) owner.findViewById(R.id.aMoreCustomRtnNameET)).setText(ticket.rtnStationName);
                        updateGroupSpinnerAdapter(ticket.getOutJourneyStringList(), R.id.aMoreOutSelectSP);
                        updateGroupSpinnerAdapter(ticket.getRtnJourneyStringList(), R.id.aMoreRtnSelectSP);
                        if (onCreate) {
                            ((Spinner)owner.findViewById(R.id.aMoreOutSelectSP)).setSelection(TicketFactory.activeOutIndex);
                            ((Spinner)owner.findViewById(R.id.aMoreRtnSelectSP)).setSelection(TicketFactory.activeRtnIndex);
                        }
                    } else {
                        Toast.makeText(ActivityRoot.rootOwner, "Failed to load ticket: " + name, Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // New ticket group
        ((Button)owner.findViewById(R.id.aMoreCustomNewBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketInformation ticket = TicketFactory.getDefaultTicket();
                ticket.groupName = java.util.UUID.randomUUID().toString().substring(0, 5);
                TicketFactory.save(ticket);
                ArrayAdapter spinnerAdapter = (ArrayAdapter) customSpinner.getAdapter();
                spinnerAdapter.insert(ticket.groupName, customSpinner.getSelectedItemPosition());
            }
        });

        // Delete ticket group
        ((Button)owner.findViewById(R.id.aMoreCustomDelBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            String name = (String)customSpinner.getSelectedItem();
                            if (TicketFactory.delete(name)) {
                                updateGroupSpinnerAdapter(TicketFactory.getCustomFileNames(), customSpinner.getId());
                                Toast.makeText(ActivityRoot.rootOwner, "Deleted Ticket: " + name, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ActivityRoot.rootOwner, "Failed Deleted Ticket: " + name, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                };
                new AlertDialog.Builder(ActivityRoot.rootOwner).setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        // Out selection spinner
        final Spinner outSpinner = ((Spinner)owner.findViewById(R.id.aMoreOutSelectSP));
        outSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String)customSpinner.getSelectedItem();
                TicketInformation ticket = TicketFactory.load(name);
                ((TextView)owner.findViewById(R.id.aMoreCustomOutDepartTimeET)).setText(ticket.outJourney.get(position).outTime);
                ((TextView)owner.findViewById(R.id.aMoreCustomOutArriveTimeET)).setText(ticket.outJourney.get(position).rtnTime);
                ((TextView)owner.findViewById(R.id.aMoreCustomPriceOutET)).setText(ticket.outJourney.get(position).price);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Out spinner new item
        ((Button)owner.findViewById(R.id.aMoreOutNewBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketFactory.save(getTicketFromUI());
                String name = (String)customSpinner.getSelectedItem();
                TicketInformation ticket = TicketFactory.load(name);
                ticket.outJourney.add(TicketJourney.getDefault());
                TicketFactory.save(ticket);
                updateGroupSpinnerAdapter(ticket.getOutJourneyStringList(), outSpinner.getId());
                outSpinner.setSelection(ticket.outJourney.size() - 1);
            }
        });

        // Out spinner delete item
        ((Button)owner.findViewById(R.id.aMoreOutDelBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            String name = (String)customSpinner.getSelectedItem();
                            TicketInformation ticket = TicketFactory.load(name);
                            ticket.outJourney.remove(outSpinner.getSelectedItemPosition());
                            if (ticket.outJourney.size() == 0) ticket.outJourney.add(TicketJourney.getDefault());
                            TicketFactory.save(ticket);
                            updateGroupSpinnerAdapter(ticket.getOutJourneyStringList(), outSpinner.getId());
                        }
                    }
                };
                new AlertDialog.Builder(ActivityRoot.rootOwner).setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        // Rtn selection spinner
        final Spinner rtnSpinner = ((Spinner)owner.findViewById(R.id.aMoreRtnSelectSP));
        rtnSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String)customSpinner.getSelectedItem();
                TicketInformation ticket = TicketFactory.load(name);
                ((TextView)owner.findViewById(R.id.aMoreCustomRtnDepartTimeET)).setText(ticket.rtnJourney.get(position).outTime);
                ((TextView)owner.findViewById(R.id.aMoreCustomRtnArriveTimeET)).setText(ticket.rtnJourney.get(position).rtnTime);
                ((TextView)owner.findViewById(R.id.aMoreCustomPriceRtnET)).setText(ticket.rtnJourney.get(position).price);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });


        // Rtn spinner new item
        ((Button)owner.findViewById(R.id.aMoreRtnNewBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketFactory.save(getTicketFromUI());
                String name = (String)customSpinner.getSelectedItem();
                TicketInformation ticket = TicketFactory.load(name);
                ticket.rtnJourney.add(TicketJourney.getDefault());
                TicketFactory.save(ticket);
                updateGroupSpinnerAdapter(ticket.getRtnJourneyStringList(), rtnSpinner.getId());
                rtnSpinner.setSelection(ticket.rtnJourney.size() - 1);
            }
        });

        // Rtn spinner delete item
        ((Button)owner.findViewById(R.id.aMoreRtnDelBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            String name = (String)customSpinner.getSelectedItem();
                            TicketInformation ticket = TicketFactory.load(name);
                            ticket.rtnJourney.remove(rtnSpinner.getSelectedItemPosition());
                            if (ticket.rtnJourney.size() == 0) ticket.rtnJourney.add(TicketJourney.getDefault());
                            TicketFactory.save(ticket);
                            updateGroupSpinnerAdapter(ticket.getRtnJourneyStringList(), rtnSpinner.getId());
                        }
                    }
                };
                new AlertDialog.Builder(ActivityRoot.rootOwner).setMessage("Are you sure?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


        // Edit custom button
        final Button editButton = ((Button)owner.findViewById(R.id.aMoreCustomEditBTN));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText customText = (EditText) owner.findViewById(R.id.aMoreCustomFileET);
                int index = customSpinner.getSelectedItemPosition();
                ArrayAdapter spinnerAdapter = (ArrayAdapter) customSpinner.getAdapter();
                String existingName = (String) spinnerAdapter.getItem(index);

                if (editButtonToggle) {
                    editButton.setBackgroundTintList(ContextCompat.getColorStateList(owner, R.color.colourButtonSelected));
                    customText.setText(existingName);
                    customSpinner.setVisibility(View.INVISIBLE);
                    customText.setVisibility(View.VISIBLE);
                } else {
                    editButton.setBackgroundTintList(ContextCompat.getColorStateList(owner, R.color.colourButtonNormal));
                    String newName = customText.getText().toString();
                    if (!TicketFactory.rename(existingName, newName)) {
                        Toast.makeText(ActivityRoot.rootOwner, "Failed to rename ticket", Toast.LENGTH_LONG).show();
                    }
                    List<String> names = TicketFactory.getCustomFileNames();
                    updateGroupSpinnerAdapter(names, customSpinner.getId());
                    customSpinner.setSelection(names.indexOf(newName));
                    customSpinner.setVisibility(View.VISIBLE);
                    customText.setVisibility(View.INVISIBLE);
                }

                // Disable / enable all
                ConstraintLayout cl = ((ConstraintLayout)owner.findViewById(R.id.aMoreCustomCL));
                for (int i = 0; i < cl.getChildCount(); i++) {
                    View child = cl.getChildAt(i);
                    if ((child.getId() != editButton.getId()) && (child.getId() != customText.getId())) {
                        child.setEnabled(!editButtonToggle);
                        child.setAlpha(!editButtonToggle ? 1.0f : 0.5f);
                    }
                }
                editButtonToggle = !editButtonToggle;
            }
        });


        // Apply ticket custom
        ((Button)owner.findViewById(R.id.aMoreCustomApplyBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketInformation ticket = getTicketFromUI();
                TicketFactory.setActiveTicket(ticket, outSpinner.getSelectedItemPosition(), rtnSpinner.getSelectedItemPosition());
                TicketFactory.saveSettings();
                Toast.makeText(ActivityRoot.rootOwner, "Set Custom", Toast.LENGTH_LONG).show();
            }
        });


        // Station name edit save
        int[] nameIDList = {R.id.aMoreCustomOutNameET, R.id.aMoreCustomRtnNameET};
        for (int i: nameIDList) {
            final EditText et = (EditText)owner.findViewById(i);
            et.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (!hasFocus) {
                        String name = (String) customSpinner.getSelectedItem();
                        TicketInformation ticket = TicketFactory.load(name);
                        ticket.outStationName = ((EditText)owner.findViewById(R.id.aMoreCustomOutNameET)).getText().toString();
                        ticket.rtnStationName = ((EditText)owner.findViewById(R.id.aMoreCustomRtnNameET)).getText().toString();
                        TicketFactory.save(ticket);
                    }
                }
            });
        }

        ((Button)owner.findViewById(R.id.aMoreRtnSavBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketInformation ticket = getTicketFromUI();
                Collections.sort(ticket.rtnJourney, new Comparator<TicketJourney>() {
                    @Override
                    public int compare(TicketJourney o1, TicketJourney o2) {
                        int comp = o1.outTime.compareTo(o2.outTime);
                        if (comp == 0) return o1.rtnTime.compareTo(o2.rtnTime);
                        return comp;
                    }
                });
                TicketFactory.save(ticket);
                int index = rtnSpinner.getSelectedItemPosition();
                updateGroupSpinnerAdapter(ticket.getRtnJourneyStringList(), R.id.aMoreRtnSelectSP);
                List<String> items = ticket.getRtnJourneyStringList();
                String compare = ((TextView)owner.findViewById(R.id.aMoreCustomRtnDepartTimeET)).getText() + " - " + ((TextView)owner.findViewById(R.id.aMoreCustomRtnArriveTimeET)).getText();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).equals(compare))
                    {
                        rtnSpinner.setSelection(i);
                        return;
                    }
                }
                Toast.makeText(ActivityRoot.rootOwner, "Error in updating rtn spinner correctly", Toast.LENGTH_LONG);
            }
        });

        ((Button)owner.findViewById(R.id.aMoreOutSavBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketInformation ticket = getTicketFromUI();
                Collections.sort(ticket.outJourney, new Comparator<TicketJourney>() {
                    @Override
                    public int compare(TicketJourney o1, TicketJourney o2) {
                        int comp = o1.outTime.compareTo(o2.outTime);
                        if (comp == 0) return o1.rtnTime.compareTo(o2.rtnTime);
                        return comp;
                    }
                });
                TicketFactory.save(ticket);
                int index = outSpinner.getSelectedItemPosition();
                updateGroupSpinnerAdapter(ticket.getOutJourneyStringList(), R.id.aMoreOutSelectSP);
                List<String> items = ticket.getOutJourneyStringList();
                String compare = ((TextView)owner.findViewById(R.id.aMoreCustomOutDepartTimeET)).getText() + " - " + ((TextView)owner.findViewById(R.id.aMoreCustomOutArriveTimeET)).getText();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).equals(compare))
                    {
                        outSpinner.setSelection(i);
                        return;
                    }
                }
                Toast.makeText(ActivityRoot.rootOwner, "Error in updating out spinner correctly", Toast.LENGTH_LONG);
            }
        });

        // EditText save exit
        /*int[] journeyIDOutList = { R.id.aMoreCustomOutDepartTimeET, R.id.aMoreCustomOutArriveTimeET, R.id.aMoreCustomPriceOutET };
        for (int i: journeyIDOutList) {
            ((EditText)owner.findViewById(i)).setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override public void onFocusChange(View v, boolean hasFocus) {}
            });
        }
        int[] journeyIDRtnList = { R.id.aMoreCustomRtnDepartTimeET, R.id.aMoreCustomRtnArriveTimeET, R.id.aMoreCustomPriceRtnET };
        for (int i: journeyIDRtnList) {
            ((EditText)owner.findViewById(i)).setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override public void onFocusChange(View v, boolean hasFocus) {}
            });
        }*/

        // Show secret
        final List<Integer> secret = new ArrayList<Integer>();
        final int secretCheck[] = {0, 0, 1, 1, 0};
        final ImageView btm = ((ImageView)owner.findViewById(R.id.aMoreBodyBtmIMG));
        final ImageView top = ((ImageView)owner.findViewById(R.id.aMoreBodyTopIMG));
        final ConstraintLayout cl = ((ConstraintLayout)owner.findViewById(R.id.aMoreCustomCL));
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secret.add(0);
                if (secret.size() == secretCheck.length) {
                    boolean found = true;
                    for (int i = 0; i < secretCheck.length; i++) {
                        if (secretCheck[i] != secret.get(i)) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        btm.setVisibility(View.INVISIBLE);
                        top.setVisibility(View.INVISIBLE);
                        cl.setVisibility(View.VISIBLE);
                    }
                    secret.clear();
                }
            }
        });
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secret.add(1);
            }
        });
        btm.setVisibility(View.VISIBLE);
        top.setVisibility(View.VISIBLE);
        cl.setVisibility(View.INVISIBLE);
        ((TextView)owner.findViewById(R.id.aMoreAppVersionTXT)).setText("app version: " + BuildConfig.VERSION_NAME);
        outSpinner.setSelection(TicketFactory.activeOutIndex);
        rtnSpinner.setSelection(TicketFactory.activeRtnIndex);
    }

    private TicketInformation getTicketFromUI() {
        try {
            Spinner spinner = ((Spinner) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomFileSP));
            String current = (String) spinner.getSelectedItem();
            TicketInformation ticket = TicketFactory.load(current);
            ticket.outStationName = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomOutNameET)).getText().toString();
            ticket.rtnStationName = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomRtnNameET)).getText().toString();

            int index = ((Spinner) ActivityRoot.rootOwner.findViewById(R.id.aMoreOutSelectSP)).getSelectedItemPosition();
            ticket.outJourney.get(index).outTime = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomOutDepartTimeET)).getText().toString();
            ticket.outJourney.get(index).rtnTime = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomOutArriveTimeET)).getText().toString();
            ticket.outJourney.get(index).price = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomPriceOutET)).getText().toString();

            index = ((Spinner) ActivityRoot.rootOwner.findViewById(R.id.aMoreRtnSelectSP)).getSelectedItemPosition();
            ticket.rtnJourney.get(index).outTime = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomRtnDepartTimeET)).getText().toString();
            ticket.rtnJourney.get(index).rtnTime = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomRtnArriveTimeET)).getText().toString();
            ticket.rtnJourney.get(index).price = ((TextView) ActivityRoot.rootOwner.findViewById(R.id.aMoreCustomPriceRtnET)).getText().toString();
            return ticket;
        } catch (Exception e) {
            return null;
        }
    }

    private void updateGroupSpinnerAdapter(List<String> list, int spinnerRef) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityRoot.rootOwner, android.R.layout.simple_list_item_1, list);
        Spinner spinner = (Spinner) ActivityRoot.rootOwner.findViewById(spinnerRef);
        spinner.setAdapter(adapter);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMore.
     */
    public static FragmentMore newInstance() {
        FragmentMore fragment = new FragmentMore();

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

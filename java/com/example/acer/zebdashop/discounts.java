package com.example.acer.zebdashop;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class discounts extends Fragment {
    RecyclerView recyclerView;
    Context context;
    int tag = 0;
    String names[];
    private AdView mAdView;
    ArrayList<discount_class> arrayList_forSearch;
    AutoCompleteTextView textView;
    RecyclerAdapter_discount recyclerAdapter;
    DatabaseReference databaseReference;

    public discounts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discounts, container, false);

        MobileAds.initialize(getActivity(), "ca-app-pub-1151520966182945/4105626545");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        context = getActivity();
        recyclerView = view.findViewById(R.id.rv_discount);
        textView = view.findViewById(R.id.Auto_discount);
        if (check_connection()) {
            databaseReference = FirebaseDatabase.getInstance().getReference("discount_class");
            upload_data();
            ((AutoCompleteTextView) view.findViewById(R.id.Auto_discount)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayList_forSearch = new ArrayList<>();
                    for (int j = 0; j < MainActivity.arrayList_discount.size(); ++j) {
                        if (names[j].contains(charSequence)) {
                            arrayList_forSearch.add(MainActivity.arrayList_discount.get(j));
                        }
                    }
                    update_adapter(arrayList_forSearch);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            Toast.makeText(context, "الرجاء الاتصال بالانترنت.", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public boolean check_connection() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                isConnected = true;
            }
        }
        return isConnected;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (check_connection()) {
            upload_data();
        }
    }

    public void upload_data() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.arrayList_discount.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    discount_class student2 = dataSnapshot1.getValue(discount_class.class);
                    MainActivity.arrayList_discount.add(student2);
                }
                update_adapter(MainActivity.arrayList_discount);
                names = new String[MainActivity.arrayList_discount.size()];
                for (int i = 0; i < MainActivity.arrayList_discount.size(); ++i) {
                    names[i] = MainActivity.arrayList_discount.get(i).getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_dropdown_item_1line, names);
                textView.setAdapter(adapter);
                //show_notes();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
////////////////////////////////////////////////////////
    public void show_notes() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if ((!MainActivity.preferences.getBoolean("notHasNotification", false))) {

                    try {
                        discount_class notification = dataSnapshot.getValue(discount_class.class);
                        if (notification.getName().equals(MainActivity.arrayList_discount.get(MainActivity.arrayList_discount.size() - 1).getName())) {
                            NotificationCompat.Builder mBuilder =
                                    new NotificationCompat.Builder(context)
                                            .setSmallIcon(R.drawable.custom_discount)
                                            .setContentTitle(notification.getName())
                                            .setAutoCancel(true)
                                            .setContentText(notification.getPrice()+" شيكل ");
                            Intent intent = new Intent(context, MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(pendingIntent);
                            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                            manager.notify(40111, mBuilder.build());
                        }
                    } catch (Exception e) {

                    }
                }
                ++tag;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void update_adapter(ArrayList<discount_class> arrayList) {
        RecyclerView.LayoutManager mlayout = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mlayout);
        recyclerAdapter = new RecyclerAdapter_discount(arrayList, context);
        recyclerView.setAdapter(recyclerAdapter);
    }
}

package com.example.acer.zebdashop;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.ctrlplusz.anytextview.AnyEditTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Prices extends Fragment {
    Context context;
    private AdView mAdView;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    String names[];
    RecyclerAdapter_product recyclerAdapter;
    ArrayList<product_class> arrayList_forSearch;
    AutoCompleteTextView textView;

    public Prices() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prices, container, false);

        MobileAds.initialize(getActivity(), "ca-app-pub-1151520966182945/4105626545");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        context = getActivity();
        recyclerView = view.findViewById(R.id.rv_price);
        textView = view.findViewById(R.id.Auto_price);
        if (check_connection()) {
            databaseReference = FirebaseDatabase.getInstance().getReference("product_class");
            Uplaod_Data();
            ((AutoCompleteTextView) view.findViewById(R.id.Auto_price)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    arrayList_forSearch = new ArrayList<>();
                    for (int j = 0; j < MainActivity.arrayList_product.size(); ++j) {
                        if (names[j].contains(charSequence)) {
                            arrayList_forSearch.add(MainActivity.arrayList_product.get(j));
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
            Uplaod_Data();
        }
    }

    public void Uplaod_Data() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.arrayList_product.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    product_class student = dataSnapshot1.getValue(product_class.class);
                    MainActivity.arrayList_product.add(student);
                }
                update_adapter(MainActivity.arrayList_product);
                names = new String[MainActivity.arrayList_product.size()];
                for (int i = 0; i < MainActivity.arrayList_product.size(); ++i) {
                    names[i] = MainActivity.arrayList_product.get(i).getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_dropdown_item_1line, names);
                textView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void update_adapter(ArrayList<product_class> arrayList) {
        RecyclerView.LayoutManager mlayout = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mlayout);
        recyclerAdapter = new RecyclerAdapter_product(arrayList, context);
        recyclerView.setAdapter(recyclerAdapter);
    }
}

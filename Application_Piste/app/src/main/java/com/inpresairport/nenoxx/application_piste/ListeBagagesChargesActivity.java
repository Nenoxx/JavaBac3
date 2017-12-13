package com.inpresairport.nenoxx.application_piste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListeBagagesChargesActivity extends AppCompatActivity {
    private ArrayList<String> BagageCharge = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_bagages_charges);
        Bundle b = getIntent().getExtras();
        BagageCharge = b.getStringArrayList("ListeBagage");
        if(BagageCharge != null){
            if(BagageCharge.isEmpty())
                BagageCharge.add("---AUCUN BAGAGE---");
            ListView mListView = findViewById(R.id.listBagage);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, BagageCharge);
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}

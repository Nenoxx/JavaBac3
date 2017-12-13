package com.inpresairport.nenoxx.application_piste;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ProtocoleLUGAP.RequeteLUGAP;

public class ListeVolsActivity extends AppCompatActivity{
    ListView mListView = null;
    ArrayAdapter<String> adapter = null;
    ArrayList<String> ListeVols = null;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_bagages);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toast.makeText(getApplicationContext(), "Bonjour " + LoginActivity.getUser() + "!", Toast.LENGTH_LONG).show();
        ListeVols = new ArrayList<String>();
        mListView = (ListView) findViewById(R.id.viewVols);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListeVols);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i >= 0){
                    String numVol = ListeVols.get(i);
                    System.out.println("Vol " + numVol + " sélectionné");
                    Intent intent = new Intent(getApplicationContext(), ListeBagagesActivity.class);
                    intent.putExtra("numVol", numVol);
                    startActivity(intent);
                }
            }
        });

        FlemmeDeDonnerUnNom a = new FlemmeDeDonnerUnNom();
        a.execute();

    }


    public class FlemmeDeDonnerUnNom extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                if(ListeVols != null && !ListeVols.isEmpty()){
                    System.out.println("Requête OK");
                    //adapter = new ArrayAdapter<String>(CallerContext, android.R.layout.simple_list_item_1, ListeVols);
                    for(String s : ListeVols){
                        adapter.add("Vol " + s);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //Récupération des flux déjà créés
            try {
                System.out.println("Récupération des flux...");
                ois = LoginActivity.ois;
                oos = LoginActivity.oos;
                System.out.println("Récupération des vols...");
                //Récupération des vols
                String query = "SELECT numVol from VOLS";
                RequeteLUGAP req = new RequeteLUGAP(RequeteLUGAP.GETVOL, query);
                oos.writeObject(req);
                oos.flush();
                System.out.println("Requête envoyée au serveur distant");
                //Attente de la réponse du serveur
                ListeVols = (ArrayList<String>)ois.readObject();
                System.out.println("Requête récupérée");
                return true;
            }
            catch(Exception ex){
                System.out.println("Erreur : " + ex.getLocalizedMessage());
                return false;
            }
        }
    }

}

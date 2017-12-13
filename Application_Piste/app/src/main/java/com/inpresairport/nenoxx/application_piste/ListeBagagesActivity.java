package com.inpresairport.nenoxx.application_piste;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ProtocoleLUGAP.ReponseLUGAP;
import ProtocoleLUGAP.RequeteLUGAP;

public class ListeBagagesActivity extends AppCompatActivity {
    private ListView mListView = null;
    private ArrayAdapter<String> Adapter = null;
    private ArrayList<String> ListeBagages = null;
    private ArrayList<String> EtatBagage = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private String numVol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_bagages2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //récupération du numéro de vol passé en paramètre à l'activity
        Bundle b = getIntent().getExtras();
        numVol = b.getString("numVol");

        //Init de la view list
        ListeBagages = new ArrayList<String>();
        EtatBagage = new ArrayList<>();
        this.mListView = (ListView) findViewById(R.id.viewBagages);
        Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, ListeBagages);
        this.mListView.setAdapter(Adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); //Pour pouvoir cocher plusieurs trucs
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mListView.isItemChecked(i))
                    mListView.setItemChecked(i, true);
                else
                    mListView.setItemChecked(i, false);
            }
        });

        Button charger = findViewById(R.id.buttonCharger);
        charger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checked = mListView.getCheckedItemPositions();
                for(int j = 0; j < mListView.getAdapter().getCount(); j++){
                    if(checked.get(j)){
                        String bagage = ListeBagages.get(j);
                        String update = "update BAGAGES set charge = 'O' " +
                                "        where numBagage = '"+ bagage.substring(0, bagage.length() - 2)+"';";
                        UpdateBDD u = new UpdateBDD();
                        u.execute(update);

                        //Leur état est passé à "O" pour "chargé" donc on les enlève.
                        EtatBagage.remove(j);
                        Adapter.remove(Adapter.getItem(j));
                        Adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        LaFlemmeEstEncoreLa a = new LaFlemmeEstEncoreLa();
        a.execute(getApplicationContext());
    }

    public class LaFlemmeEstEncoreLa extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... contexts) {
           try{
               System.out.println("Récupération des flux...");
               ois = LoginActivity.ois;
               oos = LoginActivity.oos;
               System.out.println("Récupération des bagages pour le vol " + numVol);
               String query = "select numBagage, charge from BAGAGES where substr(numBagage,1,3) = '"+numVol+"';";
               RequeteLUGAP req = new RequeteLUGAP(RequeteLUGAP.GETBAGAGE, query);
               oos.writeObject(req);
               oos.flush();
               System.out.println("Requête envoyée au serveur distant");

               ListeBagages = (ArrayList<String>)ois.readObject();
               System.out.println("Requête récupérée");
               return true;
           }
           catch(Exception ex){
               System.out.println("Erreur : " + ex.getLocalizedMessage() + ";" + ex.getCause());
               return false;
           }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                if(ListeBagages != null && !ListeBagages.isEmpty()){
                    System.out.println("Requête OK");
                    //Adapter = new ArrayAdapter<String>(CallerContext, android.R.layout.simple_list_item_1, ListeVols);
                    for(String s : ListeBagages){
                        String[] tmp = s.split(";");

                        if(tmp[1].equals("N")) { //Si le bagage n'est pas encore chargé en soute
                            EtatBagage.add(tmp[1]);
                            Adapter.add("n° " + tmp[0]);
                            System.out.println("élément ajouté : " + s);
                            Adapter.notifyDataSetChanged();
                        }

                    }

                }
            }
        }
    }

    public class UpdateBDD extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String update = strings[0];
            RequeteLUGAP req = new RequeteLUGAP(RequeteLUGAP.UPDATE_BAGAGE, update);
            try{
                oos.writeObject(req);
                oos.flush();

                ReponseLUGAP rep = (ReponseLUGAP)ois.readObject();
                if(rep.getCode() == ReponseLUGAP.OK)
                    return true;
                else return false;
            }
            catch(Exception ex){
                System.out.println("Erreur : " + ex.getLocalizedMessage() + ";" + ex.getCause());
                return false;
            }
        }
    }


}

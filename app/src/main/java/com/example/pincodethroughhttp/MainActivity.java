package com.example.pincodethroughhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText pincode;
    Button get;
    ListView lv;
    String res = "";
    ProgressDialog pd;
    List<String> ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pincode = (EditText) findViewById(R.id.pincode);
        get = (Button) findViewById(R.id.bu);
        lv = (ListView) findViewById(R.id.ls);
        pd = new ProgressDialog(this);
        pd.setMessage("fetching data please wait");
        pd.setCancelable(true);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = pincode.getText().toString();
                String pass = "https://api.postalpincode.in/pincode/" + value;
                pd.show();
                new Task().execute(pass);
            }
        });
    }


    public class Task extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            try {
                ls = new ArrayList<>();
                URL url = new URL(strings[0]);
                //connection opening through url stated above
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                //method for requesting data from htttp
                huc.setRequestMethod("GET");
                //
                huc.connect();

                //data we recieve from internet is in form of stream or packet and
                // for reciveing we use inputstream and destination as transmission we use outputstream
                //Earlier is was InputStream is=huc.getInputStream();

                InputStream is = new BufferedInputStream(huc.getInputStream());
                //conversion from byte to char
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String temp;

                while ((temp = br.readLine()) != null) {
                    res = res + temp;
                }

                is.close();
                publishProgress(res);
                JSONObject job = new JSONObject(res);
                JSONArray jar = job.getJSONArray("PostOffice");

                for (int i = 0; i < jar.length(); i++) {
                    JSONObject job1 = jar.getJSONObject(i);


                /*    "Name":"Shastri Nagar (Ajmer)",
                            "Description":null,
                            "BranchType":"Sub Post Office",
                            "DeliveryStatus":"Non-Delivery",
                            "Circle":"Rajasthan",
                            "District":"Ajmer",
                            "Division":"Ajmer",
                            "Region":"Ajmer",
                            "Block":"Ajmer",
                            "State":"Rajasthan",
                            "Country":"India",
                            "Pincode":"305001"
                */
                    ls.add(job1.getString("Name") + "\n" +
                            job1.getString("Circle") + "\n" +
                            job1.getString("District") + "\n" +
                            job1.getString("Region") + "\n" +
                            job1.getString("State") + "\n" +
                            job1.getString("Country") + "\n" +
                            job1.getString("Pincode")
                    );

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, ls);
            lv.setAdapter(adapter);

        }

    }
}
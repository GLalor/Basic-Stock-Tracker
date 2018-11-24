package com.example.graha.stocktracker;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;

public class MainActivity extends AppCompatActivity {

    Button getPriceBTN;
    TextView stockPriceJson;
    TextView storedTickerView;
    TextView baseText;
    ProgressDialog pd;
    EditText tickerSymbolInput;
    Toast errorMessage;
    String tickerSymbol;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    final String KEY = "Ticker Symbol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getPreferences(getApplicationContext().MODE_PRIVATE);
        editor = sharedPref.edit();


        getPriceBTN = (Button) findViewById(R.id.getPriceBTN);
        stockPriceJson = (TextView) findViewById(R.id.tvJsonItem);
        storedTickerView = (TextView) findViewById(R.id.storedTicker);
        baseText = (TextView) findViewById(R.id.baseTextView);
        tickerSymbolInput = (EditText) findViewById(R.id.tickerSymbol);

        if(sharedPref.contains(KEY)){
            tickerSymbolInput.setVisibility(View.GONE);
            tickerSymbolInput.setVisibility(View.GONE);
            baseText.setVisibility(View.GONE);
            getPriceBTN.setText("Update price");
            storedTickerView.setText("Ticker Symbol: "+sharedPref.getString(KEY, ""));
            storedTickerView.setVisibility(View.VISIBLE);
            tickerSymbol = sharedPref.getString(KEY, "");
            new JsonTask().execute("https://api.iextrading.com/1.0/stock/" + tickerSymbol + "/price");
        }

        getPriceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPref.contains(KEY)){
                    tickerSymbol = sharedPref.getString(KEY, "");
                }else {
                    tickerSymbol = tickerSymbolInput.getText().toString();
                }
                if(tickerSymbol.matches((""))) {
                    errorMessage = Toast.makeText(getApplicationContext(),
                            "Please Enter a ticker Symbol!",
                            Toast.LENGTH_LONG);
                    errorMessage.show();
                }else{
                    new JsonTask().execute("https://api.iextrading.com/1.0/stock/" + tickerSymbol + "/price");
                    getPriceBTN.setText("Update price");
                }
            }
        });


    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (result == null) {
                getPriceBTN.setText(R.string.getBtn_default);
                errorMessage = Toast.makeText(getApplicationContext(),
                        "Invalid Ticker Symbol!",
                        Toast.LENGTH_LONG);
                errorMessage.show();
            } else {
                editor.putString(KEY, tickerSymbol);
                editor.commit();
                if(sharedPref.contains(KEY)){
                    tickerSymbolInput.setVisibility(View.GONE);
                    tickerSymbolInput.setVisibility(View.GONE);
                    baseText.setVisibility(View.GONE);
                    storedTickerView.setText("Ticker Symbol: "+sharedPref.getString(KEY, ""));
                    storedTickerView.setVisibility(View.VISIBLE);
                }
                stockPriceJson.setText("$" + result);
            }
        }

    }
}
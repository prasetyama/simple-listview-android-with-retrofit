package com.example.pras.enursing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {


    private APIService mAPIService;

    private ListView lv;
    private String[] groupArray = {"Bernapas", "Makan dan Minum", "Eliminasi", "Berpindah", "Istirahat dan Tidur", "Kebersihan", "Mempertahankan Suhu Tubuh", "Menghindari bahaya lingkungan", "Komunikasi", "Keimanan", "Hiburan", "Belajar"};
    private String[][] childArray = {{"Saya merasa sesak", "Oksigen yang diberikan terlalu kencang", "Kerongkongan saya sangat gatal", "Banyak dahak dikerongkongan saya"},
            {"Saya merasa lapar", "Saya merasa haus", "Badan saya lemah", "Mulut saya rasa tidak enak", "Saya rasa ingin muntah", "Saya rasa ingin mual"}, {"Saya ingin BAK", "Saya ingin BAB", "Pampers saya penuh", "Saya diare", "Saya merasa nyeri di selang kateter", "Perut saya kembung", "Perut saya sakit"},
            {"Saya ingin miring kiri", "saya ingin miring kanan", "Saya ingin meninggikan kepala", "Saya ingin jalan-jalan"}, {"Saya tidak bisa tidur", "Lingkungan berisik dan tidak nyaman", "Saya merasa bagian tubuh saya sakit"},
            {"Saya ingin mandi", "Saya ingin ganti baju", "Saya ingin menggosok gigi", "Saya ingin potong kuku", "Saya ingin keramas"}, {"Saya merasa lingkungan teralu dingin", "Saya merasa lingkungan terlalu panas", "Saya merasa demam"},
            {"Posisi saya terlalu di tepi", "Tolong naikkan bedrail tempat tidur saya", "Kaki saya terjepit", "Infus tercabut dari tangan saya", "Cairan di botol sudah hampir habis"}, {"Saya ingin bicara dengan keluarga saya", "Saya ingin berbincang (ngobrol-ngobrol) dengan perawat"},
            {"Saya mau sholat/ berdoa", "Saya mau didoakan", "Saya mau dengar murottal Al-Quran", "Saya mendengar lagu rohani"}, {"Saya ingin menonton TV", "Saya ingin Mendengarkan musik"},{"Saya ingin mendengar berita TV", "Saya ingin membaca koran"}
    };

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listview);

        mAPIService = ApiUtils.getAPIService();

        String[] data = getIntent().getStringArrayExtra("strArray");
        AdapterView.OnItemClickListener clickListener = null;

        // If no data received means this is the first activity
        if (data == null) {
            data = groupArray;
            clickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("strArray", childArray[position]);
                    startActivity(intent);
                }
            };


        } else {
            clickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemSelected = ((TextView) view).getText().toString();

                    sendPost("Notification", itemSelected);

                    Log.e("item selected", itemSelected);
                }
            };
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, data);

        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(clickListener);


    }

    public void sendPost(String notification, String itemSelected) {
        mAPIService.savePost(notification, itemSelected, "").enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });
    }
}

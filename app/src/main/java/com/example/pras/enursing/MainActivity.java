package com.example.pras.enursing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pras.enursing.Utils.NotificationsUtils;
import com.example.pras.enursing.app.Config;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;


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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };

        displayFirebaseRegId();

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

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);
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

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationsUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}

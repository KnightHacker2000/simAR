package edu.osu.simar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;

import static edu.osu.simar.R.string.Test_Noti_Title;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private Button mButton_cam = null;
    private Button mButton_SMS = null;
    private ImageView mImageView = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String ACTION_SMS_STATUS = "edu.osu.simar.ACTION_SMS_STATUS"; // String for customized action
    public static final int SMS_NOTI_ID = 0; // ID for notification
    public static final String TEST_CHANNEL_ID = "SIMAR_TEST_CHANNEL"; // ID for the test channel
    private int exitCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.my_image);

        mButton_cam = (Button) findViewById(R.id.camera_button);
        mButton_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }

            private void dispatchTakePictureIntent() {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        mButton_SMS = (Button) findViewById(R.id.SMS_button);
        mButton_SMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                Intent intent = new Intent(ACTION_SMS_STATUS);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                smsManager.sendTextMessage("+8613918446326", null, "This is a test message!", pendingIntent, null);

            }
        });

        // Context-registered broadcast receiver for SMS ACTION
        BroadcastReceiver br = new SMS_Receiver();
        IntentFilter filter = new IntentFilter(ACTION_SMS_STATUS);
        this.registerReceiver(br, filter);

        // Get ExitCount and make Welcome Toast
        GetExitCount();

        //获取地图控件引用
        //mMapView = (MapView) findViewById(R.id.bmapView);
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        //mMapView.onResume();
        SMS_notification();

    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        //mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        //mMapView.onDestroy();
    }
    @Override
    public void onStop() {
        super.onStop();
        UpdateExitCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }

//        if(requestCode == )

    }

    public void SMS_notification(){

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        createNotificationChannel();

        // Build a notification object
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TEST_CHANNEL_ID)
                .setSmallIcon(R.drawable.done)
                .setContentTitle(getResources().getString(R.string.Test_Noti_Title))
                .setContentText(getResources().getString(R.string.Test_Noti_Text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(SMS_NOTI_ID, builder.build());
    }

    // Set up Test_channel notification channel
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel( TEST_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Example code to access SharedPreferences
    private void GetExitCount() {
        /* Get default shardPref for current activity*/
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        /* Get shardPref by name */
        //SharedPreferences sharedPref = this.getSharedPreferences(
        //        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        /* Get Entry from SharedPref */
        exitCount = sharedPref.getInt(getString(R.string.saved_exit_count), 0);
        if(exitCount == 0){
            Toast.makeText(this, getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show();
        } else{
            if(exitCount%10 == 1){
                Toast.makeText(this, "Welcome to SimAR for the "+exitCount+"st time!", Toast.LENGTH_SHORT).show();
            }
            else if(exitCount%10 == 2){
                Toast.makeText(this, "Welcome to SimAR for the "+exitCount+"nd time!", Toast.LENGTH_SHORT).show();
            }
            else if(exitCount%10 == 3){
                Toast.makeText(this, "Welcome to SimAR for the "+exitCount+"rd time!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Welcome to SimAR for the "+exitCount+"th time!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Example code to update SharedPreferences
    private void UpdateExitCount() {
        /* Get default shardPref for current activity*/
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        /* Get shardPref by name */
        //SharedPreferences sharedPref = this.getSharedPreferences(
        //        getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        /* Edit SharedPref */
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.contains(getString(R.string.saved_exit_count))){
            exitCount = sharedPref.getInt(getString(R.string.saved_exit_count), 1);
            editor.putInt(getString(R.string.saved_exit_count), exitCount+1);
        }else{
            editor.putInt(getString(R.string.saved_exit_count), 1);
        }

        editor.apply();
    }

}

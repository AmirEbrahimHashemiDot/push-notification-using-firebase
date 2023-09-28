package com.example.pushnotificationfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Context context;
    String myToken;
    private static final int REQUEST_PERMISSION_CODE = 1001;
    CharSequence channelName = "My Notification Channel";
    String channelId = "channel_id";
    int notificationId = 1;
    String channelDescription = "Description of Notification";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    NotificationChannel notificationChannel;
    NotificationManager notificationManager;
    TextView tvShowAPILevel;
    TextView tvUserToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserToken();
        setUpViews();

        //checkAPILevelCompatibility();


    }

    private void getUserToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Could not get FirebaseMessagingToken", Toast.LENGTH_SHORT).show();
                    }
                    if (null != task.getResult()) {
                        myToken = Objects.requireNonNull(task.getResult());
                        Toast.makeText(this, "token: " + myToken, Toast.LENGTH_SHORT).show();
                        Log.i("MY_TOKEN_LOG", "Token: " + myToken);
                        tvUserToken = findViewById(R.id.tvUserToken);
                        tvUserToken.setText(myToken);
                        tvUserToken.setTextIsSelectable(true);
                    }
                }
        );
    }


    private void checkAPILevelCompatibility() {
        // <= API 33
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISSION_CODE);
            } else {
                sendNotification();
            }
        }

        // between 27, 32
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotification();
        }

        // > 27
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            sendNotification();
        }
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelId, channelName, importance);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.setDescription(channelDescription);
        }

        notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //set onclick intent for notification
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                .setSmallIcon(R.drawable.o)
                .setContentTitle("حراج پاییزه")
                .setContentText("با خرید از دیجیکالا کد تخفیف خرید لباس بگیرید.")
                .setContentIntent(pendingIntent)
                //.setStyle(new NotificationCompat.InboxStyle().addLine("title1").addLine("title2").addLine("title3"))
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("My Big Text My Big Text ").setBigContentTitle("My Big Title"))
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.dk_banner)).setBigContentTitle("حراج شگفت انگیز").bigLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.dk_larg_icon2)))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    private void setUpViews() {
        tvShowAPILevel = findViewById(R.id.tvShowAPILevel);
        tvShowAPILevel.setText("" + Build.VERSION.SDK_INT);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You accepted permission.", Toast.LENGTH_SHORT).show();
            sendNotification();
        } else {
            Toast.makeText(this, "You denied permission.", Toast.LENGTH_SHORT).show();
        }
    }
}
package co.humaniq.services.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import co.humaniq.R;


public class FcmListenerService extends FirebaseMessagingService {
    private static final String TAG = "FcmListenerService";

    private void sendMessage(String message) {
        Intent intent = new Intent("fcm-message");
        intent.putExtra("message", message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();
        String category = data.get("category");
        Log.d(TAG, "notification received");

        if (category == null) {
            Log.w(TAG, "Category is null");
            return;
        }

        switch (category) {
            case "push":
                handlePushNotification(message);
                break;

            case "system":
                handleSystemNotification(message);
                break;

            default:
                Log.w(TAG, "Unknown notification category: " + category);
        }
    }

    private void handleSystemNotification(RemoteMessage message) {
        Map<String, String> data = message.getData();
        String method = data.get("method");
        sendMessage(method);
    }

    private void handlePushNotification(RemoteMessage message) {
        Map<String, String> data = message.getData();

        String msg = data.get("message");
        String title = data.get("title");

        createNotification(msg, title);
        sendMessage("update");
    }

    private void createNotification(String message, String title) {
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.cake)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setColor(0xff023876);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}

package com.example.navigationsecurityapp; // Places this activity in the required app package.

import android.Manifest; // Provides the POST_NOTIFICATIONS permission constant on Android 13+.
import android.app.NotificationChannel; // Creates notification channels required on Android 8.0+.
import android.app.NotificationManager; // Sends notifications and manages notification channels.
import android.app.PendingIntent; // Wraps future intents for notifications in a controlled way.
import android.content.ComponentName; // Represents a resolved component that can handle an intent.
import android.content.Context; // Provides access to system services and app resources.
import android.content.Intent; // Carries navigation requests between Android components.
import android.content.pm.PackageManager; // Checks whether notification permission is granted.
import android.net.Uri; // Builds HTTPS URLs for implicit intents and App Links.
import android.os.Build; // Checks Android version before using version-specific APIs.
import android.os.Bundle; // Stores activity state and intent extras.
import android.util.Log; // Writes lifecycle and navigation events to Logcat.
import android.widget.Button; // Provides clickable controls for each demo action.
import android.widget.EditText; // Lets the learner enter an item ID for navigation demos.
import android.widget.Toast; // Shows short feedback messages on screen.

import androidx.appcompat.app.AppCompatActivity; // Provides backward-compatible activity behavior.
import androidx.core.app.ActivityCompat; // Requests runtime permissions using AndroidX compatibility helpers.
import androidx.core.app.NotificationCompat; // Builds notifications consistently across Android versions.
import androidx.core.content.ContextCompat; // Checks runtime permissions using AndroidX compatibility helpers.

public class MainActivity extends AppCompatActivity { // Main launcher screen for the navigation/security lessons.

    public static final String EXTRA_ITEM_ID = "ITEM_ID"; // Key used to pass an item ID between activities.
    public static final String EXTRA_TASK_NAME = "TASK_NAME"; // Key used to pass a task name to the service.
    public static final String EXTRA_SECURE_DATA = "SECURE_DATA"; // Key used to pass private data to SecureActivity.
    private static final String TAG = "MainActivity"; // Log tag helps filter this screen's messages in Logcat.
    private static final String CHANNEL_ID = "navigation_security_channel"; // Stable channel ID for notifications.
    private static final int NOTIFICATION_ID = 100; // Stable notification ID lets this demo update the same notification.
    private static final int REQUEST_OPEN_DETAIL = 200; // Unique request code for the detail notification PendingIntent.
    private static final int REQUEST_SECURE_ACTIVITY = 201; // Unique request code for the secure PendingIntent demo.
    private static final int REQUEST_POST_NOTIFICATIONS = 300; // Unique request code for Android 13+ notification permission.

    private EditText itemIdEditText; // Holds the input field so button handlers can read the current item ID.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when Android creates the activity instance.
        super.onCreate(savedInstanceState); // Lets AppCompat initialize the activity correctly.
        setContentView(R.layout.activity_main); // Connects this Java activity to its XML layout.
        Log.d(TAG, "onCreate"); // Logs lifecycle creation for Task/BackStack observation.

        itemIdEditText = findViewById(R.id.editTextItemId); // Finds the item ID input from the layout.
        Button explicitButton = findViewById(R.id.buttonExplicitIntent); // Finds the explicit intent demo button.
        Button implicitButton = findViewById(R.id.buttonImplicitIntent); // Finds the implicit intent demo button.
        Button singleTopButton = findViewById(R.id.buttonSingleTop); // Finds the singleTop BackStack demo button.
        Button serviceButton = findViewById(R.id.buttonStartService); // Finds the background service demo button.
        Button notificationButton = findViewById(R.id.buttonShowNotification); // Finds the notification PendingIntent button.
        Button securePendingButton = findViewById(R.id.buttonSecurePendingIntent); // Finds the secure PendingIntent button.
        Button customActionButton = findViewById(R.id.buttonCustomAction); // Finds the custom action activity button.
        Button reportButton = findViewById(R.id.buttonSecurityReport); // Finds the security report screen button.
        Button secureActivityButton = findViewById(R.id.buttonSecureActivity); // Finds the private secure activity button.

        createNotificationChannel(); // Creates the notification channel before any notification is posted.

        explicitButton.setOnClickListener(view -> openDetailExplicitly()); // Opens DetailActivity using an explicit intent.
        implicitButton.setOnClickListener(view -> openItemImplicitly()); // Opens the HTTPS item URL through intent resolution.
        singleTopButton.setOnClickListener(view -> startSingleTopTest()); // Relaunches MainActivity to trigger onNewIntent().
        serviceButton.setOnClickListener(view -> startBackgroundService()); // Starts BackgroundService with an explicit intent.
        notificationButton.setOnClickListener(view -> showNotificationPendingIntent()); // Shows immutable PendingIntent notification.
        securePendingButton.setOnClickListener(view -> showSecurePendingIntentDemo()); // Demonstrates private explicit PendingIntent.
        customActionButton.setOnClickListener(view -> openCustomActionActivity()); // Opens the exposed custom-action activity.
        reportButton.setOnClickListener(view -> openSecurityReport()); // Opens the internal report screen.
        secureActivityButton.setOnClickListener(view -> openSecureActivity()); // Opens SecureActivity with private data.

        handleIncomingIntent(getIntent()); // Reads any item ID delivered by the first launch intent.
    }

    private String readItemId() { // Centralizes input cleanup so all demos use the same item ID behavior.
        String itemId = itemIdEditText.getText().toString().trim(); // Reads and trims learner input.
        if (itemId.isEmpty()) { // Provides a default so demos still work with blank input.
            itemId = "42"; // Uses a memorable default item ID for educational testing.
            itemIdEditText.setText(itemId); // Shows the default to make the launched data visible to the learner.
        }
        return itemId; // Returns the item ID for intent extras or URL paths.
    }

    private void openDetailExplicitly() { // Demonstrates explicit Intent navigation inside the same app.
        Intent intent = new Intent(this, DetailActivity.class); // Names the exact internal activity to launch.
        intent.putExtra(EXTRA_ITEM_ID, readItemId()); // Passes ITEM_ID so DetailActivity can display it.
        startActivity(intent); // Starts the selected activity directly without external resolution.
    }

    private void openItemImplicitly() { // Demonstrates implicit Intent navigation to an HTTPS item URL.
        Uri itemUri = Uri.parse("https://example.com/items/" + readItemId()); // Builds the App Link-style URL.
        Intent intent = new Intent(Intent.ACTION_VIEW, itemUri); // Requests any capable activity to view the URL.
        ComponentName componentName = intent.resolveActivity(getPackageManager()); // Checks whether Android can resolve the intent.
        if (componentName != null) { // Prevents a crash when no browser or App Link handler exists.
            startActivity(intent); // Launches the resolved activity chosen by the Android resolver.
        } else {
            Toast.makeText(this, "No activity can open this URL.", Toast.LENGTH_SHORT).show(); // Explains why nothing launched.
        }
    }

    private void startSingleTopTest() { // Demonstrates launchMode singleTop and onNewIntent().
        Intent intent = new Intent(this, MainActivity.class); // Explicitly targets this same activity.
        intent.putExtra(EXTRA_ITEM_ID, readItemId()); // Adds ITEM_ID so onNewIntent() has data to show.
        startActivity(intent); // Because MainActivity is singleTop, Android reuses it when it is already on top.
    }

    private void startBackgroundService() { // Demonstrates starting a private background service explicitly.
        Intent intent = new Intent(this, BackgroundService.class); // Explicitly targets the internal service.
        intent.putExtra(EXTRA_TASK_NAME, "Demo task for item " + readItemId()); // Sends a clear task label to the service.
        startService(intent); // Starts the service so it can run its short fake background task.
        Toast.makeText(this, "BackgroundService started.", Toast.LENGTH_SHORT).show(); // Gives immediate UI feedback.
    }

    private void showNotificationPendingIntent() { // Demonstrates an immutable PendingIntent launched from a notification.
        if (!hasNotificationPermission()) { // Checks runtime permission before posting on Android 13+.
            requestNotificationPermission(); // Asks the learner to grant notification permission if needed.
            return; // Stops now so the learner can tap the button again after granting permission.
        }

        Intent detailIntent = new Intent(this, DetailActivity.class); // Uses an explicit internal activity for safety.
        detailIntent.putExtra(EXTRA_ITEM_ID, readItemId()); // Includes the item ID that the notification will open.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, // Uses this app context as the PendingIntent owner.
                REQUEST_OPEN_DETAIL, // Uses a unique request code for this notification action.
                detailIntent, // Stores the explicit activity intent.
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Keeps extras fresh and prevents mutation.

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID) // Builds for the required channel.
                .setSmallIcon(R.drawable.ic_notification) // Uses the sample vector icon for the notification.
                .setContentTitle("Open item detail") // Tells the user what tapping the notification does.
                .setContentText("Tap to open DetailActivity with an immutable PendingIntent.") // Describes the security point.
                .setContentIntent(pendingIntent) // Attaches the safe PendingIntent to the notification tap.
                .setAutoCancel(true) // Removes the notification after the user taps it.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Uses normal priority to avoid noisy behavior.

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Gets notification service.
        manager.notify(NOTIFICATION_ID, builder.build()); // Posts the notification for the learner to tap.
    }

    private boolean hasNotificationPermission() { // Determines whether this app may post notifications.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) { // Android 12 and older do not require runtime notification permission.
            return true; // Permission is effectively granted on older versions.
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED; // Checks Android 13+ permission.
    }

    private void requestNotificationPermission() { // Requests notification permission only when Android requires it.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Runtime notification permission exists on API 33+.
            ActivityCompat.requestPermissions(
                    this, // Uses this activity to show the system permission dialog.
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, // Requests only the notification permission needed by the demo.
                    REQUEST_POST_NOTIFICATIONS); // Uses a stable request code for the permission response.
        }
    }

    private void showSecurePendingIntentDemo() { // Demonstrates a PendingIntent to a private secure component.
        Intent secureIntent = new Intent(this, SecureActivity.class); // Explicitly targets the non-exported SecureActivity.
        secureIntent.putExtra(EXTRA_SECURE_DATA, "Sensitive data for item " + readItemId()); // Adds private sample data.
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, // Uses this app as the creator identity.
                REQUEST_SECURE_ACTIVITY, // Uses a unique request code to avoid colliding with other PendingIntents.
                secureIntent, // Stores an explicit private component intent.
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Prevents other apps from changing intent fields.

        try { // Sending locally lets the learner observe the secure activity without creating a mutable PendingIntent.
            pendingIntent.send(); // Fires the immutable PendingIntent immediately for demonstration.
        } catch (PendingIntent.CanceledException exception) { // Handles rare cases where Android cancels the PendingIntent.
            Log.e(TAG, "Secure PendingIntent was canceled.", exception); // Logs diagnostic information for learners.
        }
    }

    private void openCustomActionActivity() { // Demonstrates a public custom action and custom URI scheme.
        Intent intent = new Intent("com.example.navigationsecurityapp.CUSTOM_VIEW"); // Uses the custom action from the manifest.
        intent.setData(Uri.parse("navsec://demo/item/" + readItemId())); // Adds a navsec:// URI for the activity to display.
        intent.setPackage(getPackageName()); // Limits the implicit custom action to this app for this internal launch.
        intent.putExtra(EXTRA_ITEM_ID, readItemId()); // Adds extras so CustomActionActivity can show them.
        startActivity(intent); // Launches the custom-action activity through intent resolution.
    }

    private void openSecurityReport() { // Opens the internal component security report.
        Intent intent = new Intent(this, SecurityReportActivity.class); // Uses explicit intent because the report is private.
        startActivity(intent); // Displays the report screen.
    }

    private void openSecureActivity() { // Opens the private activity with sensitive data.
        Intent intent = new Intent(this, SecureActivity.class); // Explicitly targets SecureActivity to avoid exposing sensitive data.
        intent.putExtra(EXTRA_SECURE_DATA, "Private message from MainActivity."); // Sends sample secure data.
        startActivity(intent); // Starts the private activity inside this app.
    }

    private void createNotificationChannel() { // Creates the channel required before posting notifications on Android 8+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Notification channels exist only on API 26 and newer.
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, // Stable ID used by NotificationCompat.Builder.
                    "Navigation Security Lessons", // Human-readable channel name shown in system settings.
                    NotificationManager.IMPORTANCE_DEFAULT); // Default importance keeps the demo visible but not intrusive.
            channel.setDescription("Notifications that demonstrate immutable PendingIntents."); // Explains the channel purpose.
            NotificationManager manager = getSystemService(NotificationManager.class); // Retrieves the typed notification manager.
            manager.createNotificationChannel(channel); // Registers the channel with Android.
        }
    }

    private void handleIncomingIntent(Intent intent) { // Reads ITEM_ID from initial launches and singleTop relaunches.
        if (intent != null && intent.hasExtra(EXTRA_ITEM_ID)) { // Confirms that the incoming intent carries an item ID.
            String itemId = intent.getStringExtra(EXTRA_ITEM_ID); // Extracts the item ID sent by another demo path.
            Toast.makeText(this, "Received ITEM_ID: " + itemId, Toast.LENGTH_SHORT).show(); // Shows the result on screen.
            Log.d(TAG, "Received ITEM_ID: " + itemId); // Logs the result for Task/BackStack observation.
        }
    }

    @Override
    protected void onNewIntent(Intent intent) { // Called for singleTop relaunches instead of creating a new activity.
        super.onNewIntent(intent); // Preserves AppCompat behavior for new intents.
        setIntent(intent); // Updates getIntent() so the current activity reflects the newest launch data.
        Log.d(TAG, "onNewIntent"); // Logs that singleTop delivered a new intent.
        handleIncomingIntent(intent); // Displays and logs the new ITEM_ID.
    }

    @Override
    protected void onStart() { // Called when the activity becomes visible.
        super.onStart(); // Keeps the Android lifecycle contract intact.
        Log.d(TAG, "onStart"); // Logs visibility changes for BackStack learning.
    }

    @Override
    protected void onResume() { // Called when the activity is ready for interaction.
        super.onResume(); // Keeps the Android lifecycle contract intact.
        Log.d(TAG, "onResume"); // Logs foreground interaction state.
    }

    @Override
    protected void onPause() { // Called when another activity partially or fully covers this one.
        super.onPause(); // Keeps the Android lifecycle contract intact.
        Log.d(TAG, "onPause"); // Logs the transition away from active input.
    }

    @Override
    protected void onStop() { // Called when the activity is no longer visible.
        super.onStop(); // Keeps the Android lifecycle contract intact.
        Log.d(TAG, "onStop"); // Logs when the activity leaves the visible screen.
    }

    @Override
    protected void onDestroy() { // Called when the activity instance is finishing or being destroyed.
        super.onDestroy(); // Keeps the Android lifecycle contract intact.
        Log.d(TAG, "onDestroy"); // Logs final destruction for lifecycle learning.
    }
}

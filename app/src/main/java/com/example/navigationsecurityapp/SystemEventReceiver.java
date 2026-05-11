package com.example.navigationsecurityapp; // Places the receiver in the required package.

import android.content.BroadcastReceiver; // Base class for receiving broadcasts.
import android.content.Context; // Provides access to app services from the receiver.
import android.content.Intent; // Contains the broadcast action and extras.
import android.os.Binder; // Provides caller UID for the security demonstration.
import android.util.Log; // Logs received broadcasts and caller identity.
import android.widget.Toast; // Shows simple feedback for the custom broadcast action.

public class SystemEventReceiver extends BroadcastReceiver { // Receives system boot and custom demo broadcasts.

    private static final String TAG = "SystemEventReceiver"; // Log tag identifies receiver messages.
    public static final String ACTION_CUSTOM = "com.example.navigationsecurityapp.CUSTOM_ACTION"; // Custom broadcast action.

    @Override
    public void onReceive(Context context, Intent intent) { // Called by Android when a matching broadcast arrives.
        String action = intent != null ? intent.getAction() : null; // Reads the broadcast action defensively.
        int callerUid = Binder.getCallingUid(); // Gets the UID reported by Binder for this broadcast delivery.
        Log.d(TAG, "Received action: " + action + " from caller UID: " + callerUid); // Logs action and caller identity.

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) { // Handles the system boot completed broadcast.
            Intent serviceIntent = new Intent(context, BackgroundService.class); // Uses an explicit intent for the private service.
            serviceIntent.putExtra(MainActivity.EXTRA_TASK_NAME, "Boot completed initialization"); // Labels the boot task.
            context.startService(serviceIntent); // Starts the background service after boot.
        } else if (ACTION_CUSTOM.equals(action)) { // Handles the custom educational broadcast action.
            Toast.makeText(context, "Custom broadcast received.", Toast.LENGTH_SHORT).show(); // Shows visible feedback.
        }
    }
}

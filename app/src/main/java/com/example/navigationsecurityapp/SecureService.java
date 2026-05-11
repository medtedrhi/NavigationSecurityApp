package com.example.navigationsecurityapp; // Places the secure service in the required package.

import android.app.Service; // Base class for Android services.
import android.content.Intent; // Carries secure operation data into the service.
import android.os.Binder; // Provides caller UID for the security demonstration.
import android.os.IBinder; // Represents binding support, which this sample does not use.
import android.os.Process; // Provides this app's UID for comparison.
import android.util.Log; // Logs authorization and operation status.

public class SecureService extends Service { // Demonstrates UID checks for sensitive service work.

    public static final String EXTRA_SECURE_OPERATION = "SECURE_OPERATION"; // Key for the secure operation name.
    private static final String TAG = "SecureService"; // Log tag identifies secure service messages.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // Called when a caller starts this service.
        int callerUid = Binder.getCallingUid(); // Gets the UID associated with the Binder call path.
        int appUid = Process.myUid(); // Gets this app's own UID.
        if (callerUid != appUid) { // Refuses work unless the caller UID matches this app.
            Log.w(TAG, "Unauthorized caller UID: " + callerUid); // Logs the denied caller for audit learning.
            stopSelf(startId); // Stops this service start request because it is not authorized.
            return START_NOT_STICKY; // Avoids automatic restart of a denied operation.
        }

        String operation = intent != null ? intent.getStringExtra(EXTRA_SECURE_OPERATION) : null; // Reads the secure operation.
        if (operation == null) { // Provides a readable default for missing extras.
            operation = "Default secure operation"; // Keeps logs understandable during experiments.
        }
        final String finalOperation = operation; // Captures the operation for the worker thread.

        new Thread(() -> { // Runs fake secure work off the main thread.
            Log.d(TAG, "Secure operation started: " + finalOperation); // Logs operation start.
            try { // Handles sleep interruption explicitly.
                Thread.sleep(5000); // Simulates a five-second secure operation.
            } catch (InterruptedException exception) { // Handles thread interruption safely.
                Log.e(TAG, "Secure operation interrupted: " + finalOperation, exception); // Logs interruption details.
                Thread.currentThread().interrupt(); // Restores interrupted state.
            }
            Log.d(TAG, "Secure operation ended: " + finalOperation); // Logs operation completion.
            stopSelf(startId); // Stops this service start after the secure operation finishes.
        }).start(); // Starts the background work immediately.

        return START_NOT_STICKY; // Tells Android not to restart this service automatically if killed.
    }

    @Override
    public IBinder onBind(Intent intent) { // Binding is not supported in this simple secure service.
        return null; // Returning null rejects bound-service use.
    }
}

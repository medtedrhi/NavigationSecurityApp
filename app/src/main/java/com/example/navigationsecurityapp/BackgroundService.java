package com.example.navigationsecurityapp; // Places the service in the required package.

import android.app.Service; // Base class for started background services.
import android.content.Intent; // Carries the task name extra into the service.
import android.os.IBinder; // Represents binding support, which this started service does not use.
import android.util.Log; // Logs background task progress.

public class BackgroundService extends Service { // Demonstrates a simple private started service.

    private static final String TAG = "BackgroundService"; // Log tag identifies service messages.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // Called each time startService() starts this service.
        String taskName = intent != null ? intent.getStringExtra(MainActivity.EXTRA_TASK_NAME) : null; // Reads the requested task name.
        if (taskName == null) { // Provides a fallback task name for defensive clarity.
            taskName = "Unnamed background task"; // Makes logs readable even when no extra is supplied.
        }
        final String finalTaskName = taskName; // Captures the task name safely for the background thread.

        new Thread(() -> { // Runs fake work away from the main thread to avoid blocking the UI.
            Log.d(TAG, "Task started: " + finalTaskName); // Logs task start for Logcat observation.
            try { // Sleep can throw InterruptedException, so it is handled explicitly.
                Thread.sleep(5000); // Simulates five seconds of background work.
            } catch (InterruptedException exception) { // Handles interruption if Android or the process stops the thread.
                Log.e(TAG, "Task interrupted: " + finalTaskName, exception); // Logs interruption details for debugging.
                Thread.currentThread().interrupt(); // Restores the interrupted flag for correct thread behavior.
            }
            Log.d(TAG, "Task ended: " + finalTaskName); // Logs task completion for learning.
            stopSelf(startId); // Stops this specific service start request after work finishes.
        }).start(); // Starts the worker thread immediately.

        return START_NOT_STICKY; // Tells Android not to recreate the service automatically after it is killed.
    }

    @Override
    public IBinder onBind(Intent intent) { // Binding is not supported in this started-service example.
        return null; // Returning null tells Android clients cannot bind to this service.
    }
}

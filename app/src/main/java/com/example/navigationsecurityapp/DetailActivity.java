package com.example.navigationsecurityapp; // Places this screen in the required app package.

import android.content.Intent; // Gives access to extras and App Link launch data.
import android.net.Uri; // Parses the HTTPS App Link URI.
import android.os.Bundle; // Provides saved-state data for activity creation.
import android.util.Log; // Logs navigation events for learning.
import android.widget.Button; // Provides the Retour button.
import android.widget.TextView; // Displays the received item ID.

import androidx.appcompat.app.AppCompatActivity; // Provides backward-compatible activity behavior.

public class DetailActivity extends AppCompatActivity { // Shows an item opened by explicit intent or App Link.

    private static final String TAG = "DetailActivity"; // Log tag identifies this activity's messages.
    private TextView itemTextView; // Holds the display view so onNewIntent() can update it.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when Android creates the detail screen.
        super.onCreate(savedInstanceState); // Lets AppCompat initialize the activity correctly.
        setContentView(R.layout.activity_detail); // Connects this class to its XML layout.

        itemTextView = findViewById(R.id.textViewItemId); // Finds the TextView that shows item data.
        Button backButton = findViewById(R.id.buttonBack); // Finds the Retour button.
        backButton.setOnClickListener(view -> finish()); // Finishes this activity and returns to the previous screen.

        displayIntentData(getIntent()); // Reads and shows the data from the launch intent.
    }

    private void displayIntentData(Intent intent) { // Handles both explicit extras and App Link URI data.
        String itemId = intent.getStringExtra(MainActivity.EXTRA_ITEM_ID); // Reads ITEM_ID when launched explicitly.
        Uri data = intent.getData(); // Reads URL data when launched from an App Link.
        if (itemId == null && data != null) { // Falls back to URL parsing if no explicit extra exists.
            itemId = data.getLastPathSegment(); // Extracts the final /items/{id} segment as the item ID.
            Log.d(TAG, "Received App Link URI: " + data); // Logs the deep link that opened this screen.
        }
        if (itemId == null) { // Provides a friendly fallback when no item data is present.
            itemId = "No ITEM_ID received"; // Explains that the launch did not include an item ID.
        }
        itemTextView.setText("Item ID: " + itemId); // Displays the final item ID result to the learner.
    }

    @Override
    protected void onNewIntent(Intent intent) { // Supports updating this screen if Android reuses the activity.
        super.onNewIntent(intent); // Preserves normal activity behavior.
        setIntent(intent); // Updates getIntent() to the latest data.
        displayIntentData(intent); // Refreshes the UI with the new item ID or App Link.
    }
}

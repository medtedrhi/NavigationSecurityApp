package com.example.navigationsecurityapp; // Places this activity in the required package.

import android.content.Intent; // Provides action, URI data, and extras from the launch request.
import android.net.Uri; // Represents the navsec:// URI sent to this activity.
import android.os.Bundle; // Provides saved-state data for activity creation.
import android.widget.Button; // Provides the Retour button.
import android.widget.TextView; // Displays action, data, and extras.

import androidx.appcompat.app.AppCompatActivity; // Provides backward-compatible activity behavior.

import java.util.Set; // Lets the activity list all extra keys in the incoming intent.

public class CustomActionActivity extends AppCompatActivity { // Demonstrates an intentionally exported custom action activity.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when Android creates the custom action screen.
        super.onCreate(savedInstanceState); // Lets AppCompat initialize the activity correctly.
        setContentView(R.layout.activity_custom_action); // Connects this class to its XML layout.

        TextView infoTextView = findViewById(R.id.textViewCustomActionInfo); // Finds the output TextView.
        Button backButton = findViewById(R.id.buttonCustomBack); // Finds the Retour button.
        backButton.setOnClickListener(view -> finish()); // Finishes this activity and returns to the previous screen.

        Intent intent = getIntent(); // Reads the launch intent that reached this exposed component.
        String action = intent.getAction(); // Reads the custom action name.
        Uri data = intent.getData(); // Reads the navsec:// data URI.
        StringBuilder builder = new StringBuilder(); // Builds a readable report for the screen.
        builder.append("Action: ").append(action).append("\n"); // Shows the action used to open the activity.
        builder.append("Data URI: ").append(data).append("\n\n"); // Shows the URI used to open the activity.
        builder.append("Extras:\n"); // Adds a section for all extras.

        Bundle extras = intent.getExtras(); // Reads extras sent by the caller.
        if (extras != null) { // Lists extras only when they exist.
            Set<String> keys = extras.keySet(); // Gets all extra names for display.
            for (String key : keys) { // Iterates through each extra key.
                builder.append(key).append(" = ").append(extras.get(key)).append("\n"); // Shows each extra value.
            }
        } else {
            builder.append("No extras received.\n"); // Explains when no extras were included.
        }

        infoTextView.setText(builder.toString()); // Displays the complete launch details.
    }
}

package com.example.navigationsecurityapp; // Places this private activity in the required package.

import android.os.Binder; // Provides caller UID for the security demonstration.
import android.os.Bundle; // Provides saved-state data for activity creation.
import android.os.Process; // Provides this app process UID for comparison.
import android.widget.Button; // Provides the Retour button.
import android.widget.TextView; // Displays authorization result and secure data.

import androidx.appcompat.app.AppCompatActivity; // Provides backward-compatible activity behavior.

public class SecureActivity extends AppCompatActivity { // Demonstrates UID checking for sensitive component access.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when Android creates the secure screen.
        super.onCreate(savedInstanceState); // Lets AppCompat initialize the activity correctly.
        setContentView(R.layout.activity_secure); // Connects this class to its XML layout.

        TextView secureTextView = findViewById(R.id.textViewSecureData); // Finds the TextView for the result message.
        Button backButton = findViewById(R.id.buttonSecureBack); // Finds the Retour button.
        backButton.setOnClickListener(view -> finish()); // Returns to the previous screen safely.

        int callerUid = Binder.getCallingUid(); // Gets the UID that Binder reports for this call path.
        int appUid = Process.myUid(); // Gets this app's own UID for comparison.
        boolean authorized = callerUid == appUid; // Allows access only when the caller UID matches this app.
        String secureData = getIntent().getStringExtra(MainActivity.EXTRA_SECURE_DATA); // Reads private data from the intent.

        if (authorized) { // Shows sensitive data only for same-UID calls.
            secureTextView.setText("Authorized caller UID " + callerUid + "\nSecure data: " + secureData); // Displays success details.
        } else {
            secureTextView.setText("Unauthorized caller UID " + callerUid + "\nAccess denied."); // Warns that access was denied.
        }
    }
}

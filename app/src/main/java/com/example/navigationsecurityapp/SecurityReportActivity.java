package com.example.navigationsecurityapp; // Places this internal report screen in the required package.

import android.content.pm.ActivityInfo; // Describes activity components from PackageManager.
import android.content.pm.PackageInfo; // Holds package component arrays for analysis.
import android.content.pm.PackageManager; // Reads this app's declared components.
import android.content.pm.ServiceInfo; // Describes service components from PackageManager.
import android.os.Build; // Selects modern or legacy PackageManager flags.
import android.os.Bundle; // Provides saved-state data for activity creation.
import android.widget.Button; // Provides the Retour button.
import android.widget.TextView; // Displays the generated report.

import androidx.appcompat.app.AppCompatActivity; // Provides backward-compatible activity behavior.

public class SecurityReportActivity extends AppCompatActivity { // Scans declared components and explains export risk.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when Android creates the report screen.
        super.onCreate(savedInstanceState); // Lets AppCompat initialize the activity correctly.
        setContentView(R.layout.activity_security_report); // Connects this class to its XML layout.

        TextView reportTextView = findViewById(R.id.textViewSecurityReport); // Finds the TextView that displays the report.
        Button backButton = findViewById(R.id.buttonReportBack); // Finds the Retour button.
        backButton.setOnClickListener(view -> finish()); // Returns to the previous screen.

        reportTextView.setText(buildSecurityReport()); // Generates and displays the component security report.
    }

    private String buildSecurityReport() { // Reads PackageManager data and builds a readable report.
        StringBuilder report = new StringBuilder(); // Collects report lines efficiently.
        report.append("Component Security Report\n\n"); // Adds a clear report title.

        try { // PackageManager lookups can throw if the package name is unavailable.
            PackageManager packageManager = getPackageManager(); // Gets the system service that knows package metadata.
            PackageInfo packageInfo; // Holds all requested component information.
            int flags = PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS; // Requests components.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 uses typed flag APIs.
                packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.PackageInfoFlags.of(flags)); // Reads package info.
            } else {
                packageInfo = packageManager.getPackageInfo(getPackageName(), flags); // Reads package info on older API levels.
            }

            appendActivities(report, packageInfo.activities); // Adds activity risk details.
            appendServices(report, packageInfo.services); // Adds service risk details.
            appendReceivers(report, packageInfo.receivers); // Adds receiver risk details.
        } catch (PackageManager.NameNotFoundException exception) { // Handles unexpected package lookup failure.
            report.append("Unable to read package information: ").append(exception.getMessage()).append("\n"); // Shows the error.
        }

        report.append("\nRecommendations\n"); // Adds security guidance section.
        report.append("1. Use android:exported=\"false\" for private components.\n"); // Recommends private defaults.
        report.append("2. Use explicit Intents when possible.\n"); // Recommends precise internal navigation.
        report.append("3. Use PendingIntent.FLAG_IMMUTABLE.\n"); // Recommends immutable PendingIntents.
        report.append("4. Verify caller UID for sensitive components.\n"); // Recommends caller validation.
        report.append("5. Avoid exposing Services unless necessary.\n"); // Recommends minimizing service attack surface.
        return report.toString(); // Returns the finished report for display.
    }

    private void appendActivities(StringBuilder report, ActivityInfo[] activities) { // Adds each declared activity to the report.
        report.append("Activities\n"); // Labels the activity section.
        if (activities == null) { // Handles apps with no activity array.
            report.append("No activities found.\n\n"); // Explains missing data.
            return; // Leaves the section early.
        }
        for (ActivityInfo activity : activities) { // Reviews every activity declared in the manifest.
            String risk = activity.exported ? "MEDIUM" : "LOW"; // Exported normal activities are medium risk.
            if (activity.name.endsWith("SecureActivity") && activity.exported) { // Sensitive exported activity would be high risk.
                risk = "HIGH"; // Marks exported secure component as high risk.
            }
            appendComponent(report, "Activity", activity.name, activity.exported, risk); // Adds the activity line.
        }
        report.append("\n"); // Separates this section from the next section.
    }

    private void appendServices(StringBuilder report, ServiceInfo[] services) { // Adds each declared service to the report.
        report.append("Services\n"); // Labels the service section.
        if (services == null) { // Handles apps with no service array.
            report.append("No services found.\n\n"); // Explains missing data.
            return; // Leaves the section early.
        }
        for (ServiceInfo service : services) { // Reviews every service declared in the manifest.
            String risk = service.exported ? "HIGH" : "LOW"; // Exported services are high risk because they run code for callers.
            appendComponent(report, "Service", service.name, service.exported, risk); // Adds the service line.
        }
        report.append("\n"); // Separates this section from the next section.
    }

    private void appendReceivers(StringBuilder report, ActivityInfo[] receivers) { // Adds each declared broadcast receiver to the report.
        report.append("Receivers\n"); // Labels the receiver section.
        if (receivers == null) { // Handles apps with no receiver array.
            report.append("No receivers found.\n\n"); // Explains missing data.
            return; // Leaves the section early.
        }
        for (ActivityInfo receiver : receivers) { // Reviews every receiver declared in the manifest.
            String risk = receiver.exported ? "MEDIUM" : "LOW"; // Exported receivers are medium risk in this lesson model.
            appendComponent(report, "Receiver", receiver.name, receiver.exported, risk); // Adds the receiver line.
        }
        report.append("\n"); // Separates this section from recommendations.
    }

    private void appendComponent(StringBuilder report, String type, String name, boolean exported, String risk) { // Formats one report row.
        report.append(type).append(": ").append(name).append("\n"); // Shows component type and class name.
        report.append("  exported: ").append(exported).append("\n"); // Shows whether external apps can reach it.
        report.append("  risk: ").append(risk).append("\n"); // Shows the lesson's risk level for this component.
    }
}

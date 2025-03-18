package com.example.fresh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.check.MyTest;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a LinearLayout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create a TextView
        TextView textView = new TextView(this);
        textView.setText("Hello World!");

        // Create a Button
        Button button = new Button(this);
        button.setText("Fetch");

        // Add the TextView and Button to the LinearLayout
        layout.addView(textView);
        layout.addView(button);

        // Set the LinearLayout as the content view
        setContentView(layout);

        MyTest myPlugin = new MyTest(this);
        myPlugin.whistleLoopEvents("eventName", "extraParameters");
    }
}
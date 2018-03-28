package ru.apchola.team.citywalks63;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button customRouteButton = findViewById(R.id.custom_route_button);
        Button mapOverviewButton = findViewById(R.id.map_overview_button);

        customRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMap = new Intent(getApplicationContext(), MapActivity.class);
                toMap.putExtra("mode", "custom_route");
                startActivity(toMap);
            }
        });

        mapOverviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMap = new Intent(getApplicationContext(), MapActivity.class);
                toMap.putExtra("mode", "map_overview");
                startActivity(toMap);
            }
        });
    }
}

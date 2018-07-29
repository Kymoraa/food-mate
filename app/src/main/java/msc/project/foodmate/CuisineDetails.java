package msc.project.foodmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CuisineDetails extends AppCompatActivity {

    private ImageView ivCuisine, ivFavourites;
    private TextView tvCuisineName, tvRestaurantName, tvDescription;
    private Button bCall, bDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuisine_details);

        ivCuisine = findViewById(R.id.ivCuisine);

        ivFavourites = findViewById(R.id.ivFavourites);
        tvCuisineName = findViewById(R.id.tvCuisineName);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvDescription = findViewById(R.id.tvDescription);

        bCall = findViewById(R.id.bCall);
        bDirections = findViewById(R.id.bDirections);

    }
}

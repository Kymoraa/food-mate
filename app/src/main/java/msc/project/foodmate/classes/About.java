package msc.project.foodmate.classes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import msc.project.foodmate.R;

/*
class to hold the about layout
layout with imortant information such as Ts&Cs and the privacy policy
 */

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
}

package msc.project.foodmate;

import android.widget.CheckBox;

/**
 * Created by Jackie Moraa on 7/17/2018.
 */

public class User {

    public String email;
    public CheckBox cbRestaurant;

    public User(){
        //blank constructor
    }
    public User(String email, CheckBox cbRestaurant) {
        this.email = email;
        this.cbRestaurant = cbRestaurant;
    }


}

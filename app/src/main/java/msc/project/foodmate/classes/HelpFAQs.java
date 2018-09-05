package msc.project.foodmate.classes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import msc.project.foodmate.R;
import msc.project.foodmate.view.ExpandableListViewAdapter;

/*
class to display the help and FAQs about the application in the expandable list view
 */
public class HelpFAQs extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private List<String> parentHeaderInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_faqs);

        parentHeaderInformation = new ArrayList<>();

        parentHeaderInformation.add("How do set my profile?");
        parentHeaderInformation.add("How many ingredients or diets can I enter?");
        parentHeaderInformation.add("Can I use the app without an account?");

        HashMap<String, List<String>> allChildItems = returnGroupedChildItems();

        expandableListView = findViewById(R.id.expandableListView);
        ExpandableListViewAdapter expandableListViewAdapter = new ExpandableListViewAdapter(getApplicationContext(), parentHeaderInformation, allChildItems);
        expandableListView.setAdapter(expandableListViewAdapter);
    }

    private HashMap<String, List<String>> returnGroupedChildItems() {

        HashMap<String, List<String>> childContent = new HashMap<String, List<String>>();

        childContent.put(parentHeaderInformation.get(0), depositlist());
        childContent.put(parentHeaderInformation.get(1), populate());
        childContent.put(parentHeaderInformation.get(2), populate2());

        return childContent;

    }

    public ArrayList depositlist() {
        ArrayList deposittems = new ArrayList();

        deposittems.add("Under the profile section -> " + "\n"+
                "click on one of the cards...ingredients, excluded or allergens" +"\n"+
                "Click the '+' button on top to launch a dialog to add your requirements" +"\n"+
                "Click save to add the item to your preferences." +"\n"+
                "The counter on the main profile screen lets you know how many items you have save d for each category" +"\n"+
                "To delete/update an entry -> Long press on the item");

        return deposittems;
    }

    public ArrayList populate() {
        ArrayList items = new ArrayList();
        items.add("In a registered account the number of ingredients or diets you can enter is limitless."+"\n"+
                "However, in a guest account you can only enter 5 ingredients at a time");

        return items;
    }

    public ArrayList populate2() {
        ArrayList items = new ArrayList();
        items.add("Most certainly!" +"\n"+
        "Just opt to proceed as guest on the sign in screen");

        return items;
    }
}

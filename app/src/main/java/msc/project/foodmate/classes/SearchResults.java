package msc.project.foodmate.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import msc.project.foodmate.R;
import msc.project.foodmate.database.DatabaseHelper;
import msc.project.foodmate.database.model.AllergenDB;
import msc.project.foodmate.database.model.CuisineUploads;
import msc.project.foodmate.database.model.DietDB;
import msc.project.foodmate.database.model.IngredientDB;
import msc.project.foodmate.view.SearchAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResults.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResults#newInstance} factory method to
 * create an instance of this fragment.
 */

//class to display the results from the search cuisines action
public class SearchResults extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;
    private List<CuisineUploads> mCuisineUploads;
    private EditText etSearch;
    private ProgressDialog progressDialog;
    private ValueEventListener mDBListener;
    private DatabaseHelper dbHelper;
    private TextView tvNoEntries;
    private Context mContext;
    private RelativeLayout relativeLayout;
    private SearchAdapter searchAdapter;

    public SearchResults() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResults.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResults newInstance(String param1, String param2) {
        SearchResults fragment = new SearchResults();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_results, container, false);

        tvNoEntries = view.findViewById(R.id.tvNoEntries);
        tvNoEntries.setVisibility(View.GONE);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCuisineUploads= new ArrayList<>();
        searchAdapter = new SearchAdapter(getActivity(), mCuisineUploads);
        recyclerView.setAdapter(searchAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");

        //check if the device has internet connection
        isOnline();

        //add the cuisines to the search adapter
        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCuisineUploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CuisineUploads cuisineUploads = postSnapshot.getValue(CuisineUploads.class);
                    mCuisineUploads.add(cuisineUploads);
                }

                resultsMatch();
                searchAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        etSearch = view.findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    //search method - from users input in the search edit text
    //to display only intended cuisines
    public void performSearch(){
        String userInput = etSearch.getText().toString().toLowerCase();
                List<CuisineUploads> newList = new ArrayList<>();

                //loop through the list to check if the name contains the user input
                for(CuisineUploads newCuisine : mCuisineUploads){
                    if(newCuisine.getName().toLowerCase().contains(userInput) ||
                            newCuisine.getDiet().toLowerCase().contains(userInput)){
                        newList.add(newCuisine);
                        tvNoEntries.setVisibility(View.GONE);
                        break;
                    }else if(!newCuisine.getName().toLowerCase().contains(userInput) ||
                            !newCuisine.getDiet().toLowerCase().contains(userInput)){
                        tvNoEntries.setVisibility(View.VISIBLE);
                    }
                }

                searchAdapter.searchList(newList);
    }

    //match results
    public void resultsMatch() {
        dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //fetch all the data from the diets, ingredients and allergens SQLite database
        //add them to an arrayList
        String dietQuery = "SELECT " + DietDB.COLUMN_DIET + " FROM " + DietDB.TABLE_NAME;
        Cursor dietCursor = db.rawQuery(dietQuery, null);
        ArrayList<String> dietsEntered =new ArrayList<String>() ;

        dietCursor.moveToFirst();
        if(dietCursor.getCount()>0){
            do{
                dietsEntered.add(dietCursor.getString(0));
            }while(dietCursor.moveToNext());
        }

        String ingredientQuery = "SELECT " + IngredientDB.COLUMN_INGREDIENT + " FROM " + IngredientDB.TABLE_NAME;
        Cursor ingredientCursor = db.rawQuery(ingredientQuery,null);
        ArrayList<String> ingredientsEntered=new ArrayList<String>() ;


        ingredientCursor.moveToFirst();
        if(ingredientCursor.getCount()>0){
            do{
                ingredientsEntered.add(ingredientCursor.getString(0));
            }while(ingredientCursor.moveToNext());
        }

        String allergenQuery = "SELECT " + AllergenDB.COLUMN_ALLERGEN + " FROM " + AllergenDB.TABLE_NAME;
        Cursor allergenCursor = db.rawQuery(allergenQuery,null);
        ArrayList<String> allergensEntered=new ArrayList<String>() ;


        allergenCursor.moveToFirst();
        if(allergenCursor.getCount()>0){
            do{
                allergensEntered.add(allergenCursor.getString(0));
            }while(allergenCursor.moveToNext());
        }


        dietCursor.close();
        ingredientCursor.close();
        allergenCursor.close();


        //Check for matches
        List<CuisineUploads> newList = new ArrayList<>();
        for(CuisineUploads newCuisine : mCuisineUploads){

            //use regex to append ',' used to split th array list of ingredients, diets and allergens
            String diets = newCuisine.getDiet().toLowerCase();
            ArrayList<String> cuisineDiet = new ArrayList<String>(Arrays.asList(diets.split(",")));

            String ingredients = newCuisine.getIngredients().toLowerCase();
            ArrayList<String> cuisineIngredients = new ArrayList<String>(Arrays.asList(ingredients.split(",")));

            String allergens = newCuisine.getIngredients().toLowerCase();
            ArrayList<String> cuisineAllergens = new ArrayList<String>(Arrays.asList(allergens.split(",")));

            //regex to separate the words in the arralList
            StringBuilder i_regex = new StringBuilder();
            boolean first = true;
            i_regex.append("^ *(");
            for (String w : ingredientsEntered) {
                if (!first) {
                    i_regex.append('|');
                } else {
                    first = false;
                }
                i_regex.append(w);
            }
            i_regex.append(") *$" );

            Pattern i_pattern = Pattern.compile(i_regex.toString().toLowerCase());
            boolean ingredientsMatch =false;
            for (int i = 0; i < cuisineIngredients.size(); i++) {

                if (i_pattern.matcher(cuisineIngredients.get(i)).find()) {
                    ingredientsMatch = true;
                    break;
                }
            }

            StringBuilder d_regex = new StringBuilder();
            boolean first1 = true;
            d_regex.append("^ *(");
            for (String w : dietsEntered) {
                if (!first1) {
                    d_regex.append('|');
                } else {
                    first1 = false;
                }
                d_regex.append(w);
            }
            d_regex.append(") *$" );

            //If the dish does not have the diet,
            //it is added to a new list which populates the recyclerview when the search method is invoked.
            //If the dish does not have the ingredient, it is added to a new list which populates the recyclerview
            //when the search method is invoked.
            Pattern d_pattern = Pattern.compile(d_regex.toString().toLowerCase());
            boolean dietsMatch =false;
            for (int i = 0; i < cuisineDiet.size(); i++) {

                if (d_pattern.matcher(cuisineDiet.get(i)).find() || dietsEntered.isEmpty()) {
                    // Do something
                    dietsMatch = true;
                    break;
                }
            }

            StringBuilder a_regex = new StringBuilder();
            boolean first2 = true;
            a_regex.append("^ *(");
            for (String w : allergensEntered) {
                if (!first2) {
                    a_regex.append('|');
                } else {
                    first2 = false;
                }
                a_regex.append(w);
            }
            a_regex.append(") *$" );

            Pattern a_pattern = Pattern.compile(a_regex.toString().toLowerCase());
            boolean allergensMatch =false;
            for (int i = 0; i < cuisineAllergens.size(); i++) {

                if (a_pattern.matcher(cuisineAllergens.get(i)).find()) {
                    // Do something
                    allergensMatch = true;
                    break;
                }
            }

            //if ingredients and allergens are not present and the diet is a match - show the cuisine
            if(((!ingredientsMatch) || ingredientsEntered.isEmpty())&& (dietsMatch) &&((!allergensMatch)
                    || allergensEntered.isEmpty())){
                newList.add(newCuisine);
            }


            searchAdapter.searchList(newList);

            if(newList.size()>0){
                tvNoEntries.setVisibility(View.GONE);
            }else{
                tvNoEntries.setVisibility(View.VISIBLE);
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
    }

    //menu items - filter/search recycler view results

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);

   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else{
            Toast.makeText(mContext, "You appear to be offline", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

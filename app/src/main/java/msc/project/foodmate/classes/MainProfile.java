package msc.project.foodmate.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import msc.project.foodmate.R;
import msc.project.foodmate.database.DatabaseHelper;
import msc.project.foodmate.database.model.AllergenDB;
import msc.project.foodmate.database.model.DietDB;
import msc.project.foodmate.database.model.IngredientDB;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainProfile#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
main  customer profile fragment
to set their preferences
diets, ingredients to exclude, allergens
 */
public class MainProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView tvUser, tvDietsCount, tvIngredientsCount, tvAllergensCount;
    private CardView cvDiets, cvIngredients, cvAllergens;
    private ActionBar aBar;
    private DatabaseHelper dbHelper;

    public MainProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static MainProfile newInstance(String param1, String param2) {
        MainProfile fragment = new MainProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_profile, container, false);

        aBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        //check the current user and set their email on the profile page
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        tvUser = view.findViewById(R.id.tvUser);

            if (firebaseAuth.getCurrentUser() != null) {
                tvUser.setText(firebaseUser.getEmail());
            }

        //actions when the cardviews are clicked
        cvDiets = view.findViewById(R.id.cvDiets);
        cvDiets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aBar.setTitle(R.string.diets);
                Diets diets = new Diets();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, diets,"Find This Fragment")
                        .addToBackStack(null)
                        .commit();

            }
        });

        cvIngredients = view.findViewById(R.id.cvIngredients);
        cvIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aBar.setTitle("Excluded Ingredients");
                Ingredients ingredients = new Ingredients();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, ingredients,"Find This Fragment")
                        .addToBackStack(null)
                        .commit();

            }
        });

        cvAllergens = view.findViewById(R.id.cvAllergens);
        cvAllergens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aBar.setTitle(R.string.allergens);
                Allergens allergens = new Allergens();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, allergens,"Find This Fragment")
                        .addToBackStack(null)
                        .commit();

            }
        });

        //views to set the SQLite db item counts
        tvDietsCount = view.findViewById(R.id.tvDietsCount);
        tvIngredientsCount = view.findViewById(R.id.tvIngredientsCount);
        tvAllergensCount = view.findViewById(R.id.tvAllergensCount);

        getDietDBCount();
        getIngredientDBCount();
        getAllergenDBCount();

        return view;

    }

    //get the diets count and set them on the diets textview
    public int getDietDBCount() {
        String countQuery = "SELECT  * FROM " + DietDB.TABLE_NAME;
        dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        tvDietsCount.setText(""+count);
        cursor.close();
        return count;
    }

    //get the ingredients count and set them on the ingredients textview
    public int getIngredientDBCount() {
        String countQuery = "SELECT  * FROM " + IngredientDB.TABLE_NAME;
        dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        tvIngredientsCount.setText(""+count);
        cursor.close();
        return count;
    }

    //get the allergens count and set them on the allergens textview
    public int getAllergenDBCount() {
        String countQuery = "SELECT  * FROM " + AllergenDB.TABLE_NAME;
        dbHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        tvAllergensCount.setText(""+count);
        cursor.close();
        return count;
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
}

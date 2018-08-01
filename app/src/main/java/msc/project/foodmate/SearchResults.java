package msc.project.foodmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResults.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResults#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResults extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private DatabaseReference databaseReference;
    private List<CuisineUploads> mCuisineUploads;
    private EditText etSearch;
    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    public static final String MYPREFERENCES = "dietPreferences";


    private Context mContext;

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

//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.search_results);

        sharedPreferences = getActivity().getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCuisineUploads= new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CuisineUploads cuisineUploads = postSnapshot.getValue(CuisineUploads.class);
                    mCuisineUploads.add(cuisineUploads);
                }


                searchAdapter = new SearchAdapter(getActivity(), mCuisineUploads);
                recyclerView.setAdapter(searchAdapter);

                resultsMatch();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new SearchTextWatcher(etSearch));
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

        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(etSearch);



        return view;
    }

    //search method
    public void performSearch(){
        String userInput = etSearch.getText().toString().toLowerCase();
                List<CuisineUploads> newList = new ArrayList<>();

                for(CuisineUploads newCuisine : mCuisineUploads){
                    if(newCuisine.getName().toLowerCase().contains(userInput)){
                        newList.add(newCuisine);
                    }

                }

                searchAdapter.searchList(newList);
    }

    //match results
    public void resultsMatch(){
//        String diet = "vegan";
//        List<CuisineUploads> newList = new ArrayList<>();
//
//        for(CuisineUploads newCuisine : mCuisineUploads){
//            if(newCuisine.getDiet().toLowerCase().contains(diet)){
//                newList.add(newCuisine);
//            }
//
//        }
//
//        searchAdapter.searchList(newList);

//        if(sharedPreferences.contains(MYPREFERENCES)){
//            String savedItems = sharedPreferences.getString(MYPREFERENCES, "").toLowerCase();
//            List<CuisineUploads> newList = new ArrayList<>();
//            for(CuisineUploads newCuisine : mCuisineUploads){
//                if(newCuisine.getDiet().toLowerCase().contains(savedItems)){
//                    newList.add(newCuisine);
//                }
//
//            }
//            searchAdapter.searchList(newList);
//
//            System.out.println("Search Results: " + savedItems);
//        }


    }

    //menu items - filter/search recycler view results

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.search_menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.menu_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();

   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    //Text watcher - Search EditText
    public class SearchTextWatcher implements TextWatcher {
        public SearchTextWatcher(EditText e) {
            etSearch = e;
            etSearch.setTypeface(Typeface.SERIF);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            etSearch.setTypeface(Typeface.SERIF);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etSearch.setTypeface(Typeface.SERIF);
        }

        public void afterTextChanged(Editable s) {
            if(s.length() == 0){
                etSearch.setTypeface(Typeface.SERIF);
            } else {
                etSearch.setTypeface(Typeface.SERIF);
            }

        }

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

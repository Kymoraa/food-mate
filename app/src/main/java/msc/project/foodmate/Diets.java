package msc.project.foodmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Diets.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Diets extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ListView lvDiets;
    private DatabaseReference databaseReference;
    private ArrayList<String>arrayList = new ArrayList<>();
    private ArrayAdapter<String>adapter;

    SharedPreferences sharedPreferences;
    public static final String MYPREFERENCES = "dietPreferences";
    ArrayList<String> selectedItems = new ArrayList<>();

    public Diets() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.diets, container, false);

        lvDiets = view.findViewById(R.id.lvDiets);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, arrayList);
        lvDiets.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvDiets.setAdapter(adapter);

        sharedPreferences = getActivity().getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

//        Set<String> checkedItemsSource = sharedPreferences.getStringSet("checked_items", new HashSet<String>());
//        SparseBooleanArray checkedItems = convertToCheckedItems(checkedItemsSource);
//        for (int i = 0; i < checkedItems.size(); i++) {
//            int checkedPosition = checkedItems.keyAt(i);
//            lvDiets.setItemChecked(checkedPosition, true);
//
//        }

        //Opening shared preference in private mode
        //sharedPreferences = getActivity().getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


            if(sharedPreferences.contains(MYPREFERENCES)){
                loadSelections();

                //System.out.println("This are the shared preferences: " + MYPREFERENCES);

            }

        //Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("dietsList");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String string = dataSnapshot.getValue(String.class);
                arrayList.add(string);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    //checked state of the list view
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        SparseBooleanArray checkedItems = lvDiets.getCheckedItemPositions();
//        Set<String> stringSet = convertToStringSet(checkedItems);
//        sharedPreferences.edit()
//                .putStringSet("checked_items", stringSet)
//                .apply();
//    }

//    private SparseBooleanArray convertToCheckedItems(Set<String> checkedItems) {
//        SparseBooleanArray array = new SparseBooleanArray();
//        for(String itemPositionStr : checkedItems) {
//            int position = Integer.parseInt(itemPositionStr);
//            array.put(position, true);
//        }
//
//        return array;
//    }

//    private Set<String> convertToStringSet(SparseBooleanArray checkedItems) {
//        Set<String> result = new HashSet<>();
//        for (int i = 0; i < checkedItems.size(); i++) {
//            result.add(String.valueOf(checkedItems.keyAt(i)));
//        }
//
//        return result;
//    }

    //Menu items
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.save_selection, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_save) {

            String selected = "";
            int cntChoice = lvDiets.getCount();
            SparseBooleanArray sparseBooleanArray = lvDiets.getCheckedItemPositions();

            for (int i = 0; i < cntChoice; i++) {

                if (sparseBooleanArray.get(i)) {
                    selected += lvDiets.getItemAtPosition(i).toString() + "\n";
                    System.out.println("Checking list while adding:" + lvDiets.getItemAtPosition(i).toString());

                    Toast.makeText(getActivity(), "Selected", Toast.LENGTH_SHORT).show();

                    saveSelections();

                }

            }
        }

        return super.onOptionsItemSelected(item);
    }

        public void saveSelections() {

            //save the selections in the shared preference in private mode for the user
            SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
            String savedItems = getSavedItems();
            preferenceEditor.putString(MYPREFERENCES, savedItems);
            preferenceEditor.commit();

            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
        }


        public String getSavedItems(){
            String savedItems = "";
            int count = this.lvDiets.getAdapter().getCount();

            for (int i =0; i<count; i++){
                if(this.lvDiets.isItemChecked(i)){
                    if(savedItems.length()>0){
                        savedItems+="," + this.lvDiets.getItemAtPosition(i);
                    }else{
                        savedItems+=this.lvDiets.getItemAtPosition(i);
                    }
                }
            }

            System.out.println("Saved Items are: " + savedItems);
            return savedItems;

        }

    public void loadSelections(){
        //if selections were previously saved, load them
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        if(sharedPreferences.contains(MYPREFERENCES)){
            String savedItems = sharedPreferences.getString(MYPREFERENCES, "");
            selectedItems.addAll(Arrays.asList(savedItems.split(",")));

            int count = this.lvDiets.getAdapter().getCount();
            for (int i =0; i<count; i++){
                String currentItem = (String) this.lvDiets.getAdapter().getItem(i);

                if(selectedItems.contains(currentItem)){
                    this.lvDiets.setItemChecked(i, true);

                    Toast.makeText(getActivity(), "Current Item: " + currentItem, Toast.LENGTH_LONG).show();
                }else{
                    this.lvDiets.setItemChecked(i, false);
                }
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

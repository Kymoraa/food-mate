package msc.project.foodmate.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import msc.project.foodmate.R;
import msc.project.foodmate.database.model.CuisineUploads;
import msc.project.foodmate.view.RestaurantAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RestaurantHome#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
displays all the cuisines that the restaurant has uploaded in the database
swiping the cards removes the data from the database
 */
public class RestaurantHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private DatabaseReference databaseReference;
    private List<CuisineUploads> mCuisineUploads;
    private ValueEventListener mDBListener;
    private TextView tvNoEntries;
    private RelativeLayout relativeLayout;
    private FirebaseStorage mStorage;


    public RestaurantHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantHome.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantHome newInstance(String param1, String param2) {
        RestaurantHome fragment = new RestaurantHome();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.restaurant_home, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");
        tvNoEntries = view.findViewById(R.id.tvNoEntries);
        relativeLayout = view.findViewById(R.id.relativeLayout);

        mStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");

        //recyclerview
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mCuisineUploads= new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(getActivity(), mCuisineUploads);
        recyclerView.setAdapter(restaurantAdapter);

        //check for internet connectivity
        isOnline();

        //set the uploaded cuisines in the restaurant adapter
        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCuisineUploads.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    CuisineUploads cuisineUploads = postSnapshot.getValue(CuisineUploads.class);
                    cuisineUploads.setKey(postSnapshot.getKey());
                    mCuisineUploads.add(cuisineUploads);
                }
                restaurantAdapter.notifyDataSetChanged();

                toggleEmptyList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    //Swiping recyclerview
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            try {

                //delete item at position that is swiped
                final int position = viewHolder.getAdapterPosition();

                CuisineUploads selectedItem = mCuisineUploads.get(position);
                final String selectedKey = selectedItem.getKey();
                StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUri());
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.child(selectedKey).removeValue();
                        Toast.makeText(getActivity(), "Cuisine item deleted", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to delete cuisine", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch(Exception e){
                Toast.makeText(getActivity(), e.getMessage() + ": Failed to delete cuisine", Toast.LENGTH_SHORT).show();
                System.out.println(e.getMessage());

            }

        }
    };

    private void toggleEmptyList() {
        //check if the list is empty
        // toggle the textview if empty
        //hide if it contains data
        if (mCuisineUploads.size() > 0) {
            tvNoEntries.setVisibility(View.GONE);
        } else {
            tvNoEntries.setVisibility(View.VISIBLE);
        }
    }

    //check for internet connectivity
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else{
            Toast.makeText(getActivity(), "You appear to be offline", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mDBListener);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
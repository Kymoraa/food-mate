package msc.project.foodmate.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import msc.project.foodmate.R;
import msc.project.foodmate.database.DatabaseHelper;
import msc.project.foodmate.database.model.AllergenDB;
import msc.project.foodmate.utils.MyDividerItemDecoration;
import msc.project.foodmate.utils.RecyclerTouchListener;
import msc.project.foodmate.view.AllergensAdapter;


/*
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Allergens.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Allergens extends Fragment {

    /*
    fragment to display all the allergens stored in the
    allergens table in the SQLite database
     */

    private OnFragmentInteractionListener mListener;
    private AllergensAdapter mAdapter;
    private List<AllergenDB> allergensList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView tvNoEntries;
    private DatabaseHelper db;

    public Allergens() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.allergen, container, false);



        coordinatorLayout = view.findViewById(R.id.coordinator_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        tvNoEntries = view.findViewById(R.id.tvNoEntries);

        db = new DatabaseHelper(getActivity());

        allergensList.addAll(db.getAllAllergens());

        mAdapter = new AllergensAdapter(getActivity(), allergensList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        //if no allergens are set in the database,
        // method to toggle the textView
        toggleEmptyAllergens();

        /*
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));



        return view;

    }

    /*
     * Inserting new allergen in db
     * and refreshing the list
     */
    private void createAllergen(String allergen) {
        // inserting allergen in db and getting
        // newly inserted allergen id
        long id = db.insertAllergen(allergen);

        // get the newly inserted allergen from db
        AllergenDB n = db.getAllergenDB(id);

        if (n != null) {
            // adding new allergen to array list at 0 position
            allergensList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyAllergens();
        }
    }

    /*
     * Updating allergen in db and updating
     * item in the list by its position
     */
    private void updateAllergen(String allergen, int position) {
        AllergenDB n = allergensList.get(position);
        // updating text
        n.setAllergenDB(allergen);

        // updating in db
        db.updateAllergenDB(n);

        // refreshing the list
        allergensList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyAllergens();
    }

    /*
     * Deleting allergen from SQLite and removing the
     * item from the list by its position
     */
    private void deleteAllergen(int position) {
        // deleting the allergen from db
        db.deleteAllergenDB(allergensList.get(position));

        // removing from the list
        allergensList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyAllergens();
    }


    /*
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showAllergenDialog(true, allergensList.get(position), position);
                } else {
                    deleteAllergen(position);
                }
            }
        });
        builder.show();
    }

    /*
     * Shows alert dialog with EditText options to enter / edit
     * a allergen.
     * when shouldUpdate=true, it automatically displays old allergen and changes the
     * button text to UPDATE
     */
    private void showAllergenDialog(final boolean shouldUpdate, final AllergenDB allergen, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View view = layoutInflaterAndroid.inflate(R.layout.allergen_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(view);

        final EditText inputAllergen = view.findViewById(R.id.allergen);
        inputAllergen.setTypeface(Typeface.SERIF);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? "New Allergen" : "Edit Allergen");

        if (shouldUpdate && allergen != null) {
            inputAllergen.setText(allergen.getAllergenDB());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered; when special characters are used
                Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(inputAllergen.getText().toString());
                boolean b = m.find();

                if (TextUtils.isEmpty(inputAllergen.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter allergen...", Toast.LENGTH_SHORT).show();
                    return;
                }else if (b){
                    Toast.makeText(getActivity(), "Enter one diet at a time. Avoid special characters", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    alertDialog.dismiss();
                }

                // check if user updating allergen
                if (shouldUpdate && allergen != null) {
                    // update allergen by it's id
                    updateAllergen(inputAllergen.getText().toString().trim(), position);
                } else {
                    // create new allergen
                    createAllergen(inputAllergen.getText().toString().trim());
                }
            }
        });
    }

    /*
     * Toggling list and empty allergens view
     */
    private void toggleEmptyAllergens() {
        //you can check allergensList.size() > 0

        if (db.getAllergenDBCount() > 0) {
            tvNoEntries.setVisibility(View.GONE);
        } else {
            tvNoEntries.setVisibility(View.VISIBLE);
        }
    }

    //Menu items
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add) {
            showAllergenDialog(false, null, -1);
        }

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


}

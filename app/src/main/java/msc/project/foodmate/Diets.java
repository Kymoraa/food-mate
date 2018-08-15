package msc.project.foodmate;

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

import msc.project.foodmate.database.DatabaseHelper;
import msc.project.foodmate.database.model.DietDB;
import msc.project.foodmate.utils.MyDividerItemDecoration;
import msc.project.foodmate.utils.RecyclerTouchListener;
import msc.project.foodmate.view.DietsAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Diets.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Diets extends Fragment {

    private OnFragmentInteractionListener mListener;

    private DietsAdapter mAdapter;
    private List<DietDB> dietsList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView tvNoEntries;

    private DatabaseHelper db;

    public Diets() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.diet, container, false);

        /**
         * *********************
         */

        coordinatorLayout = view.findViewById(R.id.coordinator_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        tvNoEntries = view.findViewById(R.id.tvNoEntries);

        db = new DatabaseHelper(getActivity());

        dietsList.addAll(db.getAllDiets());

        mAdapter = new DietsAdapter(getActivity(), dietsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyDiets();

        /**
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

    /**
     * Inserting new diet in db
     * and refreshing the list
     */
    private void createDiet(String diet) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertDiet(diet);

        // get the newly inserted note from db
        DietDB n = db.getDietDB(id);

        if (n != null) {
            // adding new diet to array list at 0 position
            dietsList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyDiets();
        }
    }

    /**
     * Updating diet in db and updating
     * item in the list by its position
     */
    private void updateDiet(String diet, int position) {
        DietDB n = dietsList.get(position);
        // updating note text
        n.setDietDB(diet);

        // updating note in db
        db.updateDietDB(n);

        // refreshing the list
        dietsList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyDiets();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteDiet(int position) {
        // deleting the note from db
        db.deleteDietDB(dietsList.get(position));

        // removing the note from the list
        dietsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyDiets();
    }


    /**
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
                    showDietDialog(true, dietsList.get(position), position);
                } else {
                    deleteDiet(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showDietDialog(final boolean shouldUpdate, final DietDB diet, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View view = layoutInflaterAndroid.inflate(R.layout.diet_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(view);

        final EditText inputDiet = view.findViewById(R.id.diet);
        inputDiet.setTypeface(Typeface.SERIF);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && diet != null) {
            inputDiet.setText(diet.getDietDB());
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
                Matcher m = p.matcher(inputDiet.getText().toString());
                boolean b = m.find();

                if (TextUtils.isEmpty(inputDiet.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter diet...", Toast.LENGTH_SHORT).show();
                    return;
                           }else if (b){
                Toast.makeText(getActivity(), "Enter one diet at a time. Avoid special characters", Toast.LENGTH_SHORT).show();
                return;
            }else {
                alertDialog.dismiss();
            }

                // check if user updating diet
                if (shouldUpdate && diet != null) {
                    // update diet by it's id
                    updateDiet(inputDiet.getText().toString().trim(), position);
                } else {
                    // create new diet
                    createDiet(inputDiet.getText().toString().trim());
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyDiets() {
        // you can check notesList.size() > 0

        if (db.getDietDBCount() > 0) {
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
            showDietDialog(false, null, -1);
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

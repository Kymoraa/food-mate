package msc.project.foodmate.classes;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import msc.project.foodmate.R;
import msc.project.foodmate.database.model.CuisineUploads;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantAdd.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
/*
for the restaurants
add a new cuisine/dish to the Firebase database
 */
public class RestaurantAdd extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FrameLayout frameLayout;
    private ImageView ivCuisine;
    private EditText etCuisineName, etPrice, etDescription, etIngredients, etDiet;
    private StorageTask uploadTask;
    private FloatingActionButton fab;

    //storage and database paths in Firebase
    String mStoragePath = "cuisineUploads/";
    String mDatabasePath = "cuisineUploads";
    Uri mFilePathUri;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    ProgressDialog mProgressDialog;

    int IMAGE_REQUEST_CODE = 5;
    String imageUrl;

    public RestaurantAdd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.restaurant_add, container, false);

        frameLayout = view.findViewById(R.id.frameLayout);

        ivCuisine = view.findViewById(R.id.ivCuisine);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }

        });

        etCuisineName = view.findViewById(R.id.etCuisineName);
        etPrice = view.findViewById(R.id.etPrice);
        etDescription = view.findViewById(R.id.etDescription);
        etIngredients = view.findViewById(R.id.etIngredients);
        etDiet = view.findViewById(R.id.etDiet);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(getActivity());


        return view;
    }

    //open the image files in the device to select the cuisine image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null && data.getData() != null ){

            mFilePathUri = data.getData();

            try{
                //getting selected image into bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mFilePathUri);
                //set bitmap to imageview
                ivCuisine.setImageBitmap(bitmap);


            }catch (Exception e){
                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }



    //menu items - upload button
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.actionbar_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_upload) {
            //upload the cuisines database
            if (uploadTask != null && uploadTask.isInProgress()){
                //Toast.makeText(getActivity(), "Upload already in progress", Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(frameLayout, "Upload already in progress", Snackbar.LENGTH_LONG);
                snackbar.show ();
            }else {
                uploadCuisine();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //method to upload the cuisine to the database
    private void uploadCuisine(){

        //check whether the filepath is empty or not
        if(mFilePathUri != null){
            //setting the progress bar title
            mProgressDialog.setTitle("Uploading...");
            mProgressDialog.show();

            //create a second storage reference

            final StorageReference storageReference2 = mStorageReference.child(mStoragePath + System.currentTimeMillis()
                    + "." + getFileExtension(mFilePathUri));

            //adding addOnSuccessListener to Storage Reference
            storageReference2.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //get the edit text values
                            String name = etCuisineName.getText().toString().trim();
                            String price = etPrice.getText().toString().trim();
                            String description = etDescription.getText().toString().trim();
                            String ingredients = etIngredients.getText().toString().trim();
                            String diet = etDiet.getText().toString().trim();

                            //hide progress dialog
                            mProgressDialog.dismiss();

                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                            Snackbar snackbar = Snackbar.make(frameLayout, "Upload successful", Snackbar.LENGTH_LONG);
                            snackbar.show ();

                            RestaurantHome restaurantHome = new RestaurantHome();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayout, restaurantHome,"Find This Fragment")
                                    .addToBackStack(null)
                                    .commit();

                            CuisineUploads cuisineUploads = new CuisineUploads(name, price, description, ingredients,
                                    //taskSnapshot.getStorage().getDownloadUrl().toString(),diet);
                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),diet);

                            //getting image ID
                            String imageUploadID = mDatabaseReference.push().getKey();
                            //uploading it into database reference
                            mDatabaseReference.child(imageUploadID).setValue(cuisineUploads);

                        }
                    })
                    //if something goes wrong
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();

                            //show error toast
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Snackbar snackbar = Snackbar.make(frameLayout, e.getMessage(), Snackbar.LENGTH_LONG);
                            snackbar.show ();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.setTitle("Uploading...");

                        }
                    });

        }else{
            Toast.makeText(getActivity(), "Please select image", Toast.LENGTH_SHORT).show();
        }

    }

    //method to get the selected image file extension from the file path uri
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        //returning the file extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

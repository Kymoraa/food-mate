package msc.project.foodmate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantAdd.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RestaurantAdd extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final int CHOOSE_FILE = 1;

    private ImageView ivCuisine;
    private Button bChooseFile;
    private EditText etCuisineName, etPrice, etDescription, etIngredients;
    private ProgressBar pbUpload;
    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTask;

    public RestaurantAdd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.restaurant_add, container, false);

        ivCuisine = view.findViewById(R.id.ivCuisine);
        bChooseFile = view.findViewById(R.id.bChooseFile);
        bChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }


        });

        etCuisineName = view.findViewById(R.id.etCuisineName);
        etPrice = view.findViewById(R.id.etPrice);
        etDescription = view.findViewById(R.id.etDescription);
        etIngredients = view.findViewById(R.id.etIngredients);
        pbUpload = view.findViewById(R.id.pbUpload);

        storageReference = FirebaseStorage.getInstance().getReference("cuisineUploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("cuisineUploads");

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CHOOSE_FILE && resultCode == RESULT_OK && data !=null && data.getData() != null ){

            imageUri = data.getData();
            Picasso.get().load(imageUri).into(ivCuisine);

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
                Toast.makeText(getActivity(), "Upload already in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadCuisine();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadCuisine(){

        if (imageUri != null){

            StorageReference sRef = storageReference.child(System.currentTimeMillis() + "." +
            getFileExtension(imageUri));

            uploadTask = sRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pbUpload.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();

                            CuisineUploads cuisineUploads = new CuisineUploads(etCuisineName.getText().toString().trim(),
                                    etPrice.getText().toString().trim(), etDescription.getText().toString().trim(),
                                    etIngredients.getText().toString().trim(), taskSnapshot.getMetadata().getReference()
                                    .getDownloadUrl().toString());

                            String uploadID = databaseReference.push().getKey();
                            databaseReference.child(uploadID).setValue(cuisineUploads);

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            Fragment fragment = new RestaurantHome();
                            transaction.replace(R.id.frameLayout, fragment);
                            transaction.commit();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progressBar = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            pbUpload.setProgress((int) progressBar);

                        }
                    });

        }else{
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
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

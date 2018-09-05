package msc.project.foodmate.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import msc.project.foodmate.R;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RestaurantProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */

/*
class to set the restaurant profile
 */
public class RestaurantProfile extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FrameLayout frameLayout;
    private EditText etRestaurantName, etRestaurantPhone, etRestaurantWebsite;
    private FloatingActionButton fab;
    private ImageView ivRestaurant;
    ProgressDialog mProgressDialog;
    int IMAGE_REQUEST_CODE = 5;
    Uri profileImageUri;
    FirebaseAuth mAuth;
    String profileImageUrl;

    public RestaurantProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        frameLayout = view.findViewById(R.id.frameLayout);
        ivRestaurant = view.findViewById(R.id.ivRestaurant);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }

        });

        etRestaurantName = view.findViewById(R.id.etRestaurantName);
        etRestaurantPhone = view.findViewById(R.id.etRestaurantPhone);
        etRestaurantWebsite = view.findViewById(R.id.etRestaurantWebsite);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null && data.getData() != null){
            profileImageUri = data.getData();

            try{
                //getting selected image into bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), profileImageUri);
                //set bitmap to imageview
                ivRestaurant.setImageBitmap(bitmap);
                uploadImage();

            }catch (Exception e){

                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }
    }

    //upload the image to profile uploads storage
    private void uploadImage() {
        final StorageReference profileReference = FirebaseStorage.getInstance().getReference("profileUploads/"
                +System.currentTimeMillis()+".jpg");

        if (profileImageUri!=null){
            profileReference.putFile(profileImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    //do something with downloadurl
                                    profileImageUrl = downloadUrl.toString();

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Profile picture upload failed! "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }

    public void saveRestaurant(){
        String restaurantName = etRestaurantName.getText().toString();
        String restaurantNumber = etRestaurantPhone.getText().toString();
        String restaurantWebsite = etRestaurantWebsite.getText().toString();

        if (restaurantName.isEmpty()||restaurantNumber.isEmpty()||restaurantWebsite.isEmpty()){
            Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null&&profileImageUrl !=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(restaurantName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
//                    .setRestaurantNumber(restaurantNumber)
//                    .setRestaurantWebsite(restaurantWebsite)

                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    //open the intent to select images from the library
    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Restaurant Profile Image"), IMAGE_REQUEST_CODE);
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
        inflater.inflate(R.menu.save_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_save) {
            //save the profile to the database
            saveRestaurant();

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

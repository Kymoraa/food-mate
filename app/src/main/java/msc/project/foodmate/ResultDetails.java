package msc.project.foodmate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultDetails.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ResultDetails extends Fragment {

    private ImageView ivCuisine, ivFavourites;
    private TextView tvCuisineName, tvRestaurantName, tvDescription;
    private Button bCall, bDirections;

    private OnFragmentInteractionListener mListener;

    public ResultDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.result_details, container, false);


        ivCuisine = view.findViewById(R.id.ivCuisine);
        ivFavourites = view.findViewById(R.id.ivFavourites);
        tvCuisineName = view.findViewById(R.id.tvCuisineName);
        tvRestaurantName = view.findViewById(R.id.tvRestaurantName);
        tvDescription = view.findViewById(R.id.tvDescription);
        bCall = view.findViewById(R.id.bCall);
        bDirections = view.findViewById(R.id.bDirections);



        return view;
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

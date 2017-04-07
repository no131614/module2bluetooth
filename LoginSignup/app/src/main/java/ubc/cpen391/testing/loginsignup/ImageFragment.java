package ubc.cpen391.testing.loginsignup;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by james on 2017-04-04.
 */

public class ImageFragment extends DialogFragment{

    Uri uri;

    static ImageFragment newInstance(String s) {
        ImageFragment f = new ImageFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("uri", s);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = Uri.parse(getArguments().getString("uri"));

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_marker, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.imageView);

        Picasso.with(getActivity()).load(uri).rotate(90).centerCrop().fit().into(image);

        Button button = (Button)rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                dismiss();
            }
        });

        return rootView;
    }
}

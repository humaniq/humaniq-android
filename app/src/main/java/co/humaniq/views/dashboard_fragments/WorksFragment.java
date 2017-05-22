package co.humaniq.views.dashboard_fragments;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import co.humaniq.R;
import co.humaniq.views.BaseFragment;


public class WorksFragment extends BaseFragment {
    void fillItem(View root, final @IdRes int id, final @DrawableRes int image, final boolean isNew) {
        View layout = root.findViewById(id);
        TextView text = (TextView) layout.findViewById(R.id.textView9);
        ImageView imageView = (ImageView) layout.findViewById(R.id.imageView16);

        imageView.setImageResource(image);
        text.setVisibility(isNew ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_works_under_construction, container, false);

//        fillItem(view, R.id.buttonTwitter, R.mipmap.twitter, true);
//        fillItem(view, R.id.buttonTwitch, R.mipmap.twitch, false);
//        fillItem(view, R.id.buttonYoutube, R.mipmap.youtube, false);
//        fillItem(view, R.id.buttonFacebook, R.mipmap.facebook, false);
//        fillItem(view, R.id.buttonPaypal, R.mipmap.paypal, false);
//        fillItem(view, R.id.buttonInstagram, R.mipmap.instagram, false);

        return view;
    }
}

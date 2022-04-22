package be.kuleuven.buddy.other;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import be.kuleuven.buddy.R;

public class InfoFragment extends DialogFragment {

    private static String titleText, bodyText;
    TextView title, body;

    public static InfoFragment newInstance(String tText, String bText) {
        titleText = tText;
        bodyText = bText;
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        title = view.findViewById(R.id.titleFragment);
        body = view.findViewById(R.id.bodyFragment);
        title.setText(titleText);
        body.setText(bodyText);
        return view;
    }
}

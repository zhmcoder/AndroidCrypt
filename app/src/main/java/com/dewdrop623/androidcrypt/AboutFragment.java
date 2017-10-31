package com.dewdrop623.androidcrypt;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * This fragment is the about page accessed via the action bar from MainActivityFragment
 */

public class AboutFragment extends Fragment {
    WebView aboutWebView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        aboutWebView = (WebView) view.findViewById(R.id.aboutWebView);
        

        return view;
    }


}

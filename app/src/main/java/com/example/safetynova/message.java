package com.example.safetynova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class message extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout containing the WebView
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Initialize the WebView
        webView = view.findViewById(R.id.webView);

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Ensure links open in WebView, not the default browser
        webView.setWebViewClient(new WebViewClient());

        // Load your website URL
        webView.loadUrl("https://livelocate.netlify.app/");

        return view;
    }

    @Override
    public void onDestroyView() {
        // Clean up WebView resources to avoid memory leaks
        if (webView != null) {
            webView.clearCache(true);
            webView.destroy();
        }
        super.onDestroyView();
    }
}

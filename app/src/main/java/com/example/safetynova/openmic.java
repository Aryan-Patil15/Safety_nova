package com.example.safetynova;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class openmic extends Fragment {

    private View f1, f2, f3;
    private Button btnMaleCall, btnFemaleCall, btnAccept, btnDecline;

    public openmic() {
        super(R.layout.fragment_openmic); // Ensure your XML layout filename matches
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        f1 = view.findViewById(R.id.f1);
        f2 = view.findViewById(R.id.f2);
        f3 = view.findViewById(R.id.f3);
        btnMaleCall = view.findViewById(R.id.btnMaleCall);
        btnFemaleCall = view.findViewById(R.id.btnFemaleCall);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);

        // Show f1 initially
        showFrame(f1);

        // Click listeners
        btnMaleCall.setOnClickListener(v -> showFrame(f2));
        btnFemaleCall.setOnClickListener(v -> showFrame(f2));
        btnAccept.setOnClickListener(v -> showFrame(f3));
        btnDecline.setOnClickListener(v -> showFrame(f1));
    }

    private void showFrame(View frameToShow) {
        // Set visibility for all frames
        f1.setVisibility(View.GONE);
        f2.setVisibility(View.GONE);
        f3.setVisibility(View.GONE);

        // Make the selected frame visible and bring it to front
        frameToShow.setVisibility(View.VISIBLE);
        frameToShow.bringToFront();
        if(frameToShow==f2) {
            Toast.makeText(getContext(), "Frame shown is f2", Toast.LENGTH_SHORT).show();
        }
        frameToShow.requestLayout();
        frameToShow.invalidate();
    }
}

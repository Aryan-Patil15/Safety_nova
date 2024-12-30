package com.example.safetynova;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_emergency_location_fragment).setOnClickListener(v ->
                Toast.makeText(getContext(), "Crises Alert clicked", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btn_emergency_services_fragment).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Emergencyservices.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btn_location_fragment).setOnClickListener(v ->
                Toast.makeText(getContext(), "SOS clicked", Toast.LENGTH_SHORT).show());
    }
}
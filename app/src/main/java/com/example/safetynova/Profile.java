package com.example.safetynova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Profile extends Fragment {

    private TextView profileName, userName, userBirthday, userPhone, userTrustedContacts, userEmail, userMedicalInfo;
    private ImageView profilePicture;
    private Button editProfileButton;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        profileName = view.findViewById(R.id.profile_name);
        profilePicture = view.findViewById(R.id.profile_picture);
        editProfileButton = view.findViewById(R.id.edit_profile_button);

        // Details section
        userTrustedContacts = view.findViewById(R.id.trusted_contacts);
        userMedicalInfo = view.findViewById(R.id.medical_info);

        // Edit Profile Button functionality
        editProfileButton.setOnClickListener(v -> {
            // Implement action for editing the profile
        });

        // Set up navigation for userMedicalInfo
        userMedicalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Medical());
            }
        });

        return view;
    }


    private void loadFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Ensure this ID matches your container in the activity layout
                    .addToBackStack(null) // Optional: Allows the user to navigate back
                    .commit();
        }
    }
}

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
        userName = view.findViewById(R.id.details_container).findViewWithTag("userName");
        userBirthday = view.findViewById(R.id.details_container).findViewWithTag("userBirthday");
        userPhone = view.findViewById(R.id.details_container).findViewWithTag("userPhone");
        userTrustedContacts = view.findViewById(R.id.details_container).findViewWithTag("userTrustedContacts");
        userEmail = view.findViewById(R.id.details_container).findViewWithTag("userEmail");
        userMedicalInfo = view.findViewById(R.id.details_container).findViewWithTag("userMedicalInfo");

        // Set data for UI components (hardcoded example data for now)
        profileName.setText("Anna Avetisyan");
        userName.setText("Anna Avetisyan");
        userBirthday.setText("Birthday: 01/01/1990");
        userPhone.setText("818 123 4567");
        userTrustedContacts.setText("Trusted Contacts");
        userEmail.setText("info@aplusdesign.co");
        userMedicalInfo.setText("Medical Information");

        // Edit Profile Button functionality
        editProfileButton.setOnClickListener(v -> {
            // Implement action for editing the profile
            // Example: Navigate to another fragment or open an edit screen
        });

        return view;
    }
}

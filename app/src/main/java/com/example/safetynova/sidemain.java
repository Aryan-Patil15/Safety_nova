package com.example.safetynova;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

public class sidemain extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout dw;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home, container, false);

        dw = view.findViewById(R.id.drawer_layout); // Initialize DrawerLayout
        Toolbar tb = view.findViewById(R.id.toolbar); // Initialize Toolbar

        // Attach the toolbar to the containing activity
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(tb);
        }

        NavigationView nv = view.findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle
        ActionBarDrawerToggle t = new ActionBarDrawerToggle(
                getActivity(), dw, tb, R.string.open, R.string.close);
        dw.addDrawerListener(t);
        t.syncState();

        // Load default fragment
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.f1, new HomeFragment())
                    .commit();
            nv.setCheckedItem(R.id.ho);
        }

        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ho) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.f1, new HomeFragment())
                    .commit();
        } else if (item.getItemId() == R.id.in) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.f1, new About())
                    .commit();
        } else if (item.getItemId() == R.id.we) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.f1, new fragment_weather())
                    .commit();
        } else if (item.getItemId() == R.id.lg) {
            Toast.makeText(getContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        dw.closeDrawer(GravityCompat.START);
        return true;
    }
}

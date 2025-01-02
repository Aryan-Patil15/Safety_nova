package com.example.safetynova;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Emergencyservices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Emergencyservices extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Emergencyservices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Emergencyservices.
     */
    // TODO: Rename and change types and number of parameters
    public static Emergencyservices newInstance(String param1, String param2) {
        Emergencyservices fragment = new Emergencyservices();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergencyservices, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
                view.findViewById(R.id.button1).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:108"));
                startActivity(intent);});
                view.findViewById(R.id.button2).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:107"));
                startActivity(intent);});
                view.findViewById(R.id.button3).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:101"));
                startActivity(intent);});
                view.findViewById(R.id.button4).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:100"));
                startActivity(intent);});
                view.findViewById(R.id.button5).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:181"));
                startActivity(intent);});
                view.findViewById(R.id.button6).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:112"));
                startActivity(intent);});
                view.findViewById(R.id.button7).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1363"));
                startActivity(intent);});
                view.findViewById(R.id.button8).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1930"));
                startActivity(intent);});
                view.findViewById(R.id.button9).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:14567"));
                startActivity(intent);});
                view.findViewById(R.id.button10).setOnClickListener(v-> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1906"));
                startActivity(intent);});
    }

}
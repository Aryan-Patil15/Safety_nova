package com.example.safetynova;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class openmic extends Fragment {

    private ConstraintLayout mainLayout;
    private LinearLayout fakeCallButtonsLayout;
    private ConstraintLayout incomingCallLayout;
    private TextView tvFakeReply;
    private Button btnMaleCall, btnFemaleCall, btnAccept, btnDecline,btn;
    private MediaPlayer ringtone,mediaPlayer = null;

    public openmic() {
        super(R.layout.fragment_openmic); // Ensure your XML layout filename matches
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);// Initialize Views
        mainLayout = view.findViewById(R.id.mainLayout);
        fakeCallButtonsLayout = view.findViewById(R.id.fakeCallButtonsLayout);
        incomingCallLayout = view.findViewById(R.id.incomingCallLayout);
        tvFakeReply = view.findViewById(R.id.tvFakeReply);
        btnMaleCall = view.findViewById(R.id.btnMaleCall);
        btnFemaleCall = view.findViewById(R.id.btnFemaleCall);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);

        ringtone = MediaPlayer.create(getContext(),R.raw.ringtone);
        ringtone.start();
        // Show fakeCallButtonsLayout initially
        showFrame(fakeCallButtonsLayout);

        // Click listeners
        btnMaleCall.setOnClickListener(v -> {
            showFrame(incomingCallLayout);
            btn = btnMaleCall;
        });
        btnFemaleCall.setOnClickListener(v -> {
            showFrame(incomingCallLayout);
            btn = btnFemaleCall;
        });
        btnAccept.setOnClickListener(v -> {
            ringtone.stop();
            showFrame(tvFakeReply);
            play();
        });
        btnDecline.setOnClickListener(v -> showFrame(fakeCallButtonsLayout));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ringtone.stop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void showFrame(View frameToShow) {
        // Set visibility for all frames
        fakeCallButtonsLayout.setVisibility(View.GONE);
        incomingCallLayout.setVisibility(View.GONE);
        tvFakeReply.setVisibility(View.GONE);
        // Make the selected frame visible
        frameToShow.setVisibility(View.VISIBLE);
        if(frameToShow==tvFakeReply)
        {
            if(btn==btnMaleCall) {
                tvFakeReply.setText("हाँ पापा, मैं ठीक हूँ। कैब मिल गई है, बस निकल रहा हूँ। आप चिंता मत करो, मैं पहुँचकर कॉल कर दूँगा। Bye!");
            }
            else
            {
                tvFakeReply.setText("हाँ मम्मी, मैं ठीक हूँ। कैब मिल गई है, बस निकल रहा हूँ। आप चिंता मत करो, मैं पहुँचकर कॉल कर दूँगा। Bye!");
            }
        }
    }
    private void play()
    {
        if (btn == btnMaleCall) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(getContext(),R.raw.male); // Replace with your file name
            }
        } else if (btn == btnFemaleCall) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(getContext(),R.raw.female); // Replace with your file name
            }
        }

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(getContext(), "Fake Call sound playing", Toast.LENGTH_SHORT).show();
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }
}
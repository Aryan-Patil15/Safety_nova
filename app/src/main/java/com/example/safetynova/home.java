package com.example.safetynova;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class home extends AppCompatActivity {

    private Button btnView, btnDismiss, btnEmergencyLocation, btnEmergencyServices;
    private ImageButton btnLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons
        btnView = findViewById(R.id.btn_view);
        btnDismiss = findViewById(R.id.btn_dismiss);
        btnEmergencyLocation = findViewById(R.id.btn_emergency_location);
        btnEmergencyServices = findViewById(R.id.btn_emergency_services);
        btnLocation = findViewById(R.id.nav_maps);

        // Set click listeners
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "View button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "Dismiss button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmergencyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "Emergency Location Sharing clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnEmergencyServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "Emergency Services clicked", Toast.LENGTH_SHORT).show();
            }

            class BlurryImageView extends androidx.appcompat.widget.AppCompatImageView {
                private Paint paint;
                private Bitmap bitmap;

                public BlurryImageView(Context context) {
                    super(context);
                    init();
                }

                public BlurryImageView(Context context, AttributeSet attrs) {
                    super(context, attrs);
                    init();
                }

                public BlurryImageView(Context context, AttributeSet attrs, int defStyle) {
                    super(context, attrs, defStyle);
                    init();
                }

                private void init() {
                    paint = new Paint();
                    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
                }

                @Override
                protected void onDraw(Canvas canvas) {
                    if (getDrawable() != null) {
                        bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
                        canvas.drawBitmap(bitmap, 0, 0, paint);
                        blur(canvas, bitmap);
                    }
                }

                private void blur(Canvas canvas, Bitmap bitmap) {
                    int radius = 10; // adjust the blur radius as needed
                    android.graphics.BlurMaskFilter filter = new android.graphics.BlurMaskFilter(radius, android.graphics.BlurMaskFilter.Blur.NORMAL);
                    paint.setMaskFilter(filter);
                    canvas.drawBitmap(bitmap, 0, 0, paint);
                }
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this, Map.class);
                startActivity(intent);
            }
        });
    }
}


package com.example.memo.camerameasure;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class main_menu extends Activity  {
    @Override


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        Button btn_offline = (Button) findViewById(R.id.btn_off);//object
        Button btn_online = (Button) findViewById(R.id.btn_on);//interior
        Button btn_tutorial = (Button) findViewById(R.id.btn_tuto);


        ShapeDrawable shapedrawable = new ShapeDrawable();
        shapedrawable.setShape(new RectShape());
        shapedrawable.getPaint().setColor(Color.LTGRAY);
        shapedrawable.getPaint().setStrokeWidth(10f);
        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
        btn_offline.setBackground(shapedrawable);
        btn_online.setBackground(shapedrawable);
        btn_tutorial.setBackground(shapedrawable);


        btn_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_menu.this, ImageAccess.class));
            }
        });
        btn_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main_menu.this, CameraFocusActivity.class));
            }
        });
        btn_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               setContentView(R.layout.tutorial);


            }
        });


    }
    public void onBackPressed()
    {
        startActivity(new Intent(main_menu.this, main_menu.class));

    }
}
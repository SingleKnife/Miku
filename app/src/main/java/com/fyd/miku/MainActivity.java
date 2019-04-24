package com.fyd.miku;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fyd.miku.model.mmd.MikuModel;
import com.fyd.miku.model.pmd.PMDFile;
import com.fyd.miku.model.vmd.VMDFile;
import com.fyd.miku.model.render.MikuRender;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    MikuRender mikuRender;
    MikuModel mikuModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView surfaceView = findViewById(R.id.gl_surface_view);
        surfaceView.setEGLContextClientVersion(2);
        mikuRender = new MikuRender(this);
        MikuGLRender glRender = new MikuGLRender(this, mikuRender);
        surfaceView.setRenderer(glRender);

        Button button = findViewById(R.id.load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parsePmd();
            }
        });
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
    }

    private void parsePmd() {
        AssetManager assetManager = getAssets();
        try {
            PMDFile pmdParser = new PMDFile();
            InputStream inputStream = assetManager.open("Miku_Hatsune_Ver2.pmd");
            pmdParser.parse(inputStream);
            mikuModel = new MikuModel(pmdParser);
            mikuRender.setMikuModel(mikuModel);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startAnimation() {
        AssetManager assetManager = getAssets();
        VMDFile vmdFile = new VMDFile();
        InputStream inputStream;
        try {
//            inputStream = assetManager.open("wavefile_full_miku_v2.vmd");
            inputStream = assetManager.open("ik3.vmd");
            vmdFile.parse(inputStream);
            mikuModel.attachMotion(vmdFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

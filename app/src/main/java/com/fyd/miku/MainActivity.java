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
    GLSurfaceView surfaceView;
    MikuGLRender mikuGLRender;
    int frame = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.gl_surface_view);
        surfaceView.setEGLContextClientVersion(2);
        mikuGLRender = new MikuGLRender(this);
        surfaceView.setRenderer(mikuGLRender);

        Button button = findViewById(R.id.load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parsePmd();
            }
        });
        findViewById(R.id.load_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAnimation();
            }
        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mikuModel != null) {
                    mikuModel.startAnimation();
                }
            }
        });

        findViewById(R.id.increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mikuModel != null) {
                    mikuModel.setFrame(++frame);
                }
            }
        });

        findViewById(R.id.decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mikuModel != null) {
                    mikuModel.setFrame(--frame);
                }
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
            mikuRender = new MikuRender(this, mikuModel);
            mikuGLRender.setMikuRender(mikuRender);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAnimation() {
        AssetManager assetManager = getAssets();
        VMDFile vmdFile = new VMDFile();
        InputStream inputStream;
        try {
//            inputStream = assetManager.open("wavefile_full_miku_v2.vmd");
            inputStream = assetManager.open("motion.vmd");
            vmdFile.parse(inputStream);
            mikuModel.attachMotion(vmdFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

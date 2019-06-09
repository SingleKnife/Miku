package com.fyd.miku;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fyd.miku.model.mmd.MikuModel;
import com.fyd.miku.model.render.MikuRender;
import com.fyd.miku.model.vmd.VMDFile;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    MikuRender mikuRender;
    MikuModel mikuModel;
    GLSurfaceView surfaceView;
    MikuGLRender mikuGLRender;

    Disposable loadModelDisposable;
    Disposable loadMotionDisposable;

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
                loadModel();
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
    }

    private void loadModel() {
        loadModelDisposable = DefaultModelManager.initAndLoadDefaultModel(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MikuModel>() {
                    @Override
                    public void accept(MikuModel mikuModel) {
                        MainActivity.this.mikuModel = mikuModel;
                        mikuRender = new MikuRender(MainActivity.this, mikuModel);
                        mikuGLRender.setMikuRender(mikuRender);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable){
                        Log.e("fyd", "load model failed");
                    }
                });
    }

    private void loadAnimation() {
        loadMotionDisposable = DefaultModelManager.initAndLoadDefaultMotion(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VMDFile>() {
                    @Override
                    public void accept(VMDFile vmdFile) {
                        mikuModel.attachMotion(vmdFile);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e("fyd", "load motion failed");
                    }
                });

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
        if(loadModelDisposable != null && !loadModelDisposable.isDisposed()) {
            loadModelDisposable.dispose();
        }

        if(loadMotionDisposable != null && !loadMotionDisposable.isDisposed()) {
            loadMotionDisposable.dispose();
        }
        super.onDestroy();
    }
}

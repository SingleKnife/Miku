package com.fyd.miku;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.fyd.miku.helper.ShaderHelper;
import com.fyd.miku.model.render.MikuRender;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MikuGLRender implements GLSurfaceView.Renderer {
    MikuRender mikuRender;

    public MikuGLRender(Context context, MikuRender mikuRender) {
        this.mikuRender = mikuRender;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1, 1, 1, 1);
        if(mikuRender != null) {
            mikuRender.onSurfaceCreate();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if(mikuRender != null) {
            mikuRender.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if(mikuRender != null) {
            mikuRender.draw();
        }
        ShaderHelper.checkGlError("onDrawFrame");
    }
}

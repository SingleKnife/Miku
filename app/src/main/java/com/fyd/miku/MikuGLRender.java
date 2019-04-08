package com.fyd.miku;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.fyd.miku.helper.ShaderHelper;
import com.fyd.miku.model.render.MikuRender;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MikuGLRender implements GLSurfaceView.Renderer {
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    MikuRender mikuRender;
    AxisRender axisRender;

    public MikuGLRender(Context context, MikuRender mikuRender) {
        axisRender = new AxisRender(context);
        this.mikuRender = mikuRender;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1, 1, 1, 1);
        axisRender.onSurfaceCreate();
        if(mikuRender != null) {
            mikuRender.onSurfaceCreate();
        }
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1, 1000);
        Matrix.setLookAtM(viewMatrix, 0, 0, 10, 40,
                0, 7, 0, 0, 1, 0);
        axisRender.onSurfaceChanged(width, height);
        if(mikuRender != null) {
            mikuRender.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        axisRender.beginDraw();
        axisRender.updateMatrix(projectionMatrix, viewMatrix);
        axisRender.draw();
        axisRender.endDraw();

        if(mikuRender != null) {
            mikuRender.beginDraw();
            mikuRender.updateMatrix(projectionMatrix, viewMatrix);
            mikuRender.draw();
            mikuRender.endDraw();
        }
//        ShaderHelper.checkGlError("draw");
    }
}

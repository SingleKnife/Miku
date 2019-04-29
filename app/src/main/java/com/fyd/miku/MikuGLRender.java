package com.fyd.miku;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.fyd.miku.model.render.AxisRender;
import com.fyd.miku.model.render.MikuRender;
import com.fyd.miku.model.render.Render;

import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MikuGLRender implements GLSurfaceView.Renderer {
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    private MikuRender mikuRender;
    private AxisRender axisRender;

    private final Queue<Runnable> runOnDraw;

    public MikuGLRender(Context context) {
        axisRender = new AxisRender(context);
        runOnDraw = new LinkedList<>();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1, 1, 1, 1);
        axisRender.createOnGlThread();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1, 1000);
        Matrix.setLookAtM(viewMatrix, 0, 0, 10, 40,
                0, 7, 0, 0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll();

        axisRender.beginDraw();
        axisRender.updateMatrix(projectionMatrix, viewMatrix);
        axisRender.draw();

        if(mikuRender != null) {
            mikuRender.beginDraw();
            mikuRender.updateMatrix(projectionMatrix, viewMatrix);
            mikuRender.draw();
        }
    }

    public void setMikuRender(final MikuRender newRender) {
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                MikuRender oldRender = mikuRender;
                if(oldRender != null) {
                    oldRender.destroy();
                }
                newRender.createOnGLThread();
                mikuRender = newRender;
            }
        });
    }

    private void runAll() {
        synchronized (runOnDraw) {
            while (!runOnDraw.isEmpty()) {
                Runnable runnable = runOnDraw.poll();
                runnable.run();
            }
        }
    }

    private void runOnGLThread(Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.add(runnable);
        }
    }
}

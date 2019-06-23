package com.fyd.miku;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.fyd.miku.model.render.AxisRender;
import com.fyd.miku.model.render.MikuRender;
import com.fyd.miku.model.render.PlaneRender;

import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;

public class MikuGLRender implements GLSurfaceView.Renderer {
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    private MikuRender mikuRender;
    private AxisRender axisRender;
    private PlaneRender planRender;

    private int depthMapFBO = -1;
    private int depthMap = -1;

    private float[] lightDir = {2f, 2f, 4f};
    private float[] lightColor = {1.0f, 1.0f, 1.0f};

    //光照空间中变换矩阵
    private float[] lightProjectionMatrix = new float[16];
    private float[] lightViewMatrix = new float[16];

    private final Queue<Runnable> runOnDraw;

    public MikuGLRender(Context context) {
        axisRender = new AxisRender(context);
        planRender = new PlaneRender(context);
        runOnDraw = new LinkedList<>();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1, 1, 1, 1);
        axisRender.createOnGlThread();
        planRender.createOnGLThread();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1, 1000);
        Matrix.setLookAtM(viewMatrix, 0, 0, 10, 40,
                0, 7, 0, 0, 1, 0);
        generateFBO(width, height);
        Matrix.orthoM(lightProjectionMatrix, 0, -10f, 10f, -10f, 10f, 0.1f, 15.5f);
        Matrix.setLookAtM(lightViewMatrix, 0, lightDir[0], lightDir[1], lightDir[2],
                0, 0, 0, 0, 1, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        clearShadowMap();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll();

        axisRender.beginDraw();
        axisRender.updateMatrix(projectionMatrix, viewMatrix);
        axisRender.draw();

        planRender.beginDraw();
        planRender.updateMatrix(projectionMatrix, viewMatrix);
        planRender.draw();

        if(mikuRender != null) {
            mikuRender.beginDraw();
            mikuRender.setLight(lightDir, lightColor);
            beginDrawShadowMap();
            mikuRender.updateMatrix(projectionMatrix, viewMatrix);
            mikuRender.draw();
            endDrawShadowMap();
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

    private void clearShadowMap() {
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void beginDrawShadowMap() {
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
    }

    private void endDrawShadowMap() {
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    private void generateFBO(int width, int height) {
        int[] bufferId = new int[1];
        if (depthMap == -1) {
            glGenTextures(1, bufferId, 0);
            depthMap = bufferId[0];
            glBindTexture(GL_TEXTURE_2D, depthMap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        if(depthMap != -1) {
            GLES20.glDeleteFramebuffers(1, new int[]{depthMapFBO}, 0);
        }
        glGenFramebuffers(1, bufferId, 0);
        depthMapFBO = bufferId[0];
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // check FBO status
        int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("fyd", "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }
}

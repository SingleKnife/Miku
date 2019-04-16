package com.fyd.miku.model.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.fyd.miku.model.pmd.Material;
import com.fyd.miku.model.mmd.Mesh;
import com.fyd.miku.model.mmd.MikuModel;

import java.nio.ByteBuffer;

public class MikuRender {
    public static final int MAX_SUPPORT_BONE_NUM = 50;

    private MikuModel mikuModel;
    private MikuRenderProgram renderProgram;
    private Context context;
    int[] toonTextures = new int[10];


    private float[] modelMatrix = new float[16];

    private float[] lightDir = {2f, 2f, 4f};
    private float[] lightColor = {1.0f, 1.0f, 1.0f};

    public MikuRender(Context context) {
        this.context = context;
    }

    public void onSurfaceCreate() {
        renderProgram = new MikuRenderProgram(context);
    }

    public void onSurfaceChanged(int width, int height) {

        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.scaleM(modelMatrix, 0, 1, 1, -1f);
//        Matrix.rotateM(modelMatrix, 0, 180f, 0f, 1, 0);
        generateToonTextures();
    }

    public void beginDraw() {
        renderProgram.useProgram();
    }

    public void draw() {
        if(mikuModel == null) {
            return;
        }

        renderProgram.setLight(lightDir, lightColor);
        drawModel(mikuModel);
    }

    public void endDraw() {

    }

    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        renderProgram.updateMatrix(projectionMatrix, viewMatrix, modelMatrix);
    }

    private void drawModel(MikuModel model) {
        renderProgram.bindVertexData(model.getAllVertex());
        renderProgram.bindBoneMatrices(model.getBoneManager().getAllMatrices());
        for(Mesh mesh : model.getMeshes()) {
            drawMesh(model, mesh);
        }
    }

    private void drawMesh(MikuModel model, Mesh mesh) {
        Material material = mesh.getMaterial();
        renderProgram.setAmbient(material.getAmbientColor());
        renderProgram.setDiffuse(material.getDiffuseColor());
        renderProgram.setSpecular(material.getSpecularColor(), material.getSpecularPower());

        boolean hasToonTexture = material.hasToonTexture();
        int toonTextureId = hasToonTexture ? toonTextures[material.getToonIndex()] : -1;
        renderProgram.setToonTexture(hasToonTexture, toonTextureId);

        ByteBuffer indexBuffer = model.getAllVertex().getIndices(mesh.getIndexOffset());
        renderProgram.draw(indexBuffer, mesh.getIndexCount());
    }

    public void setMikuModel(MikuModel model) {
        this.mikuModel = model;
    }

    private void generateToonTextures() {
        GLES20.glGenTextures(10, toonTextures, 0);
        for(int i = 0; i < 10; ++i) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, toonTextures[i]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

            Bitmap toonBitmap = BitmapFactory.decodeResource(context.getResources(), Material.toonRes[i]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, toonBitmap, 0);
            toonBitmap.recycle();
        }
    }
}

package com.fyd.miku.model.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.fyd.bullet.Physics;
import com.fyd.miku.model.mmd.MikuModel;
import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.Material;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MikuRender implements Render{
    private MikuModel mikuModel;
    private MikuRenderProgram renderProgram;
    private Context context;
    private int[] toonTextures = new int[10];
    private Map<String, Integer> textures = new HashMap<>();

    private int vertexBufferId;
    private int indexBufferId;
    private int boneIndexBufferId;

    private float[] modelMatrix = new float[16];
    private float[] projectionMatrix = new float[16];

    private float[] boneMatrices = new float[Material.MAX_BONE_SIZE * 16];

    public MikuRender(Context context, MikuModel mikuModel) {
        this.context = context;
        this.mikuModel = mikuModel;
    }

    public void createOnGLThread() {
        Log.i("MikuRender", "createOnGLThread: " + Thread.currentThread());
        renderProgram = new MikuRenderProgram(context);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(modelMatrix, 0, 1, 1, -1f);
        generateToonTextures();

        int[] bufferIds = new int[3];
        GLES20.glGenBuffers(3, bufferIds, 0);
        vertexBufferId = bufferIds[0];
        indexBufferId = bufferIds[1];
        boneIndexBufferId = bufferIds[2];

        AllVertex vertexData = mikuModel.getAllVertex();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.getAllVertices().limit(),
                vertexData.getAllVertices(), GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexData.getIndices().limit(),
                vertexData.getIndices(), GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        int boneIndexBufferPerMaterial = mikuModel.getMaterials().get(0).getBoneIndexBuffer().limit();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, boneIndexBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, boneIndexBufferPerMaterial * mikuModel.getMaterials().size(),
                null, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, boneIndexBufferId);
        for(int i = 0; i < mikuModel.getMaterials().size(); ++i) {
            Material material = mikuModel.getMaterials().get(i);
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, i * boneIndexBufferPerMaterial,
                    material.getBoneIndexBuffer().limit(), material.getBoneIndexBuffer());
        }
    }

    @Override
    public void beginDraw() {
        mikuModel.updateMotion();
        renderProgram.useProgram();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
        AllVertex vertexData = mikuModel.getAllVertex();
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.getAllVertices().limit(),
                vertexData.getAllVertices());
        renderProgram.bindVertexData(mikuModel.getAllVertex());
    }

    public void setLight(float[] lightDir, float[] lightColor) {
        renderProgram.setLight(lightDir, lightColor);
    }

    @Override
    public void draw() {
        drawModel();
    }

    @Override
    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        Matrix.multiplyMM(this.projectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Physics.updateProjectionMatrix(this.projectionMatrix);
        renderProgram.updateMatrix(projectionMatrix, viewMatrix, modelMatrix);
    }

    @Override
    public void destroy() {
        renderProgram.destroy();
        int[] bufferIds = {vertexBufferId, indexBufferId};
        GLES20.glDeleteBuffers(2, bufferIds, 0);
    }

    private void drawModel() {
        for(int i = 0; i < mikuModel.getMaterials().size(); ++i) {
            drawMaterial(i);
        }

    }

    @Override
    public void endDraw() {
        renderProgram.endDraw();
    }

    private void drawMaterial(int materialIndex) {
        Material material = mikuModel.getMaterials().get(materialIndex);
        renderProgram.setAmbient(material.getAmbientColor());
        renderProgram.setDiffuse(material.getDiffuseColor());
        renderProgram.setSpecular(material.getSpecularColor(), material.getSpecularPower());

        boolean hasToonTexture = material.hasToonTexture();
        int toonTextureId = hasToonTexture ? toonTextures[material.getToonIndex()] : -1;
        renderProgram.setToonTexture(hasToonTexture, toonTextureId);

        for(short i = 0; i < material.getBoneIndexMapping().size(); ++i) {
            short boneIndexOfAll = material.getBoneIndexMapping().get(i);
            System.arraycopy(mikuModel.getBoneManager().getAllMatrices(), boneIndexOfAll * 16,
                    boneMatrices, i * 16, 16);
        }
        renderProgram.bindBoneMatrices(boneMatrices);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, boneIndexBufferId);
        renderProgram.bindBoneIndex(material.getBoneIndexBuffer().limit() * materialIndex);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);


        boolean hasTexture = material.hasTexture();
        if(hasTexture) {
            Integer textureId = textures.get(material.getTextureName());
            if(textureId == null) {
                textureId = generateTexture(material.getTextureName());
                if(textureId > 0) {
                    textures.put(material.getTextureName(), textureId);
                }
            }
            if(textureId > 0) {
                renderProgram.setTexture(true, textureId);
            }
        } else {
            renderProgram.setTexture(false, -1);
        }

        int indexByteOffset = mikuModel.getAllVertex().getIndicesByteOffset(material.getVertexIndexOffset());
        renderProgram.draw(indexByteOffset, material.getVertexIndicesNum());
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

    private int generateTexture(String textureName) {
        File file = new File(textureName);
        if(!file.exists()) {
            Log.i("fyd", "generateTexture: " + textureName + " not exist");
            return -1;
        }

        int[] texture = new int[1];
        GLES20.glGenTextures(1 , texture, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);

        Bitmap textureBitmap = BitmapFactory.decodeFile(textureName);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
        GLES20.glGenerateMipmap(texture[0]);
        textureBitmap.recycle();

        return texture[0];
    }
}

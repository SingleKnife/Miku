package com.fyd.miku.model.render;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.fyd.miku.R;
import com.fyd.miku.helper.ResourceHelper;
import com.fyd.miku.helper.ShaderHelper;
import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.Material;

import java.nio.ByteBuffer;

public class MikuRenderProgram {
    private int aPositionLocation;
    private int aNormalLocation;
    private int aUVLocation;
    private int aBoneIndicesLocation;
    private int aBoneWeightAndEdgeFlagLocation;

    private int uProjectionMatrixLocation;
    private int uViewMatrixLocation;
    private int uModelMatrixLocation;          //model view matrix
    private int uBoneMatricesLocation;

    private int uLightColorLocation;
    private int uLightDirLocation;

    private int uDiffuseLocation;
    private int uSpecularPowerLocation;
    private int uSpecularLocation;
    private int uAmbientLocation;

    private int uHasToonLocation;
    private int uToonTextureLocation;

    private int uHasTextureLocation;
    private int uTextureLocation;

    private int program;



    public MikuRenderProgram(Context context) {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.mmd_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.mmd_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        aPositionLocation = GLES20.glGetAttribLocation(program, "aPosition");
        aNormalLocation = GLES20.glGetAttribLocation(program, "aNormal");
        aUVLocation = GLES20.glGetAttribLocation(program, "aUV");
        aBoneIndicesLocation = GLES20.glGetAttribLocation(program, "aBoneIndices");
        aBoneWeightAndEdgeFlagLocation = GLES20.glGetAttribLocation(program, "aBoneWeightAndEdgeFlag");

        uProjectionMatrixLocation = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        uViewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");
        uModelMatrixLocation = GLES20.glGetUniformLocation(program, "uModelMatrix");
        uBoneMatricesLocation = GLES20.glGetUniformLocation(program, "uBoneMatrices");

        uLightColorLocation = GLES20.glGetUniformLocation(program, "uLightColor");
        uLightDirLocation = GLES20.glGetUniformLocation(program, "uLightDir");

        uDiffuseLocation = GLES20.glGetUniformLocation(program, "uDiffuse");
        uSpecularPowerLocation = GLES20.glGetUniformLocation(program, "uSpecularPower");
        uSpecularLocation = GLES20.glGetUniformLocation(program, "uSpecular");
        uAmbientLocation = GLES20.glGetUniformLocation(program, "uAmbient");

        uHasToonLocation = GLES20.glGetUniformLocation(program, "uHasToon");
        uToonTextureLocation = GLES20.glGetUniformLocation(program, "uToonTexture");

        uHasTextureLocation = GLES20.glGetUniformLocation(program, "uHasTexture");
        uTextureLocation = GLES20.glGetUniformLocation(program, "uTextrue");
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }

    public void bindVertexData(AllVertex allVertex) {
        GLES20.glVertexAttribPointer(aPositionLocation, AllVertex.VERTEX_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getPositionByteOffset());
        GLES20.glVertexAttribPointer(aUVLocation, AllVertex.UV_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getUVByteOffset());
        GLES20.glVertexAttribPointer(aNormalLocation, AllVertex.NORMAL_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getNormalByteOffset());
//        GLES20.glVertexAttribPointer(aBoneIndicesLocation, AllVertex.BONE_INDEX_COMPONENT_SIZE,
//                GLES20.GL_UNSIGNED_SHORT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getBoneIndexByteOffset());

        GLES20.glVertexAttribPointer(aBoneWeightAndEdgeFlagLocation, AllVertex.BONE_WEIGHT_AND_EDGE_FLAG_COMPONENT_SIZE,
                GLES20.GL_UNSIGNED_BYTE, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getBoneWeightAndEdgeFlagByteOffset());
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glEnableVertexAttribArray(aUVLocation);
        GLES20.glEnableVertexAttribArray(aNormalLocation);
        GLES20.glEnableVertexAttribArray(aBoneIndicesLocation);
        GLES20.glEnableVertexAttribArray(aBoneWeightAndEdgeFlagLocation);


    }

    public void bindBoneIndex(ByteBuffer boneIndexBuffer) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glVertexAttribPointer(aBoneIndicesLocation, AllVertex.BONE_INDEX_COMPONENT_SIZE,
                GLES20.GL_UNSIGNED_SHORT, false, 0, boneIndexBuffer);
    }

    public void bindBoneMatrices(float[] boneMatrices) {
        GLES20.glUniformMatrix4fv(uBoneMatricesLocation, boneMatrices.length / 16, false, boneMatrices, 0);
    }

    void setLight(float[] lightDir, float[] lightColor) {
        GLES20.glUniform3fv(uLightDirLocation, 1, lightDir, 0);
        GLES20.glUniform3fv(uLightColorLocation, 1, lightColor, 0);
    }

    void setDiffuse(float[] diffuse) {
        GLES20.glUniform4fv(uDiffuseLocation, 1, diffuse, 0);
    }

    void setSpecular(float[] specular, float specularPower) {
        GLES20.glUniform3fv(uSpecularLocation, 1, specular, 0);
        GLES20.glUniform1f(uSpecularPowerLocation, specularPower);
    }

    void setAmbient(float[] ambient) {
        GLES20.glUniform3fv(uAmbientLocation, 1, ambient, 0);
    }

    void setToonTexture(boolean hasToon, int textureId) {
        GLES20.glUniform1i(uHasToonLocation, hasToon ? 1 : 0);
        GLES20.glUniform1i(uToonTextureLocation, 0);
        if(hasToon) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        }
    }

    void setTexture(boolean hasTexture, int textureId) {
        GLES20.glUniform1i(uHasTextureLocation, hasTexture ? 1 : 0);
        GLES20.glUniform1i(uTextureLocation, 1);
        if(hasTexture) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        }
    }


    void updateMatrix(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix) {
        GLES20.glUniformMatrix4fv(uProjectionMatrixLocation, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uViewMatrixLocation, 1, false, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(uModelMatrixLocation, 1, false, modelMatrix, 0);
    }

    void draw(int indexByteOffset, int indexCount) {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexByteOffset);
    }

     void endDraw() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aUVLocation);
        GLES20.glDisableVertexAttribArray(aNormalLocation);
        GLES20.glDisableVertexAttribArray(aBoneIndicesLocation);
        GLES20.glDisableVertexAttribArray(aBoneWeightAndEdgeFlagLocation);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    void destroy() {
        GLES20.glDeleteProgram(program);
    }

}

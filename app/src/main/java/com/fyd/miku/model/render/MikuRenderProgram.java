package com.fyd.miku.model.render;

import android.content.Context;
import android.opengl.GLES20;

import com.fyd.miku.R;
import com.fyd.miku.helper.ResourceHelper;
import com.fyd.miku.helper.ShaderHelper;
import com.fyd.miku.model.pmd.AllVertex;

import java.nio.ByteBuffer;

public class MikuRenderProgram {
    private int aPositionLocation;
    private int aNormalLocation;
    private int aUVLocation;

    private int uProjectionMatrixLocaiton;
    private int uViewMatrixLocation;

    private int uLightColorLocation;
    private int uLightDirLocation;

    private int uDiffuseLocation;
    private int uSpecularPowerLocation;
    private int uSpecularLocation;
    private int uAmbientLocation;

    private int program;

    public MikuRenderProgram(Context context) {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.mmd_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.mmd_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        aPositionLocation = GLES20.glGetAttribLocation(program, "aPosition");
        aNormalLocation = GLES20.glGetAttribLocation(program, "aNormal");
        aUVLocation = GLES20.glGetAttribLocation(program, "aUV");
        uProjectionMatrixLocaiton = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        uViewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");

        uLightColorLocation = GLES20.glGetUniformLocation(program, "uLightColor");
        uLightDirLocation = GLES20.glGetUniformLocation(program, "uLightDir");

        uDiffuseLocation = GLES20.glGetUniformLocation(program, "uDiffuse");
        uSpecularPowerLocation = GLES20.glGetUniformLocation(program, "uSpecularPower");
        uSpecularLocation = GLES20.glGetUniformLocation(program, "uSpecular");
        uAmbientLocation = GLES20.glGetUniformLocation(program, "uAmbientLocation");
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }

    public void bindVertexData(AllVertex allVertex) {
        GLES20.glVertexAttribPointer(aPositionLocation, AllVertex.VERTEX_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getPositionBuffer());
        GLES20.glVertexAttribPointer(aUVLocation, AllVertex.UV_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getUVBuffer());
        GLES20.glVertexAttribPointer(aNormalLocation, AllVertex.NORMAL_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getNormalBuffer());
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glEnableVertexAttribArray(aUVLocation);
        GLES20.glEnableVertexAttribArray(aNormalLocation);
    }

    void setLigth(float[] lightDir, float[] lightColor) {
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

    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUniformMatrix4fv(uProjectionMatrixLocaiton, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uViewMatrixLocation, 1, false, viewMatrix, 0);
    }

    public void draw(ByteBuffer indicesBuffer, int indexCount) {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
    }

}

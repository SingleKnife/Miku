package com.fyd.miku.model.render;

import android.content.Context;
import android.opengl.GLES20;

import com.fyd.miku.R;
import com.fyd.miku.helper.ResourceHelper;
import com.fyd.miku.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PlaneRender implements Render {
    private static final float HALF_X = 100;
    private static final float HALF_Z = 100;
    private static final float Y = 0.00f;

    float planVertices[] = {
            // Positions          // Normals         // Texture Coords
            HALF_X, Y, HALF_Z, 0.0f, 1.0f, 0.0f, 25.0f, 0.0f,
            -HALF_X, Y, -HALF_Z, 0.0f, 1.0f, 0.0f, 0.0f, 25.0f,
            -HALF_X, Y, HALF_Z, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,

            HALF_X, Y, HALF_Z, 0.0f, 1.0f, 0.0f, 25.0f, 0.0f,
            HALF_X, Y, -HALF_Z, 0.0f, 1.0f, 0.0f, 25.0f, 25.0f,
            -HALF_X, Y, -HALF_Z, 0.0f, 1.0f, 0.0f, 0.0f, 25.0f
    };
    private FloatBuffer verticesBuffer;
    private Context context;

    private int program;
    private int vertexAttributeLocation;
    private int viewMatrixLocation;
    private int projectionMatrixLocation;
    private int lightSpaceMatrixLocation;
    private int shadowMapLocation;
    private int isDrawingShadowLocation;

    public void createOnGLThread() {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.plan_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.plan_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        vertexAttributeLocation = GLES20.glGetAttribLocation(program, "aVertex");
        viewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");
        projectionMatrixLocation = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        lightSpaceMatrixLocation = GLES20.glGetUniformLocation(program, "uLightSpaceMatrix");
        shadowMapLocation = GLES20.glGetUniformLocation(program, "uShadowMap");
        isDrawingShadowLocation = GLES20.glGetUniformLocation(program, "uIsDrawingShadow");
        verticesBuffer = ByteBuffer.allocateDirect(planVertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        verticesBuffer.put(planVertices);
        verticesBuffer.position(0);
    }

    public PlaneRender(Context context) {
        this.context = context;
    }

    @Override
    public void beginDraw() {
        GLES20.glUseProgram(program);
        GLES20.glVertexAttribPointer(vertexAttributeLocation, 3, GLES20.GL_FLOAT, false, 8 * 4, verticesBuffer);
        GLES20.glEnableVertexAttribArray(vertexAttributeLocation);
    }

    @Override
    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    public void drawShadow(int shadowMap) {
        GLES20.glUniform1i(isDrawingShadowLocation, GLES20.GL_TRUE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMap);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        GLES20.glUniform1i(isDrawingShadowLocation, GLES20.GL_FALSE);
    }

    @Override
    public void endDraw() {
        GLES20.glDisableVertexAttribArray(vertexAttributeLocation);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(projectionMatrixLocation, 1, false, projectionMatrix, 0);
    }

    public void updateLightSpaceMatrix(float[] lightSpaceMatrix) {
        GLES20.glUniformMatrix4fv(lightSpaceMatrixLocation, 1, false, lightSpaceMatrix, 0);
    }
}

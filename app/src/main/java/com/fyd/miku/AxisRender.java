package com.fyd.miku;

import android.content.Context;
import android.opengl.GLES20;

import com.fyd.miku.helper.ResourceHelper;
import com.fyd.miku.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class AxisRender {
    float[] vertices = {
            0f, 0f, 0f,
             20f,  0, 0,
            0, 0, 0,
            0, 20, 0,
            0, 0, 0,
            0, 0, 20,
    };

    private int program;
    private int vertexAttributeLocation;
    private int colorAttributeLocation;
    private int viewMatrixLocation;
    private int projectionMatrixLocation;

    FloatBuffer verticesBuffer;

    Context context;

    AxisRender(Context context) {
        this.context = context;
    }

    void onSurfaceCreate() {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.axis_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.axis_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        vertexAttributeLocation = GLES20.glGetAttribLocation(program, "aVertex");
        colorAttributeLocation = GLES20.glGetAttribLocation(program, "aColor");
        viewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");
        projectionMatrixLocation = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);
    }

    void onSurfaceChanged(int width, int height) {

    }

    public void beginDraw() {
        GLES20.glUseProgram(program);
    }

    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(projectionMatrixLocation, 1, false, projectionMatrix, 0);
    }

    public void draw() {
        GLES20.glVertexAttribPointer(vertexAttributeLocation, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        GLES20.glEnableVertexAttribArray(vertexAttributeLocation);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
    }

    public void endDraw() {

    }
}

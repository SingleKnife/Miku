package com.fyd.miku.model.render;

import android.content.Context;
import android.opengl.GLES20;

import com.fyd.miku.R;
import com.fyd.miku.helper.ResourceHelper;
import com.fyd.miku.helper.ShaderHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class AxisRender implements Render {
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
    private int viewMatrixLocation;
    private int projectionMatrixLocation;

    FloatBuffer verticesBuffer;

    Context context;

    public AxisRender(Context context) {
        this.context = context;
    }

    public void createOnGlThread() {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.axis_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.axis_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        vertexAttributeLocation = GLES20.glGetAttribLocation(program, "aVertex");
        viewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");
        projectionMatrixLocation = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);
    }

    @Override
    public void beginDraw() {
        GLES20.glUseProgram(program);
    }

    @Override
    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(projectionMatrixLocation, 1, false, projectionMatrix, 0);
    }

    @Override
    public void draw() {
        GLES20.glVertexAttribPointer(vertexAttributeLocation, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);
        GLES20.glEnableVertexAttribArray(vertexAttributeLocation);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);

        endDraw();
    }

    private void endDraw() {
        GLES20.glDisableVertexAttribArray(vertexAttributeLocation);
    }

    @Override
    public void destroy() {
        GLES20.glDeleteProgram(program);
    }
}

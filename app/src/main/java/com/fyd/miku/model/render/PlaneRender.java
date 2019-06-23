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
    float planVertices[] = {
            // Positions          // Normals         // Texture Coords
            25.0f, -0.5f, 25.0f, 0.0f, 1.0f, 0.0f, 25.0f, 0.0f,
            -25.0f, -0.5f, -25.0f, 0.0f, 1.0f, 0.0f, 0.0f, 25.0f,
            -25.0f, -0.5f, 25.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,

            25.0f, -0.5f, 25.0f, 0.0f, 1.0f, 0.0f, 25.0f, 0.0f,
            25.0f, -0.5f, -25.0f, 0.0f, 1.0f, 0.0f, 25.0f, 25.0f,
            - 25.0f, -0.5f, -25.0f, 0.0f, 1.0f, 0.0f, 0.0f, 25.0f
    };
    private FloatBuffer verticesBuffer;
    private Context context;

    private int program;
    private int vertexAttributeLocation;
    private int viewMatrixLocation;
    private int projectionMatrixLocation;

    public void createOnGLThread() {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.plan_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.plan_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        vertexAttributeLocation = GLES20.glGetAttribLocation(program, "aVertex");
        viewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");
        projectionMatrixLocation = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
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
    }

    @Override
    public void draw() {
        GLES20.glVertexAttribPointer(vertexAttributeLocation, 3, GLES20.GL_FLOAT, false, 8 * 4, verticesBuffer);
        GLES20.glEnableVertexAttribArray(vertexAttributeLocation);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        endDraw();
    }

    private void endDraw() {
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
}

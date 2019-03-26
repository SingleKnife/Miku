package com.fyd.miku.model.render;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.fyd.miku.helper.ResourceHelper;
import com.fyd.miku.helper.ShaderHelper;
import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.R;

import java.nio.ByteBuffer;

public class MikuRenderProgram {
    private int aPositionLocation;
    private int aUVLocation;
    private int uProjectionMatrixLocaiton;
    private int uViewMatrixLocation;
    private int program;

    public MikuRenderProgram(Context context) {
        String frag = ResourceHelper.getRawResourceString(context, R.raw.mmd_frag);
        String vert = ResourceHelper.getRawResourceString(context, R.raw.mmd_vert);
        program = ShaderHelper.buildProgram(vert, frag);
        aPositionLocation = GLES20.glGetAttribLocation(program, "aPosition");
        aUVLocation = GLES20.glGetAttribLocation(program, "aUV");
        uProjectionMatrixLocaiton = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        uViewMatrixLocation = GLES20.glGetUniformLocation(program, "uViewMatrix");
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }

    public void bindVertexData(AllVertex allVertex) {
//        GLES20.glEnableVertexAttribArray(aUVLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, AllVertex.VERTEX_COMPONENT_SIZE,
                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getPositionBuffer());
        GLES20.glEnableVertexAttribArray(aPositionLocation);
//        GLES20.glVertexAttribPointer(aUVLocation, AllVertex.UV_COMPONENT_SIZE,
//                GLES20.GL_FLOAT, false, AllVertex.BYTE_SIZE_PER_VERTEX, allVertex.getUVBuffer());


    }

    public void updateMatrix(float[] projectionMatrix, float[] viewMatrix) {
        GLES20.glUniformMatrix4fv(uProjectionMatrixLocaiton, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uViewMatrixLocation, 1, false, viewMatrix, 0);
    }

    public void draw(ByteBuffer indicesBuffer, int indexCount) {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
    }

}

package com.fyd.miku.model.render;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.fyd.miku.model.pmd.Mesh;
import com.fyd.miku.model.pmd.MikuModel;

import java.nio.ByteBuffer;

public class MikuRender {
    private MikuModel mikuModel;
    private MikuRenderProgram renderProgram;
    private Context context;

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    public MikuRender(Context context) {
        this.context = context;
    }
    public void onSurfaceCreate() {
        renderProgram = new MikuRenderProgram(context);
    }

    public void onSurfaceChanged(int width, int height) {
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1, 1000);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 100,
                0, 0, 0, 0, 1, 0);
    }

    public void draw() {
        if(mikuModel == null) {
            return;
        }
        renderProgram.useProgram();
        renderProgram.updateMatrix(projectionMatrix, viewMatrix);
        drawModel(mikuModel);
    }

    private void drawModel(MikuModel model) {
        renderProgram.bindVertexData(model.getAllVertex());
        for(Mesh mesh : model.getMeshes()) {
            drawMesh(model, mesh);
        }
    }

    private void drawMesh(MikuModel model, Mesh mesh) {
        ByteBuffer indexBuffer = model.getAllVertex().getIndices(mesh.getIndexOffset());
        renderProgram.draw(indexBuffer, mesh.getIndexCount());
    }

    public void setMikuModel(MikuModel model) {
        this.mikuModel = model;
    }
}

package com.fyd.miku.model.render;

public interface Render {
    void beginDraw();
    void draw();
    void destroy();
    void updateMatrix(float[] projectionMatrix, float[] viewMatrix);
}

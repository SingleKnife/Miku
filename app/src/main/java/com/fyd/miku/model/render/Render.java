package com.fyd.miku.model.render;

public interface Render {
    void beginDraw();
    void draw();
    void endDraw();
    void destroy();
    void updateMatrix(float[] projectionMatrix, float[] viewMatrix);
}

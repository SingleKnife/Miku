//
// Created by dong on 2019/5/12.
//

#include "BulletDebugDrawer.h"
#include "log.h"
#include "gl/gl_utils.h"

#define BUFFER_OFFSET(offset) ((void *) (offset))

BulletDebugDrawer::BulletDebugDrawer() {
    auto vertexShader =
            "attribute vec3 aPosition;\n"
            "attribute vec3 aColor;\n"
            "uniform mat4 VPMatrix;\n"
            "varying vec3 color;\n"
            "void main() {\n"
            "  gl_Position = VPMatrix * vec4(aPosition, 1.0);\n"
            "  gl_PointSize = 5.0;"
            "  color = aColor;\n"
            "}\n";

    auto fragmentShader =
            "precision mediump float;\n"
            "varying vec3 color;\n"
            "void main() {\n"
            "  gl_FragColor = vec4(1.0, 0, 0, 1.0);\n"
            "}\n";
    mProgram = createProgram(vertexShader, fragmentShader);
    mVPMatrixHandler = glGetUniformLocation(mProgram, "VPMatrix");
    mAColorHandler = glGetAttribLocation(mProgram, "aColor");
    mAPositionHandler = glGetAttribLocation(mProgram, "aPosition");
    glGenBuffers(1, &mVertexBufferHandler);
}

void BulletDebugDrawer::drawLine(const btVector3 &from, const btVector3 &to, const btVector3 &color) {
//    LOGD("drawLine from %f, %f, %f, , to %f, %f, %f", from.getX(), from.getY(), from.getZ(), to.getX(),to.getY(), to.getZ());
//    LOGD("drawLine color %f, %f, %f", color.getX(), color.getY(), color.getZ());
    mLineVertices.push_back(btVector3(from));
    mLineVertices.push_back(btVector3(to));

    mLineColors.push_back(btVector3(color));
    mLineColors.push_back(btVector3(color));
}



void BulletDebugDrawer::drawContactPoint(const btVector3 &PointOnB, const btVector3 &normalOnB,
                                       btScalar distance, int lifeTime, const btVector3 &color) {

}

void BulletDebugDrawer::draw3dText(const btVector3 &location, const char *textString) {

}

void BulletDebugDrawer::setDebugMode(int debugMode) {
    mDebugMode = debugMode;
}

int BulletDebugDrawer::getDebugMode() const {
    return DBG_DrawWireframe | DBG_DrawConstraintLimits | DBG_DrawFrames;
}

void BulletDebugDrawer::reportErrorWarning(const char *warningString) {

}

void BulletDebugDrawer::render() {
//    LOGD("render: %d", mLineVertices.size());
    float vertices[mLineVertices.size()][6];
    for(int i = 0; i < mLineVertices.size(); ++i) {
        vertices[i][0] = mLineVertices[i].getX();
        vertices[i][1] = mLineVertices[i].getY();
        vertices[i][2] = mLineVertices[i].getZ();

        vertices[i][3] = mLineColors[i].getX();
        vertices[i][4] = mLineColors[i].getY();
        vertices[i][5] = mLineColors[i].getZ();
    }

    glUseProgram(mProgram);
    glUniformMatrix4fv(mVPMatrixHandler, 1, GL_FALSE, mVPMatrix);

    glBindBuffer(GL_ARRAY_BUFFER, mVertexBufferHandler);
    glBufferData(GL_ARRAY_BUFFER, mLineVertices.size() * sizeof(float) * 6, vertices, GL_DYNAMIC_DRAW);

    glVertexAttribPointer(mAPositionHandler, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(float), BUFFER_OFFSET(0));
    glVertexAttribPointer(mAColorHandler, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(float), BUFFER_OFFSET(3 * sizeof(float)));

    glEnableVertexAttribArray(mAPositionHandler);
    glEnableVertexAttribArray(mAColorHandler);

//    glDrawArrays(GL_POINTS, 0, mLineVertices.size());
    glDrawArrays(GL_LINES, 0, mLineVertices.size());

    glDisableVertexAttribArray(mAPositionHandler);
    glDisableVertexAttribArray(mAColorHandler);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    mLineVertices.clear();
    mLineColors.clear();
}

void BulletDebugDrawer::updateProjectionMatrix(float *matrix) {
    for(int i = 0; i < 16;  ++i) {
        mVPMatrix[i] = matrix[i];
    }
}

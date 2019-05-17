//
// Created by dong on 2019/5/12.
//

#ifndef BULLETDEMO_BULLETDEBUGDRAW_H
#define BULLETDEMO_BULLETDEBUGDRAW_H


#include <GLES2/gl2.h>
#include "bullet/src/LinearMath/btIDebugDraw.h"
#include <vector>
#include <array>

class BulletDebugDrawer : public btIDebugDraw {
public:
    BulletDebugDrawer();

    virtual void drawLine(const btVector3 &from, const btVector3 &to, const btVector3 &color);

    virtual void
    drawContactPoint(const btVector3 &PointOnB, const btVector3 &normalOnB, btScalar distance,
                     int lifeTime, const btVector3 &color);

    virtual void draw3dText(const btVector3 &location, const char *textString);

    virtual void setDebugMode(int debugMode);

    virtual int getDebugMode() const;

    virtual void reportErrorWarning(const char *warningString);

    void updateProjectionMatrix(float *matrix);

    void render();

private:
    int mDebugMode;

    GLuint mVertexBufferHandler;
    GLuint mProgram;
    GLint mVPMatrixHandler;
    GLint mAColorHandler;
    GLint mAPositionHandler;

    float mVPMatrix[16];

    std::vector<btVector3> mLineVertices;
    std::vector<btVector3> mLineColors;
};


#endif //BULLETDEMO_BULLETDEBUGDRAW_H

//
// Created by dong on 2018/11/5.
//

#ifndef MODEL_LOADER_GL_UTILS_H
#define MODEL_LOADER_GL_UTILS_H
#include <GLES2/gl2.h>

GLuint loadShader(GLenum shaderType, const char* pSource);

GLuint createProgram(const char* pVertexSource, const char* pFragmentSource);

void checkGlError(const char* op);

void printGLString(const char *name, GLenum s);

#endif //MODEL_LOADER_GL_UTILS_H

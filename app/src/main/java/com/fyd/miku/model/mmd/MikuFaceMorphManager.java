package com.fyd.miku.model.mmd;

import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.FaceMorph;

import java.util.List;

public class MikuFaceMorphManager {
    private List<FaceMorph> morphs;
    private AllVertex allVertex;

    MikuFaceMorphManager(List<FaceMorph> morphs, AllVertex allVertex) {
        this.morphs = morphs;
        this.allVertex = allVertex;
        //更新顶点初始位置
        for(FaceMorph morph : morphs) {
            for(FaceMorph.Vertex vertex : morph.getRelatedVertices()) {
                vertex.basePos = allVertex.getPosValue(vertex.vertexIndex);
            }
        }
    }

    int getMorphNum() {
        return morphs.size();
    }

    void setMorphMotion(int morphIndex, float weight) {
        FaceMorph morph = morphs.get(morphIndex);
        for(FaceMorph.Vertex vertex : morph.getRelatedVertices()) {
            float x = vertex.basePos[0] + vertex.maxOffset[0] * weight * 5;
            float y = vertex.basePos[1] + vertex.maxOffset[1] * weight * 5;
            float z = vertex.basePos[2] + vertex.maxOffset[2] * weight * 5;
            allVertex.updatePosValue(vertex.vertexIndex, x, y, z);
        }
    }

    int findMorph(String morphName) {
        for(int i = 0; i < morphs.size(); ++i) {
            FaceMorph morph = morphs.get(i);
            if(morph.getMorphName().equals(morphName)) {
                return i;
            }
        }
        return -1;
    }
}

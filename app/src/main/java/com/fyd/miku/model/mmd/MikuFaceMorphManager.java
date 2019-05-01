package com.fyd.miku.model.mmd;

import com.fyd.miku.model.pmd.AllVertex;
import com.fyd.miku.model.pmd.FaceMorph;

import java.util.ArrayList;
import java.util.List;

public class MikuFaceMorphManager {
    private List<FaceMorph> morphs;
    private AllVertex allVertex;
    private List<FaceMorph.Vertex> baseVertices;
    private List<FaceMorph.Vertex> updatedVertices;

    MikuFaceMorphManager(List<FaceMorph> morphs, AllVertex allVertex) {
        this.morphs = morphs;
        this.allVertex = allVertex;
        //更新顶点初始位置
        FaceMorph baseMorph = null;
        for(FaceMorph morph : morphs) {
            if(morph.getMorphType() == 0) {
                baseMorph = morph;
                break;
            }
        }
        morphs.remove(baseMorph);
        baseVertices = baseMorph.getRelatedVertices();
        updatedVertices = new ArrayList<>(baseVertices.size());

        for(FaceMorph.Vertex vertex : baseVertices) {
            FaceMorph.Vertex updatedVetex = new FaceMorph.Vertex();
            updatedVetex.vertexIndex = vertex.vertexIndex;
            System.arraycopy(vertex.posOffset, 0, updatedVetex.posOffset, 0, 3);
            updatedVertices.add(updatedVetex);
        }
    }

    int getMorphNum() {
        return morphs.size();
    }

    void resetMorphPos() {
        for(int i = 0; i < baseVertices.size(); ++i) {
            FaceMorph.Vertex basePos = baseVertices.get(i);
            FaceMorph.Vertex updatedPos = updatedVertices.get(i);
            System.arraycopy(basePos.posOffset, 0, updatedPos.posOffset, 0, 3);
        }
    }

    void setMorphMotion(int morphIndex, float weight) {
        FaceMorph morph = morphs.get(morphIndex);
        for(FaceMorph.Vertex vertex : morph.getRelatedVertices()) {
            float[] vertexPos = baseVertices.get(vertex.vertexIndex).posOffset;
            float[] updatePos = updatedVertices.get(vertex.vertexIndex).posOffset;
            updatePos[0] = vertexPos[0] + vertex.posOffset[0] * weight;
            updatePos[1] = vertexPos[1] + vertex.posOffset[1] * weight;
            updatePos[2] = vertexPos[2] + vertex.posOffset[2] * weight;
        }
    }

    void updatePos() {
        for (FaceMorph.Vertex updatePos : updatedVertices) {
            allVertex.updatePosValue(updatePos.vertexIndex, updatePos.posOffset);
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

package hex.hglm;

import water.fvec.Frame;

import java.util.List;

public class HGLMUtils {
  public static void readRandomEffectInitFrame(Frame randomEffects, double[][] ubeta, List<String> randomCoeffNames) {
    if (ubeta == null)
      return;
    int numRow = (int) randomEffects.numRows();
    int numCol = randomEffects.numCols();
    for (int colInd = 0; colInd < numCol; colInd++)
      for(int rowInd = 0; rowInd < numRow; rowInd++)
        ubeta[rowInd][colInd] = randomEffects.vec(colInd).at(rowInd);
  }
}

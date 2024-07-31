package water.rapids.ast.prims.mungers;

import water.Key;
import water.MRTask;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.rapids.Env;
import water.rapids.ast.AstPrimitive;
import water.rapids.ast.AstRoot;
import water.rapids.vals.ValFrame;

/**
 * For a numeric column, NAs will be replaced with mean.
 * For a categorical column, NAs will be replaced with mode.
 * Any other column type will not be touched.
 */
public class AstNaReplaceMeanMode extends AstPrimitive {
    @Override
    public String[] args() { return new String[]{"ary"};}
    
    @Override
    public String str() { return "na.replace.mean.mode";}
    
    @Override
    public int nargs() { return 1;}
    
    @Override
    public ValFrame apply(Env env, Env.StackHelp stk, AstRoot asts[]) {
        Frame fr = stk.track(asts[1].exec(env)).getFrame().deepCopy(Key.make().toString()); // don't want to change original here.
        int numCol = fr.numCols();
        double[] meanMode = new double[numCol];
        boolean[] enumCol = new boolean[numCol];
        
        for (int colIndex = 0; colIndex < numCol; colIndex++) {
            if (fr.vec(colIndex).isCategorical()) {
                meanMode[colIndex] = fr.vec(colIndex).mode();
                enumCol[colIndex] = true;
            } else if (fr.vec(colIndex).isNumeric()) {
                meanMode[colIndex] = fr.vec(colIndex).mean();
                enumCol[colIndex] = false;
            } else {
                meanMode[colIndex] = Double.NaN;
                enumCol[colIndex] = false;
            }
        }
        new ReplaceFrameNAs(meanMode, enumCol).doAll(fr);
        return new ValFrame(fr);
    }
    
    private static class ReplaceFrameNAs extends MRTask<ReplaceFrameNAs> {
        private final double[] _meanMode;
        private final boolean[] _enumCol;
        
        ReplaceFrameNAs(double[] meanMode, boolean[] enumCol) {
            _meanMode = meanMode;
            _enumCol = enumCol;
        }
        
        @Override
        public void map(Chunk[] cs) {
            int csWidth = cs.length;
            int csLen = cs[0]._len;
            int replaceMode = 0;
            for (int colInd = 0; colInd < csWidth; colInd++) {
                if (!Double.isNaN(_meanMode[colInd])) { // only work on enum/numeical columns here
                    if (_enumCol[colInd])
                        replaceMode = (int ) _meanMode[colInd];
                    for (int rowInd = 0; rowInd < csLen; rowInd++) {
                        if (Double.isNaN(cs[colInd].atd(rowInd))) {
                            if (_enumCol[colInd])
                                cs[colInd].set(rowInd, replaceMode);
                            else
                                cs[colInd].set(rowInd, _meanMode[colInd]);
                        }
                    }
                }
            }
        }
    }
}

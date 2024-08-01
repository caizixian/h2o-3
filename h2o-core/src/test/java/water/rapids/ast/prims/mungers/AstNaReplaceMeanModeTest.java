package water.rapids.ast.prims.mungers;

import hex.CreateFrame;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.rapids.Rapids;
import water.rapids.Val;

public class AstNaReplaceMeanModeTest extends TestUtil {

  @BeforeClass
  static public void setup() { stall_till_cloudsize(1); }
  
  @Test
  public void testNaReplaceModeMean() {
    Scope.enter();
    try {
      CreateFrame cf = new CreateFrame();
      cf.rows = 100;
      cf.cols = 10;
      cf.categorical_fraction = 0.2;
      cf.integer_fraction = 0.2;
      cf.time_fraction = 0.0;
      cf.string_fraction = 0.2;
      cf.binary_fraction = 0.0;
      cf.missing_fraction = 0.01;
      cf.seed = 12345;
      Frame testFrame = cf.execImpl().get();
      Scope.track(testFrame);
      
      //String x = String.format("(na.replace.mean.mode %s)", testFrame._key);
      String x = String.format("(na.replace.mean.mode %s)", "abc");
      Val val = Rapids.exec(x);
      Frame naReplaced = Scope.track(val.getFrame());
      
      // check when there is na is the original test frame, the new frame should contain the mean for numerical, mode
      // for categorical and no change for all other column types.
      for (int colIndex=0; colIndex < cf.cols; colIndex++) {
        if (testFrame.vec(colIndex).isCategorical() || testFrame.vec(colIndex).isNumeric()) {
          for (int rowIndex=0; rowIndex < cf.rows; rowIndex++) {
            if (Double.isNaN(testFrame.vec(colIndex).at(rowIndex))) {
              if (testFrame.vec(colIndex).isCategorical()) {
                Assert.assertTrue("value "+naReplaced.vec(colIndex).at(rowIndex)+" at row "+rowIndex+
                                " and column "+colIndex+" should be "+testFrame.vec(colIndex).mode()+ " but is not.",
                        testFrame.vec(colIndex).mode() == naReplaced.vec(colIndex).at(rowIndex));
              } else {  // numerical column
                Assert.assertTrue("value "+naReplaced.vec(colIndex).at(rowIndex)+" at row "+rowIndex+
                                " and column "+colIndex+" should be "+testFrame.vec(colIndex).mean()+ " but is not.",
                        Math.abs(testFrame.vec(colIndex).mean() - naReplaced.vec(colIndex).at(rowIndex)) < 1e-6);
              }
            } else {
              Assert.assertTrue("values "+testFrame.vec(colIndex).at(rowIndex)+" and " + 
                      naReplaced.vec(colIndex).at(rowIndex)+ " at row "+rowIndex+" and column "+colIndex+" should be" +
                      " equal but is not", 
                      Math.abs(testFrame.vec(colIndex).at(rowIndex) - naReplaced.vec(colIndex).at(rowIndex)) < 1e-6);
            }
          }
    
        } else {
          TestUtil.assertBitIdentical(new Frame(testFrame.vec(colIndex)), new Frame(naReplaced.vec(colIndex)));
        }
      }
      
    } finally {
      Scope.exit();
    }
  }
}

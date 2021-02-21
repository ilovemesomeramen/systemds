package org.apache.sysds.test.benchmark;

import org.apache.sysds.common.Types;
import org.apache.sysds.runtime.matrix.data.FrameBlock;
import org.apache.sysds.runtime.transform.encode.Encoder;
import org.apache.sysds.runtime.transform.encode.EncoderFactory;
import org.apache.sysds.runtime.util.DataConverter;
import org.apache.sysds.test.TestUtils;
import scala.collection.mutable.StringBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.apache.sysds.test.TestUtils.generateTestMatrix;

public class BenchmarkUtils {


    public static FrameBlock getInputFrame(int rows, int cols, int maxUniqueValues, double sparsity, long seed){
        double[][] A = TestUtils.round(generateTestMatrix(rows, cols, 1, maxUniqueValues, sparsity, seed));
        FrameBlock fb = DataConverter.convertToFrameBlock(DataConverter.convertToMatrixBlock(A), Types.ValueType.STRING);
        zeroToNull(fb);
        return fb;
    }

    public static Encoder getBenchmarkEncoder(String specName, String[] colnames) throws IOException {
        StringBuilder spec = new StringBuilder();
        Files.readAllLines(Paths.get("benchmark_spec/" + specName)).forEach(s -> spec.append(s).append("\n"));
        return EncoderFactory.createEncoder(spec.toString(), colnames, colnames.length, null);

    }

    public static void zeroToNull(FrameBlock frameBlock) {
        for(int i = 0; i < frameBlock.getNumRows(); i++) {
            for(int j = 0; j < frameBlock.getNumColumns(); j++){
                if((frameBlock.get(i, j)).equals("0.0")){
                    frameBlock.set(i,j, null);
                }
            }
        }
    }

}

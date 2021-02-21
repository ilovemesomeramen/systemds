package org.apache.sysds.test.benchmark;

import org.apache.sysds.common.Types;
import org.apache.sysds.runtime.matrix.data.FrameBlock;
import org.apache.sysds.runtime.matrix.data.MatrixBlock;
import org.apache.sysds.runtime.transform.encode.Encoder;
import org.apache.sysds.runtime.transform.encode.EncoderFactory;
import org.apache.sysds.runtime.util.DataConverter;
import org.apache.sysds.test.TestUtils;
import org.apache.wink.json4j.JSONException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import scala.collection.mutable.StringBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.apache.sysds.test.TestUtils.generateTestMatrix;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TransformEncode2ColRandomMissingDataBenchmark {
    private static final int cols = 2;

    @Param({"100000"})
    private int rows;

    @Param({"1000"})
    private int maxUniqueValues;

    @Param({"0.5"})
    private double sparsity;

    @Param({"spec_i1m0.json"})
    private String specName;

    private FrameBlock TEST_DATA_IN;
    private MatrixBlock TEST_DATA_OUT;
    private Encoder TEST_ENCODER;

    @Setup(Level.Trial)
    public void setup() throws JSONException, IOException {
        TEST_DATA_IN = BenchmarkUtils.getInputFrame(rows, cols, maxUniqueValues, sparsity, 7);
        TEST_DATA_OUT = new MatrixBlock(TEST_DATA_IN.getNumRows(), TEST_DATA_IN.getNumColumns(), false);
        TEST_ENCODER = BenchmarkUtils.getBenchmarkEncoder(specName, TEST_DATA_IN.getColumnNames());
    }

    @Benchmark
    public void encode(Blackhole bh){
        TEST_ENCODER.encode(TEST_DATA_IN,TEST_DATA_OUT);
    }

}

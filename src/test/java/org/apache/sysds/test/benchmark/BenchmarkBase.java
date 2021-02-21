package org.apache.sysds.test.benchmark;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.HotspotMemoryProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class BenchmarkBase {

    public static void main(String[] args) throws RunnerException, IOException {
        int warmup = 0;
        int iterations = 5;
        int forks = 0;
        int threads = 1;
        String testClassRegExPattern = "TransformEncode2ColRandomMissingDataBenchmark";
        String resultFilePrefix = "jmh-benchmark-result-";

        ResultFormatType resultsFileOutputType = ResultFormatType.JSON;

        Options opt = new OptionsBuilder()
                .include(testClassRegExPattern)
                .warmupIterations(warmup)
                .measurementIterations(iterations)
                .forks(forks)
                .threads(threads)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .resultFormat(resultsFileOutputType)
                .result(buildResultsFileName(resultFilePrefix, resultsFileOutputType))
                .shouldFailOnError(true)
                .jvmArgs("-server")
                //.addProfiler(GCProfiler.class)
                //.addProfiler(LinuxPerfNormProfiler.class)
                //.addProfiler(HotspotMemoryProfiler.class)
                .build();

        new Runner(opt).run();
    }

    private static String buildResultsFileName(String resultFilePrefix, ResultFormatType resultType) {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm-dd-yyyy-hh-mm-ss");

        String suffix;
        switch (resultType) {
            case CSV:
                suffix = ".csv";
                break;
            case SCSV:
                // Semi-colon separated values
                suffix = ".scsv";
                break;
            case LATEX:
                suffix = ".tex";
                break;
            case JSON:
            default:
                suffix = ".json";
                break;

        }

        return String.format("target/benchmark/%s%s%s", resultFilePrefix, date.format(formatter), suffix);
    }

}
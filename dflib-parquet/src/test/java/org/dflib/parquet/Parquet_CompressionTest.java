package org.dflib.parquet;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Parquet_CompressionTest extends BaseParquetTest {

    static final DataFrame df = DataFrame.foldByRow("a", "b", "c")
            .of(
                    // create data that can be reasonably compressed
                    4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd",
                    4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd",
                    4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

    static long UNCOMPRESSED_SIZE;

    @BeforeAll
    static void measureUncompressed() {
        File file = new File(outPath("uncompressed.parquet"));
        Parquet.saver().save(df, file);

        UNCOMPRESSED_SIZE = file.length();
        assertTrue(UNCOMPRESSED_SIZE > 0);
    }

    @Test
    public void gzip() {
        File file = new File(outPath("gzip.parquet"));
        Parquet.saver()
                .compressionCodec(CompressionCodec.GZIP)
                .save(df, file);

        assertTrue(file.length() < UNCOMPRESSED_SIZE, () -> "Failed to compress: " + file.length() + " vs " + UNCOMPRESSED_SIZE);

        DataFrame loaded = Parquet.loader().load(file);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void snappy() {
        File file = new File(outPath("snappy.parquet"));
        Parquet.saver()
                .compressionCodec(CompressionCodec.SNAPPY)
                .save(df, file);

        assertTrue(file.length() < UNCOMPRESSED_SIZE, () -> "Failed to compress: " + file.length() + " vs " + UNCOMPRESSED_SIZE);

        DataFrame loaded = Parquet.loader().load(file);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void lz4_raw() {
        File file = new File(outPath("lz4_raw.parquet"));
        Parquet.saver()
                .compressionCodec(CompressionCodec.LZ4_RAW)
                .save(df, file);

        assertTrue(file.length() < UNCOMPRESSED_SIZE, () -> "Failed to compress: " + file.length() + " vs " + UNCOMPRESSED_SIZE);

        DataFrame loaded = Parquet.loader().load(file);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test
    public void zstd() {
        File file = new File(outPath("zstd.parquet"));
        Parquet.saver()
                .compressionCodec(CompressionCodec.ZSTD)
                .save(df, file);

        assertTrue(file.length() < UNCOMPRESSED_SIZE, () -> "Failed to compress: " + file.length() + " vs " + UNCOMPRESSED_SIZE);

        DataFrame loaded = Parquet.loader().load(file);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(3)
                .expectRow(0, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(1, 4.0f, true, "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd")
                .expectRow(2, 4.0f, true, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }
}

package org.dflib.parquet;

import org.dflib.DataFrame;

import java.io.File;
import java.nio.file.Path;

/**
 * @since 1.0.0-M23
 */
public class Parquet {

    public static DataFrame load(File file) {
        return loader().load(file);
    }

    public static DataFrame load(Path filePath) {
        return loader().load(filePath);
    }

    public static DataFrame load(String filePath) {
        return loader().load(filePath);
    }

    public static void save(DataFrame df, Path filePath) {
        saver().save(df, filePath);
    }

    public static void save(DataFrame df, String filePath) {
        saver().save(df, filePath);
    }

    public static void save(DataFrame df, File file) {
        saver().save(df, file);
    }

    public static ParquetSaver saver() {
        return new ParquetSaver();
    }

    public static ParquetLoader loader() {
        return new ParquetLoader();
    }

}

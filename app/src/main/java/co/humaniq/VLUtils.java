package co.humaniq;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class VLUtils {
    public static final String PATH_TO_EXTRACTED_VL_DATA = "/vl/data";
    private static final String PATH_TO_VL_DATA = "/vl";
    private static final String VL_DATA_PACK = "vl/data.zip";

    /**
     * Распаковывает данные для VL Face Engine из ассетов приложения
     *
     * @param context контекст (обычно Application или Activity)
     * @return true в случае успешной распаковки, false в случае если не удалось распаковать архив
     */
    static boolean unpackZipFromAssets(Context context) {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(VL_DATA_PACK);
            return unzip(context, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Распаковывает файлы из zip архива
     * @param context контекст (обычно Application или Activity)
     * @param inputStream поток для распаковки
     * @return true в случае успеха, false в случае если не удалось распаковать архив
     */
    @SuppressLint("NewApi")
    private static boolean unzip(Context context, InputStream inputStream) {
        File dir = new File(context.getFilesDir() + PATH_TO_VL_DATA);
        if (!(dir.mkdirs() || dir.isDirectory())) {
            throw new RuntimeException("mkdir failed: unable to create dir:" + PATH_TO_VL_DATA);
        }

        final byte[] buffer = new byte[1024 * 1024];
        final String path = context.getFilesDir() + PATH_TO_VL_DATA + "/";

        try (final ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                final String filename = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    final File fmd = new File(path + filename);

                    if (!(fmd.mkdirs() || fmd.isDirectory())) {
                        throw new RuntimeException(String.format("mkdir failed: unable to create dir:%s %s", path, filename));
                    }
                    continue;
                }

                try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path + filename))) {
                    int count;
                    while ((count = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, count);
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

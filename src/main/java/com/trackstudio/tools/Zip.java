package com.trackstudio.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;

import net.jcip.annotations.Immutable;
/**
 * Класс для компрессии в формат Zip
 */
@Immutable
public class Zip implements Serializable {

    /**
     * Производит компрессию
     *
     * @param name    Название архива
     * @param content Сжимаемый контент
     * @return массив файтов
     * @throws GranException при необходимости
     */
    public static byte[] compress(String name, String content) throws GranException {
        return compress(name, content, Config.getEncoding());
    }

    /**
     * Производит компрессию
     *
     * @param name     Название архива
     * @param content  Сжимаемый контент
     * @param encoding Кодировка
     * @return массив файтов
     * @throws GranException при необходимости
     */
    public static byte[] compress(String name, String content, String encoding) throws GranException {
        File file = null;
        try {
            file = new File(Config.getInstance().getUploadDir() + "/tmpfile" + System.currentTimeMillis());
            FileOutputStream fos = new FileOutputStream(file);
            ZipOutputStream targetStream = new ZipOutputStream(fos);
            targetStream.setMethod(ZipOutputStream.DEFLATED);
            targetStream.setLevel(5);
            // sourceStream = new StringBufferInputStream(content);
            ByteArrayInputStream sourceStream;
            if (encoding != null && encoding.length() != 0)
                sourceStream = new ByteArrayInputStream(content.getBytes(encoding));
            else
                sourceStream = new ByteArrayInputStream(content.getBytes());
            // sourceStream = new ByteArrayInputStream(content);
            ZipEntry theEntry = new ZipEntry(name);
            targetStream.putNextEntry(theEntry);
            int DATA_BLOCK_SIZE = 16384;
            byte[] data = new byte[DATA_BLOCK_SIZE];

            int byteCount;
            while ((byteCount = sourceStream.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
                targetStream.write(data, 0, byteCount);
            }
            targetStream.flush();
            targetStream.closeEntry();
            sourceStream.close();
            targetStream.close();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            data = new byte[(int) file.length()];
            in.read(data);
            in.close();
            return data;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (file.isFile())
                file.delete();
        }
    }

}
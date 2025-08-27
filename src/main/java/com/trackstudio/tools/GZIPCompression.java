package com.trackstudio.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompression {
	public static byte[] compress(final String str)  {
		try {
			if ((str == null) || (str.length() == 0)) {
				return null;
			}
			ByteArrayOutputStream obj = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(str.getBytes("UTF-8"));
			gzip.flush();
			gzip.close();
			return obj.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decompress(final byte[] compressed)  {
		try {
			if ((compressed == null) || (compressed.length == 0)) {
				return "";
			}
			if (isCompressed(compressed)) {
				final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
				java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();

				int res = 0;
				byte buf[] = new byte[16384];
				while (res >= 0) {
					res = gis.read(buf, 0, buf.length);
					if (res > 0) {
						byteout.write(buf, 0, res);
					}
				}
				byteout.close();
				return byteout.toString("UTF-8");
			} else {
				return "Internal TrackStudio Error";
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isCompressed(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}
}
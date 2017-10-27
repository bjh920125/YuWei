package com.bap.yuwei.util;

import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Base64Util {
	/**
	 * 文件转base64
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String file2String(String filePath) throws IOException {
		File file = new File(filePath);
		InputStream inStream = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		String s = new String(Base64.encode(data, Base64.DEFAULT));
		outStream.close();
		inStream.close();
		return s;
	}
	
	/**
	 * base64转文件
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static File String2file(String base64Code, String savePath)throws Exception {
		byte[] data = Base64.decode(base64Code, Base64.DEFAULT);
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = new File(savePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		fos = new FileOutputStream(file);
		bos = new BufferedOutputStream(fos);
		bos.write(data);
		if (bos != null) {
			bos.close();
		}
		if (fos != null) {
			fos.close();
		}
		return file;
	}
	
}

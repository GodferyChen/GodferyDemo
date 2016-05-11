package a.b.c.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 简单的ZIP压缩工具类
 */
public class ZipManager {

	/**
	 * 将一个文件压缩进ZIP文件中
	 */
	public static boolean zipFile(File file, File zip) {
		boolean isOk = true;
		ZipOutputStream zipOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			if (!zip.exists()) {
				File dir = new File(zip.getParent());
				if (!dir.exists()) {
					isOk = dir.mkdirs();
					if (!isOk) return false;
				}
				if (!zip.exists()) {
					isOk = zip.createNewFile();
					if (!isOk) return false;
				}
			}
			zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)));
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file), 4096);
			ZipEntry entry = new ZipEntry(file.getName());
			zipOutputStream.putNextEntry(entry);
			byte[] buffer = new byte[1024 * 4];
			int len = 0;
			while (-1 != (len = bufferedInputStream.read(buffer))) {
				zipOutputStream.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isOk = false;
		} finally {
			try {
				if (zipOutputStream != null) zipOutputStream.close();
				if (bufferedInputStream != null) bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isOk;
	}

	/**
	 * 解压缩功能.
	 * 将zipFile文件解压到folderPath目录下.
	 */
	public static boolean unZipFile(File zip, File file) {
		boolean isOk = true;
		ZipFile zipFile = null;
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				isOk = dir.mkdirs();
				if (!isOk) return false;
			}
			if (!file.exists()) {
				isOk = file.createNewFile();
				if (!isOk) return false;
			}
			zipFile = new ZipFile(zip);
			ZipEntry entry = zipFile.getEntry(file.getName());
			bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(entry));
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buffer = new byte[1024 * 4];
			int len = 0;
			while (-1 != (len = bufferedInputStream.read(buffer))) {
				bufferedOutputStream.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isOk = false;
		} finally {
			try {
				if (bufferedOutputStream != null) bufferedOutputStream.close();
				if (bufferedInputStream != null) bufferedInputStream.close();
				if (zipFile != null) zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isOk;
	}
}

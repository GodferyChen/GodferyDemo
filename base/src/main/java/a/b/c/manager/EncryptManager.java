package a.b.c.manager;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * 简单的字符串和文件加密工具类
 */
@SuppressLint("DefaultLocale")
public class EncryptManager {
	
	/**
	 * 获取字符串的16位小写MD5
	 * 
	 * @param text
	 * 			操作的字符串
	 * @return
	 * 			字符串的16位小写MD5
	 */
	public static String MD5StrLower16(String text) {
		return MD5Str(text).toLowerCase().substring(8, 24);
	}

	/**
	 * 获取字符串的32位小写MD5
	 * 
	 * @param text
	 * 			操作的字符串
	 * @return
	 * 			字符串的32位小写MD5
	 */
	public static String MD5StrLower32(String text) {
		return MD5Str(text).toLowerCase();
	}

	/**
	 *获取字符串的16位大写MD5
	 * 
	 * @param text
	 * 			操作的字符串
	 * @return
	 * 			字符串的16位大写MD5
	 */
	public static String MD5StrUpper16(String text) {
		return MD5Str(text).toUpperCase().substring(8, 24);
	}

	/**
	 * 获取字符串的32位大写MD5
	 * 
	 * @param text
	 * 			操作的字符串
	 * @return
	 * 			字符串的32位大写MD5
	 */
	public static String MD5StrUpper32(String text) {
		return MD5Str(text).toUpperCase();
	}

	/**
	 * 获取字符串的MD5
	 * 
	 * @param text
	 * 			操作的字符串
	 * @return
	 * 			字符串的MD5
	 */
	private static String MD5Str(String text) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String md5 = new BigInteger(1, md.digest()).toString(16);
		int offset = 32 - md5.length();
		if (offset>0) {
			for (int i=0; i < offset; i++) {
				md5 = "0" + md5;
			}
		}
		return md5;
	}
	
	/**
	 * 获取单个文件的16位小写MD5
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的16位小写MD5
	 */
	public static String MD5FileLower16(File file) {
		return MD5File(file).toLowerCase().substring(8, 24);
	}

	/**
	 * 获取单个文件的32位小写MD5
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的32位小写MD5
	 */
	public static String MD5FileLower32(File file) {
		return MD5File(file).toLowerCase();
	}

	/**
	 * 获取单个文件的16位大写MD5
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的16位大写MD5
	 */
	public static String MD5FileUpper16(File file) {
		return MD5File(file).toUpperCase().substring(8, 24);
	}

	/**
	 * 获取单个文件的32位大写MD5
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的32位大写MD5
	 */
	public static String MD5FileUpper32(File file) {
		return MD5File(file).toUpperCase();
	}
	
	/**
	 * 获取单个文件的MD5
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的MD5
	 */
	private static String MD5File(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
	  
	/**
	 * 获取单个文件的16位小写SHA1
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			16位小写SHA1
	 */
	public static String SHA1FileLower16(File file) {
		return SHA1File(file).toLowerCase().substring(8, 24);
	}

	/**
	 * 获取单个文件的32位小写SHA1
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的32位小写SHA1
	 */
	public static String SHA1FileLower32(File file) {
		return SHA1File(file).toLowerCase();
	}

	/**
	 * 获取单个文件的16位大写SHA1
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的16位大写SHA1
	 */
	public static String SHA1FileUpper16(File file) {
		return SHA1File(file).toUpperCase().substring(8, 24);
	}

	/**
	 * 获取单个文件的32位大写SHA1
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的32位大写SHA1
	 */
	public static String SHA1FileUpper32(File file) {
		return SHA1File(file).toUpperCase();
	}
	
	/**
	 * 获取单个文件的SHA1
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的SHA1
	 */
	private static String SHA1File(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("SHA1");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16).toUpperCase();
	}
	
	/**
	 * 获取单个文件的小写CRC32
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的小写CRC32
	 */
	public static String CRC32FileLower(File file) {
		return CRC32File(file).toLowerCase();
	}

	/**
	 * 获取单个文件的大写CRC32
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的大写CRC32
	 */
	public static String CRC32FileUpper(File file) {
		return CRC32File(file).toUpperCase();
	}
	
	/**
	 * 获取单个文件的CRC32
	 * 
	 * @param file
	 * 			操作的文件
	 * @return
	 * 			文件的CRC32
	 */
	@SuppressWarnings("resource")
	public static String CRC32File(File file) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			CRC32 crc32 = new CRC32();
			CheckedInputStream checkedinputstream = new CheckedInputStream(fileInputStream, crc32);
			byte[] buffer = new byte[1024*4];
			while (checkedinputstream.read(buffer) != -1) {}
			return Long.toHexString(crc32.getValue());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取目录中所有文件的16位小写MD5
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的16位小写MD5,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> MD5DirLower16(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,md5>
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(MD5DirLower16(f, listChild));
			} else if (f.isFile()) {
				md5 = MD5FileLower16(f);
				if (md5 != null) {
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}

	/**
	 * 获取目录中所有文件的32位小写MD5
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的32位小写MD5,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> MD5DirLower32(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,md5>
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(MD5DirLower32(f, listChild));
			} else if (f.isFile()) {
				md5 = MD5FileLower32(f);
				if (md5 != null) {
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}

	/**
	 * 获取目录中所有文件的16位大写MD5
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的16位大写MD5,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> MD5DirUpper16(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,md5>
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(MD5DirUpper16(f, listChild));
			} else if (f.isFile()) {
				md5 = MD5FileUpper16(f);
				if (md5 != null) {
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}

	/**
	 * 获取目录中所有文件的32位大写MD5
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的32位大写MD5,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> MD5DirUpper32(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,md5>
		Map<String, String> map = new HashMap<String, String>();
		String md5;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(MD5DirUpper32(f, listChild));
			} else if (f.isFile()) {
				md5 = MD5FileUpper32(f);
				if (md5 != null) {
					map.put(f.getPath(), md5);
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取目录中所有文件的16位小写SHA1
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的16位小写SHA1,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> SHA1DirLower16(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,SHA1>
		Map<String, String> map = new HashMap<String, String>();
		String sha1;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(SHA1DirLower16(f, listChild));
			} else if (f.isFile()) {
				sha1 = SHA1FileLower16(f);
				if (sha1 != null) {
					map.put(f.getPath(), sha1);
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取目录中所有文件的32位小写SHA1
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的32位小写SHA1,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> SHA1DirLower32(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,SHA1>
		Map<String, String> map = new HashMap<String, String>();
		String sha1;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(SHA1DirLower32(f, listChild));
			} else if (f.isFile()) {
				sha1 = SHA1FileLower32(f);
				if (sha1 != null) {
					map.put(f.getPath(), sha1);
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取目录中所有文件的16位大写SHA1
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的16位大写SHA1,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> SHA1DirUpper16(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,SHA1>
		Map<String, String> map = new HashMap<String, String>();
		String sha1;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(SHA1DirUpper16(f, listChild));
			} else if (f.isFile()) {
				sha1 = SHA1FileUpper16(f);
				if (sha1 != null) {
					map.put(f.getPath(), sha1);
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取目录中所有文件的32位大写SHA1
	 * 
	 * @param dir
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的32位大写SHA1,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> SHA1DirUpper32(File dir, boolean listChild) {
		if (!dir.isDirectory()) {
			return null;
		}
		// <filepath,SHA1>
		Map<String, String> map = new HashMap<String, String>();
		String sha1;
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(SHA1DirUpper32(f, listChild));
			} else if (f.isFile()) {
				sha1 = SHA1FileUpper32(f);
				if (sha1 != null) {
					map.put(f.getPath(), sha1);
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取目录中所有文件的小写CRC32
	 * 
	 * @param file
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的小写CRC32,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> CRC32DirLower(File file, boolean listChild) {
		if (!file.isDirectory()) {
			return null;
		}
		// <filepath,CRC32>
		Map<String, String> map = new HashMap<String, String>();
		String crc32;
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(CRC32DirLower(f, listChild));
			} else if (f.isFile()) {
				crc32 = CRC32FileLower(f);
				if (crc32 != null) {
					map.put(f.getPath(), crc32);
				}
			}
		}
		return map;
	}
	
	/**
	 * 获取目录中所有文件的大写CRC32
	 * 
	 * @param file
	 * 			操作的目录
	 * @param listChild
	 * 			true:递归自目录		false:不递归子目录
	 * @return
	 * 			目录中所有对应路径文件的大写CRC32,如果目录中无文件,则返回的时候内容为空的Map
	 */
	public static Map<String, String> CRC32DirUpper(File file, boolean listChild) {
		if (!file.isDirectory()) {
			return null;
		}
		// <filepath,CRC32>
		Map<String, String> map = new HashMap<String, String>();
		String crc32;
		File files[] = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory() && listChild) {
				map.putAll(CRC32DirUpper(f, listChild));
			} else if (f.isFile()) {
				crc32 = CRC32FileUpper(f);
				if (crc32 != null) {
					map.put(f.getPath(), crc32);
				}
			}
		}
		return map;
	}
}

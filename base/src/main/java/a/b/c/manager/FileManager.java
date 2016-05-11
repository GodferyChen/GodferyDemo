package a.b.c.manager;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 文件工具类
 */
public class FileManager {

    private final static long KB = 1024L;
    private final static long MB = 1048576L;
    private final static long GB = 1073741824L;
    private final static long TB = 1099511627776L;
    private final static int BUFFER = 1024*4;
    private static HashMap<File, Boolean> mFileCopyMap;
    private static HashMap<File, Boolean> mFileDeleteMap;

    private FileManager() {
    }

    /**
     * 判断是否挂载了SD卡
     */
    public boolean isMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 计算指定目录内目录和文件个数，输出格式为 --> 目录：234，文件：452
     */
    public static String getNumStr(File dir, final boolean calculateHidden) {

        File fileNum = new File(dir.getAbsolutePath());
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(calculateHidden) {
                    return true;
                } else {
                    if(pathname.isHidden()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        };
        File[] fileNums = fileNum.listFiles(fileFilter);
        int dNum = 0;
        int fNum = 0;
        for(File file2 : fileNums) {
            if(file2.isDirectory()) {
                dNum++;
            } else {
                fNum++;
            }
        }
        // 格式化文件夹中子文件夹和文件数量
        String strNum = "";
        strNum = String.format("目录：%d，文件：%d", dNum, fNum);
        return strNum;
    }

    /**
     * 获取目录或文件的长度
     */
    public static long getFileLen(File file) {

        long len = 0;
        if(file.exists()) {
            if(file.isFile()) {
                len = file.length();
            } else {
                File[] files = file.listFiles();
                for(File subFile : files) {
                    if(subFile.isFile()) {
                        len += subFile.length();
                    } else {
                        len += getFileLen(subFile);
                    }
                }
            }
        }
        return len;
    }

    /**
     * 获取指定的一个(多个)目录或文件的总长度
     */
    public static long getFilesLen(File... files) {

        long len = 0;
        for(File file : files) {
            len += getFileLen(file);
        }
        return len;
    }

    /**
     * 获取文件长度格式化之后的字符串，输出格式为 --> 4B 4KB 4MB 4GB 4TB
     */
    public static String getLenStr(long len) {

        String lenStr = "";
        if(len>=0 && len<KB) {
            lenStr = String.format("%.02f B", len*1.0);
        } else if(len>=KB && len<MB) {
            lenStr = String.format("%.02f KB", (len*1.0)/KB);
        } else if(len>=MB && len<GB) {
            lenStr = String.format("%.02f MB", (len*1.0)/MB);
        } else if(len>=GB && len<TB) {
            lenStr = String.format("%.02f GB", (len*1.0)/GB);
        } else {
            lenStr = String.format("%.02f TB", (len*1.0)/TB);
        }
        return lenStr;
    }

    /**
     * 创建新文件
     */
    public static boolean newFile(File file) {

        try {
            if(!file.exists()) {
                return newFolder(new File(file.getParent())) && file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建新目录
     */
    public static boolean newFolder(File file) {

        if(!file.exists()) {
            return file.mkdirs();
        } else {
            return false;
        }
    }

    /**
     * 重新命名目录或文件的名字
     */
    public static boolean rename(File file, String newName) {

        File newFile = new File(newName);
        if(!newFile.exists()) {
            return file.renameTo(newFile);
        } else {
            return false;
        }
    }

    /**
     * 复制选中的目录或者文件
     */
    public static HashMap<File, Boolean> copyFiles(File destDir, File... srcFiles) {

        if(mFileCopyMap==null) {
            mFileCopyMap = new HashMap<File, Boolean>();
        }
        for(File file : srcFiles) {
            if(file.isDirectory()) {
                if(file.getPath().equals(destDir.getPath()) || destDir.getParent().startsWith(file.getPath())) {
                    mFileCopyMap.put(file, false);
                    return mFileCopyMap;
                }
            }
        }
        if(!destDir.exists()) {
            destDir.mkdirs();
        }
        for(File srcFile : srcFiles) {
            if(srcFile.exists()) {
                if(srcFile.isFile()) {
                    mFileCopyMap.put(srcFile, copyFile(srcFile, new File(destDir, srcFile.getName())));
                } else if(srcFile.isDirectory()) {
                    File subDestDir = new File(destDir, srcFile.getName());
                    if(!subDestDir.exists()) {
                        subDestDir.mkdir();
                        if(srcFile.isHidden()) {
                            try {
                                Runtime.getRuntime().exec("attrib "+"\""+subDestDir.getAbsolutePath()+"\""+" +H");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    File[] files = srcFile.listFiles();
                    copyFiles(subDestDir, files);
                }
            } else {
                mFileCopyMap.put(srcFile, false);
            }
        }
        return mFileCopyMap;
    }

    /**
     * 复制文件
     */
    private static boolean copyFile(File src, File dest) {

        if(src.exists()) {
            if(!dest.exists()) {
                try {
                    newFile(dest);
                    String pattern = "";
                    if(!src.canWrite()) {
                        pattern = " +R";
                    }
                    if(src.isHidden()) {
                        pattern += " +H";
                    }
                    if(pattern.length()>0) {
                        Runtime.getRuntime().exec("attrib "+"\""+dest.getAbsolutePath()+"\""+pattern);
                    }
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
                    byte[] buf = new byte[BUFFER];
                    int len = -1;
                    while(-1!=(len = in.read(buf))) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                    in.close();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 删除目录或文件
     */
    public static HashMap<File, Boolean> deleteFiles(File... files) {

        if(mFileDeleteMap==null) {
            mFileDeleteMap = new HashMap<File, Boolean>();
        }
        for(File file : files) {
            if(file.exists()) {
                if(file.isFile()) {
                    mFileDeleteMap.put(file, file.delete());
                } else if(file.isDirectory()) {
                    File[] subFiles = file.listFiles();
                    deleteFiles(subFiles);
                }
                if(file.isDirectory()) {
                    mFileDeleteMap.put(file, file.delete());
                }
            } else {
                mFileDeleteMap.put(file, false);
            }
        }
        return mFileDeleteMap;
    }

    /**
     * 写文本文件,保存在 /data/data/PACKAGE_NAME/files 目录下
     */
    public static boolean writeTextFile(File file, String content) {

        try {
            if(!(content!=null && content.length()>0)) {
                return false;
            }
            newFolder(new File(file.getParent()));
            newFile(file);
            ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("utf-8"));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buf = new byte[BUFFER];
            int len = -1;
            while((len = in.read(buf))!=-1) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 在Android系统中,读取文本文件,保存在 /data/data/PACKAGE_NAME/files 目录下
     */
    public static String readTextFile(File file) {
        try {
            return readStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件写入字节数组
     */
    public static byte[] readByteFile(File file) {

        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            out = new ByteArrayOutputStream();
            byte[] buf = new byte[BUFFER];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将输入流转换为字符串
     */
    public static String readStream(InputStream inputStream) {
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(inputStream);
            out = new ByteArrayOutputStream();
            byte[] buf = new byte[BUFFER];
            int len = -1;
            while((len = in.read(buf))!=-1) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(out!=null) {
                    out.flush();
                    out.close();
                }
                if(in!=null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将字节数组写入文件中，一般用来向磁盘写图片,音频,视频等
     */
    public static boolean writeByteFile(File file, byte[] bytes) {

        newFolder(new File(file.getParent()));
        newFile(file);
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[BUFFER];
            int len = 0;
            while((len = in.read(buffer))!=-1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
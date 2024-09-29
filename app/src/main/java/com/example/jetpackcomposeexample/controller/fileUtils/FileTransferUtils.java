package com.example.jetpackcomposeexample.controller.fileUtils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipInputStream;

public class FileTransferUtils {
    static final String TAG = FileTransferUtils.class.getSimpleName();
//
//    public static String[] build(String path)
//    {
//        String line = "";
//        try (BufferedReader br = new BufferedReader(new FileReader(path))){
//            br.readLine();
//            if((line = br.readLine()) != null)return line.split(",");
//        } catch (FileNotFoundException e) {
//            Log.e(TAG, "FileNotFoundException");
//            e.printStackTrace();
//        }
//        catch (Exception e) {
//            Log.e(TAG, "Exception");
//            e.printStackTrace();
//        }
//        return new String[]{""};
//    }
//
    public static boolean isArchive(File file) {
        String[] extensions = {".zip", ".tar.gz"};
        for (var e: extensions) {
            if(file.getName().toLowerCase().endsWith(e)) {
                return true;
            }
        }
        return false;
    }
//
//    public static void removeArchiveIn(File folder) {
//        var candidates = folder.listFiles(FileTransferUtils::isArchive);
//        if (candidates != null) {
//            for(var f: candidates) {
//                f.delete();
//            }
//        }
//    }
//
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void syncArchive(String sourcePath, String destinationPath) {
        File source = new File(sourcePath);
        if (!source.exists()) {
            return;
        }
        Log.d(TAG, "Copying: " + sourcePath + " -> " + destinationPath);
        File destination = new File(destinationPath);
        if (source.isFile()) {
            if(!isArchive(source)) {
                return;
            }
            try {
                var parent = destination.getParentFile();
                if(parent != null) {
                    parent.mkdirs();
                    emptyDirectory(parent);
                    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    setFullPermission(destination.getAbsolutePath());
                    Log.d(TAG, "Copied: " + destinationPath);
                } else {
                    Log.e(TAG, "Copy failed, parent folder does not exist");
                }
            } catch (IOException e) {
                Log.e(TAG, "Copy failed: " + e.getMessage());
            }
        } else {
            //destination.setReadable(true, false);
            var children = source.listFiles();
            if(children != null) {
                for (var entry : children) {
                    var a = entry.getAbsolutePath();
                    var b = destinationPath + "/" + entry.getName();
                    destination.mkdirs();
                    setFullPermission(destination.getAbsolutePath());
                    syncArchive(a, b);
                }
            }
        }
    }
//
//    public static void setFullPermissionRecursively(String path) {
//        File root = new File(path);
//        root.setExecutable(true, false);
//        root.setWritable(true, false);
//        root.setReadable(true, false);
//        var children = root.listFiles();
//        if(children == null) {
//            return;
//        }
//        for (var entry : root.listFiles()) {
//            setFullPermissionRecursively(entry.getAbsolutePath());
//        }
//    }
//
    public static void setFullPermission(String path) {
        File root = new File(path);
        root.setExecutable(true, false);
        root.setWritable(true, false);
        root.setReadable(true, false);
    }
//
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void copyDirectory(String sourcePath, String destinationPath) {
        File source = new File(sourcePath);
        if (!source.exists()) {
            return;
        }
        Log.d(TAG, "Copying: " + sourcePath + " -> " + destinationPath);
        File destination = new File(destinationPath);
        if (source.isFile()) {
            try {
                var parent = destination.getParentFile();
                if(parent != null) {
                    parent.mkdirs();
                }
                setFullPermission(destination.getAbsolutePath());
                Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Log.d(TAG, "Copied: " + destinationPath);
            } catch (IOException e) {
                Log.d(TAG, "Copy failed: " + e.getMessage());
            }
        } else {
            destination.mkdirs();
            setFullPermission(destination.getAbsolutePath());
            for (var entry : source.listFiles()) {
                var a = entry.getAbsolutePath();
                var b = destinationPath + "/" + entry.getName();
                copyDirectory(a, b);
            }
        }
    }
//
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean extractZip(String zipFilePath, String destDir) {
        Log.d(TAG, "Extracting "+zipFilePath+" to "+destDir+"...");
        File root = new File(destDir);
        if (!root.exists()) root.mkdirs();;
        var destination = Paths.get(destDir);
        ZipInputStream fin = null;
        try {
            fin = new ZipInputStream(
                    new FileInputStream(zipFilePath)
            );
            var entry = fin.getNextEntry();
            while (entry != null) {
                var path = destination.resolve(entry.getName());
                if(entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Log.d(TAG, "extracting " + entry.getName());
                    Files.copy(fin, path, StandardCopyOption.REPLACE_EXISTING);
                    if(Files.size(path) == 0) {
                        throw new IOException("something went wrong, file size is 0");
                    }
                }
                entry = fin.getNextEntry();
            }
            fin.close();
        } catch (IOException e) {
            Log.e(TAG, "Error while extracting "+zipFilePath+": "+e.getMessage());
            return false;
        } finally {
            if(fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing archive file "+zipFilePath+": "+e.getMessage());
                    return false;
                }
            }
        }
        Log.d(TAG, "Unzip "+zipFilePath+" to "+destDir+" successfully");
        return true;
    }

    public static void emptyDirectory(String entry) {
        emptyDirectory(new File(entry));
    }

    public static void emptyDirectory(File entry) {
        if(!entry.exists()) {
            boolean success = entry.mkdirs();
            Log.d(TAG, "creating directory: " + entry.getAbsolutePath() +
                    " success " + success);
        }
        setFullPermission(entry.getAbsolutePath());
        var children = entry.listFiles();
        if(children != null) {
            for (var e : children) {
                removeDirectory(e);
            }
        }
    }

    public static void removeDirectory(String entry) {
        removeDirectory(new File(entry));
    }

    public static void removeDirectory(File entry) {
        var children = entry.listFiles();
        if(children != null) {
            for (var e : children) {
                removeDirectory(e);
            }
        }
        entry.delete();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void makeTarGz(String outputFileName, File inputFile) {
        Log.d(TAG, "making " + outputFileName + "...");
        File dest = new File(outputFileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
//        setFullPermission(dest.getParentFile().getAbsolutePath());
        TarArchiveOutputStream fout = null;
        try {
            fout = new TarArchiveOutputStream(
                    new GzipCompressorOutputStream(
                            new FileOutputStream(dest)
                    )
            );
            Log.v(TAG, "adding " + inputFile.getPath());
            fout.putArchiveEntry(new TarArchiveEntry(inputFile, inputFile.getAbsolutePath()));
            Files.copy(inputFile.toPath(), fout);
            fout.closeArchiveEntry();
            Log.d(TAG, "done making " + outputFileName);
        } catch (IOException e) {
            Log.d(TAG, "Error while making "+outputFileName+":"+e);
        } finally {
            if(fout != null) {
                try {
                    fout.finish();
                    fout.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing archive file "+outputFileName+": "+e.getMessage());
                }
            }
        }
    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void makeTarGz(String outputFileName, String inputDir) {
//        Log.d(TAG, "making " + outputFileName + "...");
//        TarArchiveOutputStream fout = null;
//        File dest = new File(outputFileName);
//        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
//        setFullPermission(dest.getParentFile().getAbsolutePath());
//        try {
//            var dir = new File(inputDir);
//            fout = new TarArchiveOutputStream(
//                    new GzipCompressorOutputStream(
//                            new FileOutputStream(dest)
//                    )
//            );
//            var files = walk(dir);
//            for (File file : files) {
//                Log.v(TAG, "adding " + file.getPath());
//                fout.putArchiveEntry(new TarArchiveEntry(file, file.getAbsolutePath()));
//                Files.copy(file.toPath(), fout);
//                fout.closeArchiveEntry();
//            }
//            Log.d(TAG, "done making " + outputFileName);
//        } catch (IOException e) {
//            Log.d(TAG, "Error while making "+outputFileName+":"+e.getMessage());
//        } finally {
//            if(fout != null) {
//                try {
//                    fout.finish();
//                    fout.close();
//                } catch (IOException e) {
//                    Log.e(TAG, "Error while closing archive file "+outputFileName+": "+e.getMessage());
//                }
//            }
//        }
//    }
//
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean extractTarGz(String inputFileName, String outputDir) {
        Log.d(TAG, "Extracting "+inputFileName+" to "+outputDir+"...");
        File root = new File(outputDir);
        if (!root.exists()) root.mkdirs();
//        FileTransferUtils.setFullPermission(outputDir);
        var destination = Paths.get(outputDir);
        TarArchiveInputStream fin = null;
        try {
            try {
                fin = new TarArchiveInputStream(
                    new GzipCompressorInputStream(
                        new FileInputStream(inputFileName)
                    )
                );
            } catch (IOException e) {
                fin = new TarArchiveInputStream(
                    new FileInputStream(inputFileName)
                );
            }
            var entry = fin.getNextEntry();
            while (entry != null) {
                var path = destination.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
//                    FileTransferUtils.setFullPermission(path.toAbsolutePath().toString());
                } else {
                    Log.v(TAG, "extracting " + entry.getName());
                    Files.copy(fin, path, StandardCopyOption.REPLACE_EXISTING);
//                    FileTransferUtils.setFullPermission(path.toAbsolutePath().toString());
                    if(Files.size(path) == 0) {
                        throw new IOException("something went wrong, file size is 0");
                    }
                }
                entry = fin.getNextEntry();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found "+inputFileName+": "+e.getMessage());
            return false;
        }catch (IOException e) {
            Log.e(TAG, "Error while extracting "+inputFileName+": "+e.getMessage());
            return false;
        } finally {
            if(fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing archive file "+inputFileName+": "+e.getMessage());
                    return false;
                }
            }
        }
        Log.d(TAG, "Extracted "+inputFileName+" successfully to "+outputDir);
        return true;
    }
//
//    static List<File> walk(File root) {
//        if (root.isFile()) {
//            return List.of(root);
//        }
//        var ret = new ArrayList<File>();
//        var children = root.listFiles();
//        if(children != null) {
//            for (var entry : root.listFiles()) {
//                ret.addAll(walk(entry));
//            }
//        }
//        return ret;
//    }
}

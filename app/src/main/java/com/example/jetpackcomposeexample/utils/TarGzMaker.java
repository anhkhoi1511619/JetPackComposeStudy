package com.example.jetpackcomposeexample.utils;

import android.util.Log;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TarGzMaker {
    public static void _createTarGzFromCsv(String csvFilePath, String tarGzOutputPath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(tarGzOutputPath);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos);
                TarArchiveOutputStream taos = new TarArchiveOutputStream(gzos)
        ) {
            Log.d("TarGzMaker", "Csv Input File Path: "+csvFilePath);
            File csvFile = new File(csvFilePath);
            long fileSize = csvFile.length();
            TarArchiveEntry entry = new TarArchiveEntry(csvFile, csvFile.getName());
            entry.setSize(fileSize);
            taos.putArchiveEntry(entry);
            Log.d("TarGzMaker", "Csv Output File Path: "+tarGzOutputPath);
            try (FileInputStream fis = new FileInputStream(csvFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    taos.write(buffer, 0, len);
                }
            } catch (Exception e) {
                Log.d("TarGzMaker", "Exception: "+e.getMessage());
            }
            Log.d("TarGzMaker", "Closing ... ");
            taos.closeArchiveEntry();
        }
    }

    public static void createTarGzFromCsv(String csvFilePath, String tarGzOutputPath) throws IOException {
        File csvFile = new File(csvFilePath);
        String entryName = csvFile.getName();

        // Bước 1: Đọc toàn bộ file vào ByteArrayOutputStream để biết chính xác kích thước
        byte[] content;
        try (FileInputStream fis = new FileInputStream(csvFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            content = baos.toByteArray();
        }

        // Bước 2: Ghi nội dung vào .tar.gz với kích thước chính xác
        try (
                FileOutputStream fos = new FileOutputStream(tarGzOutputPath);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos);
                TarArchiveOutputStream taos = new TarArchiveOutputStream(gzos)
        ) {
            TarArchiveEntry entry = new TarArchiveEntry(entryName);
            entry.setSize(content.length); // Kích thước chính xác
            taos.putArchiveEntry(entry);
            taos.write(content);           // Ghi nội dung từ buffer
            taos.closeArchiveEntry();
        }
    }

    public static void delete(String csvFilePath)
    {
        File csvFile = new File(csvFilePath);
        // ✅ Bước 3: Xóa file CSV gốc
        if (csvFile.delete()) {
            System.out.println("Đã xóa file CSV: " + csvFilePath);
        } else {
            System.err.println("Không thể xóa file CSV: " + csvFilePath);
        }
    }
}

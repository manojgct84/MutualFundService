package org.mymf.exception;

import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Component
public class RecordStatusTracker
{

    private static final String RESOURCE_FOLDER = "src/main/resources/";

    private static final String STATUS_FILE = "update_status.txt";
    private static final String SUCCESS_RECORDS = "success_records.txt";

    private static final String SUCCESS_FILE_PATH = RESOURCE_FOLDER + SUCCESS_RECORDS;

    private static final String STATUS_FILE_PATH = RESOURCE_FOLDER + STATUS_FILE;


    public void writeRecordStatus(Long recordId, String status, String errorMessage) throws IOException {
        String line = recordId + "," + status + "," + (errorMessage != null ? errorMessage : "");
        ensureFileExists(STATUS_FILE_PATH);
        Files.write(Paths.get(STATUS_FILE_PATH), (line + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public Map<Long, String> readFailedOrInProgressRecords() throws IOException {
        Map<Long, String> recordsToProcess = new HashMap<>();
        ensureFileExists(STATUS_FILE_PATH);
        List<String> lines = Files.readAllLines(Paths.get(STATUS_FILE_PATH));
        for (String line : lines) {
            String[] parts = line.split(",");
            Long recordId = Long.parseLong(parts[0]);
            String status = parts[1];

            if ("FAILED".equals(status) || "IN_PROGRESS".equals(status)) {
                recordsToProcess.put(recordId, status);
            }
        }
        return recordsToProcess;
    }

    /**
     * Logs a success record if it does not already exist in the file.
     * @param record The success record to log
     */
    public static void logSuccessRecord(String record) {
        try {
            ensureFileExists(SUCCESS_FILE_PATH);
            // Step 1: Load existing records from file
            Set<String> existingRecords = loadExistingRecords();

            // Step 2: Check if record already exists
            if (existingRecords.contains(record)) {
                System.out.println("Record already exists in the log file.");
                return;
            }

            // Step 3: Append new record to file
            try (FileWriter fw = new FileWriter(SUCCESS_FILE_PATH, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(record);
                System.out.println("Record successfully logged.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while logging the success record: " + e.getMessage());
        }
    }

    /**
     * Loads existing records from the file into a Set for quick lookup.
     * @return Set of existing records
     */
    public static Set<String> loadExistingRecords() {
        Set<String> records = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SUCCESS_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line.trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Log file not found. It will be created when a new record is added.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the log file: " + e.getMessage());
        }
        return records;
    }

    /**
     * Ensures the success_records.txt file exists in the resources folder.
     * If it doesn't exist, it will be created.
     */
    private static void ensureFileExists(String file_path) throws IOException {
        Path path = Paths.get(file_path);
        if (!Files.exists(path)) {
            // Create the file if it does not exist
            Files.createDirectories(path.getParent()); // Ensure parent directories exist
            Files.createFile(path); // Create the file
            System.out.println("File created in resources folder: " + file_path);
        }
    }
}

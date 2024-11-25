package org.uoc.group.groupassignment;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HelloController {

    @FXML
    private Label resultLabel; // To display sorted result

    @FXML
    private ComboBox<String> algorithmComboBox; // To choose sorting algorithm

    @FXML
    private ComboBox<String> columnComboBox; // To select column for sorting

    private final List<List<String>> dataset = new ArrayList<>(); // Store CSV data
    private final List<String> columnNames = new ArrayList<>(); // Store column names

    @FXML
    public void initialize() {
        algorithmComboBox.getItems().addAll("Insertion Sort", "Shell Sort", "Merge Sort", "Quick Sort", "Heap Sort");
    }

    // Upload CSV file
    @FXML
    protected void onUploadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            // Check if the file is of type CSV
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                resultLabel.setText("Invalid file type. Please upload a CSV file.");
                return; // Exit the method if it's not a CSV
            }

            try {
                // Read all lines from the CSV file
                List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));

                // Clear previous data
                dataset.clear();
                columnNames.clear();

                // Parse the CSV data and add to dataset
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim(); // Trim whitespace around the line

                    // Skip empty lines
                    if (line.isEmpty()) {
                        continue;
                    }

                    // Split the line by commas to extract individual values
                    String[] values = line.split(",");
                    dataset.add(Arrays.asList(values));

                    // Only take the first row as column names (header)
                    if (i == 0) {
                        columnNames.addAll(Arrays.asList(values));
                    }
                }

                // Debugging: Print out the column names and dataset
                System.out.println("Column Names: " + columnNames);
                System.out.println("Dataset: " + dataset);

                // Filter only numeric columns
                List<String> numericColumns = new ArrayList<>();
                for (int col = 0; col < columnNames.size(); col++) {
                    boolean isNumeric = true;

                    // Check if all rows (except the header) have numeric values in this column
                    for (int row = 1; row < dataset.size(); row++) {
                        String value = dataset.get(row).get(col).trim();
                        if (!value.matches("-?\\d+(\\.\\d+)?")) { // Regex for integer or decimal
                            isNumeric = false;
                            break;
                        }
                    }

                    if (isNumeric) {
                        numericColumns.add(columnNames.get(col));
                    }
                }

                // Update the ComboBox with numeric column names
                columnComboBox.getItems().clear();
                columnComboBox.getItems().addAll(numericColumns);

                if (numericColumns.isEmpty()) {
                    resultLabel.setText("No numeric columns found.");
                } else {
                    resultLabel.setText("File uploaded successfully!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                resultLabel.setText("Error reading the file.");
            }
        }
    }

    // Sort and display the result for the selected algorithm
    @FXML
    protected void onSortButtonClick() {
        String selectedAlgorithm = algorithmComboBox.getValue();
        String selectedColumn = columnComboBox.getValue();
        int columnIndex = columnNames.indexOf(selectedColumn);

        if (columnIndex == -1) {
            resultLabel.setText("Please select a valid column.");
            return;
        }

        List<Integer> columnData = new ArrayList<>();
        for (List<String> row : dataset) {
            String cell = row.get(columnIndex).trim();

            if (cell.isEmpty()) {
                continue;
            }

            try {
                columnData.add(Integer.parseInt(cell));
            } catch (NumberFormatException e) {
                System.out.println("Skipping invalid data: " + cell);
            }
        }

        if (columnData.isEmpty()) {
            resultLabel.setText("No valid numeric data found in the selected column.");
            return;
        }

        // Measure execution time for the selected algorithm
        List<Integer> dataCopy = new ArrayList<>(columnData); // Copy data to avoid modifying original
        long startTime = System.nanoTime();
        List<Integer> sortedData = switch (selectedAlgorithm) {
            case "Insertion Sort" -> SortingAlgorithms.insertionSort(dataCopy);
            case "Shell Sort" -> SortingAlgorithms.shellSort(dataCopy);
            case "Merge Sort" -> SortingAlgorithms.mergeSort(dataCopy);
            case "Quick Sort" -> SortingAlgorithms.quickSort(dataCopy);
            case "Heap Sort" -> SortingAlgorithms.heapSort(dataCopy);
            default -> dataCopy;
        };
        long endTime = System.nanoTime();

        double duration = (endTime - startTime) / 1_000_000.0; // Time in milliseconds

        // Display the sorted data and execution time for the selected algorithm
        resultLabel.setText("Sorted Data: " + sortedData + "\nExecution Time (" + selectedAlgorithm + "): " + duration + " ms");
    }

    // Find the best sorting method and display its details
    @FXML
    protected void onFindBestMethodButtonClick() {
        String selectedColumn = columnComboBox.getValue();
        int columnIndex = columnNames.indexOf(selectedColumn);

        if (columnIndex == -1) {
            resultLabel.setText("Please select a valid column.");
            return;
        }

        List<Integer> columnData = new ArrayList<>();
        for (List<String> row : dataset) {
            String cell = row.get(columnIndex).trim();

            if (cell.isEmpty()) {
                continue;
            }

            try {
                columnData.add(Integer.parseInt(cell));
            } catch (NumberFormatException e) {
                System.out.println("Skipping invalid data: " + cell);
            }
        }

        if (columnData.isEmpty()) {
            resultLabel.setText("No valid numeric data found in the selected column.");
            return;
        }

        // Measure execution times for all algorithms
        Map<String, Double> executionTimes = new LinkedHashMap<>();
        Map<String, List<Integer>> sortedResults = new LinkedHashMap<>();

        measureSortingTime("Insertion Sort", columnData, executionTimes, sortedResults);
        measureSortingTime("Shell Sort", columnData, executionTimes, sortedResults);
        measureSortingTime("Merge Sort", columnData, executionTimes, sortedResults);
        measureSortingTime("Quick Sort", columnData, executionTimes, sortedResults);
        measureSortingTime("Heap Sort", columnData, executionTimes, sortedResults);

        // Find the best-performing algorithm
        String bestAlgorithm = executionTimes.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("None");

        double bestTime = executionTimes.getOrDefault(bestAlgorithm, Double.MAX_VALUE);

        // Display execution times and the best-performing algorithm
        StringBuilder result = new StringBuilder("Execution Times:\n");
        executionTimes.forEach((algorithm, time) -> result.append(algorithm).append(": ").append(time).append(" ms\n"));
        result.append("\nBest Performing Algorithm: ").append(bestAlgorithm).append(" (").append(bestTime).append(" ms)");

        // Display the sorted result of the best algorithm
        List<Integer> bestSortedData = sortedResults.get(bestAlgorithm);
        result.append("\n\nSorted Data (").append(bestAlgorithm).append("): ").append(bestSortedData);

        resultLabel.setText(result.toString());
    }

    // Measure sorting execution time and store results
    private void measureSortingTime(String algorithm, List<Integer> data, Map<String, Double> times, Map<String, List<Integer>> results) {
        List<Integer> dataCopy = new ArrayList<>(data);

        long startTime = System.nanoTime();
        List<Integer> sortedData = switch (algorithm) {
            case "Insertion Sort" -> SortingAlgorithms.insertionSort(dataCopy);
            case "Shell Sort" -> SortingAlgorithms.shellSort(dataCopy);
            case "Merge Sort" -> SortingAlgorithms.mergeSort(dataCopy);
            case "Quick Sort" -> SortingAlgorithms.quickSort(dataCopy);
            case "Heap Sort" -> SortingAlgorithms.heapSort(dataCopy);
            default -> dataCopy;
        };
        long endTime = System.nanoTime();

        double duration = (endTime - startTime) / 1_000_000.0; // Time in milliseconds
        times.put(algorithm, duration);
        results.put(algorithm, sortedData);
    }
}

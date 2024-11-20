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
    private TextField inputTextField; // To input unsorted numbers

    @FXML
    private ComboBox<String> algorithmComboBox; // To choose sorting algorithm

    @FXML
    private ComboBox<String> columnComboBox; // To select column for sorting

    @FXML
    private Button sortButton; // To trigger sorting

    @FXML
    private Button uploadButton; // To trigger file upload

    private List<List<String>> dataset = new ArrayList<>(); // Store CSV data
    private List<String> columnNames = new ArrayList<>(); // Store column names

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

                // Update the ComboBox with column names
                columnComboBox.getItems().clear();
                columnComboBox.getItems().addAll(columnNames);

            } catch (IOException e) {
                e.printStackTrace();
                resultLabel.setText("Error reading the file.");
            }
        }
    }

    // Sort and display the result
    @FXML
    protected void onSortButtonClick() {
        String selectedColumn = columnComboBox.getValue();
        int columnIndex = columnNames.indexOf(selectedColumn);

        if (columnIndex == -1) {
            resultLabel.setText("Please select a valid column.");
            return;
        }

        List<Integer> columnData = new ArrayList<>();
        for (List<String> row : dataset) {
            String cell = row.get(columnIndex).trim(); // Trim whitespace

            // Skip empty values
            if (cell.isEmpty()) {
                continue;
            }

            try {
                // Attempt to parse the column data as integers
                columnData.add(Integer.parseInt(cell));
            } catch (NumberFormatException e) {
                // If the value is not numeric, skip this row and print a warning
                System.out.println("Skipping invalid data: " + cell);
            }
        }

        if (columnData.isEmpty()) {
            resultLabel.setText("No valid numeric data found in the selected column.");
            return;
        }

        // Perform sorting and measure time
        long startTime = System.nanoTime();
        List<Integer> sortedData = switch (algorithmComboBox.getValue()) {
            case "Insertion Sort" -> SortingAlgorithms.insertionSort(columnData);
            case "Shell Sort" -> SortingAlgorithms.shellSort(columnData);
            case "Merge Sort" -> SortingAlgorithms.mergeSort(columnData);
            case "Quick Sort" -> SortingAlgorithms.quickSort(columnData);
            case "Heap Sort" -> SortingAlgorithms.heapSort(columnData);
            default -> columnData;
        };
        long endTime = System.nanoTime();

        double duration = (endTime - startTime) / 1_000_000.0; // Time in milliseconds
        resultLabel.setText("Sorted: " + sortedData + "\nExecution Time: " + duration + " ms");
    }
}

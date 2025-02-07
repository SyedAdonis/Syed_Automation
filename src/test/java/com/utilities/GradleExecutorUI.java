import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class GradleExecutorUI {
    public static void main(String[] args) {
        // Create main frame
        JFrame frame = new JFrame("Excel-Octane Task Executor");
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components

        // Source File Selection
        JLabel fileLabel = new JLabel("Source File:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(fileLabel, gbc);

        JTextField filePathField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(filePathField, gbc);

        JButton fileButton = new JButton("Browse");
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        frame.add(fileButton, gbc);

        // Target File Name
        JLabel label2 = new JLabel("Target File Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(label2, gbc);

        JTextField textField2 = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(textField2, gbc);

        // Sheet Name (Dropdown List)
        JLabel label3 = new JLabel("Sheet Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        frame.add(label3, gbc);

        JComboBox<String> sheetDropdown = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(sheetDropdown, gbc);

        // Product Area
        JLabel label4 = new JLabel("Product Area:");
        gbc.gridx = 2;
        gbc.gridy = 2;
        frame.add(label4, gbc);

        JTextField textField4 = new JTextField(10);
        gbc.gridx = 3;
        gbc.gridy = 2;
        frame.add(textField4, gbc);

        // Run Task Button
        JButton executeButton = new JButton("Run Task");
        executeButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(executeButton, gbc);

        // File Picker ActionListener
        fileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                // Validate File Type
                if (!filePath.endsWith(".xlsx") && !filePath.endsWith(".xlsm")) {
                    JOptionPane.showMessageDialog(frame, "Invalid file type! Only .xlsx and .xlsm are allowed.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                filePathField.setText(filePath);

                // Generate Target File Name
                String targetFileName = generateTargetFileName(filePath);
                textField2.setText(targetFileName);

                // Populate Sheet Dropdown
                List<String> sheetNames = null;
                try {
                    sheetNames = getSheetNames(filePath);
                } catch (InvalidFormatException ex) {
                    throw new RuntimeException(ex);
                }
                sheetDropdown.removeAllItems();
                if (!sheetNames.isEmpty()) {
                    for (String sheet : sheetNames) {
                        sheetDropdown.addItem(sheet);
                    }
                }
            }
        });

        // Execute Button Action
        executeButton.addActionListener((ActionEvent e) -> {
            String sourceFile = filePathField.getText();
            String targetFileName = textField2.getText();
            String sheetName = (String) sheetDropdown.getSelectedItem();
            String productArea = textField4.getText();

            if (sourceFile.isEmpty() || targetFileName.isEmpty() || sheetName == null || productArea.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Construct Gradle Command
            String command = String.format("gradle execute -PsourceFile=\"%s\" -PtargetFileName=\"%s\" -PsheetName=\"%s\" -PproductArea=\"%s\"",
                    sourceFile, targetFileName, sheetName, productArea);

            try {
                Process process = Runtime.getRuntime().exec(command);
                JOptionPane.showMessageDialog(frame, "Command executed:\n" + command, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Failed to execute command!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    // Method to Generate Target File Name
    private static String generateTargetFileName(String sourceFilePath) {
        int dotIndex = sourceFilePath.lastIndexOf(".");
        if (dotIndex > 0) {
            return sourceFilePath.substring(0, dotIndex) + "_Octane" + sourceFilePath.substring(dotIndex);
        }
        return sourceFilePath + "_Octane";
    }

    // Method to Read Sheet Names from Excel File
    private static List<String> getSheetNames(String filePath) throws InvalidFormatException {
        List<String> sheetNames = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read sheets from file!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return sheetNames;
    }
}
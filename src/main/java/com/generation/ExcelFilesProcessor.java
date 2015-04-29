package com.generation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * User: YamStranger
 * Date: 4/24/15
 * Time: 1:33 AM
 */
public class ExcelFilesProcessor {
    final String product = "Product Name".trim();
    final String uuid = "Unique Identification Number".trim();
    final Path file;

    public ExcelFilesProcessor(Path file) {
        this.file = file;
    }

    public void process() throws IOException {
        //Read Excel document first
        Generator generator = new Generator();
        try (InputStream document = Files.newInputStream(file)) {
            final XSSFWorkbook workbook = new XSSFWorkbook(document);
            final XSSFSheet worksheet = workbook.getSheetAt(0);
            // Get iterator to all the rows in current sheet
            Iterator<Row> rows = worksheet.iterator();
            // Traversing over each row of XLSX file
            boolean isBody = false;
            int uuidCell = -1;
            int productName = -1;
            int number = -1;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (!isBody) {
                    // For each row, iterate through each columns
                    Iterator<Cell> cells = row.cellIterator();
                    while (cells.hasNext()) {
                        number += 1;
                        Cell cell = cells.next();
                        String value = cell.getStringCellValue();
                        if (value != null) {
                            if (product.equalsIgnoreCase(value.trim())) {
                                productName = number;
                            }
                            if (uuid.equalsIgnoreCase(value.trim())) {
                                isBody = true;
                                uuidCell = number;
                                break;
                            }
                        }

                    }
                    number = -1;
                } else {
                    if (productName != -1) {
                        Cell pCell = row.getCell(productName);
                        if (pCell != null && pCell.getStringCellValue() != null &&
                                row.getCell(productName) != null &&
                                row.getCell(productName).getStringCellValue().length() > 0
                                ) {
                            Cell uCell = row.getCell(uuidCell);
                            if (uCell == null) {
                                uCell = row.createCell(uuidCell);
                            }
                            uCell.setCellValue(generator.generate());
                        }
                    }

                }
            }
            document.close();
            if (isBody) {
                OutputStream output = Files.newOutputStream(file);
                workbook.write(output);
                output.close();
                workbook.close();
            }
        } catch (IOException e) {
            throw e;
        }
    }

}

package fr.uphf.sae5a1api.tests;

import fr.uphf.sae5a1api.data.actions.ActionHandball;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CSVReading {

    public CSVReading() {}

    public List<ActionHandball> readActions(String fileName, String sheetName) {
        List<ActionHandball> actions = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = (workbook.getNumberOfSheets() == 1) ?
                    workbook.getSheetAt(0) : workbook.getSheet(sheetName);

            if (sheet == null) {
                System.out.println("La feuille '" + sheetName + "' n'existe pas.");
                return actions;
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if(getStringCell(row, 0).isEmpty())
                    continue;

                String position    = getStringCell(row, 0);
                double temps       = getNumericCell(row, 1);
                double duree       = getNumericCell(row, 2);
                String joueuse     = getStringCell(row, 4);
                String resultat    = getStringCell(row, 5);
                String secteur     = getStringCell(row, 6);
                String defense     = getStringCell(row, 7);
                String commentaire = getStringCell(row, 14);

                ActionHandball action = new ActionHandball(position, temps, duree,
                        joueuse, resultat, secteur,
                        defense, commentaire);
                actions.add(action);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return actions;
    }

    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null) ? cell.toString().trim() : "";
    }

    private double getNumericCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        return 0.0;
    }


}

package fr.uphf.sae5a1api.tests;

import fr.uphf.sae5a1api.SAE5A1ApiApplication;
import fr.uphf.sae5a1api.data.impl.actions.ActionHandball;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public class CSVReading {

    public CSVReading() {}

    public List<ActionHandball> readActions(String fileName, String sheetName) {
        List<ActionHandball> actions = new ArrayList<>();
        String lower = fileName.toLowerCase(Locale.ROOT);

        try {
            if (lower.endsWith(".csv")) {
                return readCsv(fileName);
            }

            try (FileInputStream fis = new FileInputStream(fileName);
                 Workbook workbook = lower.endsWith(".xls") ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis)) {

                Sheet sheet = (workbook.getNumberOfSheets() == 1) ?
                        workbook.getSheetAt(0) : workbook.getSheet(sheetName);

                if (sheet == null) {
                    SAE5A1ApiApplication.getLogger().log(Level.INFO, "La feuille '" + sheetName + "' n'existe pas.");
                    return actions;
                }

                Iterator<Row> rowIterator = sheet.iterator();
                if (rowIterator.hasNext()) rowIterator.next();

                UUID uuid = UUID.randomUUID();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (getStringCell(row, 0).isEmpty()) continue;

                    ActionHandball action = buildFromRow(row, uuid);
                    actions.add(action);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return actions;
    }

    private List<ActionHandball> readCsv(String fileName) throws IOException {
        List<ActionHandball> actions = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] cols = splitCsvLine(line);
                if (cols.length == 0 || cols[0].trim().isEmpty()) continue;

                ActionHandball action = buildFromCsvCols(cols, uuid);
                actions.add(action);
            }
        }

        return actions;
    }

    private String[] splitCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuote && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    cur.append('\"');
                    i++;
                } else {
                    inQuote = !inQuote;
                }
                continue;
            }
            if (!inQuote && (c == ',' || c == ';')) {
                parts.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString().trim());
        return parts.toArray(new String[0]);
    }

    private ActionHandball buildFromRow(Row row, UUID uuid) {
        String nom                    = getStringCell(row, 0);
        double position               = getNumericCell(row, 1);
        double duree                  = getNumericCell(row, 2);
        String defense                = getStringCell(row, 3);
        String resultat               = getStringCell(row, 4);
        String defensePlus            = getStringCell(row, 5);
        String joueuse                = getStringCell(row, 6);
        String secteur                = getStringCell(row, 7);
        String attaquePlacees         = getStringCell(row, 8);
        String enclenchements06       = getStringCell(row, 9);
        String lieuPb                 = getStringCell(row, 10);
        String passeD                 = getStringCell(row, 11);
        String repli                  = getStringCell(row, 12);
        String defenseMoins           = getStringCell(row, 13);
        String enclenchementsTransiER = getStringCell(row, 14);
        String grandEspace            = getStringCell(row, 15);
        String jets7m                 = getStringCell(row, 16);
        String enclenchements6c5      = getStringCell(row, 17);

        return new ActionHandball(
                (long) -1,
                uuid,
                nom,
                position,
                duree,
                defense,
                resultat,
                defensePlus,
                joueuse,
                secteur,
                attaquePlacees,
                enclenchements06,
                lieuPb,
                passeD,
                repli,
                defenseMoins,
                enclenchementsTransiER,
                grandEspace,
                jets7m,
                enclenchements6c5
        );
    }

    private ActionHandball buildFromCsvCols(String[] cols, UUID uuid) {
        String nom                    = (0 < cols.length) ? cols[0].trim() : "";
        double position               = parseDoubleSafe((1 < cols.length) ? cols[1].trim() : "");
        double duree                  = parseDoubleSafe((2 < cols.length) ? cols[2].trim() : "");
        String defense                = (3 < cols.length) ? cols[3].trim() : "";
        String resultat               = (4 < cols.length) ? cols[4].trim() : "";
        String defensePlus            = (5 < cols.length) ? cols[5].trim() : "";
        String joueuse                = (6 < cols.length) ? cols[6].trim() : "";
        String secteur                = (7 < cols.length) ? cols[7].trim() : "";
        String attaquePlacees         = (8 < cols.length) ? cols[8].trim() : "";
        String enclenchements06       = (9 < cols.length) ? cols[9].trim() : "";
        String lieuPb                 = (10 < cols.length) ? cols[10].trim() : "";
        String passeD                 = (11 < cols.length) ? cols[11].trim() : "";
        String repli                  = (12 < cols.length) ? cols[12].trim() : "";
        String defenseMoins           = (13 < cols.length) ? cols[13].trim() : "";
        String enclenchementsTransiER = (14 < cols.length) ? cols[14].trim() : "";
        String grandEspace            = (15 < cols.length) ? cols[15].trim() : "";
        String jets7m                 = (16 < cols.length) ? cols[16].trim() : "";
        String enclenchements6c5      = (17 < cols.length) ? cols[17].trim() : "";

        return new ActionHandball(
                (long) -1,
                uuid,
                nom,
                position,
                duree,
                defense,
                resultat,
                defensePlus,
                joueuse,
                secteur,
                attaquePlacees,
                enclenchements06,
                lieuPb,
                passeD,
                repli,
                defenseMoins,
                enclenchementsTransiER,
                grandEspace,
                jets7m,
                enclenchements6c5
        );
    }

    private double parseDoubleSafe(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s.replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null) ? cell.toString().trim() : "";
    }

    private double getNumericCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else {
                return parseDoubleSafe(cell.toString().trim());
            }
        }
        return 0.0;
    }
}

package org.example.wettkampfverwaltung;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ReadFromCSV {
    public ArrayList<FighterPair> read(File csvFile) {
        ArrayList<FighterPair> fighterPairs = new ArrayList<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(',')
                .setIgnoreEmptyLines(true)
                .build();

        try (BufferedReader reader = Files.newBufferedReader(csvFile.toPath(), StandardCharsets.UTF_8)) {

            // UTF-8-BOM überspringen, BEVOR der Parser die Datei sieht
            reader.mark(1);
            if (reader.read() != '\uFEFF') {
                reader.reset();
            }

            try (CSVParser parser = format.parse(reader)) {
                for (CSVRecord row : parser) {
                    if (row.size() != 6) {
                        throw new IllegalArgumentException(
                                "Zeile " + row.getRecordNumber()
                                        + " hat " + row.size()
                                        + " statt 6 Spalten."
                        );
                    }

                    fighterPairs.add(new FighterPair(
                            row.get(0).trim(), row.get(1).trim(), row.get(2).trim(),
                            row.get(3).trim(), row.get(4).trim(), row.get(5).trim()
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV-Datei konnte nicht gelesen werden.", e);
        }

        return fighterPairs;
    }
}
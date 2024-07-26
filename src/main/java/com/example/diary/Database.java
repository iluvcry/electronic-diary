package com.example.diary;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<LocalDate, DiaryEntry> diaryEntries = new HashMap<>();

    public static DiaryEntry getEntry(LocalDate date) {
        return diaryEntries.getOrDefault(date, new DiaryEntry(date, ""));
    }

    public static void addEntry(DiaryEntry entry) {
        diaryEntries.put(entry.getDate(), entry);
    }
}

package com.example.diary;

import java.time.LocalDate;

public class DiaryEntry {
    private LocalDate date;
    private String content;

    public DiaryEntry(LocalDate date, String content) {
        this.date = date;
        this.content = content;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

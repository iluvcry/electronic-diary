package com.example.diary;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DiaryController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ListView<String> taskListView;

    @FXML
    private TextArea taskTextArea;

    @FXML
    private TabPane viewTabPane;

    @FXML
    private GridPane weekGridPane;

    @FXML
    private GridPane monthGridPane;

    @FXML
    private VBox dayBox;

    @FXML
    private Button updateTaskButton;

    private Map<LocalDate, String> taskMap = new HashMap<>();
    private String selectedTask;
    private int selectedIndex;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        setupWeekView();
        setupMonthView();
        onDateSelected();

        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedTask = newValue;
            selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
            if (selectedTask != null) {
                taskTextArea.setText(selectedTask.split("\\. ", 2)[1]);
                updateTaskButton.setDisable(false);
            }
        });
    }

    @FXML
    public void onDateSelected() {
        LocalDate selectedDate = datePicker.getValue();
        updateTaskList(selectedDate);
        setupDayView(selectedDate);
    }

    @FXML
    public void onAddTask() {
        LocalDate selectedDate = datePicker.getValue();
        String newTask = taskTextArea.getText().trim();
        if (!newTask.isEmpty()) {
            String existingTasks = taskMap.getOrDefault(selectedDate, "");
            String updatedTasks = existingTasks.isEmpty() ? newTask : existingTasks + "\n" + newTask;
            taskMap.put(selectedDate, updatedTasks);
            taskTextArea.clear();
            onDateSelected();
            setupWeekView();
            setupMonthView();
        }
    }

    @FXML
    public void onUpdateTask() {
        if (selectedTask != null && selectedIndex >= 0) {
            LocalDate selectedDate = datePicker.getValue();
            String updatedTask = taskTextArea.getText().trim();
            if (!updatedTask.isEmpty()) {
                String tasks = taskMap.get(selectedDate);
                String[] taskArray = tasks.split("\n");
                taskArray[selectedIndex] = updatedTask;
                taskMap.put(selectedDate, String.join("\n", taskArray));
                taskTextArea.clear();
                onDateSelected();
                setupWeekView();
                setupMonthView();
                updateTaskButton.setDisable(true);
            }
        }
    }

    @FXML
    public void onDeleteTask() {
        if (selectedTask != null && selectedIndex >= 0) {
            LocalDate selectedDate = datePicker.getValue();
            String tasks = taskMap.get(selectedDate);
            String[] taskArray = tasks.split("\n");
            taskArray[selectedIndex] = "";
            taskMap.put(selectedDate, String.join("\n", taskArray).replaceAll("(?m)^[ \t]*\r?\n", ""));
            taskTextArea.clear();
            onDateSelected();
            setupWeekView();
            setupMonthView();
            updateTaskButton.setDisable(true);
        }
    }

    private void updateTaskList(LocalDate date) {
        String tasks = taskMap.getOrDefault(date, "");
        taskTextArea.setText("");
        taskListView.getItems().clear();
        if (!tasks.isEmpty()) {
            int taskNumber = 1;
            for (String task : tasks.split("\n")) {
                taskListView.getItems().add(taskNumber + ". " + task);
                taskNumber++;
            }
        }
    }

    private void setupWeekView() {
        weekGridPane.getChildren().clear();
        LocalDate startOfWeek = datePicker.getValue().minusDays(datePicker.getValue().getDayOfWeek().getValue() - 1);

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            VBox dayBox = createDayBox(day);
            weekGridPane.add(dayBox, i, 1);

            // Добавляем задачи в weekGridPane
            String tasks = taskMap.getOrDefault(day, "");
            if (!tasks.isEmpty()) {
                VBox taskBox = new VBox();
                for (String task : tasks.split("\n")) {
                    Text taskText = new Text(task);
                    taskBox.getChildren().add(taskText);
                }
                weekGridPane.add(taskBox, i, 2);
            }
        }

        // Adding days of the week
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            Text dayOfWeekText = new Text(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            HBox dayOfWeekBox = new HBox(dayOfWeekText);
            dayOfWeekBox.setStyle("-fx-alignment: center; -fx-border-color: lightblue; -fx-border-width: 1; -fx-padding: 5;");
            weekGridPane.add(dayOfWeekBox, i, 0);
        }
    }

    private void setupMonthView() {
        monthGridPane.getChildren().clear();
        LocalDate firstDayOfMonth = datePicker.getValue().withDayOfMonth(1);

        int daysInMonth = datePicker.getValue().lengthOfMonth();
        int dayOfWeekOfFirstDay = firstDayOfMonth.getDayOfWeek().getValue();

        // Adding days of the week
        for (int i = 0; i < 7; i++) {
            Text dayOfWeekText = new Text(firstDayOfMonth.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            HBox dayOfWeekBox = new HBox(dayOfWeekText);
            dayOfWeekBox.setStyle("-fx-alignment: center; -fx-border-color: lightblue; -fx-border-width: 1; -fx-padding: 5;");
            monthGridPane.add(dayOfWeekBox, i, 0);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = firstDayOfMonth.plusDays(day - 1);
            VBox dayBox = createDayBox(date);

            int column = (dayOfWeekOfFirstDay + day - 2) % 7;
            int row = (dayOfWeekOfFirstDay + day - 2) / 7 + 1;
            monthGridPane.add(dayBox, column, row);
        }
    }

    private void setupDayView(LocalDate date) {
        dayBox.getChildren().clear();
        dayBox.setStyle("-fx-border-color: lightblue; -fx-border-width: 1; -fx-padding: 5;");

        Text dateText = new Text(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        dayBox.getChildren().add(dateText);

        VBox taskVBox = new VBox();
        taskVBox.setSpacing(10);

        Text taskTitle = new Text("Задачи:");
        taskVBox.getChildren().add(taskTitle);

        String tasks = taskMap.getOrDefault(date, "");
        if (!tasks.isEmpty()) {
            for (String task : tasks.split("\n")) {
                Text taskText = new Text(task);
                taskVBox.getChildren().add(taskText);
            }
        } else {
            Text noTasksText = new Text("Нет задач на этот день.");
            taskVBox.getChildren().add(noTasksText);
        }

        dayBox.getChildren().add(taskVBox);
    }


    private VBox createDayBox(LocalDate date) {
        VBox dayBox = new VBox();
        dayBox.setStyle("-fx-border-color: lightblue; -fx-border-width: 1; -fx-padding: 5;");
        dayBox.setOnMouseClicked(event -> {
            datePicker.setValue(date);
            viewTabPane.getSelectionModel().select(0); // Переход на вкладку "День"
            onDateSelected();
        });

        Text dateText = new Text(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        dayBox.getChildren().add(dateText);

        HBox indicators = new HBox();
        indicators.setSpacing(5);

        if (date.equals(LocalDate.now())) {
            Rectangle currentDayIndicator = new Rectangle(10, 2);
            currentDayIndicator.setFill(Color.GREEN);
            indicators.getChildren().add(currentDayIndicator);
        }

        if (taskMap.containsKey(date)) {
            int taskCount = taskMap.get(date).split("\n").length;
            if (taskCount > 0) {
                Rectangle taskIndicator = new Rectangle(10, 2);
                taskIndicator.setFill(Color.RED);
                indicators.getChildren().add(taskIndicator);

                Text taskCountText = new Text("Задач: " + taskCount);
                dayBox.getChildren().add(taskCountText);
            }
        }

        dayBox.getChildren().add(indicators);
        return dayBox;
    }

    @FXML
    public void goToPreviousDay() {
        datePicker.setValue(datePicker.getValue().minusDays(1));
        onDateSelected();
    }

    @FXML
    public void goToNextDay() {
        datePicker.setValue(datePicker.getValue().plusDays(1));
        onDateSelected();
    }

    @FXML
    public void goToCurrentDay() {
        datePicker.setValue(LocalDate.now());
        onDateSelected();
    }

    @FXML
    public void goToPreviousWeek() {
        datePicker.setValue(datePicker.getValue().minusWeeks(1));
        setupWeekView();
    }

    @FXML
    public void goToNextWeek() {
        datePicker.setValue(datePicker.getValue().plusWeeks(1));
        setupWeekView();
    }

    @FXML
    public void goToCurrentWeek() {
        datePicker.setValue(LocalDate.now());
        setupWeekView();
    }

    @FXML
    public void goToPreviousMonth() {
        datePicker.setValue(datePicker.getValue().minusMonths(1));
        setupMonthView();
    }

    @FXML
    public void goToNextMonth() {
        datePicker.setValue(datePicker.getValue().plusMonths(1));
        setupMonthView();
    }

    @FXML
    public void goToCurrentMonth() {
        datePicker.setValue(LocalDate.now());
        setupMonthView();
    }
}

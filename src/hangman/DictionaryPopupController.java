package hangman;

import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class DictionaryPopupController {
    @FXML private PieChart pieChart;
    @FXML public void initialize(Dictionary dict) {
        String[] words = dict.getWords();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("6 Letters",
                Arrays.stream(words).filter(x -> x.length() == 6).collect(Collectors.toList()).size()),
            new PieChart.Data("7-9 Letters", 
                Arrays.stream(words).filter(x -> (x.length() >= 6) && (x.length() <= 9)).collect(Collectors.toList()).size()),
            new PieChart.Data(">9 Letters",
                Arrays.stream(words).filter(x -> x.length() > 9).collect(Collectors.toList()).size())
        );
        pieChart.setData(pieChartData);
    }
}


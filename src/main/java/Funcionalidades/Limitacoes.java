package Funcionalidades;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Limitacoes {

    public static void limitarTextFieldComApenasLetras(TextField textField) {
        textField.textProperty().addListener((obs, valorAntigo, novoValor) -> {
            if (novoValor != null && !novoValor.matches("[A-Za-zãõúáéíóâôê ]*")) {
                textField.setText(valorAntigo);
            }
        });
    }

    public static void limitarTamanhoMaximo(TextField textField, int tamanhoMaximo){
        textField.textProperty().addListener((obs, valorAntigo, novoValor) ->{
            if(novoValor != null && novoValor.length() > tamanhoMaximo){
                textField.setText(valorAntigo);
            }
        });
    }

    public static void limitarTamanhoMaximo(PasswordField passwordField, int tamanhoMaximo){
        passwordField.textProperty().addListener((obs, valorAntigo, novoValor) ->{
            if(novoValor != null && novoValor.length() > tamanhoMaximo){
                passwordField.setText(valorAntigo);
            }
        });
    }

    public static void limitarTamanhoMaximo(DatePicker datePicker, int tamanhoMaximo){
        datePicker.getEditor().textProperty().addListener((obs, valorAntigo, novoValor) ->{
            if(novoValor != null && novoValor.length() > tamanhoMaximo){
                datePicker.getEditor().setText(valorAntigo);
            }
        });
    }

    public static void limitarDatePickerComApenasNumerosBarras(DatePicker datePicker){
        datePicker.getEditor().textProperty().addListener((obs, valorAntigo, novoValor) -> {
            if (novoValor != null && !novoValor.matches("[0-9/]*")) {
                datePicker.getEditor().setText(valorAntigo);
            }
        });
    }

    public static void limitarDatePickerComApenasDatasValidas(DatePicker datePicker){
        datePicker.setConverter(new StringConverter<>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, formatter);
                    } catch (DateTimeParseException e) {
                        datePicker.getEditor().clear();
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });
    }

    public static void limitarDatePickerComDatasAnterioresHoje(DatePicker datePicker){
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isAfter(LocalDate.now())) {
                                    setDisable(true);
                                    setStyle("-fx-border-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        datePicker.setDayCellFactory(dayCellFactory);
    }

    public static void adicionarBarrasAutomaticamente(DatePicker datePicker){
        datePicker.getEditor().textProperty().addListener((observableValue, valorAntigo, novoValor) -> {
            int tamanho = datePicker.getEditor().getText().length();
            if(!datePicker.getEditor().getText().isEmpty() && valorAntigo.length() < novoValor.length()){
                char ultimaLetra = datePicker.getEditor().getText().charAt(datePicker.getEditor().getText().length() - 1);
                if ((tamanho == 2 || tamanho == 5) && '/' != ultimaLetra) {
                    datePicker.getEditor().setText(datePicker.getEditor().getText().concat("/"));
                }
            }
        });
    }
}
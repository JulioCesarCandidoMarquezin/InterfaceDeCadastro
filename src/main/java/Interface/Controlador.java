package Interface;

import DataBase.DataBase;
import Funcionalidades.Limitacoes;
import Funcionalidades.Verificacoes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.Closeable;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controlador implements Initializable, Closeable {

    private final Limitacoes limitacoes = new Limitacoes();

    private final Verificacoes verificacoes = new Verificacoes();

    @FXML
    private TextField nome = null;

    @FXML
    private TextField email = null;

    @FXML
    private PasswordField senha = null;

    @FXML
    private PasswordField confirmarSenha = null;

    @FXML
    private DatePicker dataDeNascimento = new DatePicker();

    @FXML
    protected Button botaoCadastrar;

    private Connection conexao = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        limitacoes.limitarTextFieldComApenasLetras(nome);
        limitacoes.limitarTamanhoMaximo(nome, 50);
        limitacoes.limitarTamanhoMaximo(email, 50);
        limitacoes.limitarTamanhoMaximo(dataDeNascimento, 10);
        limitacoes.limitarTamanhoMaximo(senha, 20);
        limitacoes.limitarTamanhoMaximo(confirmarSenha, 20);
        limitacoes.limitarDatePickerComApenasDatasValidas(dataDeNascimento);
        limitacoes.limitarDatePickerComApenasNumerosBarras(dataDeNascimento);
        limitacoes.limitarDatePickerComDatasAnterioresHoje(dataDeNascimento);
        limitacoes.adicionarBarrasAutomaticamente(dataDeNascimento);
        dataDeNascimento.setShowWeekNumbers(true);
        Thread abrirBancoDeDadosSeparadamente = new Thread(() -> conexao = DataBase.getConnection());
        abrirBancoDeDadosSeparadamente.start();
    }

    @Override
    public void close(){
        DataBase.closeConnection(conexao);
    }

    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecalho, String mensagem){
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.show();
    }

    public void cadastrar() {
        try {
            if (verificacoes.cadastroValido(conexao, nome, email, dataDeNascimento, senha, confirmarSenha)) {
                if(conexao.isClosed()){
                    conexao.beginRequest();
                }
                DataBase.cadastrarUsuario(conexao, nome.getText().trim(), email.getText().trim(), Date.valueOf(dataDeNascimento.getValue()), senha.getText());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cadastro realizado", "", "Usu??rio cadastrado com sucesso!");
                limparFormulario();
            }
            else {
                mostrarAlerta(Alert.AlertType.WARNING, "Informa????es que devem ser alteradas para cadastro", "", verificacoes.getMensagemDeErro());
                verificacoes.setMensagemDeErro("");
            }
        }
        catch (SQLException e){
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "", "Erro ao acessar o banco de dados");
        }
    }

    private void limparFormulario(){
        nome.clear();
        email.clear();
        senha.clear();
        confirmarSenha.clear();
        dataDeNascimento.setValue(null);
    }
}
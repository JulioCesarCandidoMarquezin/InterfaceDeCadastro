package DataBase;

import javafx.scene.control.Alert;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DataBase {

    private static Connection conexao = null;

    private static Properties loadProerties(){
        try{
            FileInputStream fs = new FileInputStream("DataBase.properties");
            Properties properties = new Properties();
            properties.load(fs);
            return properties;
        }
        catch (IOException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    private static Connection getConnection(){
        if(conexao == null){
            try {
                Properties properties = loadProerties();
                String url = properties.getProperty("dburl");
                String usuario = properties.getProperty("usuario");
                String senha = properties.getProperty("senha");
                conexao = DriverManager.getConnection(url, usuario, senha);
            }
            catch (SQLException e){
                new Alert(Alert.AlertType.ERROR, e.getMessage());
            }
        }
        return conexao;
    }

    private static void closeConnection(){
        if (conexao != null){
            try {
                conexao.close();
            }
            catch (SQLException e){
                throw new DataBaseException(e.getMessage());
            }
        }
    }

    public static void cadastrarNovoUsuario(String nome, String email, Date dataNascimento, String senha, String sexo){
        getConnection();
        try {
            PreparedStatement comandoSQL = conexao.prepareStatement(
                    "insert into cadastro "
                    + "(nome, email, dataDeNascimento, senha, sexo) "
                    + "values "
                    + "(?,?,?,?,?)"
            );
            comandoSQL.setString(1, nome);
            comandoSQL.setString(2, email);
            comandoSQL.setDate(3, dataNascimento);
            comandoSQL.setString(4, senha);
            comandoSQL.setString(5, sexo);
            comandoSQL.executeUpdate();
            Alert cadastroRealizado = new Alert(Alert.AlertType.INFORMATION, "Usuario cadastrado com sucesso!");
            cadastroRealizado.setTitle("Cadastro realizado");
            cadastroRealizado.setHeaderText("");
            cadastroRealizado.show();
        }
        catch (SQLException excecaoCadastro) {
            Alert cadastroFalhou = new Alert(Alert.AlertType.ERROR, "Não foi possível cadastrar o usuario!");
            cadastroFalhou.setTitle("Erro ao cadastrar usuário!");
            cadastroFalhou.setHeaderText("");
            cadastroFalhou.show();
        }
        finally {
            closeConnection();
        }
    }

    public static boolean usuarioJaCadastrado(String nome, String email){
        try {
            boolean existeNomeOuEmail = false;
            Statement pesquisaDeNomesEmails = conexao.createStatement();
            ResultSet resultadoDaPesquisa = pesquisaDeNomesEmails.executeQuery("select nome, email from cadastro");
            while(resultadoDaPesquisa.next()){
                if(resultadoDaPesquisa.getString("nome").hashCode() == nome.hashCode()){
                    if(resultadoDaPesquisa.getString("nome").equals(nome)){
                        return !existeNomeOuEmail;
                    }
                }
                if(resultadoDaPesquisa.getString("email").hashCode() == email.hashCode()){
                    if(resultadoDaPesquisa.getString("email").equals(email)){
                        return !existeNomeOuEmail;
                    }
                }
            }
            return existeNomeOuEmail;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
package bmt.ui;

import bmt.model.User;
import bmt.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginView {
    private final UserService userService;
    private final Stage primaryStage;

    public LoginView(Stage primaryStage) {
        this.userService = new UserService();
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setTitle("Kelime Ezberleme Oyunu - Giriş");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Hoş Geldiniz");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Kullanıcı Adı:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Şifre:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Giriş Yap");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Button registerBtn = new Button("Kayıt Ol");
        HBox hbRegisterBtn = new HBox(10);
        hbRegisterBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbRegisterBtn.getChildren().add(registerBtn);
        grid.add(hbRegisterBtn, 0, 4);

        Button forgotPwBtn = new Button("Şifremi Unuttum");
        HBox hbForgotPwBtn = new HBox(10);
        hbForgotPwBtn.setAlignment(Pos.CENTER);
        hbForgotPwBtn.getChildren().add(forgotPwBtn);
        grid.add(hbForgotPwBtn, 0, 5, 2, 1);

        Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(e -> {
            try {
                User user = userService.login(userTextField.getText(), pwBox.getText());
                MainView mainView = new MainView(primaryStage, user);
                mainView.show();
            } catch (RuntimeException ex) {
                actiontarget.setText(ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> {
            RegisterView registerView = new RegisterView(primaryStage);
            registerView.show();
        });

        forgotPwBtn.setOnAction(e -> {
            ForgotPasswordView forgotPasswordView = new ForgotPasswordView(primaryStage);
            forgotPasswordView.show();
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
} 
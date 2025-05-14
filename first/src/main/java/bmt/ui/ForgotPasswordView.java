package bmt.ui;

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

public class ForgotPasswordView {
    private final UserService userService;
    private final Stage primaryStage;

    public ForgotPasswordView(Stage primaryStage) {
        this.userService = new UserService();
        this.primaryStage = primaryStage;
    }

    public void show() {
        primaryStage.setTitle("Kelime Ezberleme Oyunu - Şifremi Unuttum");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Şifre Sıfırlama");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Kullanıcı Adı:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label newPw = new Label("Yeni Şifre:");
        grid.add(newPw, 0, 2);

        PasswordField newPwBox = new PasswordField();
        grid.add(newPwBox, 1, 2);

        Label confirmPw = new Label("Yeni Şifre Tekrar:");
        grid.add(confirmPw, 0, 3);

        PasswordField confirmPwBox = new PasswordField();
        grid.add(confirmPwBox, 1, 3);

        Button btn = new Button("Şifreyi Sıfırla");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Button backBtn = new Button("Geri Dön");
        HBox hbBackBtn = new HBox(10);
        hbBackBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbBackBtn.getChildren().add(backBtn);
        grid.add(hbBackBtn, 0, 4);

        Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String newPassword = newPwBox.getText();
            String confirmPassword = confirmPwBox.getText();

            if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                actiontarget.setText("Lütfen tüm alanları doldurun");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                actiontarget.setText("Şifreler eşleşmiyor");
                return;
            }

            try {
                userService.resetPassword(username, newPassword);
                LoginView loginView = new LoginView(primaryStage);
                loginView.show();
            } catch (RuntimeException ex) {
                actiontarget.setText(ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            loginView.show();
        });

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
    }
} 
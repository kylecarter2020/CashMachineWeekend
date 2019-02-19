package rocks.zipcode.atm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import rocks.zipcode.atm.bank.AccountData;
import rocks.zipcode.atm.bank.Bank;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author ZipCodeWilmington
 */
public class CashMachineApp extends Application {

    private CashMachine cashMachine = new CashMachine(new Bank());

    private Label welcomeLabel = new Label("Welcome to ZipCloudBank");
    private ComboBox accountsComboBox = new ComboBox();


    private int accountNumberTracker = 2000;

    private Parent createContent() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int textFieldWidth = 400;

        accountsComboBox.setPrefWidth(400);
        accountsComboBox.setDisable(true);

        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox vbox = new VBox(50);
        vbox.setPrefSize(800, 600);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);


        Button createAccount = new Button("Create Account");
        createAccount.setPrefSize(buttonWidth, buttonHeight);

        Button login = new Button("Login");
        login.setPrefSize(buttonWidth, buttonHeight);

        Button logout = new Button("Logout");
        logout.setPrefSize(buttonWidth, buttonHeight);

        HBox hBox = new HBox(100);
        hBox.setAlignment(Pos.CENTER);

        Button deposit = new Button("Deposit");
        deposit.setPrefSize(buttonWidth, buttonHeight);
        deposit.setDisable(true);

        Button withdraw = new Button("Withdraw");
        withdraw.setPrefSize(buttonWidth, buttonHeight);
        withdraw.setDisable(true);

        HBox hBox2 = new HBox(100);
        hBox2.setAlignment(Pos.TOP_CENTER);

        Button help = new Button("Help");
        help.setPrefSize(buttonWidth, buttonHeight);

        Button exit = new Button("Exit");
        exit.setPrefSize(buttonWidth, buttonHeight);

        HBox hBox3 = new HBox(500);
        hBox3.setAlignment(Pos.BOTTOM_CENTER);

        hBox.getChildren().addAll(createAccount, login, logout);
        hBox2.getChildren().addAll(deposit, withdraw);
        hBox3.getChildren().addAll(help, exit);

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField accountBalance = new TextField();

        nameField.setPrefWidth(textFieldWidth);
        emailField.setPrefWidth(textFieldWidth);
        accountBalance.setPrefWidth(textFieldWidth);

        nameField.setDisable(true);
        emailField.setDisable(true);
        accountBalance.setDisable(true);
        logout.setDisable(true);

        exit.setOnAction(e -> System.exit(0));

        createAccount.setOnAction(e -> {
            Dialog<Void> dialog = new Dialog();
            dialog.setTitle("New Account");

            //Creating a GridPane container
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(10);
            grid.setHgap(10);
            grid.setAlignment(Pos.CENTER);

            //Defining the Name text field
            final TextField name = new TextField();
            name.setPromptText("Name");
            name.setPrefWidth(textFieldWidth);
            GridPane.setConstraints(name, 0, 0);

            //Defining the Last Name text field
            final TextField email = new TextField();
            email.setPromptText("Email Address");
            name.setPrefWidth(textFieldWidth);
            GridPane.setConstraints(email, 0, 1);

            //Defining the Comment text field
            final TextField initialDeposit = new TextField();
            initialDeposit.setPrefWidth(textFieldWidth);;
            initialDeposit.setPromptText("Opening Deposit");
            GridPane.setConstraints(initialDeposit, 0, 2);

            grid.getChildren().add(name);
            grid.getChildren().add(email);
            grid.getChildren().add(initialDeposit);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

            Optional<Void> result = dialog.showAndWait();

            result. (accountInfo -> {
                accountNumberTracker+=1000;
                cashMachine.addAccount(accountNumberTracker, name.getText(), email.getText(), Integer.parseInt(initialDeposit.getText()));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("New Account Created! Tha account number is" + accountNumberTracker);
            });
        });

        login.setOnAction(e -> {
            //cashMachine
            createAccount.setDisable(true);
            login.setDisable(true);
            logout.setDisable(false);
            accountsComboBox.setDisable(false);

            ObservableList<Integer> menuOptions = FXCollections.observableArrayList();

            for (int i = 1000; i <= accountNumberTracker; i+=1000) {
                menuOptions.add(i);
            }

            accountsComboBox.setItems(menuOptions);

            accountsComboBox.setOnAction(event -> {
                int id = Integer.parseInt(accountsComboBox.getValue().toString());
                cashMachine.login(id);
                float bal = cashMachine.getBalance();
                nameField.setText(cashMachine.getName());
                emailField.setText(cashMachine.getEmail());
                accountBalance.setText(String.format("$%.2f", bal));

                deposit.setDisable(false);
                withdraw.setDisable(false);
            });


        });

        deposit.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Deposit");
            dialog.setHeaderText("Enter Deposit Amount:");
            dialog.setContentText("Deposit:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(amount -> {
                cashMachine.deposit(Integer.parseInt(amount));
                float bal = cashMachine.getBalance();
                accountBalance.setText(String.format("$%.2f", bal));
            });
        });

        withdraw.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Withdraw");
            dialog.setHeaderText("Enter Withdrawal Amount:");
            dialog.setContentText("Withdraw:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(amount -> {
                cashMachine.withdraw(Integer.parseInt(amount));
                float bal = cashMachine.getBalance();
                accountBalance.setText(String.format("$%.2f", bal));
            });
        });

        logout.setOnAction(event -> {
            login.setDisable(false);
            createAccount.setDisable(false);
            accountsComboBox.setDisable(true);
            logout.setDisable(true);
            withdraw.setDisable(true);
            deposit.setDisable(true);
            accountsComboBox.getSelectionModel().clearSelection();
            nameField.setText("");
            emailField.setText("");
            accountBalance.setText("");
        });
//        Button btnExit = new Button("Exit");
//        btnExit.setOnAction(e -> {
//            cashMachine.exit();
//
//            areaInfo.setText(cashMachine.toString());
//        });

        FlowPane flowpane = new FlowPane();
        flowpane.setAlignment(Pos.TOP_CENTER);
        flowpane.setOrientation(Orientation.VERTICAL);
        flowpane.setPrefHeight(300);
        flowpane.setVgap(30);

        flowpane.getChildren().add(accountsComboBox);
        flowpane.getChildren().add(nameField);
        flowpane.getChildren().add(emailField);
        flowpane.getChildren().add(accountBalance);

        vbox.getChildren().addAll(welcomeLabel, hBox, flowpane, hBox2, hBox3);
        return vbox;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

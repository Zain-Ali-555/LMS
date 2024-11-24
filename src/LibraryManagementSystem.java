import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.geometry.HPos;

import javafx.stage.Modality;


import org.mindrot.jbcrypt.BCrypt;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.util.Duration;
import javafx.animation.Animation;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.scene.paint.Color;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import javafx.animation.AnimationTimer;


public class LibraryManagementSystem extends Application {


    @Override
    public void start(Stage primaryStage) {
        // Main Layout
        StackPane mainLayout = new StackPane();

        // Animated Background Canvas
        Canvas animatedCanvas = new Canvas(900, 600);
        GraphicsContext gc = animatedCanvas.getGraphicsContext2D();
        mainLayout.getChildren().add(animatedCanvas);

        // Create the Back-and-Forth Gradient Animation
        startReversingGradientAnimation(gc);

        // Welcome Section
        VBox welcomeSection = new VBox(20);
        welcomeSection.setPadding(new Insets(30));
        welcomeSection.setAlignment(Pos.CENTER);
        welcomeSection.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.6); " +
                        "-fx-border-radius: 15; -fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 25, 0, 0, 5);");

        // Welcome Label
        Label welcomeLabel = new Label("📚 Welcome to Good Books Library!");
        welcomeLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        // Quote Label
        Label quoteLabel = new Label("“A room without books is like a body without a soul.”");
        quoteLabel.setStyle("-fx-font-size: 20px; -fx-font-style: italic; -fx-text-fill: #F8F8FF;");

        // Buttons for Portal Navigation
        Button librarianButton = new Button("👨‍💼 Librarian Portal");
        Button readerButton = new Button("📖 Reader Portal");

        styleButton(librarianButton, "#4CAF50", "#388E3C", "#FFFFFF");
        styleButton(readerButton, "#2196F3", "#1565C0", "#FFFFFF");


        // Button Actions
        librarianButton.setOnAction(e -> openLibrarianChoicePage(primaryStage));
        readerButton.setOnAction(e -> openReaderEntryPage(primaryStage));

        // HBox for Buttons
        HBox buttonSection = new HBox(30, librarianButton, readerButton);
        buttonSection.setAlignment(Pos.CENTER);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024 | All Rights Reserved");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #A9A9A9; -fx-padding: 10;");

        // Combine Welcome Section
        welcomeSection.getChildren().addAll(welcomeLabel, quoteLabel, buttonSection);

        // Add Welcome Section and Footer to Main Layout
        mainLayout.getChildren().addAll(welcomeSection, footerLabel);
        StackPane.setAlignment(footerLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(footerLabel, new Insets(10));

        // Scene Setup
        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startReversingGradientAnimation(GraphicsContext gc) {
        AnimationTimer timer = new AnimationTimer() {
            double offset = 0; // Offset for the gradient animation
            double direction = 0.005; // Controls the direction of animation (positive or negative)

            @Override
            public void handle(long now) {
                offset += direction;

                // Reverse the direction if the offset exceeds bounds
                if (offset >= 1 || offset <= 0) {
                    direction = -direction; // Reverse the animation
                }

                // Create a dynamic gradient
                gc.setFill(new LinearGradient(
                        0, 0, 1, 1, true,
                        javafx.scene.paint.CycleMethod.NO_CYCLE,
                        new Stop(0, interpolateColor(Color.web("#0A192F"), Color.web("#1E90FF"), offset)),
                        new Stop(0.5, interpolateColor(Color.web("#1E90FF"), Color.web("#001B48"), offset)),
                        new Stop(1, interpolateColor(Color.web("#001B48"), Color.web("#0A192F"), offset))
                ));

                // Fill the canvas with the gradient
                gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
            }
        };
        timer.start();
    }

    private void styleButton(Button button, String bgColor, String hoverColor, String textColor) {
        button.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-radius: 10; -fx-background-radius: 10;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; " +
                "-fx-text-fill: " + textColor + "; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10 20; -fx-border-radius: 10; -fx-background-radius: 10;"));

        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + bgColor + "; " +
                "-fx-text-fill: " + textColor + "; -fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-padding: 10 20; -fx-border-radius: 10; -fx-background-radius: 10;"));
    }

    private Color interpolateColor(Color startColor, Color endColor, double fraction) {
        double red = clamp(startColor.getRed() + (endColor.getRed() - startColor.getRed()) * fraction);
        double green = clamp(startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * fraction);
        double blue = clamp(startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * fraction);
        return new Color(red, green, blue, 1.0);
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    // ************** Librarian **************

    private void openLibrarianChoicePage(Stage stage) {
        // Main Layout
        VBox choiceLayout = new VBox(30);
        choiceLayout.setAlignment(Pos.CENTER);
        choiceLayout.setPadding(new Insets(30));
        choiceLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #EAF2F8, #D5DBDB); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label choiceLabel = new Label("Librarian Portal");
        choiceLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; -fx-padding: 10;");

        Label subtitleLabel = new Label("Access your librarian tools below:");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-text-fill: #5D6D7E;");

        VBox titleSection = new VBox(5, choiceLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Buttons Section
        HBox buttonSection = new HBox(20);
        buttonSection.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Button backButton = new Button("Back");

        // Style the buttons
        styleButton(loginButton, "#0078D4", "#0056A3", "#FFFFFF");
        styleButton(registerButton, "#28A745", "#218838", "#FFFFFF");
        styleButton(backButton, "#DC3545", "#C82333", "#FFFFFF");

        // Add buttons to HBox
        buttonSection.getChildren().addAll(loginButton, registerButton, backButton);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024 | All Rights Reserved");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #707070; -fx-padding: 10;");

        // Combine sections
        choiceLayout.getChildren().addAll(titleSection, buttonSection, footerLabel);

        // Scene Setup
        Scene choiceScene = new Scene(choiceLayout, 900, 600);
        stage.setScene(choiceScene);

        // Button Actions
        loginButton.setOnAction(e -> openLibrarianLoginPage(stage));
        registerButton.setOnAction(e -> openLibrarianRegisterPage(stage));
        backButton.setOnAction(e -> start(stage));
    }

    private void openLibrarianRegisterPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #D7CCC8, #FBE9E7); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("Librarian Signup");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");
        Label subtitleLabel = new Label("Create your librarian account");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-font-style: italic; -fx-text-fill: #424242;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Signup Form
        GridPane signupForm = new GridPane();
        signupForm.setPadding(new Insets(30));
        signupForm.setHgap(20);
        signupForm.setVgap(20);
        signupForm.setAlignment(Pos.CENTER); // Center the entire GridPane

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        passwordField.setPrefWidth(300);

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        nameField.setPrefWidth(300);

        // Align labels and fields to center alignment
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);
        GridPane.setHalignment(nameLabel, HPos.RIGHT);

        signupForm.add(usernameLabel, 0, 0);
        signupForm.add(usernameField, 1, 0);
        signupForm.add(passwordLabel, 0, 1);
        signupForm.add(passwordField, 1, 1);
        signupForm.add(nameLabel, 0, 2);
        signupForm.add(nameField, 1, 2);

        // Buttons Section
        HBox buttonSection = new HBox(40);
        buttonSection.setAlignment(Pos.CENTER);

        Button signupButton = new Button("Signup");
        styleResponsiveButton(signupButton, "#28A745", "#218838", "#FFFFFF");

        Button backButton = new Button("Back");
        styleResponsiveButton(backButton, "#DC3545", "#C82333", "#FFFFFF");

        buttonSection.getChildren().addAll(signupButton, backButton);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, signupForm, buttonSection, footerLabel);

        // Scene Setup
        Scene signupScene = new Scene(mainLayout, 900, 700); // Slightly taller for larger fields
        stage.setScene(signupScene);

        // Button Actions
        signupButton.setOnAction(e -> {
            if (registerLibrarian(usernameField.getText(), passwordField.getText(), nameField.getText())) {
                showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Account created successfully.");
                openLibrarianChoicePage(stage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "Could not create account. Please try again.");
            }
        });

        backButton.setOnAction(e -> openLibrarianChoicePage(stage));
    }

    private boolean registerLibrarian(String username, String password, String name) {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return false;
        }

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                String query = "INSERT INTO librarians (username, password, name) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, hashedPassword); // Save hashed password
                statement.setString(3, name);

                int rowsInserted = statement.executeUpdate();
                statement.close();
                connection.close();

                return rowsInserted > 0;
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    showAlert(Alert.AlertType.ERROR, "Signup Failed", "Username already exists.");
                } else {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void openLibrarianLoginPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #E3F2FD, #BBDEFB); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("Librarian Login");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");
        Label subtitleLabel = new Label("Access your library tools");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-font-style: italic; -fx-text-fill: #424242;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Login Form
        GridPane loginForm = new GridPane();
        loginForm.setPadding(new Insets(30));
        loginForm.setHgap(20);
        loginForm.setVgap(20);
        loginForm.setAlignment(Pos.CENTER); // Center the entire GridPane

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        passwordField.setPrefWidth(300);

        // Align labels and fields within their cells
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);

        loginForm.add(usernameLabel, 0, 0);
        loginForm.add(usernameField, 1, 0);
        loginForm.add(passwordLabel, 0, 1);
        loginForm.add(passwordField, 1, 1);

        // Buttons Section
        HBox buttonSection = new HBox(40);
        buttonSection.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        styleResponsiveButton(loginButton, "#1976D2", "#1565C0", "#FFFFFF");

        Button backButton = new Button("Back");
        styleResponsiveButton(backButton, "#E53935", "#D32F2F", "#FFFFFF");

        buttonSection.getChildren().addAll(loginButton, backButton);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, loginForm, buttonSection, footerLabel);

        // Scene Setup
        Scene loginScene = new Scene(mainLayout, 900, 700); // Increased height for larger elements
        stage.setScene(loginScene);

        // Button Actions
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Both fields are required.");
                return;
            }

            if (validateLibrarianLogin(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Librarian!");
                openLibrarianSection(stage); // Proceed to librarian dashboard
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });

        backButton.setOnAction(e -> openLibrarianChoicePage(stage));
    }

    private boolean validateLibrarianLogin(String username, String password) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT password FROM librarians WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String storedHashedPassword = resultSet.getString("password");
                    if (BCrypt.checkpw(password, storedHashedPassword)) {
                        resultSet.close();
                        statement.close();
                        connection.close();
                        return true; // Successful login
                    }
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false; // Login failed
    }

    // New
    private void openLibrarianSection(Stage stage) {
        VBox librarianSection = new VBox(20);
        librarianSection.setPadding(new Insets(30));
        librarianSection.setAlignment(Pos.CENTER);
        librarianSection.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");

        Label titleLabel = new Label("Librarian Dashboard");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);");

        Button addBookButton = createStyledButtonWithIcon("Add Book", "📚");
        Button viewBooksButton = createStyledButtonWithIcon("View Books", "📖");
        Button updateBookButton = createStyledButtonWithIcon("Update Book", "✏️");
        Button removeBookButton = createStyledButtonWithIcon("Remove Book", "❌");
        Button ordersButton = createStyledButtonWithIcon("Orders", "🛒");
        Button issuedBooksButton = createStyledButtonWithIcon("Issued Books", "📤");
        Button exitButton = createStyledButtonWithIcon("Exit", "🔙", "#E74C3C", "#C0392B");

        // Set width for all buttons
        double buttonWidth = 200.0;
        addBookButton.setPrefWidth(buttonWidth);
        viewBooksButton.setPrefWidth(buttonWidth);
        updateBookButton.setPrefWidth(buttonWidth);
        removeBookButton.setPrefWidth(buttonWidth);
        ordersButton.setPrefWidth(buttonWidth);
        issuedBooksButton.setPrefWidth(buttonWidth);

        exitButton.setPrefWidth(150.0);

        librarianSection.getChildren().addAll(
                titleLabel, addBookButton, viewBooksButton, updateBookButton,
                removeBookButton, ordersButton, issuedBooksButton, exitButton);

        // Check for pending orders and apply highlight effect if needed
        if (!fetchPendingOrders().isEmpty()) {
            highlightOrderButton(ordersButton);
        }

        Scene librarianScene = new Scene(librarianSection, 900, 600);
        stage.setScene(librarianScene);

        // Button Actions
        addBookButton.setOnAction(e -> openAddBookPage(stage));
        viewBooksButton.setOnAction(e -> openViewBooksPage(stage));
        updateBookButton.setOnAction(e -> openUpdateBookPage(stage));
        removeBookButton.setOnAction(e -> openDeleteBookPage(stage));
        ordersButton.setOnAction(e -> {
            openOrdersPage(stage);
            removeHighlightEffect(ordersButton); // Stop highlight effect when the orders are viewed
        });
        issuedBooksButton.setOnAction(e -> openIssuedBooksPage(stage));
        exitButton.setOnAction(e -> start(stage));
    }

    private void highlightOrderButton(Button orderButton) {
        // Set fixed height and width
        double buttonWidth = 200.0;
        double buttonHeight = 40.0;
        orderButton.setPrefWidth(buttonWidth);
        orderButton.setPrefHeight(buttonHeight);

        // Color transition for background
        Timeline colorTransition = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(orderButton.styleProperty(),
                                "-fx-background-color: #3498DB; -fx-text-fill: white; " +
                                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                                        "-fx-border-radius: 10; -fx-background-radius: 10; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1); -fx-padding: 12 24;")),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(orderButton.styleProperty(),
                                "-fx-background-color: yellow; -fx-text-fill: black; " +
                                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                                        "-fx-border-radius: 10; -fx-background-radius: 10; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1); -fx-padding: 12 24;")),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(orderButton.styleProperty(),
                                "-fx-background-color: #3498DB; -fx-text-fill: white; " +
                                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                                        "-fx-border-radius: 10; -fx-background-radius: 10; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1); -fx-padding: 12 24;"))
        );
        colorTransition.setCycleCount(Animation.INDEFINITE); // Repeat indefinitely

        // Start the transition
        colorTransition.play();

        // Store the transition for later stopping
        orderButton.setUserData(colorTransition);
    }

    private void removeHighlightEffect(Button orderButton) {
        if (orderButton.getUserData() instanceof Timeline) {
            Timeline colorTransition = (Timeline) orderButton.getUserData();
            colorTransition.stop(); // Stop the animation
        }
        // Reset to original styles
        orderButton.setStyle(
                "-fx-background-color: #3498DB; -fx-text-fill: white; " +
                        "-fx-font-size: 16px; -fx-font-weight: bold; " +
                        "-fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);"
        );
        // Reset to original size
        orderButton.setPrefWidth(200.0);
        orderButton.setPrefHeight(40.0);
    }

    // Utility method to create styled buttons with icons
    private Button createStyledButtonWithIcon(String text, String icon) {
        return createStyledButtonWithIcon(text, icon, "#3498DB", "#2980B9");
    }

    private Button createStyledButtonWithIcon(String text, String icon, String baseColor, String hoverColor) {
        Button button = new Button(icon + " " + text);
        button.setStyle(String.format(
                "-fx-padding: 12 24; -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);",
                baseColor));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-padding: 12 24; -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 4, 0, 2, 2);",
                hoverColor)));
        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-padding: 12 24; -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);",
                baseColor)));

        return button;
    }

    // New

    private void openAddBookPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #E3F2FD, #BBDEFB); " +
                "-fx-border-radius: 20; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📚 Add a New Book");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("Fill in the details below to add a book to the library.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #5D6D7E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Form Layout
        GridPane formLayout = new GridPane();
        formLayout.setPadding(new Insets(20));
        formLayout.setHgap(15);
        formLayout.setVgap(15);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 2, 2);");

        // Form Fields
        Label isbnLabel = new Label("ISBN:");
        isbnLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        TextField isbnField = new TextField();
        isbnField.setPromptText("13-digit ISBN");

        Label bookNameLabel = new Label("Book Name:");
        bookNameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        TextField bookNameField = new TextField();
        bookNameField.setPromptText("Enter book name");

        Label authorLabel = new Label("Author:");
        authorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        TextField authorField = new TextField();
        authorField.setPromptText("Enter author name");

        Label genreLabel = new Label("Genre:");
        genreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        TextField genreField = new TextField();
        genreField.setPromptText("Enter genre");

        formLayout.add(isbnLabel, 0, 0);
        formLayout.add(isbnField, 1, 0);
        formLayout.add(bookNameLabel, 0, 1);
        formLayout.add(bookNameField, 1, 1);
        formLayout.add(authorLabel, 0, 2);
        formLayout.add(authorField, 1, 2);
        formLayout.add(genreLabel, 0, 3);
        formLayout.add(genreField, 1, 3);

        // Buttons Section
        HBox buttonSection = new HBox(20);
        buttonSection.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add Book");
        addButton.setStyle("-fx-padding: 10 30; -fx-background-color: #3498DB; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");
        addButton.setOnMouseEntered(e -> addButton.setStyle("-fx-padding: 10 30; -fx-background-color: #2980B9; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));
        addButton.setOnMouseExited(e -> addButton.setStyle("-fx-padding: 10 30; -fx-background-color: #3498DB; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #E74C3C; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #C0392B; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #E74C3C; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));
        backButton.setOnAction(e -> openLibrarianSection(stage));

        buttonSection.getChildren().addAll(addButton, backButton);

        // Add functionality to the Add button
        addButton.setOnAction(e -> {
            String isbn = isbnField.getText();
            String bookName = bookNameField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();

            if (isbn.isEmpty() || bookName.isEmpty() || author.isEmpty() || genre.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
                return;
            }

            if (isbn.length() != 13 || !isbn.matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "ISBN must be a 13-digit number.");
                return;
            }

            if (addBookToDatabase(isbn, bookName, author, genre)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book successfully added.");
                openLibrarianSection(stage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add the book.");
            }
        });

        // Combine all sections
        mainLayout.getChildren().addAll(titleSection, formLayout, buttonSection);

        // Scene Setup
        Scene addBookScene = new Scene(mainLayout, 900, 600);
        stage.setScene(addBookScene);
    }

    private boolean addBookToDatabase(String isbn, String bookName, String author, String genre) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO books (isbn, book_name, author, genre) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, isbn);
                statement.setString(2, bookName);
                statement.setString(3, author);
                statement.setString(4, genre);

                int rowsInserted = statement.executeUpdate();
                statement.close();
                connection.close();

                return rowsInserted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void openViewBooksPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #F3F9FB, #A9D5E4); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📖 View Books");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("Browse the list of books available in the library.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #5D6D7E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Table Section
        TableView<Book> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; -fx-border-radius: 10; -fx-background-radius: 10;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Columns auto-resize

        // Define Table Columns
        TableColumn<Book, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> colBookName = new TableColumn<>("Book Name");
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));

        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        tableView.getColumns().addAll(colIsbn, colBookName, colAuthor, colGenre);

        // Fetch books from the database
        ObservableList<Book> bookData = FXCollections.observableArrayList();
        fetchBooksFromDatabase(bookData); // Populate the ObservableList
        tableView.setItems(bookData);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #2980B9; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #3498DB; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));
        backButton.setOnAction(e -> openLibrarianSection(stage));

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, tableView, backButton);

        // Scene Setup
        Scene viewBooksScene = new Scene(mainLayout, 900, 600);
        stage.setScene(viewBooksScene);
    }

    // Fetch books from the database and add them to the VBox

    public static class Book {
        private String isbn;
        private String bookName;
        private String author;
        private String genre;

        public Book(String isbn, String bookName, String author, String genre) {
            this.isbn = isbn;
            this.bookName = bookName;
            this.author = author;
            this.genre = genre;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getBookName() {
            return bookName;
        }

        public String getAuthor() {
            return author;
        }

        public String getGenre() {
            return genre;
        }
    }

    private void fetchBooksFromDatabase(ObservableList<Book> bookData) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT isbn, book_name, author, genre FROM books";  // Ensure correct table and column names
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                // Populate the ObservableList with data from the database
                while (resultSet.next()) {
                    bookData.add(new Book(
                            resultSet.getString("isbn"),
                            resultSet.getString("book_name"),
                            resultSet.getString("author"),
                            resultSet.getString("genre")
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load books from the database.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Database connection is not available.");
        }
    }

    // Librarian Update Page

    private void openUpdateBookPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #EAF2F8, #D6EAF8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📖 Update Book Details");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("Search for a book and update its details below.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #5D6D7E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Search Section
        Label instructionLabel = new Label("Search a Book:");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        HBox searchFields = new HBox(10);
        searchFields.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Book Name or ISBN");
        searchField.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-padding: 8;");

        Button searchButton = new Button("Search");
        Update_styleButton(searchButton, "#3498DB", "#2980B9", "white");

        searchFields.getChildren().addAll(searchField, searchButton);

        // Results Section
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #16A085;");

        // Update Options
        Label updateOptionsLabel = new Label("Select Fields to Update:");
        updateOptionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        HBox checkBoxFields = new HBox(15);
        checkBoxFields.setAlignment(Pos.CENTER);

        CheckBox updateBookName = new CheckBox("Book Name");
        updateBookName.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        CheckBox updateAuthor = new CheckBox("Author");
        updateAuthor.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");
        CheckBox updateGenre = new CheckBox("Genre");
        updateGenre.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

        checkBoxFields.getChildren().addAll(updateBookName, updateAuthor, updateGenre);

        // Update Fields Section
        TextField bookNameField = new TextField();
        bookNameField.setPromptText("New Book Name");
        bookNameField.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-padding: 8;");
        bookNameField.setVisible(false);

        TextField authorField = new TextField();
        authorField.setPromptText("New Author");
        authorField.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-padding: 8;");
        authorField.setVisible(false);

        TextField genreField = new TextField();
        genreField.setPromptText("New Genre");
        genreField.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-padding: 8;");
        genreField.setVisible(false);

        VBox updateFields = new VBox(15, bookNameField, authorField, genreField);
        updateFields.setAlignment(Pos.CENTER);

        // Bind visibility of fields to checkboxes
        updateBookName.setOnAction(e -> bookNameField.setVisible(updateBookName.isSelected()));
        updateAuthor.setOnAction(e -> authorField.setVisible(updateAuthor.isSelected()));
        updateGenre.setOnAction(e -> genreField.setVisible(updateGenre.isSelected()));

        // Buttons Section
        HBox buttonSection = new HBox(20);
        buttonSection.setAlignment(Pos.CENTER);

        Button updateButton = new Button("Update Book");
        Update_styleButton(updateButton, "#27AE60", "#229954", "white");
        updateButton.setDisable(true); // Disable until a search is performed

        Button backButton = new Button("Back");
        Update_styleButton(backButton, "#E74C3C", "#C0392B", "white");
        backButton.setOnAction(e -> openLibrarianSection(stage));

        buttonSection.getChildren().addAll(updateButton, backButton);

        // Combine All Sections
        mainLayout.getChildren().addAll(
                titleSection,
                instructionLabel,
                searchFields,
                resultLabel,
                updateOptionsLabel,
                checkBoxFields,
                updateFields,
                buttonSection
        );

        // Scene Setup
        Scene updateBookScene = new Scene(mainLayout, 900, 600);
        stage.setScene(updateBookScene);

        // Search Button Logic
        searchButton.setOnAction(e -> {
            String searchText = searchField.getText();
            if (searchText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a value to search.");
                return;
            }

            Book foundBook = searchBookInDatabase(searchText);
            if (foundBook != null) {
                resultLabel.setText("Book Found: " + foundBook.getBookName());
                updateButton.setDisable(false); // Enable update button
            } else {
                resultLabel.setText("Book not found.");
                updateButton.setDisable(true);
            }
        });

        // Update Button Logic
        updateButton.setOnAction(e -> {
            String newBookName = updateBookName.isSelected() ? bookNameField.getText() : null;
            String newAuthor = updateAuthor.isSelected() ? authorField.getText() : null;
            String newGenre = updateGenre.isSelected() ? genreField.getText() : null;

            if (updateBookInDatabase(searchField.getText(), newBookName, newAuthor, newGenre)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book details updated successfully.");
                openLibrarianSection(stage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book.");
            }
        });
    }

    private void Update_styleButton(Button button, String baseColor, String hoverColor, String textColor) {
        button.setStyle(String.format(
                "-fx-padding: 10 30; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 14px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;", baseColor, textColor));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-padding: 10 30; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 14px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;", hoverColor, textColor)));

        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-padding: 10 30; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 14px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;", baseColor, textColor)));
    }


    // Update the book in the database
    private boolean updateBookInDatabase(String searchValue, String bookName, String author, String genre) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                StringBuilder query = new StringBuilder("UPDATE books SET ");
                boolean addComma = false;

                if (bookName != null) {
                    query.append("book_name = ?");
                    addComma = true;
                }

                if (author != null) {
                    if (addComma) query.append(", ");
                    query.append("author = ?");
                    addComma = true;
                }

                if (genre != null) {
                    if (addComma) query.append(", ");
                    query.append("genre = ?");
                }

                query.append(" WHERE isbn = ? OR book_name = ?");

                PreparedStatement statement = connection.prepareStatement(query.toString());

                int paramIndex = 1;
                if (bookName != null) statement.setString(paramIndex++, bookName);
                if (author != null) statement.setString(paramIndex++, author);
                if (genre != null) statement.setString(paramIndex++, genre);
                statement.setString(paramIndex++, searchValue);
                statement.setString(paramIndex, searchValue);

                int rowsUpdated = statement.executeUpdate();
                statement.close();
                connection.close();

                return rowsUpdated > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Delete Book

    private void openDeleteBookPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #FDEDEC, #F5B7B1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("🗑️ Delete Book");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #A93226;");
        Label subtitleLabel = new Label("Search for a book and delete it from the library.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #6C3483;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Search Section
        Label instructionLabel = new Label("Search a Book to Delete:");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        HBox searchFields = new HBox(15);
        searchFields.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Enter Book Name or ISBN");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: white; " +
                "-fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");

        Button searchButton = new Button("Search");
        styleButton(searchButton, "#3498DB", "#2980B9", "white");

        searchFields.getChildren().addAll(searchField, searchButton);

        // Results Section
        Label resultLabel = new Label("");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #16A085;");

        // Delete Button
        Button deleteButton = new Button("Delete Book");
        styleButton(deleteButton, "#E74C3C", "#C0392B", "white");
        deleteButton.setDisable(true); // Disable until a book is found

        // Back Button
        Button backButton = new Button("Back");
        styleButton(backButton, "#3498DB", "#2980B9", "white");
        backButton.setOnAction(e -> openLibrarianSection(stage));

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, instructionLabel, searchFields, resultLabel, deleteButton, backButton);

        // Scene Setup
        Scene deleteBookScene = new Scene(mainLayout, 900, 600);
        stage.setScene(deleteBookScene);

        // Search Button Logic
        searchButton.setOnAction(e -> {
            String searchText = searchField.getText();
            if (searchText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a value to search.");
                return;
            }

            Book foundBook = searchBookInDatabase(searchText);
            if (foundBook != null) {
                resultLabel.setText("Book Found: " + foundBook.getBookName() + " by " + foundBook.getAuthor());
                deleteButton.setDisable(false); // Enable delete button
            } else {
                resultLabel.setText("Book not found.");
                deleteButton.setDisable(true);
            }
        });

        // Delete Button Logic
        deleteButton.setOnAction(e -> {
            String searchText = searchField.getText();
            if (deleteBookFromDatabase(searchText)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully.");
                openLibrarianSection(stage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete the book.");
            }
        });
    }

    // Delete a book from the database
    private boolean deleteBookFromDatabase(String searchValue) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "DELETE FROM books WHERE isbn = ? OR book_name = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, searchValue);
                statement.setString(2, searchValue);

                int rowsDeleted = statement.executeUpdate();
                statement.close();
                connection.close();

                return rowsDeleted > 0;  // Return true if a row was deleted
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Search a book by name or ISBN
    private Book searchBookInDatabase(String searchValue) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM books WHERE isbn = ? OR book_name = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, searchValue);
                statement.setString(2, searchValue);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return new Book(
                            resultSet.getString("isbn"),
                            resultSet.getString("book_name"),
                            resultSet.getString("author"),
                            resultSet.getString("genre")
                    );
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // ******** Orders Page ********

    private void openOrdersPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #EAF2F8, #D6EAF8); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📋 Manage Pending Orders");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("Review and take action on pending book orders.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #5D6D7E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Table Section
        TableView<Order> ordersTable = new TableView<>();
        ordersTable.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; -fx-border-radius: 10; -fx-background-radius: 10;");
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define Table Columns
        TableColumn<Order, String> colReader = new TableColumn<>("Reader Name");
        colReader.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colReader.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, String> colBook = new TableColumn<>("Book Name");
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBook.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colIssueDate = new TableColumn<>("Issue Date");
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssueDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Action Column with Buttons
        TableColumn<Order, Void> colActions = new TableColumn<>("Actions");
        colActions.setStyle("-fx-alignment: CENTER;");
        colActions.setCellFactory(tc -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");

            {
                Order_styleButton(approveButton, "#27AE60", "#229954");
                Order_styleButton(rejectButton, "#E74C3C", "#C0392B");

                approveButton.setOnAction(e -> approveOrder(getTableRow().getItem(), stage));
                rejectButton.setOnAction(e -> rejectOrder(getTableRow().getItem(), stage));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actionBox = new HBox(10, approveButton, rejectButton);
                    actionBox.setAlignment(Pos.CENTER);
                    setGraphic(actionBox);
                }
            }
        });

        // Populate Table
        ordersTable.getColumns().addAll(colReader, colBook, colIssueDate, colReturnDate, colActions);
        ObservableList<Order> orderData = FXCollections.observableArrayList(fetchPendingOrders());
        ordersTable.setItems(orderData);

        // Back Button
        Button backButton = new Button("Back");
        Order_styleButton(backButton, "#3498DB", "#2980B9");
        backButton.setOnAction(e -> openLibrarianSection(stage));

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, ordersTable, backButton);

        // Scene Setup
        Scene ordersScene = new Scene(mainLayout, 900, 600);
        stage.setScene(ordersScene);
    }

    private void Order_styleButton(Button button, String baseColor, String hoverColor) {
        String textColor = "white";

        button.setStyle(String.format(
                "-fx-padding: 5 15; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 12px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;", baseColor, textColor));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-padding: 5 15; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 12px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;", hoverColor, textColor)));

        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-padding: 5 15; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 12px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;", baseColor, textColor)));
    }

    private ObservableList<Order> fetchPendingOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM orders WHERE status = 'Pending'";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    orders.add(new Order(
                            resultSet.getInt("order_id"),
                            resultSet.getString("reader_name"),
                            resultSet.getString("book_name"),
                            resultSet.getDate("issue_Date").toLocalDate(),
                            resultSet.getDate("return_date").toLocalDate()
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return orders;
    }

    private void approveOrder(Order order, Stage currentStage) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                // Approve the order and set issue date
                String updateOrderQuery = "UPDATE orders SET status = 'Approved', issue_date = CURDATE() WHERE order_id = ?";
                PreparedStatement orderStatement = connection.prepareStatement(updateOrderQuery);
                orderStatement.setInt(1, order.getOrderId());
                orderStatement.executeUpdate();

                // Remove the book from the available books
                String removeBookQuery = "DELETE FROM books WHERE book_name = ? OR isbn = ?";
                PreparedStatement bookStatement = connection.prepareStatement(removeBookQuery);
                bookStatement.setString(1, order.getBookName());
                bookStatement.setString(2, order.getBookName()); // Assuming ISBN might be used as well
                bookStatement.executeUpdate();

                orderStatement.close();
                bookStatement.close();
                connection.close();

                showAlert(Alert.AlertType.INFORMATION, "Order Approved", "Order has been approved successfully.");
                openOrdersPage(currentStage); // Refresh the orders page
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to approve order.");
            }
        }
    }


    private void rejectOrder(Order order, Stage currentStage) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "UPDATE orders SET status = 'Rejected' WHERE order_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, order.getOrderId());
                statement.executeUpdate();
                statement.close();
                connection.close();

                showAlert(Alert.AlertType.INFORMATION, "Order Rejected", "Order has been rejected.");
                openOrdersPage(currentStage); // Refresh the page
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ******** Issued Books Page ********

    private void openIssuedBooksPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #F4F6F6, #AED6F1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📚 Issued Books");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("View the list of books currently issued.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: #5D6D7E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Table Section
        TableView<Order> issuedBooksTable = new TableView<>();
        issuedBooksTable.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; -fx-border-radius: 10; -fx-background-radius: 10;");
        issuedBooksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define Table Columns
        TableColumn<Order, String> colReader = new TableColumn<>("Reader Name");
        colReader.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colReader.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, String> colBook = new TableColumn<>("Book Name");
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBook.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colIssueDate = new TableColumn<>("Issue Date");
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssueDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Populate Table
        issuedBooksTable.getColumns().addAll(colReader, colBook, colIssueDate, colReturnDate);
        ObservableList<Order> issuedBooksData = FXCollections.observableArrayList(fetchIssuedBooks());
        issuedBooksTable.setItems(issuedBooksData);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #2980B9; -fx-text-fill: white;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-padding: 10 30; -fx-background-color: #3498DB; -fx-text-fill: white;"));
        backButton.setOnAction(e -> openLibrarianSection(stage));

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, issuedBooksTable, backButton);

        // Scene Setup
        Scene issuedBooksScene = new Scene(mainLayout, 900, 600);
        stage.setScene(issuedBooksScene);
    }

    private ObservableList<Order> fetchIssuedBooks() {
        ObservableList<Order> issuedBooks = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM orders WHERE status = 'Approved'";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    issuedBooks.add(new Order(
                            resultSet.getInt("order_id"),
                            resultSet.getString("reader_name"),
                            resultSet.getString("book_name"),
                            resultSet.getDate("issue_Date").toLocalDate(),
                            resultSet.getDate("return_date").toLocalDate()
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return issuedBooks;
    }

    // ******** Order Class for TableView ********

    public static class Order {
        private int orderId;
        private String readerName;
        private String bookName;
        private LocalDate issueDate; // Needed for reader's borrowed books section
        private LocalDate returnDate;

        public Order(int orderId, String readerName, String bookName, LocalDate issueDate, LocalDate returnDate) {
            this.orderId = orderId;
            this.readerName = readerName;
            this.bookName = bookName;
            this.issueDate = issueDate;
            this.returnDate = returnDate;
        }

        public int getOrderId() {
            return orderId;
        }

        public String getReaderName() {
            return readerName;
        }

        public String getBookName() {
            return bookName;
        }

        public LocalDate getIssueDate() { // Ensure this getter exists
            return issueDate;
        }

        public LocalDate getReturnDate() {
            return returnDate;
        }
    }


    // ************** Reader **************

    // Reader Section Entry Point
    private void openReaderEntryPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #EAF2F8, #D5DBDB); " +
                "-fx-border-radius: 20; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📚 Reader Portal");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label subtitleLabel = new Label("Manage your library account with ease.");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #5D6D7E; -fx-font-style: italic;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Reader Info Section
        VBox readerInfoSection = new VBox(20);
        readerInfoSection.setPadding(new Insets(20));
        readerInfoSection.setAlignment(Pos.CENTER);
        readerInfoSection.setStyle("-fx-background-color: linear-gradient(to bottom right, #85C1E9, #F8F9F9); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 2, 2);");

        Label readerInfoLabel = new Label("Why Use the Reader Portal?");
        readerInfoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1F618D;");

        Label infoDetails = new Label("Access your borrowed books, pending orders, and much more! Log in or sign up to start your journey.");
        infoDetails.setWrapText(true);
        infoDetails.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495E; -fx-padding: 0 15;");

        readerInfoSection.getChildren().addAll(readerInfoLabel, infoDetails);

        // Buttons for Login, Signup, and Back
        HBox buttonSection = new HBox(30);
        buttonSection.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        Button signupButton = new Button("Signup");
        Button backButton = new Button("Back");

        styleButton(loginButton, "#0078D4", "#0056A3", "#FFFFFF");
        styleButton(signupButton, "#28A745", "#218838", "#FFFFFF");
        styleButton(backButton, "#DC3545", "#C82333", "#FFFFFF");

        buttonSection.getChildren().addAll(loginButton, signupButton, backButton);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024 | All Rights Reserved");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5D6D7E; -fx-padding: 10;");

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, readerInfoSection, buttonSection, footerLabel);

        // Scene Setup
        Scene readerEntryScene = new Scene(mainLayout, 900, 600);
        stage.setScene(readerEntryScene);

        // Button Actions
        loginButton.setOnAction(e -> openReaderLoginPage(stage));
        signupButton.setOnAction(e -> openReaderSignupPage(stage));
        backButton.setOnAction(e -> start(stage));
    }

    // Reader SignUp Page
    private void openReaderSignupPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #D7CCC8, #FBE9E7); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("Reader Signup");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");
        Label subtitleLabel = new Label("Create your library account");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-font-style: italic; -fx-text-fill: #424242;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Signup Form
        GridPane signupForm = new GridPane();
        signupForm.setPadding(new Insets(30));
        signupForm.setHgap(20);
        signupForm.setVgap(20);
        signupForm.setAlignment(Pos.CENTER); // Center the entire GridPane

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        passwordField.setPrefWidth(300);

        Label nameLabel = new Label("Name:");
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        nameField.setPrefWidth(300);

        Label deptLabel = new Label("Department:");
        deptLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField deptField = new TextField();
        deptField.setPromptText("Enter your department");
        deptField.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        deptField.setPrefWidth(300);

        // Align labels and fields to the center of their cells
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);
        GridPane.setHalignment(nameLabel, HPos.RIGHT);
        GridPane.setHalignment(deptLabel, HPos.RIGHT);

        signupForm.add(usernameLabel, 0, 0);
        signupForm.add(usernameField, 1, 0);
        signupForm.add(passwordLabel, 0, 1);
        signupForm.add(passwordField, 1, 1);
        signupForm.add(nameLabel, 0, 2);
        signupForm.add(nameField, 1, 2);
        signupForm.add(deptLabel, 0, 3);
        signupForm.add(deptField, 1, 3);

        // Buttons Section
        HBox buttonSection = new HBox(40);
        buttonSection.setAlignment(Pos.CENTER);

        Button signupButton = new Button("Signup");
        styleSignupButton(signupButton, "#28A745", "#218838", "#FFFFFF");

        Button backButton = new Button("Back");
        styleSignupButton(backButton, "#DC3545", "#C82333", "#FFFFFF");

        buttonSection.getChildren().addAll(signupButton, backButton);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, signupForm, buttonSection, footerLabel);

        // Scene Setup
        Scene signupScene = new Scene(mainLayout, 900, 700); // Slightly taller for larger fields
        stage.setScene(signupScene);

        // Button Actions
        signupButton.setOnAction(e -> {
            if (registerReader(usernameField.getText(), passwordField.getText(), nameField.getText(), deptField.getText())) {
                showAlert(Alert.AlertType.INFORMATION, "Signup Successful", "Account created successfully.");
                openReaderLoginPage(stage);
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "Could not create account. Please try again.");
            }
        });

        backButton.setOnAction(e -> openReaderEntryPage(stage));
    }

    // Utility Method to Style Buttons
    private void styleSignupButton(Button button, String baseColor, String hoverColor, String textColor) {
        button.setStyle(String.format(
                "-fx-padding: 15 50; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 18px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                baseColor, textColor));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-padding: 15 50; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 18px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                hoverColor, textColor)));

        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-padding: 15 50; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 18px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                baseColor, textColor)));
    }

    // Register a New Reader
    private boolean registerReader(String username, String password, String name, String department) {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || department.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return false;
        }

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO readers (username, password, name, department) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, name);
                statement.setString(4, department);

                int rowsInserted = statement.executeUpdate();
                statement.close();
                connection.close();

                return rowsInserted > 0;
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    showAlert(Alert.AlertType.ERROR, "Signup Failed", "Username already exists.");
                } else {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // Reader Login Page
    private void openReaderLoginPage(Stage stage) {
        // Main Layout
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #E3F2FD, #BBDEFB); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("Reader Login");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #0D47A1;");
        Label subtitleLabel = new Label("Access your library account");
        subtitleLabel.setStyle("-fx-font-size: 18px; -fx-font-style: italic; -fx-text-fill: #424242;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Login Form
        GridPane loginForm = new GridPane();
        loginForm.setPadding(new Insets(30));
        loginForm.setHgap(20);
        loginForm.setVgap(20);
        loginForm.setAlignment(Pos.CENTER); // Center the entire GridPane

        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        usernameField.setPrefWidth(300); // Set a wider field width

        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A237E;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10; -fx-border-color: #1976D2; -fx-border-radius: 5;");
        passwordField.setPrefWidth(300);

        // Align labels and fields within their cells
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);

        loginForm.add(usernameLabel, 0, 0);
        loginForm.add(usernameField, 1, 0);
        loginForm.add(passwordLabel, 0, 1);
        loginForm.add(passwordField, 1, 1);

        // Buttons Section
        HBox buttonSection = new HBox(40);
        buttonSection.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        styleResponsiveButton(loginButton, "#1976D2", "#1565C0", "#FFFFFF");

        Button backButton = new Button("Back");
        styleResponsiveButton(backButton, "#E53935", "#D32F2F", "#FFFFFF");

        buttonSection.getChildren().addAll(loginButton, backButton);

        // Footer Section
        Label footerLabel = new Label("Good Books Library © 2024");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

        // Combine All Sections
        mainLayout.getChildren().addAll(titleSection, loginForm, buttonSection, footerLabel);

        // Scene Setup
        Scene loginScene = new Scene(mainLayout, 900, 700); // Increased height for larger elements
        stage.setScene(loginScene);

        // Button Actions
        loginButton.setOnAction(e -> {
            String[] readerDetails = validateLogin(usernameField.getText(), passwordField.getText());
            if (readerDetails != null) {  // Successful login
                openReaderSection(stage, readerDetails[0], readerDetails[1]);  // Pass reader's name and department
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        });

        backButton.setOnAction(e -> openReaderEntryPage(stage));
    }

    // Utility Method to Style Buttons
    private void styleResponsiveButton(Button button, String baseColor, String hoverColor, String textColor) {
        button.setStyle(String.format(
                "-fx-padding: 15 50; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 18px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                baseColor, textColor));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-padding: 15 50; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 18px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                hoverColor, textColor)));

        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-padding: 15 50; -fx-background-color: %s; -fx-text-fill: %s; -fx-font-size: 18px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                baseColor, textColor)));
    }

    // Validate Login for Reader
    private String[] validateLogin(String username, String password) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT name, department FROM readers WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new String[]{resultSet.getString("name"), resultSet.getString("department")};
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;  // Login failed
    }

    // Show Alert Method
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();  // This ensures the alert doesn't terminate the app
    }

    private void openReaderSection(Stage stage, String readerName, String department) {
        VBox readerSection = new VBox(20);
        readerSection.setPadding(new Insets(30));
        readerSection.setAlignment(Pos.CENTER);
        readerSection.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2); " +
                "-fx-border-radius: 15; -fx-background-radius: 15;");

        // Title Label
        Label titleLabel = new Label("Reader Dashboard");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);");

        // Welcome Label
        Label welcomeLabel = new Label("Welcome, " + readerName + " from " + department + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #34495E; -fx-font-weight: normal; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 1, 0, 0.5, 0.5);");

        // Buttons
        Button placeOrderButton = ReadercreateStyledButtonWithIcon("Place Order", "🛒");
        Button viewBooksButton = ReadercreateStyledButtonWithIcon("View Books", "📖");
        Button pendingOrdersButton = ReadercreateStyledButtonWithIcon("Pending Orders", "⏳");
        Button borrowedBooksButton = ReadercreateStyledButtonWithIcon("Borrowed Books", "📤");
        Button returnBookButton = ReadercreateStyledButtonWithIcon("Return Book", "🔄");
        Button exitButton = ReadercreateStyledButtonWithIcon("Exit", "🔙", "#E74C3C", "#C0392B");


        // set width fo all btns

        double buttonWidth = 200.0;

        placeOrderButton.setPrefWidth(buttonWidth);
        viewBooksButton.setPrefWidth(buttonWidth);
        pendingOrdersButton.setPrefWidth(buttonWidth);
        borrowedBooksButton.setPrefWidth(buttonWidth);
        returnBookButton.setPrefWidth(buttonWidth);

        exitButton.setPrefWidth(150.0);

        // Disable Buttons Initially
        pendingOrdersButton.setDisable(true);
        borrowedBooksButton.setDisable(true);
        returnBookButton.setDisable(true);

        // Check Conditions for Enabling Buttons
        updateButtonStates(pendingOrdersButton, borrowedBooksButton, returnBookButton, readerName);

        // Add all components to the layout
        readerSection.getChildren().addAll(
                titleLabel,
                welcomeLabel,
                placeOrderButton,
                viewBooksButton,
                pendingOrdersButton,
                borrowedBooksButton,
                returnBookButton,
                exitButton
        );

        // Scene Setup
        Scene readerScene = new Scene(readerSection, 900, 600);
        stage.setScene(readerScene);

        // Button Actions
        placeOrderButton.setOnAction(e -> openPlaceOrderPopup(readerName, department, pendingOrdersButton));
        viewBooksButton.setOnAction(e -> openReaderViewBooksPage(stage, readerName, department));
        pendingOrdersButton.setOnAction(e -> openPendingOrdersPage(stage, readerName));
        borrowedBooksButton.setOnAction(e -> openBorrowedBooksPage(stage, readerName));
        returnBookButton.setOnAction(e -> openReturnBookPage(stage, readerName));
        exitButton.setOnAction(e -> start(stage));
    }

    // Helper Method to Update Button States
    private void updateButtonStates(Button pendingOrdersButton, Button borrowedBooksButton, Button returnBookButton, String readerName) {
        boolean hasPendingOrders = !fetchPendingOrders(readerName).isEmpty();
        boolean hasBorrowedBooks = !fetchBorrowedBooks(readerName).isEmpty();

        // Enable or Disable Buttons Based on Conditions
        pendingOrdersButton.setDisable(!hasPendingOrders);
        borrowedBooksButton.setDisable(!hasBorrowedBooks);
        returnBookButton.setDisable(!hasBorrowedBooks);
    }

    // Utility method to create styled buttons with icons
    private Button ReadercreateStyledButtonWithIcon(String text, String icon) {
        return ReadercreateStyledButtonWithIcon(text, icon, "#3498DB", "#2980B9");
    }

    private Button ReadercreateStyledButtonWithIcon(String text, String icon, String baseColor, String hoverColor) {
        Button button = new Button(icon + " " + text);
        button.setStyle(String.format(
                "-fx-padding: 12 24; -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);",
                baseColor));

        button.setOnMouseEntered(e -> button.setStyle(String.format(
                "-fx-padding: 12 24; -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 4, 0, 2, 2);",
                hoverColor)));
        button.setOnMouseExited(e -> button.setStyle(String.format(
                "-fx-padding: 12 24; -fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 16px; " +
                        "-fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 1, 1);",
                baseColor)));

        return button;
    }

    // Place Order Popup
    private void openPlaceOrderPopup(String readerName, String department, Button pendingOrdersButton) {
        Stage popupStage = new Stage();
        popupStage.setTitle("📖 Place Order");

        // Main Layout
        VBox orderForm = new VBox(15);
        orderForm.setPadding(new Insets(20));
        orderForm.setAlignment(Pos.CENTER);
        orderForm.setStyle("-fx-background-color: linear-gradient(to bottom, #FDFEFE, #AED6F1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📋 Place Your Order");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Reader Details
        Label nameLabel = new Label("👤 Name: " + readerName);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495E;");
        Label deptLabel = new Label("🏢 Department: " + department);
        deptLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495E;");

        // Search Box
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Book Name...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-border-color: #BDC3C7; -fx-border-radius: 5;");
        searchField.setPrefWidth(400);

        searchBox.getChildren().add(searchField);

        // Available Books Table
        TableView<Book> availableBooksTable = new TableView<>();
        availableBooksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, String> colBookName = new TableColumn<>("Book Name");
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBookName.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 14px;");

        TableColumn<Book, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colIsbn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");

        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAuthor.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");

        TableColumn<Book, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colGenre.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");

        availableBooksTable.getColumns().addAll(colBookName, colIsbn, colAuthor, colGenre);

        // Fetch available books and set up real-time search
        ObservableList<Book> availableBooks = fetchAvailableBooks(); // Fetch books from the database
        FilteredList<Book> filteredBooks = new FilteredList<>(availableBooks, b -> true); // Show all books initially

        // Real-time search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all books if the search field is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return book.getBookName().toLowerCase().contains(lowerCaseFilter); // Filter by book name
            });
        });

        // Bind the filtered list to the table view
        SortedList<Book> sortedBooks = new SortedList<>(filteredBooks); // Enable sorting
        sortedBooks.comparatorProperty().bind(availableBooksTable.comparatorProperty());
        availableBooksTable.setItems(sortedBooks);

        // Return Date Picker
        DatePicker returnDatePicker = new DatePicker();
        returnDatePicker.setPromptText("Select Return Date");
        returnDatePicker.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: white; " +
                "-fx-border-color: #BDC3C7; -fx-border-radius: 5;");

        // Submit Button
        Button submitButton = new Button("Submit Order");
        PopUp_styleButton(submitButton, "#27AE60", "#229954", "white");

        // Close Button
        Button closeButton = new Button("Cancel");
        PopUp_styleButton(closeButton, "#E74C3C", "#C0392B", "white");
        closeButton.setOnAction(e -> popupStage.close());

        // Buttons Layout
        HBox buttonLayout = new HBox(20, submitButton, closeButton);
        buttonLayout.setAlignment(Pos.CENTER);

        // Combine All Elements
        orderForm.getChildren().addAll(titleLabel, nameLabel, deptLabel, searchBox, availableBooksTable, returnDatePicker, buttonLayout);

        // Scene Setup
        Scene popupScene = new Scene(orderForm, 800, 600);
        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        popupStage.show();

        // Submit Button Logic
        submitButton.setOnAction(e -> {
            Book selectedBook = availableBooksTable.getSelectionModel().getSelectedItem();
            LocalDate returnDate = returnDatePicker.getValue();

            if (selectedBook == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a book to place an order.");
                return;
            }
            if (returnDate == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a return date.");
                return;
            }

            // Process the order
            if (processOrder(readerName, department, selectedBook, returnDate)) {
                popupStage.close();
                showAlert(Alert.AlertType.INFORMATION, "Order Placed", "Your order has been successfully placed for pending.");

                // Enable Pending Orders Button
                updatePendingOrdersButtonState(pendingOrdersButton, readerName);
            } else {
                showAlert(Alert.AlertType.ERROR, "Order Failed", "Failed to place the order.");
            }
        });
    }

    // Fetch available books from the database
    private ObservableList<Book> fetchAvailableBooks() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM books";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    books.add(new Book(
                            resultSet.getString("isbn"),
                            resultSet.getString("book_name"),
                            resultSet.getString("author"),
                            resultSet.getString("genre")
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return books;
    }

    // Updated processOrder to use the selected Book object
    private boolean processOrder(String readerName, String department, Book book, LocalDate returnDate) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO orders (reader_name, book_name, isbn, author, genre, issue_date, return_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);

                statement.setString(1, readerName);
                statement.setString(2, book.getBookName());
                statement.setString(3, book.getIsbn());
                statement.setString(4, book.getAuthor());
                statement.setString(5, book.getGenre());
                statement.setDate(6, java.sql.Date.valueOf(LocalDate.now())); // Issue Date
                statement.setDate(7, java.sql.Date.valueOf(returnDate));      // Return Date

                int rowsInserted = statement.executeUpdate();
                statement.close();
                connection.close();

                return rowsInserted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void PopUp_styleButton(Button button, String baseColor, String hoverColor, String textColor) {
        button.setStyle("-fx-padding: 10 20; -fx-background-color: " + baseColor + "; -fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-padding: 10 20; -fx-background-color: " + hoverColor + "; -fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));

        button.setOnMouseExited(e -> button.setStyle("-fx-padding: 10 20; -fx-background-color: " + baseColor + "; -fx-text-fill: " + textColor + "; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-border-radius: 10; -fx-background-radius: 10;"));
    }

    private void updatePendingOrdersButtonState(Button pendingOrdersButton, String readerName) {
        boolean hasPendingOrders = !fetchPendingOrders(readerName).isEmpty();
        pendingOrdersButton.setDisable(!hasPendingOrders);
    }

    private void openReaderViewBooksPage(Stage stage, String readerName, String department) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #FDFEFE, #AED6F1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📚 Browse Books");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("Welcome, " + readerName + " from " + department + ".");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Back Button
        Button backButton = new Button("Back");
        styleButton(backButton, "#E74C3C", "#C0392B", "white");
        backButton.setOnAction(e -> openReaderSection(stage, readerName, department));

        // Filter Section
        HBox filterSection = new HBox(15);
        filterSection.setAlignment(Pos.CENTER);

        Label filterLabel = new Label("Search:");
        filterLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

        TextField searchField = new TextField();
        searchField.setPromptText("Type to search by book name");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-border-radius: 5; -fx-background-radius: 5;");
        filterSection.getChildren().addAll(filterLabel, searchField);

        // Book Table
        TableView<Book> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> colBookName = new TableColumn<>("Book Name");
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));

        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        tableView.getColumns().addAll(colIsbn, colBookName, colAuthor, colGenre);

        // Fetch books from the database
        ObservableList<Book> allBooks = fetchBooksForReaderFromDatabase();
        FilteredList<Book> filteredBooks = new FilteredList<>(allBooks, p -> true); // Initially show all books

        // Real-time search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all books if the search field is empty
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return book.getBookName().toLowerCase().contains(lowerCaseFilter); // Filter by book name
            });
        });

        // Bind the filtered list to the table view
        tableView.setItems(filteredBooks);

        // Combine Sections
        mainLayout.getChildren().addAll(titleSection, filterSection, tableView, backButton);

        // Scene Setup
        Scene readerViewBooksScene = new Scene(mainLayout, 900, 600);
        stage.setScene(readerViewBooksScene);
    }

    private ObservableList<Book> fetchBooksForReaderFromDatabase() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.getConnection();

        if (connection != null) {
            try {
                String query = "SELECT isbn, book_name, author, genre FROM books";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    books.add(new Book(
                            resultSet.getString("isbn"),
                            resultSet.getString("book_name"),
                            resultSet.getString("author"),
                            resultSet.getString("genre")
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Database connection is not available.");
        }
        return books;
    }


    // Pending Orders

    private void openPendingOrdersPage(Stage stage, String readerName) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #FDFEFE, #AED6F1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📋 Pending Orders");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("View all your pending book orders.");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Back Button
        Button backButton = new Button("Back");
        styleButton(backButton, "#E74C3C", "#C0392B", "white");
        backButton.setOnAction(e -> openReaderSection(stage, readerName, ""));

        // Table Section
        TableView<Order> pendingOrdersTable = new TableView<>();
        pendingOrdersTable.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; " +
                "-fx-border-radius: 10; -fx-background-radius: 10;");
        pendingOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define Table Columns
        TableColumn<Order, String> colBook = new TableColumn<>("Book Name");
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBook.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colIssueDate = new TableColumn<>("Issue Date");
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssueDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Populate Table
        pendingOrdersTable.getColumns().addAll(colBook, colIssueDate, colReturnDate);
        ObservableList<Order> pendingOrdersData = FXCollections.observableArrayList(fetchPendingOrders(readerName));
        pendingOrdersTable.setItems(pendingOrdersData);

        // Combine All Sections
        mainLayout.getChildren().addAll( titleSection, pendingOrdersTable, backButton);

        // Scene Setup
        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setScene(scene);
    }

    private ObservableList<Order> fetchPendingOrders(String readerName) {
        ObservableList<Order> pendingOrders = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM orders WHERE reader_name = ? AND status = 'Pending'";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, readerName);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    pendingOrders.add(new Order(
                            resultSet.getInt("order_id"),
                            resultSet.getString("reader_name"),
                            resultSet.getString("book_name"),
                            resultSet.getDate("issue_Date").toLocalDate(),
                            resultSet.getDate("return_date").toLocalDate()
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return pendingOrders;
    }

    // Borrowed Books

    private void openBorrowedBooksPage(Stage stage, String readerName) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #EAF2F8, #AED6F1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("📚 Borrowed Books");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("View the list of books you have borrowed.");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-text-fill: #34495E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Back Button
        Button backButton = new Button("Back");
        styleButton(backButton, "#E74C3C", "#C0392B", "white");
        backButton.setOnAction(e -> openReaderSection(stage, readerName, ""));

        // Table Section
        TableView<Order> borrowedBooksTable = new TableView<>();
        borrowedBooksTable.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; " +
                "-fx-border-radius: 10; -fx-background-radius: 10;");
        borrowedBooksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define Table Columns
        TableColumn<Order, String> colBook = new TableColumn<>("Book Name");
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBook.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colIssueDate = new TableColumn<>("Issue Date");
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssueDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Populate Table
        borrowedBooksTable.getColumns().addAll(colBook, colIssueDate, colReturnDate);
        ObservableList<Order> borrowedBooksData = FXCollections.observableArrayList(fetchBorrowedBooks(readerName));
        borrowedBooksTable.setItems(borrowedBooksData);

        // Combine All Sections
        mainLayout.getChildren().addAll( titleSection, borrowedBooksTable,backButton);

        // Scene Setup
        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setScene(scene);
    }


    private ObservableList<Order> fetchBorrowedBooks(String readerName) {
        ObservableList<Order> borrowedBooks = FXCollections.observableArrayList();
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM orders WHERE reader_name = ? AND status = 'Approved'";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, readerName);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    borrowedBooks.add(new Order(
                            resultSet.getInt("order_id"),
                            resultSet.getString("reader_name"),
                            resultSet.getString("book_name"),
                            resultSet.getDate("issue_Date").toLocalDate(),
                            resultSet.getDate("return_date").toLocalDate()
                    ));
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return borrowedBooks;
    }

    // Return Books

    private void openReturnBookPage(Stage stage, String readerName) {
        // Main Layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #FDFEFE, #D5DBDB); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Title Section
        Label titleLabel = new Label("🔄 Return Borrowed Books");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        Label subtitleLabel = new Label("Select a book to return.");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-text-fill: #34495E;");

        VBox titleSection = new VBox(10, titleLabel, subtitleLabel);
        titleSection.setAlignment(Pos.CENTER);

        // Back Button
        Button backButton = new Button("Back");
        styleButton(backButton, "#E74C3C", "#C0392B", "white");
        backButton.setOnAction(e -> openReaderSection(stage, readerName, ""));

        // Table Section
        TableView<Order> returnBooksTable = new TableView<>();
        returnBooksTable.setStyle("-fx-background-color: white; -fx-border-color: #BDC3C7; " +
                "-fx-border-radius: 10; -fx-background-radius: 10;");
        returnBooksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define Table Columns
        TableColumn<Order, String> colBook = new TableColumn<>("Book Name");
        colBook.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBook.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colIssueDate = new TableColumn<>("Issue Date");
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssueDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        TableColumn<Order, LocalDate> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturnDate.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Populate Table
        returnBooksTable.getColumns().addAll(colBook, colIssueDate, colReturnDate);
        ObservableList<Order> returnBooksData = FXCollections.observableArrayList(fetchBorrowedBooks(readerName));
        returnBooksTable.setItems(returnBooksData);

        // Fine Label
        Label fineLabel = new Label();
        fineLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495E;");

        // Return Button
        Button returnButton = new Button("Return Selected Book");
        styleButton(returnButton, "#3498DB", "#2980B9", "white");
        returnButton.setOnAction(e -> {
            Order selectedOrder = returnBooksTable.getSelectionModel().getSelectedItem();
            if (selectedOrder != null) {
                int fine = calculateFine(selectedOrder);
                if (fine > 0) {
                    showFinePaymentWindow(fine, selectedOrder, stage, readerName);
                } else {
                    completeReturn(selectedOrder);
                    fineLabel.setText("✅ Book returned successfully.");
                    returnBooksTable.setItems(FXCollections.observableArrayList(fetchBorrowedBooks(readerName))); // Refresh table
                }
            } else {
                fineLabel.setText("⚠️ Please select a book to return.");
            }
        });

        // Combine All Sections
        mainLayout.getChildren().addAll( titleSection, returnBooksTable, returnButton,backButton, fineLabel);

        // Scene Setup
        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setScene(scene);
    }

    private int calculateFine(Order order) {
        LocalDate dueDate = order.getReturnDate();
        int daysLate = (int) LocalDate.now().toEpochDay() - (int) dueDate.toEpochDay();
        return daysLate > 0 ? daysLate * 100 : 0; // Rs. 100 per day late fee
    }

    private void showFinePaymentWindow(int fine, Order order, Stage parentStage, String readerName) {
        Stage fineStage = new Stage();
        fineStage.setTitle("💵 Pay Fine");

        // Main Layout
        VBox fineLayout = new VBox(20);
        fineLayout.setPadding(new Insets(30));
        fineLayout.setAlignment(Pos.CENTER);
        fineLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #FDFEFE, #F5B7B1); " +
                "-fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        // Fine Message
        Label fineMessageLabel = new Label("🔔 Pay Fine First: Rs. " + fine);
        fineMessageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        // Fine Input Field
        TextField fineField = new TextField();
        fineField.setPromptText("Enter Fine Amount");
        fineField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: white; " +
                "-fx-border-color: #BDC3C7; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Pay Fine Button
        Button payFineButton = new Button("Pay Fine");
        styleButton(payFineButton, "#27AE60", "#229954", "white");

        // Cancel Button
        Button cancelButton = new Button("Cancel");
        styleButton(cancelButton, "#E74C3C", "#C0392B", "white");
        cancelButton.setOnAction(e -> fineStage.close());

        // Buttons Layout
        HBox buttonLayout = new HBox(20, payFineButton, cancelButton);
        buttonLayout.setAlignment(Pos.CENTER);

        // Pay Fine Button Logic
        payFineButton.setOnAction(e -> {
            String enteredAmount = fineField.getText();
            if (enteredAmount.matches("\\d+")) {
                int enteredFine = Integer.parseInt(enteredAmount);
                if (enteredFine == fine) { // Check if the entered fine matches exactly
                    completeReturn(order);
                    fineStage.close();
                    showAlert(Alert.AlertType.INFORMATION, "Fine Paid", "Fine paid successfully. Book returned.");
                    openReturnBookPage(parentStage, readerName); // Refresh return page
                } else {
                    showAlert(Alert.AlertType.ERROR, "Payment Error", "The entered amount must match the fine: Rs. " + fine);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric fine amount.");
            }
        });


        // Combine All Elements
        fineLayout.getChildren().addAll(fineMessageLabel, fineField, buttonLayout);

        // Scene Setup
        Scene fineScene = new Scene(fineLayout, 400, 300);
        fineStage.setScene(fineScene);
        fineStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with parent window
        fineStage.show();
    }

    private void completeReturn(Order order) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                // Fetch the full book details for the returned order
                String fetchQuery = "SELECT isbn, book_name, author, genre FROM orders WHERE order_id = ?";
                PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery);
                fetchStatement.setInt(1, order.getOrderId());
                ResultSet resultSet = fetchStatement.executeQuery();

                if (resultSet.next()) {
                    String isbn = resultSet.getString("isbn");
                    String bookName = resultSet.getString("book_name");
                    String author = resultSet.getString("author");  // Fetch author
                    String genre = resultSet.getString("genre");    // Fetch genre

                    // Insert back into books table
                    String insertBookQuery = "INSERT INTO books (isbn, book_name, author, genre) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertBookStatement = connection.prepareStatement(insertBookQuery);
                    insertBookStatement.setString(1, isbn);
                    insertBookStatement.setString(2, bookName);
                    insertBookStatement.setString(3, author);
                    insertBookStatement.setString(4, genre);
                    insertBookStatement.executeUpdate();
                    insertBookStatement.close();
                }

                resultSet.close();
                fetchStatement.close();

                // Delete order record after returning
                String deleteOrderQuery = "DELETE FROM orders WHERE order_id = ?";
                PreparedStatement deleteOrderStatement = connection.prepareStatement(deleteOrderQuery);
                deleteOrderStatement.setInt(1, order.getOrderId());
                deleteOrderStatement.executeUpdate();
                deleteOrderStatement.close();

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}


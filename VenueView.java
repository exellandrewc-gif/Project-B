import VenueManager.BaseClasses.Booking;
import VenueManager.BaseClasses.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VenueView {
    private HBox view;
    private VBox menuPane;
    private VBox contentPane;
    private Label statusLabel;

    private VenueController controller;
    private VenueModel model;
    private Stage primaryStage;

    public VenueView(VenueController controller, VenueModel model, Stage primaryStage) {
        this.controller = controller;
        this.model = model;
        this.primaryStage = primaryStage;

        createAndConfigurePane();
        createAndLayoutControls();
        observeModelAndUpdateControls();
        showDashboardPane();
    }

    public Parent asParent() {
        return this.view;
    }

    private void createAndConfigurePane() {
        this.view = new HBox(10);
        this.menuPane = new VBox(8);
        this.contentPane = new VBox(8);
        this.statusLabel = new Label();

        this.menuPane.setPrefWidth(180);
        this.contentPane.setPrefWidth(780);
        this.menuPane.setAlignment(Pos.TOP_CENTER);
    }

    private void createAndLayoutControls() {
        Label title = new Label("Venue Manager");
        Label venueName = new Label(this.model.getVenue().name);

        Button dashboardButton = new Button("Dashboard");
        Button eventsButton = new Button("Events");
        Button bookingsButton = new Button("Bookings");
        Button checkInButton = new Button("Check In/Out");
        Button layoutButton = new Button("Venue Layout");
        Button analyticsButton = new Button("Analytics");

        dashboardButton.setMaxWidth(Double.MAX_VALUE);
        eventsButton.setMaxWidth(Double.MAX_VALUE);
        bookingsButton.setMaxWidth(Double.MAX_VALUE);
        checkInButton.setMaxWidth(Double.MAX_VALUE);
        layoutButton.setMaxWidth(Double.MAX_VALUE);
        analyticsButton.setMaxWidth(Double.MAX_VALUE);

        dashboardButton.setOnAction(event -> showDashboardPane());
        eventsButton.setOnAction(event -> showEventsPane());
        bookingsButton.setOnAction(event -> showBookingsPane());
        checkInButton.setOnAction(event -> showCheckInPane());
        layoutButton.setOnAction(event -> showLayoutPane());
        analyticsButton.setOnAction(event -> showAnalyticsPane());

        this.menuPane.getChildren().addAll(title, venueName, dashboardButton, eventsButton, bookingsButton,
                checkInButton, layoutButton, analyticsButton);

        VBox rightPane = new VBox(8, this.contentPane, this.statusLabel);
        this.view.getChildren().addAll(this.menuPane, rightPane);
    }

    private void observeModelAndUpdateControls() {
        this.statusLabel.textProperty().bind(this.model.statusMessageProperty());
    }

    private void showDashboardPane() {
        this.contentPane.getChildren().clear();

        Label heading = new Label("Dashboard");
        Label totalSeats = new Label();
        Label availableSeats = new Label();
        Label bookedSeats = new Label();
        Label checkedInSeats = new Label();
        Label bookingCount = new Label();

        totalSeats.textProperty().bind(this.model.totalSeatsProperty().asString("Total seats: %d"));
        availableSeats.textProperty().bind(this.model.availableSeatCountProperty().asString("Available seats: %d"));
        bookedSeats.textProperty().bind(this.model.bookedSeatCountProperty().asString("Booked seats: %d"));
        checkedInSeats.textProperty().bind(this.model.checkedInSeatCountProperty().asString("Checked-in seats: %d"));
        bookingCount.textProperty().bind(this.model.bookingCountProperty().asString("Bookings: %d"));

        TableView<AreaStatsRecord> table = createAreaStatsTable();
        this.contentPane.getChildren().addAll(heading, totalSeats, availableSeats, bookedSeats, checkedInSeats,
                bookingCount, table);
    }

    private void showEventsPane() {
        this.contentPane.getChildren().clear();

        Label heading = new Label("Concert Events");
        TableView<Event> eventTable = createEventTable();

        Button addButton = new Button("Create Event");
        addButton.setOnAction(event -> createEventForm());

        Button deleteButton = new Button("Delete Selected Event");
        deleteButton.setOnAction(event -> {
            Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
            this.controller.deleteEvent(selectedEvent);
        });

        HBox buttons = new HBox(5, addButton, deleteButton);
        this.contentPane.getChildren().addAll(heading, eventTable, buttons);
    }

    private void showBookingsPane() {
        this.contentPane.getChildren().clear();

        Label heading = new Label("Seat Bookings");
        TableView<Booking> bookingTable = createBookingTable();

        Button addButton = new Button("Create Booking");
        addButton.setOnAction(event -> createBookingForm());

        Button viewButton = new Button("View Selected Booking");
        viewButton.setOnAction(event -> showBookingDetails(bookingTable.getSelectionModel().getSelectedItem()));

        Button cancelButton = new Button("Cancel Selected Booking");
        cancelButton.setOnAction(event -> {
            Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
            this.controller.cancelBooking(selectedBooking);
        });

        HBox buttons = new HBox(5, addButton, viewButton, cancelButton);
        this.contentPane.getChildren().addAll(heading, bookingTable, buttons);
    }

    private void showCheckInPane() {
        this.contentPane.getChildren().clear();

        Label heading = new Label("Check In and Check Out");
        TableView<SeatRecord> seatTable = createSeatTable(this.model.seatsProperty());

        Button checkInButton = new Button("Check In Selected Seat");
        checkInButton.setOnAction(event -> {
            SeatRecord selectedSeat = seatTable.getSelectionModel().getSelectedItem();
            this.controller.checkInSeat(selectedSeat);
        });

        Button checkOutButton = new Button("Check Out Selected Seat");
        checkOutButton.setOnAction(event -> {
            SeatRecord selectedSeat = seatTable.getSelectionModel().getSelectedItem();
            this.controller.checkOutSeat(selectedSeat);
        });

        HBox buttons = new HBox(5, checkInButton, checkOutButton);
        this.contentPane.getChildren().addAll(heading, seatTable, buttons);
    }

    private void showLayoutPane() {
        this.contentPane.getChildren().clear();

        Label heading = new Label("Venue Layout");
        TableView<AreaRecord> areaTable = createAreaTable();
        TableView<SeatRecord> seatTable = createSeatTable(this.model.seatsProperty());

        Button addAreaButton = new Button("Create Seating Area");
        addAreaButton.setOnAction(event -> createAreaForm());

        Button deleteAreaButton = new Button("Delete Selected Area");
        deleteAreaButton.setOnAction(event -> {
            AreaRecord selectedArea = areaTable.getSelectionModel().getSelectedItem();
            this.controller.deleteSeatingArea(selectedArea);
        });

        HBox buttons = new HBox(5, addAreaButton, deleteAreaButton);
        this.contentPane.getChildren().addAll(heading, new Label("Seating Areas"), areaTable, buttons,
                new Label("Seats"), seatTable);
    }

    private void showAnalyticsPane() {
        this.contentPane.getChildren().clear();

        Label heading = new Label("Analytics and Capacity");
        Label totalSeats = new Label();
        Label availableSeats = new Label();
        Label bookedSeats = new Label();
        Label checkedInSeats = new Label();
        Label bookingCount = new Label();

        totalSeats.textProperty().bind(this.model.totalSeatsProperty().asString("Total capacity: %d"));
        availableSeats.textProperty().bind(this.model.availableSeatCountProperty().asString("Available: %d"));
        bookedSeats.textProperty().bind(this.model.bookedSeatCountProperty().asString("Booked: %d"));
        checkedInSeats.textProperty().bind(this.model.checkedInSeatCountProperty().asString("Checked in: %d"));
        bookingCount.textProperty().bind(this.model.bookingCountProperty().asString("Total bookings: %d"));

        HBox summary = new HBox(12, totalSeats, availableSeats, bookedSeats, checkedInSeats, bookingCount);
        TableView<AreaStatsRecord> areaStatsTable = createAreaStatsTable();

        this.contentPane.getChildren().addAll(heading, summary, areaStatsTable);
    }

    private void createEventForm() {
        Stage stage = createModalStage("Create Event");

        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Event name");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            // MVC step 1: the View receives the button click and reads the user's input.
            // It does not create the Event itself; it passes the request to the Controller.
            this.controller.addEvent(eventNameField.getText());
            stage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> stage.close());

        HBox eventRow = new HBox(5, new Label("Event name:"), eventNameField);
        HBox buttonRow = new HBox(5, submitButton, cancelButton);
        eventRow.setAlignment(Pos.CENTER);
        buttonRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(8, eventRow, buttonRow);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 360, 120));
        stage.show();
    }

    private void createBookingForm() {
        Stage stage = createModalStage("Create Booking");

        TextField customerNameField = new TextField();
        customerNameField.setPromptText("Customer name");

        TableView<Event> eventTable = createEventTable();
        eventTable.setPrefHeight(180);

        TableView<SeatRecord> seatTable = createSeatTable(this.model.availableSeatsProperty());
        seatTable.setPrefHeight(220);

        Button submitButton = new Button("Create Booking");
        submitButton.setOnAction(event -> {
            // MVC step 1: the View collects the selected Event, Seat and customer name.
            Event selectedEvent = eventTable.getSelectionModel().getSelectedItem();
            SeatRecord selectedSeat = seatTable.getSelectionModel().getSelectedItem();
            // MVC step 2 begins when the View sends those values to the Controller.
            this.controller.createBooking(customerNameField.getText(), selectedEvent, selectedSeat);
            stage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> stage.close());

        HBox nameRow = new HBox(5, new Label("Customer:"), customerNameField);
        HBox buttonRow = new HBox(5, submitButton, cancelButton);
        nameRow.setAlignment(Pos.CENTER);
        buttonRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(8, nameRow, new Label("Select Event"), eventTable, new Label("Select Available Seat"),
                seatTable, buttonRow);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 760, 560));
        stage.show();
    }

    private void createAreaForm() {
        Stage stage = createModalStage("Create Seating Area");

        TextField areaIdField = new TextField();
        areaIdField.setPromptText("Example: C");

        TextField areaNameField = new TextField();
        areaNameField.setPromptText("Area name");

        TextField seatCountField = new TextField();
        seatCountField.setPromptText("Number of seats");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            this.controller.createSeatingArea(areaIdField.getText(), areaNameField.getText(),
                    seatCountField.getText());
            stage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> stage.close());

        HBox idRow = new HBox(5, new Label("Area ID:"), areaIdField);
        HBox nameRow = new HBox(5, new Label("Area name:"), areaNameField);
        HBox countRow = new HBox(5, new Label("Seat count:"), seatCountField);
        HBox buttonRow = new HBox(5, submitButton, cancelButton);
        idRow.setAlignment(Pos.CENTER);
        nameRow.setAlignment(Pos.CENTER);
        countRow.setAlignment(Pos.CENTER);
        buttonRow.setAlignment(Pos.CENTER);

        VBox root = new VBox(8, idRow, nameRow, countRow, buttonRow);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 420, 180));
        stage.show();
    }

    private void showBookingDetails(Booking booking) {
        Stage stage = createModalStage("Booking Details");

        Label details = new Label(this.model.formatBookingDetails(booking));
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> stage.close());

        VBox root = new VBox(8, details, closeButton);
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 520, 220));
        stage.show();
    }

    private Stage createModalStage(String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initOwner(this.primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    private TableView<Event> createEventTable() {
        TableView<Event> table = new TableView<Event>();

        TableColumn<Event, String> idColumn = new TableColumn<Event, String>("Event ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(shortId(cellData.getValue().getId())));

        TableColumn<Event, String> nameColumn = new TableColumn<Event, String>("Event Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setMinWidth(220.0);

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.setItems(this.model.eventsProperty());
        return table;
    }

    private TableView<Booking> createBookingTable() {
        TableView<Booking> table = new TableView<Booking>();

        TableColumn<Booking, String> idColumn = new TableColumn<Booking, String>("Booking ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(shortId(cellData.getValue().getId())));

        TableColumn<Booking, String> customerColumn = new TableColumn<Booking, String>("Customer");
        customerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        customerColumn.setMinWidth(160.0);

        TableColumn<Booking, String> eventColumn = new TableColumn<Booking, String>("Event");
        eventColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(this.model.getEventName(cellData.getValue().getEventId())));
        eventColumn.setMinWidth(180.0);

        TableColumn<Booking, String> seatsColumn = new TableColumn<Booking, String>("Seats");
        seatsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(joinSeats(cellData.getValue())));

        table.getColumns().add(idColumn);
        table.getColumns().add(customerColumn);
        table.getColumns().add(eventColumn);
        table.getColumns().add(seatsColumn);
        table.setItems(this.model.bookingsProperty());
        return table;
    }

    private TableView<SeatRecord> createSeatTable(ObservableList<SeatRecord> seatData) {
        TableView<SeatRecord> table = new TableView<SeatRecord>();

        TableColumn<SeatRecord, String> seatColumn = new TableColumn<SeatRecord, String>("Seat");
        seatColumn.setCellValueFactory(cellData -> cellData.getValue().seatIdProperty());

        TableColumn<SeatRecord, String> areaColumn = new TableColumn<SeatRecord, String>("Area");
        areaColumn.setCellValueFactory(cellData -> cellData.getValue().areaIdProperty());

        TableColumn<SeatRecord, String> nameColumn = new TableColumn<SeatRecord, String>("Display Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().displayNameProperty());
        nameColumn.setMinWidth(160.0);

        TableColumn<SeatRecord, String> statusColumn = new TableColumn<SeatRecord, String>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        TableColumn<SeatRecord, String> bookingColumn = new TableColumn<SeatRecord, String>("Booking");
        bookingColumn.setCellValueFactory(cellData -> cellData.getValue().bookingIdProperty());
        bookingColumn.setMinWidth(180.0);

        table.getColumns().add(seatColumn);
        table.getColumns().add(areaColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(statusColumn);
        table.getColumns().add(bookingColumn);
        table.setItems(seatData);
        return table;
    }

    private TableView<AreaRecord> createAreaTable() {
        TableView<AreaRecord> table = new TableView<AreaRecord>();

        TableColumn<AreaRecord, String> idColumn = new TableColumn<AreaRecord, String>("Area ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().areaIdProperty());

        TableColumn<AreaRecord, String> nameColumn = new TableColumn<AreaRecord, String>("Area Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().areaNameProperty());
        nameColumn.setMinWidth(220.0);

        TableColumn<AreaRecord, Integer> countColumn = new TableColumn<AreaRecord, Integer>("Seats");
        countColumn.setCellValueFactory(cellData -> cellData.getValue().seatCountProperty().asObject());

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(countColumn);
        table.setItems(this.model.seatingAreasProperty());
        return table;
    }

    private TableView<AreaStatsRecord> createAreaStatsTable() {
        TableView<AreaStatsRecord> table = new TableView<AreaStatsRecord>();

        TableColumn<AreaStatsRecord, String> idColumn = new TableColumn<AreaStatsRecord, String>("Area ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().areaIdProperty());

        TableColumn<AreaStatsRecord, String> nameColumn = new TableColumn<AreaStatsRecord, String>("Area Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().areaNameProperty());
        nameColumn.setMinWidth(180.0);

        TableColumn<AreaStatsRecord, Integer> totalColumn = new TableColumn<AreaStatsRecord, Integer>("Total");
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalSeatsProperty().asObject());

        TableColumn<AreaStatsRecord, Integer> availableColumn = new TableColumn<AreaStatsRecord, Integer>("Available");
        availableColumn.setCellValueFactory(cellData -> cellData.getValue().availableSeatsProperty().asObject());

        TableColumn<AreaStatsRecord, Integer> bookedColumn = new TableColumn<AreaStatsRecord, Integer>("Booked");
        bookedColumn.setCellValueFactory(cellData -> cellData.getValue().bookedSeatsProperty().asObject());

        TableColumn<AreaStatsRecord, Integer> checkedInColumn = new TableColumn<AreaStatsRecord, Integer>("Checked In");
        checkedInColumn.setCellValueFactory(cellData -> cellData.getValue().checkedInSeatsProperty().asObject());

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(totalColumn);
        table.getColumns().add(availableColumn);
        table.getColumns().add(bookedColumn);
        table.getColumns().add(checkedInColumn);
        table.setItems(this.model.areaStatsProperty());
        return table;
    }

    private String joinSeats(Booking booking) {
        if (booking.getSeatIds() == null) {
            return "";
        }
        return String.join(", ", booking.getSeatIds());
    }

    private String shortId(String id) {
        if (id == null || id.length() <= 8) {
            return id;
        }
        return id.substring(0, 8);
    }
}

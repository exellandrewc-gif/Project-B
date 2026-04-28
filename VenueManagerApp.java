import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VenueManagerApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Venue Manager - Project B");

        VenueModel model = new VenueModel();
        VenueController controller = new VenueController(model);
        VenueView view = new VenueView(controller, model, primaryStage);

        Scene scene = new Scene(view.asParent(), 980, 620);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

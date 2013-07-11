package FXBrowser;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SwingFXBrowser implements DefaultURL{

	static JFrame frame = new JFrame("Swing integrated JavaFX Browser");
	static JFXPanel fxPanel = new JFXPanel();

	private static void initAndShowGUI() {
		// This method is invoked on Swing thread

		frame.setSize(1365, 769);
		frame.add(fxPanel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel);
			}
		});
	}

	private static void initFX(JFXPanel fxPanel) {
		// This method is invoked on JavaFX thread
		Scene scene = createScene();
		fxPanel.setScene(scene);
	}

	//Custom function for creation of New Tabs.
	private static Tab createAndSelectNewTab(final TabPane tabPane, final String title) {

		Tab tab = new Tab(title);
		Label aboutLabel = new Label();
		aboutLabel.setText("\n\n\t\t365: A Program for a day.\n\n\t\t\tWelcome to JavaFX Custom Browser. " +
				"\n\t\t\tThis is a custom browser created for demo purpose only." +
		"\n\t\t\tTo start browsing, click on New Tab.");
		aboutLabel.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		tab.setContent(aboutLabel);

		final ObservableList<Tab> tabs = tabPane.getTabs();
		tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
		tabs.add(tabs.size() - 1, tab);
		tabPane.getSelectionModel().select(tab);
		return tab;
	}


	public static Scene createScene() {

		final Group root = new Group();

		BorderPane borderPane = new BorderPane();
		final TabPane tabPane = new TabPane();

		//Preferred Size of TabPane.
		tabPane.setPrefSize(1365, 768);

		//Placement of TabPane.
		tabPane.setSide(Side.TOP);

		/* To disable closing of tabs.
		 * tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);*/

		final Tab newtab = new Tab();
		newtab.setText("New Tab");
		newtab.setClosable(false);

		//Addition of New Tab to the tabpane.
		tabPane.getTabs().addAll(newtab);

		createAndSelectNewTab(tabPane, "About the Browser");

		//Function to add and display new tabs with default URL display.
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable,
					Tab oldSelectedTab, Tab newSelectedTab) {
				if (newSelectedTab == newtab) {

					Tab tab = new Tab();
					tab.setText("               ");

					//WebView - to display, browse web pages.
					final WebView webView = new WebView();
					final WebEngine webEngine = webView.getEngine();
					webEngine.load(DEFAULT_URL);

					final TextField urlField = new TextField(DEFAULT_URL);
					webEngine.locationProperty().addListener(new ChangeListener<String>() {

						@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
							urlField.setText(newValue);
						}
					});

					//Action definition for the Button Go.
					EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {

						@Override public void handle(ActionEvent event) {
							webEngine.load(urlField.getText().startsWith("http://") 
									? urlField.getText() 
											: "http://" + urlField.getText());
						}
					};

					urlField.setOnAction(goAction);

					Button goButton = new Button("Go");
					goButton.setDefaultButton(true);
					goButton.setOnAction(goAction);


					//Action definition for the Button Capture.
					EventHandler<ActionEvent> CaptureAction = new EventHandler<ActionEvent>() {
						int i = 1;
						@Override public void handle(ActionEvent event) {
							frame.setSize((int)webView.getWidth(),(int)webView.getHeight());
							BufferedImage bi = new BufferedImage(fxPanel.getWidth(), fxPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
							Graphics graphics = bi.createGraphics();
							fxPanel.paint(graphics);
							try {
								ImageIO.write(bi, "PNG", new File("c:\\demo"+i+".png"));
								i++;
							} catch (IOException e) {
								e.printStackTrace();
							}
							graphics.dispose();
							bi.flush();

							/*WritableImage snapshot = webView.snapshot(new SnapshotParameters(), null);
											BufferedImage image;
											File file = new File("C:/test.jpg");
											BufferedImage bufferedImage = new BufferedImage(550, 400, BufferedImage.TYPE_INT_ARGB);
											image = javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, bufferedImage);
											try {
												Graphics2D gd = (Graphics2D) image.getGraphics();
												gd.translate(1366,768);
												ImageIO.write(image, "png", file);
											} catch (IOException ex) {

											};*/

							/*	}
									});*/
						}
					};


					webEngine.getLoadWorker().stateProperty().addListener(
							new ChangeListener() {
								public void changed(ObservableValue observable,
										Object oldValue, Object newValue) {
									State oldState = (State)oldValue;
									State newState = (State)newValue;
									if (State.SUCCEEDED == newValue) {
										System.out
										.println("Success");
										captureView();
									}
								}

								private void captureView() {

									System.out.println("Inside Capture View");
									BufferedImage bi = new BufferedImage(fxPanel.getWidth(), fxPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
									Graphics graphics = bi.createGraphics();
									fxPanel.paint(graphics);
									try {
										ImageIO.write(bi, "PNG", new File("c:\\demo.png"));

									} catch (IOException e) {
										e.printStackTrace();
									}
									graphics.dispose();
									bi.flush();
								
								}
							});

					Button Capture = new Button("Capture");
					Capture.setDefaultButton(false);

					Capture.setOnAction(CaptureAction);


					// Layout logic
					HBox hBox = new HBox(5);
					hBox.getChildren().setAll(urlField, goButton,Capture);
					HBox.setHgrow(urlField, Priority.ALWAYS);

					final VBox vBox = new VBox(5);
					vBox.getChildren().setAll(hBox, webView);
					VBox.setVgrow(webView, Priority.ALWAYS);


					tab.setContent(vBox);
					final ObservableList<Tab> tabs = tabPane.getTabs();
					tab.closableProperty().bind(Bindings.size(tabs).greaterThan(2));
					tabs.add(tabs.size() - 1, tab);
					tabPane.getSelectionModel().select(tab);

				}
			}
		});

		borderPane.setCenter(tabPane);
		root.getChildren().add(borderPane);


		return new Scene(root);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initAndShowGUI();
			}
		});
	}
}

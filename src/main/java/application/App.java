package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

/**
 * 主程序入口 JavaFX App Package:mvn clean javafx:jlink
 */
public class App extends Application {
	private static Stage sstage;
	private static Scene scene;
	private static Robot r;
	public static boolean start_up = false;

	@Override
	public void start(Stage stage) throws IOException {
		System.out.println("欢迎使用Java Remote Assistant远程协助助手。");
		System.out.println("Ryan Yim -2020-07-29-");
		System.out.println("版本：2.0");
		System.out.println("\n\n正在初始化……");
		sstage = stage;
		scene = new Scene(loadFXML("primary"));
		scene.getStylesheets().add(getClass().getResource("sample.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("JRA Tool");
		stage.setResizable(false);
		r = new Robot();
		// 如果是Windows系统才会显示主窗口
		if (File.separator.equals("\\")) {
			stage.setAlwaysOnTop(true);
			stage.show();
		}
		/**
		 * 指定按键监视 更新按键状态
		 */
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String Key = event.getCode().getName();
				if (PrimaryController.getCtrl()) {
					if (Key == "Ctrl") {
						System.out.println("检测到CTRL松开");
						r.keyPress(KeyCode.CONTROL);
					}
				} else if (PrimaryController.getAlt()) {
					if (Key == "Alt") {
						System.out.println("检测到ALT松开");
						r.keyPress(KeyCode.ALT);
					}
				} else if (PrimaryController.getShift()) {
					if (Key == "Shift") {
						System.out.println("检测到SHIFT松开");
						r.keyPress(KeyCode.SHIFT);
					}
				} else if (PrimaryController.getWindows()) {
					if (Key == "Windows") {
						System.out.println("检测到WINDOWS松开");
						r.keyPress(KeyCode.WINDOWS);
					}
				} else {
					switch (Key) {
					case "Ctrl":
						System.out.println("Update Ctrl");
						PrimaryController.setButtonText(0, "CTRL未锁定");
						break;
					case "Shift":
						System.out.println("Update Shift");
						PrimaryController.setButtonText(1, "SHIFT未锁定");
						break;
					case "Alt":
						System.out.println("Update Alt");
						PrimaryController.setButtonText(2, "ALT未锁定");
						break;
					case "Windows":
						System.out.println("Update Windows");
						PrimaryController.setButtonText(3, "开始键未锁定");
						break;
					default:
						System.out.println(Key);
					}
				}
			}
		});
		stage.setAlwaysOnTop(true);
		stage.show();
	}

	/**
	 * 加载指定的fxml文件
	 * 
	 * @param fxml
	 * @throws IOException
	 */
	static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}

	/**
	 * 设置是否置顶
	 * 
	 * @param setTop
	 */
	static void setTop(boolean setTop) {
		sstage.setAlwaysOnTop(setTop);
	}

	/**
	 * 设置窗口X坐标
	 * 
	 * @param d
	 */
	public static void setStageX(double d) {
		sstage.setX(d);
	}

	/**
	 * 设置窗口Y坐标
	 * 
	 * @param i
	 */
	public static void setStageY(double i) {
		sstage.setY(i);
	}

	// 设置窗口靠前
	public static void showFront() {
		try {
			sstage.hide();
			sstage.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("To front");
	}

	/**
	 * 私有的加载fxml的方法
	 * 
	 * @param fxml
	 * @return
	 * @throws IOException
	 */
	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	/**
	 * main方法
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
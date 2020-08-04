package application;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
		System.out.println("Ryan Yim -2020-08-03-");
		System.out.println("版本：4.1");
		System.out.println("\n====>>>>正在初始化<<<<====");
		sstage = stage;
		scene = new Scene(loadFXML("primary"));
		scene.getStylesheets().add(getClass().getResource("sample.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("JRA Tool");
		stage.setResizable(false);
		try {
			stage.getIcons().add(new Image(App.class.getResourceAsStream("/application/icon.png")));
		} catch (Exception e) {
			System.err.println("找不到图标：" + e.toString());
		}
//		Platform.setImplicitExit(false);//不会关闭程序
		r = new Robot();
		// 如果是Windows系统才会显示主窗口
		if (File.separator.equals("\\")) {
			stage.setAlwaysOnTop(true);
			stage.show();
		} else {
			System.out.println("====>>>>非Windows系统，不进入主程序<<<<====");
		}
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.out.println(String.format("[%s]感谢使用Java Remote Assistant！",
						LocalDateTime.now().toString().replace("T", "|")));
				System.exit(0);
			}
		});
		
		/**
		 * 系统托盘，单击显示主界面
		 */
		if (!SystemTray.isSupported()) {
			System.out.println(String.format("[%s]系统似乎不支持系统托盘功能……？",
					LocalDateTime.now().toString().replace("T", "|")));
		} else {
			try {
				URL resource = this.getClass().getResource("icon.png"); // 获得图片路径
				ImageIcon icon = new ImageIcon(resource);
				TrayIcon trayIcon = new TrayIcon(icon.getImage(), "Java Remote Assistant v4.1");
				trayIcon.setImageAutoSize(true);
				SystemTray sysTray = SystemTray.getSystemTray();
				try {
					sysTray.add(trayIcon);
					trayIcon.displayMessage("Woops...欢迎使用!\n        Java Remote Assistant v4.1", "单击系统托盘显示主界面~(=。=)~", TrayIcon.MessageType.INFO);
				} catch (AWTException e1) {
					System.out.println(String.format("[%s]系统托盘AWTException：%s",
							LocalDateTime.now().toString().replace("T", "|"),e1.toString()));
				}
				trayIcon.addActionListener(new ActionListener() { // 给窗体最小化添加事件监听.
					@Override
					public void actionPerformed(ActionEvent e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								showFront();
							}
						});
					}
				});
			} catch (Exception e) {
				System.out.println(String.format("[%s]系统托盘设置出错！%s",
						LocalDateTime.now().toString().replace("T", "|"),e.toString()));
			}
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
						System.out.println(String.format("[%s]锁定状态：检测到%s松开，重新按下。",
								LocalDateTime.now().toString().replace("T", "|"), Key));
						r.keyPress(KeyCode.CONTROL);
					}
				} else if (PrimaryController.getAlt()) {
					if (Key == "Alt") {
						System.out.println(String.format("[%s]锁定状态：检测到%s松开，重新按下。",
								LocalDateTime.now().toString().replace("T", "|"), Key));
						r.keyPress(KeyCode.ALT);
					}
				} else if (PrimaryController.getShift()) {
					if (Key == "Shift") {
						System.out.println(String.format("[%s]锁定状态：检测到%s松开，重新按下。",
								LocalDateTime.now().toString().replace("T", "|"), Key));
						r.keyPress(KeyCode.SHIFT);
					}
				} else if (PrimaryController.getWindows()) {
					if (Key == "Windows") {
						System.out.println(String.format("[%s]锁定状态：检测到%s松开，重新按下。",
								LocalDateTime.now().toString().replace("T", "|"), Key));
						r.keyPress(KeyCode.WINDOWS);
					}
				} else {
					switch (Key) {
					case "Ctrl":
						System.out.println(
								String.format("[%s]当前按键名称：%s", LocalDateTime.now().toString().replace("T", "|"), Key));
						break;
					case "Shift":
						System.out.println(
								String.format("[%s]当前按键名称：%s", LocalDateTime.now().toString().replace("T", "|"), Key));
						break;
					case "Alt":
						System.out.println(
								String.format("[%s]当前按键名称：%s", LocalDateTime.now().toString().replace("T", "|"), Key));
						break;
					case "Windows":
						System.out.println(
								String.format("[%s]当前按键名称：%s", LocalDateTime.now().toString().replace("T", "|"), Key));
						break;
					default:
						System.out.println(
								String.format("[%s]当前按键名称：%s", LocalDateTime.now().toString().replace("T", "|"), Key));
					}
				}
			}

		});
		stage.setAlwaysOnTop(true);
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
			System.out.println(String.format("[%s]界面前置出错。",
					LocalDateTime.now().toString().replace("T", "|")));
		}
		System.out.println(String.format("[%s]界面前置。",
				LocalDateTime.now().toString().replace("T", "|")));
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
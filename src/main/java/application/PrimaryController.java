package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;

public class PrimaryController implements Initializable {
	// 用于停止执行VNC服务的启动
	private final static String EXIT_CODE = "ERROR";
	private static Robot robot = new Robot();
	private double xOffset = 0;
	private double yOffset = 0;

	private static boolean isCtrlPressed = false;
	private static boolean isShiftPressed = false;
	private static boolean isWindowsPressed = false;
	private static boolean isAltPressed = false;

	private static Button sCtrlLock = new Button();
	private static Button sShiftLock = new Button();
	private static Button sAltLock = new Button();
	private static Button sWinLock = new Button();
	private static int DelaySecond = 15;//已失效！修改为左键5s、右键10s、滚轮按键20s
	private static boolean flag = true;


	@FXML
	private VBox vb;
	@FXML
	private Label info;

	@FXML
	private Button LoadVNC;
	@FXML
	private Button ShutdownVNC;
	@FXML
	private Button SystemMonitor;
	@FXML
	private Button ShowDesktop;
	@FXML
	private Button DesktopsRolling;
	@FXML
	private Button InputmethodSwitch;
	@FXML
	private Button CtrlLock = sCtrlLock;
	@FXML
	private Button ShiftLock = sShiftLock;
	@FXML
	private Button AltLock = sAltLock;
	@FXML
	private Button WinLock = sWinLock;
	@FXML
	private Button AHK;
	@FXML
	private Button Keyboard;
	@FXML
	private Button About;

	/**
	 * 移动主窗口
	 */
	@FXML
	void move() {
		// 让鼠标的位置不再默认左上角
		vb.setOnMousePressed((MouseEvent event) -> {
			event.consume();
//			System.out.println("Pressed!");
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
//			xOffset = event.getX();
//			yOffset = event.getY();
		});
		// 拖动窗口事件
		vb.setOnMouseDragged((MouseEvent event) -> {
			event.consume();
			App.setStageX(event.getScreenX() - xOffset);
//			System.out.println("Draged!");
			if (event.getScreenY() - yOffset < 0) {
				App.setStageY(0);
			} else {
				App.setStageY(event.getScreenY() - yOffset);
			}
		});
	}

	/**
	 * 运行VNC服务
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@FXML
	void loadVNC(ActionEvent event) throws InterruptedException {
//		FileOpen:server_launcher.vbs
		App.setTop(false);
		// 输入远程主机地址的对话框
		System.out.println("是否自定义远程主机地址？");
		final String RemoteHost = showInputDialog(
				"询问：请输入远程主机地址。如没有，请留空(一般是留空,直接回车就是)。\n" + "随后的防火墙联网许可、UAC管理员权限请求【请选择>允许<】谢谢！",
				"8.8.8.8:666");
		// 判断是否需要停止执行
		if (RemoteHost.equals(EXIT_CODE)) {
			System.out.println("无效参数,启动远程桌面失败。");
		} else if (isVNCon()) {
			Process cmd;
			String cmd_run = "";

			String stat = "";// 正常输出流
			String error = "";// 错误输出流
			String exit = "";// 退出执行状态码

			try {
				System.out.println("VNC is running.");
				String Connect = ".\\bin\\server\\RAServer\\start_server.exe -controlapp -connect " + RemoteHost;
				cmd_run = Connect;
				cmd = Runtime.getRuntime().exec(cmd_run);

				InputStream IS = cmd.getInputStream();
				InputStreamReader ISR = new InputStreamReader(IS, "gbk");
				BufferedReader BR0 = new BufferedReader(ISR);
				BufferedReader BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));

				String line = null;
				line = null;
				while ((line = BR0.readLine()) != null) {
					stat = stat + line + "\n";
					System.out.println(line);
				}
				while ((line = BR1.readLine()) != null) {
					error = error + line + "\n";
					System.out.println(line);
				}
				int exitValue = cmd.waitFor();
				exitValue = cmd.waitFor();
				exit = String.valueOf(exitValue);
				if (exitValue != 0) {
					Alert cmdError = new Alert(AlertType.WARNING);
					cmdError.setContentText(error);
					cmdError.setResizable(true);
					cmdError.setOnHidden((DialogEvent) -> {
						App.setTop(true);
					});
					cmdError.show();
				} else {
					System.out.println("Cmd exec success on connect command.");
				}
				System.out.println("On connect return exit code:" + exit);
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {
			Process cmd;
			final String cmd_str = "cmd.exe /C start ";
			String cmd_run = "";

			String stat = "";// 正常输出流
			String error = "";// 错误输出流
			String exit = "";// 退出执行状态码

			try {
				// 启动TightVNC
				cmd_run = cmd_str + ".\\bin\\server\\RAServer\\server_launcher.vbs";
				cmd = Runtime.getRuntime().exec(cmd_run);
				InputStream IS = cmd.getInputStream();
				InputStreamReader ISR = new InputStreamReader(IS, "gbk");
				BufferedReader BR0 = new BufferedReader(ISR);
				BufferedReader BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
				String line = null;
				while ((line = BR0.readLine()) != null) {
					stat = stat + line + "\n";
					System.out.println(line);
				}
				while ((line = BR1.readLine()) != null) {
					error = error + line + "\n";
					System.out.println(line);
				}
				int exitValue = cmd.waitFor();
				exit = String.valueOf(exitValue);
				if (exitValue != 0) {
					Alert cmdError = new Alert(AlertType.WARNING);
					cmdError.setContentText(error);
					cmdError.setResizable(true);
					cmdError.setOnHidden((DialogEvent) -> {
						App.setTop(true);
					});
					cmdError.show();
				} else {
					System.out.println("Cmd exec success on start.");
				}
				System.out.println("On start return exit code:" + exit);

				System.out.println("等待TightVNC初始化(2s)……");
				stat = stat + "\n__________我只是一条分割线_________\n";
				error = error + "\n__________我只是一条分割线_________\n";
				IS = null;
				ISR = null;
				BR0 = null;
				BR1 = null;
				Thread.sleep(3000);
				// 只有上一步的启动VNC返回值是0才会进行连接。
				if (exitValue == 0) {
					// 检查VNC
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO 自动生成的方法存根
							try {
								Thread.sleep(5000);
								System.out.println("Time up");
								flag = false;
							} catch (InterruptedException e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}
						}
					}).start();
					while (flag) {
						isVNCon();
					}
					flag = true;
					// 尝试逆向连接远程主机
					String Connect = ".\\bin\\server\\RAServer\\start_server.exe -controlapp -connect " + RemoteHost;
					cmd_run = Connect;
					cmd = Runtime.getRuntime().exec(cmd_run);
					IS = cmd.getInputStream();
					ISR = new InputStreamReader(IS, "gbk");
					BR0 = new BufferedReader(ISR);
					BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
					line = null;
					while ((line = BR0.readLine()) != null) {
						stat = stat + line + "\n";
						System.out.println(line);
					}
					while ((line = BR1.readLine()) != null) {
						error = error + line + "\n";
						System.out.println(line);
					}
					exitValue = cmd.waitFor();
					exit = String.valueOf(exitValue);
					if (exitValue != 0) {
						Alert cmdError = new Alert(AlertType.WARNING);
						cmdError.setContentText(error);
						cmdError.setResizable(true);
						cmdError.setOnHidden((DialogEvent) -> {
							App.setTop(true);
						});
						cmdError.show();
					} else {
						System.out.println("Cmd exec success on connect command.");
					}
					System.out.println("On connect return exit code:" + exit);
				} else {
					System.out.println("在启动VNC时遇到了困难，未连接！");
				}
			} catch (Exception e) {
				System.out.println("Java Exception :\n" + e.toString());
				Alert javaError = new Alert(AlertType.ERROR);
				javaError.setContentText("Java Exception :\n" + e.toString() + "\n\n" + "Error Stream:\n" + error);
				javaError.setResizable(true);
				javaError.setOnHidden((DialogEvent) -> {
					App.setTop(true);
				});
				javaError.show();
			} finally {
				System.out.println("Input Stream:" + stat);
				System.out.println("Error Stream:" + error);
				App.setTop(true);
			}
		}
	}

	/**
	 * 停止VNC服务
	 * 
	 * @param event
	 */
	@FXML
	void shutdownVNC(ActionEvent event) throws InterruptedException {
		App.setTop(false);
		Process cmd;
		final String cmd_str = "cmd.exe /C start ";
		String cmd_run = "";

		String stat = "";// 正常输出流
		String error = "";// 错误输出流
		String exit = "";// 退出执行状态码

		try {
			// 关闭TightVNC
			cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -controlapp -shutdown";
			cmd = Runtime.getRuntime().exec(cmd_run);
			BufferedReader BR0 = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
			BufferedReader BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
			String line = null;
			while ((line = BR0.readLine()) != null) {
				stat = stat + line + "\n";
				System.out.println(line);
			}
			while ((line = BR1.readLine()) != null) {
				error = error + line + "\n";
//				System.out.println(line);
			}
			int exitValue = cmd.waitFor();
			exit = String.valueOf(exitValue);
			if (exitValue != 0) {
				System.out.println("On shutdown:" + error);
//				Alert cmdError = new Alert(AlertType.WARNING);
//				cmdError.setContentText(error);
//				cmdError.setResizable(true);
//				cmdError.setOnHidden((DialogEvent) -> {
//					App.setTop(true);
//				});
//				cmdError.show();
			} else {
				System.out.println("Cmd exec success on shutdown.");
			}
			System.out.println("On shutdown return exit code:" + exit);

			System.out.println("正在检测是否正常结束(2s)……");
			stat = stat + "\n__________我是一条无情的分割线_________\n";
			error = error + "\n__________我是一条无情的分割线_________\n";
			BR0 = null;
			BR1 = null;
			Thread.sleep(2000);
			// 上一步没有正常执行就主动杀死。
			if (exitValue != 0) {
//				String kill = "taskkill /f /t /im start_server.exe";
				String kill = ".\\bin\\server\\RAServer\\kill_server.bat";
				cmd_run = cmd_str + kill;
				cmd = Runtime.getRuntime().exec(cmd_run);
				BR0 = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
				BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
				line = null;
				while ((line = BR0.readLine()) != null) {
					stat = stat + line + "\n";
					System.out.println(line);
				}
				while ((line = BR1.readLine()) != null) {
					error = error + line + "\n";
					System.out.println(line);
				}
				exitValue = cmd.waitFor();
				exit = String.valueOf(exitValue);
				if (exitValue != 0) {
					System.out.println("On kill:" + error);
//					Alert cmdError = new Alert(AlertType.WARNING);
//					cmdError.setContentText(error);
//					cmdError.setResizable(true);
//					cmdError.setOnHidden((DialogEvent) -> {
//						App.setTop(true);
//					});
//					cmdError.show();
				} else {
					System.out.println("Cmd exec success on kill command.");
				}
				System.out.println("On kill return exit code:" + exit);
			} else {
				System.out.println("一次检查");
				// 检查
				try {
					cmd_run = "tasklist";
					cmd = Runtime.getRuntime().exec(cmd_run);
					BufferedReader readTask = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
					LinkedList<String> Tasks = new LinkedList<>();
					boolean isFound = false;
					// 遍历tasklist
					while ((line = readTask.readLine()) != null) {
						Tasks.add(line);
						System.out.println(line);
					}
					for (String task : Tasks) { // 也可以改写 for(int i=0;i<list.size();i++) 这种形式
						if (task.contains("start_server.exe")) {
							isFound = true;
						}
					}
					if (isFound) {
						System.out.println("发现残留进程");
						cmd_run = cmd_str + ".\\bin\\server\\RAServer\\kill_server.bat";
						cmd = Runtime.getRuntime().exec(cmd_run);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Alert cmdError = new Alert(AlertType.WARNING);
					cmdError.setContentText("未能停止远程桌面，请手动打开任务管理器(快捷键:Ctrl+Shift+Esc)\n"
							+ "或者点击下方的“任务管理器”按钮。打开任务管理器后在“详细信息”中找\n" + "到“start_server.exe”，然后'右击'选择'结束任务'。");
					cmdError.setResizable(true);
					cmdError.setOnHidden((DialogEvent) -> {
						App.setTop(true);
					});
					cmdError.show();
					System.out.println("在杀死VNC时遇到了困难:" + e.toString());
				}
			}
		} catch (Exception e) {
			System.out.println("Java Exception :\n" + e.toString());
			Alert javaError = new Alert(AlertType.ERROR);
			javaError.setContentText("Java Exception :\n" + e.toString() + "\n\n" + "Error Stream:\n" + error);
			javaError.setResizable(true);
			javaError.setOnHidden((DialogEvent) -> {
				App.setTop(true);
			});
			javaError.show();
		} finally {
			System.out.println("二次检查");
			// 检查
			try {
				cmd_run = "tasklist";
				cmd = Runtime.getRuntime().exec(cmd_run);
				BufferedReader readTask = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
				LinkedList<String> Tasks = new LinkedList<>();
				boolean isFound = false;
				// 遍历tasklist
				String line = null;
				while ((line = readTask.readLine()) != null) {
					Tasks.add(line);
					System.out.println(line);
				}
				for (String task : Tasks) { // 也可以改写 for(int i=0;i<list.size();i++) 这种形式
					if (task.contains("start_server.exe")) {
						isFound = true;
					}
				}
				if (isFound) {
					System.out.println("发现残留进程");
					cmd_run = cmd_str + ".\\bin\\server\\RAServer\\kill_server.bat";
					cmd = Runtime.getRuntime().exec(cmd_run);
				}
			} catch (Exception e) {
				Alert cmdError = new Alert(AlertType.WARNING);
				cmdError.setContentText("未能停止远程桌面，请手动打开任务管理器(快捷键:Ctrl+Shift+Esc)\n"
						+ "或者点击下方的“任务管理器”按钮。打开任务管理器后在“详细信息”中找\n" + "到“start_server.exe”，然后'右击'选择'结束任务'。");
				cmdError.setResizable(true);
				cmdError.setOnHidden((DialogEvent) -> {
					App.setTop(true);
				});
				cmdError.show();
				System.out.println("在杀死VNC时遇到了困难:" + e.toString());
			}
			System.out.println("Input Stream:" + stat);
			System.out.println("Error Stream:" + error);
			App.setTop(true);
		}
	}

	@FXML
	void openSystemMonitor(ActionEvent event) throws InterruptedException {
		Thread.sleep(500);
		robot.keyPress(KeyCode.CONTROL);
		robot.keyPress(KeyCode.SHIFT);
		robot.keyPress(KeyCode.ESCAPE);
		robot.keyRelease(KeyCode.CONTROL);
		robot.keyRelease(KeyCode.SHIFT);
		robot.keyRelease(KeyCode.ESCAPE);
	}

	@FXML
	void showDesktop(ActionEvent event) throws InterruptedException {
//		Thread.sleep(500);
		robot.keyPress(KeyCode.WINDOWS);
		robot.keyPress(KeyCode.D);
		robot.keyRelease(KeyCode.WINDOWS);
		robot.keyRelease(KeyCode.D);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		App.showFront();
	}

	@FXML
	void inputmethodSwitch(ActionEvent event) throws InterruptedException {
		Thread.sleep(500);
		robot.keyPress(KeyCode.CONTROL);
		robot.keyPress(KeyCode.SPACE);
		robot.keyRelease(KeyCode.CONTROL);
		robot.keyRelease(KeyCode.SPACE);
	}

	@FXML
	void ctrl(MouseEvent event) {
		if (isCtrlPressed) {
			robot.keyRelease(KeyCode.CONTROL);
			isCtrlPressed = false;
			CtrlLock.setText("CTRL未锁定");
			CtrlLock.getStyleClass().add("btn");
		} else {
			robot.keyPress(KeyCode.CONTROL);
			isCtrlPressed = true;
			CtrlLock.setText("CTRL已锁定");
			CtrlLock.getStyleClass().add("on_btn_pressed");
		}
	}

	@FXML
	void shift(MouseEvent event) {
		if (isShiftPressed) {
			robot.keyRelease(KeyCode.SHIFT);
			isShiftPressed = false;
			ShiftLock.setText("SHIFT未锁定");
			ShiftLock.setTextFill(Color.BLACK);
			ShiftLock.getStyleClass().add("btn");
		} else {
			robot.keyPress(KeyCode.SHIFT);
			isShiftPressed = true;
			ShiftLock.setText("SHIFT已锁定");
			ShiftLock.getStyleClass().add("on_btn_pressed");
		}
	}

	@FXML
	void alt(MouseEvent event) {
		if (isAltPressed) {
			robot.keyRelease(KeyCode.ALT);
			isAltPressed = false;
			AltLock.setText("ALT未锁定");
			AltLock.getStyleClass().add("btn");
		} else {
			robot.keyPress(KeyCode.ALT);
			isAltPressed = true;
			AltLock.setText("ALT已锁定");
			AltLock.getStyleClass().add("on_btn_pressed");
		}
	}

	@FXML
	void win(MouseEvent event) {
		if (isWindowsPressed) {
			robot.keyRelease(KeyCode.WINDOWS);
			isWindowsPressed = false;
			WinLock.setText("开始键未锁定");
			WinLock.getStyleClass().add("btn");
		} else {
			robot.keyPress(KeyCode.WINDOWS);
			isWindowsPressed = true;
			WinLock.setText("开始键已锁定");
			WinLock.getStyleClass().add("on_btn_pressed");
		}
	}

	@FXML
	void ahks(MouseEvent event) {
		if(event.getButton() == MouseButton.PRIMARY){
			DelaySecond = 5;
		}
		else if(event.getButton() == MouseButton.SECONDARY) {
			DelaySecond = 10;
		}
		else if(event.getButton() == MouseButton.MIDDLE) {
			DelaySecond = 20;
		}
		else {
			System.out.println(DelaySecond);
		}
		final String filename = showInputDialog("请输入ahk文件名：","sample");
		if (filename.equals(EXIT_CODE)) {
			System.out.println("无效参数");
			return;
		}
		AHK_Delay_Util ahk = new AHK_Delay_Util(filename);
		ahk.start();
	}

	@FXML
	void keyboard(ActionEvent event) throws InterruptedException {
		Thread.sleep(500);
		robot.keyPress(KeyCode.CONTROL);
		robot.keyPress(KeyCode.WINDOWS);
		robot.keyPress(KeyCode.O);
		robot.keyRelease(KeyCode.CONTROL);
		robot.keyRelease(KeyCode.WINDOWS);
		robot.keyRelease(KeyCode.O);
	}

	@FXML
	private void switchToSecondary() throws IOException {
		if (isCtrlPressed || isShiftPressed || isWindowsPressed || isAltPressed) {
			Alert info = new Alert(AlertType.WARNING, "请先解除Ctrl、Alt、Shift和Windows按键的锁定状态。");
			info.setOnHidden((DialogEvent) -> {
				App.setTop(true);
			});
			info.show();
		} else {
			App.setRoot("secondary");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/**
		 * 在不是Windows的系统上将发出警告
		 */
		if (File.separator.equals("/")) {
			System.out.println(File.separator);
			Alert quit = new Alert(AlertType.ERROR, "请在Windows上使用Java Remote Assistant！");
			System.out.println("Not a Windows!");
			quit.show();
		}

		vb.getStyleClass().add("bgm");
		info.getStyleClass().add("font");
		if(!App.start_up) {
			try {
				getIP();
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
		}
		App.start_up = true;

		/**
		 * 切换桌面的方法
		 */
		DesktopsRolling.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
					System.out.println("下个桌面");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					robot.keyPress(KeyCode.CONTROL);
					robot.keyPress(KeyCode.WINDOWS);
					robot.keyPress(KeyCode.RIGHT);
					robot.keyRelease(KeyCode.CONTROL);
					robot.keyRelease(KeyCode.WINDOWS);
					robot.keyRelease(KeyCode.RIGHT);
					App.showFront();
				} else if (event.getButton() == MouseButton.SECONDARY) {
					System.out.println("上个桌面");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					robot.keyPress(KeyCode.CONTROL);
					robot.keyPress(KeyCode.WINDOWS);
					robot.keyPress(KeyCode.LEFT);
					robot.keyRelease(KeyCode.CONTROL);
					robot.keyRelease(KeyCode.WINDOWS);
					robot.keyRelease(KeyCode.LEFT);
					App.showFront();
				} else if (event.getButton() == MouseButton.MIDDLE) {
					System.out.println("新建桌面");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					robot.keyPress(KeyCode.CONTROL);
					robot.keyPress(KeyCode.WINDOWS);
					robot.keyPress(KeyCode.D);
					robot.keyRelease(KeyCode.CONTROL);
					robot.keyRelease(KeyCode.WINDOWS);
					robot.keyRelease(KeyCode.D);
					App.showFront();
				} else {
					System.out.println("Unknown Mouse Key");
				}
			}
		});
	}

	/**
	 * 重命名按钮名字 0,1,2,3分别对应Ctrl、Shift、Alt、Windows
	 * 
	 * @param i
	 * @param string
	 */
	public static void setButtonText(int i, String str) {
		switch (i) {
		case 0:
			sCtrlLock.setText(str);
			break;
		case 1:
			sShiftLock.setText(str);
			break;
		case 2:
			sAltLock.setText(str);
			break;
		case 3:
			sWinLock.setText(str);
			break;
		default:
			System.out.println("update button");
		}
	}

	/**
	 * 检查VNC是否启动
	 * 
	 * @return
	 */
	private static boolean isVNCon() {
		boolean isFound = false;
		try {
			Process cmd;
			String cmd_run = "tasklist";
			cmd = Runtime.getRuntime().exec(cmd_run);
			BufferedReader readTask = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
			LinkedList<String> Tasks = new LinkedList<>();
			String line = null;
			// 遍历tasklist
			while ((line = readTask.readLine()) != null) {
				Tasks.add(line);
				System.out.println(line);
			}
			for (String task : Tasks) { // 也可以改写 for(int i=0;i<list.size();i++) 这种形式
				if (task.contains("start_server.exe")) {
					isFound = true;
				}
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println("VNC未运行。" + e.toString());
		}
		if (isFound) {
			flag = false;
			System.out.println("发现VNC");
			return true;
		}
		return false;
	}

	/**
	 * 显示远程主机地址对话框
	 * 
	 * @param message
	 * @param defaultValue
	 * @return
	 */
	private static String showInputDialog(String message, String defaultValue) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setContentText(message);
		Optional<String> value = dialog.showAndWait();
		String input = null;
		try {
			input = value.get();
		} catch (Exception e) {
			// 不显示报错
//			System.err.println(e.toString());
			Alert a = new Alert(AlertType.INFORMATION, "用户取消");
			a.show();
			a.setOnHidden((DialogEvent) -> {
				App.setTop(true);
			});
			input = EXIT_CODE;
		}
		System.out.println(input);
		if (input.equals(EXIT_CODE)) {
			return EXIT_CODE;
		} else if (input.equals("")) {
			System.out.println("No input,return default value.");
			return defaultValue;
		} else {
			System.out.println("Return user value:" + input);
			return input;
		}
	}

	/**
	 * 获取局域网IP
	 * 
	 * @throws SocketException
	 */
	static void getIP() throws SocketException {// get all local ips
		Enumeration<NetworkInterface> interfs = NetworkInterface.getNetworkInterfaces();
		System.out.println("正在獲取电脑本地IP....");
		int n = 1;
		boolean getStatus = false;
		while (interfs.hasMoreElements()) {
			NetworkInterface interf = interfs.nextElement();
			Enumeration<InetAddress> addres = interf.getInetAddresses();
			if (n == 1 | getStatus) {
				System.out.println("<------第" + n + "组网卡------>");
				getStatus = false;
			}
			while (addres.hasMoreElements()) {
				InetAddress in = addres.nextElement();
				if (in instanceof Inet4Address) {
					System.out.println(" - IPv4地址:" + in.getHostAddress());
					getStatus = true;
				} else if (in instanceof Inet6Address) {
					System.out.println(" - IPv6地址:" + in.getHostAddress());
					getStatus = true;
				}
			}
			if (getStatus) {
				n += 1;
			}
		}
		System.out.println("<--没有第" + n + "组网卡，如果以上结果没有显示出你所在局域网的IP地址。请手动查看您的IPv4地址谢谢-->\n");
		System.out.println("网卡信息获取完毕");
		try {
			System.out.println("\n正在初始化...");
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.err.println("\n======>>>>>> 准备就绪！<<<<<<======");
	}

	/**
	 * 倒计时执行auto hot key 2.0
	 * 注意：中途无法取消！
	 * @author Ryan Yim
	 *
	 */
	private final class AHK_Delay_Util extends Thread {
		String src = "";
		int Sec_src;
		String filename;

		private AHK_Delay_Util(String filename) {
			src = info.getText();
			Sec_src = DelaySecond;
			this.filename = filename;
			setDaemon(true);
		}

		@Override
		public void run() {
			System.out.println("Ahk delay:" + DelaySecond);
			while (true) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// 更新JavaFX的主线程的代码放在此处
						String text = String.format("距离执行还有 %d 秒", DelaySecond);
						info.setText(text);
						System.out.println("JavaFX Label updated.");
					}
				});
				if (DelaySecond == 0) {
					System.out.println("Time up,do.");
					try {
						Process cmd;
						String cmd_run = ".\\bin\\AHK.exe "+ filename +".ahk";
						cmd = Runtime.getRuntime().exec(cmd_run);
						System.out.println("AHK执行结果："+cmd.waitFor());
					} catch (Exception e) {
						System.err.println(e.toString());
					}
					info.setText(src);
					System.out.println("JavaFX Label RESET");
					DelaySecond = Sec_src;
					return;
				}
				try {
					DelaySecond -= 1;
					sleep(1000); // 1秒更新一次显示
					System.out.println("Second-1");
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	/**
	 * 返回Ctrl、Shift、Windows、Alt的锁定状态
	 * 
	 * @return
	 */
	public static boolean getCtrl() {
		return isCtrlPressed;
	}

	public static boolean getShift() {
		return isShiftPressed;
	}

	public static boolean getAlt() {
		return isAltPressed;
	}

	public static boolean getWindows() {
		return isWindowsPressed;
	}
}

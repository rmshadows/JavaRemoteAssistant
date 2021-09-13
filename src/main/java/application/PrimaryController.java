package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Properties;
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
	// 默认连接地址
	private static String defaultRemoteHostAddress = "";
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
	private static int DelaySecond = 15;
	@SuppressWarnings("unused")
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
	 * 初始化
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 加载配置文件
		Properties config = new Properties();
		File conf_file = new File("jra.properties");
		// 配置文件不存在就加载
		if(!conf_file.exists()) {
			try {
				config.setProperty("DefaultIP", "Default IP to Connect");
				config.store(new FileOutputStream(conf_file), "JRA Config File");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else {
			try {
				config.load(new FileInputStream(conf_file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			defaultRemoteHostAddress = config.getProperty("DefaultIP");
			System.out.println("获取配置信息：");
			System.out.println("默认连接地址："+defaultRemoteHostAddress);
		}
		/**
		 * 在不是Windows的系统上将发出警告
		 */
		if (File.separator.equals("/")) {
//			System.out.println(File.separator);
			Alert quit = new Alert(AlertType.ERROR, "请在Windows上使用Java Remote Assistant！");
			System.out
					.println(String.format("[%s]错误：不是一个Windows系统！", LocalDateTime.now().toString().replace("T", "|")));
			quit.show();
		}

		vb.getStyleClass().add("bgm");
		info.getStyleClass().add("font");
		if (!App.start_up) {
			try {
				getIPaddr();
			} catch (SocketException e1) {
				System.out.println(String.format("[%s]获取IP出错：%s", 
						LocalDateTime.now().toString().replace("T", "|"),e1.toString()));
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
					System.out.println(String.format("[%s]下一个桌面。", LocalDateTime.now().toString().replace("T", "|")));
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
					System.out.println(String.format("[%s]上一个桌面。", LocalDateTime.now().toString().replace("T", "|")));
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
					System.out.println(String.format("[%s]新建桌面。", LocalDateTime.now().toString().replace("T", "|")));
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
					System.out.println(
							String.format("[%s]切换桌面错误：未定义该鼠标按键。", LocalDateTime.now().toString().replace("T", "|")));
				}
			}
		});
	}

	/**
	 * 移动主窗口
	 */
	@FXML
	void move() {
		// 让鼠标的位置不再默认左上角
		vb.setOnMousePressed((MouseEvent event) -> {
			event.consume();
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
//			xOffset = event.getX();
//			yOffset = event.getY();
		});
		// 拖动窗口事件
		vb.setOnMouseDragged((MouseEvent event) -> {
			event.consume();
			App.setStageX(event.getScreenX() - xOffset);
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
		App.setTop(false);
		setupVNC();
	}

	/**
	 * 停止VNC服务
	 * 
	 * @param event
	 */
	@FXML
	void shutdownVNC(ActionEvent event) throws InterruptedException {
		removeVNC();
		App.setTop(true);
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
		System.out.println(String.format("[%s]打开任务管理器。", LocalDateTime.now().toString().replace("T", "|")));
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
			System.out.println(String.format("[%s]显示桌面：%s", 
					LocalDateTime.now().toString().replace("T", "|"),e.toString()));
		}
		App.showFront();
		System.out.println(String.format("[%s]显示桌面。", LocalDateTime.now().toString().replace("T", "|")));
	}

	@FXML
	void inputmethodSwitch(ActionEvent event) throws InterruptedException {
		IM_Delay_Util imd = new IM_Delay_Util(4);
		imd.start();
		System.out.println(String.format("[%s]三秒后执行输入法关断动作。", LocalDateTime.now().toString().replace("T", "|")));
	}

	@FXML
	void ctrl(MouseEvent event) {
		if (isCtrlPressed) {
			robot.keyRelease(KeyCode.CONTROL);
			isCtrlPressed = false;
			CtrlLock.setText("CTRL未锁定");
			CtrlLock.getStyleClass().add("btn");
			System.out.println(String.format("[%s]解锁Ctrl键。", LocalDateTime.now().toString().replace("T", "|")));
		} else {
			robot.keyPress(KeyCode.CONTROL);
			isCtrlPressed = true;
			CtrlLock.setText("CTRL已锁定");
			CtrlLock.getStyleClass().add("on_btn_pressed");
			System.out.println(String.format("[%s]锁定Ctrl键。", LocalDateTime.now().toString().replace("T", "|")));
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
			System.out.println(String.format("[%s]解锁Shift键。", LocalDateTime.now().toString().replace("T", "|")));
		} else {
			robot.keyPress(KeyCode.SHIFT);
			isShiftPressed = true;
			ShiftLock.setText("SHIFT已锁定");
			ShiftLock.getStyleClass().add("on_btn_pressed");
			System.out.println(String.format("[%s]锁定Shift键。", LocalDateTime.now().toString().replace("T", "|")));
		}
	}

	@FXML
	void alt(MouseEvent event) {
		if (isAltPressed) {
			robot.keyRelease(KeyCode.ALT);
			isAltPressed = false;
			AltLock.setText("ALT未锁定");
			AltLock.getStyleClass().add("btn");
			System.out.println(String.format("[%s]解锁Alt键。", LocalDateTime.now().toString().replace("T", "|")));
		} else {
			robot.keyPress(KeyCode.ALT);
			isAltPressed = true;
			AltLock.setText("ALT已锁定");
			AltLock.getStyleClass().add("on_btn_pressed");
			System.out.println(String.format("[%s]锁定Alt键。", LocalDateTime.now().toString().replace("T", "|")));
		}
	}

	@FXML
	void win(MouseEvent event) {
		if (isWindowsPressed) {
			robot.keyRelease(KeyCode.WINDOWS);
			isWindowsPressed = false;
			WinLock.setText("开始键未锁定");
			WinLock.getStyleClass().add("btn");
			System.out.println(String.format("[%s]解锁开始键。", LocalDateTime.now().toString().replace("T", "|")));
		} else {
			robot.keyPress(KeyCode.WINDOWS);
			isWindowsPressed = true;
			WinLock.setText("开始键已锁定");
			WinLock.getStyleClass().add("on_btn_pressed");
			System.out.println(String.format("[%s]锁定开始键。", LocalDateTime.now().toString().replace("T", "|")));
		}
	}

	@FXML
	void ahks(MouseEvent event) {
		App.setTop(false);
		if (event.getButton() == MouseButton.PRIMARY) {
			DelaySecond = 5;
		} else if (event.getButton() == MouseButton.SECONDARY) {
			DelaySecond = 10;
		} else if (event.getButton() == MouseButton.MIDDLE) {
			DelaySecond = 20;
		} else {
			System.out.println(
					String.format("[%s]自定义按键设置延时：%d", LocalDateTime.now().toString().replace("T", "|"), DelaySecond));
		}
		final String filename = userInputDialog("请输入ahk文件名(ahks文件夹中)：", "sample");
		if (filename.equals(EXIT_CODE)) {
			System.out.println(
					String.format("[%s]自定义按键：无效Auto Hot Key脚本参数。", LocalDateTime.now().toString().replace("T", "|")));
			return;
		}
		AHK_Delay_Util ahk = new AHK_Delay_Util(filename);
		ahk.start();
		App.setTop(true);
	}

	@FXML
	void keyboard(ActionEvent event) throws InterruptedException {
		System.out.println(String.format("[%s]打开屏幕键盘。", LocalDateTime.now().toString().replace("T", "|")));
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
	
	/**
	 * 启动VNC
	 * 
	 * @throws InterruptedException
	 */
	private void setupVNC() throws InterruptedException {
		// 检查VNC服务是否注册
		if (isServiceReg()) {
			System.out.println(String.format("[%s]VNC服务已注册。", LocalDateTime.now().toString().replace("T", "|")));
			// 服务已注册，VNC已启动，直接尝试连接。
			if (isVNCon()) {
				System.out.println(
						String.format("[%s]检测到VNC服务进程，开始直连……", LocalDateTime.now().toString().replace("T", "|")));
				// 连接远程主机
				if (servConnect() == 0) {
					// 成功就返回
					System.out.println(String.format("[%s]====>>>>连接远程主机完成<<<<====",
							LocalDateTime.now().toString().replace("T", "|")));
					return;
				} else {
					// 不成功就等待2秒再次尝试
					System.out.println(String.format("[%s]servConnect返回值不为零，2秒后重试。",
							LocalDateTime.now().toString().replace("T", "|")));
					Thread.sleep(1000);
					// 如果连接返回值不为零，弹出警示框
					if (servConnect() != 0) {
						System.out.println(String.format("[%s]servConnect返回值不为零，请先关闭远程桌面!",
								LocalDateTime.now().toString().replace("T", "|")));
						Alert cmdError = new Alert(AlertType.WARNING);
						cmdError.setContentText("远程主机连接失败。");
						cmdError.setResizable(true);
						cmdError.setOnHidden((DialogEvent) -> {
							App.setTop(true);
						});
						cmdError.show();
					}
				}
			}
			// 服务已注册，VNC未启动，尝试启动服务，并连接。
			else {
				System.out.println(
						String.format("[%s]未检测到VNC服务进程，启动中……", LocalDateTime.now().toString().replace("T", "|")));
				if (startServ() == 0) {
					System.out.println(String.format("[%s]====>>>>服务启动完成<<<<====",
							LocalDateTime.now().toString().replace("T", "|")));
					if (isVNCon()) {
						System.out.println(String.format("[%s]检测到VNC服务进程，开始连接……",
								LocalDateTime.now().toString().replace("T", "|")));
						setupVNC();
						return;
					}
				} else {
					System.out.println(String.format("[%s]startServ返回值不为零，2秒后进行检测并重试。",
							LocalDateTime.now().toString().replace("T", "|")));
					Thread.sleep(1000);
					if (isVNCon()) {
						System.out.println(String.format("[%s]检测到VNC服务启动，不进行重试。",
								LocalDateTime.now().toString().replace("T", "|")));
						System.out.println(String.format("[%s]检测到VNC服务进程，开始连接……",
								LocalDateTime.now().toString().replace("T", "|")));
						setupVNC();
						return;
					} else {
						System.out.println(
								String.format("[%s]未检测到VNC服务启动，重试。", LocalDateTime.now().toString().replace("T", "|")));
						if (startServ() == 0) {
							System.out.println(
									String.format("[%s]服务启动完成,开始检测", LocalDateTime.now().toString().replace("T", "|")));
							if (isVNCon()) {
								System.out.println(String.format("[%s]检测到VNC服务进程，开始连接……",
										LocalDateTime.now().toString().replace("T", "|")));
								setupVNC();
								return;
							}
						} else {
							System.out.println(
									String.format("[%s]启动失败。", LocalDateTime.now().toString().replace("T", "|")));
							Alert cmdError = new Alert(AlertType.WARNING);
							cmdError.setContentText("远程桌面启动失败。");
							cmdError.setResizable(true);
							cmdError.setOnHidden((DialogEvent) -> {
								App.setTop(true);
							});
							cmdError.show();
						}
					}
				}
			}
		}
		// 服务未注册
		else {
			System.out.println(String.format("[%s]VNC服务未注册。", LocalDateTime.now().toString().replace("T", "|")));
			// 服务未注册，VNC已启动，结束当前VNC。
			if (isVNCon()) {
				System.out.println(String.format("[%s]猜测可能是以App形式运行的VNC，尝试结束。",
						LocalDateTime.now().toString().replace("T", "|")));
				try {
					Process cmd;
					String cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -controlapp -shutdown";
					cmd = Runtime.getRuntime().exec(cmd_run);
					cmd.waitFor();
				} catch (Exception e) {
					System.out.println(String.format("[%s]以App形式结束尝试结束失败。",
							LocalDateTime.now().toString().replace("T", "|")));
				}
				if(isVNCon()) {
					System.out.println(String.format("[%s]执行强制关闭。",
							LocalDateTime.now().toString().replace("T", "|")));
					removeVNC();
					setupVNC();
					return;
				}else {
					System.out.println(String.format("[%s]以App形式主动结束成功。",
							LocalDateTime.now().toString().replace("T", "|")));
					setupVNC();
					return;
				}
			}
			// 服务未注册，VNC未启动。
			else {
				System.out.println(
						String.format("[%s]未检测到VNC服务进程，重新注册中……", LocalDateTime.now().toString().replace("T", "|")));
				if (reRegServ() == 0) {
					System.out.println(String.format("[%s]====>>>>服务注册完成<<<<====",
							LocalDateTime.now().toString().replace("T", "|")));
					setupVNC();
					return;
				} else {
					System.out.println(String.format("[%s]reRegServ返回值不为零，2秒后重试。",
							LocalDateTime.now().toString().replace("T", "|")));
					Thread.sleep(2000);
					if (isServiceReg()) {
						System.out.println(String.format("[%s]====>>>>检测到服务注册完成<<<<====",
								LocalDateTime.now().toString().replace("T", "|")));
						setupVNC();
						return;
					} else {
						if (reRegServ() != 0) {
							System.out.println(String.format("[%s]reRegServ返回值不为零，请重新加载!",
									LocalDateTime.now().toString().replace("T", "|")));
						}
					}
				}
			}
		}
	}

	/**
	 * 注册VNC服务
	 * 
	 * @return
	 */
	private int reRegServ() {
		Process cmd;
		String cmd_run = "";
		String stat = "";// 正常输出流
		String error = "";// 错误输出流
		String exit = "";// 退出执行状态码
		try {
			// 注册TightVNC
			System.out.println(
					String.format("[%s]reRegServ():注册VNC服务。", LocalDateTime.now().toString().replace("T", "|")));
			cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -reinstall";
			cmd = Runtime.getRuntime().exec(cmd_run);
			InputStream IS = cmd.getInputStream();
			InputStreamReader ISR = new InputStreamReader(IS, "gbk");
			BufferedReader BR0 = new BufferedReader(ISR);
			BufferedReader BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
			String line = null;
			while ((line = BR0.readLine()) != null) {
				stat = stat + line + "\n";
				System.out.println(String.format("[%s]s: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			while ((line = BR1.readLine()) != null) {
				error = error + line + "\n";
				System.out.println(String.format("[%s]e: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			int exitValue = cmd.waitFor();
			exit = String.valueOf(exitValue);
			if (exitValue != 0) {
				System.out.println(String.format("[%s]reRegServ():VNC服务注册返回值不为零，弹出警示框，返回值：%s",
						LocalDateTime.now().toString().replace("T", "|"), exit));
			} else {
				System.out.println(String.format("[%s]reRegServ():VNC服务注册执行完成，正在启动服务，返回值：%s",
						LocalDateTime.now().toString().replace("T", "|"), exit));
			}
		} catch (Exception e) {
			System.out.println(String.format("[%s]reRegServ():Java Exception:%s",
					LocalDateTime.now().toString().replace("T", "|"), e.toString()));
		}
		return Integer.valueOf(exit);
	}

	/**
	 * 启动VNC服务
	 * 
	 * @return
	 */
	private int startServ() {
		Process cmd;
		String cmd_run = "";
		String stat = "";// 正常输出流
		String error = "";// 错误输出流
		String exit = "";// 退出执行状态码
		try {
			// 启动TightVNC
			System.out.println(
					String.format("[%s]startServ():启动VNC服务。", LocalDateTime.now().toString().replace("T", "|")));
			cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -start";
			cmd = Runtime.getRuntime().exec(cmd_run);
			InputStream IS = cmd.getInputStream();
			InputStreamReader ISR = new InputStreamReader(IS, "gbk");
			BufferedReader BR0 = new BufferedReader(ISR);
			BufferedReader BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
			String line = null;
			while ((line = BR0.readLine()) != null) {
				stat = stat + line + "\n";
				System.out.println(String.format("[%s]s: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			while ((line = BR1.readLine()) != null) {
				error = error + line + "\n";
				System.out.println(String.format("[%s]e: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			int exitValue = cmd.waitFor();
			exit = String.valueOf(exitValue);
			if (exitValue != 0) {
				System.out.println(String.format("[%s]startServ():VNC服务启动返回值不为零，弹出警示框，返回值：%s",
						LocalDateTime.now().toString().replace("T", "|"), exit));
			} else {
				System.out.println(String.format("[%s]startServ():VNC服务启动执行完成，返回值：%s",
						LocalDateTime.now().toString().replace("T", "|"), exit));
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println(String.format("[%s]startServ():Java Exception:%s",
					LocalDateTime.now().toString().replace("T", "|"), e.toString()));
		}
		return Integer.valueOf(exit);
	}

	/**
	 * 连接远程主机地址
	 * 
	 * @param Host
	 * @return
	 */
	private int servConnect() {
		// 输入远程主机地址的对话框
		System.out.println(String.format("[%s]显示远程主机对话框。", LocalDateTime.now().toString().replace("T", "|")));
		final String RemoteHost = userInputDialog(
				"询问：请输入远程主机地址。如没有，请留空(一般是留空,直接回车就是)。\n" + "随后的防火墙联网许可、UAC管理员权限请求【请选择>允许<】谢谢！",
				defaultRemoteHostAddress);
		// 判断是否需要停止执行
		if (RemoteHost.equals(EXIT_CODE)) {
			System.out.println(String.format("[%s]无效参数,启动远程桌面失败。", LocalDateTime.now().toString().replace("T", "|")));
		}
		Process cmd;
		String cmd_run = "";
		String stat = "";// 正常输出流
		String error = "";// 错误输出流
		String exit = "";// 退出执行状态码
		try {
			System.out
					.println(String.format("[%s]servConnect:开始连接……", LocalDateTime.now().toString().replace("T", "|")));
			String Connect = ".\\bin\\server\\RAServer\\start_server.exe -controlservice -connect " + RemoteHost;
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
				System.out.println(String.format("[%s]s: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			while ((line = BR1.readLine()) != null) {
				error = error + line + "\n";
				System.out.println(String.format("[%s]e: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			int exitValue = cmd.waitFor();
			exitValue = cmd.waitFor();
			exit = String.valueOf(exitValue);
			if (exitValue != 0) {
				System.out.println(String.format("[%s]servConnect:VNC连接返回值不为零,弹出提示框，返回值：%s",
						LocalDateTime.now().toString().replace("T", "|"), exit));
			} else {
				System.out.println(String.format("[%s]servConnect:VNC连接执行完毕，返回值：%s",
						LocalDateTime.now().toString().replace("T", "|"), exit));
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println(String.format("[%s]servConnect - JavaException：%s",
					LocalDateTime.now().toString().replace("T", "|"), e.toString()));
		}
		App.setTop(true);
		return Integer.valueOf(exit);
	}

	/**
	 * 停止VNC服务并卸载
	 */
	private void removeVNC() {
		System.out.println(String.format("[%s]开始关闭VNC服务。", LocalDateTime.now().toString().replace("T", "|")));
		App.setTop(false);
		Process cmd;
//		final String cmd_str = "cmd.exe /C start ";
//		System.out.println(cmd_str);
		String cmd_run = "";

		String stat = "";// 正常输出流
		String error = "";// 错误输出流
		String exit = "";// 退出执行状态码

		// 先检测VNC服务是否运行
		if (isVNCon()) {
			try {
				// 关闭TightVNC
				System.out.println(String.format("[%s]正在主动停止VNC服务。", LocalDateTime.now().toString().replace("T", "|")));
				cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -stop";
				cmd = Runtime.getRuntime().exec(cmd_run);
				BufferedReader BR0 = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
				BufferedReader BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
				String line = null;
				while ((line = BR0.readLine()) != null) {
					stat = stat + line + "\n";
					System.out.println(
							String.format("[%s]s: %s", LocalDateTime.now().toString().replace("T", "|"), line));
				}
				while ((line = BR1.readLine()) != null) {
					error = error + line + "\n";
					System.out.println(
							String.format("[%s]e: %s", LocalDateTime.now().toString().replace("T", "|"), line));
				}
				int exitValue = cmd.waitFor();
				exit = String.valueOf(exitValue);
				System.out.println(
						String.format("[%s]第一次主动停止服务返回值：%s", LocalDateTime.now().toString().replace("T", "|"), exit));
				if (exitValue != 0) {
					System.out.println(String.format("[%s]第一次主动停止服务返回值不为零：%s",
							LocalDateTime.now().toString().replace("T", "|"), exit));
				} else {
					System.out.println(String.format("[%s]第一次主动停止VNC命令正常运行，返回值：%s。",
							LocalDateTime.now().toString().replace("T", "|"), exit));
				}
				System.out.println(
						String.format("[%s]正在检测VNC服务是否正常停止(2.5s)……", LocalDateTime.now().toString().replace("T", "|")));
				stat = stat + "\n__________我是一条无情的分割线_________\n";
				error = error + "\n__________我是一条无情的分割线_________\n";
				BR0 = null;
				BR1 = null;
				Thread.sleep(2500);
				// 上一步没有正常执行就主动杀死。
				if (exitValue != 0) {
					System.out.println(String.format("[%s]第一次主动关闭失败，检测VNC服务是否运行。",
							LocalDateTime.now().toString().replace("T", "|")));
					if (isVNCon()) {
						System.out.println(String.format("[%s]检测到VNC服务未结束，第二次主动关闭开始。",
								LocalDateTime.now().toString().replace("T", "|")));
						try {
							cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -stop";
							cmd = Runtime.getRuntime().exec(cmd_run);
						} catch (Exception e) {
							System.out.println(String.format("[%s]第二次主动关闭失败：%s",
									LocalDateTime.now().toString().replace("T", "|"), e.toString()));
						}
					}
					Thread.sleep(2400);
					if (isVNCon()) {
						System.out.println(
								String.format("[%s]主动关闭失败，尝试强制关闭。", LocalDateTime.now().toString().replace("T", "|")));
//						String kill = "taskkill /f /t /im start_server.exe";
						System.out.println(
								String.format("[%s]第一次强制关闭开始。", LocalDateTime.now().toString().replace("T", "|")));
						String kill = ".\\bin\\server\\RAServer\\kill_server.bat";
						cmd_run = kill;
						cmd = Runtime.getRuntime().exec(cmd_run);
						BR0 = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
						BR1 = new BufferedReader(new InputStreamReader(cmd.getErrorStream(), "gbk"));
						line = null;
						while ((line = BR0.readLine()) != null) {
							stat = stat + line + "\n";
							System.out.println(
									String.format("[%s]s: %s", LocalDateTime.now().toString().replace("T", "|"), line));
						}
						while ((line = BR1.readLine()) != null) {
							error = error + line + "\n";
							System.out.println(
									String.format("[%s]e: %s", LocalDateTime.now().toString().replace("T", "|"), line));
						}
						exitValue = cmd.waitFor();
						exit = String.valueOf(exitValue);
						System.out.println(String.format("[%s]第一次强制关闭返回值：%s",
								LocalDateTime.now().toString().replace("T", "|"), exit));
						if (exitValue != 0) {
							System.out.println(String.format("[%s]检测到第一次强制关闭返回值不为零：%s",
									LocalDateTime.now().toString().replace("T", "|"), exit));
						} else {
							System.out.println(String.format("[%s]第一次强制关闭执行成功，返回值：%s",
									LocalDateTime.now().toString().replace("T", "|"), exit));
						}
					}
				} else {
					System.out.println(String.format("[%s]执行第一次检查。", LocalDateTime.now().toString().replace("T", "|")));
					if (isVNCon()) {
						System.out.println(String.format("[%s]一次检查，发现残留进程，执行第三次主动停止。",
								LocalDateTime.now().toString().replace("T", "|")));
						try {
//							cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -controlapp -shutdown";
							cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -stop";
							cmd = Runtime.getRuntime().exec(cmd_run);
							cmd.waitFor();
						} catch (Exception e) {
//							System.out.println(e.toString());
							System.out.println(String.format("[%s]第三次主动停止失败：%s",
									LocalDateTime.now().toString().replace("T", "|"), e.toString()));
						}
					}
					// 再次检查
					try {
						System.out.println(String.format("[%s]再次检查。", 
								LocalDateTime.now().toString().replace("T", "|")));
						cmd_run = "tasklist";
						cmd = Runtime.getRuntime().exec(cmd_run);
						BufferedReader readTask = new BufferedReader(
								new InputStreamReader(cmd.getInputStream(), "gbk"));
						LinkedList<String> Tasks = new LinkedList<>();
						boolean isFound = false;
						// 遍历tasklist
						while ((line = readTask.readLine()) != null) {
							Tasks.add(line);
							System.out.println(String.format("[%s] %s", 
									LocalDateTime.now().toString().replace("T", "|"),line));
						}
						for (String task : Tasks) { // 也可以改写 for(int i=0;i<list.size();i++) 这种形式
							if (task.contains("start_server.exe")) {
								isFound = true;
							}
						}
						if (isFound) {
							System.out.println(String.format("[%s]发现残留进程,执行第二次强制关闭。",
									LocalDateTime.now().toString().replace("T", "|")));
							cmd_run = ".\\bin\\server\\RAServer\\kill_server.bat";
							cmd = Runtime.getRuntime().exec(cmd_run);
							exitValue = cmd.waitFor();
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
						System.out.println(String.format("[%s]在杀死VNC时遇到了困难[1]: %s",
								LocalDateTime.now().toString().replace("T", "|"), e.toString()));
					}
				}
			} catch (Exception e) {
				System.out.println(String.format("[%s]杀死进程 - Exception : %s",
						LocalDateTime.now().toString().replace("T", "|"), e.toString()));
				Alert javaError = new Alert(AlertType.ERROR);
				javaError.setContentText("Java Exception :\n" + e.toString() + "\n\n" + "Error Stream:\n" + error);
				javaError.setResizable(true);
				javaError.setOnHidden((DialogEvent) -> {
					App.setTop(true);
				});
				javaError.show();
			} finally {
				System.out.println(String.format("[%s]开始执行二次检查。", LocalDateTime.now().toString().replace("T", "|")));
				// 检查
				try {
					if (isVNCon()) {
						System.out.println(String.format("[%s]发现残留进程，执行第三次强制关闭。",
								LocalDateTime.now().toString().replace("T", "|")));
						cmd_run = ".\\bin\\server\\RAServer\\kill_server.bat";
						cmd = Runtime.getRuntime().exec(cmd_run);
						cmd.wait();
					} else {
						System.out.println(
								String.format("[%s]二次检查未发现VNC进程。", LocalDateTime.now().toString().replace("T", "|")));
					}
				} catch (Exception e) {
					Alert cmdError = new Alert(AlertType.WARNING);
					cmdError.setContentText("如果未能停止远程桌面，请手动打开任务管理器(快捷键:Ctrl+Shift+Esc)\n"
							+ "或者点击下方的“任务管理器”按钮。打开任务管理器后在“详细信息”中找\n" + "到“start_server.exe”，然后'右击'选择'结束任务'。");
					cmdError.setResizable(true);
					cmdError.setOnHidden((DialogEvent) -> {
						App.setTop(true);
					});
					cmdError.show();
					System.out.println(String.format("[%s]在杀死VNC进程时遇到了困难[2]: %s",
							LocalDateTime.now().toString().replace("T", "|"), e.toString()));
					App.setTop(true);
				}
				// 尝试卸载服务
				System.out.println(String.format("[%s]开始卸载VNC服务。", LocalDateTime.now().toString().replace("T", "|")));
				try {
					cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -remove";
					cmd = Runtime.getRuntime().exec(cmd_run);
					cmd.waitFor();
				} catch (Exception e2) {
					System.out.println(String.format("[%s]卸载服务出错: %s", LocalDateTime.now().toString().replace("T", "|"),
							e2.toString()));
					try {
						if (isVNCon()) {
							cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -stop";
							cmd = Runtime.getRuntime().exec(cmd_run);
							cmd.waitFor();
						}
						cmd_run = ".\\bin\\server\\RAServer\\start_server.exe -remove";
						cmd = Runtime.getRuntime().exec(cmd_run);
						cmd.wait();
					} catch (Exception e3) {
						System.out.println(String.format(
								"[%s]二次卸载服务出错，停止尝试。" + "请进入bin/server/RAServer/目录，手动在powershell或者cmd中输入"
										+ "“start_server.exe -remove”卸载",
								LocalDateTime.now().toString().replace("T", "|")));
						App.setTop(true);
					}
				}
//				System.out.println(
//						String.format("[%s]Input Stream: %s", LocalDateTime.now().toString().replace("T", "|"), stat));
//				System.out.println(
//						String.format("[%s]Error Stream: %s", LocalDateTime.now().toString().replace("T", "|"), error));
//				App.setTop(true);
			}
		} else {
			System.out.println(
					String.format("[%s]未检出到VNC服务进程，不进行关闭操作。", LocalDateTime.now().toString().replace("T", "|")));
			App.setTop(true);
		}
		App.setTop(true);
		System.out
				.println(String.format("[%s]====>>>>停止进程结束<<<<====", LocalDateTime.now().toString().replace("T", "|")));
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
			System.out.println(String.format("[%s]setButtonText():未定义按键ID：%d 和字符串 %s",
					LocalDateTime.now().toString().replace("T", "|"), i, str));
		}
	}

	/**
	 * 检查VNC是否注册为服务
	 * 
	 * @return
	 */
	private static boolean isServiceReg() {
		boolean hasReg = false;
		try {
			Process cmd;
			String cmd_run = "sc.exe query tvnserver";
			cmd = Runtime.getRuntime().exec(cmd_run);
			BufferedReader readServ = new BufferedReader(new InputStreamReader(cmd.getInputStream(), "gbk"));
			LinkedList<String> Services = new LinkedList<>();
			String line = null;
			// 遍历service
			while ((line = readServ.readLine()) != null) {
				Services.add(line);
				System.out.println(String.format("[%s]read: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			for (String task : Services) { // 也可以改写 for(int i=0;i<list.size();i++) 这种形式
				if (task.contains("tvnserver")) {
					hasReg = true;
				}
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println(String.format("[%s]isServiceReg()出现Exception : %s",
					LocalDateTime.now().toString().replace("T", "|"), e.toString()));
		}
		if (hasReg) {
			flag = false;
			System.out.println(
					String.format("[%s]isServiceReg()：VNC服务已注册。", LocalDateTime.now().toString().replace("T", "|")));
			return true;
		}
		return false;
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
				System.out.println(String.format("[%s]read: %s", LocalDateTime.now().toString().replace("T", "|"), line));
			}
			for (String task : Tasks) { // 也可以改写 for(int i=0;i<list.size();i++) 这种形式
				if (task.contains("start_server.exe")) {
					isFound = true;
				}
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			System.out.println(String.format("[%s]isVNCon()出现Exception : %s",
					LocalDateTime.now().toString().replace("T", "|"), e.toString()));
		}
		if (isFound) {
			flag = false;
			System.out.println(
					String.format("[%s]isVNCon()：发现VNC服务正在运行。", LocalDateTime.now().toString().replace("T", "|")));
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
	private static String userInputDialog(String message, String defaultValue) {
		System.out.println(String.format("[%s]等待用户输入远程主机地址。", LocalDateTime.now().toString().replace("T", "|")));
		TextInputDialog dialog = new TextInputDialog();
		dialog.setContentText(message);
		Optional<String> value = dialog.showAndWait();
		String input = null;
		try {
			input = value.get();
		} catch (Exception e) {
			System.out.println(
					String.format("[%s]用户取消：%s", LocalDateTime.now().toString().replace("T", "|"), e.toString()));
			Alert a = new Alert(AlertType.INFORMATION, "用户取消");
			a.show();
			a.setOnHidden((DialogEvent) -> {
				App.setTop(true);
			});
			input = EXIT_CODE;
		}
		System.out.println(String.format("[%s]获取到用户自定义远程主机参数：%s", 
				LocalDateTime.now().toString().replace("T", "|"),input));
		if (input.equals(EXIT_CODE)) {
			return EXIT_CODE;
		} else if (input.equals("")) {
			System.out.println(String.format("[%s]userInputDialog：用户未输入有效字符，返回默认值。",
					LocalDateTime.now().toString().replace("T", "|")));
			return defaultValue;
		} else {
			System.out.println(
					String.format("[%s]返回远程主机地址: %s", LocalDateTime.now().toString().replace("T", "|"), input));
			return input;
		}
	}

	/**
	 * 获取局域网IP
	 * 
	 * @throws SocketException
	 */
	static void getIPaddr() throws SocketException {// get all local ips
		Enumeration<NetworkInterface> interfs = NetworkInterface.getNetworkInterfaces();
		System.out.println(String.format("[%s]正在獲取电脑本地IP地址....\n\n", LocalDateTime.now().toString().replace("T", "|")));
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
		System.out.println("<--没有第" + n + "组网卡，如果以上结果没有显示出你所在局域网的IP地址。请手动查看您的IPv4地址谢谢-->");
		System.out.println(String.format("\n\n[%s]网卡信息输出完毕。", LocalDateTime.now().toString().replace("T", "|")));
		try {
			System.out.println(String.format("[%s]请稍等片刻……", LocalDateTime.now().toString().replace("T", "|")));
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("\n======>>>>>> 准备就绪！<<<<<<======\n");
	}

	/**
	 * 倒计时执行auto hot key 2.0 注意：中途无法取消！
	 * 
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
			System.out.println(String.format("[%s]进入AHK_Delay_Util倒计时：%d 秒",
					LocalDateTime.now().toString().replace("T", "|"), DelaySecond));
			while (true) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// 更新JavaFX的主线程的代码放在此处
						String text = String.format("距离执行还有 %d 秒", DelaySecond);
						info.setText(text);
						System.out.println(String.format("[%s]距离Auto Hot Key脚本执行还有 %d 秒。",
								LocalDateTime.now().toString().replace("T", "|"), DelaySecond));
					}
				});
				if (DelaySecond == 0) {
					System.out.println(String.format("[%s]AHK_Delay_Util：捕获计时器讯号（0），" + "执行Auto Hot Key脚本中……",
							LocalDateTime.now().toString().replace("T", "|")));
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// 更新JavaFX的主线程的代码放在此处
							info.setText(src);
							System.out.println(String.format("[%s]AHK_Delay_Util：重置JavaFX标签。",
							LocalDateTime.now().toString().replace("T", "|")));
						}
					});
					try {
						Process cmd;
						String cmd_run = ".\\bin\\AHK.exe .\\ahks\\" + filename + ".ahk";
						cmd = Runtime.getRuntime().exec(cmd_run);
						System.out.println(String.format("[%s]%s.ahk执行结果：%d",
								LocalDateTime.now().toString().replace("T", "|"), filename, cmd.waitFor()));
					} catch (Exception e) {
						System.out.println(String.format("[%s]AHK_Delay_Util执行错误：%s",
							LocalDateTime.now().toString().replace("T", "|"),e.toString()));
					}
					DelaySecond = Sec_src;
					System.out.println(String.format("[%s]====>>>>AHK_Delay_Util 执行完成<<<<====",
							LocalDateTime.now().toString().replace("T", "|")));
					return;
				}
				try {
					DelaySecond -= 1;
					sleep(1000); // 1秒更新一次显示
//					System.out.println("Second-1");
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println(String.format("[%s]AHK_Delay_Util计时出现错误[退出]：%s",
							LocalDateTime.now().toString().replace("T", "|"), e.toString()));
					System.exit(1);
				}
			}
		}
	}
	
	/**
	 * 输入法延时关断
	 * @author Ryan Yim
	 *
	 */
	private final class IM_Delay_Util extends Thread {
		String src = "";
		private IM_Delay_Util(int sec) {
			src = info.getText();
			DelaySecond = sec;
			setDaemon(true);
		}
		@Override
		public void run() {
			System.out.println(String.format("[%s]进入Delay_Util倒计时：%d 秒",
					LocalDateTime.now().toString().replace("T", "|"), DelaySecond));
			while (true) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// 更新JavaFX的主线程的代码放在此处
						String text = String.format("距离执行还有 %d 秒", DelaySecond);
						info.setText(text);
						System.out.println(String.format("[%s]距离执行还有 %d 秒。",
								LocalDateTime.now().toString().replace("T", "|"), DelaySecond));
					}
				});
				if (DelaySecond == 0) {
					System.out.println(String.format("[%s]Delay_Util：捕获计时器讯号（0）。",
							LocalDateTime.now().toString().replace("T", "|")));
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							robot.keyPress(KeyCode.CONTROL);
							robot.keyPress(KeyCode.SPACE);
							robot.keyRelease(KeyCode.CONTROL);
							robot.keyRelease(KeyCode.SPACE);
							info.setText(src);
							System.out.println(String.format("[%s]Delay_Util：重置JavaFX标签。",
									LocalDateTime.now().toString().replace("T", "|"), DelaySecond));
						}
					});
					System.out.println(String.format("[%s]====>>>>Delay_Util 执行完成<<<<====",
							LocalDateTime.now().toString().replace("T", "|")));
					return;
				}
				try {
					DelaySecond -= 1;
					sleep(1000); // 1秒更新一次显示
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println(String.format("[%s]Delay_Util出现错误[退出]：%s",
							LocalDateTime.now().toString().replace("T", "|"), e.toString()));
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

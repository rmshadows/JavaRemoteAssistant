package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * 帮助界面的控制器
 * @author Ryan Yim
 *
 */
public class SecondaryController implements Initializable{

	@FXML
	private Label Help;

	@FXML
	private Button Back2main;
	
	/**
	 * 返回上级（主菜单）
	 * @throws IOException
	 */
	@FXML
	private void switchToPrimary() throws IOException {
		App.setRoot("primary");
	}
	
	/**
	 * 初始化
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO 自动生成的方法存根
		final String HELP = "功能：远程协助\n\n平台：Windows\n\n版本："+App.version+"\n\n"
				+ "使用方式：\n打开软件后，请点击一下“加载远程桌面”按钮。"
				+ "随后可能弹出UAC询问窗口“是否以管理员权限运行”，"
				+ "请选择同意。可能还会弹出Windows防火墙询问窗口“是否允许连接网络”。"
				+ "同样，请选择同意。\n\n注意事项：如果远程桌面掉线，请稍等片刻"
				+ "后再重新点击一次“加载远程桌面”按钮。\n\n单击“主菜单”按钮返回上级菜单。";
		Help.setText(HELP);
	}
}
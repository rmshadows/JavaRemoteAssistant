/**
 * Java远程桌面助手
 * 使用TightVNC
 * 版本：2.0
 * @author Ryan Yim
 *
 */
module application {
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;

	opens application to javafx.fxml;
	
	exports application;
}
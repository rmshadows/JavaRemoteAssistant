/**
 * Java远程桌面助手
 * 基于TightVNC的远程协作服务助手，JRA部署于被控端。控制端VNC_Viewer请自行到TightVNC下载。
 * 版本：4.1
 * @author Ryan Yim
 * 
 * 3.1更新：
 * 部署方式从app更改为service。
 * 优化了部分体验。
 * 优化日志。
 * 增加了系统托盘功能，多桌面不再迷路。
 */
module application {
	requires java.desktop;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;

	opens application to javafx.fxml;
	
	exports application;
}
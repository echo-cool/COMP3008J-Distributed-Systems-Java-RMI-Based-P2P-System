package com.echo.p2p_project.server.gui;

import cn.hutool.core.io.resource.ResourceUtil;
import com.echo.p2p_project.server.ServerMain;
import javafx.application.Application;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/23-17:20
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.gui
 * @Description:
 **/
public class ServerIndexApplication extends Application {
    private static Thread server_thread;
    private static Thread log_thread;
    private static Boolean is_started = false;
//    public static ObservableMap O_UHPT;
//    public static ObservableMap O_UHRT;

    public static void main(String[] args) {
        launch();
    }


    public static void init_server() {
        if (is_started == false) {
            is_started = true;
            server_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerMain.main(new String[]{});
                }
            });
            server_thread.start();
        } else {
            System.out.println("CAN NOT START");
        }
    }

    public static void stopServer() {
        if (is_started) {
            ServerMain.exit();
            server_thread.interrupt();
            is_started = false;
            System.exit(0);
        }
    }

    public static void start_log(TextArea logField, TableView uhpt_table, TableView uhrt_table, TreeView uhrt_tree) {
//        log_thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
//        log_thread.start();

    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ResourceUtil.getResource("gui/server_index.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }
}

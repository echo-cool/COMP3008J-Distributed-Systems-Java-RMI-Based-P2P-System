package com.echo.p2p_project.client.gui;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Console;
import com.echo.p2p_project.client.ClientMain;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/23-17:11
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.client.gui
 * @Description:
 **/
public class ClientIndexController {
    public TextField server_ip;
    public TextField server_port;
    public Button connect_button;
    public ChoiceBox download_datalist;
    public Button download_button;
    public ChoiceBox reg_datalist;
    public Button reg_button;
    public TableView<Resource> DHRT_Table;
    public TextArea log_field;
    public TableColumn<Resource, String> DHRT_GUID;
    public TableColumn<Resource, String> DHRT_NAME;
    public TableColumn<Resource, String> DHRT_HASH;
    public Button sync_button;
    public TextField lookup_filename;
    public Button lookup_button;

    public void initialize() {
        ObservableList<String> local_file_list = new ObservableListWrapper<>(Collections.synchronizedList(new ArrayList<>()));
        ObservableList<Resource> remote_file_list = new ObservableListWrapper<>(Collections.synchronizedList(new ArrayList<>()));
        ClientMain.DHRT.addListener(new MapChangeListener<String, Resource>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Resource> change) {
                log_field.appendText(change.toString());
                ObservableList<Resource> resources = new ObservableListWrapper<Resource>(new ArrayList<>(ClientMain.DHRT.values()));
                DHRT_Table.setItems(resources);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (remote_file_list) {
                            if (change.wasAdded()) {
                                remote_file_list.add(change.getValueAdded());
                            }
                            if (change.wasRemoved()) {
                                remote_file_list.remove(change.getValueRemoved());
                            }
                        }
                        if (remote_file_list.size() >= 1) {
                            download_datalist.setValue(remote_file_list.get(0));
                        }
                    }
                });

            }
        });
        download_datalist.setItems(remote_file_list);

        try {
            File[] files = FileUtil.ls(System.getProperty("user.dir") + "/res/");
            local_file_list.clear();
            for (File f : files) {
                local_file_list.add(f.getName());
            }
        } catch (Exception e) {
            System.out.println("Download DIR: " + System.getProperty("user.dir") + "/download/");
            System.out.println("Resource DIR: " + System.getProperty("user.dir") + "/res/");
            FileUtil.mkdir(System.getProperty("user.dir") + "/download/");
            FileUtil.mkdir(System.getProperty("user.dir") + "/res/");
        }

        //这里只监听文件或目录的修改事件
        //        修改：res-> 0.a
        //        修改：res-> 0.a
        //        修改：res-> 0.a
        //        创建：res-> 123 copy.db
        //        创建：res-> ddd.db
        //        删除：res-> 123 copy.db
        //        删除：res-> ddd.db
        //        修改：res-> .DS_Store
        WatchMonitor watchMonitor = WatchMonitor.create(System.getProperty("user.dir") + "/res/", WatchMonitor.EVENTS_ALL);
        watchMonitor.setWatcher(new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Create：{}-> {}", currentPath, obj);
                File[] files = FileUtil.ls(System.getProperty("user.dir") + "/res/");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        local_file_list.clear();
                        for (File f : files) {
                            local_file_list.add(f.getName());
                        }
                        if (local_file_list.size() >= 1)
                            download_datalist.setValue(local_file_list.get(0));
                    }
                });
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Modify：{}-> {}", currentPath, obj);
                File[] files = FileUtil.ls(System.getProperty("user.dir") + "/res/");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        local_file_list.clear();
                        for (File f : files) {
                            local_file_list.add(f.getName());
                        }
                        if (local_file_list.size() >= 1)
                            download_datalist.setValue(local_file_list.get(0));
                    }
                });

            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Delete：{}-> {}", currentPath, obj);
                File[] files = FileUtil.ls(System.getProperty("user.dir") + "/res/");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        local_file_list.clear();
                        for (File f : files) {
                            local_file_list.add(f.getName());
                        }
                        if (local_file_list.size() >= 1)
                            download_datalist.setValue(local_file_list.get(0));
                    }
                });
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Overflow：{}-> {}", currentPath, obj);
            }
        });
        //启动监听
        watchMonitor.start();
        reg_datalist.setItems(local_file_list);
        if (local_file_list.size() >= 1)
            reg_datalist.setValue(local_file_list.get(0));


        connect_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ClientMain.MainServerIP = server_ip.getText();
                ClientMain.RMI_PORT = Integer.valueOf(server_port.getText()).intValue();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClientMain.recover();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        ClientMain.sync_DHRT();
                    }
                }).start();

                download_button.setDisable(false);
                reg_button.setDisable(false);
                sync_button.setDisable(false);
                lookup_button.setDisable(false);
            }
        });

        DHRT_GUID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Resource, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Resource, String> param) {
                return new SimpleStringProperty(param.getValue().getGUID().toString());
            }
        });

        DHRT_NAME.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Resource, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Resource, String> param) {
                return new SimpleStringProperty(param.getValue().getName().toString());
            }
        });

        DHRT_HASH.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Resource, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Resource, String> param) {
                return new SimpleStringProperty(param.getValue().getHash().toString());
            }
        });


        sync_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                ClientMain.sync_DHRT();
            }
        });
        reg_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String filename = String.valueOf(reg_datalist.getValue());
                ClientMain.reg_file(filename);
            }
        });

        download_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Resource r = null;
                try {
                    r = (Resource) download_datalist.getValue();
                } catch (Exception e) {
                    return;
                }
                if (r != null) {
                    System.out.println(r);
                    ArrayList<Peer> processed_peers = new ArrayList<>();
                    for (Peer p : r.possessedBy.values()) {
                        processed_peers.add(p);
                    }
                    processed_peers.sort(new Comparator<Peer>() {
                        @Override
                        public int compare(Peer o1, Peer o2) {
                            return o1.getRoutingMetric() - o2.getRoutingMetric();
                        }
                    });
                    for (Peer p : processed_peers) {
                        System.out.println(p.getGUID().toString() + "  <>  " + p.getRoutingMetric());
                    }
                    ClientMain.P2P_download(processed_peers.get(0), r, new Runnable() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Information Dialog");
                                    alert.setHeaderText("File downloaded");
                                    alert.setContentText("Please check in your download folder!");

                                    alert.showAndWait();
                                }
                            });
                        }
                    });

                }
            }
        });
        lookup_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String filename = lookup_filename.getText();
                ClientMain.look_up_file(filename);
            }
        });


    }
}

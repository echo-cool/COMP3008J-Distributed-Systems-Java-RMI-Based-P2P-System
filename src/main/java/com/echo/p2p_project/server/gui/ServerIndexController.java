package com.echo.p2p_project.server.gui;

import com.echo.p2p_project.server.ServerMain;
import com.echo.p2p_project.u_model.Peer;
import com.echo.p2p_project.u_model.Resource;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * @Author: WangYuyang
 * @Date: 2021/10/23-17:21
 * @Project: P2P_Project
 * @Package: com.echo.p2p_project.server.gui
 * @Description:
 **/
public class ServerIndexController {
    public Button StartServerButton;
    public TextArea LogField;
    public Button StopServerButton;
    public TableView<Peer> uhpt_table;
    public TableView<Resource> uhrt_table;
    public TreeView uhrt_tree;
    public TableColumn<Peer, String> UHPT_GUID;
    public TableColumn<Peer, String> UHPT_IP;
    public TableColumn<Peer, String> UHPT_PORT;
    public TableColumn<Resource, String> UHRT_GUID;
    public TableColumn<Resource, String> UHRT_NAME;


    public void initialize() {
        UHPT_GUID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Peer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Peer, String> param) {
                return new SimpleStringProperty(param.getValue().getGUID().toString());
            }
        });
        UHPT_IP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Peer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Peer, String> param) {
                return new SimpleStringProperty(param.getValue().getIP().toString());
            }
        });
        UHPT_PORT.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Peer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Peer, String> param) {
                return new SimpleStringProperty(param.getValue().getP2P_port().toString());
            }
        });
        UHRT_GUID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Resource, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Resource, String> param) {
                return new SimpleStringProperty(param.getValue().getGUID().toString());
            }
        });
        UHRT_NAME.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Resource, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Resource, String> param) {
                return new SimpleStringProperty(param.getValue().getName().toString());
            }
        });

        ServerMain.UHPT.addListener(new MapChangeListener() {
            @Override
            public void onChanged(Change change) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {


                        //update log
                        LogField.appendText(change.toString() + "\n\n");

                        //update table
                        ObservableList<Peer> peers = new ObservableListWrapper<Peer>(new ArrayList<>(ServerMain.UHPT.values()));
                        uhpt_table.setItems(peers);


                        //update tree
                        TreeItem<String> rootItem = new TreeItem<String>("Server");
                        rootItem.setExpanded(true);
                        for (Peer p : peers) {
                            TreeItem<String> item = new TreeItem<String>(p.getGUID().toString());
                            for (Resource r : p.possessing.values()) {
                                item.setExpanded(true);
                                TreeItem<String> filename = new TreeItem<String>(r.getName().toString());
                                item.getChildren().add(filename);
                            }
                            rootItem.getChildren().add(item);

                        }
                        uhrt_tree.setRoot(rootItem);
                    }
                });


            }
        });
        ServerMain.UHRT.addListener(new MapChangeListener() {
            @Override
            public void onChanged(Change change) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //update log
                        LogField.appendText(change.toString() + "\n\n");

                        //update table
                        ObservableList<Resource> resources = new ObservableListWrapper<Resource>(new ArrayList<>(ServerMain.UHRT.values()));
                        uhrt_table.setItems(resources);

                        //update tree
                        ObservableList<Peer> peers = new ObservableListWrapper<Peer>(new ArrayList<>(ServerMain.UHPT.values()));
                        TreeItem<String> rootItem = new TreeItem<String>("Server");
                        rootItem.setExpanded(true);
                        for (Peer p : peers) {
                            TreeItem<String> item = new TreeItem<String>(p.getGUID().toString());
                            for (Resource r : p.possessing.values()) {
                                item.setExpanded(true);
                                TreeItem<String> filename = new TreeItem<String>(r.getName().toString());
                                item.getChildren().add(filename);
                            }
                            rootItem.getChildren().add(item);

                        }
                        uhrt_tree.setRoot(rootItem);

                    }
                });
            }
        });


    }

    public void StartServerButtonPressed(ActionEvent actionEvent) {
        ServerIndexApplication.init_server();

    }

    public void StopServerButtonPressed(ActionEvent actionEvent) {
        ServerIndexApplication.stopServer();
    }
}

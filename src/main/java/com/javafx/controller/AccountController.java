package com.javafx.controller;

import com.javafx.dto.RoleDto;
import com.javafx.entity.Account;
import com.javafx.entity.BookCategory;
import com.javafx.entity.Location;
import com.javafx.entity.Role;
import com.javafx.repository.AccountRepository;
import com.javafx.repository.RoleRepository;
import com.javafx.repository.impl.AccountRepositoryImpl;
import com.javafx.repository.impl.RoleRepositoryImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountController {
    AccountRepository accountRepository = new AccountRepositoryImpl();
    RoleRepository roleRepository = new RoleRepositoryImpl();

    public AccountController() {
        String hashed = BCrypt.hashpw("123456", BCrypt.gensalt());
//        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
//        accountRepository.save(new Account("test@gmai.com", "test", hashed, 1, 1));
    }


    public void processAccount(TreeView<Object> treeViews) {
        List<Account> accounts = accountRepository.findAllActive();
        BookCategory catRoot = new BookCategory("ROOT", "Root");
        // Phần tử gốc
        TreeItem rootItems = new TreeItem();
        rootItems = new TreeItem<>(catRoot);
        rootItems.setExpanded(true);

        TreeItem itemId = new TreeItem();
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("size: " + accounts.size());
            itemId.setValue(accounts.get(i).getId());
            TreeItem toStringAccount = new TreeItem(accounts.get(i).toString());

            itemId.getChildren().addAll(toStringAccount);

            System.out.println(itemId);
            rootItems.getChildren().addAll(itemId);
            itemId = new TreeItem();
        }


        treeViews.setRoot(rootItems);

        // Ẩn phần tử gốc.
        treeViews.setShowRoot(false);

    }

    public void processAccount(TableView<Object> tableViews, HBox hBoxSaveAndUpdate) {


        tableViews.getColumns().clear();
        tableViews.getItems().clear();


        List<Account> accounts = accountRepository.findAllActive();
//        tableViews.setFixedCellSize(25);
//        tableViews.prefHeightProperty().bind(Bindings.size(tableViews.getItems()).multiply(tableViews.getFixedCellSize()).add(30));

//        for (Account role : accounts) {
//            System.out.println(role.toString());
//        }
        final ObservableList<Object> data = FXCollections.observableArrayList(accounts);

        TableColumn numberCol = new TableColumn("#");
        numberCol.setMinWidth(20);
        numberCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Account>, ObservableValue<Account>>() {
            @Override
            public ObservableValue<Account> call(TableColumn.CellDataFeatures<Account, Account> p) {
                return new ReadOnlyObjectWrapper(p.getValue());
            }
        });

        numberCol.setCellFactory(new Callback<TableColumn<Account, Account>, TableCell<Account, Account>>() {
            @Override
            public TableCell<Account, Account> call(TableColumn<Account, Account> param) {
                return new TableCell<Account, Account>() {
                    @Override
                    protected void updateItem(Account item, boolean empty) {
                        super.updateItem(item, empty);

                        if (this.getTableRow() != null && item != null) {
                            setText(this.getTableRow().getIndex() + 1 + "");
                        } else {
                            setText("");
                        }
                    }
                };
            }
        });
        numberCol.setSortable(false);


        TableColumn idCol = new TableColumn("Id");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(
                new PropertyValueFactory<Account, Integer>("id"));

        TableColumn usernameCol = new TableColumn("Username");
        usernameCol.setMinWidth(100);
        usernameCol.setCellValueFactory(
                new PropertyValueFactory<Account, String>("username"));

        TableColumn emailCol = new TableColumn("Email");
        emailCol.setMinWidth(100);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<Account, String>("email"));

        TableColumn roleCol = new TableColumn("Role");
        roleCol.setMinWidth(100);
        roleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Account, String> c) {
                return new SimpleStringProperty(c.getValue().getRole().getName());
            }
        });


        tableViews.getColumns().addAll(numberCol, idCol, usernameCol, emailCol, roleCol);

        TableColumn actionCol = new TableColumn<Role, Void>("Action");
        actionCol.setMinWidth(130);
//        actionCol.setCellValueFactory(new PropertyValueFactory<Role, Role>("id"));
        actionCol.setCellFactory(param -> new TableCell<Account, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button blockButton = new Button("Block");

            private final HBox pane = new HBox(editButton, deleteButton, blockButton);

            {
                editButton.setStyle("-fx-background-color: #74d4c0; -fx-text-fill: white; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #d9455f; -fx-text-fill: white; -fx-cursor: hand;");
                blockButton.setStyle("-fx-background-color: #9a1f40; -fx-text-fill: white; -fx-cursor: hand;");

                editButton.setMaxWidth(Double.MAX_VALUE);
                deleteButton.setMaxWidth(Double.MAX_VALUE);
                blockButton.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(editButton, Priority.ALWAYS);
                HBox.setHgrow(deleteButton, Priority.ALWAYS);
                HBox.setHgrow(blockButton, Priority.ALWAYS);
                pane.setSpacing(10);

                deleteButton.setOnAction(event -> {
                    Account getPatient = getTableView().getItems().get(getIndex());
                    System.out.println(getPatient.getId() + "   " + getPatient.getEmail());
                    getPatient.setDelete(true);
                    accountRepository.save(getPatient);
                    hBoxSaveAndUpdate.getChildren().clear();
                    processAccount(tableViews, hBoxSaveAndUpdate);
                });

                editButton.setOnAction(event -> {
                    Account getPatient = getTableView().getItems().get(getIndex());
                    System.out.println(getPatient.getId() + "   " + getPatient.getEmail());
                    Account account = accountRepository.findById(getPatient.getId());


                    String hashed = BCrypt.hashpw("123456", BCrypt.gensalt());
                    account.setPassword(hashed);
                    accountRepository.save(account);
                    if (BCrypt.checkpw("123456", account.getPassword())) {
                        System.out.println("It matches");

                    } else
                        System.out.println("It does not match");


                });

                blockButton.setOnAction(event -> {
                    Account getPatient = getTableView().getItems().get(getIndex());
                    System.out.println(getPatient.getId() + "   " + getPatient.getEmail());
                    getPatient.setBlock(true);
                    accountRepository.save(getPatient);
                    hBoxSaveAndUpdate.getChildren().clear();
                    processAccount(tableViews, hBoxSaveAndUpdate);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : pane);
            }
        });


        tableViews.getColumns().add(actionCol);
        tableViews.setItems(data);


        tableViews.setRowFactory(tv -> {
            TableRow row = new TableRow<Account>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2) {

                    Account clickedRow = (Account) row.getItem();
                    printRow(clickedRow);
                }
            });
            return row;
        });


        //        FORM ADD VS UPDATE
        final TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxWidth(usernameCol.getPrefWidth());

        final TextField email = new TextField();
        email.setMaxWidth(emailCol.getPrefWidth());
        email.setPromptText("Email");


        final PasswordField password = new PasswordField();
        password.setMaxWidth(emailCol.getPrefWidth());
        password.setPromptText("Password");


        ModelMapper modelMapper = new ModelMapper();
        List<Role> roles = roleRepository.findAllActive();
        List<RoleDto> roleDtos = roles.stream().map(role -> modelMapper.map(role, RoleDto.class)).collect(Collectors.toList());

        ObservableList<RoleDto> dataRoles
                = FXCollections.observableArrayList(roleDtos);
        final ChoiceBox<RoleDto> choiceBoxRole = new ChoiceBox<>(dataRoles);
        final int[] roleId = new int[1];

        ChangeListener<RoleDto> changeListener = new ChangeListener<RoleDto>() {

            @Override
            public void changed(ObservableValue<? extends RoleDto> observable, RoleDto oldValue, RoleDto newValue) {
                if (newValue != null) {
                    System.out.println(newValue.getId());
                    roleId[0] = newValue.getId();
                }
            }
        };

        // Sự kiện khi thay đổi Item trên ChoiceBox
        choiceBoxRole.getSelectionModel().selectedItemProperty().addListener(changeListener);


        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String hashed = BCrypt.hashpw(password.getText(), BCrypt.gensalt());
                System.out.println("role id add user: " + roleId[0]);
                if (roleId[0] == 0 || roleId == null) {
                    roleId[0] = 4; // ROLE_USER
                }

                accountRepository.save(new Account(
                        username.getText(),
                        email.getText(),
                        hashed
                        , roleId[0]));
//                data.add(new Location(
//                        nameLocation.getText(),
//                        address.getText(),
//                        Integer.parseInt(capacity.getText())));


                username.clear();
                email.clear();
                password.clear();
                hBoxSaveAndUpdate.getChildren().clear();
                processAccount(tableViews, hBoxSaveAndUpdate);
            }
        });


        hBoxSaveAndUpdate.getChildren().addAll(username, email, password, choiceBoxRole, addButton);
        hBoxSaveAndUpdate.setSpacing(30);


    }

    private void printRow(Account item) {

        System.out.println(item.toString());


    }
}

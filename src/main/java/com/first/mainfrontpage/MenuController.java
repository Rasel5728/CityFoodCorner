package com.first.mainfrontpage;

import com.first.mainfrontpage.FloatPick.Builder;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Button    searchBtn;
    @FXML private TextField search;
    @FXML private TextField ammountField;
    @FXML private Label     changes;
    @FXML private Label     totalField;
    @FXML private TableView tablevVew;
    @FXML private TableColumn<String[], String> tableColumn;
    @FXML private TableColumn<String[], String> quantity;
    @FXML private TableColumn<String[], String> price;
    @FXML private ScrollPane scrollbar;
    @FXML private GridPane   menuGrid;

    @FXML private ListView<String> searchList = new ListView<>();

    private int    currentFoodCount = -1;
    private double currentPrice     = 0;
    private int    stock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchList.setVisible(false);
        searchList.toFront();

        new Builder(search, (AnchorPane) search.getParent())
                .suggestions(this::liveSearch)
                .onSelect((val, f) -> {
                    f.setText(val);
                    f.positionCaret(val.length());
                })
                .showOnFocus(false)
                .debounceMs(220)
                .maxRows(6)
                .build()
                .attach();

        removeScrollBar(scrollbar);
        loadAllProducts();
    }

    public void addFoodToMenu(String name, String price, byte[] image, int count, int stock) throws IOException {
        int col = count % 2;
        int row = count / 2;
        menuGrid.add(loadList(name, price, image, stock), col, row);
    }

    AnchorPane loadList(String foodName, String foodPrice, byte[] imageBytes, int stock) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("foodCard.fxml"));
        AnchorPane card = loader.load();
        FoodCardController controller = loader.getController();
        controller.setFoodValue(foodName, foodPrice, getImg(imageBytes), stock);
        controller.setMenuController(this);
        return card;
    }

    void removeScrollBar(ScrollPane scrollPane) {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    void indexMaping() {
        tableColumn.setCellValueFactory(nameData ->
                new ReadOnlyStringWrapper(nameData.getValue()[0]));
        quantity.setCellValueFactory(q ->
                new ReadOnlyStringWrapper(q.getValue()[1]));
        price.setCellValueFactory(prc ->
                new ReadOnlyStringWrapper(prc.getValue()[2]));
    }

    public void setToTable(String name, String quantity, String price) {
        indexMaping();
        tablevVew.getItems().add(new String[]{name, quantity, price});
        currentPrice += Double.parseDouble(price);
        totalField.setText(String.format("%.2f", currentPrice));
    }

    @FXML
    private void remove() {
        String[] selected = (String[]) tablevVew.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        currentPrice -= Double.parseDouble(selected[2]);
        if (currentPrice < 0) currentPrice = 0;
        tablevVew.getItems().remove(selected);
        totalField.setText(String.format("%.2f", currentPrice));
        changes.setText("0.0");
        ammountField.clear();
    }

    @FXML
    private void pay() {
        if (ammountField.getText().isEmpty()) return;
        double amount = Double.parseDouble(ammountField.getText());
        if (amount < currentPrice) {
            changes.setText("Insufficient");
            return;
        }
        updateStock();
        changes.setText(String.format("%.2f", amount - currentPrice));
    }

    private void loadAllProducts() {
        currentFoodCount = -1;
        menuGrid.getChildren().clear();
        String sql = "SELECT * FROM products";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                currentFoodCount++;
                addFoodToMenu(
                        rs.getString("product_name"),
                        String.valueOf(rs.getDouble("price")),
                        rs.getBytes("image"),
                        currentFoodCount,
                        rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAllProducts(String nm) {
        menuGrid.getChildren().clear();
        currentFoodCount = -1;
        String sql = "SELECT * FROM products WHERE product_name LIKE ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + nm + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                currentFoodCount++;
                addFoodToMenu(
                        rs.getString("product_name"),
                        String.valueOf(rs.getDouble("price")),
                        rs.getBytes("image"),
                        currentFoodCount,
                        rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getProductStock(String name) {
        String sql = "SELECT stock FROM products WHERE product_name = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) stock = rs.getInt("stock");
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public void updateStock(String name, int n) {
        String sql = "UPDATE products SET stock = ? WHERE product_name = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, n);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    public int getStck(String s) {
        getProductStock(s);
        return stock;
    }

    Image getImg(byte[] imageBytes) {
        if (imageBytes != null) {
            return new Image(new ByteArrayInputStream(imageBytes));
        }
        return null;
    }

    private void updateStock() {
        for (Object item : tablevVew.getItems()) {
            String[] row     = (String[]) item;
            String name      = row[0];
            int qty          = Integer.parseInt(row[1]);
            int currentStock = getStck(name);
            updateStock(name, Math.max(currentStock - qty, 0));
        }
    }

    public void insertSellHistory(String productName, int quantity, double total, String date, String cashier) {
        String sql = "INSERT INTO sellHistory (product_name, quantity, total, date, cashier) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, productName);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, total);
            pstmt.setString(4, date);
            pstmt.setString(5, cashier);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Insert failed: " + e.getMessage());
        }
    }

    public void processAndSave() {
        String date = LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        for (Object item : tablevVew.getItems()) {
            String[] row       = (String[]) item;
            String productName = row[0];
            int qty            = Integer.parseInt(row[1]);
            double p           = Double.parseDouble(row[2]);
            insertSellHistory(productName, qty, p, date, currentUser.userName);
        }
    }

    @FXML
    private void receipt() {
        if (tablevVew.getItems().isEmpty()) return;
        processAndSave();
        tablevVew.getItems().clear();
        currentPrice = 0;
        totalField.setText("0.00");
        changes.setText("0.0");
        ammountField.clear();
    }

    List<String> liveSearch(String query) {
        List<String> prodList = new ArrayList<>();
        String sql = "SELECT product_name FROM products WHERE product_name LIKE ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) prodList.add(rs.getString("product_name"));
            return prodList;
        } catch (Exception e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        return List.of();
    }

    @FXML
    private void searchIt() {
        if (!Objects.equals(search.getText(), "")) {
            loadAllProducts(search.getText());
            search.setText("");
            searchBtn.setText("Refresh");
        } else {
            searchBtn.setText("Search");
            loadAllProducts();
        }
    }
}
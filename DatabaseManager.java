import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/mini_rpg?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";       // default user XAMPP
    private static final String PASSWORD = "";       // kosong (default XAMPP)

static {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("✅ Driver MySQL berhasil dimuat!");
    } catch (ClassNotFoundException e) {
        System.out.println("❌ Driver MySQL tidak ditemukan! Pastikan .jar sudah di classpath.");
    }
}


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void savePlayer(Player p) {
        String sql = "INSERT INTO player(nama, level, exp, hp, attack, defense) VALUES(?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.name);
            stmt.setInt(2, p.level);
            stmt.setInt(3, p.exp);
            stmt.setInt(4, p.hp);
            stmt.setInt(5, p.attack);
            stmt.setInt(6, p.defense);
            stmt.executeUpdate();

            System.out.println("✅ Data pemain disimpan ke database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Player loadLastPlayer() {
        String sql = "SELECT * FROM player ORDER BY id DESC LIMIT 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Player p = new Player(rs.getString("nama"));
                p.level = rs.getInt("level");
                p.exp = rs.getInt("exp");
                p.hp = rs.getInt("hp");
                p.attack = rs.getInt("attack");
                p.defense = rs.getInt("defense");
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

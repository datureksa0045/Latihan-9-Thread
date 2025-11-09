# Mini RPG Thread - README

Proyek sederhana "Mini RPG Thread" memperagakan:
- Pertarungan sederhana antara Player dan Enemy yang dijalankan di Thread terpisah.
- Penyimpanan dan pemuatan data pemain ke/dari database MySQL menggunakan JDBC.

Ringkasan file:
- `MainGame.java` — antarmuka konsol untuk memulai game baru atau melanjutkan dari database.
- `Player.java` — model pemain (nama, hp, attack, defense, level, exp).
- `Enemy.java` — model musuh dan generator musuh acak.
- `BattleThread.java` — logika pertarungan berjalan di Thread terpisah.
- `DatabaseManager.java` — koneksi JDBC, menyimpan dan memuat data pemain.

Persyaratan:
- Java JDK (11+ disarankan)
- MySQL atau MariaDB berjalan di localhost
- MySQL Connector/J (tambahkan ke classpath)
- Database & tabel contoh:

SQL untuk membuat database/tabel:
```sql
-- Buat database (jika belum ada)
CREATE DATABASE IF NOT EXISTS mini_rpg;

USE mini_rpg;

CREATE TABLE IF NOT EXISTS player (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama VARCHAR(100),
  level INT,
  exp INT,
  hp INT,
  attack INT,
  defense INT
);
```

Cara menjalankan:
1. Pastikan MySQL berjalan dan DatabaseManager.URL sesuai konfigurasi.
2. Letakkan MySQL Connector/J (jar) di classpath saat kompilasi/eksekusi.
3. Kompilasi:
   javac *.java
4. Jalankan:
   java -cp .;path\to\mysql-connector-java.jar MainGame
5. Pilih "Mulai Game Baru" atau "Lanjutkan dari Database" di menu.

Cuplikan kode (intisari):

- Loop pertarungan di BattleThread (inti logika):
```java
// Serangan player
enemy.hp -= Math.max(0, player.attack - 2);
System.out.println(player.name + " menyerang " + enemy.name + " (HP musuh: " + enemy.hp + ")");

if (enemy.hp <= 0) {
    System.out.println("\n🎉 " + player.name + " menang!");
    player.exp += 50;
    if (player.exp >= 100) {
        player.exp = 0;
        player.levelUp();
    }
    DatabaseManager.savePlayer(player);
    battleOver = true;
    break;
}

// Serangan musuh
player.hp -= Math.max(0, enemy.attack - player.defense);
System.out.println(enemy.name + " menyerang balik! (HP pemain: " + player.hp + ")");

if (player.hp <= 0) {
    System.out.println("\n💀 " + player.name + " kalah!");
    DatabaseManager.savePlayer(player);
    battleOver = true;
}
```

- Menyimpan/memuat pemain di DatabaseManager (konsep):
```java
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
```

- Alur utama di MainGame:
```java
public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    System.out.println("=== MINI RPG THREAD GAME ===");
    System.out.println("1. Mulai Game Baru");
    System.out.println("2. Lanjutkan dari Database");
    System.out.print("Pilih: ");
    int pilih = input.nextInt();
    input.nextLine(); // buang newline

    Player player;

    if (pilih == 1) {
        System.out.print("Masukkan nama pemain: ");
        String nama = input.nextLine();
        player = new Player(nama);
        DatabaseManager.savePlayer(player);
    } else {
        player = DatabaseManager.loadLastPlayer();
        if (player == null) {
            System.out.println("Tidak ada data tersimpan. Membuat karakter baru...");
            System.out.print("Masukkan nama pemain: ");
            String nama = input.nextLine();
            player = new Player(nama);
            DatabaseManager.savePlayer(player);
        } else {
            System.out.println("✅ Data pemain berhasil dimuat: " + player.name + " (Lv " + player.level + ")");
        }
    }

    Enemy musuh = Enemy.randomEnemy();
    System.out.println("\nMusuh muncul: " + musuh.name + " (HP " + musuh.hp + ")");

    BattleThread battle = new BattleThread(player, musuh);
    battle.start();
}
```

Kegunaan setiap bagian:
- Thread (BattleThread): memungkinkan loop pertarungan berjalan terpisah dari thread utama sehingga bisa dikembangkan untuk UI/non-blocking flow.
- JDBC (DatabaseManager): menyimpan snapshot pemain ke DB untuk persistensi antar sesi.

Kesimpulan singkat — Thread dan Database di Java:
- Thread:
  - Berguna untuk melakukan pekerjaan yang berulang atau panjang tanpa memblokir alur utama.
  - Perlu hati-hati terhadap kondisi balapan dan sinkronisasi bila ada akses bersama ke data.
- Database (JDBC):
  - JDBC menyediakan API standar untuk koneksi, query dan update database.
  - Pastikan pengelolaan koneksi yang baik (tutup Connection/Statement/ResultSet) dan tangani SQLException.
  - Untuk aplikasi nyata, pertimbangkan connection pool dan desain schema yang sesuai.

Catatan:
- Contoh ini dibuat untuk tujuan pembelajaran; untuk produksi, perbaiki validasi input, handling exception, dan mekanisme update (UPDATE) alih-alih selalu INSERT saat menyimpan status pemain.
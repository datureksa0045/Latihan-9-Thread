import java.util.Scanner;

public class MainGame {
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
                System.out.println("Data pemain berhasil dimuat: " + player.name + " (Lv " + player.level + ")");
            }
        }

        Enemy musuh = Enemy.randomEnemy();
        System.out.println("\nMusuh muncul: " + musuh.name + " (HP " + musuh.hp + ")");

        BattleThread battle = new BattleThread(player, musuh);
        battle.start();
    }
}

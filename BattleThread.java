public class BattleThread extends Thread {
    private Player player;
    private Enemy enemy;
    private boolean battleOver = false;

    public BattleThread(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void run() { 
        System.out.println("\n⚔️  Pertarungan dimulai antara " + player.name + " dan " + enemy.name + "!\n");

        while (!battleOver) {
            try {
                Thread.sleep(2000); // 2 detik per giliran
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
        }
    }
}

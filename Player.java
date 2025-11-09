public class Player {
    String name;
    int hp, attack, defense, exp, level;

    public Player(String name) {
        this.name = name;
        this.hp = 100;
        this.attack = 20;
        this.defense = 5;
        this.level = 1;
        this.exp = 0;
    }

    public void levelUp() {
        level++;
        hp += 20;
        attack += 5;
        defense += 3;
        System.out.println("🎉 " + name + " naik level ke " + level + "!");
    }
}

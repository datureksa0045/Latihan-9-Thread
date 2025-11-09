import java.util.Random;

public class Enemy {
    String name;
    int hp, attack;

    public Enemy(String name, int hp, int attack) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
    }

    public static Enemy randomEnemy() {
        String[] names = {"Goblin", "Slime", "Skeleton", "Orc"};
        Random rand = new Random();
        String name = names[rand.nextInt(names.length)];
        int hp = 50 + rand.nextInt(50);
        int attack = 10 + rand.nextInt(10);
        return new Enemy(name, hp, attack);
    }
}

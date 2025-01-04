package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        int round = 0; // Каунт раунда

        // Создаем списки юнитов для обеих армий, копируя их из объектов Army
        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        // Пока в обеих армиях есть хотя бы один живой юнит крутим while
        while (playerUnits.stream().anyMatch(Unit::isAlive) && computerUnits.stream().anyMatch(Unit::isAlive)) {
            round++; // Щелкаем каунтер раунда
            System.out.println("Раунд " + round + " начинается!");

            // Создаем очередь ходов для юнитов игрока
            // Берем только живых юнитов, сортируем их по убыванию атаки, затем добавляем в очередь
            Queue<Unit> playerQueue = new LinkedList<>(
                    playerUnits.stream().filter(Unit::isAlive).sorted(Comparator.comparingInt(Unit::getBaseAttack).reversed()).toList()
            );

            // Делаем такую же очередь для компьютерса
            Queue<Unit> computerQueue = new LinkedList<>(
                    computerUnits.stream().filter(Unit::isAlive).sorted(Comparator.comparingInt(Unit::getBaseAttack).reversed()).toList()
            );

            // Пока есть юниты в очередях крутим while
            while (!playerQueue.isEmpty() || !computerQueue.isEmpty()) {

                // Гоняем по очередям, если они не пустые

                // Ход юнита игрока
                if (!playerQueue.isEmpty()) {
                    Unit playerUnit = playerQueue.poll();  // Берем юнита из очереди
                    Unit target = playerUnit.getProgram().attack(); // Юнит выбирает цель для атаки


                    printBattleLog.printBattleLog(playerUnit, target); // Логируем атаку. Кто и кого атакует

                    // Если цель мертва после атаки, удаляем её из очереди компьютера
                    if (target != null && !target.isAlive()) {
                        computerQueue.remove(target);
                    }
                }

                // Ход юнита компьютера
                if (!computerQueue.isEmpty()) {
                    Unit computerUnit = computerQueue.poll(); // Берем юнита из очереди
                    Unit target = computerUnit.getProgram().attack(); // Юнит выбирает цель для атаки

                    printBattleLog.printBattleLog(computerUnit, target);// Логируем атаку. Кто и кого атакует

                    // Если цель мертва после атаки, удаляем её из очереди игрока
                    if (target != null && !target.isAlive()) {
                        playerQueue.remove(target);
                    }
                }
            }

            // После завершения всех ходов в раунде выводим саммари
            System.out.println("Раунд " + round + " закончен!");
            System.out.println("У игрока осталось " + playerUnits.stream().filter(Unit::isAlive).count() + " юнитов.");
            System.out.println("У компьютера осталось " + computerUnits.stream().filter(Unit::isAlive).count() + " юнитов.");
        }

        // Когда битва окончена, выводим итоговый результат проведенной битвы
        System.out.println("Битва окончена!");
        if (playerUnits.stream().anyMatch(Unit::isAlive)) {
            System.out.println("Выиграл игрок!");
        }
        else if (computerUnits.stream().anyMatch(Unit::isAlive)) {
            System.out.println("Выиграл комьютер!");
        }
        else {
            System.out.println("Битва окончилась ничьей! :)");
        }
    }
}
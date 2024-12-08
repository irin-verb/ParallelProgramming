import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Runner {

    public static void main(String[] args) {
            Scanner in = new Scanner(System.in);
            while (true) {
                String inputString = in.nextLine();
                String[] strings = inputString.split(" ");
                if (strings.length > 2)
                    System.out.println("Неправильный формат команды! Должен быть формат \"команда целое_число\" или \"команда\"");
                else {
                    CommandEnum command = CommandEnum.fromString(strings[0]);
                    int number = 0;
                    if (strings.length == 2) {
                        try {
                            number = Integer.valueOf(strings[1]);
                        }
                        catch (Exception e) {
                            System.out.println("Вторым аргументом команды должно быть целое положительное число");
                        }
                    }
                    switch (command) {
                        case NONE: {
                            System.out.println("Неизвестная команда!");
                        }
                        case EXIT: {
                            StreamsManager.killThemAll();
                            System.exit(0);
                            break;
                        }
                        case START: {
                            StreamsManager.start(number);
                            break;
                        }
                        case AWAIT: {
                            StreamsManager.await(number);
                            break;
                        }
                        case STOP: {
                            StreamsManager.stop(number);
                            break;
                        }
                    }
                }
            }
    }
}
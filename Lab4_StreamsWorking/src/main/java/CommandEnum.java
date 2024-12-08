public enum CommandEnum {
    START,
    STOP,
    AWAIT,
    EXIT,
    NONE;

    public static CommandEnum fromString(String command) {
        for (CommandEnum cmd : CommandEnum.values()) {
            if (cmd.name().equalsIgnoreCase(command)) { // Сравнение с игнорированием регистра
                return cmd;
            }
        }
        return CommandEnum.NONE;
    }
}

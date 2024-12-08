public class StringHandler {
    public static byte countCapitalLetters(String string) {
        byte count = 0;
        if (string != null) {
            for (char ch : string.toCharArray()) {
                if (Character.isUpperCase(ch)) {
                    count++;
                }
            }
        }
        return count;
    }
}

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.RepeatedTest;

public class TestStringHandler {

    @RepeatedTest(50)
    public void is4CapitalLetters() {
        assertEquals(StringHandler.countCapitalLetters("FFFF"),4);
    }

    @RepeatedTest(50)
    public void isNoCapitalLetters() {
        assertEquals(StringHandler.countCapitalLetters("ghfgdfgdvrr564g"),0);
    }

    @RepeatedTest(50)
    public void isEmptyString() {
        assertEquals(StringHandler.countCapitalLetters(""),0);
    }

    @RepeatedTest(50)
    public void isNullString() {
        assertEquals(StringHandler.countCapitalLetters(null),0);
    }
}

import org.example.Burse.Burse10;
import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Currency.CurrencyType;
import org.example.Burse.IBurse;
import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Notification.BurseNotificationType;
import org.example.Burse.Order.OrderType;
import org.example.BurseTester;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Burse10Test {

    private static IBurse burse;

    @BeforeEach
    public void openBurse() { burse = new Burse10(); }

    @AfterEach
    public void closeBurse() { burse.close(); }

    @Test
    public void testCloseBurse() throws InterruptedException {
        BurseCallback callback = new BurseCallback();
        BurseTester.processBurseClosing(burse, callback);

        BurseNotificationType expectedNote = BurseNotificationType.CANCELLED;
        BurseNotificationType actualNote = callback.getNote(1).getType();

        assertEquals(expectedNote, actualNote, "Ордер не был отменен");
    }


    @Test
    public void testAddOrder() throws InterruptedException {
        BurseCallback callback = new BurseCallback();
        BurseTester.processRandomOrder(burse, callback);

        BurseNotificationType expectedNote = BurseNotificationType.PLACED;
        BurseNotificationType actualNote = callback.getNote(0).getType();

        assertEquals(expectedNote, actualNote, "Ордер не был добавлен на биржу.");
    }

    @Test
    public void testFullOrderExecution() throws InterruptedException {
        BurseCallback callback1 = new BurseCallback();
        BurseCallback callback2 = new BurseCallback();
        CurrencyPair pair = new CurrencyPair(CurrencyType.EUR, CurrencyType.USD);

        BurseTester.processOrder(burse, callback1, pair, OrderType.SELL, 4, 100);
        BurseTester.processOrder(burse, callback2, pair, OrderType.BUY, 6, 100);

        BurseNotificationType expectedNote = BurseNotificationType.FULLY_COMPLETED;

        BurseNotificationType actualNote1 = callback1.getNote(1).getType();
        assertEquals(expectedNote, actualNote1, "Ордер не был полностью завершен");

        BurseNotificationType actualNote2 = callback2.getNote(1).getType();
        assertEquals(expectedNote, actualNote2, "Ордер не был полностью завершен");
    }

    @Test
    public void testPartialOrderExecution() throws InterruptedException {
        BurseCallback callback1 = new BurseCallback();
        BurseCallback callback2 = new BurseCallback();
        CurrencyPair pair = new CurrencyPair(CurrencyType.EUR, CurrencyType.USD);

        BurseTester.processOrder(burse, callback1, pair, OrderType.SELL, 4, 100);
        BurseTester.processOrder(burse, callback2, pair, OrderType.BUY, 6, 50);

        BurseNotificationType expectedNote1 = BurseNotificationType.PARTIAL_COMPLETED;
        BurseNotificationType expectedNote2 = BurseNotificationType.FULLY_COMPLETED;

        BurseNotificationType actualNote1 = callback1.getNote(1).getType();
        assertEquals(expectedNote1, actualNote1, "Ордер не был частично заверше");

        BurseNotificationType actualNote2 = callback2.getNote(1).getType();
        assertEquals(expectedNote2, actualNote2, "Ордер не был полностью завершен");
    }

    @Test
    public void testBalanceDuringOrderExecution() throws InterruptedException {
        final int CALLBACKS_COUNT = 10;
        final int ORDERS_FOR_CALLBACK = 5;

        BurseCallback[] callbacks = new BurseCallback[CALLBACKS_COUNT];

        for (int i = 0; i < CALLBACKS_COUNT; ++i) {
            callbacks[i] = new BurseCallback();
            for (int j = 0; j < ORDERS_FOR_CALLBACK; ++j)
                BurseTester.processRandomOrder(burse, callbacks[i]);
        }

        assertEquals(0, BurseTester.getGeneralCount(callbacks), "Количественный баланс не равен нулю");
        assertEquals(0, BurseTester.getGeneralMoney(callbacks), "Баланс денег не равен нулю");
    }

    @Test
    public void testStressOrderProcessing() throws InterruptedException {
        final int THREADS_NUMBER = 100;
        final int CALLBACKS_NUMBER = 10;
        final int ORDERS_PER_CALLBACK = 3;

        BurseCallback[] callbacks = new BurseCallback[CALLBACKS_NUMBER];
        for (int i = 0; i < CALLBACKS_NUMBER; ++i)
            callbacks[i] = new BurseCallback();

        BurseTester.processRandomOrdersByThreads(burse, callbacks, ORDERS_PER_CALLBACK, THREADS_NUMBER);

        assertEquals(0, BurseTester.getGeneralMoney(callbacks),"Баланс денег между клиентами не равен нулю");
        assertEquals(0, BurseTester.getGeneralCount(callbacks), "Количественный баланс между клиентами не равен нулю");
    }

}

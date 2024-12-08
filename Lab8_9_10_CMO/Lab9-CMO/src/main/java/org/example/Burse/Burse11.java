package org.example.Burse;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Notification.IBurseCallback;
import org.example.Burse.Order.Order;
import org.example.Burse.Order.OrderEvent;
import org.example.Burse.Order.OrderType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Burse11 implements IBurse {
    private final Disruptor<OrderEvent> disruptor;

    public Burse11() {
        disruptor = new Disruptor<>(
                OrderEvent::new,
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI,
                new com.lmax.disruptor.BusySpinWaitStrategy()
                );
        disruptor.handleEventsWith(new Burse11OrdersProcessor());
        disruptor.start();
    }

    @Override
    public void addOrder(IBurseCallback callback, CurrencyPair currencyPair, OrderType orderType, double priceLimit, int quantity) {
        disruptor.publishEvent((event, sequence) -> {
            event.setOrder(new Order(callback, orderType, currencyPair, priceLimit, quantity));
        });
    }

    @Override
    public void close() {
        disruptor.publishEvent((event, sequence) -> event.setCloseEvent(true));
        disruptor.shutdown();
    }

}

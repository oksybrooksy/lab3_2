package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    @Mock
    private Clock clock;
    private Order order;

    @BeforeEach
    void setUp() throws Exception {}

    @Test
    void expiredOrderShouldThrownExceptionTest() {
        order = new Order(clock);

        Instant submission = Instant.parse("2010-10-10T10:10:10.00Z");
        Instant confirmation = submission.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submission).thenReturn(confirmation);

        order.submit();
        assertThrows(OrderExpiredException.class, order::confirm);
    }

    @Test
    void expiredOrderShouldNotThrownExceptionTest(){
        order = new Order(clock);

        Instant submission = Instant.parse("2010-10-10T10:10:10.00Z");
        Instant confirmation = submission.plus(Order.VALID_PERIOD_HOURS, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submission).thenReturn(confirmation);

        order.submit();
        Assertions.assertDoesNotThrow(()->order.confirm());
    }

    @Test
    void orderStateShouldBeConfirmedTest() {
        order = new Order(clock);
        Instant start = Instant.parse("2020-03-20T00:00:00.00Z");
        Instant end = start.plus(12, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(start).thenReturn(end);

        order.submit();
        order.confirm();
        assertEquals(order.getOrderState(), Order.State.CONFIRMED);
    }

    @Test
    void orderStateShouldBeCancelledTest() {
        order = new Order(clock);
        Instant start = Instant.parse("2014-02-01T03:00:00.00Z");
        Instant end = start.plus(28, ChronoUnit.HOURS);

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(start).thenReturn(end);

        order.submit();

        assertThrows(OrderExpiredException.class,()->order.confirm());
        assertEquals(Order.State.CANCELLED,order.getOrderState());
    }
}







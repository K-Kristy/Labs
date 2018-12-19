package info.kgeorgiy.java.advanced.concurrent;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListIPTest<P extends ListIP> extends ScalarIPTest<P> {
    @Test
    public void test07_join() throws InterruptedException {
        test((data, ignore) -> data.stream().map(Object::toString).collect(Collectors.joining()), (i, t, d, v) -> i.join(t, d), unit);
    }

    @Test
    public void test08_filter() throws InterruptedException {
        test((data, predicate) -> data.stream().filter(predicate).collect(Collectors.toList()), ListIP::filter, predicates);
    }

    @Test
    public void test09_map() throws InterruptedException {
        test((data, f) -> data.stream().map(f).collect(Collectors.toList()), ListIP::map, functions);
    }

    private final List<Named<Function<Integer, ?>>> functions = Arrays.asList(
            new Named<>("* 2", v -> v * 2),
            new Named<>("is even", v -> v % 2 == 0),
            new Named<>("toString", Object::toString)
    );
    private final List<Named<Void>> unit = Arrays.asList(new Named<>("Common", null));
}

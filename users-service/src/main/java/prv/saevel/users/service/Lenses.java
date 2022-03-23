package prv.saevel.users.service;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class Lenses {

    private Lenses(){}

    public static <T> UnaryOperator<T> lens(Consumer<T> f){
        return t -> {
            f.accept(t);
            return t;
        };
    }
}

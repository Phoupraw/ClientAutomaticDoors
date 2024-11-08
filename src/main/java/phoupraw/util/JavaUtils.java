package phoupraw.util;

import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiFunction;

@ApiStatus.NonExtendable
public interface JavaUtils {
    /**
     @see BiFunction
     */
    static <T, U> T firstOfTwo(T first, U second) {
        return first;
    }
    /**
     @see BiFunction
     */
    static <T, U> U secondOfTwo(T first, U second) {
        return second;
    }
    @SuppressWarnings("unchecked")
    @SneakyThrows
    static <T> Class<T> findClassOrThrow(String className) {
        return (Class<T>) Class.forName(className);
    }
}

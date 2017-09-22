package edu.nju.memo.common;

/**
 * Created by tinker on 2017/9/22.
 */

public interface Function<T, R> {
    R apply(T value);
}

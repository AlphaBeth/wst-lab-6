package ru.ifmo.wst.lab;

import lombok.Value;

@Value
public class Pair<L, R> {
    private final L left;
    private final R right;

}

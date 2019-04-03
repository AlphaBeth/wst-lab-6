package ru.ifmo.wst.lab;

import lombok.Data;

@Data
public class Pair<L, R> {
    private L left;
    private R right;

}


package io.github.sullis.java.lang.instrument.example;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("premain invoked");
        ClassFileTransformerUtil.install(instrumentation);
    }
}

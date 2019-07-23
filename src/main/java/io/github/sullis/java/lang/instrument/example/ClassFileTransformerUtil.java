package io.github.sullis.java.lang.instrument.example;


import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class ClassFileTransformerUtil {
    static private AgentBuilder.Identified.Extendable createBuilder() {
        return new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform(new AgentBuilderTransformerImpl());
    }

    static public ClassFileTransformer createClassFileTransformer() {
        return createBuilder().makeRaw();
    }

    static public ResettableClassFileTransformer install(Instrumentation instrumentation) {
        return createBuilder().installOn(instrumentation);
    }
}

class AgentBuilderTransformerImpl implements AgentBuilder.Transformer {
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
        return builder.visit(Advice.to(MethodLogging.class).on(ElementMatchers.any()));
    }
}

class MethodLogging {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t|#m|#s") String origin,
                             @Advice.AllArguments Object[] allArgs)
            throws Exception {
        String[] originInfo = origin.split("\\|");
        String className = originInfo[0];
        String methodName = originInfo[1];
        String methodSignature = originInfo[2];
        System.out.println("OnMethodEnter: " + className + " " + methodName + " " + methodSignature);
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Origin("#t|#m|#s") String origin,
                            @Advice.AllArguments Object[] allArgs)
            throws Exception {
        String[] originInfo = origin.split("\\|");
        String className = originInfo[0];
        String methodName = originInfo[1];
        String methodSignature = originInfo[2];
        System.out.println("OnMethodExit: " + className + " " + methodName + " " + methodSignature);
    }
}

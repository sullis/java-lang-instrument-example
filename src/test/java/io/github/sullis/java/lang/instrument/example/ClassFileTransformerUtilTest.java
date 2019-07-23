package io.github.sullis.java.lang.instrument.example;

import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import org.junit.jupiter.api.Test;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClassFileTransformerUtilTest {
    @Test
    public void happyPath() throws Exception {
        ClassFileTransformer classFileTransformer = ClassFileTransformerUtil.createClassFileTransformer();
        Class clazz = HelloWorld.class;
        ClassLoader loader = clazz.getClassLoader();
        byte[] transformedClassData = classFileTransformer.transform(loader,
                clazz.getName(),
                clazz,
                clazz.getProtectionDomain(),
                ClassFileLocator.ForClassLoader.of(loader).locate(clazz.getName()).resolve());

        assertNotNull(transformedClassData);
        assertTrue(transformedClassData.length > 0);

        Map<String, byte[]> classMap = new HashMap<String, byte[]>();
        classMap.put(clazz.getName(), transformedClassData);
        ByteArrayClassLoader byteLoader = new ByteArrayClassLoader(null, classMap);
        Class<?> transformedClazz = byteLoader.loadClass(clazz.getName());
        assertEquals(clazz.getName(), transformedClazz.getName());
        assertNotEquals(clazz.hashCode(), transformedClazz.hashCode());
        Constructor<?> constructor = transformedClazz.getConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        Method m = transformedClazz.getMethod("hello", String.class);
        assertNotNull(m);
        m.setAccessible(true);
        Object invocationResult = m.invoke(instance, "Obama");
        assertEquals("Hello Obama", invocationResult);
    }
}

class HelloWorld {
    public HelloWorld() { /* public */ }
    public String hello(String name) {
        return "Hello " + name;
    }
}

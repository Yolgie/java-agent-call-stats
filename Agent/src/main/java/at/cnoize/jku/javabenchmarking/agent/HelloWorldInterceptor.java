package at.cnoize.jku.javabenchmarking.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class HelloWorldInterceptor implements ClassFileTransformer {
    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer
    ) {
        if (className.contains("cnoize")) {
            System.out.println(className + " loaded");
        }
        return classfileBuffer;
    }
}

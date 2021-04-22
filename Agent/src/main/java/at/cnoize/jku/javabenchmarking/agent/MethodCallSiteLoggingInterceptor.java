package at.cnoize.jku.javabenchmarking.agent;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MethodCallSiteLoggingInterceptor implements ClassFileTransformer {
    private final String methodToInstrument;

    public MethodCallSiteLoggingInterceptor(String methodToInstrument) {
        this.methodToInstrument = methodToInstrument;
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer
    ) {
        if (className.contains("cnoize")) {
            try {
                System.out.println(className + " loaded");
                final ClassPool classPool = ClassPool.getDefault();
                final CtClass newClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                System.out.println("New Class: " + newClass.getName() + " - " + newClass.isInterface());
                final CtBehavior[] declaredBehaviors = newClass.getDeclaredBehaviors();
                final String declaredBehaviorsPrintable = Arrays.stream(declaredBehaviors).map(CtMember::getName).collect(Collectors.joining(","));
                System.out.println("Behaviors: " + declaredBehaviorsPrintable);

                Arrays.stream(declaredBehaviors)
                        .filter(ctBehavior -> !ctBehavior.isEmpty())
                        .forEach(method -> findMethodCallSitesAndInstrument(method, newClass.getSimpleName()));

                return newClass.toBytecode();
            } catch (IOException | CannotCompileException e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    private void findMethodCallSitesAndInstrument(final CtBehavior method, String className) {
        try {
            method.instrument(
                    new ExprEditor() {
                        @Override
                        public void edit(MethodCall methodCall) throws CannotCompileException {
                            if (methodCall.getMethodName().equals(methodToInstrument)) {
                                final String caller = className + "::" + method.getName() + ":" + methodCall.getLineNumber();
                                final String callee = getCalleeString(methodCall);
                                System.out.println("Adding instrumentation to " + caller);
                                methodCall.replace(
                                        "{ " +
                                                "String params = \"\"; " +
                                                getCalleeParams(methodCall) +
//                                                "String params = $$.collect(java.util.stream.Collectors.joining()); " +
//                                                "String params = java.util.Arrays.stream($args).<String>map(it -> it.toString()).collect(java.util.stream.Collectors.joining()); " +
//                                                "System.out.println(\"> \" + $class + \"::fib(\" + $args + \" \" + $1 + \")\"); " +
                                                "System.out.println(\"> " + caller + " -> " + callee + "(\" + params + \")\"); " +
                                                "java.lang.management.ThreadMXBean threadMXBean = java.lang.management.ManagementFactory.getThreadMXBean();" +
                                                "long startCpuTime = threadMXBean.getCurrentThreadCpuTime();" +
                                                "long startTime = System.nanoTime(); " +
                                                "$_ = $proceed($$); " +
                                                "long endCpuTime = threadMXBean.getCurrentThreadCpuTime();" +
                                                "long endTime = System.nanoTime(); " +
                                                "int gcCount = 0; int gcTime = 0; " +
                                                "java.util.List garbageCollectorMXBeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();" +
                                                "gcCount += ((java.lang.management.GarbageCollectorMXBean) garbageCollectorMXBeans.get(0)).getCollectionCount();" +
                                                "gcCount += ((java.lang.management.GarbageCollectorMXBean) garbageCollectorMXBeans.get(1)).getCollectionCount();" +
                                                "gcTime += ((java.lang.management.GarbageCollectorMXBean) garbageCollectorMXBeans.get(0)).getCollectionTime();" +
                                                "gcTime += ((java.lang.management.GarbageCollectorMXBean) garbageCollectorMXBeans.get(1)).getCollectionTime();" +
                                                "String cpuTime = Long.toString((endCpuTime-startCpuTime)/1000000);" +
                                                "String totalCpuTime = Long.toString(endCpuTime/1000000);" +
                                                "String wallTime = Long.toString((endTime-startTime)/1000000);" +
                                                "String jmxStats = \" (wall time: \" + wallTime + \"ms, cpu time: \" + cpuTime + \"ms (\" + totalCpuTime + \"ms), gcs: \" + gcCount + \", gct: \" + gcTime + \"ms)\";" +
                                                "System.out.println(\"< " + caller + " -> " + callee + "(\" + params + \") -> \" + $_ + jmxStats);" +
                                                "}");
                            }
                        }
                    }
            );
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    private String getCalleeString(MethodCall methodCall) {
        final String longClassName = methodCall.getClassName();
        return longClassName.substring(longClassName.lastIndexOf('.') + 1) + "::" + methodCall.getMethodName();
    }

    private String getCalleeParams(MethodCall methodCall) {
        try {
            final int calleeParamCount = methodCall.getMethod().getParameterTypes().length;
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < calleeParamCount; i++) {
                if (i != 0) result.append("params += \",\";");
                result.append("params += $").append(i).append(";");
            }
            return result.toString();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}

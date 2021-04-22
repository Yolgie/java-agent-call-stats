package at.cnoize.jku.javabenchmarking.agent;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("Agent loaded");

        instrumentation.addTransformer(new MethodCallSiteLoggingInterceptor("fib"));
//        instrumentation.addTransformer(new MethodCallSiteLoggingInterceptor("doStupidWork"));
//        instrumentation.addTransformer(new MethodCallSiteLoggingInterceptor("doWorkIn"));
    }
}

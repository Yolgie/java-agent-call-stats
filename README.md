# Excercise 1 - Assignment 6: Monitoring Method Calls
## Goal
The goal of this assignment is to develop a Java managed agent that monitors the time a specific
method takes. However, the method itself must not be instrumented, but rather all call sites of the
method. The instrumentation should print the call site, the called method, the parameters, the return
value, and several other metrics (wall clock time, cpu time, the number of garbage collections that
occurred during the method execution and the accumulated time of these garbage collections).
These metrics can be obtained using JMX Beans.

## Testing
Test your agent on some on methods of self-written applications, with a different combination of
parameter types, return types, and call sites.

## Example
Consider the following sample output:
```
> Fib::main → Fib::fib(2)
> Fib::fib → Fib::fib(1)
< Fib::fib → Fib::fib(1) → 1 (wall time 0ms, cpu time 0ms, gcs: 1, gc time:
10ms)
> Fib::fib → Fib::fib(0)
< Fib::fib → Fib::fib(0) → 0 (wall time 1ms, cpu time 0ms, ...)
< Fib::main → Fib::fib(2) → 1 (wall time 3ms, cpu time 1ms, ...)
```

## Source
Excercise for JKU LVA 339349: Special Topics - Java Performance Monitoring and Benchmarking in SS2021

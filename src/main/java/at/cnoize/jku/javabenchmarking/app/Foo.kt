package at.cnoize.jku.javabenchmarking.app

import at.cnoize.jku.javabenchmarking.app.StupidFoo.randomCallee
import kotlin.random.Random

object Foo {
    @JvmStatic
    fun main(args: Array<String>) {
        val fooBar: FooBar = FooBarImpl()
        doStupidWork(2)
        fooBar.doWorkIn(listOf("Paris", "London", "Vienna")) { doStupidWork(1) }
    }
}

interface FooBar {
    fun doWorkIn(places: List<String>, work: (String) -> Unit): String
}

class FooBarImpl : FooBar {
    override fun doWorkIn(places: List<String>, work: (String) -> Unit): String {
        places.forEach { System.out.println("Doing work in $it"); work(it) }
        return "done"
    }
}

fun doStupidWork(count: Int) {
    if (count > 0 && randomCallee(Random.nextInt(), "ThisIsaweirdTextsthathasnotebatuhackntaohentbicaschksnthoe"))
        (1..count).asSequence()
                .forEach { doStupidWork(it - 1) }
}

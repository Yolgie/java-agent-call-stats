package at.cnoize.jku.javabenchmarking.app

import kotlin.random.Random

object StupidFoo {
    fun randomCallee(int: Int, string: String): Boolean {
        return if (string.length < int) {
            string.get(Random.nextInt(string.length)) != int.toChar()
        } else {
            Thread.sleep(20)

            doStupidWork(1)
            true
        }
    }
}

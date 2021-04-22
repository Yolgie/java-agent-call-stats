package at.cnoize.jku.javabenchmarking.app

object Bar {
    @JvmStatic
    fun main(args: Array<String>) {
        doWorkIn("24", 12, 14L, null, "this" as Object, this, this.javaClass)
    }

    private fun doWorkIn(
        string: String,
        int: Int,
        long: Long,
        nothing: Nothing?,
        any: Any,
        bar: Bar,
        clazz: Any
    ) {
        System.out.println("Rly?")
    }
}

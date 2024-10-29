package tornaco.apps.shortx.services.rule.actor.impl.js

interface Console {
    fun log(message: Any?)
    fun info(message: Any?)
    fun warn(message: Any?)
    fun error(message: Any?)
    fun group(label: String)
    fun groupEnd()
    fun time(label: String)
    fun timeEnd(label: String)
    fun count(label: String)
    fun countReset(label: String)
}

class KotlinConsole : Console {
    private val timers = mutableMapOf<String, Long>()
    private val counters = mutableMapOf<String, Int>()
    private var groupLevel = 0

    private fun printWithGroup(message: Any?) {
        println("  ".repeat(groupLevel) + message)
    }

    override fun log(message: Any?) = printWithGroup("LOG: $message")
    override fun info(message: Any?) = printWithGroup("INFO: $message")
    override fun warn(message: Any?) = printWithGroup("WARN: $message")
    override fun error(message: Any?) = printWithGroup("ERROR: $message")

    override fun group(label: String) {
        printWithGroup("GROUP: $label")
        groupLevel++
    }

    override fun groupEnd() {
        if (groupLevel > 0) groupLevel--
    }

    override fun time(label: String) {
        timers[label] = System.currentTimeMillis()
    }

    override fun timeEnd(label: String) {
        val startTime = timers.remove(label)
        if (startTime != null) {
            val elapsed = System.currentTimeMillis() - startTime
            printWithGroup("TIMER [$label]: ${elapsed}ms")
        } else {
            printWithGroup("TIMER [$label] not started.")
        }
    }

    override fun count(label: String) {
        val count = counters.getOrDefault(label, 0) + 1
        counters[label] = count
        printWithGroup("COUNT [$label]: $count")
    }

    override fun countReset(label: String) {
        counters[label] = 0
        printWithGroup("COUNT [$label] reset to 0")
    }
}

fun main() {
    val console = KotlinConsole()

    console.log("Hello World!")
    console.info("Information message")
    console.warn("Warning message")
    console.error("Error message")

    console.group("Group 1")
    console.log("Inside Group 1")
    console.group("Group 2")
    console.log("Inside Group 2")
    console.groupEnd()
    console.groupEnd()

    console.time("Process")
    Thread.sleep(100)
    console.timeEnd("Process")

    console.count("Test")
    console.count("Test")
    console.countReset("Test")
    console.count("Test")
}

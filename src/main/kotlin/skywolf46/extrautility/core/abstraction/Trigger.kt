package skywolf46.extrautility.core.abstraction

interface Trigger : Runnable {
    operator fun invoke() {
        run()
    }
}
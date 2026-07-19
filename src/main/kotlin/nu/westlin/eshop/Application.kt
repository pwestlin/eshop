package nu.westlin.eshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulithic

@SpringBootApplication
// TODO pwestlin: Jag var tvungen att slå av dessa för att få vissa tester att funka. Minns inte ens varför jag lade till dem...
//@Modulithic(sharedModules = [/*Modules.CONFIG*//*, Modules.SECURITY*/])
class Application

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

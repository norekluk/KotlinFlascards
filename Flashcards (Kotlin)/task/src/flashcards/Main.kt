package flashcards

import java.io.File

fun main(args: Array<String>) {
    flashcards(args)
}

const val IMPORT = "-import"
const val EXPORT = "-export"
val log = mutableListOf<String>()
val cards = mutableMapOf<String, String>()
val mistakes = mutableMapOf<String, Int>()

fun <K, V> MutableMap<K, V>.getKey(value: V): Set<K> {
    return this.filter { value == it.value }.keys
}

fun printAndLog(message: String) {
    log.add(message)
    println(message)
}

fun readLineAndLog(): String {
    val input = readln()
    log.add(input)
    return input
}

fun flashcards(args: Array<String>) {
    val importIndex = args.indexOf(IMPORT)
    if (importIndex >= 0) {
        importCardsFromFile(args[importIndex + 1])
    }
    while (true) {
        printAndLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (val input = readLineAndLog()) {
            "add" -> addCard()
            "remove" -> removeCard()
            "import" -> importCardsFromFile()
            "export" -> exportCardsToFile()
            "ask" -> askForDefinitionOfRandomCard()
            "log" -> logCards()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
            "exit" -> {
                printAndLog("Bye bye!")
                val exportIndex = args.indexOf(EXPORT)
                if (exportIndex >= 0) {
                    exportCardsToFile(args[exportIndex + 1])
                }
                return
            }

            else -> printAndLog("No such action: $input. Try again!")
        }
        printAndLog("")
    }

}


fun addCard() {
    printAndLog("The card:")
    val term = readLineAndLog()
    if (cards.containsKey(term)) {
        printAndLog("The card \"$term\" already exists.")
        return
    }
    printAndLog("The definition of the card:")
    val definition = readLineAndLog()
    if (cards.containsValue(definition)) {
        printAndLog("The definition \"$definition\" already exists.")
        return
    }
    cards[term] = definition
    mistakes[term] = 0
    printAndLog("The pair (\"$term\":\"$definition\") has been added.")
}

fun removeCard() {
    printAndLog("Which card?")
    val card = readLineAndLog()
    if (cards.containsKey(card)) {
        cards.remove(card)
        mistakes.remove(card)
        printAndLog("The card has been removed")
    } else {
        printAndLog("Can't remove \"$card\": there is no such card.")
    }
}

fun importCardsFromFile() {
    printAndLog("File name:")
    importCardsFromFile(readLineAndLog())
}

fun importCardsFromFile(filename: String) {
    val file = File(filename)
    if (file.exists()) {
        var numberOfLines = 0
        file.forEachLine { line ->
            run {
                val lineSplit = line.split(";")
                cards[lineSplit[0]] = lineSplit[1]
                mistakes[lineSplit[0]] = lineSplit[2].toInt()
                numberOfLines++
            }
        }
        printAndLog("$numberOfLines cards have been loaded.")
    } else {
        printAndLog("File not found.")
    }
}

fun exportCardsToFile() {
    printAndLog("File name:")
    exportCardsToFile(readLineAndLog())
}

fun exportCardsToFile(filename: String) {
    val file = File(filename)
    file.writeText("")
    for (card in cards) {
        file.appendText("${card.key};${card.value};${mistakes[card.key]}\n")
    }
    printAndLog("${cards.size} cards have been saved.")
}

fun askForDefinitionOfRandomCard() {
    printAndLog("How many times to ask?")
    repeat(readLineAndLog().toInt()) {
        val randomCardKey = cards.keys.random()
        printAndLog("Print the definition of \"${randomCardKey}\"")
        checkAnswer(randomCardKey, readLineAndLog())
    }
}

fun checkAnswer(term: String, answer: String) {
    if (cards[term] == answer) {
        printAndLog("Correct!")
    } else {
        mistakes[term] = mistakes[term]!! + 1
        var anotherDefinition = ""
        if (cards.containsValue(answer)) {
            val key = cards.getKey(answer).first()
            anotherDefinition = ", but your definition is correct for \"$key\"."
        }
        printAndLog("Wrong. The right answer is \"${cards[term]}\"$anotherDefinition")
    }
}

fun logCards() {
    printAndLog("File name:")
    val file = File(readLineAndLog())
    file.writeText(log.joinToString("\n"))
    printAndLog("The log has been saved.")
}

fun hardestCard() {
    val maxMistakes = mistakes.maxByOrNull { it.value }
    if (maxMistakes == null || maxMistakes.value == 0) {
        printAndLog("There are no cards with errors.")
        return
    }
    val allMistakes = mistakes.getKey(maxMistakes.value)
    if (allMistakes.size > 1) {
        printAndLog(
            "The hardest cards are ${
                allMistakes.joinToString(
                    separator = "\", \"", prefix = "\"", postfix = "\""
                )
            }. You have ${maxMistakes.value} errors answering them."
        )
    } else {
        printAndLog("The hardest card is \"${maxMistakes.key}\". You have ${maxMistakes.value} errors answering it.")
    }
}

fun resetStats() {
    for (mistake in mistakes.keys) {
        mistakes[mistake] = 0
    }
    printAndLog("Card statistics have been reset.")
}


fun main() {
    val density = readln().toDouble()
    val height = readln().toDouble()
    val gravity = 9.8
    val pressure = density * height * gravity
    println(pressure)
}
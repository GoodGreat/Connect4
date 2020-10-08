import es.uam.eps.multij.Jugador
import es.uam.eps.multij.JugadorAleatorio
import es.uam.eps.multij.Partida
import JugadorCuatroEnRayaHumano
import TableroCuatroEnRaya
import ObservadorCuatroEnRaya
import java.io.File
import java.io.InputStream

fun main(args: Array<String>){
    val jugadores = arrayListOf<Jugador>()
    var opcion: String? = null
    println("Bienvenido al cuatro en raya.")
    println("1. Jugar partida nueva")
    println("2. Cargar partida")
    println("3. Salir")
    while(opcion == null){
        print("Elige una opcion: ")
        opcion = readLine()
    }
    if(opcion == "1"){
        jugadores += JugadorAleatorio("Aleatorio")
        jugadores += JugadorCuatroEnRayaHumano("Humano")
        val partida = Partida(TableroCuatroEnRaya(), jugadores)
        partida.addObservador(ObservadorCuatroEnRaya())
        partida.comenzar()
    }else if(opcion == "2"){
        var contador = 0
        var partidas = arrayListOf<String>()
        var opcionPartida : Int?
        File("partidas").walk().forEach {
            if(contador!=0){
                println("$contador - $it")
                partidas.add(it.absolutePath)
            }
            contador++

        }
        if(contador <= 1){
            println("No hay partidas guardadas.")
            return
        }
        print("Elige una partida: ")
        opcionPartida = readLine()?.toIntOrNull()
        if(opcionPartida!=null){
            var namePartida = partidas.get(opcionPartida-1)
            println("Partida elegida $namePartida")
            val inputStream: File = File(namePartida)
            var stringTablero = inputStream.readLines()[0]
            jugadores += JugadorAleatorio("Aleatorio")
            jugadores += JugadorCuatroEnRayaHumano("Humano")
            val partida = Partida(TableroCuatroEnRaya(), jugadores)
            partida.tablero.stringToTablero(stringTablero)
            partida.addObservador(ObservadorCuatroEnRaya())
            partida.comenzar()
        }


    }

}
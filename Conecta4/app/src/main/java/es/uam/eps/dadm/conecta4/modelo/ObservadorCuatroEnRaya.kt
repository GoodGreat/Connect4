import es.uam.eps.multij.AccionMover
import es.uam.eps.multij.Evento
import es.uam.eps.multij.PartidaListener

class ObservadorCuatroEnRaya: PartidaListener {

    /*Metodo que se va a encargar de dibujar el estado de la partida a partir de evento.getPartida()*/
    override fun onCambioEnPartida(evento: Evento?) {
        when (evento?.getTipo()) {
            Evento.EVENTO_FIN->{
                /*Printear cosas de fin de partida*/
                val t = evento.partida.tablero
                println(" 0 1 2 3 4 5 6 7")
                if(t is TableroCuatroEnRaya){
                    for(i in t.caracteresTablero.indices){
                        print("|")
                        for(j in t.caracteresTablero[i].indices){
                            if(t.caracteresTablero[i][j]==-1)
                                print(" |")
                            else if(t.caracteresTablero[i][j]==0)
                                print("O|")
                            else
                                print("X|")
                        }
                        println()
                    }
                    println("-----------------")
                }
                println("${evento.descripcion}")
            }
            Evento.EVENTO_CAMBIO -> {
                /*Printea estado actual de la partida*/
                val t = evento.partida.tablero
                println(" 0 1 2 3 4 5 6 7")
                if(t is TableroCuatroEnRaya){
                    for(i in t.caracteresTablero.indices){
                        print("|")
                        for(j in t.caracteresTablero[i].indices){
                            if(t.caracteresTablero[i][j]==-1)
                                print(" |")
                            else if(t.caracteresTablero[i][j]==0)
                                print("O|")
                            else
                                print("X|")
                        }
                        println()
                    }
                    println("-----------------")
                }

            }

            Evento.EVENTO_CONFIRMA -> {}

            Evento.EVENTO_TURNO -> {}
        }
    }
}
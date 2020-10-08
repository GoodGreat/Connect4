import es.uam.eps.multij.AccionMover
import es.uam.eps.multij.Evento
import es.uam.eps.multij.Jugador
import es.uam.eps.multij.Tablero

class JugadorCuatroEnRayaHumano(private val nombre: String): Jugador {

    /*Metodo que devuelve el nombre de jugador*/
    override fun getNombre(): String {
        return nombre
    }
    /*Devuelve true si el jugador puede jugar, esto es un poco raro porque solo se le va a ofrecer jugar en su turno, asi que parece que siempre devolvera true*/
    override fun puedeJugar(tablero: Tablero?): Boolean {return true}
    /*Metodo que esta atento a los eventos para saber si es su turno o que esta ocurriendo en el juego*/
    override fun onCambioEnPartida(evento: Evento?) {
        when (evento?.getTipo()) {
            Evento.EVENTO_FIN, Evento.EVENTO_CAMBIO -> {
            }

            Evento.EVENTO_CONFIRMA ->
                // este jugador confirma al azar
                try {
                    evento.getPartida().confirmaAccion(
                        this, evento.getCausa(), Math.random() > .5
                    )
                } catch (e: Exception) {

                }

            Evento.EVENTO_TURNO -> {
                print("Introduce la columna para tu siguiente ficha ('g' , 'guardar' o 's' , 'salir'): ")
                val entradaTeclado = readLine()
                if (entradaTeclado=="g" || entradaTeclado=="guardar"){
                    try {
                        evento.getPartida().realizaAccion(AccionGuardar(this))
                    } catch (e: Exception) {
                        println(e.message)
                    }

                }else if(entradaTeclado=="s" || entradaTeclado=="salir"){
                    /*Codigo para cerrar el juego*/
                }else{
                    var columnaMovimiento = entradaTeclado?.toIntOrNull()
                    if(columnaMovimiento!=null){
                        /*Codigo para crear el movimiento que quiere hacer, comprobacion de que es valido y realizar accion*/
                        val mov = MovimientoCuatroEnRaya(columnaMovimiento)
                        try {
                            evento.getPartida().realizaAccion(AccionMover(this, mov)
                            )
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }else{
                        /*Ha introducido mal el comando, ni numero, ni guardar, ni salir*/
                        /*Creo que llamando a la funcion otra vez con el mismo evento se soluciona*/
                        println("Entrada no valida. Revisa la entradas validas.")
                        this.onCambioEnPartida(evento)
                    }
                }

            }
        }
    }
}
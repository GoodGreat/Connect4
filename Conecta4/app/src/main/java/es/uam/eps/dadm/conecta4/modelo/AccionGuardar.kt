import es.uam.eps.multij.Accion
import es.uam.eps.multij.Jugador
import es.uam.eps.multij.Partida
import java.io.File
import java.time.LocalDateTime

class AccionGuardar(val j: Jugador): Accion {


    /*Funcion que devuelve el jugador que guarda la partida*/
    override fun getOrigen(): Jugador {
        return j
    }

    /*Metodo que indica si la accion requiere confirmacion*/
    override fun requiereConfirmacion(): Boolean {
        return false
    }

    /*Metodo que ejecuta la accion sobre la partida que se pasa como argumento*/
    override fun ejecuta(p: Partida?) {
        if (p!=null){
            var cadenaGuardar = p.tablero.tableroToString()
            val fechahora = LocalDateTime.now()
            File("partidas/$fechahora.txt").writeText(cadenaGuardar)
        }

    }
}
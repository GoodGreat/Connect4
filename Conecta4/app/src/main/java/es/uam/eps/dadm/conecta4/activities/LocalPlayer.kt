package es.uam.eps.dadm.conecta4.activities

import android.view.View
import es.uam.eps.dadm.conecta4.R
import es.uam.eps.multij.*
import MovimientoCuatroEnRaya
import android.support.design.widget.Snackbar
import es.uam.eps.dadm.conecta4.modelo.Round


class LocalPlayer(var round: Round): View.OnClickListener, Jugador{

    private val ids = arrayOf(intArrayOf(R.id.tablero00, R.id.tablero01, R.id.tablero02, R.id.tablero03, R.id.tablero04, R.id.tablero05, R.id.tablero06, R.id.tablero07),
                                intArrayOf(R.id.tablero10, R.id.tablero11, R.id.tablero12, R.id.tablero13, R.id.tablero14, R.id.tablero15, R.id.tablero16, R.id.tablero17),
                                intArrayOf(R.id.tablero20, R.id.tablero21, R.id.tablero22, R.id.tablero23, R.id.tablero24, R.id.tablero25, R.id.tablero26, R.id.tablero27),
                                intArrayOf(R.id.tablero30, R.id.tablero31, R.id.tablero32, R.id.tablero33, R.id.tablero34, R.id.tablero35, R.id.tablero36, R.id.tablero37),
                                intArrayOf(R.id.tablero40, R.id.tablero41, R.id.tablero42, R.id.tablero43, R.id.tablero44, R.id.tablero45, R.id.tablero46, R.id.tablero47),
                                intArrayOf(R.id.tablero50, R.id.tablero51, R.id.tablero52, R.id.tablero53, R.id.tablero54, R.id.tablero55, R.id.tablero56, R.id.tablero57),
                                intArrayOf(R.id.tablero60, R.id.tablero61, R.id.tablero62, R.id.tablero63, R.id.tablero64, R.id.tablero65, R.id.tablero66, R.id.tablero67))

    private lateinit var game: Partida

    /*Setea una partida*/
    fun setPartida(game: Partida){
        this.game = game
    }
    /*Metodo onClick para que al tocar una casilla del tablero se transforme en un movimiento y se ejecute el movimiento*/
    override fun onClick(v: View) {
        if((game.tablero.turno == 0 && ERSettingsActivity.getPlayerUUID(v.context) == round.firstPlayerUUID) || (game.tablero.turno == 1 && ERSettingsActivity.getPlayerUUID(v.context) == round.secondPlayerUUID)){
            var turnoAux = game.tablero.turno
            val m: Movimiento = MovimientoCuatroEnRaya(fromViewToJ(v))
            val a = AccionMover(this, m)
            game.realizaAccion(a)
            if(turnoAux == game.tablero.turno){
                Snackbar.make(v, R.string.movimientoInvalido, Snackbar.LENGTH_SHORT).show()
            }
        }else{
            Snackbar.make(v, R.string.noestuturno, Snackbar.LENGTH_SHORT).show()
        }

    }

    /*Devuelve el nombre del jugador*/
    override fun getNombre(): String {
        return "Local Player"
    }

    /*Tiene que implementarla por extender de Jugador, pero no hace nada en esta ocasion*/
    override fun onCambioEnPartida(evento: Evento) {
    }

    /*Devuelve si puede jugar el jugador*/
    override fun puedeJugar(Tablero: Tablero): Boolean {
        return true
    }

    /*Extrae la coordenada J de la casilla que se le pasa por arguemnto*/
    fun fromViewToJ(view: View) : Int{
        for(i in 0..6){
            for(j in 0..7){
                if(view.id == ids[i][j])
                    return j
            }
        }
        return -1
    }
}
package es.uam.eps.dadm.conecta4.activities


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent

import es.uam.eps.dadm.conecta4.R
import TableroCuatroEnRaya
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import es.uam.eps.dadm.conecta4.modelo.Round
import es.uam.eps.dadm.conecta4.modelo.RoundRepositoryFactory
import es.uam.eps.multij.*
import kotlinx.android.synthetic.main.fragment_round_list.*
import java.lang.RuntimeException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoundFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RoundFragment : Fragment(), PartidaListener {
    private val rows = 7
    private val cols = 8
    private lateinit var game: Partida
    //private lateinit var tablero: TableroCuatroEnRaya
    private lateinit var round: Round
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var listener: OnRoundFragmentInteractionListener? = null
    interface OnRoundFragmentInteractionListener{
        fun onRoundUpdated(round: Round)
    }

    /*Metodo onCreate del RoundFragment que si esta cargando una partida, genera el tablero guardado*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        round = Round()
        try{
            arguments?.let {
                round = Round.fromJSONString(it.getString(ARG_ROUND))
            }
        }catch (e: Exception){
            Log.d("DEBUG", e.message)
            activity?.finish()
        }
    }

    /*Companion object para crear nuevas instancias desde fuera de la clase*/
    companion object {
        val ARG_ROUND = "es.uam.eps.dadm.conecta4.round"
        val EXTRA_BOARD_STRING = "es.uam.eps.dadm.conecta4.boardString"
        @JvmStatic
        fun newInstance(round: String) =
            RoundFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROUND, round)
                }
            }
    }

    /*Metodo que rellena el fragment de round*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_round, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState != null){
            round = Round.fromJSONString(savedInstanceState.getString(EXTRA_BOARD_STRING))
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnRoundFragmentInteractionListener)
            listener = context
        else{
            throw RuntimeException(context.toString() + " must implement OnRoundFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /*Metodo que configura los botones que aparecen en pantalla y empieza la partida*/
    override fun onStart() {
        super.onStart()
        configurarBotonListaPartidas()
        configurarBotonSalir()
        startRound()
    }

    override fun onResume() {
        super.onResume()
        view?.update(round.board, rows, cols)
    }

    /*Devuelve el tablero*/
    fun devolverRound(): Round{
        /*
        if(!(::tablero.isInitialized)){
            round.board = TableroCuatroEnRaya()
        }
        */
        return round
    }

    /*Añade los jugadores, los observadores y comienza la partida tras poner al jugador como un listener de los clicks*/
    fun startRound(){
        val players = ArrayList<Jugador>()
        //val randomPlayer = JugadorAleatorio(resources.getString(R.string.nameRandomPlayer))
        val localPlayer = LocalPlayer(round)
        val localPlayer2 = LocalPlayer(round)
        //players.add(randomPlayer)
        players.add(localPlayer)
        players.add(localPlayer2)
        /*
        if(!(::tablero.isInitialized)){
            tablero = TableroCuatroEnRaya()
        }
        */
        game = Partida(round.board, players)
        game.addObservador(this)
        localPlayer.setPartida(game)
        view?.setPlayerAsOnClickListener(localPlayer, rows, cols)
        if(game.tablero.estado == Tablero.EN_CURSO){
            game.comenzar()
        }
        if(game.tablero.turno == 0){
            /*Eres color verde*/
            var textoColor = view!!.findViewById<TextView>(R.id.colorJugador)
            textoColor.text = getString(R.string.juegasConVerde)
        }else{
            /*Eres color azul*/
            var textoColor = view!!.findViewById<TextView>(R.id.colorJugador)
            textoColor.text = getString(R.string.juegasConAzul)
        }
    }

    /*Metodo que actua ante los distintos eventos que suceden durante la partida*/
    override fun onCambioEnPartida(evento: Evento) {
        when (evento.tipo){
            Evento.EVENTO_CAMBIO ->{
                view?.update(round.board, rows, cols)
                listener?.onRoundUpdated(round)
            }
            Evento.EVENTO_FIN -> {
                view?.update(round.board, rows, cols)
                listener?.onRoundUpdated(round)
                if(game.tablero.turno == 0)
                    Snackbar.make(view!!, R.string.ganaMaquina, Snackbar.LENGTH_SHORT).show()
                else
                    Snackbar.make(view!!, R.string.ganaHumano, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /*Metodo que configura el listener del boton salir*/
    fun configurarBotonSalir(){
        var botonSalir = view!!.findViewById(R.id.botonSalir) as ImageButton
        botonSalir.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View) {
                activity!!.onBackPressed()
            }
        })
    }


    /*Metodo que configura el listener del boton para listar las partidas*/
    fun configurarBotonListaPartidas(){
        var botonListaPartidas = view!!.findViewById(R.id.botonListaPartidas) as ImageButton
        botonListaPartidas.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View) {
                startActivity(Intent(context, RoundListActivity::class.java))
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXTRA_BOARD_STRING, round.toJSONString())
        super.onSaveInstanceState(outState)
    }


}

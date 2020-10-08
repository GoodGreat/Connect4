package es.uam.eps.dadm.conecta4.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import es.uam.eps.dadm.conecta4.R
import es.uam.eps.dadm.conecta4.modelo.Round
import kotlinx.android.synthetic.main.activity_round_list.*
import kotlinx.android.synthetic.main.activity_twopane.*
import kotlinx.android.synthetic.main.fragment_round_list.*
import java.io.File
import TableroCuatroEnRaya
import android.app.Activity
import android.util.DisplayMetrics
import android.view.Display
import es.uam.eps.dadm.conecta4.modelo.RoundRepository
import es.uam.eps.dadm.conecta4.modelo.RoundRepositoryFactory

class RoundListActivity : AppCompatActivity(), RoundListFragment.OnRoundListFragmentInteractionListener, RoundFragment.OnRoundFragmentInteractionListener{

    var fragment: RoundFragment? = null
    val TABLEROSTRING = "es.uam.eps.dadm.conecta4"
    val EXTRA_BOARD_STRING = "es.uam.eps.dadm.conecta4.boardString"

    /*Metodo onCreate de RoundListActivity que crea un fragmento de RoundListFragment y lo añade, o si viene de crear nueva partida en tablet, añade el fragment de nueva partida tambien*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_detail)
        val fm = supportFragmentManager
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            fm.executeTransaction { add(R.id.fragment_container, RoundListFragment()) }
        }

        if(intent.extras!=null){
            if (intent.extras!!.getString("NUEVA_PARTIDA") != null) {
                var display = windowManager.defaultDisplay
                var outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                var density = resources.displayMetrics.density
                var dpWidth = outMetrics.widthPixels / density
                if (dpWidth >= 900) {
                    if(intent.extras!!.getString("NUEVA_PARTIDA") != "NUEVA_PARTIDA") {
                        fragment = RoundFragment.newInstance(intent.extras!!.getString("NUEVA_PARTIDA")!!)
                        fm.executeTransaction { replace(R.id.detail_fragment_container, fragment!!) }
                    }else {
                        var tableroAux = TableroCuatroEnRaya()
                        fragment = RoundFragment.newInstance(tableroAux.tableroToString())
                        fm.executeTransaction { replace(R.id.detail_fragment_container, fragment!!) }
                    }
                }
            }
        }
    }

    /*Listener de los elementos del recycler view de cargar partida*/
    override fun onRoundSelected(round: Round, bol: Boolean) {
        if(!bol){
            val fm = supportFragmentManager
            /*Si no soy quien creo la partida y hay un jugador "vacio", me uno a la partida*/
            if(round.secondPlayerUUID != ERSettingsActivity.getPlayerUUID(this@RoundListActivity) && round.firstPlayerUUID == "vacio"){
                round.firstPlayerName = ERSettingsActivity.getPlayerName(this@RoundListActivity)
                round.firstPlayerUUID = ERSettingsActivity.getPlayerUUID(this@RoundListActivity)
            }
            if(detail_fragment_container == null){
                val intent = Intent(applicationContext, RoundActivity::class.java)
                intent.putExtra(EXTRA_BOARD_STRING, round.toJSONString())
                startActivity(intent)
            }else{
                fragment = RoundFragment.newInstance(round.toJSONString())
                fm.executeTransaction { replace(R.id.detail_fragment_container, fragment!!)}
            }
        }else{
            val repository = RoundRepositoryFactory.createRepository(this)
            repository!!.deleteRound(round)
            recyclerView.update(ERSettingsActivity.getPlayerUUID(baseContext),{round, bol -> onRoundSelected(round, bol)})
        }

    }

    /*Metodo que sirve para actualizar el recycler view de cargar partida cuando se cambia algun elemento*/
    override fun onRoundUpdated(round: Round) {
        val repository = RoundRepositoryFactory.createRepository(this)
        val callback = object : RoundRepository.BooleanCallback{
            override fun onResponse(ok: Boolean){
                if(ok == true){
                    recyclerView.update(ERSettingsActivity.getPlayerUUID(baseContext),{round, bol -> onRoundSelected(round, bol)})
                }
            }
        }
        repository?.updateRound(round, callback)
    }

    /*Metodo que guarda el estado de la actividad antes de rotar el dispositivo*/
    override fun onSaveInstanceState(outState: Bundle?) {
        var display = windowManager.defaultDisplay
        var outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        var density = resources.displayMetrics.density
        var dpWidth = outMetrics.widthPixels / density
        if (dpWidth >= 900) {
            if(fragment!=null)
                outState?.putString(EXTRA_BOARD_STRING, fragment!!.devolverRound().toJSONString())
        }
        super.onSaveInstanceState(outState)
    }

    /*Metodo que recargar el estado anterior a la rotacion de la partida*/
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState?.getString(EXTRA_BOARD_STRING) != null){
            var display = windowManager.defaultDisplay
            var outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            var density = resources.displayMetrics.density
            var dpWidth = outMetrics.widthPixels / density
            if(dpWidth < 900){
                finish()
                var intent = Intent(this@RoundListActivity, RoundActivity::class.java)
                intent.putExtra(EXTRA_BOARD_STRING, savedInstanceState.getString(EXTRA_BOARD_STRING))
                startActivity(intent)
            }
            if (dpWidth >= 900) {
                var fm = supportFragmentManager
                fragment = RoundFragment.newInstance(savedInstanceState.getString(EXTRA_BOARD_STRING)!!)
                fm.executeTransaction { replace(R.id.detail_fragment_container, fragment!!) }
            }
        }

    }

}

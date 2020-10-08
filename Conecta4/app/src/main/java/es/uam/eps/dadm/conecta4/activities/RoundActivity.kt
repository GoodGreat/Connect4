package es.uam.eps.dadm.conecta4.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import es.uam.eps.dadm.conecta4.R
import es.uam.eps.multij.*
import TableroCuatroEnRaya
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import es.uam.eps.dadm.conecta4.modelo.Round
import es.uam.eps.dadm.conecta4.modelo.RoundRepository
import es.uam.eps.dadm.conecta4.modelo.RoundRepositoryFactory
import kotlinx.android.synthetic.main.fragment_round_list.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.random.Random

class RoundActivity : AppCompatActivity(), RoundFragment.OnRoundFragmentInteractionListener {


    val TABLEROSTRING = "es.uam.eps.dadm.conecta4"

    val EXTRA_BOARD_STRING = "es.uam.eps.dadm.conecta4.boardString"
    var fragment = RoundFragment()

    /*Metodo onCreate del RoundActivity, crea el fragmento si no existe de una nueva partida y lo añade*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_detail)
        val fm = supportFragmentManager
        if (fm.findFragmentById(R.id.fragment_container) == null) {

            if(intent.getStringExtra(EXTRA_BOARD_STRING) != null) {
                fragment = RoundFragment.newInstance(intent.getStringExtra(EXTRA_BOARD_STRING))
            }
            else
                fragment = RoundFragment()
            fm.executeTransaction { add(R.id.fragment_container, fragment) }
        }
    }

    /*Metodo que se ejecuta cuando se selecciona una partida en la recyclerview*/
    override fun onRoundUpdated(round: Round) {
        val repository = RoundRepositoryFactory.createRepository(this)
        val callback = object : RoundRepository.BooleanCallback{
            override fun onResponse(ok: Boolean){
                if(ok == true){

                }else{

                }

            }
        }
        repository?.updateRound(round, callback)
    }



    /*Metodo que sirve para guardar el estado de la partida cuando se va a girar el dispositivo*/

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(EXTRA_BOARD_STRING, fragment.devolverRound().toJSONString())
        super.onSaveInstanceState(outState)
    }



    /*Metood que sirve para cargar el estado anterior de la partida despues de haber girado el dispositivo*/

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        try {
            if (savedInstanceState?.getString(EXTRA_BOARD_STRING) != null) {
                var display = windowManager.defaultDisplay
                var outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                var density = resources.displayMetrics.density
                var dpWidth = outMetrics.widthPixels / density
                if (dpWidth < 900){
                    fragment = RoundFragment.newInstance(savedInstanceState.getString(EXTRA_BOARD_STRING)!!)
                    val fm = supportFragmentManager
                    fm.executeTransaction { add(R.id.fragment_container, fragment) }
                }else{
                    var intent = Intent(this@RoundActivity, RoundListActivity::class.java)
                    intent.putExtra("NUEVA_PARTIDA", savedInstanceState.getString(EXTRA_BOARD_STRING))
                    finish()
                    startActivity(intent)
                }
            }
        } catch (e: ExcepcionJuego) {
            e.printStackTrace()
            Snackbar.make(findViewById(R.id.nombreAplicacion), "ExcepcionJuego thrown.", Snackbar.LENGTH_SHORT).show()
        }
    }




}

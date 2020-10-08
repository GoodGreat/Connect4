package es.uam.eps.dadm.conecta4.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import es.uam.eps.dadm.conecta4.R
import es.uam.eps.multij.*
import TableroCuatroEnRaya
import android.content.Intent
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import es.uam.eps.dadm.conecta4.database.ERDataBase
import es.uam.eps.dadm.conecta4.modelo.Round
import es.uam.eps.dadm.conecta4.modelo.RoundRepository
import es.uam.eps.dadm.conecta4.modelo.RoundRepositoryFactory

class MainActivity : AppCompatActivity() {


    val EXTRA_BOARD_STRING = "es.uam.eps.dadm.conecta4.boardString"

    /*Funcion onCreate del MainActivity que configura los botones que aparecen en la misma*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configurarBotonNuevaPartida();
        configurarBotonCargarPartida();

    }

    /*Metodo que configura el listener del boton de nueva partida*/
    private fun configurarBotonNuevaPartida(){
        var botonNuevaPartida = findViewById(R.id.botonNuevaPartida) as Button
        botonNuevaPartida.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View) {
                var display = windowManager.defaultDisplay
                var outMetrics = DisplayMetrics()
                display.getMetrics(outMetrics)
                var density = resources.displayMetrics.density
                var dpWidth = outMetrics.widthPixels / density
                val round = Round()
                round.secondPlayerName = ERSettingsActivity.getPlayerName(this@MainActivity)
                round.secondPlayerUUID = ERSettingsActivity.getPlayerUUID(this@MainActivity)
                val repository = RoundRepositoryFactory.createRepository(this@MainActivity)
                if(repository is ERDataBase){
                    round.firstPlayerName = ERSettingsActivity.getPlayerName(this@MainActivity)
                    round.firstPlayerUUID = ERSettingsActivity.getPlayerUUID(this@MainActivity)
                }else{
                    round.firstPlayerName = "vacio" // vacio
                    round.firstPlayerUUID = "vacio" // vacio
                }
                val callback = object : RoundRepository.BooleanCallback {
                    override fun onResponse(response: Boolean) {
                        if(response == false)
                            Snackbar.make(findViewById(R.id.nombreAplicacion), "Error añadiendo partida", Snackbar.LENGTH_LONG).show()
                        else{
                            Snackbar.make(findViewById(R.id.nombreAplicacion), "Nueva ronda", Snackbar.LENGTH_LONG).show()
                        }
                    }

                }
                repository?.addRound(round, callback)



            }
        })

    }

    /*Metodo que configura el listener del boton de cargar partida*/
    private fun configurarBotonCargarPartida(){
        var botonCargarPartida = findViewById(R.id.botonCargarPartida) as Button
        botonCargarPartida.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View){
                startActivity(Intent(this@MainActivity, RoundListActivity::class.java))
            }
        })
    }



}

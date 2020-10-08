package es.uam.eps.dadm.conecta4.activities

import android.widget.ImageButton
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.FragmentManager
import android.view.View
import es.uam.eps.dadm.conecta4.R
import TableroCuatroEnRaya
import android.support.v7.widget.RecyclerView
import es.uam.eps.dadm.conecta4.firebase.FBDataBase
import es.uam.eps.dadm.conecta4.modelo.Round
import es.uam.eps.dadm.conecta4.modelo.RoundRepository
import es.uam.eps.dadm.conecta4.modelo.RoundRepositoryFactory


/*Funcion que actualiza el color de una ficha en funcion del estado del tablero*/
fun ImageButton.update(tablero: TableroCuatroEnRaya, i: Int, j: Int){
    if(tablero.caracteresTablero[i][j] == 0)
        setBackgroundResource(R.drawable.casilla_verde)
    else if(tablero.caracteresTablero[i][j] == -1)
        setBackgroundResource(R.drawable.casilla_vacia)
    else
        setBackgroundResource(R.drawable.casilla_azul)
}
/*Funcion que ejecuta la operacion pertinente sobre el fragmento indicado*/
fun FragmentManager.executeTransaction(operations: (FragmentTransaction.()->Unit)) {
    val transaction = beginTransaction()
    transaction.operations()
    transaction.commit()
}
/*Funcion que le asigna el listener a todos los botones del tablero para reaccionar a los eventos*/
fun View.setPlayerAsOnClickListener(player: View.OnClickListener, rows : Int, cols: Int){
    val ids = arrayOf(intArrayOf(R.id.tablero00, R.id.tablero01, R.id.tablero02, R.id.tablero03, R.id.tablero04, R.id.tablero05, R.id.tablero06, R.id.tablero07),
        intArrayOf(R.id.tablero10, R.id.tablero11, R.id.tablero12, R.id.tablero13, R.id.tablero14, R.id.tablero15, R.id.tablero16, R.id.tablero17),
        intArrayOf(R.id.tablero20, R.id.tablero21, R.id.tablero22, R.id.tablero23, R.id.tablero24, R.id.tablero25, R.id.tablero26, R.id.tablero27),
        intArrayOf(R.id.tablero30, R.id.tablero31, R.id.tablero32, R.id.tablero33, R.id.tablero34, R.id.tablero35, R.id.tablero36, R.id.tablero37),
        intArrayOf(R.id.tablero40, R.id.tablero41, R.id.tablero42, R.id.tablero43, R.id.tablero44, R.id.tablero45, R.id.tablero46, R.id.tablero47),
        intArrayOf(R.id.tablero50, R.id.tablero51, R.id.tablero52, R.id.tablero53, R.id.tablero54, R.id.tablero55, R.id.tablero56, R.id.tablero57),
        intArrayOf(R.id.tablero60, R.id.tablero61, R.id.tablero62, R.id.tablero63, R.id.tablero64, R.id.tablero65, R.id.tablero66, R.id.tablero67))
    for(i in 0..rows-1){
        for(j in 0..cols-1){
            val button = findViewById(ids[i][j]) as ImageButton
            button.setOnClickListener(player)
        }
    }
}
/*Funcion que actualiza los colores de los botones del tablero, llama a la funcion update de ImageButton descrita anteriormente en este fichero*/
fun View.update(tablero: TableroCuatroEnRaya, rows: Int, cols: Int){
    val ids = arrayOf(intArrayOf(R.id.tablero00, R.id.tablero01, R.id.tablero02, R.id.tablero03, R.id.tablero04, R.id.tablero05, R.id.tablero06, R.id.tablero07),
        intArrayOf(R.id.tablero10, R.id.tablero11, R.id.tablero12, R.id.tablero13, R.id.tablero14, R.id.tablero15, R.id.tablero16, R.id.tablero17),
        intArrayOf(R.id.tablero20, R.id.tablero21, R.id.tablero22, R.id.tablero23, R.id.tablero24, R.id.tablero25, R.id.tablero26, R.id.tablero27),
        intArrayOf(R.id.tablero30, R.id.tablero31, R.id.tablero32, R.id.tablero33, R.id.tablero34, R.id.tablero35, R.id.tablero36, R.id.tablero37),
        intArrayOf(R.id.tablero40, R.id.tablero41, R.id.tablero42, R.id.tablero43, R.id.tablero44, R.id.tablero45, R.id.tablero46, R.id.tablero47),
        intArrayOf(R.id.tablero50, R.id.tablero51, R.id.tablero52, R.id.tablero53, R.id.tablero54, R.id.tablero55, R.id.tablero56, R.id.tablero57),
        intArrayOf(R.id.tablero60, R.id.tablero61, R.id.tablero62, R.id.tablero63, R.id.tablero64, R.id.tablero65, R.id.tablero66, R.id.tablero67))
    for(i in 0..rows-1){
        for(j in 0..cols-1){
            val button = findViewById(ids[i][j]) as ImageButton
            button.update(tablero, i, j)
        }
    }
}
/*Funcion que actualiza el recycler view de cargar partida, recibe la lista de partidas y el listener que tendra cada elemento*/
fun RecyclerView.update(userName: String, onClickListener: (Round, Boolean) -> Unit){
    val repository = RoundRepositoryFactory.createRepository(context)

    val roundsCallback = object : RoundRepository.RoundsCallback {
        override fun onResponse(rounds: List<Round>) {
            if (adapter == null)
                adapter = RoundAdapter(rounds, onClickListener)
            else {
                (adapter as RoundAdapter).rounds = rounds
                adapter!!.notifyDataSetChanged()
            }
        }

        override fun onError(error: String){

        }
    }

    if(repository is FBDataBase)
        repository.startListeningChanges(userName, roundsCallback)

    repository?.getRounds(userName, "", "", roundsCallback)
}
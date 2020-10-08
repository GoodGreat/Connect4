package es.uam.eps.dadm.conecta4.activities


import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import es.uam.eps.dadm.conecta4.R
import es.uam.eps.dadm.conecta4.modelo.Round
import java.io.File

class RoundHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    var idTextView: TextView
    var boardTextView: TextView
    var dateTextView: TextView
    var botonBorrarPartida: ImageButton
    init {
        idTextView = itemView.findViewById(R.id.list_item_id) as TextView
        boardTextView = itemView.findViewById(R.id.list_item_board) as TextView
        dateTextView = itemView.findViewById(R.id.list_item_date) as TextView
        botonBorrarPartida = itemView.findViewById((R.id.botonBorrarPartida)) as ImageButton
    }
    /*Metodo que asigna los valores oportunos y los listener a los items del recycler view*/
    fun bindRound(round: Round, listener:(Round, Boolean) -> Unit){
        idTextView.text = round.title
        boardTextView.text = round.board.simpleString()
        dateTextView.text = round.date.toString().substring(0,19)

        itemView.setOnClickListener{listener(round, false)}
        botonBorrarPartida.setOnClickListener{listener(round, true)}
    }
}

class RoundAdapter(var rounds: List<Round>, val listener: (Round, Boolean) -> Unit): RecyclerView.Adapter<RoundHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoundHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_round, parent, false)
        return RoundHolder(view)
    }
    /*Metodo que devuelve el tamaño de la lista que se muestra en el recycler view*/
    override fun getItemCount(): Int = rounds.size

    /*Metodo que añade un round al recycler view*/
    override fun onBindViewHolder(holder: RoundHolder, position: Int) {
        holder.bindRound(rounds[position], listener)
    }
}
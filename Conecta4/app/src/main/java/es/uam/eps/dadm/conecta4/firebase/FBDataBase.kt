package es.uam.eps.dadm.conecta4.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import es.uam.eps.dadm.conecta4.activities.ERSettingsActivity
import es.uam.eps.dadm.conecta4.modelo.Round
import es.uam.eps.dadm.conecta4.modelo.RoundRepository

class FBDataBase(var context: Context): RoundRepository{

    private val DATABASENAME = "partidas"
    lateinit var db: DatabaseReference

    /**FUNCIONES COMENTADAS EN LA INTERFAZ ROUNDREPOSITORY*/

    override fun open() {
        db = FirebaseDatabase.getInstance().reference.child(DATABASENAME)
    }

    override fun close() {
    }

    override fun login(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(playername, password).addOnCompleteListener {
            if(it.isSuccessful)
                callback.onLogin(firebaseAuth.currentUser!!.uid)
            else
                callback.onError("Username or password incorrect.")
        }
    }

    override fun register(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(playername, password).addOnCompleteListener {
            if(it.isSuccessful)
                callback.onLogin(firebaseAuth.currentUser!!.uid)
            else
                callback.onError("Error inserting new player named $playername")
        }
    }

    override fun getRounds(
        playerUuid: String,
        orderByField: String,
        group: String,
        callback: RoundRepository.RoundsCallback
    ) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                var rounds = listOf<Round>()
                for (postSnapshot in p0.children){
                    val round = postSnapshot.getValue(Round::class.java)!!
                    if(isOpenOrIamIn(round, playerUuid))
                        rounds += round
                }
                callback.onResponse(rounds)
            }
        })
    }

    fun isOpenOrIamIn(round: Round, playerUuid: String): Boolean{
        /*Si la partida es mia, o ya me uni a ella o puedo unirme a ella*/
        if(round.firstPlayerUUID == playerUuid || round.secondPlayerUUID == playerUuid || round.firstPlayerUUID == "vacio"){
            /*Si no me toca jugar la partida, no me la muestra*/
            if((round.board.turno == 0 && ERSettingsActivity.getPlayerUUID(context) == round.secondPlayerUUID) || (round.board.turno == 1 && ERSettingsActivity.getPlayerUUID(context) == round.firstPlayerUUID))
                return false
            return true
        }

        return false
    }

    override fun addRound(round: Round, callback: RoundRepository.BooleanCallback) {
        //println(round.toJSONString())
        if(db.child(round.id).setValue(round).isComplete){
            callback.onResponse(false)
        }

        else {
            callback.onResponse(true)
        }
    }

    override fun deleteRound(round: Round) {
        db.child(round.id).removeValue()
    }

    override fun updateRound(round: Round, callback: RoundRepository.BooleanCallback) {
        if(db.child(round.id).setValue(round).isSuccessful)
            callback.onResponse(true)
        else
            callback.onResponse(false)
    }

    /**Se la tiene que llamar una vez al arrancar la app para escuchar los cambios en las partidas de la nube*/
    fun startListeningChanges(playerUuid: String, callback: RoundRepository.RoundsCallback){
        db = FirebaseDatabase.getInstance().reference.child(DATABASENAME)
        db.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                var rounds = listOf<Round>()
                for(postSnapshot in p0.children){
                    var round = postSnapshot.getValue(Round::class.java)!!
                    if(isOpenOrIamIn(round, playerUuid))
                        rounds += round
                }

                callback.onResponse(rounds)
            }
        })
    }

}
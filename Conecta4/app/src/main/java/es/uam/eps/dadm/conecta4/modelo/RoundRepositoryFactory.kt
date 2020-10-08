package es.uam.eps.dadm.conecta4.modelo
import android.content.Context
import es.uam.eps.dadm.conecta4.database.ERDataBase
import es.uam.eps.dadm.conecta4.firebase.FBDataBase
import java.lang.Exception

object RoundRepositoryFactory {
    /**VARIABLE A CAMBIAR SI SE QUIERE LOCAL(true) O EN FIREBASE(false)*/
    private val LOCAL = false
    fun createRepository(context: Context): RoundRepository?{
        val repository: RoundRepository
        repository = if (LOCAL) ERDataBase(context) else FBDataBase(context)

        try{
            repository.open()
        }catch (e: Exception){
            return null
        }
        return repository
    }
}
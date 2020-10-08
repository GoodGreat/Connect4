package es.uam.eps.dadm.conecta4.modelo

interface RoundRepository {

    @Throws(Exception::class)
    /**Abre una conexion con la base de datos*/
    fun open()
    /**Cierra la conexion con la base de datos*/
    fun close()
    /**Interfaz que implementa los metodos de logeo correcto o error en el logeo*/
    interface LoginRegisterCallback{
        fun onLogin(playerUuid: String)
        fun onError(error: String)
    }
    /**Logea el usuario con su usuario y contraseña*/
    fun login(playername: String, password: String, callback: LoginRegisterCallback)
    /**Registra al usuario con usuario y contraseña*/
    fun register(playername: String, password: String, callback: LoginRegisterCallback)
    interface BooleanCallback{
        fun onResponse(ok: Boolean)
    }
    /**Genera la lista de partidas asociadas a un jugador determinado*/
    fun getRounds(playerUuid: String, orderByField: String, group: String, callback: RoundsCallback)
    /**Añade una partida al repositorio de partidas*/
    fun addRound(round: Round, callback: BooleanCallback)
    /**Acutaliza una partida del repositorio*/
    fun updateRound(round: Round, callback: BooleanCallback)
    interface RoundsCallback{
        fun onResponse(rounds: List<Round>)
        fun onError(error: String)
    }
    /**Borra una partida determinada*/
    fun deleteRound(round: Round)
}
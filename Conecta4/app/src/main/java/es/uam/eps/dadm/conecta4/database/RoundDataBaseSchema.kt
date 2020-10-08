package es.uam.eps.dadm.conecta4.database


/**Aqui se define el esquema de la base de datos local*/
object RoundDataBaseSchema {
    object UserTable{
        val NAME = "users"
        object Cols {
            val PLAYERUUID = "playeruuid1"
            val PLAYERNAME = "playername"
            val PLAYERPASSWORD = "playerpassword"
        }
    }
    object RoundTable{
        val NAME = "rounds"
        object Cols{
            val PLAYERUUID = "playeruuid2"
            val ROUNDUUID = "rounduuid"
            val DATE = "date"
            val TITLE = "title"
            val SIZE = "size"
            val BOARD = "board"
        }
    }
}
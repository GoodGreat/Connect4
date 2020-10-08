package es.uam.eps.dadm.conecta4.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import es.uam.eps.dadm.conecta4.modelo.Round
import es.uam.eps.dadm.conecta4.modelo.RoundRepository
import es.uam.eps.dadm.conecta4.database.RoundDataBaseSchema.UserTable
import es.uam.eps.dadm.conecta4.database.RoundDataBaseSchema.RoundTable
import java.sql.SQLException
import java.util.*

class ERDataBase(context: Context) : RoundRepository {

    private val DEBUG_TAG = "DEBUG"
    private val helper: DatabaseHelper
    private var db: SQLiteDatabase? = null

    /**Al iniciarse la clase se crea un DatabaseHelper que se define en esta misma clase*/
    init{
        helper = DatabaseHelper(context)
    }

    private class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
        /**Cuando se crea el DatabaseHelper se crean todas las tablas necesarias*/
        override fun onCreate(db: SQLiteDatabase) {
            createTable(db)
        }
        /**Borra todas las tablas y las vuelve a crear*/
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS "+UserTable.NAME)
            db.execSQL("DROP TABLE IF EXISTS "+RoundTable.NAME)
            createTable(db)
        }
        /**Metodo que crea todas las tablas*/
        private fun createTable(db: SQLiteDatabase){
            val str1 = ("CREATE TABLE " + UserTable.NAME + " ("
                    + "_id integer primary key autoincrement, "
                    + UserTable.Cols.PLAYERUUID + " TEXT UNIQUE, "
                    + UserTable.Cols.PLAYERNAME + " TEXT UNIQUE, "
                    + UserTable.Cols.PLAYERPASSWORD + " TEXT);")
            val str2 = ("CREATE TABLE " + RoundTable.NAME + " ("
                    + "_id integer primary key autoincrement, "
                    + RoundTable.Cols.ROUNDUUID + " TEXT UNIQUE, "
                    + RoundTable.Cols.PLAYERUUID + " TEXT REFERENCES "
                    + UserTable.Cols.PLAYERUUID + ", "
                    + RoundTable.Cols.DATE + " TEXT, "
                    + RoundTable.Cols.TITLE + " TEXT, "
                    + RoundTable.Cols.SIZE + " TEXT, "
                    + RoundTable.Cols.BOARD + " TEXT);")
            try {
                db.execSQL(str1)
                db.execSQL(str2)
            } catch (e: SQLException){
                e.printStackTrace()
            }
        }
    }

    /**Funcion que crea el objeto para escribir en la base de datos*/
    @Throws(SQLException::class)
    override fun open(){
        db = helper.writableDatabase
    }

    /**Metodo que cierra la conexion con la base de datos*/
    override fun close() {
        db?.close()
    }

    /**EL RESTO DE FUNCIONES COMENTADAS EN LA INTERFAZ ROUNDREPOSITORY*/

    override fun login(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {
        Log.d(DEBUG_TAG, "Login $playername")
        val cursor = db!!.query(UserTable.NAME,
                                arrayOf(UserTable.Cols.PLAYERUUID),
                                UserTable.Cols.PLAYERNAME + " = ? AND "
                                + UserTable.Cols.PLAYERPASSWORD + " = ?",
                                arrayOf(playername, password),
                                null, null, null)
        val count = cursor.count
        val uuid = if(count == 1 && cursor.moveToFirst()) cursor.getString(0) else ""
        cursor.close()
        if(count == 1)
            callback.onLogin(uuid)
        else
            callback.onError("Username or password incorrect.")
    }

    override fun register(playername: String, password: String, callback: RoundRepository.LoginRegisterCallback) {
        val values = ContentValues()
        val uuid = UUID.randomUUID().toString()
        values.put(UserTable.Cols.PLAYERUUID, uuid)
        values.put(UserTable.Cols.PLAYERNAME, playername)
        values.put(UserTable.Cols.PLAYERPASSWORD, password)
        val id = db!!.insert(UserTable.NAME, null, values)
        if(id<0)
            callback.onError("Error inserting new player named $playername")
        else
            callback.onLogin(uuid)
    }

    override fun addRound(round: Round, callback: RoundRepository.BooleanCallback) {
        val values = getContentValues(round)
        val id = db!!.insert(RoundTable.NAME, null, values)
        callback.onResponse(id >= 0)
    }

    override fun deleteRound(round: Round) {
        db!!.delete(RoundTable.NAME, RoundTable.Cols.TITLE +"= '"+round.title+"'", null)
    }

    private fun getContentValues(round: Round): ContentValues{
        val values = ContentValues()
        values.put(RoundTable.Cols.PLAYERUUID, round.secondPlayerUUID)
        values.put(RoundTable.Cols.ROUNDUUID, round.id)
        values.put(RoundTable.Cols.DATE, round.date)
        values.put(RoundTable.Cols.TITLE, round.title)
        values.put(RoundTable.Cols.SIZE, round.size)
        values.put(RoundTable.Cols.BOARD, round.board.tableroToString())
        return values
    }

    override fun updateRound(round: Round, callback: RoundRepository.BooleanCallback) {
        val values = getContentValues(round)
        val id = db!!.update(RoundTable.NAME, values, RoundTable.Cols.ROUNDUUID + " = ?", arrayOf(round.id))
        callback.onResponse(id>=0)
    }

    override fun getRounds(
        playerUuid: String,
        orderByField: String,
        group: String,
        callback: RoundRepository.RoundsCallback
    ) {
        val rounds = ArrayList<Round>()
        val cursor = queryRounds()

        cursor!!.moveToFirst()
        while(!cursor.isAfterLast){
            val round = cursor.round
            if(round.secondPlayerUUID.equals(playerUuid))
                rounds.add(round)
            cursor.moveToNext()
        }
        cursor.close()
        if(cursor.count > 0)
            callback.onResponse(rounds)
        else
            callback.onError("No rounds found in database")
    }

    private fun queryRounds(): RoundCursorWrapper?{
        val sql = "SELECT " + UserTable.Cols.PLAYERNAME + ", " +
                UserTable.Cols.PLAYERUUID + ", " +
                RoundTable.Cols.ROUNDUUID + ", " +
                RoundTable.Cols.DATE + ", " +
                RoundTable.Cols.TITLE + ", " +
                RoundTable.Cols.SIZE + ", " +
                RoundTable.Cols.BOARD + " " +
                "FROM " + UserTable.NAME + " AS p, "+
                RoundTable.NAME + " AS r " +
                "WHERE " + "p." + UserTable.Cols.PLAYERUUID + "=" +
                "r." + RoundTable.Cols.PLAYERUUID + ";"
        val cursor = db!!.rawQuery(sql, null)
        return RoundCursorWrapper(cursor)
    }

    companion object {
        private val DATABASE_NAME = "er.db"
        private val DATABASE_VERSION = 1
    }
}
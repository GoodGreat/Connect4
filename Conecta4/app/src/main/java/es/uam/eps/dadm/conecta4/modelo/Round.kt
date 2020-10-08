package es.uam.eps.dadm.conecta4.modelo

import TableroCuatroEnRaya
import org.json.JSONObject
import java.util.*

class Round {

    var board: TableroCuatroEnRaya
    var title: String
    var date: String
    var id: String
    var size: Int
    lateinit var firstPlayerName: String
    lateinit var firstPlayerUUID: String
    lateinit var secondPlayerName: String
    lateinit var secondPlayerUUID: String

    init {
        id = UUID.randomUUID().toString()
        title = "ROUND ${id.toString().substring(19,23).toUpperCase()}"
        date = Date().toString()
        board = TableroCuatroEnRaya()
        size = 56
    }

    fun toJSONString(): String{
        val json = JSONObject()
        json.put("id", id)
        json.put("title", title)
        json.put("date", date)
        json.put("size", size)
        json.put("boardString", board.tableroToString())
        json.put("firstPlayerName", firstPlayerName)
        json.put("firstPlayerUUID", firstPlayerUUID)
        json.put("secondPlayerName", secondPlayerName)
        json.put("secondPlayerUUID", secondPlayerUUID)
        return json.toString()
    }

    companion object {
        fun fromJSONString(string: String): Round{
            val jsonObject = JSONObject(string)
            val round = Round()
            round.id = jsonObject.get("id") as String
            round.title = jsonObject.get("title") as String
            round.date = jsonObject.get("date") as String
            round.board.stringToTablero(jsonObject.get("boardString") as String)
            round.firstPlayerName = jsonObject.get("firstPlayerName") as String
            round.firstPlayerUUID = jsonObject.get("firstPlayerUUID") as String
            round.secondPlayerName = jsonObject.get("secondPlayerName") as String
            round.secondPlayerUUID = jsonObject.get("secondPlayerUUID") as String
            return round
        }
    }

}
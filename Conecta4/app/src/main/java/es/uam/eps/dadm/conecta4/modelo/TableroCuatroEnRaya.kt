import es.uam.eps.multij.Movimiento
import es.uam.eps.multij.Tablero
import java.io.File
import java.util.ArrayList
import java.io.InputStream

class TableroCuatroEnRaya: Tablero() {

    val rows = 7
    val columns = 8
    //var caracteresTablero = Array(rows) { IntArray(columns) }
    val TAMANIO_ELEMENTOS_FICHERO = 59
    var uMovimiento = -1

    init{
        setEstado(EN_CURSO)
        setTurno(0)
        /*
        for(i in caracteresTablero.indices){
            for(j in caracteresTablero[i].indices){
                caracteresTablero[i][j] = -1
            }
        }
        */
    }

    var caracteresTablero = mutableListOf(mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1),
        mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1),
        mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1),
        mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1),
        mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1),
        mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1),
        mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1))



    /*Metodo que imprime la informacion necesaria de un tablero de cuatro en raya*/
    override fun toString(): String {
        return "Tablero con $rows filas y $columns columnas"
    }
    /*Metodo que devuelve una lista de Movimientos validos*/
    override fun movimientosValidos(): ArrayList<Movimiento> {
        var listaValidos = ArrayList<Movimiento>()
        for(columna in 0..columns-1){
            var mov = MovimientoCuatroEnRaya(columna)
            if(esValido(mov)){ /*Movimiento valido, lo creamos y lo añadimos a la lista de movimientos validos*/
                listaValidos.add(mov)
            }
        }
        return listaValidos
    }
    /*Funcion que interpreta un string determinado y crea un tablero con la informacion proporcionada*/
    override fun stringToTablero(cadena: String?) {
        var count : Int = 0
        if(cadena != null){
            val tablero_parseado = cadena.split(' ')
            if(tablero_parseado.size != TAMANIO_ELEMENTOS_FICHERO){
                /*Error leyendo archivo*/
                return
            }

            for (i in 0..rows-1){

                for (j in 0..columns-1){

                    caracteresTablero[i][j] = tablero_parseado[count].toInt()
                    count++
                }
            }

            setTurno(tablero_parseado[count].toInt())
            setEstado(tablero_parseado[count+1].toInt())
            uMovimiento = tablero_parseado[count+2].toInt()
            //ultimoMovimiento = MovimientoCuatroEnRaya(tablero_parseado[count+2].toInt())
        }

    }
    /*Metodo que realiza un Movimiento en el tablero, aqui creo que se cambia de turno con el metodo de tablero*/
    override fun mueve(m: Movimiento?) {
        if(m is MovimientoCuatroEnRaya){
            if(esValido(m)){
                //this.ultimoMovimiento = m
                uMovimiento = m.columna
                if(caracteresTablero[rows-1][m.columna]==-1){ /*La ficha cae hasta el fondo*/
                    caracteresTablero[rows-1][m.columna]=turno

                }else{ /*Hay mas fichas en la columna*/
                    var fila = this.rows-2
                    var fichaColocada = false
                    while(fila>=0 && fichaColocada == false){
                        if(caracteresTablero[fila][m.columna]==-1){
                            caracteresTablero[fila][m.columna]=turno
                            fichaColocada = true
                        }
                        fila--
                    }
                }
                comprobarEstadoPartida()
            }
        }
    }
    /*Metodo que evalua si un Movimiento es valido*/
    override fun esValido(m: Movimiento?): Boolean {
        if(estado == FINALIZADA)
            return false
        if(m is MovimientoCuatroEnRaya){
            if(caracteresTablero[0][m.columna]==-1)
                return true
        }else{
            return false
        }
        return false
    }
    /*Metodo que transforma un tablero a un string para guardar la partida en un fichero*/
    override fun tableroToString(): String {

        var cadena : String = ""

        for (i in caracteresTablero.indices){
            for (j in caracteresTablero[i].indices){
                cadena += caracteresTablero[i][j]
                cadena += " "
            }
        }

        cadena += turno
        cadena += " "
        cadena += estado
        cadena += " "
        if(uMovimiento == null)
            cadena += -1
        else
            cadena += uMovimiento.toString()

        return cadena
    }

    fun simpleString(): String{
        var cadena = ""
        for (i in caracteresTablero.indices){
            for (j in caracteresTablero[i].indices){
                if(caracteresTablero[i][j]==-1)
                    cadena += "-- "
                else if(caracteresTablero[i][j]==0)
                    cadena += "0 "
                else
                    cadena += "X "
            }
            cadena += "\n"
        }

        return cadena

    }

    fun setEstado(estado: Int){
        this.estado = estado
    }
    fun setTurno(turno: Int){
        this.turno = turno
    }
/*
    fun getCaracterestablero(): Array<IntArray>{
        return caracteresTablero
    }
*/

    private fun comprobarEstadoPartida(){
        /*Primero se comprueba que no haya ningun 4 en raya, cuando no haya ninguno se mira si se pueden hacer mas movimientos.
        Si no hay cuatro en raya y se pueden hacer mas movimientos se cambia de turno*/
        var cuatroHorizontal = false
        var cuatroVertical = false
        var cuatroDiagonalDerecha = false
        var cuatroDiagonalIzquierda = false
        for(fila in 0..rows-1){
            for(columna in 0..columns-1){
                if(columna<=columns-4){
                    cuatroHorizontal = comprobarHorizontal(fila, columna)
                }
                if(fila<=rows-4){
                    cuatroVertical = comprobarVertical(fila, columna)
                }
                if(columna<=columns-4 && fila<=rows-4){
                    cuatroDiagonalDerecha = comprobarDiagonalDerecha(fila, columna)
                }
                if(columna>=3 && fila<=rows-4){
                    cuatroDiagonalIzquierda = comprobarDiagonalIzquierda(fila, columna)
                }
                if(cuatroHorizontal || cuatroDiagonalDerecha || cuatroDiagonalIzquierda || cuatroVertical){
                    setEstado(FINALIZADA)
                    return
                }

            }
        }


        if(movimientosValidos().isEmpty()){
            setEstado(TABLAS)
            return
        }
        cambiaTurno()
        return

    }

    private fun comprobarVertical(fila: Int, columna: Int): Boolean {
        var ficha1 = caracteresTablero[fila][columna]
        var ficha2 = caracteresTablero[fila+1][columna]
        var ficha3 = caracteresTablero[fila+2][columna]
        var ficha4 = caracteresTablero[fila+3][columna]
        return (ficha1.equals(ficha2) && ficha1.equals(ficha3) && ficha1.equals(ficha4) && !ficha1.equals(-1))
    }

    private fun comprobarHorizontal(fila: Int, columna: Int): Boolean {
        var ficha1 = caracteresTablero[fila][columna]
        var ficha2 = caracteresTablero[fila][columna+1]
        var ficha3 = caracteresTablero[fila][columna+2]
        var ficha4 = caracteresTablero[fila][columna+3]
        return (ficha1.equals(ficha2) && ficha1.equals(ficha3) && ficha1.equals(ficha4) && !ficha1.equals(-1))
    }

    private fun comprobarDiagonalDerecha(fila: Int, columna: Int): Boolean {
        var ficha1 = caracteresTablero[fila][columna]
        var ficha2 = caracteresTablero[fila+1][columna+1]
        var ficha3 = caracteresTablero[fila+2][columna+2]
        var ficha4 = caracteresTablero[fila+3][columna+3]
        return (ficha1.equals(ficha2) && ficha1.equals(ficha3) && ficha1.equals(ficha4) && !ficha1.equals(-1))
    }

    private fun comprobarDiagonalIzquierda(fila: Int, columna: Int): Boolean {
        var ficha1 = caracteresTablero[fila][columna]
        var ficha2 = caracteresTablero[fila+1][columna-1]
        var ficha3 = caracteresTablero[fila+2][columna-2]
        var ficha4 = caracteresTablero[fila+3][columna-3]
        return (ficha1.equals(ficha2) && ficha1.equals(ficha3) && ficha1.equals(ficha4) && !ficha1.equals(-1))
    }
}
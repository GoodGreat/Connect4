import es.uam.eps.multij.Movimiento

class MovimientoCuatroEnRaya(var columna: Int): Movimiento() {

    /** compara esta jugada con otra, a fin de comprobar si son iguales
     * @param o otro Movimiento
     * @return el valor de la comparacion (true o false)
     */
    override fun equals(other: Any?): Boolean {
        if(other is MovimientoCuatroEnRaya){
            if(other.columna == this.columna){
                return true
            }
        }else{
            return false
        }
        return false
    }
    /**
     * genera una cadena que describe este movimiento
     * @return una cadena de la forma "Ficha a columna 4"
     */
    override fun toString(): String {
        return "$columna"
    }


}
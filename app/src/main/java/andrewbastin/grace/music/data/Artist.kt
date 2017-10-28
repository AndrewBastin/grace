package andrewbastin.grace.music.data

class Artist(val id: Long, val name: String) : Comparable<Artist> {

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Artist) = name.compareTo(other.name)

    override fun hashCode() = id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Artist

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }
}
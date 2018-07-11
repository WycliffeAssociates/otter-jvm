package persistance.mapping

interface Mapper<E,D> {
    fun mapFromEntity(type: D): E
    fun mapToEntity(type: E): D
}
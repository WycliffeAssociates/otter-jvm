package org.wycliffeassociates.otter.jvm.persistence.repositories

class InMemoryDao<T: Any> {
    private var nextId = 1
     val rows = mutableMapOf<Int, T>()

    fun insert(entity: T): Int {
        rows[nextId] = entity
        if (entity::class.java.declaredFields.map { it.name }.contains("id")) {
            val field = entity::class.java.getDeclaredField("id")
            field.isAccessible = true
            field.setInt(entity, nextId)
            field.isAccessible = false
        }
        return nextId++
    }

    fun fetchAll(): List<T> {
        return rows.values.toList()
    }

    fun <V: Any> fetchByProperty(propName: String, value: V): List<T> {
        if (rows.values.isNotEmpty()) {
            val field = rows.values.first()::class.java.getDeclaredField(propName)
            field.isAccessible = true
            val result = rows.values.filter {
                field.get(it) as V == value
            }
            field.isAccessible = false
            return result
        }
        return emptyList()
    }

    fun fetchById(id: Int): T? {
        return rows[id]
    }

    fun update(entity: T, id: Int) {
        rows[id] = entity
    }

    fun delete(id: Int) {
        rows.remove(id)
    }
}
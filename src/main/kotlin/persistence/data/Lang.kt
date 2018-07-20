package persistence.data

class Lang(
        val pk: Int, //id
        val lc: String, //slug
        val ln: String, //name
        val gw: Boolean, //isGateway
        val ang: String //anglicizedName
)
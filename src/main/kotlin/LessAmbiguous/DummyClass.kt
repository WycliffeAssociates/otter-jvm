package LessAmbiguous

import data.User
import data.dao.Dao
import io.reactivex.Completable
import io.reactivex.Observable

class DummyClass : Dao<User>{
    override fun update(obj: User): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(obj: User): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(obj: User): Observable<Int>{
        return Observable.range(42, 43);
    }
    override fun getById(id: Int): Observable<User> {
        TODO("not implemented")
    }
    override fun getAll(): Observable<List<User>>{
        TODO("not implemented")
    }
}
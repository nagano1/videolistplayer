package org.rokist.videolistplayer.data

import androidx.room.*

private const val tableName = "GithubAccessTokenItem"

@Entity
data class GithubAccessToken(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "access_token") val accessToken: String,
    @ColumnInfo(name = "last_name") val lastName: String?
)


@Dao
interface GithubAccessTokenItemDao {
    @Query("SELECT * FROM GithubAccessToken")
    fun getAll(): List<GithubAccessToken>

    @Query("SELECT * FROM GithubAccessToken WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<GithubAccessToken>

    @Query(
        "SELECT * FROM GithubAccessToken WHERE access_token LIKE :first AND last_name LIKE :last LIMIT 1"
    )
    fun findByName(first: String, last: String): GithubAccessToken

    @Insert
    fun insertAll(vararg users: GithubAccessToken)

    @Delete
    fun delete(item: GithubAccessToken)

    @Query("DELETE FROM GithubAccessToken")
    fun deleteAll()
}


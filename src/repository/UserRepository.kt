package cz.skywall.microfunspace.repository

import cz.skywall.microfunspace.model.User


class UserRepository {

    private val users = listOf(
        User("61d7b344-07a6-4227-bbfb-48f2f3b73df0", "alice", 10),
        User("ff6f6e75-f3ff-451d-9e23-1955f5963c12", "bob", 10),
        User("f86c6c25-8a79-4146-a735-78a61ba9abd0", "cyril", 5),
        User("b43f90b9-d8a1-4b3a-9a29-d359c7338e44", "don", 5)
    )

    suspend fun getAll(): List<User> {
        return users
    }

    fun getByName(name: String): User {
        return users.first { it.name == name }
    }
}
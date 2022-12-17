package com.example.androidclass.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.androidclass.data.dao.UserDao
import com.example.androidclass.data.util.DefaultSecurityUtilsImpl
import com.example.androidclass.data.util.SecurityUtils
import com.example.androidclass.domain.exceptions.AuthException
import com.example.androidclass.domain.exceptions.PasswordMismatchException
import com.example.androidclass.domain.exceptions.StorageException
import com.example.androidclass.domain.exceptions.UserAlreadyExistsException
import com.example.androidclass.domain.model.SignUpData
import com.example.androidclass.domain.model.User
import com.example.androidclass.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthUserRepositoryImpl(private val dao: UserDao) : AuthRepository {
    private val securityUtils: SecurityUtils = DefaultSecurityUtilsImpl()

    @kotlin.jvm.Throws(UserAlreadyExistsException::class)
    override suspend fun signUp(signUpData: SignUpData): Long {
        try {
            val salt = securityUtils.generateSalt()
            val hash = securityUtils.passwordToHash(signUpData.password.toCharArray(), salt)
            signUpData.password.toCharArray().fill('*')
            return dao.insert(
                User(
                    0,
                    signUpData.username,
                    securityUtils.bytesToString(hash),
                    securityUtils.bytesToString(salt)
                )
            )
        } catch (e: SQLiteConstraintException) {
            throw UserAlreadyExistsException().apply { initCause(e) }
        }
    }

    @kotlin.jvm.Throws(AuthException::class, PasswordMismatchException::class)
    override suspend fun login(username: String, password: String): User {
        val user = dao.getUserByUsername(username) ?: throw AuthException()
        val salt = securityUtils.stringToBytes(user.salt)
        val _password = securityUtils.passwordToHash(password.toCharArray(), salt)
        if (user.password != securityUtils.bytesToString(_password)) throw PasswordMismatchException()
        return user
    }

    @kotlin.jvm.Throws(StorageException::class)
    override suspend fun update(user: User) = withContext(Dispatchers.IO) {
        try {
            dao.update(user)
        } catch (e: SQLiteConstraintException) {
            throw StorageException().apply { initCause(e) }
        }
    }

}

package com.proyek.infrastructures.user.auth.interfaces

interface IUserRepository {
    fun Find()
    fun Save()
    fun Login()
    fun Logout()
}
// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.RoleDao
import com.gma.tsunjo.school.domain.models.Role

class RoleRepository(private val roleDao: RoleDao) {

    fun getAllRoles(): List<Role> = roleDao.findAll()

    fun getRoleById(id: Long): Role? = roleDao.findById(id)

    fun getRoleByName(name: String): Role? = roleDao.findByName(name)

    fun createRole(name: String): Role? = roleDao.insert(name)
}

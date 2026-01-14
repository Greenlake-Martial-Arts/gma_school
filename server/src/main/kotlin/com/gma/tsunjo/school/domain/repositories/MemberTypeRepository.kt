// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MemberTypeDao
import com.gma.tsunjo.school.domain.models.MemberType

class MemberTypeRepository(
    private val memberTypeDao: MemberTypeDao
) {

    fun getAllMemberTypes(): List<MemberType> = memberTypeDao.findAll()

    fun getMemberTypeById(id: Long): MemberType? = memberTypeDao.findById(id)

    fun getMemberTypeByName(name: String): MemberType? = memberTypeDao.findByName(name)
}

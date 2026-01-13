// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MoveCategoryDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.MoveCategory

class MoveCategoryRepository(
    private val moveCategoryDao: MoveCategoryDao
) {

    fun getAllMoveCategories(): List<MoveCategory> = moveCategoryDao.findAll()

    fun getMoveCategoryById(id: Long): MoveCategory? = moveCategoryDao.findById(id)

    fun createMoveCategory(name: String, description: String?): Result<MoveCategory> {
        return try {
            val category = moveCategoryDao.create(name, description)
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Database error during move category creation", e))
        }
    }

    fun updateMoveCategory(id: Long, name: String, description: String?): Boolean {
        return moveCategoryDao.update(id, name, description)
    }

    fun deleteMoveCategory(id: Long): Boolean {
        return moveCategoryDao.delete(id)
    }
}

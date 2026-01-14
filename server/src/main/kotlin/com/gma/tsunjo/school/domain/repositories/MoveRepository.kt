// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.MoveDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Move

class MoveRepository(
    private val moveDao: MoveDao
) {

    fun getAllMoves(): List<Move> = moveDao.findAll()

    fun getMoveById(id: Long): Move? = moveDao.findById(id)

    fun getMovesByCategory(categoryId: Long): List<Move> = moveDao.findByCategory(categoryId)

    fun createMove(name: String, description: String?): Result<Move> {
        return try {
            val move = moveDao.create(name, description)
            Result.success(move)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Database error during move creation", e))
        }
    }

    fun updateMove(id: Long, name: String, description: String?, categoryId: Long): Boolean {
        return moveDao.update(id, name, description, categoryId)
    }

    fun deleteMove(id: Long): Boolean {
        return moveDao.delete(id)
    }
}

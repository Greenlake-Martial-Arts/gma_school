// © 2025-2026 Hector Torres - Greenlake Martial Arts

package com.gma.tsunjo.school.domain.repositories

import com.gma.school.database.data.dao.LevelDao
import com.gma.tsunjo.school.domain.exceptions.AppException
import com.gma.tsunjo.school.domain.models.Level

class LevelRepository(
    private val levelDao: LevelDao
) {

    fun getAllLevels(): List<Level> = levelDao.findAll()

    fun getLevelById(id: Long): Level? = levelDao.findById(id)

    fun createLevel(code: String, displayName: String, orderSeq: Int, description: String?): Result<Level> {
        return try {
            val level = levelDao.create(code, displayName, orderSeq, description)
            Result.success(level)
        } catch (e: Exception) {
            Result.failure(AppException.DatabaseError("Database error during level creation", e))
        }
    }

    fun updateLevel(id: Long, code: String, displayName: String, orderSeq: Int, description: String?): Boolean {
        return levelDao.update(id, code, displayName, orderSeq, description)
    }

    fun deleteLevel(id: Long): Boolean {
        return levelDao.delete(id)
    }
}

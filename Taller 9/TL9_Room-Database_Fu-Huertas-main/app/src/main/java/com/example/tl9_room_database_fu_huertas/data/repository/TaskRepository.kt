package com.example.tl9_room_database_fu_huertas.data.repository

import com.example.tl9_room_database_fu_huertas.data.local.Task
import com.example.tl9_room_database_fu_huertas.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)
}
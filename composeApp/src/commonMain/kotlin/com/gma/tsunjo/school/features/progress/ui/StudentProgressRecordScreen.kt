// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.features.progress.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gma.tsunjo.school.features.students.ui.viewmodel.StudentsViewModel
import com.gma.tsunjo.school.theme.BlueSash
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.ui.components.SearchableTopBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

data class ProgressRecord(
    val levelRequirementId: Long,
    val progressId: Long?,
    val technique: String,
    val status: TechniqueStatus,
    val notes: String = ""
)

enum class TechniqueStatus {
    NOT_STARTED,
    IN_PROGRESS,
    MASTERED;
    
    fun next(): TechniqueStatus = when (this) {
        NOT_STARTED -> IN_PROGRESS
        IN_PROGRESS -> MASTERED
        MASTERED -> NOT_STARTED
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProgressRecordScreen(
    studentId: String,
    studentName: String,
    studentRank: String,
    studentRankColor: String,
    onNavigateBack: () -> Unit,
    viewModel: StudentsViewModel = koinInject()
) {
    val uiState by viewModel.studentProgressDetailUiState.collectAsState()
    
    LaunchedEffect(studentId) {
        viewModel.loadStudentProgress(studentId.toLong(), studentName)
    }
    
    val rankColor = com.gma.tsunjo.school.theme.getLevelColor(studentRankColor)
    
    // Map API data to UI model
    val progressRecords = when (uiState) {
        is com.gma.tsunjo.school.features.students.ui.viewmodel.StudentProgressDetailUiState.Success -> {
            (uiState as com.gma.tsunjo.school.features.students.ui.viewmodel.StudentProgressDetailUiState.Success).detail.requirements.map { req ->
                ProgressRecord(
                    levelRequirementId = req.levelRequirementId,
                    progressId = req.progressId,
                    technique = req.moveName,
                    status = when (req.status) {
                        com.gma.tsunjo.school.domain.models.ProgressState.NOT_STARTED -> TechniqueStatus.NOT_STARTED
                        com.gma.tsunjo.school.domain.models.ProgressState.IN_PROGRESS -> TechniqueStatus.IN_PROGRESS
                        com.gma.tsunjo.school.domain.models.ProgressState.PASSED -> TechniqueStatus.MASTERED
                    },
                    notes = req.notes ?: ""
                )
            }
        }
        else -> emptyList()
    }
    
    var mutableProgressRecords by remember(progressRecords) {
        mutableStateOf(progressRecords)
    }
    
    StudentProgressRecordView(
        studentName = studentName,
        studentRank = studentRank,
        studentRankColor = rankColor,
        progressRecords = mutableProgressRecords,
        onNavigateBack = onNavigateBack,
        onToggleTechnique = { technique ->
            mutableProgressRecords = mutableProgressRecords.map {
                if (it.technique == technique) {
                    val currentStatus = it.status
                    val newStatus = if (currentStatus == TechniqueStatus.MASTERED) {
                        TechniqueStatus.NOT_STARTED
                    } else {
                        currentStatus.next()
                    }
                    if (newStatus != TechniqueStatus.IN_PROGRESS) {
                        it.copy(status = newStatus, notes = "")
                    } else {
                        it.copy(status = newStatus)
                    }
                } else it
            }
        },
        onNotesChange = { technique, newNotes ->
            mutableProgressRecords = mutableProgressRecords.map {
                if (it.technique == technique) it.copy(notes = newNotes) else it
            }
        },
        onSaveProgress = {
            mutableProgressRecords.forEach { record ->
                val apiStatus = when (record.status) {
                    TechniqueStatus.NOT_STARTED -> com.gma.tsunjo.school.domain.models.ProgressState.NOT_STARTED
                    TechniqueStatus.IN_PROGRESS -> com.gma.tsunjo.school.domain.models.ProgressState.IN_PROGRESS
                    TechniqueStatus.MASTERED -> com.gma.tsunjo.school.domain.models.ProgressState.PASSED
                }
                
                if (record.progressId == null) {
                    if (record.status != TechniqueStatus.NOT_STARTED) {
                        viewModel.createStudentProgress(
                            studentId = studentId.toLong(),
                            levelRequirementId = record.levelRequirementId,
                            status = apiStatus,
                            notes = record.notes.ifBlank { null }
                        )
                    }
                } else {
                    viewModel.updateStudentProgress(
                        studentId = studentId.toLong(),
                        progressId = record.progressId,
                        status = apiStatus,
                        notes = record.notes.ifBlank { null }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProgressRecordView(
    studentName: String,
    studentRank: String,
    studentRankColor: Color,
    progressRecords: List<ProgressRecord>,
    onNavigateBack: () -> Unit,
    onToggleTechnique: (String) -> Unit,
    onNotesChange: (String, String) -> Unit,
    onSaveProgress: () -> Unit
) {
    var showBackDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    val masteredCount = progressRecords.count { it.status == TechniqueStatus.MASTERED }
    val totalCount = progressRecords.size
    val progressPercentage = if (totalCount > 0) ((masteredCount.toFloat() / totalCount) * 100).toInt() else 0

    Scaffold(
        topBar = {
            SearchableTopBar(
                title = "Progress Record",
                onNavigateBack = { showBackDialog = true },
                actionIcon = Icons.Default.Check,
                onActionClick = { showSaveDialog = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {
            // Student Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Avatar + Info
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        .padding(3.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = studentName.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = studentName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                studentRankColor,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = studentRank.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (studentRank.contains("White", ignoreCase = true)) Color.Black else Color.White
                                        )
                                    }
                                }
                            }

                            // Right: Circular Progress
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(70.dp)
                            ) {
                                androidx.compose.foundation.Canvas(
                                    modifier = Modifier.size(70.dp)
                                ) {
                                    val strokeWidth = 7.dp.toPx()
                                    drawCircle(
                                        color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.3f),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
                                    )
                                    drawArc(
                                        color = androidx.compose.ui.graphics.Color(0xFFE11028),
                                        startAngle = -90f,
                                        sweepAngle = (progressPercentage / 100f) * 360f,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            strokeWidth,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    )
                                }
                                Text(
                                    text = "$progressPercentage%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Requirements Header
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REQUIREMENTS",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$masteredCount / $totalCount MASTERED",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        // Legend
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )
                                Text(
                                    text = com.gma.tsunjo.school.resources.Strings.PROGRESS_NOT_STARTED,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2196F3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    DrawDotsInProgress()
                                }
                                Text(
                                    text = "In Progress",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50)),
                                    contentAlignment = Alignment.TopCenter,

                                ) {
                                    Canvas(modifier = Modifier.size(14.dp)) {
                                        val path = androidx.compose.ui.graphics.Path().apply {
                                            moveTo(size.width * 0.2f, size.height * 0.5f)
                                            lineTo(size.width * 0.4f, size.height * 0.7f)
                                            lineTo(size.width * 0.8f, size.height * 0.3f)
                                        }
                                        drawPath(
                                            path = path,
                                            color = androidx.compose.ui.graphics.Color.White,
                                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                                        )
                                    }
                                }
                                Text(
                                    text = com.gma.tsunjo.school.resources.Strings.PROGRESS_MASTERED,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Technique Items
                items(progressRecords) { record ->
                    TechniqueItem(
                        technique = record.technique,
                        status = record.status,
                        notes = record.notes,
                        onTap = { onToggleTechnique(record.technique) },
                        onLongPress = { onToggleTechnique(record.technique) },
                        onNotesChange = { newNotes -> onNotesChange(record.technique, newNotes) }
                    )
                }

                // Save Button
                item {
                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            "SAVE PROGRESS",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
    }

    // Back Confirmation Dialog
    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text("Cancel Changes?") },
            text = { Text("Are you sure you want to go back? Any unsaved changes will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Yes, Cancel", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackDialog = false }) {
                    Text("Continue Editing")
                }
            }
        )
    }

    // Save Confirmation Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Progress?") },
            text = { Text("Do you want to save the progress for this student?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                        onSaveProgress()
                        onNavigateBack()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TechniqueItem(
    technique: String,
    status: TechniqueStatus,
    notes: String,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onNotesChange: (String) -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showMasteredDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (status == TechniqueStatus.IN_PROGRESS) {
                    Modifier.drawBehind {
                        drawLine(
                            color = androidx.compose.ui.graphics.Color(0xFF2196F3),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(0f, size.height),
                            strokeWidth = 4.dp.toPx()
                        )
                    }
                } else {
                    Modifier
                }
            )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = when (status) {
                    TechniqueStatus.MASTERED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    TechniqueStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                    TechniqueStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            when (status) {
                                TechniqueStatus.NOT_STARTED -> onTap()
                                TechniqueStatus.IN_PROGRESS -> showMasteredDialog = true
                                TechniqueStatus.MASTERED -> {} // Do nothing
                            }
                    },
                    onLongClick = {
                        if (status == TechniqueStatus.MASTERED) {
                            showResetDialog = true
                        }
                    }
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = technique,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 2
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when (status) {
                                TechniqueStatus.MASTERED -> Color(0xFF4CAF50)
                                TechniqueStatus.IN_PROGRESS -> Color(0xFF2196F3)
                                TechniqueStatus.NOT_STARTED -> Color.Gray
                            }
                        ),
                    contentAlignment = Alignment.Center
            ) {
                when (status) {
                    TechniqueStatus.MASTERED -> {
                        // Checkmark using Canvas
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.5f)
                                lineTo(size.width * 0.4f, size.height * 0.7f)
                                lineTo(size.width * 0.8f, size.height * 0.3f)
                            }
                            drawPath(
                                path = path,
                                color = androidx.compose.ui.graphics.Color.White,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                            )
                        }
                    }
                    TechniqueStatus.IN_PROGRESS -> {
                        // Three dots using Canvas
                        DrawDotsInProgress()
                    }
                    TechniqueStatus.NOT_STARTED -> {
                        // Empty circle
                    }
                }
            }
            }
        }
    }
        
        // Notes field for IN_PROGRESS status
        androidx.compose.animation.AnimatedVisibility(
            visible = status == TechniqueStatus.IN_PROGRESS,
            enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 12.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Notes icon using Canvas
                            Canvas(modifier = Modifier.size(14.dp)) {
                                // Draw paper outline
                                val rect = androidx.compose.ui.geometry.Rect(
                                    left = size.width * 0.1f,
                                    top = 0f,
                                    right = size.width * 0.9f,
                                    bottom = size.height * 0.9f
                                )
                                drawRoundRect(
                                    color = androidx.compose.ui.graphics.Color.Gray,
                                    topLeft = rect.topLeft,
                                    size = rect.size,
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                                )
                                // Draw lines
                                val lineSpacing = size.height * 0.25f
                                for (i in 1..3) {
                                    drawLine(
                                        color = androidx.compose.ui.graphics.Color.Gray,
                                        start = androidx.compose.ui.geometry.Offset(size.width * 0.25f, lineSpacing * i),
                                        end = androidx.compose.ui.geometry.Offset(size.width * 0.75f, lineSpacing * i),
                                        strokeWidth = 1f
                                    )
                                }
                            }
                            Text(
                                text = "Notes:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${notes.length}/150",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (notes.length >= 150) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    androidx.compose.material3.TextField(
                        value = notes,
                        onValueChange = { newValue ->
                            val lineCount = newValue.count { it == '\n' }
                            if (newValue.length <= 150 && lineCount < 3) {
                                onNotesChange(newValue)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Add notes about what needs improvement...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        ),
                        shape = MaterialTheme.shapes.small,
                        minLines = 1,
                        maxLines = 3
                    )
                }
            }
        }
    }
    
    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?") },
            text = { Text("Reset \"$technique\" back to Not Started?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onLongPress()
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Mastered confirmation dialog
    if (showMasteredDialog) {
        AlertDialog(
            onDismissRequest = { showMasteredDialog = false },
            title = { Text("Mark as Mastered?") },
            text = { Text("Confirm that \"$technique\" has been mastered?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMasteredDialog = false
                        onTap()
                    }
                ) {
                    Text("Confirm", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMasteredDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DrawDotsInProgress() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val dotRadius = 2f
        val spacing = size.width / 4
        drawCircle(
            color = Color.White,
            radius = dotRadius,
            center = Offset(spacing, size.height / 2)
        )
        drawCircle(
            color = Color.White,
            radius = dotRadius,
            center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        )
        drawCircle(
            color = Color.White,
            radius = dotRadius,
            center = androidx.compose.ui.geometry.Offset(size.width - spacing, size.height / 2)
        )
    }
}

@Composable
private fun StudentProgressRecordPreviewContent() {
    StudentProgressRecordView(
        studentName = "John Doe",
        studentRank = "Blue Sash",
        studentRankColor = BlueSash,
        progressRecords = listOf(
            ProgressRecord(1L, null, "Front Kick", TechniqueStatus.MASTERED),
            ProgressRecord(2L, null, "Side Kick", TechniqueStatus.NOT_STARTED),
            ProgressRecord(3L, null, "Hook Kick", TechniqueStatus.IN_PROGRESS, "Needs to work on hip rotation"),
            ProgressRecord(4L, null, "Roundhouse Kick", TechniqueStatus.MASTERED),
            ProgressRecord(5L, null, "Back Kick", TechniqueStatus.IN_PROGRESS, notes = "Maintain a slight bend in your elbow. The power comes from the hip rotation, not just the arm swing."),
            ProgressRecord(6L, null, "Axe Kick", TechniqueStatus.MASTERED),
            ProgressRecord(2L, null, "Spinning Kick", TechniqueStatus.IN_PROGRESS, "Practice balance and follow-through"),
            ProgressRecord(3L, null, "Bear Hug Escape #1 ", TechniqueStatus.NOT_STARTED)
        ),
        onNavigateBack = {},
        onToggleTechnique = {},
        onNotesChange = { _, _ -> },
        onSaveProgress = {}
    )
}

@Preview
@Composable
fun StudentProgressRecordPreview() {
    GMATheme {
        androidx.compose.material3.Surface {
            StudentProgressRecordPreviewContent()
        }
    }
}

@Preview
@Composable
fun StudentProgressRecordPreviewDark() {
    GMATheme(darkTheme = true) {
        androidx.compose.material3.Surface {
            StudentProgressRecordPreviewContent()
        }
    }
}

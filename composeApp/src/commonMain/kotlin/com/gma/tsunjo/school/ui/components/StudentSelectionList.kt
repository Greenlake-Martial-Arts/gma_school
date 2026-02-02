// © 2025-2026 Hector Torres – Student & Primary Engineer
// Greenlake Martial Arts – internal use only
// Developed after a verbal agreement to help with the school's tech side.

package com.gma.tsunjo.school.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gma.tsunjo.school.theme.BlackSash
import com.gma.tsunjo.school.theme.BlueSash
import com.gma.tsunjo.school.theme.BrownSash
import com.gma.tsunjo.school.theme.GMATheme
import com.gma.tsunjo.school.theme.GreenSash
import com.gma.tsunjo.school.theme.WhiteSash
import org.jetbrains.compose.ui.tooling.preview.Preview

data class StudentItem(
    val id: String,
    val name: String,
    val rankBadge: String,
    val rankColor: Color,
    val isSelected: Boolean = false
)

@Composable
fun StudentSelectionList(
    students: List<StudentItem>,
    onStudentToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    showCheckbox: Boolean = true
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(students) { student ->
            StudentSelectionItem(
                student = student,
                onToggle = { onStudentToggle(student.id) },
                showCheckbox = showCheckbox
            )
        }
    }
}

@Composable
fun StudentSelectionItem(
    student: StudentItem,
    onToggle: () -> Unit,
    showCheckbox: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onToggle)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Student",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .background(
                            student.rankColor,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = student.rankBadge,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (student.rankBadge.contains("White")) Color.Black else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        if (showCheckbox) {
            Checkbox(
                checked = student.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


@Preview
@Composable
fun StudentSelectionListPreview() {
    GMATheme {
        StudentSelectionList(
            students = listOf(
                StudentItem("1", "John Doe", "White Sash", WhiteSash, false),
                StudentItem("2", "Jane Smith", "Blue Sash", BlueSash, true),
                StudentItem("3", "Bob Johnson", "Green Sash", GreenSash, false),
                StudentItem("4", "Alice Williams", "Brown Sash", BrownSash, true),
                StudentItem("5", "Charlie Brown", "Black Sash", BlackSash, false)
            ),
            onStudentToggle = {}
        )
    }
}

@Preview
@Composable
fun StudentSelectionListPreviewDark() {
    GMATheme(darkTheme = true) {
        StudentSelectionList(
            students = listOf(
                StudentItem("1", "John Doe", "White Sash", WhiteSash, false),
                StudentItem("2", "Jane Smith", "Blue Sash", BlueSash, true),
                StudentItem("3", "Bob Johnson", "Green Sash", GreenSash, false),
                StudentItem("4", "Alice Williams", "Brown Sash", BrownSash, true),
                StudentItem("5", "Charlie Brown", "Black Sash", BlackSash, false)
            ),
            onStudentToggle = {}
        )
    }
}

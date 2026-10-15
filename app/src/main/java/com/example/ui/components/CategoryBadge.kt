package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserCategory
import com.example.ui.theme.*

@Composable
fun CategoryBadge(
    category: UserCategory,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (bgColor, textColor, icon) = when (category) {
        UserCategory.SCHOOL -> Triple(
            SchoolOrangeLight,
            SchoolOrange,
            Icons.Default.School
        )
        UserCategory.COLLEGE -> Triple(
            CollegeTealLight,
            CollegeTeal,
            Icons.Default.WorkspacePremium
        )
        UserCategory.WORKER -> Triple(
            WorkerBlueLight,
            WorkerBlue,
            Icons.Default.BusinessCenter
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 2.dp else 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category.label,
            tint = textColor,
            modifier = Modifier
                .size(if (compact) 12.dp else 14.dp)
                .padding(end = 4.dp)
        )
        Text(
            text = if (compact) category.badgeTitle else category.label,
            color = textColor,
            fontSize = if (compact) 11.sp else 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

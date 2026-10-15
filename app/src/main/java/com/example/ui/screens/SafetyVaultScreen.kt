package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.GuardianRose
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.OmniSphereViewModel

@Composable
fun SafetyVaultScreen(
    viewModel: OmniSphereViewModel,
    modifier: Modifier = Modifier
) {
    val safetySettings by viewModel.safetySettings.collectAsState()
    val safetyReports by viewModel.safetyReports.collectAsState()
    val showReportDialog by viewModel.showAnonymousReportDialog.collectAsState()
    val prefillUser by viewModel.prefillReportedUser.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("safety_vault_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Graphic Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.safety_shield_1792085327616),
                    contentDescription = "Safe Harbor & Guardian Shield",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xDD0E131F))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = GuardianRose,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "FEMALE SAFETY & PRIVATE MODERATION",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Guardian Shield & Safe Harbor",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Zero-tolerance for harassment, unsolicited contact, or toxicity.",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Master Guardian Mode Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (safetySettings.guardianMode) GuardianRose.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = if (safetySettings.guardianMode) Icons.Filled.Shield else Icons.Outlined.Shield,
                        contentDescription = "Guardian Mode",
                        tint = GuardianRose,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Guardian Shield Protection",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GuardianRose
                        )
                        Text(
                            text = if (safetySettings.guardianMode) "Active: Private filtering, keyword masking & DM shields are engaged."
                            else "Guardian protection is currently paused.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = safetySettings.guardianMode,
                        onCheckedChange = { viewModel.toggleGuardianMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GuardianRose,
                            checkedTrackColor = GuardianRose.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("guardian_mode_master_switch")
                    )
                }
            }
        }

        // Anonymous Report Trigger Button
        item {
            Button(
                onClick = { viewModel.openAnonymousReport(true) },
                colors = ButtonDefaults.buttonColors(containerColor = GuardianRose),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trigger_anonymous_report_button")
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Submit 100% Anonymous Incident Report",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Private Moderation Controls
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PRIVATE MODERATION TOOLS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Control 1: Shield DMs from Strangers
                    ModerationToggleRow(
                        title = "Shield DMs from Unknown Users",
                        description = "Block direct message requests unless they are verified mentors or mutual study group cohort members.",
                        checked = safetySettings.shieldDMsFromStrangers,
                        onCheckedChange = { viewModel.updateSafetySetting(shieldDMs = it) }
                    )

                    HorizontalDivider()

                    // Control 2: Toxic Keyword Redaction
                    ModerationToggleRow(
                        title = "Keyword Redaction & Spam Filter",
                        description = "Automatically redact harassment, solicitations, or inappropriate keywords into [shielded] tags.",
                        checked = safetySettings.redactToxicKeywords,
                        onCheckedChange = { viewModel.updateSafetySetting(redactKeywords = it) }
                    )

                    HorizontalDivider()

                    // Control 3: Ghost / Hide Online Status
                    ModerationToggleRow(
                        title = "Ghost / Private Mode",
                        description = "Hide your online activity and study sprint status from public member directories.",
                        checked = safetySettings.hideOnlineStatus,
                        onCheckedChange = { viewModel.updateSafetySetting(hideOnline = it) }
                    )

                    HorizontalDivider()

                    // Control 4: Verified Mentors Only
                    ModerationToggleRow(
                        title = "Restrict DMs to Verified Mentors",
                        description = "Only allow incoming messages from verified educators and vetted professionals.",
                        checked = safetySettings.allowVerifiedMentorsOnly,
                        onCheckedChange = { viewModel.updateSafetySetting(allowVerifiedOnly = it) }
                    )
                }
            }
        }

        // Anonymous Incident Reports Tracking
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CONFIDENTIAL REPORTS STATUS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Audit trail of your private anonymous reports and automated moderation actions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(safetyReports, key = { it.id }) { report ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Anonymous",
                                tint = GuardianRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = report.id,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Surface(
                            color = if (report.status.contains("Restricted")) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = report.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.status.contains("Restricted")) Color(0xFF2E7D32) else Color(0xFFE65100),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Reported: ${report.reportedUserName} • Type: ${report.incidentType}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = report.details,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Helplines & Emergency Support Directory
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "EMERGENCY & HELPLINE DIRECTORY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        HelplineRow(
                            title = "Women & Student Safety Helpline",
                            number = "1091 / 181",
                            subtitle = "24/7 confidential safety & legal guidance",
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1091"))
                                context.startActivity(intent)
                            }
                        )
                        HorizontalDivider()
                        HelplineRow(
                            title = "National Cyber Crime Reporting",
                            number = "1930 / cybercrime.gov",
                            subtitle = "Report online harassment, stalking, or non-consensual media",
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1930"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    // Anonymous Report Dialog
    if (showReportDialog) {
        AnonymousReportDialog(
            initialUser = prefillUser,
            onSubmit = { reportedUser, incidentType, details, evidence ->
                viewModel.submitAnonymousReport(reportedUser, incidentType, details, evidence)
            },
            onDismiss = { viewModel.openAnonymousReport(false) }
        )
    }
}

@Composable
fun ModerationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PrimaryIndigo,
                checkedTrackColor = PrimaryIndigo.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
fun HelplineRow(
    title: String,
    number: String,
    subtitle: String,
    onCall: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "Helpline: $number", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = GuardianRose)
        }
        FilledTonalButton(
            onClick = onCall,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            colors = ButtonDefaults.filledTonalButtonColors(containerColor = GuardianRose.copy(alpha = 0.15f))
        ) {
            Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = GuardianRose, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call", color = GuardianRose, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnonymousReportDialog(
    initialUser: String,
    onSubmit: (String, String, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var reportedUser by remember { mutableStateOf(initialUser) }
    var selectedType by remember { mutableStateOf("Inappropriate Direct Message") }
    var details by remember { mutableStateOf("") }
    var evidence by remember { mutableStateOf("") }

    val incidentTypes = listOf(
        "Inappropriate Direct Message",
        "Harassment / Cyberbullying",
        "Solicitation of Personal Details",
        "Unwanted / Explicit Media",
        "Impersonation or Scam"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("anonymous_report_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GuardianRose)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confidential Incident Report",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Surface(
                    color = GuardianRose.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "🔒 Zero Identity Leak Guarantee: This report is completely untraceable to your user account or personal email.",
                        fontSize = 11.sp,
                        color = GuardianRose,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = reportedUser,
                        onValueChange = { reportedUser = it },
                        label = { Text("Reported Username or Handle") },
                        placeholder = { Text("e.g. @user_handle or Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("report_user_field"),
                        singleLine = true
                    )

                    Text(
                        text = "INCIDENT CATEGORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            incidentTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Incident Details & Context") },
                        placeholder = { Text("Describe what happened, timestamp, or behavior...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("report_details_field"),
                        maxLines = 5
                    )

                    OutlinedTextField(
                        value = evidence,
                        onValueChange = { evidence = it },
                        label = { Text("Message Excerpt / Evidence (Optional)") },
                        placeholder = { Text("Paste exact text or message snippet...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (reportedUser.isNotBlank() && details.isNotBlank()) {
                                onSubmit(reportedUser, selectedType, details, evidence.ifBlank { null })
                            }
                        },
                        enabled = reportedUser.isNotBlank() && details.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GuardianRose),
                        modifier = Modifier.testTag("submit_report_button")
                    ) {
                        Text("Submit Encrypted Report")
                    }
                }
            }
        }
    }
}

package com.nkhearn.psm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkhearn.psm.settings.SettingsManager
import com.nkhearn.psm.viewmodel.SolarViewModel
import com.nkhearn.psm.ui.SimpleLineChart

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: SolarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = SettingsManager(this)
        viewModel = SolarViewModel(settingsManager)

        setContent {
            var showSettings by remember { mutableStateOf(false) }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSettings) {
                        SettingsScreen(
                            initialHost = viewModel.getHost(),
                            initialPort = viewModel.getPort(),
                            onSave = { host, port ->
                                viewModel.updateSettings(host, port)
                                showSettings = false
                            },
                            onBack = { showSettings = false }
                        )
                    } else {
                        DashboardScreen(
                            viewModel = viewModel,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: SolarViewModel, onOpenSettings: () -> Unit) {
    val currentData by viewModel.currentData.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val metricHistory by viewModel.metricHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                ) {
                    Surface(
                        color = if (isConnected) Color.Green else Color.Red,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) stringResource(R.string.connection_status_connected)
                           else stringResource(R.string.connection_status_disconnected),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            currentData?.let { response ->
                Text(
                    text = "Last update: ${response.timestamp}",
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(response.data.toList()) { (key, value) ->
                        MetricCard(
                            key = key,
                            value = value.toString(),
                            history = metricHistory[key] ?: emptyList()
                        )
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_data_available))
            }
        }
    }
}

@Composable
fun MetricCard(key: String, value: String, history: List<Double>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = key.replace("_", " ").replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp
                )
            }
            if (history.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                SimpleLineChart(
                    data = history,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    initialHost: String,
    initialPort: Int,
    onSave: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var host by remember { mutableStateOf(initialHost) }
    var port by remember { mutableStateOf(initialPort.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text(stringResource(R.string.host_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = port,
                onValueChange = { port = it },
                label = { Text(stringResource(R.string.port_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onSave(host, port.toIntOrNull() ?: 8000) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.save_button))
            }
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel")
            }
        }
    }
}

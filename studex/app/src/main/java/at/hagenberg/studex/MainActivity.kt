package at.hagenberg.studex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavigationComponent(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "welcome" ) {
        composable("welcome") { SubjectOverview(navController = navController)}
        composable("showDetails/{itemId}") { backStackEntry ->
            DetailView(backStackEntry.arguments?.getString("itemId"))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubjectOverview(navController: NavController) {
    val openDialog = remember { mutableStateOf(false) }
    var list = remember { mutableStateListOf("Maths", "PRO", "WIA", "OIS", "VIS", "FPS", "DB") }
    var text by remember { mutableStateOf("") }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { openDialog.value = true },
                backgroundColor = Color(0xFF23752A),
                contentColor = Color.White) {
                Icon(Icons.Filled.Add, "")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        LazyColumn(modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),

        ) {
            itemsIndexed(items = list) { _, name ->
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White),
                    backgroundColor = Color(0xFF23752A),
                    onClick = { navController.navigate("showDetails/$name") }
                ) {
                    Text(text = name, modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontWeight = FontWeight.Bold)


                }

            }

        }
        if(openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                title = { Text(text = "Enter a Name")},
                text = {
                    TextField(value = text,
                        onValueChange = { text = it})
                },
                confirmButton = {
                    Button(onClick = {
                        openDialog.value = false
                        list.add(text)
                        text = ""
                    }) {
                        Text(text = "Save")
                    }},
                dismissButton = {
                    OutlinedButton(onClick = { openDialog.value = false }) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        val navController = rememberNavController()
        NavigationComponent(navController = navController)
    }
}
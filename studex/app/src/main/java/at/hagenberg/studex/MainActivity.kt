package at.hagenberg.studex

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import at.hagenberg.studex.core.Subject
import at.hagenberg.studex.proxy.DatabaseProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var list = remember { mutableStateListOf<Subject>() }
    var text by remember { mutableStateOf("") }

    val context = LocalContext.current
    getSubjects(context, list)

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
            itemsIndexed(items = list) { _, subject ->
                Card(modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White),
                    backgroundColor = Color(0xFF23752A),
                    onClick = { navController.navigate("showDetails/${subject.id}") }
                ) {
                    subject.name?.let { it1 ->
                        Text(text = it1, modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontWeight = FontWeight.Bold)
                    }


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
                        CoroutineScope(Dispatchers.IO).launch {
                            DatabaseProxy.createProxy(context).subjectDao().insertSubject(Subject(0, name = text))
                            text = ""
                            getSubjects(context, list)
                        }


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

fun getSubjects(context: Context, list: SnapshotStateList<Subject>) {

    CoroutineScope(Dispatchers.IO).launch {
        val subjects = DatabaseProxy.createProxy(context).subjectDao().getAll()

        withContext(Dispatchers.Main) {
            list.clear()
            list.addAll(subjects)
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
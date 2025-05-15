package com.example.holamundi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import com.example.holamundi.ui.theme.HolaMundiTheme
import androidx.room.Room
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.holamundi.AppDatabase
import com.example.holamundi.Usuario
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*


import androidx.compose.ui.text.input.PasswordVisualTransformation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.Alignment

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color








import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.clip

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.core.graphics.drawable.toDrawable
import com.example.holamundi.R


import androidx.compose.material.icons.Icons


import androidx.compose.material.icons.filled.*

import androidx.compose.material.icons.filled.*

import androidx.compose.material.icons.filled.*

import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {





        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar base de datos con soporte para migraciones destructivas
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mi_bd"
        )
            .fallbackToDestructiveMigration() // <-- esta línea permite recrear la BD si cambias el esquema
            .allowMainThreadQueries() // Solo para pruebas
            .build()


        // Luego ya puedes usarla
       // db.empresaDao().insertarEmpresa(EmpresaEntity(nombre = "Rapido del carmen1", rating = 4.0, reviews = 4))

        setContent {
            HolaMundiTheme {
                MyAppNavigation(db)
            }
        }
    }
}


@Composable
fun MyAppNavigation(db: AppDatabase) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLogin = { nombre, contrasena ->
                    val usuario = db.usuarioDao().verificarUsuario(nombre, contrasena)
                    usuario != null
                }
            )
        }

        composable("home") { RouteApp(navController) }
        composable("register") { RegisterScreen(navController, db) }






        composable("tripDetails/{origin}/{destination}/{estimatedTime}/{distance}/{fare}") { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin")
            val destination = backStackEntry.arguments?.getString("destination")
            val estimatedTime = backStackEntry.arguments?.getString("estimatedTime")
            val distance = backStackEntry.arguments?.getString("distance")
            val fare = backStackEntry.arguments?.getString("fare")
            TripDetailsScreen(navController, origin, destination, estimatedTime!!, distance!!, fare!!)
        }

        composable("mapScreen/{origin}/{destination}") { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin")
            val destination = backStackEntry.arguments?.getString("destination")
            MapScreen(navController, origin, destination)
        }

        composable("module2") { Module2Screen(navController) }


        composable("module3") { CompaniesScreen(navController, null, null) }

        composable("compras") { ComprasScreen(navController, db) }
        composable("logout") { LogoutScreen(navController) }


        composable("addCompany") {
            AddCompanyScreen(navController)
        }

        composable(
            "detalles_empresa/{empresaId}",
            arguments = listOf(navArgument("empresaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val empresaId = backStackEntry.arguments?.getInt("empresaId") ?: 0
            DetallesEmpresaScreen(navController, empresaId)
        }


        composable("pasajesComprados") {
            PasajesCompradosScreen(navController, db)

        }




        composable("companies/{origin}/{destination}") { backStackEntry ->
            val origin = backStackEntry.arguments?.getString("origin")
            val destination = backStackEntry.arguments?.getString("destination")
            CompaniesScreen(navController, origin, destination)
        }
    }
}



sealed class Pantalla(val ruta: String) {
    object Compras : Pantalla("compras")
    object PasajesComprados : Pantalla("pasajes_comprados")
}
@Composable
fun PasajesCompradosScreen(navController: NavHostController, db: AppDatabase) {
    // Estado para almacenar la lista de pasajes
    var pasajes by remember { mutableStateOf<List<ViajeEntity1>>(emptyList()) }

    // Efecto para cargar los datos al iniciar la pantalla
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val viajes = db.viajeDao1().obtenerTodos()
            pasajes = viajes
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            "Pasajes Comprados",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        pasajes.forEach { pasaje ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("De: ${pasaje.origen}", fontWeight = FontWeight.Bold)
                    Text("Hasta: ${pasaje.destino}")
                    Text("Hora de salida: ${pasaje.horaSalida}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20D562))
        ) {
            Text("Volver", color = Color.White)
        }
    }
}


data class Pasaje(val origen: String, val destino: String, val hora: String)











@Composable
fun ComprasScreen(navController: NavHostController, db: AppDatabase) {
    var origen by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var horaSalida by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            "Compra tus pasajes",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF20D562)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = origen,
                    onValueChange = { origen = it },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    placeholder = { Text("De") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = destino,
                    onValueChange = { destino = it },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    placeholder = { Text("Hasta") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = horaSalida,
            onValueChange = { horaSalida = it },
            placeholder = { Text("Ingrese su hora de salida") },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4F4F4), shape = RoundedCornerShape(12.dp))
        )

        Spacer(Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.bus_logo),
            contentDescription = "Imagen de buses",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (origen.isNotEmpty() && destino.isNotEmpty() && horaSalida.isNotEmpty()) {
                    val viaje = ViajeEntity1(origen = origen, destino = destino, horaSalida = horaSalida)
                    db.viajeDao1().insertarViaje(viaje)
                    navController.navigate("pasajesComprados")
                } else {
                    mensaje = "Todos los campos son obligatorios"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF20D562))
        ) {
            Text("Comprar Pasaje", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = Color.Red,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun LogoutScreen(navController: NavHostController) {
    // Aquí puedes limpiar sesión o redirigir al login
    LaunchedEffect(Unit) {
        navController.navigate("login") {
            popUpTo("home") { inclusive = true } // Limpia el back stack
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HolaMundiTheme {
        Greeting("Android")
    }
}







@Composable
fun LoginScreen(navController: NavHostController, onLogin: (String, String) -> Boolean) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.bus_logo),
                contentDescription = "Logo de la app",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de login
            Button(
                onClick = {
                    val isAuthenticated = onLogin(username, password)
                    if (isAuthenticated) {
                        navController.navigate("home")
                    } else {
                        errorMessage = "Usuario o contraseña incorrectos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B167)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit", color = Color.White, fontSize = 16.sp)
            }

            // Mensaje de error
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Link a registro
            Text(
                text = "Register",
                color = Color(0xFF0048FF),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simulación de avatares (no funcionales)
            Row {
                Image(painter = painterResource(id = R.drawable.avatar1), contentDescription = null, modifier = Modifier.size(32.dp).clip(CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Image(painter = painterResource(id = R.drawable.avatar1), contentDescription = null, modifier = Modifier.size(32.dp).clip(CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Image(painter = painterResource(id = R.drawable.avatar1), contentDescription = null, modifier = Modifier.size(32.dp).clip(CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+1", fontSize = 12.sp)
                }
            }
        }
    }
}



@Composable
fun RegisterScreen(navController: NavHostController, db: AppDatabase) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Círculos decorativos
        Image(
            painter = painterResource(id = R.drawable.avatar1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(150.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.avatar1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(150.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.bus_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // User
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("User") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        val existente = db.usuarioDao().verificarUsuario(username, password)
                        if (existente == null) {
                            db.usuarioDao().insertarUsuario(Usuario(username, password))
                            navController.navigate("login")
                        } else {
                            errorMessage = "El usuario ya existe"
                        }
                    } else {
                        errorMessage = "Todos los campos son obligatorios"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A550)) // Verde tipo submit
            ) {
                Text("Submit", color = Color.White)
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(text = "¿Ya tiene una cuenta? ")
                Text(
                    text = "Iniciar sesión",
                    color = Color(0xFF00A3B3),
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteApp(navController: NavHostController) {
    val chia = LatLng(4.8631, -74.0324)
    val terminalZipa = LatLng(4.7963, -74.0270)
    val tabio = LatLng(4.9187, -74.0921)

    val lugares = mapOf(
        "Chía" to chia,
        "Terminal de Zipa" to terminalZipa,
        "Tabio" to tabio
    )

    var selectedOrigin by remember { mutableStateOf<String?>(null) }
    var selectedDestination by remember { mutableStateOf<String?>(null) }
    var selectedRoute by remember { mutableStateOf<List<LatLng>?>(null) }
    var showRoute by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "mi_bd")
            .fallbackToDestructiveMigration()
            .build()
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar viaje", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Footer(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Selecciona tu ubicación", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))

            DropdownMenuComponent(
                label = "Donde estoy",
                options = lugares.keys.toList(),
                selectedOption = selectedOrigin,
                onOptionSelected = { selectedOrigin = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownMenuComponent(
                label = "A donde quiero ir",
                options = lugares.keys.toList(),
                selectedOption = selectedDestination,
                onOptionSelected = { selectedDestination = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                if (showRoute && selectedRoute != null) {
                    MapScreenContent(routePoints = selectedRoute!!)
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(chia, 10f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val originCoords = selectedOrigin?.let { lugares[it] }
                    val destCoords = selectedDestination?.let { lugares[it] }

                    if (originCoords != null && destCoords != null) {
                        selectedRoute = listOf(originCoords, destCoords)
                        showRoute = true

                        coroutineScope.launch {
                            db.viajeDao().insertarViaje(
                                ViajeEntity(
                                    origen = selectedOrigin!!,
                                    destino = selectedDestination!!,
                                    tiempoEstimado = "30 mins",
                                    distancia = "10 km",
                                    tarifa = "2.50 USD"
                                )
                            )

                            navController.navigate(
                                "tripDetails/${selectedOrigin}/${selectedDestination}/30 mins/10 km/2.50 USD"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D084))
            ) {
                Text("Siguiente")
            }
        }
    }
}




@Composable
fun MapScreenContent(routePoints: List<LatLng>) {
    val initialPosition = routePoints.firstOrNull() ?: LatLng(4.8631, -74.0324)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Polyline(points = routePoints, color = Color.Blue, width = 5f)
    }
}


@Composable
fun Footer(navController: NavHostController) {
    NavigationBar(
        containerColor = Color(0xFF00D084)
    ) {
        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "home",
            onClick = { navController.navigate("home") },
            label = { Text("Viajar") },
            icon = {}
        )
        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "module2",
            onClick = { navController.navigate("module2") },
            label = { Text("Mis viajes") },
            icon = {}
        )
        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "module3",
            onClick = { navController.navigate("module3") },
            label = { Text("Reseñas") },
            icon = {}
        )
        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "compras",
            onClick = { navController.navigate("compras") },
            label = { Text("Compras") },
            icon = {}
        )
        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "logout",
            onClick = { navController.navigate("logout") },
            label = { Text("Salir") },
            icon = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun Module2Screen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "mi_bd")
            .allowMainThreadQueries() // Solo para pruebas
            .fallbackToDestructiveMigration()
            .build()
    }

    val viajes = remember { mutableStateListOf<ViajeEntity>() }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        viajes.clear()
        viajes.addAll(db.viajeDao().obtenerTodosLosViajes())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Viajes", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00D084))
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (viajes.isEmpty()) {
                Text("No has realizado ningún viaje.", style = MaterialTheme.typography.bodyLarge)
            } else {
                viajes.forEach { viaje ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Origen: ${viaje.origen ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Destino: ${viaje.destino ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Costo: ${viaje.tarifa ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Tiempo: ${viaje.tiempoEstimado ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Distancia: ${viaje.distancia ?: "No disponible"}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

data class TripInfo(val origin: String, val destination: String, val cost: String, val time: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Module3Screen(navController: NavHostController) {
    var selectedCompany by remember { mutableStateOf<String?>(null) }
    var opinion by remember { mutableStateOf(TextFieldValue()) }
    val companies = listOf("TransCo", "BusExpress", "CityTransport")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opiniones para Empresas", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00D084))
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            DropdownMenuComponent(
                label = "Selecciona una empresa",
                options = companies,
                selectedOption = selectedCompany,
                onOptionSelected = { selectedCompany = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Escribe tu opinión", style = MaterialTheme.typography.bodyMedium)

            BasicTextField(
                value = opinion,
                onValueChange = { opinion = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF0F0F0)),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Lógica para guardar la opinión localmente (puede ser en una lista mutable)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D084))
            ) {
                Text("Enviar Opinión", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    navController: NavHostController,
    origin: String?,
    destination: String?,
    estimatedTime: String,
    distance: String,
    fare: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Viaje", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00D084))
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Origen: $origin", style = MaterialTheme.typography.headlineMedium)
                    Text("Destino: $destination", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tiempo Estimado: $estimatedTime", style = MaterialTheme.typography.bodyMedium)
                    Text("Distancia: $distance", style = MaterialTheme.typography.bodyMedium)
                    Text("Costo Aproximado: $fare", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("mapScreen/$origin/$destination")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D084))
            ) {
                Text("Iniciar Viaje", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.navigate("companies/$origin/$destination")
            }) {
                Text("Empresas que prestan el servicio", style = MaterialTheme.typography.bodyLarge, color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Atrás", style = MaterialTheme.typography.bodyLarge, color = Color.Blue)
            }
        }
    }
}















@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCompanyScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "mi_bd")
            .allowMainThreadQueries()
            .build()
    }

    var nombre by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var reviews by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Empresa", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        )
        {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )

            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating (0.0 - 5.0)") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = reviews,
                onValueChange = { reviews = it },
                label = { Text("Número de reseñas") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            if (showError) {
                Text("Por favor completa todos los campos correctamente", color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = {
                    val r = rating.toDoubleOrNull()
                    val rev = reviews.toIntOrNull()
                    if (nombre.isNotBlank() && r != null && rev != null && r in 0.0..5.0) {
                        db.empresaDao().insertarEmpresa(
                            EmpresaEntity(nombre = nombre, rating = r, reviews = rev)
                        )
                        navController.popBackStack() // volver a CompaniesScreen
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompaniesScreen(navController: NavHostController, origin: String?, destination: String?) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "mi_bd")
            .allowMainThreadQueries() // Solo para pruebas
            .build()
    }

    var companies by remember { mutableStateOf<List<EmpresaEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        companies = db.empresaDao().obtenerEmpresas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñas", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("¿Quieres opinar sobre una empresa? ", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Da click a la empresa",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF00BFA6)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            companies.forEach { company ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            navController.navigate("detalles_empresa/${company.id}")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(company.nombre, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(company.rating.toInt()) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                                }
                                if (company.rating - company.rating.toInt() >= 0.5f) {
                                    Text("⭐", fontSize = 16.sp, color = Color(0xFFFFC107))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("(${company.reviews})", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Ver más")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("¿Quieres comprar un pasaje? ", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Da click aquí",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF00BFA6)),
                modifier = Modifier.clickable {
                    // Acción al dar clic
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Agregar nueva empresa",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF00BFA6)),
                modifier = Modifier.clickable {
                    navController.navigate("addCompany")
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesEmpresaScreen(navController: NavHostController, empresaId: Int) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "mi_bd")
            .allowMainThreadQueries()
            .build()
    }

    var empresa by remember { mutableStateOf<EmpresaEntity?>(null) }

    var comentarioTexto by remember { mutableStateOf("") }
    var comentarios by remember { mutableStateOf<List<ComentarioEntity>>(emptyList()) }

    LaunchedEffect(empresaId) {
        empresa = db.empresaDao().obtenerEmpresaPorId(empresaId)
        empresa?.let {
            comentarios = db.comentarioDao().obtenerComentariosPorEmpresa(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles Empresa") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        empresa?.let { e ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Detalles de la empresa
                Text(e.nombre, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Rating: ${e.rating}")
                Spacer(modifier = Modifier.height(4.dp))
                Text("Reseñas: ${e.reviews}")
                Spacer(modifier = Modifier.height(16.dp))

                // Sección de Comentarios
                Text("Comentarios:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = comentarioTexto,
                    onValueChange = { comentarioTexto = it },
                    label = { Text("Escribe tu comentario") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (comentarioTexto.isNotBlank()) {
                            db.comentarioDao().insertarComentario(
                                ComentarioEntity(empresaId = e.id, texto = comentarioTexto)
                            )
                            comentarios = db.comentarioDao().obtenerComentariosPorEmpresa(e.id)
                            comentarioTexto = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Comentar")
                }

                Spacer(modifier = Modifier.height(16.dp))

                comentarios.forEach { comentario ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Text(
                            text = comentario.texto,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Empresa no encontrada")
            }
        }
    }
}







data class CompanyReview(val name: String, val rating: Double, val reviews: Int)


data class CompanyInfo(val name: String, val rating: Double, val fare: String, val extras: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuComponent(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption ?: "") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentSelection,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        currentSelection = option
                        expanded = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavHostController, origin: String?, destination: String?) {
    val routePoints = listOf(LatLng(4.8631, -74.0324), LatLng(4.9187, -74.0921))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa del Viaje", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00D084))
            )
        },
        bottomBar = { Footer(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Atrás", style = MaterialTheme.typography.bodyLarge, color = Color.Blue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                MapScreenContent(routePoints = routePoints)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ruta desde $origin a $destination",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
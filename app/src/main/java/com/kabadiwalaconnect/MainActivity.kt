package com.kabadiwalaconnect

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.LocalAppStrings
import com.kabadiwalaconnect.language.LocalLanguageViewModel
import com.kabadiwalaconnect.language.LocalCurrentLanguage
import com.kabadiwalaconnect.language.getAppStrings
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.LanguageSelectionDialog
import com.kabadiwalaconnect.ui.screens.*
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaTheme
import com.kabadiwalaconnect.viewmodel.viewModelFactory

class MainActivity : ComponentActivity() {
    private val languageViewModel: LanguageViewModel by viewModels { viewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentLanguage by languageViewModel.currentLanguage.collectAsState()
            val showLanguageDialog by languageViewModel.showLanguageDialog.collectAsState()
            val baseContext = LocalContext.current

            val localizedContext = remember(currentLanguage) {
                val config = Configuration(baseContext.resources.configuration)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    config.setLocale(currentLanguage.locale)
                    config.setLocales(LocaleList(currentLanguage.locale))
                } else {
                    @Suppress("DEPRECATION")
                    config.locale = currentLanguage.locale
                }
                baseContext.createConfigurationContext(config)
            }

            val currentStrings = remember(currentLanguage) {
                getAppStrings(currentLanguage.code)
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalAppStrings provides currentStrings,
                LocalLanguageViewModel provides languageViewModel,
                LocalCurrentLanguage provides currentLanguage,
                androidx.activity.compose.LocalActivityResultRegistryOwner provides this@MainActivity
            ) {
                KabadiwalaTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = KabadiwalaColors.Background
                    ) {
                        AppNavHost(languageViewModel = languageViewModel)
                    }

                    if (showLanguageDialog) {
                        LanguageSelectionDialog(
                            viewModel = languageViewModel,
                            onDismiss = { languageViewModel.dismissLanguageDialog() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val navController = rememberNavController()
    val startDestination = remember { mutableStateOf(Screen.Splash.route) }
    val showMainApp = remember { mutableStateOf(false) }

    NavHost(navController, startDestination = startDestination.value) {
        composable(route = Screen.Splash.route) {
            SplashIntroFlow(onComplete = {
                showMainApp.value = true
                startDestination.value = Screen.Home.route
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.MaterialEntry.route) {
            MaterialEntryScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.MarketPrices.route) {
            MarketPricesScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.RecyclerDiscovery.route) {
            RecyclerDiscoveryScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.Transaction.route) {
            TransactionScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.Earnings.route) {
            EarningsScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.Safety.route) {
            SafetyScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(route = Screen.MaterialScanner.route) {
            MaterialScannerScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(
            route = Screen.MaterialEntryWithId.route,
            arguments = listOf(androidx.navigation.navArgument("materialId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getString("materialId")
            MaterialEntryScreen(
                onNavigate = { screen -> navController.navigate(screen.route) },
                preselectedMaterialId = materialId,
                languageViewModel = languageViewModel
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(androidx.navigation.navArgument("transactionId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }

        composable(
            route = Screen.RecyclerDetail.route,
            arguments = listOf(androidx.navigation.navArgument("recyclerId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val recyclerId = backStackEntry.arguments?.getString("recyclerId") ?: ""
            RecyclerDetailScreen(
                recyclerId = recyclerId,
                onNavigate = { screen -> navController.navigate(screen.route) },
                languageViewModel = languageViewModel
            )
        }
    }
}
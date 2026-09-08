@file:Suppress("SpellCheckingInspection")

package com.kabadiwalaconnect.data.repository

import com.kabadiwalaconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.YearMonth
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class KabadiwalaRepositoryImpl : KabadiwalaRepository {

    private val _materials = MutableStateFlow(Material.ALL_MATERIALS)
    override val materials: StateFlow<List<Material>> = _materials.asStateFlow()

    private val _marketPrices = MutableStateFlow(generateMockMarketPrices())
    override val marketPrices: StateFlow<List<MarketPrice>> = _marketPrices.asStateFlow()

    private val _nearbyRecyclers = MutableStateFlow(generateMockRecyclers())
    override val nearbyRecyclers: StateFlow<List<Recycler>> = _nearbyRecyclers.asStateFlow()

    private val _transactions = MutableStateFlow(generateMockTransactions())
    override val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _earningsSummary = MutableStateFlow<EarningsSummary?>(generateMockEarningsSummary())
    override val earningsSummary: StateFlow<EarningsSummary?> = _earningsSummary.asStateFlow()

    private val _khataEntries = MutableStateFlow(generateMockKhataEntries())
    override val khataEntries: StateFlow<List<KhataEntry>> = _khataEntries.asStateFlow()

    private val _safetyTips = MutableStateFlow(SafetyTip.ALL_TIPS)
    override val safetyTips: StateFlow<List<SafetyTip>> = _safetyTips.asStateFlow()

    private val _emergencyContacts = MutableStateFlow(EmergencyContact.DEFAULT_CONTACTS)
    override val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(generateMockUser())
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun getMaterials(): Result<List<Material>> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        Result.Success(_materials.value)
    }

    override suspend fun getMaterial(id: String): Result<Material?> = withContext(Dispatchers.IO) {
        delay(100.milliseconds)
        Result.Success(Material.getById(id))
    }

    override suspend fun refreshMarketPrices(): Result<List<MarketPrice>> = withContext(Dispatchers.IO) {
        delay(800.milliseconds)
        val updated = _marketPrices.value.map { price ->
            val change = (Random.nextDouble() - 0.5) * 10 // -5% to +5%
            val newPrice = price.pricePerKg * (1 + change / 100)
            val trend = when {
                change > 1 -> PriceTrend.UP
                change < -1 -> PriceTrend.DOWN
                else -> PriceTrend.STABLE
            }
            price.copy(
                pricePerKg = newPrice,
                trend = trend,
                changePercent = change.absoluteValue,
                lastUpdated = Instant.now()
            )
        }
        _marketPrices.value = updated
        Result.Success(updated)
    }

    override suspend fun getMarketPrice(materialId: String): Result<MarketPrice?> = withContext(Dispatchers.IO) {
        delay(100.milliseconds)
        Result.Success(_marketPrices.value.find { it.materialId == materialId })
    }

    override suspend fun searchRecyclers(
        query: String,
        lat: Double,
        lng: Double,
        radiusKm: Double,
        category: MaterialCategory?
    ): Result<RecyclerSearchResult> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        val filtered = _nearbyRecyclers.value.filter { recycler ->
            val matchesQuery = query.isBlank() ||
                    recycler.name.contains(query, ignoreCase = true) ||
                    recycler.address.contains(query, ignoreCase = true) ||
                    recycler.contactPerson.contains(query, ignoreCase = true)
            val matchesRadius = recycler.distanceKm <= radiusKm
            val matchesCategory = category == null || category == MaterialCategory.ALL || recycler.acceptsCategory(category)
            matchesQuery && matchesRadius && matchesCategory
        }.sortedBy { it.distanceKm }
        Result.Success(RecyclerSearchResult(filtered, filtered.size, radiusKm))
    }

    override suspend fun getRecycler(id: String): Result<Recycler?> = withContext(Dispatchers.IO) {
        delay(100.milliseconds)
        Result.Success(_nearbyRecyclers.value.find { it.id == id })
    }

    override suspend fun createTransaction(transaction: Transaction): Result<Transaction> = withContext(Dispatchers.IO) {
        delay(600.milliseconds)
        val updated = listOf(transaction) + _transactions.value
        _transactions.value = updated
        Result.Success(transaction)
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Transaction> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        _transactions.value = _transactions.value.map { if (it.id == transaction.id) transaction else it }
        Result.Success(transaction)
    }

    override suspend fun confirmHandover(transactionId: String, otp: String): Result<Transaction> = withContext(Dispatchers.IO) {
        delay(500.milliseconds)
        val txn = _transactions.value.find { it.id == transactionId }
            ?: return@withContext Result.Error(Exception("Transaction not found"))

        if (txn.otp != otp) {
            return@withContext Result.Error(Exception("Invalid OTP"))
        }

        val updatedTxn = txn.copy(
            status = TransactionStatus.COMPLETED,
            handoverConfirmed = true
        )
        val updated = _transactions.value.map {
            if (it.id == transactionId) updatedTxn else it
        }
        _transactions.value = updated

        // Update khata
        val khataEntry = KhataEntry(
            id = "khata_${System.currentTimeMillis()}",
            date = updatedTxn.transactionDate,
            recyclerName = updatedTxn.recyclerName,
            materialName = updatedTxn.items.firstOrNull()?.materialName ?: "Multiple Materials",
            weightKg = updatedTxn.totalWeight,
            ratePerKg = if (updatedTxn.totalWeight > 0) updatedTxn.totalAmount / updatedTxn.totalWeight else 0.0,
            amount = updatedTxn.totalAmount,
            paymentStatus = PaymentStatus.PAID,
            paymentDate = Instant.now()
        )
        _khataEntries.value = listOf(khataEntry) + _khataEntries.value

        // Update earnings summary
        updateEarningsAfterTransaction(updatedTxn)

        Result.Success(updatedTxn)
    }

    override suspend fun getTransactions(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        Result.Success(_transactions.value)
    }

    override suspend fun getEarningsSummary(): Result<EarningsSummary> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        val summary = _earningsSummary.value ?: generateMockEarningsSummary()
        _earningsSummary.value = summary
        Result.Success(summary)
    }

    override suspend fun getKhataEntries(): Result<List<KhataEntry>> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        Result.Success(_khataEntries.value.sortedByDescending { it.date })
    }

    override suspend fun getMonthlyEarnings(): Result<List<MonthlyEarnings>> = withContext(Dispatchers.IO) {
        delay(300.milliseconds)
        val months = (0..5).map { i ->
            YearMonth.now().minusMonths(i.toLong())
        }
        val result = months.map { month ->
            val txCount = Random.nextInt(5, 20)
            val totalWeight = Random.nextDouble(50.0, 500.0)
            val totalEarnings = Random.nextDouble(1000.0, 15000.0)
            MonthlyEarnings(
                month = month,
                totalEarnings = totalEarnings,
                totalWeight = totalWeight,
                transactionCount = txCount,
                materialBreakdown = mapOf(
                    "Paper" to MaterialEarnings(Random.nextDouble(10.0, 100.0), Random.nextDouble(200.0, 1500.0), Random.nextInt(2, 8)),
                    "Plastic" to MaterialEarnings(Random.nextDouble(5.0, 50.0), Random.nextDouble(200.0, 1500.0), Random.nextInt(1, 5)),
                    "Metal" to MaterialEarnings(Random.nextDouble(2.0, 30.0), Random.nextDouble(500.0, 2000.0), Random.nextInt(1, 4))
                )
            )
        }
        Result.Success(result)
    }

    override suspend fun getSafetyTips(): Result<List<SafetyTip>> = withContext(Dispatchers.IO) {
        delay(200.milliseconds)
        Result.Success(_safetyTips.value)
    }

    override suspend fun getEmergencyContacts(): Result<List<EmergencyContact>> = withContext(Dispatchers.IO) {
        delay(200.milliseconds)
        Result.Success(_emergencyContacts.value)
    }

    override suspend fun getCurrentUser(): Result<User?> = withContext(Dispatchers.IO) {
        delay(200.milliseconds)
        Result.Success(_currentUser.value)
    }

    override suspend fun updateUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        delay(400.milliseconds)
        _currentUser.value = user
        Result.Success(user)
    }

    // Helper functions for mock data generation
    private fun generateMockMarketPrices(): List<MarketPrice> {
        return Material.ALL_MATERIALS.map { material ->
            val change = (Random.nextDouble() - 0.5) * 8
            val price = material.basePricePerKg * (1 + change / 100)
            MarketPrice(
                materialId = material.id,
                materialName = material.name,
                pricePerKg = price,
                trend = when {
                    change > 1 -> PriceTrend.UP
                    change < -1 -> PriceTrend.DOWN
                    else -> PriceTrend.STABLE
                },
                changePercent = change.absoluteValue,
                lastUpdated = Instant.now().minusSeconds(Random.nextInt(3600).toLong())
            )
        }
    }

    private fun generateMockRecyclers(): List<Recycler> {
        return listOf(
            Recycler(
                id = "recycler_sharma",
                name = "Sharma Metal & Mandi Scrap Traders",
                address = "Plot 42, Sector 18 Industrial Area, Gurgaon",
                phone = "+91 98112 34567",
                whatsappNumber = "+919811234567",
                contactPerson = "Ramesh Sharma (Proprietor)",
                latitude = 12.9780,
                longitude = 77.5920,
                rating = 4.9f,
                reviewCount = 192,
                acceptedMaterials = listOf("copper_wires_pipes", "brass_utensils_fittings", "aluminium_cans_utensils", "iron_loha_scrap", "heavy_copper_cable_scrap", "tin_metal_cans"),
                workingHours = "8:00 AM - 8:30 PM",
                isVerified = true,
                distanceKm = 0.8,
                paymentModes = listOf("Cash on Spot", "Instant UPI", "IMPS"),
                minPickupKg = 10.0,
                facilityType = "Wholesale Mandi Yard • Certified Weighbridge",
                isOpenNow = true,
                primaryCategory = MaterialCategory.METAL
            ),
            Recycler(
                id = "recycler_gupta_copper",
                name = "Gupta Brothers Copper & Wire Mart",
                address = "Shop 18, Commercial Street, Chickpet Main Road, Bangalore",
                phone = "+91 98765 43210",
                whatsappNumber = "+919876543210",
                contactPerson = "Anil Gupta (Dealer)",
                latitude = 12.9620,
                longitude = 77.5850,
                rating = 4.9f,
                reviewCount = 134,
                acceptedMaterials = listOf("copper_wires_pipes", "heavy_copper_cable_scrap", "insulated_copper_wire", "brass_utensils_fittings"),
                workingHours = "9:00 AM - 8:00 PM",
                isVerified = true,
                distanceKm = 2.9,
                paymentModes = listOf("Cash on Spot", "UPI"),
                minPickupKg = 5.0,
                facilityType = "Copper Spectrometer On-Site • Premium Rates",
                isOpenNow = true,
                primaryCategory = MaterialCategory.METAL
            ),
            Recycler(
                id = "recycler_bengaluru_eco",
                name = "Bengaluru Eco Paper & Raddi Mart",
                address = "5th Cross, 17th Main, Sector 4, HSR Layout, Bangalore",
                phone = "+91 94481 23456",
                whatsappNumber = "+919448123456",
                contactPerson = "Venkatesh Murthy",
                latitude = 12.9650,
                longitude = 77.6010,
                rating = 4.8f,
                reviewCount = 248,
                acceptedMaterials = listOf("newspapers_raddi", "books_white_paper", "cardboard_carton_gutta", "kraft_paper_heavy_brown_bags", "sorted_white_notebooks_ledger"),
                workingHours = "7:30 AM - 8:00 PM",
                isVerified = true,
                distanceKm = 1.4,
                paymentModes = listOf("Instant UPI", "Cash"),
                minPickupKg = 5.0,
                facilityType = "Doorstep Pickup Van • Daily Spot Payout",
                isOpenNow = true,
                primaryCategory = MaterialCategory.PAPER
            ),
            Recycler(
                id = "recycler_metro_ewaste",
                name = "Metro E-Waste & Circuit Dismantlers",
                address = "Plot 89, Tech Park Hub, Electronic City Phase 1, Bangalore",
                phone = "+91 98450 67890",
                whatsappNumber = "+919845067890",
                contactPerson = "Priya Narayanan",
                latitude = 12.9550,
                longitude = 77.6150,
                rating = 4.8f,
                reviewCount = 312,
                acceptedMaterials = listOf("mixed_circuit_boards", "laptop_motherboard_green_boards", "used_smartphone_mobile_pcbs", "power_supplies_transformers", "small_home_appliances", "monitors_heavy_appliance_scrap"),
                workingHours = "9:30 AM - 6:30 PM",
                isVerified = true,
                distanceKm = 3.6,
                paymentModes = listOf("Instant UPI", "NEFT / RTGS"),
                minPickupKg = 2.0,
                facilityType = "Govt Authorized E-Waste Facility • Green Certificate",
                isOpenNow = true,
                primaryCategory = MaterialCategory.ELECTRONICS
            ),
            Recycler(
                id = "recycler_delhi_ncr_battery",
                name = "EcoTron Battery & High-Tech E-Waste Hub",
                address = "B-12, Phase II Industrial Area, Bangalore / NCR",
                phone = "+91 98991 87654",
                whatsappNumber = "+919899187654",
                contactPerson = "Imran Khan & Sons",
                latitude = 12.9820,
                longitude = 77.6100,
                rating = 4.7f,
                reviewCount = 165,
                acceptedMaterials = listOf("lead_batteries_inverter_vehicle", "lithium_ion_batteries", "lead_battery_plates", "power_supplies_transformers"),
                workingHours = "9:00 AM - 7:30 PM",
                isVerified = true,
                distanceKm = 2.1,
                paymentModes = listOf("Immediate Bank Transfer", "UPI", "Cash"),
                minPickupKg = 10.0,
                facilityType = "Certified Battery Smelting & E-Waste Recovery",
                isOpenNow = true,
                primaryCategory = MaterialCategory.ELECTRONICS
            ),
            Recycler(
                id = "recycler_balaji_plastics",
                name = "Shree Balaji Plastics & Film Recyclers",
                address = "12th Cross, Peenya 2nd Stage Industrial Area, Bangalore",
                phone = "+91 93122 88990",
                whatsappNumber = "+919312288990",
                contactPerson = "Mukesh Agarwal",
                latitude = 12.9890,
                longitude = 77.5750,
                rating = 4.6f,
                reviewCount = 98,
                acceptedMaterials = listOf("pet_bottles_water_soda", "hard_plastics_buckets_crates", "hdpe_containers_shampoo_detergent", "pp_plastics_furniture_tubs", "soft_plastics_polyethylene_film", "ldpe_milk_pouch_film_clean"),
                workingHours = "8:30 AM - 7:00 PM",
                isVerified = true,
                distanceKm = 4.2,
                paymentModes = listOf("Instant UPI", "Cash"),
                minPickupKg = 20.0,
                facilityType = "Hydraulic Baling Yard • Bulk Purchases",
                isOpenNow = true,
                primaryCategory = MaterialCategory.PLASTIC
            ),
            Recycler(
                id = "recycler_prism_glass",
                name = "Prism Glass Works & Cullet Depot",
                address = "Plot 14, Glass Factory Road, Peenya 1st Stage, Bangalore",
                phone = "+91 98455 11223",
                whatsappNumber = "+919845511223",
                contactPerson = "Suresh Hegde",
                latitude = 12.9710,
                longitude = 77.5890,
                rating = 4.7f,
                reviewCount = 114,
                acceptedMaterials = listOf("broken_glass_ceramics"),
                workingHours = "9:00 AM - 6:30 PM",
                isVerified = true,
                distanceKm = 1.8,
                paymentModes = listOf("Instant UPI", "Cash"),
                minPickupKg = 15.0,
                facilityType = "Govt Certified Cullet & Glass Bottle Processing Center",
                isOpenNow = true,
                primaryCategory = MaterialCategory.GLASS
            ),
            Recycler(
                id = "recycler_vardhman_textile",
                name = "Vardhman Textile & Garment Upcyclers",
                address = "Shop 45, Weaver's Colony, Commercial Street Area, Bangalore",
                phone = "+91 97420 33445",
                whatsappNumber = "+919742033445",
                contactPerson = "Rajinder Vardhman",
                latitude = 12.9680,
                longitude = 77.6080,
                rating = 4.6f,
                reviewCount = 76,
                acceptedMaterials = listOf("old_clothes_textiles"),
                workingHours = "9:30 AM - 7:30 PM",
                isVerified = true,
                distanceKm = 2.3,
                paymentModes = listOf("Instant UPI", "Cash"),
                minPickupKg = 10.0,
                facilityType = "Textile Shredding & Fibre Reclamation Depot",
                isOpenNow = true,
                primaryCategory = MaterialCategory.TEXTILE
            ),
            Recycler(
                id = "recycler_royal_tyres",
                name = "Royal Tyres & Rubber Reclaimers",
                address = "Gate 3, Heavy Vehicle Complex, Bommasandra, Bangalore",
                phone = "+91 97110 56789",
                whatsappNumber = "+919711056789",
                contactPerson = "Gurpreet Singh",
                latitude = 12.9480,
                longitude = 77.6250,
                rating = 4.5f,
                reviewCount = 82,
                acceptedMaterials = listOf("fibre_tyres_rubber", "rubber_soles_old_shoes"),
                workingHours = "9:00 AM - 7:00 PM",
                isVerified = true,
                distanceKm = 5.8,
                paymentModes = listOf("Cash", "IMPS"),
                minPickupKg = 50.0,
                facilityType = "Industrial Rubber Processing Center",
                isOpenNow = true,
                primaryCategory = MaterialCategory.RUBBER
            ),
            Recycler(
                id = "recycler_cleancity_packaging",
                name = "CleanCity Multi-Packaging & Refuse Processing",
                address = "Gate 6, Solid Waste Management Yard, Whitefield, Bangalore",
                phone = "+91 96112 55667",
                whatsappNumber = "+919611255667",
                contactPerson = "Kavita Reddy",
                latitude = 12.9590,
                longitude = 77.6200,
                rating = 4.5f,
                reviewCount = 58,
                acceptedMaterials = listOf("multi_layered_packaging", "composite_packaging_tetra_pak"),
                workingHours = "8:00 AM - 6:00 PM",
                isVerified = true,
                distanceKm = 3.1,
                paymentModes = listOf("Instant UPI", "Cash"),
                minPickupKg = 25.0,
                facilityType = "Authorized Multi-Layer & Refuse Derived Fuel Depot",
                isOpenNow = true,
                primaryCategory = MaterialCategory.OTHER
            )
        )
    }

    private fun generateMockTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                id = "txn_8941",
                receiptNumber = "KC-REC-2026-8941",
                transactionDate = Instant.now().minusSeconds(7200),
                recyclerId = "recycler_sharma",
                recyclerName = "Sharma Metal & Mandi Scrap Traders",
                items = listOf(
                    TransactionItem(
                        materialId = "copper_wires_pipes",
                        materialName = "Copper (Wires/Pipes)",
                        weightKg = 24.5,
                        pricePerKg = 650.0,
                        amount = 15925.0
                    ),
                    TransactionItem(
                        materialId = "brass_utensils_fittings",
                        materialName = "Brass (Utensils/Fittings)",
                        weightKg = 12.0,
                        pricePerKg = 450.0,
                        amount = 5400.0
                    ),
                    TransactionItem(
                        materialId = "iron_loha_scrap",
                        materialName = "Iron / Loha (Scrap)",
                        weightKg = 85.0,
                        pricePerKg = 28.0,
                        amount = 2380.0
                    )
                ),
                totalWeight = 121.5,
                totalAmount = 23705.0,
                status = TransactionStatus.COMPLETED,
                handoverConfirmed = true,
                paymentMethod = PaymentMethod.UPI,
                paymentReference = "UPI/428901239812/HDFC",
                weighbridgeSlipNo = "WB-SEC18-4912",
                grossWeight = 122.2,
                tareWeight = 0.7,
                notes = "Weighed on digital platform scale. Certified pure copper and brass lot. Payout settled instantly via UPI."
            ),
            Transaction(
                id = "txn_8942",
                receiptNumber = "KC-REC-2026-8942",
                transactionDate = Instant.now().minusSeconds(18000),
                recyclerId = "recycler_delhi_ncr_battery",
                recyclerName = "EcoTron Battery & High-Tech E-Waste Hub",
                items = listOf(
                    TransactionItem(
                        materialId = "lead_batteries_inverter_vehicle",
                        materialName = "Lead Batteries (Inverter/Vehicle)",
                        weightKg = 48.0,
                        pricePerKg = 85.0,
                        amount = 4080.0
                    ),
                    TransactionItem(
                        materialId = "lithium_ion_batteries",
                        materialName = "Lithium-ion Batteries (Phone/Laptop)",
                        weightKg = 15.0,
                        pricePerKg = 65.0,
                        amount = 975.0
                    )
                ),
                totalWeight = 63.0,
                totalAmount = 5055.0,
                status = TransactionStatus.CONFIRMED,
                otp = "5829",
                handoverConfirmed = false,
                paymentMethod = PaymentMethod.CASH,
                weighbridgeSlipNo = "WB-MYP-8821",
                scheduledTestingTime = "Today, 10:30 AM - 11:30 AM (Driver Arrived)",
                grossWeight = 63.8,
                tareWeight = 0.8,
                notes = "Dealer pickup van arrived. Inspect material and verify 4-digit Handover OTP (5829) with driver."
            ),
            Transaction(
                id = "txn_8943",
                receiptNumber = "KC-REC-2026-8943",
                transactionDate = Instant.now().minusSeconds(43200),
                recyclerId = "recycler_bengaluru_eco",
                recyclerName = "Bengaluru Eco Paper & Raddi Mart",
                items = listOf(
                    TransactionItem(
                        materialId = "newspapers_raddi",
                        materialName = "Newspapers (Raddi)",
                        weightKg = 35.0,
                        pricePerKg = 14.0,
                        amount = 490.0
                    ),
                    TransactionItem(
                        materialId = "cardboard_carton_gutta",
                        materialName = "Cardboard / Carton (Gutta)",
                        weightKg = 45.0,
                        pricePerKg = 8.0,
                        amount = 360.0
                    ),
                    TransactionItem(
                        materialId = "books_white_paper",
                        materialName = "Books / White Paper",
                        weightKg = 18.0,
                        pricePerKg = 12.0,
                        amount = 216.0
                    )
                ),
                totalWeight = 98.0,
                totalAmount = 1066.0,
                status = TransactionStatus.PENDING,
                otp = "3194",
                handoverConfirmed = false,
                paymentMethod = PaymentMethod.UPI,
                weighbridgeSlipNo = "",
                scheduledTestingTime = "Today, 02:30 PM - 04:00 PM (Pickup Scheduled)",
                grossWeight = 98.0,
                tareWeight = 0.0,
                notes = "Pickup request scheduled for HSR Layout. Doorstep van on the way."
            ),
            Transaction(
                id = "txn_8944",
                receiptNumber = "KC-REC-2026-8944",
                transactionDate = Instant.now().minusSeconds(172800),
                recyclerId = "recycler_metro_ewaste",
                recyclerName = "Metro E-Waste & Circuit Dismantlers",
                items = listOf(
                    TransactionItem(
                        materialId = "used_smartphone_pcbs",
                        materialName = "Used Smartphone PCBs",
                        weightKg = 4.5,
                        pricePerKg = 1100.0,
                        amount = 4950.0
                    ),
                    TransactionItem(
                        materialId = "mixed_circuit_boards",
                        materialName = "Mixed Circuit Boards",
                        weightKg = 12.0,
                        pricePerKg = 35.0,
                        amount = 420.0
                    )
                ),
                totalWeight = 16.5,
                totalAmount = 5370.0,
                status = TransactionStatus.COMPLETED,
                handoverConfirmed = true,
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                paymentReference = "IMPS/601928471920/ICICI",
                weighbridgeSlipNo = "WB-ECITY-1044",
                scheduledTestingTime = "Tested & Verified: 06 Sept, 08:30 AM",
                grossWeight = 16.8,
                tareWeight = 0.3,
                notes = "Green electronic recycling certified. Direct IMPS transfer credited to collector bank account."
            ),
            Transaction(
                id = "txn_8945",
                receiptNumber = "KC-REC-2026-8945",
                transactionDate = Instant.now().minusSeconds(345600),
                recyclerId = "recycler_gupta_copper",
                recyclerName = "Gupta Brothers Copper & Wire Mart",
                items = listOf(
                    TransactionItem(
                        materialId = "insulated_copper_wire",
                        materialName = "Insulated Copper Wire",
                        weightKg = 32.0,
                        pricePerKg = 220.0,
                        amount = 7040.0
                    ),
                    TransactionItem(
                        materialId = "aluminium_cans_utensils",
                        materialName = "Aluminium (Cans/Utensils)",
                        weightKg = 18.5,
                        pricePerKg = 130.0,
                        amount = 2405.0
                    )
                ),
                totalWeight = 50.5,
                totalAmount = 9445.0,
                status = TransactionStatus.COMPLETED,
                handoverConfirmed = true,
                paymentMethod = PaymentMethod.CASH,
                weighbridgeSlipNo = "WB-CKPT-3329",
                scheduledTestingTime = "Tested & Verified: 04 Sept, 11:15 AM",
                grossWeight = 51.0,
                tareWeight = 0.5,
                notes = "Tested on copper spectrometer: 99.4% purity confirmed. Spot cash payout handed over."
            ),
            Transaction(
                id = "txn_8946",
                receiptNumber = "KC-REC-2026-8946",
                transactionDate = Instant.now().minusSeconds(604800),
                recyclerId = "recycler_balaji_plastics",
                recyclerName = "Shree Balaji Plastics & Film Recyclers",
                items = listOf(
                    TransactionItem(
                        materialId = "hdpe_containers",
                        materialName = "HDPE Containers (Shampoo/Detergent)",
                        weightKg = 65.0,
                        pricePerKg = 18.0,
                        amount = 1170.0
                    ),
                    TransactionItem(
                        materialId = "pp_plastics",
                        materialName = "PP Plastics (Furniture/Tubs)",
                        weightKg = 42.0,
                        pricePerKg = 14.0,
                        amount = 588.0
                    ),
                    TransactionItem(
                        materialId = "ldpe_milk_pouch_film",
                        materialName = "LDPE Milk Pouch Film (Clean)",
                        weightKg = 25.0,
                        pricePerKg = 16.0,
                        amount = 400.0
                    )
                ),
                totalWeight = 132.0,
                totalAmount = 2158.0,
                status = TransactionStatus.COMPLETED,
                handoverConfirmed = true,
                paymentMethod = PaymentMethod.UPI,
                paymentReference = "UPI/519283746190/SBI",
                weighbridgeSlipNo = "WB-PEENYA-7712",
                grossWeight = 133.5,
                tareWeight = 1.5,
                notes = "Hydraulic baled plastics. Instant UPI settlement verified."
            ),
            Transaction(
                id = "txn_8947",
                receiptNumber = "KC-REC-2026-8947",
                transactionDate = Instant.now().minusSeconds(1209600),
                recyclerId = "recycler_royal_tyres",
                recyclerName = "Royal Tyres & Rubber Reclaimers",
                items = listOf(
                    TransactionItem(
                        materialId = "fibre_tyres_rubber",
                        materialName = "Fibre / Tyres / Rubber",
                        weightKg = 120.0,
                        pricePerKg = 4.0,
                        amount = 480.0
                    )
                ),
                totalWeight = 120.0,
                totalAmount = 480.0,
                status = TransactionStatus.CANCELLED,
                handoverConfirmed = false,
                paymentMethod = PaymentMethod.CASH,
                grossWeight = 120.0,
                tareWeight = 0.0,
                notes = "Pickup cancelled: excessive moisture detected in tyre rubber lot. Collector rescheduled."
            )
        )
    }

    private fun generateMockEarningsSummary(): EarningsSummary {
        val transactions = _transactions.value.filter { it.status == TransactionStatus.COMPLETED }
        val thisMonth = transactions.filter { it.transactionDate.toEpochMilli() > System.currentTimeMillis() - 2592000000L }
        val totalEarnings = transactions.sumOf { it.totalAmount }
        val thisMonthEarnings = thisMonth.sumOf { it.totalAmount }
        val totalWeight = transactions.sumOf { it.totalWeight }
        return EarningsSummary(
            totalEarnings = totalEarnings,
            thisMonthEarnings = thisMonthEarnings,
            lastMonthEarnings = totalEarnings * 0.7,
            totalTransactions = transactions.size,
            thisMonthTransactions = thisMonth.size,
            averagePerTransaction = if (transactions.isNotEmpty()) totalEarnings / transactions.size else 0.0,
            topMaterial = "Paper & Cardboard",
            totalWeightKg = totalWeight
        )
    }

    private fun generateMockKhataEntries(): List<KhataEntry> {
        val recyclers = _nearbyRecyclers.value
        return (0..25).map { i ->
            val recycler = recyclers[Random.nextInt(recyclers.size)]
            val material = Material.ALL_MATERIALS[Random.nextInt(Material.ALL_MATERIALS.size)]
            val weight = Random.nextDouble(10.0, 200.0)
            val rate = material.basePricePerKg * (0.9 + Random.nextDouble() * 0.2)
            val amount = weight * rate
            KhataEntry(
                id = "khata_$i",
                date = Instant.now().minusSeconds(Random.nextInt(7776000).toLong()),
                recyclerName = recycler.name,
                materialName = material.name,
                weightKg = weight,
                ratePerKg = rate,
                amount = amount,
                paymentStatus = PaymentStatus.entries[Random.nextInt(PaymentStatus.entries.size)],
                paymentDate = if (Random.nextBoolean()) Instant.now().minusSeconds(Random.nextInt(86400).toLong()) else null
            )
        }.sortedByDescending { it.date }
    }

    private fun generateMockUser(): User {
        return User(
            id = "user_1",
            name = "Rajesh Kumar",
            phone = "+91 98765 43210",
            email = "rajesh.kumar@email.com",
            address = "123 Green Street, Sector 15, Gurgaon, Haryana 122001",
            isVerified = true
        )
    }

    private fun updateEarningsAfterTransaction(transaction: Transaction) {
        if (transaction.status == TransactionStatus.COMPLETED) {
            val current = _earningsSummary.value ?: return
            _earningsSummary.value = EarningsSummary(
                totalEarnings = current.totalEarnings + transaction.totalAmount,
                thisMonthEarnings = current.thisMonthEarnings + transaction.totalAmount,
                lastMonthEarnings = current.lastMonthEarnings,
                totalTransactions = current.totalTransactions + 1,
                thisMonthTransactions = current.thisMonthTransactions + 1,
                averagePerTransaction = (current.totalEarnings + transaction.totalAmount) / (current.totalTransactions + 1),
                topMaterial = current.topMaterial,
                totalWeightKg = current.totalWeightKg + transaction.totalWeight
            )
        }
    }
}
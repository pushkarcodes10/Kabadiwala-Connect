package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SafetyTip(
    val id: String,
    val title: String,
    val description: String,
    val category: SafetyCategory,
    val iconName: String,
    val priority: Int = 0
) {
    companion object {
        val ALL_TIPS = listOf(
            SafetyTip(
                "wear_gloves",
                "Wear Protective Gloves",
                "Always wear thick gloves when handling scrap materials to protect against cuts, sharp edges, and harmful substances.",
                SafetyCategory.PERSONAL_PROTECTION,
                "gloves",
                1
            ),
            SafetyTip(
                "wear_boots",
                "Wear Closed-Toe Boots",
                "Protect your feet with sturdy, closed-toe shoes or boots. Avoid sandals or open footwear in scrap yards.",
                SafetyCategory.PERSONAL_PROTECTION,
                "boots",
                2
            ),
            SafetyTip(
                "segregate_materials",
                "Segregate Materials Properly",
                "Separate different types of materials (paper, plastic, metal, electronics) before selling. Mixed loads fetch lower prices.",
                SafetyCategory.MATERIAL_HANDLING,
                "segregate",
                1
            ),
            SafetyTip(
                "remove_batteries",
                "Remove Batteries from Electronics",
                "Before selling electronic items, remove all batteries. Lithium-ion batteries can cause fires if damaged.",
                SafetyCategory.MATERIAL_HANDLING,
                "battery",
                2
            ),
            SafetyTip(
                "check_sharp_edges",
                "Check for Sharp Edges",
                "Inspect metal scrap for sharp edges, nails, or broken glass. Handle with extra caution and use tools when possible.",
                SafetyCategory.MATERIAL_HANDLING,
                "sharp",
                3
            ),
            SafetyTip(
                "avoid_hazardous",
                "Avoid Hazardous Materials",
                "Never handle chemicals, medical waste, asbestos, radioactive materials, or unknown substances. Report to authorities.",
                SafetyCategory.HAZARDOUS_MATERIALS,
                "hazardous",
                1
            ),
            SafetyTip(
                "no_burning",
                "Never Burn Scrap",
                "Burning scrap releases toxic fumes and is illegal. Always sell to authorized recyclers who follow proper disposal methods.",
                SafetyCategory.HAZARDOUS_MATERIALS,
                "no_fire",
                2
            ),
            SafetyTip(
                "verify_recycler",
                "Verify Recycler Credentials",
                "Only deal with licensed and verified recyclers. Check their credentials and ensure they follow environmental regulations.",
                SafetyCategory.TRANSACTION_SAFETY,
                "verified",
                1
            ),
            SafetyTip(
                "get_receipt",
                "Always Get a Receipt",
                "Insist on a proper receipt with weight, rate, and total amount. Digital records help in dispute resolution.",
                SafetyCategory.TRANSACTION_SAFETY,
                "receipt",
                2
            ),
            SafetyTip(
                "use_digital_payment",
                "Prefer Digital Payments",
                "Use UPI or bank transfers for larger amounts. Avoid carrying large cash amounts. Digital trail provides security.",
                SafetyCategory.TRANSACTION_SAFETY,
                "digital_payment",
                3
            ),
            SafetyTip(
                "keep_area_clean",
                "Keep Collection Area Clean",
                "Maintain a clean, organized collection area. Prevent water accumulation to avoid mosquito breeding and accidents.",
                SafetyCategory.ENVIRONMENTAL,
                "clean",
                1
            ),
            SafetyTip(
                "store_safely",
                "Store Materials Safely",
                "Stack materials securely to prevent toppling. Keep flammable materials away from heat sources. Ensure proper ventilation.",
                SafetyCategory.ENVIRONMENTAL,
                "storage",
                2
            )
        )
    }
}

enum class SafetyCategory {
    PERSONAL_PROTECTION,
    MATERIAL_HANDLING,
    HAZARDOUS_MATERIALS,
    TRANSACTION_SAFETY,
    ENVIRONMENTAL
}

@Serializable
data class EmergencyContact(
    val name: String,
    val phone: String,
    val description: String,
    val category: EmergencyCategory
) {
    companion object {
        val DEFAULT_CONTACTS = listOf(
            EmergencyContact("Police", "100", "Emergency police assistance", EmergencyCategory.POLICE),
            EmergencyContact("Fire Department", "101", "Fire emergency and rescue", EmergencyCategory.FIRE),
            EmergencyContact("Ambulance", "108", "Medical emergency services", EmergencyCategory.MEDICAL),
            EmergencyContact("Disaster Management", "1078", "Natural disaster response", EmergencyCategory.DISASTER),
            EmergencyContact("Women Helpline", "1091", "Women safety and support", EmergencyCategory.WOMEN_SAFETY),
            EmergencyContact("Child Helpline", "1098", "Child protection services", EmergencyCategory.CHILD_SAFETY),
            EmergencyContact("Environmental Help", "1800-11-6000", "Pollution and environmental complaints", EmergencyCategory.ENVIRONMENT)
        )
    }
}

enum class EmergencyCategory {
    POLICE, FIRE, MEDICAL, DISASTER, WOMEN_SAFETY, CHILD_SAFETY, ENVIRONMENT
}
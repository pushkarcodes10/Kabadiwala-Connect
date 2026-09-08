package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Material(
    val id: String,
    val name: String,
    val category: MaterialCategory,
    val iconName: String,
    val basePricePerKg: Double,
    val minPricePerKg: Double = basePricePerKg,
    val maxPricePerKg: Double = basePricePerKg,
    val unit: String = "kg",
    val description: String = "",
    val acceptedTypes: List<String> = emptyList(),
    val imageUrl: String = ""
) {
    val rateRange: String
        get() = when {
            basePricePerKg == 0.0 && maxPricePerKg == 0.0 -> "₹0/kg (Free/Recycle)"
            minPricePerKg == maxPricePerKg -> "₹%.0f/kg".format(basePricePerKg)
            else -> "₹%.0f - ₹%.0f/kg".format(minPricePerKg, maxPricePerKg)
        }

    fun getImageDrawableRes(): Int {
        return when (id) {
            "copper_wires_pipes", "heavy_copper_cable_scrap" -> com.kabadiwalaconnect.R.drawable.img_copper
            "brass_utensils_fittings" -> com.kabadiwalaconnect.R.drawable.img_brass
            "insulated_copper_wire" -> com.kabadiwalaconnect.R.drawable.img_wire
            "aluminium_cans_utensils", "aluminium_foil_food_cans" -> com.kabadiwalaconnect.R.drawable.img_aluminium
            "stainless_steel" -> com.kabadiwalaconnect.R.drawable.img_aluminium
            "iron_loha_scrap", "cast_iron_scrap" -> com.kabadiwalaconnect.R.drawable.img_iron
            "tin_metal_cans" -> com.kabadiwalaconnect.R.drawable.img_tin
            "lead_scrap", "zinc_scrap_castings" -> com.kabadiwalaconnect.R.drawable.img_iron
            "newspapers_raddi", "sorted_white_notebooks_ledger" -> com.kabadiwalaconnect.R.drawable.img_newspaper
            "books_white_paper", "kraft_paper_heavy_brown_bags" -> com.kabadiwalaconnect.R.drawable.img_books
            "cardboard_carton_gutta", "soiled_paper_food_packaging" -> com.kabadiwalaconnect.R.drawable.img_cardboard
            "pet_bottles_water_soda", "polycarbonate_water_cans_cds" -> com.kabadiwalaconnect.R.drawable.img_pet_bottles
            "hard_plastics_buckets_crates", "pp_plastics_furniture_tubs", "abs_plastics_tv_casings" -> com.kabadiwalaconnect.R.drawable.img_hard_plastic
            "soft_plastics_polyethylene_film", "ldpe_milk_pouch_film_clean", "pet_strap_banding", "acrylic_scrap", "nylon_scrap_ropes", "thin_pvc_flex_banners_vinyl", "heavy_pvc_pipes_thick_gauge", "industrial_compressed_eps", "styrofoam_eps", "thermocol_packaging_parts" -> com.kabadiwalaconnect.R.drawable.img_hard_plastic
            "hdpe_containers_shampoo_detergent" -> com.kabadiwalaconnect.R.drawable.img_hdpe
            "lead_batteries_inverter_vehicle", "lithium_ion_batteries", "lead_battery_plates" -> com.kabadiwalaconnect.R.drawable.img_battery
            "mixed_circuit_boards", "power_supplies_transformers", "small_home_appliances", "monitors_heavy_appliance_scrap" -> com.kabadiwalaconnect.R.drawable.img_circuit_board
            "used_smartphone_mobile_pcbs", "laptop_motherboard_green_boards" -> com.kabadiwalaconnect.R.drawable.img_smartphone_pcb
            "fibre_tyres_rubber", "rubber_soles_old_shoes" -> com.kabadiwalaconnect.R.drawable.img_tyres
            "old_clothes_textiles" -> com.kabadiwalaconnect.R.drawable.img_clothes
            "broken_glass_ceramics" -> com.kabadiwalaconnect.R.drawable.img_glass
            "multi_layered_packaging", "composite_packaging_tetra_pak", "sanitary_biohazard_waste", "contaminated_household_wet_waste" -> com.kabadiwalaconnect.R.drawable.img_packaging
            else -> when (category) {
                MaterialCategory.METAL -> com.kabadiwalaconnect.R.drawable.img_copper
                MaterialCategory.PAPER -> com.kabadiwalaconnect.R.drawable.img_newspaper
                MaterialCategory.PLASTIC -> com.kabadiwalaconnect.R.drawable.img_pet_bottles
                MaterialCategory.ELECTRONICS -> com.kabadiwalaconnect.R.drawable.img_circuit_board
                MaterialCategory.RUBBER -> com.kabadiwalaconnect.R.drawable.img_tyres
                MaterialCategory.TEXTILE -> com.kabadiwalaconnect.R.drawable.img_clothes
                MaterialCategory.GLASS -> com.kabadiwalaconnect.R.drawable.img_glass
                else -> com.kabadiwalaconnect.R.drawable.img_packaging
            }
        }
    }

    companion object {
        val ALL_MATERIALS = listOf(
            // Metals
            Material("copper_wires_pipes", "Copper (Wires/Pipes)", MaterialCategory.METAL, "metal", 650.0, 500.0, 800.0, description = "Clean copper wires, copper pipes, plumbing scrap", imageUrl = "https://images.unsplash.com/photo-1590483736622-39da8af79eb1?w=500&auto=format&fit=crop&q=80"),
            Material("brass_utensils_fittings", "Brass (Utensils/Fittings)", MaterialCategory.METAL, "metal", 450.0, 400.0, 500.0, description = "Brass valves, taps, old utensils, artifacts and ornaments", imageUrl = "https://images.unsplash.com/photo-1584285418504-0052ec44877f?w=500&auto=format&fit=crop&q=80"),
            Material("insulated_copper_wire", "Insulated Copper Wire", MaterialCategory.METAL, "metal", 200.0, 100.0, 300.0, description = "Electrical wiring, power cords with PVC/rubber insulation", imageUrl = "https://images.unsplash.com/photo-1544724569-5f546fd6f2b5?w=500&auto=format&fit=crop&q=80"),
            Material("aluminium_cans_utensils", "Aluminium (Cans/Utensils)", MaterialCategory.METAL, "metal", 135.0, 100.0, 170.0, description = "Beverage cans, cooking pots, utensils, window section scrap", imageUrl = "https://images.unsplash.com/photo-1605600659908-0ef719419d41?w=500&auto=format&fit=crop&q=80"),
            Material("stainless_steel", "Stainless Steel", MaterialCategory.METAL, "metal", 48.0, 40.0, 55.0, description = "Kitchen sinks, steel utensils, cutlery, industrial scrap", imageUrl = "https://images.unsplash.com/photo-1584905066893-7d5c142ba4e1?w=500&auto=format&fit=crop&q=80"),
            Material("iron_loha_scrap", "Iron / Loha (Scrap)", MaterialCategory.METAL, "metal", 28.0, 22.0, 35.0, description = "Rebar, angle iron, window grilles, rusted household iron", imageUrl = "https://images.unsplash.com/photo-1535813547-99c456a41d4a?w=500&auto=format&fit=crop&q=80"),
            Material("tin_metal_cans", "Tin / Metal Cans", MaterialCategory.METAL, "metal", 20.0, 15.0, 25.0, description = "Cooking oil tins, ghee tins, food cans, aerosol containers", imageUrl = "https://images.unsplash.com/photo-1586864387967-d02ef85d93e8?w=500&auto=format&fit=crop&q=80"),
            Material("lead_scrap", "Lead Scrap", MaterialCategory.METAL, "metal", 140.0, 120.0, 160.0, description = "Lead sinkers, pipes, sheets, wheel balancing weights", imageUrl = "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122?w=500&auto=format&fit=crop&q=80"),
            Material("zinc_scrap_castings", "Zinc Scrap / Castings", MaterialCategory.METAL, "metal", 210.0, 180.0, 240.0, description = "Die-cast parts, carburettor bodies, zinc roof sheets", imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80"),
            Material("heavy_copper_cable_scrap", "Heavy Copper Cable Scrap", MaterialCategory.METAL, "metal", 600.0, 550.0, 650.0, description = "Industrial thick feeder cables, transformer windings", imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500&auto=format&fit=crop&q=80"),
            Material("cast_iron_scrap", "Cast Iron Scrap", MaterialCategory.METAL, "metal", 33.0, 28.0, 38.0, description = "Heavy machine castings, motor housings, iron manhole covers", imageUrl = "https://images.unsplash.com/photo-1535813547-99c456a41d4a?w=500&auto=format&fit=crop&q=80"),
            Material("aluminium_foil_food_cans", "Aluminium Foil / Food Cans", MaterialCategory.METAL, "metal", 40.0, 30.0, 50.0, description = "Kitchen foil containers, beverage twist-off caps, clean foil", imageUrl = "https://images.unsplash.com/photo-1605600659908-0ef719419d41?w=500&auto=format&fit=crop&q=80"),

            // Paper & Cardboard
            Material("newspapers_raddi", "Newspapers (Raddi)", MaterialCategory.PAPER, "paper", 13.0, 10.0, 16.0, description = "Daily newspapers, printed raddi in dry condition", imageUrl = "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=500&auto=format&fit=crop&q=80"),
            Material("books_white_paper", "Books / White Paper", MaterialCategory.PAPER, "paper", 10.5, 8.0, 13.0, description = "School textbooks, notebooks, office printing sheets", imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&auto=format&fit=crop&q=80"),
            Material("cardboard_carton_gutta", "Cardboard / Carton (Gutta)", MaterialCategory.PAPER, "paper", 7.0, 4.0, 10.0, description = "Corrugated shipping boxes, brown packing cartons, gutta", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),
            Material("sorted_white_notebooks_ledger", "Sorted White Notebooks / Ledger", MaterialCategory.PAPER, "paper", 15.0, 12.0, 18.0, description = "White ledger books, account registers, computer printouts", imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&auto=format&fit=crop&q=80"),
            Material("kraft_paper_heavy_brown_bags", "Kraft Paper / Heavy Brown Bags", MaterialCategory.PAPER, "paper", 8.5, 6.0, 11.0, description = "Brown cement bags, grocery carry bags, kraft carton lining", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),
            Material("soiled_paper_food_packaging", "Soiled Paper / Food Packaging", MaterialCategory.PAPER, "paper", 0.0, 0.0, 0.0, description = "Oily pizza boxes, tea-stained paper, food trays (Recycling drop-off)", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),

            // Plastics
            Material("pet_bottles_water_soda", "PET Bottles (Water/Soda)", MaterialCategory.PLASTIC, "plastic", 15.0, 10.0, 20.0, description = "Transparent plastic bottles, mineral water, soft drink containers", imageUrl = "https://images.unsplash.com/photo-1572935706599-248a17bc4975?w=500&auto=format&fit=crop&q=80"),
            Material("hard_plastics_buckets_crates", "Hard Plastics (Buckets/Crates)", MaterialCategory.PLASTIC, "plastic", 11.5, 8.0, 15.0, description = "Broken plastic buckets, mugs, storage crates, drums", imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=500&auto=format&fit=crop&q=80"),
            Material("soft_plastics_polyethylene_film", "Soft Plastics / Polyethylene Film", MaterialCategory.PLASTIC, "plastic", 8.0, 6.0, 10.0, description = "Plastic wrappers, bubble wrap, plastic packaging sheets", imageUrl = "https://images.unsplash.com/photo-1618477461853-cf6ed80faba5?w=500&auto=format&fit=crop&q=80"),
            Material("hdpe_containers_shampoo_detergent", "HDPE Containers (Shampoo/Detergent)", MaterialCategory.PLASTIC, "plastic", 17.0, 12.0, 22.0, description = "Rigid plastic bottles, shampoo, disinfectant & cleaner cans", imageUrl = "https://images.unsplash.com/photo-1585751119414-ef2636f8aede?w=500&auto=format&fit=crop&q=80"),
            Material("pp_plastics_furniture_tubs", "PP Plastics (Furniture/Tubs)", MaterialCategory.PLASTIC, "plastic", 14.0, 10.0, 18.0, description = "Plastic chairs, tables, laundry baskets, storage tubs", imageUrl = "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=500&auto=format&fit=crop&q=80"),
            Material("ldpe_milk_pouch_film_clean", "LDPE Milk Pouch Film (Clean)", MaterialCategory.PLASTIC, "plastic", 16.0, 12.0, 20.0, description = "Clean washed milk bags, grocery stretch wrap", imageUrl = "https://images.unsplash.com/photo-1618477461853-cf6ed80faba5?w=500&auto=format&fit=crop&q=80"),
            Material("pet_strap_banding", "PET Strap / Banding", MaterialCategory.PLASTIC, "plastic", 9.0, 6.0, 12.0, description = "Industrial strapping strips, crate binding plastic straps", imageUrl = "https://images.unsplash.com/photo-1572935706599-248a17bc4975?w=500&auto=format&fit=crop&q=80"),
            Material("acrylic_scrap", "Acrylic Scrap", MaterialCategory.PLASTIC, "plastic", 47.5, 35.0, 60.0, description = "Plexiglass sheets, acrylic signage cutouts, display stands", imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=500&auto=format&fit=crop&q=80"),
            Material("heavy_pvc_pipes_thick_gauge", "Heavy PVC Pipes (Thick gauge)", MaterialCategory.PLASTIC, "plastic", 17.0, 12.0, 22.0, description = "Plumbing rigid PVC pipes, agricultural conduit pipes", imageUrl = "https://images.unsplash.com/photo-1585751119414-ef2636f8aede?w=500&auto=format&fit=crop&q=80"),
            Material("thin_pvc_flex_banners_vinyl", "Thin PVC Flex Banners / Vinyl", MaterialCategory.PLASTIC, "plastic", 2.0, 1.0, 3.0, description = "Printed advertising banners, vinyl floor offcuts", imageUrl = "https://images.unsplash.com/photo-1618477461853-cf6ed80faba5?w=500&auto=format&fit=crop&q=80"),
            Material("polycarbonate_water_cans_cds", "Polycarbonate (Water Cans/CDs)", MaterialCategory.PLASTIC, "plastic", 35.0, 25.0, 45.0, description = "20L water dispenser cans, compact discs, transparent sheets", imageUrl = "https://images.unsplash.com/photo-1572935706599-248a17bc4975?w=500&auto=format&fit=crop&q=80"),
            Material("abs_plastics_tv_casings", "ABS Plastics (TV Casings)", MaterialCategory.PLASTIC, "plastic", 27.5, 20.0, 35.0, description = "Monitor bezels, television back covers, printer housings", imageUrl = "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=500&auto=format&fit=crop&q=80"),
            Material("nylon_scrap_ropes", "Nylon Scrap / Ropes", MaterialCategory.PLASTIC, "plastic", 22.5, 15.0, 30.0, description = "Synthetic fishing ropes, industrial nylon filaments, netting", imageUrl = "https://images.unsplash.com/photo-1544724569-5f546fd6f2b5?w=500&auto=format&fit=crop&q=80"),
            Material("industrial_compressed_eps", "Industrial Compressed EPS", MaterialCategory.PLASTIC, "plastic", 15.0, 10.0, 20.0, description = "Densified styrofoam blocks, compacted EPS packaging", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),
            Material("styrofoam_eps", "Styrofoam (EPS)", MaterialCategory.PLASTIC, "plastic", 0.0, 0.0, 0.0, description = "Uncompressed thermocol blocks from electronics packaging", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),
            Material("thermocol_packaging_parts", "Thermocol Packaging Parts", MaterialCategory.PLASTIC, "plastic", 0.0, 0.0, 0.0, description = "Appliance protective corner blocks and cushions", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),

            // Electronics & Batteries
            Material("lead_batteries_inverter_vehicle", "Lead Batteries (Inverter/Vehicle)", MaterialCategory.ELECTRONICS, "battery", 90.0, 70.0, 110.0, description = "UPS batteries, automobile and inverter wet-cell batteries", imageUrl = "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=500&auto=format&fit=crop&q=80"),
            Material("mixed_circuit_boards", "Mixed Circuit Boards", MaterialCategory.ELECTRONICS, "electronics", 32.5, 20.0, 45.0, description = "Low to medium-grade PCBs from household gadgets and toys", imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500&auto=format&fit=crop&q=80"),
            Material("monitors_heavy_appliance_scrap", "Monitors / Heavy Appliance Scrap", MaterialCategory.ELECTRONICS, "electronics", 20.0, 15.0, 25.0, description = "Washing machine carcasses, CRT/LED shells, microwave bodies", imageUrl = "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=500&auto=format&fit=crop&q=80"),
            Material("used_smartphone_mobile_pcbs", "Used Smartphone / Mobile PCBs", MaterialCategory.ELECTRONICS, "electronics", 1150.0, 800.0, 1500.0, description = "High-grade smartphone circuit boards, gold-plated connectors", imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500&auto=format&fit=crop&q=80"),
            Material("laptop_motherboard_green_boards", "Laptop / Motherboard Green Boards", MaterialCategory.ELECTRONICS, "electronics", 350.0, 250.0, 450.0, description = "Computer motherboards, server boards, RAM and GPU circuit boards", imageUrl = "https://images.unsplash.com/photo-1555680202-c86f0e12f086?w=500&auto=format&fit=crop&q=80"),
            Material("power_supplies_transformers", "Power Supplies / Transformers", MaterialCategory.ELECTRONICS, "electronics", 62.5, 45.0, 80.0, description = "SMPS units, adapters, copper-wound step-down transformers", imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500&auto=format&fit=crop&q=80"),
            Material("lithium_ion_batteries", "Lithium-ion Batteries (Phone/Laptop)", MaterialCategory.ELECTRONICS, "battery", 65.0, 40.0, 90.0, description = "Rechargeable phone batteries, 18650 cells, power bank packs", imageUrl = "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=500&auto=format&fit=crop&q=80"),
            Material("small_home_appliances", "Small Home Appliances (Mixers/Irons)", MaterialCategory.ELECTRONICS, "electronics", 22.5, 15.0, 30.0, description = "Mixer grinders, electric irons, toasters, small electric fans", imageUrl = "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=500&auto=format&fit=crop&q=80"),
            Material("lead_battery_plates", "Lead Battery Plates", MaterialCategory.ELECTRONICS, "battery", 110.0, 90.0, 130.0, description = "Extracted lead plates and posts from disassembled batteries", imageUrl = "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=500&auto=format&fit=crop&q=80"),

            // Rubber & Tyres
            Material("fibre_tyres_rubber", "Fibre / Tyres / Rubber", MaterialCategory.RUBBER, "rubber", 4.0, 3.0, 5.0, description = "Automotive tyres, cycle tyres, conveyor belts, rubber sheets", imageUrl = "https://images.unsplash.com/photo-1578844251758-2f71da64c96f?w=500&auto=format&fit=crop&q=80"),
            Material("rubber_soles_old_shoes", "Rubber Soles / Old Shoes", MaterialCategory.RUBBER, "rubber", 3.5, 2.0, 5.0, description = "Worn out shoe soles, vulcanized rubber remnants", imageUrl = "https://images.unsplash.com/photo-1578844251758-2f71da64c96f?w=500&auto=format&fit=crop&q=80"),

            // Textiles
            Material("old_clothes_textiles", "Old Clothes / Textiles", MaterialCategory.TEXTILE, "textile", 4.0, 2.0, 6.0, description = "Discarded wearable clothes, bedsheets, cotton rags", imageUrl = "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=500&auto=format&fit=crop&q=80"),

            // Glass
            Material("broken_glass_ceramics", "Broken Glass / Ceramics", MaterialCategory.GLASS, "glass", 0.5, 0.0, 1.0, description = "Broken beverage bottles, window panes, cullet, ceramics", imageUrl = "https://images.unsplash.com/photo-1516762689617-e1cffcef479d?w=500&auto=format&fit=crop&q=80"),

            // Other / Packaging / Non-Recyclable
            Material("multi_layered_packaging", "Multi-Layered Packaging (Chips packets)", MaterialCategory.OTHER, "other", 1.0, 0.0, 2.0, description = "Metallized chip packets, biscuit wrappers, shiny pouches", imageUrl = "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&auto=format&fit=crop&q=80"),
            Material("composite_packaging_tetra_pak", "Composite Packaging (Tetra Pak)", MaterialCategory.OTHER, "other", 1.0, 0.0, 2.0, description = "Juice and dairy aseptic cartons (paper-poly-alu laminate)", imageUrl = "https://images.unsplash.com/photo-1530587191325-3db32d826c18?w=500&auto=format&fit=crop&q=80"),
            Material("sanitary_biohazard_waste", "Sanitary / Biohazard Waste", MaterialCategory.OTHER, "hazardous", 0.0, 0.0, 0.0, description = "Medical waste, diapers, sanitary pads (Non-recyclable)", imageUrl = ""),
            Material("contaminated_household_wet_waste", "Contaminated Household / Wet Waste", MaterialCategory.OTHER, "hazardous", 0.0, 0.0, 0.0, description = "Non-recyclable mixed wet waste and contaminated refuse", imageUrl = "")
        )

        fun getById(id: String): Material? = ALL_MATERIALS.find { it.id == id }
    }
}

enum class MaterialCategory {
    ALL, METAL, PLASTIC, PAPER, ELECTRONICS, GLASS, TEXTILE, RUBBER, OTHER
}
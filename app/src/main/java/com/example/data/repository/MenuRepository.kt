package com.example.data.repository

import com.example.data.model.DietType
import com.example.data.model.FoodCategory
import com.example.data.model.MenuItem
import com.example.data.model.PortionOption

object MenuRepository {
    val items: List<MenuItem> = listOf(
        // Chowmein
        MenuItem(
            id = "chow_veg",
            name = "Veg Chowmein",
            hindiName = "वेज चाउमीन",
            category = FoodCategory.CHOWMEIN,
            dietType = DietType.VEG,
            description = "Wok-tossed noodles with fresh shredded cabbage, bell peppers, carrots, garlic, and savory desi-Chinese sauces.",
            portions = listOf(
                PortionOption("Half", 40),
                PortionOption("Full", 70)
            ),
            isBestseller = true,
            isSpicy = false,
            rating = 4.8,
            imageUrl = "https://images.unsplash.com/photo-1585032226651-759b368d7246?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "chow_egg",
            name = "Egg Chowmein",
            hindiName = "एग चाउमीन",
            category = FoodCategory.CHOWMEIN,
            dietType = DietType.EGG,
            description = "Street-style noodles scrambled with fresh eggs, onions, green chillies, and aromatic spice blend.",
            portions = listOf(
                PortionOption("Half", 50),
                PortionOption("Full", 90)
            ),
            isBestseller = false,
            isSpicy = true,
            rating = 4.7,
            imageUrl = "https://images.unsplash.com/photo-1612927601601-6638404737ce?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "chow_chicken",
            name = "Chicken Chowmein",
            hindiName = "चिकन चाउमीन",
            category = FoodCategory.CHOWMEIN,
            dietType = DietType.NON_VEG,
            description = "Juicy tender chicken chunks wok-tossed with high-flame street noodles, soy sauce, and spring onions.",
            portions = listOf(
                PortionOption("Half", 70),
                PortionOption("Full", 120)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.9,
            imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "chow_paneer",
            name = "Paneer Chowmein",
            hindiName = "पनीर चाउमीन",
            category = FoodCategory.CHOWMEIN,
            dietType = DietType.VEG,
            description = "Soft fresh cottage cheese cubes seasoned with oriental spices, stir-fried with crunchy veggies and noodles.",
            portions = listOf(
                PortionOption("Half", 60),
                PortionOption("Full", 100)
            ),
            isBestseller = false,
            isSpicy = false,
            rating = 4.7,
            imageUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&w=600&q=80"
        ),

        // Rolls
        MenuItem(
            id = "roll_veg",
            name = "Veg Roll",
            hindiName = "वेज रोल",
            category = FoodCategory.ROLLS,
            dietType = DietType.VEG,
            description = "Crispy lachha paratha stuffed with sauteed spiced veggies, crunchy onions, chaat masala, and mint chutney.",
            portions = listOf(
                PortionOption("Regular", 40)
            ),
            isBestseller = false,
            isSpicy = false,
            rating = 4.6,
            imageUrl = "https://images.unsplash.com/photo-1606471191009-63994c53433b?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "roll_double_egg",
            name = "Double Egg Roll",
            hindiName = "डबल एग रोल",
            category = FoodCategory.ROLLS,
            dietType = DietType.EGG,
            description = "Golden crisp paratha layered with two farm-fresh eggs, onions, green chillies, lemon zest, and signature sauce.",
            portions = listOf(
                PortionOption("Regular", 60)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.9,
            imageUrl = "https://images.unsplash.com/photo-1509722747041-616f39b57569?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "roll_chicken",
            name = "Chicken Roll",
            hindiName = "चिकन रोल",
            category = FoodCategory.ROLLS,
            dietType = DietType.NON_VEG,
            description = "Flaky paratha packed with marinated tawa chicken boti, sliced pickled onions, and tangy sauce.",
            portions = listOf(
                PortionOption("Regular", 80)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.9,
            imageUrl = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "roll_paneer",
            name = "Paneer Roll",
            hindiName = "पनीर रोल",
            category = FoodCategory.ROLLS,
            dietType = DietType.VEG,
            description = "Char-grilled spicy paneer cubes rolled in crisp flaky paratha with zesty onion salad and sauces.",
            portions = listOf(
                PortionOption("Regular", 70)
            ),
            isBestseller = false,
            isSpicy = false,
            rating = 4.7,
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=600&q=80"
        ),

        // Starters
        MenuItem(
            id = "starter_chicken_chilli",
            name = "Chicken Chilli",
            hindiName = "चिकन चिल्ली",
            category = FoodCategory.STARTERS,
            dietType = DietType.NON_VEG,
            description = "Crispy fried chicken chunks tossed in fiery red chilli-garlic sauce with crunchy capsicum and onions.",
            portions = listOf(
                PortionOption("Half", 90),
                PortionOption("Full", 160)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.9,
            imageUrl = "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "starter_chilli_paneer",
            name = "Chilli Paneer",
            hindiName = "चिल्ली पनीर",
            category = FoodCategory.STARTERS,
            dietType = DietType.VEG,
            description = "Crispy battered paneer cubes wok-glazed in dark soy, green chillies, garlic ginger, and bell peppers.",
            portions = listOf(
                PortionOption("Half", 80),
                PortionOption("Full", 140)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.8,
            imageUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=600&q=80"
        ),

        // Momos
        MenuItem(
            id = "momos_veg",
            name = "Veg Momos",
            hindiName = "वेज मोमोज़",
            category = FoodCategory.MOMOS,
            dietType = DietType.VEG,
            description = "Steamed thin-wrapper dumplings filled with finely chopped cabbage, carrots, spring onions, and garlic.",
            portions = listOf(
                PortionOption("6 pcs", 40),
                PortionOption("10 pcs", 60)
            ),
            isBestseller = false,
            isSpicy = false,
            rating = 4.7,
            imageUrl = "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "momos_chicken",
            name = "Chicken Momos",
            hindiName = "चिकन मोमोज़",
            category = FoodCategory.MOMOS,
            dietType = DietType.NON_VEG,
            description = "Juicy minced chicken infused with ginger, coriander, and Himalayan herbs steamed to perfection.",
            portions = listOf(
                PortionOption("6 pcs", 60),
                PortionOption("10 pcs", 90)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.9,
            imageUrl = "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "momos_paneer",
            name = "Paneer Momos",
            hindiName = "पनीर मोमोज़",
            category = FoodCategory.MOMOS,
            dietType = DietType.VEG,
            description = "Delicate steamed dumplings stuffed with spiced crumbled paneer and fresh herbs. Accompanied by spicy chutney.",
            portions = listOf(
                PortionOption("6 pcs", 50),
                PortionOption("10 pcs", 80)
            ),
            isBestseller = false,
            isSpicy = false,
            rating = 4.8,
            imageUrl = "https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?auto=format&fit=crop&w=600&q=80"
        ),

        // Soups
        MenuItem(
            id = "soup_hot_sour_veg",
            name = "Hot & Sour Veg Soup",
            hindiName = "हॉट एंड सोर वेज सूप",
            category = FoodCategory.SOUPS,
            dietType = DietType.VEG,
            description = "Zesty, thick and warming Indo-Chinese soup loaded with diced veggies, mushrooms, black pepper, and chili oil.",
            portions = listOf(
                PortionOption("Regular", 40)
            ),
            isBestseller = false,
            isSpicy = true,
            rating = 4.6,
            imageUrl = "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=600&q=80"
        ),
        MenuItem(
            id = "soup_chicken_egg",
            name = "Chicken / Egg Soup",
            hindiName = "चिकन / एग सूप",
            category = FoodCategory.SOUPS,
            dietType = DietType.NON_VEG,
            description = "Rich simmered broth with shredded chicken ribbons, silky egg drop swirls, and cracked black pepper.",
            portions = listOf(
                PortionOption("Regular", 60)
            ),
            isBestseller = true,
            isSpicy = true,
            rating = 4.8,
            imageUrl = "https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?auto=format&fit=crop&w=600&q=80"
        )
    )
}

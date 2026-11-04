package com.example.data

/**
 * Central Brand Identity Configuration (Version 6.0).
 *
 * Keep APPROVED_BRAND_NAME configurable in one central location so it can be
 * updated later without modifying individual screens or game logic.
 * Note: The brand name is provisional until formal trademark and brand clearance is completed.
 */
object BrandConfig {
    const val APPROVED_BRAND_NAME: String = "PlayKids"
    const val BRAND_PRIMARY_PART: String = "Play"
    const val BRAND_SECONDARY_PART: String = "Kids"
    const val BRAND_TAGLINE: String = "Play • Match • Learn • Grow"
    const val BRAND_TAGLINE_BN: String = "খেলি • মেলাই • শিখি • এগিয়ে যাই"
    const val APP_VERSION: String = "6.0"
    const val PROVISIONAL_BRAND_NOTICE: String =
        "Provisional Brand Identity (PlayKids v6.0) — Configurable centrally via BrandConfig.kt pending final trademark clearance."
    const val PRIVACY_AND_SAFETY_NOTICE_BN: String =
        "সম্পূর্ণ অফলাইন শিক্ষামূলক অ্যাপ। কোনো ব্যক্তিগত তথ্য সংগ্রহ, ট্র্যাকিং বা বাহ্যিক বিজ্ঞাপন নেই।"
}

/**
 * All 8 required professional logo variants for PlayKids:
 * Learning Star + Open Book + Subtle Play Symbol (▶).
 */
enum class PlayKidsLogoVariant(val titleEn: String, val titleBn: String) {
    PRIMARY_HORIZONTAL("Primary Horizontal Logo", "প্রধান আনুভূমিক লোগো"),
    COMPACT("Compact Header Logo", "কমপ্যাক্ট লোগো"),
    ANDROID_APP_ICON("Android App Icon", "অ্যান্ড্রয়েড অ্যাপ আইকন"),
    TRANSPARENT_PNG("Transparent Badge Logo", "স্বচ্ছ ব্যাকগ্রাউন্ড লোগো"),
    SCALABLE_VECTOR("Scalable Vector Emblem", "স্কেলেবল ভেক্টর লোগো"),
    LIGHT_BACKGROUND("Light-Background Version", "হালকা ব্যাকগ্রাউন্ড সংস্করণ"),
    DARK_BACKGROUND("Dark-Background Version", "গাঢ় ব্যাকগ্রাউন্ড সংস্করণ"),
    MONOCHROME("Monochrome Version", "এক রঙা (Monochrome) সংস্করণ")
}

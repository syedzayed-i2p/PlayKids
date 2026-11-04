package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class LearningMode(
    val id: String,
    val titleBn: String,
    val badgeBn: String,
    val subtitleBn: String
) {
    CORE_CURRICULUM(
        id = "core",
        titleBn = "মূল প্রতিযোগিতা অনুশীলন",
        badgeBn = "মোড ক • মূল পাঠ্যক্রম",
        subtitleBn = "জাতীয় প্রতিযোগিতার ৪টি মূল খেলা ও নিয়ম"
    ),
    EXTENDED_PRACTICE(
        id = "extended",
        titleBn = "অতিরিক্ত দক্ষতা অনুশীলন",
        badgeBn = "মোড খ • ১০০+ লেভেল",
        subtitleBn = "৫টি অধ্যায়, ২৫টি করে লেভেল, ২৫টি আকৃতি ও ২০০টি চ্যালেঞ্জ"
    )
}

enum class GameType(
    val id: String,
    val serialName: String,
    val titleEn: String,
    val titleBn: String,
    val shortSubtitle: String,
    val themeColor: Color,
    val lightBg: Color,
    val accentBorder: Color
) {
    EMOJI_MATCH(
        id = "emoji_match",
        serialName = "খেলা-ক",
        titleEn = "Emoji Matching",
        titleBn = "ছবি মিলাও",
        shortSubtitle = "একই ছবি খুঁজে বের করো",
        themeColor = PlayBlue,
        lightBg = Color(0xFFFFF8D6),
        accentBorder = Color(0xFFF59E0B)
    ),
    SHAPE_MATCH(
        id = "shape_match",
        serialName = "খেলা-খ",
        titleEn = "Shape Matching",
        titleBn = "আকৃতি মিলাও",
        shortSubtitle = "সঠিক আকৃতি চিনে মিলাও",
        themeColor = LearningMint,
        lightBg = Color(0xFFDFFAF0),
        accentBorder = Color(0xFF22C55E)
    ),
    COLOR_SHAPE_CHALLENGE(
        id = "color_shape_challenge",
        serialName = "খেলা-ঘ",
        titleEn = "Colour + Shape",
        titleBn = "রং ও আকৃতি",
        shortSubtitle = "রং ও আকৃতি মিলিয়ে খেলা",
        themeColor = CreativePurple,
        lightBg = Color(0xFFF3E8FF),
        accentBorder = Color(0xFFA855F7)
    ),
    COLOR_PAPER_MATCH(
        id = "color_paper_match",
        serialName = "খেলা-গ",
        titleEn = "Colour Paper",
        titleBn = "রঙিন কাগজ মিলাও",
        shortSubtitle = "একই রঙের কাগজ বক্সে রাখো",
        themeColor = JoyCoral,
        lightBg = Color(0xFFFFE5EC),
        accentBorder = Color(0xFFEC4899)
    )
}

enum class KidColor(
    val id: String,
    val nameBn: String,
    val color: Color,
    val lightColor: Color,
    val darkColor: Color,
    val isCore: Boolean,
    val patternSymbol: String
) {
    // 5 Original Core Colors (Strictly Preserved)
    RED("red", "লাল", KidRed, KidRedLight, KidRedDark, true, "●"),
    YELLOW("yellow", "হলুদ", KidYellow, KidYellowLight, KidYellowDark, true, "★"),
    BLUE("blue", "নীল", KidBlue, KidBlueLight, KidBlueDark, true, "▲"),
    GREEN("green", "সবুজ", KidGreen, KidGreenLight, KidGreenDark, true, "■"),
    ORANGE("orange", "কমলা", KidOrange, KidOrangeLight, KidOrangeDark, true, "◆"),

    // 5 Extended Colors (Mode B)
    PINK("pink", "গোলাপি", KidPink, KidPinkLight, KidPinkDark, false, "♥"),
    PURPLE("purple", "বেগুনি", KidPurple, KidPurpleLight, KidPurpleDark, false, "✦"),
    BROWN("brown", "বাদামি", KidBrown, KidBrownLight, KidBrownDark, false, "⬟"),
    BLACK("black", "কালো", KidBlack, KidBlackLight, KidBlackDark, false, "⬢"),
    WHITE("white", "সাদা", KidWhite, KidWhiteLight, KidWhiteDark, false, "○");

    companion object {
        val CORE_COLORS: List<KidColor> = entries.filter { it.isCore }
        val EXTENDED_ONLY_COLORS: List<KidColor> = entries.filter { !it.isCore }
        val ALL_10_COLORS: List<KidColor> = entries.toList()
    }
}

enum class KidShape(
    val id: String,
    val nameBn: String,
    val isCore: Boolean
) {
    // 5 Original Core Shapes (Strictly Preserved)
    CIRCLE("circle", "বৃত্ত", true),
    TRIANGLE("triangle", "ত্রিভুজ", true),
    SQUARE("square", "বর্গ", true),
    STAR("star", "তারা", true),
    HEART("heart", "হার্ট", true),

    // 20 Additional 2D Shapes (Total = 25 2D Shapes)
    RECTANGLE("rectangle", "আয়তক্ষেত্র", false),
    OVAL("oval", "ডিম্বাকৃতি", false),
    DIAMOND("diamond", "হীরক আকৃতি", false),
    SEMICIRCLE("semicircle", "অর্ধবৃত্ত", false),
    CRESCENT("crescent", "অর্ধচন্দ্র", false),
    PENTAGON("pentagon", "পঞ্চভুজ", false),
    HEXAGON("hexagon", "ষড়ভুজ", false),
    OCTAGON("octagon", "অষ্টভুজ", false),
    TRAPEZOID("trapezoid", "ট্রাপিজিয়াম", false),
    PARALLELOGRAM("parallelogram", "সমান্তর চতুর্ভুজ", false),
    CROSS("cross", "যোগচিহ্ন", false),
    ARROW("arrow", "তীরচিহ্ন", false),
    KITE("kite", "ঘুড়ি আকৃতি", false),
    HEPTAGON("heptagon", "সপ্তভুজ", false),
    DECAGON("decagon", "দশভুজ", false),
    RIGHT_TRIANGLE("right_triangle", "সমকোণী ত্রিভুজ", false),
    RING("ring", "বলয় আকৃতি", false),
    FOUR_POINT_STAR("four_point_star", "চারকোণা তারা", false),
    CLOUD_SHAPE("cloud_shape", "মেঘ আকৃতি", false),
    SHIELD("shield", "ঢাল আকৃতি", false);

    companion object {
        val CORE_SHAPES: List<KidShape> = entries.filter { it.isCore }
        val ALL_20_SHAPES: List<KidShape> = entries.take(20)
        val ALL_25_SHAPES: List<KidShape> = entries.toList()
        val ADDITIONAL_20_SHAPES: List<KidShape> = entries.filter { !it.isCore }
        // Compatibility aliases
        val COMBO_10_SHAPES: List<KidShape> = entries.take(10)
        val ALL_15_SHAPES: List<KidShape> = entries.take(15)
    }
}

/**
 * 8 3D Solid Shapes for the Dedicated 3D Shape Discovery Section
 */
enum class Kid3DShape(
    val id: String,
    val nameBn: String,
    val realWorldExampleBn: String,
    val descriptionBn: String,
    val primaryColor: Color
) {
    SPHERE("sphere", "গোলক", "ফুটবল বা মার্বেলের মতো গোল", "চারদিক থেকেই মসৃণ ও গোলাকার ত্রিমাত্রিক আকৃতি।", KidRed),
    CUBE("cube", "ঘনক", "লুডুর ছক্কা বা রুবিকস কিউব", "৬টি সমান বর্গাকার তলবিশিষ্ট ত্রিমাত্রিক আকৃতি।", KidBlue),
    CUBOID("cuboid", "আয়তঘন", "বই, ইট বা জুতার বাক্স", "৬টি আয়তাকার তলবিশিষ্ট ত্রিমাত্রিক বাক্স আকৃতি।", KidGreen),
    CYLINDER("cylinder", "বেলন", "পানির গ্লাস বা কৌটা", "উপরে ও নিচে বৃত্তাকার এবং মাঝখানে গোলাকার স্তম্ভ।", KidOrange),
    CONE("cone", "শঙ্কু", "আইসক্রিম কোন বা জন্মদিনের টুপি", "নিচে বৃত্তাকার তল এবং উপরে একটি সূঁচালো শীর্ষবিন্দু।", KidPink),
    PYRAMID("pyramid", "পিরামিড", "মিশরের পিরামিড বা তাঁবু", "চারপাশে ত্রিভুজাকার ঢালু তল যা উপরে এক বিন্দুতে মেলে।", KidYellowDark),
    TRIANGULAR_PRISM("tri_prism", "ত্রিভুজাকার প্রিজম", "খেলার তাঁবু বা প্রিজম কাঁচ", "দুই প্রান্তে ত্রিভুজ এবং পাশে আয়তাকার তল।", KidPurple),
    RECTANGULAR_PRISM("rect_prism", "আয়তাকার প্রিজম", "আলমারি বা লম্বা বাক্স", "সমান্তরাল আয়তাকার প্রান্তবিশিষ্ট ত্রিমাত্রিক ঘনবস্তু।", LearningMint);

    val realLifeExampleBn: String
        get() = realWorldExampleBn

    companion object {
        val ALL_3D_SHAPES: List<Kid3DShape> = entries.toList()
    }
}

enum class PictureCategory(val titleBn: String) {
    CORE_EMOJI("মূল ইমোজি"),
    OPTIONAL_EMOJI("ঐচ্ছিক ইমোজি"),
    FRUITS("ফল"),
    ANIMALS("প্রাণী"),
    NATURE("প্রকৃতি"),
    VEHICLES("যানবাহন"),
    DAILY_OBJECTS("দৈনন্দিন বস্তু")
}

data class PictureCardItem(
    val id: String,
    val symbol: String,
    val nameBn: String,
    val category: PictureCategory,
    val badgeColor: Color
)

data class ColorShapeCard(
    val id: String,
    val color: KidColor,
    val shape: KidShape,
    val nameBn: String = "${color.nameBn} ${shape.nameBn}"
)

data class ChapterInfo(
    val chapterNumber: Int,
    val titleEn: String,
    val subtitleEn: String,
    val titleBn: String,
    val subtitleBn: String,
    val levelRange: IntRange,
    val themeColor: Color,
    val badgeIcon: String
)

data class LevelDefinition(
    val gameType: GameType,
    val levelNumber: Int, // 1..25
    val chapterNumber: Int, // 1..5
    val titleBn: String,
    val objectiveBn: String,
    val voiceInstructionBn: String,
    val taskCount: Int,
    val optionCount: Int,
    val isCoreCurriculumCompatible: Boolean
)

object CurriculumLibrary {
    // 9 Core Reference Emojis (Strictly Preserved)
    val CORE_EMOJIS: List<Pair<String, String>> = listOf(
        "😀" to "হাসিমুখ",
        "❤️" to "হার্ট",
        "👍" to "থাম্বস আপ",
        "⭐" to "তারা",
        "😂" to "হাসতে হাসতে চোখে পানি",
        "😎" to "সানগ্লাস মুখ",
        "🌸" to "ফুল",
        "⚽" to "ফুটবল",
        "🍎" to "আপেল"
    )

    // 3 Optional Extension Emojis (Preserved)
    val EXTRA_EMOJIS: List<Pair<String, String>> = listOf(
        "🍌" to "কলা",
        "☀️" to "সূর্য",
        "🦋" to "প্রজাপতি"
    )

    val ALL_EMOJIS: List<Pair<String, String>> = CORE_EMOJIS + EXTRA_EMOJIS

    // 50+ Familiar Objects across Fruits, Animals, Nature, Vehicles, and Daily Objects
    val FRUITS = listOf(
        PictureCardItem("mango", "🥭", "আম", PictureCategory.FRUITS, Color(0xFFFFF8E1)),
        PictureCardItem("banana", "🍌", "কলা", PictureCategory.FRUITS, Color(0xFFFFFDE7)),
        PictureCardItem("apple", "🍎", "আপেল", PictureCategory.FRUITS, Color(0xFFFFEBEE)),
        PictureCardItem("orange_fruit", "🍊", "কমলা", PictureCategory.FRUITS, Color(0xFFFFF3E0)),
        PictureCardItem("grapes", "🍇", "আঙুর", PictureCategory.FRUITS, Color(0xFFF3E5F5)),
        PictureCardItem("watermelon", "🍉", "তরমুজ", PictureCategory.FRUITS, Color(0xFFFFEBEE)),
        PictureCardItem("guava", "🍐", "পেয়ারা", PictureCategory.FRUITS, Color(0xFFE8F5E9)),
        PictureCardItem("strawberry", "🍓", "স্ট্রবেরি", PictureCategory.FRUITS, Color(0xFFFCE4EC))
    )

    val FRUITS_AND_FOOD = FRUITS

    val ANIMALS = listOf(
        PictureCardItem("panda", "🐼", "পান্ডা", PictureCategory.ANIMALS, Color(0xFFE8F5E9)),
        PictureCardItem("cat", "🐱", "বিড়াল", PictureCategory.ANIMALS, Color(0xFFFFF8E1)),
        PictureCardItem("dog", "🐶", "কুকুর", PictureCategory.ANIMALS, Color(0xFFEFEBE9)),
        PictureCardItem("elephant", "🐘", "হাতি", PictureCategory.ANIMALS, Color(0xFFECEFF1)),
        PictureCardItem("lion", "🦁", "সিংহ", PictureCategory.ANIMALS, Color(0xFFFFF3E0)),
        PictureCardItem("tiger", "🐯", "বাঘ", PictureCategory.ANIMALS, Color(0xFFFFF8E1)),
        PictureCardItem("fish", "🐟", "মাছ", PictureCategory.ANIMALS, Color(0xFFE3F2FD)),
        PictureCardItem("bird", "🐦", "পাখি", PictureCategory.ANIMALS, Color(0xFFE8F5E9)),
        PictureCardItem("rabbit", "🐰", "খরগোশ", PictureCategory.ANIMALS, Color(0xFFFCE4EC)),
        PictureCardItem("butterfly", "🦋", "প্রজাপতি", PictureCategory.ANIMALS, Color(0xFFEDE7F6))
    )

    val NATURE = listOf(
        PictureCardItem("sun", "☀️", "সূর্য", PictureCategory.NATURE, Color(0xFFFFFDE7)),
        PictureCardItem("moon", "🌙", "চাঁদ", PictureCategory.NATURE, Color(0xFFEDE7F6)),
        PictureCardItem("cloud", "☁️", "মেঘ", PictureCategory.NATURE, Color(0xFFE3F2FD)),
        PictureCardItem("rain", "🌧️", "বৃষ্টি", PictureCategory.NATURE, Color(0xFFE1F5FE)),
        PictureCardItem("tree", "🌳", "গাছ", PictureCategory.NATURE, Color(0xFFE8F5E9)),
        PictureCardItem("leaf", "🍃", "পাতা", PictureCategory.NATURE, Color(0xFFF1F8E9)),
        PictureCardItem("flower", "🌸", "ফুল", PictureCategory.NATURE, Color(0xFFFCE4EC)),
        PictureCardItem("rainbow", "🌈", "রংধনু", PictureCategory.NATURE, Color(0xFFEAF4FF))
    )

    val VEHICLES = listOf(
        PictureCardItem("car", "🚗", "গাড়ি", PictureCategory.VEHICLES, Color(0xFFFFEBEE)),
        PictureCardItem("bus", "🚌", "বাস", PictureCategory.VEHICLES, Color(0xFFFFFDE7)),
        PictureCardItem("train", "🚆", "ট্রেন", PictureCategory.VEHICLES, Color(0xFFE8EAF6)),
        PictureCardItem("airplane", "✈️", "বিমান", PictureCategory.VEHICLES, Color(0xFFE3F2FD)),
        PictureCardItem("boat", "⛵", "নৌকা", PictureCategory.VEHICLES, Color(0xFFE0F7FA)),
        PictureCardItem("bicycle", "🚲", "সাইকেল", PictureCategory.VEHICLES, Color(0xFFE8F5E9))
    )

    val DAILY_OBJECTS = listOf(
        PictureCardItem("book", "📚", "বই", PictureCategory.DAILY_OBJECTS, Color(0xFFEDE7F6)),
        PictureCardItem("ball", "⚽", "বল", PictureCategory.DAILY_OBJECTS, Color(0xFFECEFF1)),
        PictureCardItem("clock", "⏰", "ঘড়ি", PictureCategory.DAILY_OBJECTS, Color(0xFFFFF3E0)),
        PictureCardItem("cup", "☕", "কাপ", PictureCategory.DAILY_OBJECTS, Color(0xFFEFEBE9)),
        PictureCardItem("spoon", "🥄", "চামচ", PictureCategory.DAILY_OBJECTS, Color(0xFFF5F5F5)),
        PictureCardItem("glasses", "👓", "চশমা", PictureCategory.DAILY_OBJECTS, Color(0xFFE3F2FD)),
        PictureCardItem("shoe", "👟", "জুতা", PictureCategory.DAILY_OBJECTS, Color(0xFFFFEBEE)),
        PictureCardItem("umbrella", "☂️", "ছাতা", PictureCategory.DAILY_OBJECTS, Color(0xFFF3E5F5)),
        PictureCardItem("bag", "🎒", "ব্যাগ", PictureCategory.DAILY_OBJECTS, Color(0xFFFFF8E1)),
        PictureCardItem("toy", "🧸", "খেলনা", PictureCategory.DAILY_OBJECTS, Color(0xFFFFF0ED))
    )

    val VEHICLES_AND_OBJECTS = VEHICLES + DAILY_OBJECTS

    val CORE_PICTURE_ITEMS: List<PictureCardItem> = CORE_EMOJIS.mapIndexed { idx, pair ->
        PictureCardItem("core_$idx", pair.first, pair.second, PictureCategory.CORE_EMOJI, SoftSunshine)
    }

    val EXTENDED_41_PICTURES: List<PictureCardItem> =
        FRUITS + ANIMALS + NATURE + VEHICLES + DAILY_OBJECTS

    val EXTENDED_PICTURE_LIBRARY: List<PictureCardItem> =
        CORE_PICTURE_ITEMS + EXTENDED_41_PICTURES

    // Original 25 Combination Library (5 Core Colors x 5 Core Shapes)
    val ALL_COLOR_SHAPE_CARDS: List<ColorShapeCard> = KidColor.CORE_COLORS.flatMap { color ->
        KidShape.CORE_SHAPES.map { shape ->
            ColorShapeCard(
                id = "${color.id}_${shape.id}",
                color = color,
                shape = shape
            )
        }
    }

    // Complete 200 Combination Library (10 Colors x 20 2D Shapes = 200 Unique Combinations)
    val EXTENDED_200_COLOR_SHAPE_CARDS: List<ColorShapeCard> = KidColor.ALL_10_COLORS.flatMap { color ->
        KidShape.ALL_20_SHAPES.map { shape ->
            ColorShapeCard(
                id = "${color.id}_${shape.id}",
                color = color,
                shape = shape
            )
        }
    }

    // Compatibility subset of 100 combinations (10 Colors x first 10 Shapes)
    val EXTENDED_100_COLOR_SHAPE_CARDS: List<ColorShapeCard> = KidColor.ALL_10_COLORS.flatMap { color ->
        KidShape.COMBO_10_SHAPES.map { shape ->
            ColorShapeCard(
                id = "${color.id}_${shape.id}",
                color = color,
                shape = shape
            )
        }
    }

    // 5 Chapters Structure matching the Reference Image Shield Badges (5 Chapters x 5 Levels = 25 Levels per Game)
    val CHAPTERS: List<ChapterInfo> = listOf(
        ChapterInfo(
            1,
            "Chapter 1",
            "Learn & Identify",
            "অধ্যায় ১: নতুন করে শিখি",
            "খুব সহজ পরিচয় ও প্রথম ধাপ (লেভেল ১–৫)",
            1..5,
            CreativePurple,
            "🌱"
        ),
        ChapterInfo(
            2,
            "Chapter 2",
            "Recognize & Explore",
            "অধ্যায় ২: চিনে দেখি",
            "মৌলিক উপাদান চেনা ও মেলানো (লেভেল ৬–১০)",
            6..10,
            Color(0xFFF59E0B),
            "🔍"
        ),
        ChapterInfo(
            3,
            "Chapter 3",
            "Match & Practice",
            "অধ্যায় ৩: মিলিয়ে দেখি",
            "পূর্ণ মূল পাঠ্যক্রম অনুশীলন (লেভেল ১১–১৫)",
            11..15,
            Color(0xFFEC4899),
            "🧩"
        ),
        ChapterInfo(
            4,
            "Chapter 4",
            "Advanced Practice",
            "অধ্যায় ৪: আরও অনুশীলন",
            "নতুন ছবি, রং ও আকৃতির জগত (লেভেল ১৬–২০)",
            16..20,
            Color(0xFFD97706),
            "🚀"
        ),
        ChapterInfo(
            5,
            "Chapter 5",
            "Mastery Challenge",
            "অধ্যায় ৫: দক্ষতার চ্যালেঞ্জ",
            "চূড়ান্ত চ্যালেঞ্জ ও পূর্ণ দক্ষতা (লেভেল ২১–২৫)",
            21..25,
            Color(0xFFEA580C),
            "🏆"
        )
    )

    fun getLevelsForGame(gameType: GameType): List<LevelDefinition> {
        return (1..25).map { lvl ->
            val chap = ((lvl - 1) / 5) + 1
            when (gameType) {
                GameType.EMOJI_MATCH -> createEmojiLevel(lvl, chap)
                GameType.SHAPE_MATCH -> createShapeLevel(lvl, chap)
                GameType.COLOR_SHAPE_CHALLENGE -> createColorShapeLevel(lvl, chap)
                GameType.COLOR_PAPER_MATCH -> createColorPaperLevel(lvl, chap)
            }
        }
    }

    private fun createEmojiLevel(lvl: Int, chap: Int): LevelDefinition {
        val (title, obj, pairs) = when (lvl) {
            1 -> Triple("হাসিমুখ ও হার্ট", "২ জোড়া সহজ ইমোজি মেলানো", 2)
            2 -> Triple("তারা ও থাম্বস আপ", "২ জোড়া পরিচিত ইমোজি", 2)
            3 -> Triple("আনন্দের ৩ জোড়া", "৩ জোড়া মূল ইমোজি মেলানো", 3)
            4 -> Triple("ফুল, বল ও আপেল", "৩ জোড়া ছবি ও ইমোজি", 3)
            5 -> Triple("৪ জোড়া ইমোজি বোর্ড", "৪টি ইমোজির স্থান পরিবর্তন", 4)
            6 -> Triple("৫ জোড়া মনোযোগ খেলা", "৫ জোড়া ইমোজি পাশাপাশি রাখা", 5)
            7 -> Triple("৬ জোড়া ইমোজি চ্যালেঞ্জ", "৬ জোড়া কার্ড থেকে সঠিকটি খোঁজা", 6)
            8 -> Triple("দ্রুত ইমোজি শনাক্তকরণ", "৬ জোড়া এলোমেলো কার্ড", 6)
            9 -> Triple("৭ জোড়া মূল ইমোজি", "মূল পাঠ্যক্রমের ৭টি ইমোজি", 7)
            10 -> Triple("৮ জোড়া মূল ইমোজি", "মূল পাঠ্যক্রমের ৮টি ইমোজি", 8)
            11 -> Triple("পূর্ণ ৯টি মূল ইমোজি", "রেফারেন্সের সব ৯টি ইমোজি", 9)
            12 -> Triple("১২ জোড়া মাস্টার বোর্ড", "৯টি মূল ও ৩টি অতিরিক্ত ইমোজি", 12)
            13 -> Triple("মিষ্টি ফলের ঝুড়ি ১", "আম, কলা, আপেল ও কমলা মেলানো", 4)
            14 -> Triple("মিষ্টি ফলের ঝুড়ি ২", "আঙুর, তরমুজ, পেয়ারা ও স্ট্রবেরি", 5)
            15 -> Triple("৮টি ফলের সমাহার", "সব ৮টি পরিচিত ফল মেলানো", 6)
            16 -> Triple("পোষা ও বনের প্রাণী ১", "পান্ডা, বিড়াল, কুকুর, হাতি ও সিংহ", 5)
            17 -> Triple("প্রাণীদের মেলা ২", "বাঘ, মাছ, পাখি, খরগোশ ও প্রজাপতি", 6)
            18 -> Triple("সুন্দর প্রকৃতি ১", "সূর্য, চাঁদ, মেঘ ও বৃষ্টি", 4)
            19 -> Triple("সবুজ প্রকৃতি ২", "গাছ, পাতা, ফুল ও রংধনু", 6)
            20 -> Triple("যানবাহন চেনা", "গাড়ি, বাস, ট্রেন, বিমান, নৌকা ও সাইকেল", 6)
            21 -> Triple("পড়ার টেবিলের বস্তু", "বই, বল, ঘড়ি, চশমা ও ব্যাগ", 5)
            22 -> Triple("দৈনন্দিন জিনিসপত্র", "কাপ, চামচ, জুতা, ছাতা ও খেলনা", 6)
            23 -> Triple("ফল ও প্রাণীর মিশ্রণ", "ভিন্ন ক্যাটাগরির ৮ জোড়া ছবি", 8)
            24 -> Triple("প্রকৃতি ও যানবাহন মিশ্রণ", "৯ জোড়া ছবির মনোযোগ পরীক্ষা", 9)
            else -> Triple("মহাপ্রস্তুতি গ্র্যান্ড চ্যালেঞ্জ", "সব ক্যাটাগরির ১০ জোড়া কার্ড", 10)
        }
        return LevelDefinition(
            gameType = GameType.EMOJI_MATCH,
            levelNumber = lvl,
            chapterNumber = chap,
            titleBn = "লেভেল $lvl: $title",
            objectiveBn = obj,
            voiceInstructionBn = "একই ছবিটিতে ট্যাপ করো।",
            taskCount = pairs.coerceIn(5, 8),
            optionCount = pairs,
            isCoreCurriculumCompatible = lvl <= 12
        )
    }

    private fun createShapeLevel(lvl: Int, chap: Int): LevelDefinition {
        val (title, obj, optCount) = when (lvl) {
            1 -> Triple("বৃত্ত ও ত্রিভুজ", "২টি মৌলিক আকৃতি চেনা", 2)
            2 -> Triple("বর্গ ও তারা", "চারকোণা ও তারা আকৃতি মেলানো", 2)
            3 -> Triple("তিন আকৃতির বোর্ড", "বৃত্ত, ত্রিভুজ ও বর্গ", 3)
            4 -> Triple("হার্ট ও তারার খেলা", "৩টি আকৃতির মধ্যে মিল খোঁজা", 3)
            5 -> Triple("৪টি মূল আকৃতি", "৪টি জ্যামিতিক ঘর পূরণ", 4)
            6 -> Triple("পাঁচ আকৃতির পরিচয়", "বৃত্ত, ত্রিভুজ, বর্গ, তারা ও হার্ট", 5)
            7 -> Triple("এলোমেলো ৫ আকৃতি", "স্থান পরিবর্তন করে ৫টি আকৃতি", 5)
            8 -> Triple("আউটলাইন শেইপ বোর্ড", "রেখাচিত্র দেখে ৫টি মূল আকৃতি বসানো", 5)
            9 -> Triple("আয়তক্ষেত্র ও ডিম্বাকৃতি", "নতুন ২টি আকৃতির সাথে পরিচয়", 4)
            10 -> Triple("হীরক ও অর্ধবৃত্ত", "ডায়মন্ড ও অর্ধবৃত্ত চেনা", 4)
            11 -> Triple("অর্ধচন্দ্রের খেলা", "চন্দ্রাকৃতি ও জ্যামিতিক বোর্ড", 5)
            12 -> Triple("পঞ্চভুজ ও ষড়ভুজ", "৫ ও ৬ বাহুর আকৃতি চেনা", 5)
            13 -> Triple("অষ্টভুজ ও ট্রাপিজিয়াম", "উন্নত জ্যামিতিক আকৃতি", 5)
            14 -> Triple("সমান্তর চতুর্ভুজ", "চার বাহুর বিশেষ আকৃতি অনুশীলন", 5)
            15 -> Triple("যোগচিহ্ন ও তীরচিহ্ন", "ক্রস এবং অ্যারো আকৃতি মেলানো", 5)
            16 -> Triple("ঘুড়ি আকৃতির খেলা", "কাইট, হীরক ও ত্রিভুজের তুলনা", 5)
            17 -> Triple("সপ্তভুজ ও দশভুজ", "৭ ও ১০ বাহুর বহুভুজ চেনা", 5)
            18 -> Triple("সমকোণী ত্রিভুজ ও বলয়", "নতুন জ্যামিতিক আকৃতির পরিচয়", 6)
            19 -> Triple("চারকোণা তারা, মেঘ ও ঢাল", "২৫টি আকৃতির সম্পূর্ণ সম্ভার", 6)
            20 -> Triple("ছায়া আকৃতি (Silhouette)", "গাঢ় ছায়া দেখে সঠিক আকৃতি খোঁজা", 5)
            21 -> Triple("ভিন্ন রঙের একই আকৃতি", "রং ভিন্ন হলেও আকৃতি মেলানো", 5)
            22 -> Triple("বড় ও ছোট আকৃতি", "আকারের ভিন্নতায় সঠিক আকৃতি চেনা", 6)
            23 -> Triple("ঘোরানো আকৃতি (Rotated)", "ভিন্ন কোণে রাখা আকৃতি শনাক্তকরণ", 6)
            24 -> Triple("সদৃশ আকৃতির পার্থক্য", "বর্গ, আয়তক্ষেত্র, হীরক ও ঘুড়ি বাছাই", 6)
            else -> Triple("২৫ আকৃতির গ্র্যান্ড মাস্টার", "সব ২৫টি দ্বিমাত্রিক আকৃতির চূড়ান্ত পরীক্ষা", 6)
        }
        return LevelDefinition(
            gameType = GameType.SHAPE_MATCH,
            levelNumber = lvl,
            chapterNumber = chap,
            titleBn = "লেভেল $lvl: $title",
            objectiveBn = obj,
            voiceInstructionBn = "একই আকৃতিটি খুঁজে নিচে ট্যাপ করে বসাও।",
            taskCount = optCount.coerceIn(5, 8),
            optionCount = optCount,
            isCoreCurriculumCompatible = lvl <= 8
        )
    }

    private fun createColorShapeLevel(lvl: Int, chap: Int): LevelDefinition {
        val (title, obj, optCount) = when (lvl) {
            1 -> Triple("লাল, হলুদ ও নীল", "১ম ধাপ: মৌলিক রং চেনা", 3)
            2 -> Triple("সবুজ ও কমলা রং", "১ম ধাপ: ৫টি মূল রং শনাক্তকরণ", 4)
            3 -> Triple("পাঁচ রঙের চ্যালেঞ্জ", "১ম ধাপ: শুধু রং দেখে কার্ড বাছাই", 5)
            4 -> Triple("রঙের দ্রুত বাছাই", "১ম ধাপ: ৫টি মূল রঙের দক্ষতা", 5)
            5 -> Triple("বৃত্ত, ত্রিভুজ ও বর্গ", "২য় ধাপ: শুধু আকৃতি দেখে কার্ড বাছাই", 3)
            6 -> Triple("তারা ও হার্ট খোঁজা", "২য় ধাপ: ৫টি মূল আকৃতি শনাক্তকরণ", 4)
            7 -> Triple("রঙিন কার্ডে আকৃতি চেনা", "২য় ধাপ: রং উপেক্ষা করে আকৃতি নির্বাচন", 5)
            8 -> Triple("পাঁচ আকৃতির পূর্ণ দক্ষতা", "২য় ধাপ: মূল ৫ আকৃতির চূড়ান্ত যাচাই", 5)
            9 -> Triple("রং + আকৃতি শুরু", "৩য় ধাপ: লাল তারা ও হলুদ ত্রিভুজ খোঁজা", 4)
            10 -> Triple("নীল হার্ট ও সবুজ বৃত্ত", "৩য় ধাপ: দুই বৈশিষ্ট্য একসঙ্গে মেলানো", 4)
            11 -> Triple("কমলা বর্গ চ্যালেঞ্জ", "৩য় ধাপ: ২৫টি মূল কম্বিনেশন অনুশীলন", 6)
            12 -> Triple("২৫ কার্ডের চূড়ান্ত ধাপ", "৩য় ধাপ: পূর্ণ ২৫টি মূল কার্ডের পরীক্ষা", 6)
            13 -> Triple("নতুন ৫টি রঙের জগত", "গোলাপি, বেগুনি, বাদামি, কালো ও সাদা", 4)
            14 -> Triple("১০টি রঙের মহড়া", "মূল ৫টি ও নতুন ৫টি রঙের কার্ড", 5)
            15 -> Triple("আয়তক্ষেত্র, ডিম্বাকৃতি ও হীরক", "নতুন আকৃতি শনাক্তকরণ অনুশীলন", 5)
            16 -> Triple("অর্ধবৃত্ত, অর্ধচন্দ্র ও পঞ্চভুজ", "বর্ধিত জ্যামিতিক আকৃতি বাছাই", 5)
            17 -> Triple("ষড়ভুজ, অষ্টভুজ ও ট্রাপিজিয়াম", "বহুভুজ আকৃতির কার্ড নির্বাচন", 6)
            18 -> Triple("যোগচিহ্ন, তীরচিহ্ন ও ঘুড়ি", "নতুন চিহ্ন ও আকৃতি চেনা", 6)
            19 -> Triple("সপ্তভুজ ও দশভুজ পরিচয়", "২০টি আকৃতির সম্পূর্ণ পরিচিতি", 6)
            20 -> Triple("গোলাপি বৃত্ত ও বেগুনি আয়তক্ষেত্র", "নতুন রং ও আকৃতির কম্বিনেশন", 5)
            21 -> Triple("বাদামি হীরক ও কালো তারা", "২০০ কম্বিনেশন লাইব্রেরির অনুশীলন ১", 6)
            22 -> Triple("সাদা ষড়ভুজ ও গোলাপি হার্ট", "২০০ কম্বিনেশন লাইব্রেরির অনুশীলন ২", 6)
            23 -> Triple("লাল তীরচিহ্ন ও নীল ঘুড়ি", "২০ আকৃতি ও ১০ রঙের দ্বৈত পরীক্ষা", 6)
            24 -> Triple("হলুদ অর্ধচন্দ্র ও সবুজ যোগচিহ্ন", "সদৃশ বিকল্প থেকে সঠিক কার্ড বাছাই", 6)
            else -> Triple("২০০ কার্ডের গ্র্যান্ড চ্যালেঞ্জ", "১০ রং × ২০ আকৃতির পূর্ণ দক্ষতা", 6)
        }
        return LevelDefinition(
            gameType = GameType.COLOR_SHAPE_CHALLENGE,
            levelNumber = lvl,
            chapterNumber = chap,
            titleBn = "লেভেল $lvl: $title",
            objectiveBn = obj,
            voiceInstructionBn = "রং এবং আকৃতি দুটোই মিলিয়ে সঠিক কার্ডটি নাও।",
            taskCount = 5,
            optionCount = optCount,
            isCoreCurriculumCompatible = lvl <= 12
        )
    }

    private fun createColorPaperLevel(lvl: Int, chap: Int): LevelDefinition {
        val (title, obj, trayCount) = when (lvl) {
            1 -> Triple("লাল ও হলুদ পেপার", "২টি রঙের ট্রেতে কাগজ রাখা", 2)
            2 -> Triple("নীল ও সবুজ পেপার", "৩টি রঙের ট্রেতে কাগজ মেলানো", 3)
            3 -> Triple("কমলা পেপার যোগ", "৪টি রঙের ট্রের সাথে পরিচয়", 4)
            4 -> Triple("পাঁচ রঙের মূল ট্রে", "লাল, হলুদ, নীল, সবুজ ও কমলা ট্রে", 5)
            5 -> Triple("ট্রে স্থান পরিবর্তন ১", "এলোমেলো ট্রেতে সঠিক কাগজ রাখা", 3)
            6 -> Triple("ট্রে স্থান পরিবর্তন ২", "৪টি পরিবর্তনশীল রঙের ট্রে", 4)
            7 -> Triple("পাঁচ ট্রের বিন্যাস", "৫টি মূল রঙের ট্রে নতুন ক্রমে", 5)
            8 -> Triple("দ্রুত পেপার মেলানো", "৫টি রঙিন পেপার সঠিক ঘরে সাজানো", 5)
            9 -> Triple("একাধিক পেপার সর্টিং ১", "৬টি রঙিন কাগজ ৫টি ট্রেতে রাখা", 5)
            10 -> Triple("একাধিক পেপার সর্টিং ২", "৮টি এলোমেলো কাগজ সঠিক ট্রেতে ভাগ করা", 5)
            11 -> Triple("১০টি পেপারের বান্ডিল", "প্রতিটি মূল রঙে ২টি করে কাগজ সাজানো", 5)
            12 -> Triple("মূল কালার পেপার মাস্টার", "পাঁচ রঙের পূর্ণাঙ্গ ট্রে চ্যালেঞ্জ", 5)
            13 -> Triple("গোলাপি ও বেগুনি পেপার", "নতুন রঙের পেপার ও ট্রের পরিচয়", 4)
            14 -> Triple("বাদামি পেপার যোগ", "নতুন ও মূল রঙের ৫টি ট্রে", 5)
            15 -> Triple("কালো ও সাদা পেপার", "৫টি নতুন রঙের ট্রে অনুশীলন", 5)
            16 -> Triple("নতুন রঙের পেপার সর্টিং", "গোলাপি থেকে সাদা পেপার মেলানো", 5)
            17 -> Triple("৬ রঙের বড় ট্রে বোর্ড", "মূল ও অতিরিক্ত রঙের একত্রে অনুশীলন", 6)
            18 -> Triple("৭ রঙের পেপার সর্টিং", "৭টি রঙের কাগজ ও ট্রে মেলানো", 6)
            19 -> Triple("৮ রঙের পেপার চ্যালেঞ্জ", "৮টি ভিন্ন রঙের কাগজ ও ট্রে", 6)
            20 -> Triple("ব্যাচ পেপার সর্টিং ১", "৯টি মিশ্র রঙের কাগজ সঠিক ট্রেতে রাখা", 6)
            21 -> Triple("ব্যাচ পেপার সর্টিং ২", "১০টি মিশ্র রঙের কাগজ বাছাই", 6)
            22 -> Triple("প্যাটার্ন ও রং মিলানো", "চিহ্ন ও রং দেখে সঠিক ট্রে নির্বাচন", 6)
            23 -> Triple("পরিবর্তনশীল ১০ রঙের ট্রে", "১০টি রঙের মধ্যে দ্রুত বাছাই", 6)
            24 -> Triple("১২টি পেপারের মেগা বান্ডিল", "১০টি রঙের পেপার নির্ভুলভাবে সাজানো", 6)
            else -> Triple("কালার পেপার গ্র্যান্ড মাস্টার", "১০টি রঙের সম্পূর্ণ দক্ষতা যাচাই", 6)
        }
        return LevelDefinition(
            gameType = GameType.COLOR_PAPER_MATCH,
            levelNumber = lvl,
            chapterNumber = chap,
            titleBn = "লেভেল $lvl: $title",
            objectiveBn = obj,
            voiceInstructionBn = "রঙিন কাগজটি নিয়ে একই রঙের ট্রেতে রাখো।",
            taskCount = trayCount.coerceIn(5, 8),
            optionCount = trayCount,
            isCoreCurriculumCompatible = lvl <= 12
        )
    }
}

data class AudioSettingsState(
    val voiceEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val backgroundMusicEnabled: Boolean = false,
    val reducedMotion: Boolean = false
)

data class SkillMasterySnapshot(
    val colorAccuracyMap: Map<String, Int> = emptyMap(),
    val shapeAccuracyMap: Map<String, Int> = emptyMap(),
    val recentResults: List<Boolean> = emptyList()
)

data class GameProgressData(
    val gameType: GameType,
    val unlockedLevel: Int = 1,                  // Core mode level (1..5)
    val coreCompletedLevels: Set<Int> = emptySet(),
    val extendedUnlockedLevel: Int = 1,          // Extended mode level (1..25)
    val completedLevels: Set<Int> = emptySet(),  // Extended mode completed levels (1..25)
    val starsEarned: Int = 0,
    val totalPlayed: Int = 0,
    val independentSuccesses: Int = 0,
    val hintsUsed: Int = 0,
    val physicalCompleted: Boolean = false
)

enum class Screen {
    SPLASH,
    WELCOME,
    HOME,
    MY_PROGRESS,
    LEVEL_MAP,
    GAME_EMOJI,
    GAME_SHAPE,
    GAME_COLOR_SHAPE,
    GAME_COLOR_PAPER,
    PHYSICAL_PRACTICE,
    PARENT_DASHBOARD
}

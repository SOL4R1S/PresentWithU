package com.proclaimer.data

import com.proclaimer.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class SongRepository(private val dataDir: File) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val songsFile: File get() = File(dataDir, "songs.json")
    private val presentationsFile: File get() = File(dataDir, "presentations.json")

    init {
        dataDir.mkdirs()
        if (!songsFile.exists()) songsFile.writeText(json.encodeToString(emptyList<Song>()))
        if (!presentationsFile.exists()) presentationsFile.writeText(json.encodeToString(emptyList<Presentation>()))
    }

    // --- Songs ---

    fun getAllSongs(): List<Song> {
        return try {
            json.decodeFromString<List<Song>>(songsFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getSong(id: String): Song? {
        return getAllSongs().find { it.id == id }
    }

    fun saveSong(song: Song): Song {
        val songs = getAllSongs().toMutableList()
        val idx = songs.indexOfFirst { it.id == song.id }
        val updated = song.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            songs[idx] = updated
        } else {
            songs.add(updated)
        }
        songsFile.writeText(json.encodeToString(songs))
        return updated
    }

    fun deleteSong(id: String) {
        val songs = getAllSongs().toMutableList()
        songs.removeAll { it.id == id }
        songsFile.writeText(json.encodeToString(songs))
    }

    // --- Presentations ---

    fun getAllPresentations(): List<Presentation> {
        return try {
            json.decodeFromString<List<Presentation>>(presentationsFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getPresentation(id: String): Presentation? {
        return getAllPresentations().find { it.id == id }
    }

    fun savePresentation(presentation: Presentation): Presentation {
        val presentations = getAllPresentations().toMutableList()
        val idx = presentations.indexOfFirst { it.id == presentation.id }
        val updated = presentation.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            presentations[idx] = updated
        } else {
            presentations.add(updated)
        }
        presentationsFile.writeText(json.encodeToString(presentations))
        return updated
    }

    fun deletePresentation(id: String) {
        val presentations = getAllPresentations().toMutableList()
        presentations.removeAll { it.id == id }
        presentationsFile.writeText(json.encodeToString(presentations))
    }

    // --- Bible (built-in sample) ---

    fun getBibleVerse(reference: BibleReference): BibleReference {
        val bible = sampleBibleData
        val bookData = bible[reference.book] ?: return reference.copy(text = "Book not found.")
        val chapterData = bookData[reference.chapter] ?: return reference.copy(text = "Chapter not found.")

        val endVerse = reference.verseEnd ?: reference.verseStart
        val verses = (reference.verseStart..endVerse).mapNotNull { chapterData[it] }

        return if (verses.isNotEmpty()) {
            val displayRange = if (reference.verseEnd != null && reference.verseEnd != reference.verseStart) {
                "${reference.verseStart}-${reference.verseEnd}"
            } else {
                "${reference.verseStart}"
            }
            reference.copy(text = verses.joinToString(" ") { it })
        } else {
            reference.copy(text = "Verse not found.")
        }
    }
}

// Sample Bible data for offline use (abbreviated)
private val sampleBibleData: Map<String, Map<Int, Map<Int, String>>> = mapOf(
    "John" to mapOf(
        3 to mapOf(
            16 to "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.",
            17 to "For God did not send his Son into the world to condemn the world, but to save the world through him.",
            18 to "Whoever believes in him is not condemned, but whoever does not believe stands condemned already because they have not believed in the name of God's one and only Son."
        ),
        14 to mapOf(
            6 to "Jesus answered, \"I am the way and the truth and the life. No one comes to the Father except through me.\"",
            7 to "If you really know me, you will know my Father as well. From now on, you do know him and have seen him.",
            27 to "Peace I leave with you; my peace I give you. I do not give to you as the world gives. Do not let your hearts be troubled and do not be afraid."
        )
    ),
    "Psalm" to mapOf(
        23 to mapOf(
            1 to "The LORD is my shepherd, I lack nothing.",
            2 to "He makes me lie down in green pastures, he leads me beside quiet waters,",
            3 to "he refreshes my soul. He guides me along the right paths for his name's sake.",
            4 to "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me.",
            5 to "You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows.",
            6 to "Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the LORD forever."
        ),
        100 to mapOf(
            1 to "Shout for joy to the LORD, all the earth.",
            2 to "Worship the LORD with gladness; come into his presence with singing.",
            3 to "Know that the LORD is God. It is he who made us, and we are his; we are his people, the sheep of his pasture.",
            4 to "Enter his gates with thanksgiving and his courts with praise; give thanks to him and praise his name.",
            5 to "For the LORD is good and his love endures forever; his faithfulness continues through all generations."
        ),
        121 to mapOf(
            1 to "I lift up my eyes to the mountains — where does my help come from?",
            2 to "My help comes from the LORD, the Maker of heaven and earth.",
            3 to "He will not let your foot slip — he who watches over you will not slumber;",
            4 to "indeed, he who watches over Israel will neither slumber nor sleep.",
            5 to "The LORD watches over you — the LORD is your shade at your right hand;",
            6 to "the sun will not harm you by day, nor the moon by night.",
            7 to "The LORD will keep you from all harm — he will watch over your life;",
            8 to "from now and forevermore."
        )
    ),
    "Genesis" to mapOf(
        1 to mapOf(
            1 to "In the beginning God created the heavens and the earth.",
            2 to "Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters.",
            3 to "And God said, \"Let there be light,\" and there was light.",
            31 to "God saw all that he had made, and it was very good. And there was evening, and there was morning — the sixth day."
        )
    ),
    "Matthew" to mapOf(
        5 to mapOf(
            3 to "Blessed are the poor in spirit, for theirs is the kingdom of heaven.",
            4 to "Blessed are those who mourn, for they will be comforted.",
            5 to "Blessed are the meek, for they will inherit the earth.",
            6 to "Blessed are those who hunger and thirst for righteousness, for they will be filled.",
            7 to "Blessed are the merciful, for they will be shown mercy.",
            8 to "Blessed are the pure in heart, for they will see God.",
            9 to "Blessed are the peacemakers, for they will be called children of God.",
            10 to "Blessed are those who are persecuted because of righteousness, for theirs is the kingdom of heaven.",
            14 to "You are the light of the world. A town built on a hill cannot be hidden.",
            15 to "Neither do people light a lamp and put it under a bowl. Instead they put it on its stand, and it gives light to everyone in the house.",
            16 to "In the same way, let your light shine before others, that they may see your good deeds and glorify your Father in heaven."
        ),
        6 to mapOf(
            9 to "This, then, is how you should pray: \"Our Father in heaven, hallowed be your name,",
            10 to "your kingdom come, your will be done, on earth as it is in heaven.",
            11 to "Give us today our daily bread.",
            12 to "And forgive us our debts, as we also have forgiven our debtors.",
            13 to "And lead us not into temptation, but deliver us from the evil one.\"",
            33 to "But seek first his kingdom and his righteousness, and all these things will be given to you as well."
        ),
        28 to mapOf(
            19 to "Therefore go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit,",
            20 to "and teaching them to obey everything I have commanded you. And surely I am with you always, to the very end of the age.\""
        )
    ),
    "Romans" to mapOf(
        8 to mapOf(
            28 to "And we know that in all things God works for the good of those who love him, who have been called according to his purpose.",
            31 to "What, then, shall we say in response to these things? If God is for us, who can be against us?",
            37 to "No, in all these things we are more than conquerors through him who loved us.",
            38 to "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers,",
            39 to "neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord."
        ),
        12 to mapOf(
            1 to "Therefore, I urge you, brothers and sisters, in view of God's mercy, to offer your bodies as a living sacrifice, holy and pleasing to God — this is your true and proper worship.",
            2 to "Do not conform to the pattern of this world, but be transformed by the renewing of your mind. Then you will be able to test and approve what God's will is — his good, pleasing and perfect will."
        )
    ),
    "Philippians" to mapOf(
        4 to mapOf(
            4 to "Rejoice in the Lord always. I will say it again: Rejoice!",
            6 to "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
            7 to "And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
            8 to "Finally, brothers and sisters, whatever is true, whatever is noble, whatever is right, whatever is pure, whatever is lovely, whatever is admirable — if anything is excellent or praiseworthy — think about such things.",
            13 to "I can do all this through him who gives me strength."
        )
    ),
    "Ephesians" to mapOf(
        2 to mapOf(
            8 to "For it is by grace you have been saved, through faith — and this is not from yourselves, it is the gift of God —",
            9 to "not by works, so that no one can boast.",
            10 to "For we are God's handiwork, created in Christ Jesus to do good works, which God prepared in advance for us to do."
        ),
        3 to mapOf(
            20 to "Now to him who is able to do immeasurably more than all we ask or imagine, according to his power that is at work within us,",
            21 to "to him be glory in the church and in Christ Jesus throughout all generations, for ever and ever! Amen."
        )
    ),
    "Isaiah" to mapOf(
        40 to mapOf(
            31 to "But those who hope in the LORD will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint."
        ),
        43 to mapOf(
            1 to "But now, this is what the LORD says — he who created you, Jacob, he who formed you, Israel: \"Do not fear, for I have redeemed you; I have summoned you by name; you are mine.\"",
            2 to "When you pass through the waters, I will be with you; and when you pass through the rivers, they will not sweep over you. When you walk through the fire, you will not be burned; the flames will not set you ablaze."
        ),
        55 to mapOf(
            8 to "\"For my thoughts are not your thoughts, neither are your ways my ways,\" declares the LORD.",
            9 to "\"As the heavens are higher than the earth, so are my ways higher than your ways and my thoughts than your thoughts.\""
        )
    ),
    "Jeremiah" to mapOf(
        29 to mapOf(
            11 to "For I know the plans I have for you,\" declares the LORD, \"plans to prosper you and not to harm you, plans to give you hope and a future.\""
        )
    ),
    "Proverbs" to mapOf(
        3 to mapOf(
            5 to "Trust in the LORD with all your heart and lean not on your own understanding;",
            6 to "in all your ways submit to him, and he will make your paths straight."
        )
    ),
    "Hebrews" to mapOf(
        11 to mapOf(
            1 to "Now faith is confidence in what we hope for and assurance about what we do not see.",
            6 to "And without faith it is impossible to please God, because anyone who comes to him must believe that he exists and that he rewards those who earnestly seek him."
        ),
        12 to mapOf(
            1 to "Therefore, since we are surrounded by such a great cloud of witnesses, let us throw off everything that hinders and the sin that so easily entangles. And let us run with perseverance the race marked out for us,",
            2 to "fixing our eyes on Jesus, the pioneer and perfecter of faith. For the joy set before him he endured the cross, scorning its shame, and sat down at the right hand of the throne of God."
        )
    )
)

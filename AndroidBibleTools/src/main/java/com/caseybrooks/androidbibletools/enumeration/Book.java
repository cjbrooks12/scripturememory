package com.caseybrooks.androidbibletools.enumeration;

import java.util.EnumSet;

/** Enumerates all books of Canonical Bible according to their name and the
 *   number of chapters in that book. Using the static method fromString
 *   will attempt to find the appropriate enumeration given a string of the
 *   book name.
 */

//List of differences that need to be accounted for in different translations
//2 Cor 13 has 13 verses in catholic bibles, when most have 14
public enum Book {
//  Enum                Name                code    Number of verses in each chapter in the book
    Genesis(            "Genesis",          "Ge",   31, 25, 24, 26, 32, 22, 24, 22, 29, 32, 32, 20, 18, 24, 21, 16, 27, 33, 38, 18, 34, 24, 20, 67, 34, 35, 46, 22, 35, 43, 54, 33, 20, 31, 29, 43, 36, 30, 23, 23, 57, 38, 34, 34, 28, 34, 31, 22, 33, 26),
    Exodus(             "Exodus",           "Ex",   22, 25, 22, 31, 23, 30, 29, 28, 35, 29, 10, 51, 22, 31, 27, 36, 16, 27, 25, 26, 37, 30, 33, 18, 40, 37, 21, 43, 46, 38, 18, 35, 23, 35, 35, 38, 29, 31, 43, 38),
    Leviticus(          "Leviticus",        "Le",   17, 16, 17, 35, 26, 23, 38, 36, 24, 20, 47, 8, 59, 57, 33, 34, 16, 30, 37, 27, 24, 33, 44, 23, 55, 46, 34),
    Numbers(            "Numbers",          "Nu",   54, 34, 51, 49, 31, 27, 89, 26, 23, 36, 35, 16, 33, 45, 41, 35, 28, 32, 22, 29, 35, 41, 30, 25, 18, 65, 23, 31, 39, 17, 54, 42, 56, 29, 34, 13),
    Deuteronomy(        "Deuteronomy",      "Dt",   46, 37, 29, 49, 33, 25, 26, 20, 29, 22, 32, 31, 19, 29, 23, 22, 20, 22, 21, 20, 23, 29, 26, 22, 19, 19, 26, 69, 28, 20, 30, 52, 29, 12),
    Joshua(             "Joshua",           "Jos",  18, 24, 17, 24, 15, 27, 26, 35, 27, 43, 23, 24, 33, 15, 63, 10, 18, 28, 51, 9, 45, 34, 16, 33),
    Judges(             "Judges",           "Jdg",  36, 23, 31, 24, 31, 40, 25, 35, 57, 18, 40, 15, 25, 20, 20, 31, 13, 31, 30, 48, 25),
    Ruth(               "Ruth",             "Ru",   22, 23, 18, 22),
    FirstSamuel(        "1 Samuel",         "1Sa",  28, 36, 21, 22, 12, 21, 17, 22, 27, 27, 15, 25, 23, 52, 35, 23, 58, 30, 24, 42, 16, 23, 28, 23, 43, 25, 12, 25, 11, 31, 13),
    SecondSamuel(       "2 Samuel",         "2Sa",  27, 32, 39, 12, 25, 23, 29, 18, 13, 19, 27, 31, 39, 33, 37, 23, 29, 32, 44, 26, 22, 51, 39, 25),
    FirstKings(         "1 Kings",          "1Ki",  53, 46, 28, 20, 32, 38, 51, 66, 28, 29, 43, 33, 34, 31, 34, 34, 24, 46, 21, 42, 29, 54),
    SecondKings(        "2 Kings",          "2Ki",  18, 25, 27, 44, 27, 33, 20, 29, 37, 36, 20, 22, 25, 29, 38, 20, 41, 37, 37, 21, 26, 20, 37, 20, 30),
    FirstChronicles(    "1 Chronicles",     "1Ch",  54, 55, 24, 43, 41, 66, 40, 40, 44, 14, 47, 41, 14, 17, 29, 43, 27, 17, 19, 8, 30, 19, 32, 31, 31, 32, 34, 21, 30),
    SecondChronicles(   "2 Chronicles",     "2Ch",  18, 17, 17, 22, 14, 42, 22, 18, 31, 19, 23, 16, 23, 14, 19, 14, 19, 34, 11, 37, 20, 12, 21, 27, 28, 23, 9, 27, 36, 27, 21, 33, 25, 33, 26, 23),
    Ezra(               "Ezra",             "Ezr",  11, 70, 13, 24, 17, 22, 28, 36, 15, 44),
    Nehemiah(           "Nehemiah",         "Ne",   11, 20, 38, 17, 19, 19, 72, 18, 37, 40, 36, 47, 31),
    Esther(             "Esther",           "Es",   22, 23, 15, 17, 14, 14, 10, 17, 32, 3),
    Job(                "Job",              "Job",  22, 13, 26, 21, 27, 30, 21, 22, 35, 22, 20, 25, 28, 22, 35, 22, 16, 21, 29, 29, 34, 30, 17, 25, 6, 14, 21, 28, 25, 31, 40, 22, 33, 37, 16, 33, 24, 41, 30, 32, 26, 17),
    Psalms(             "Psalms",           "Ps",   6, 12, 9, 9, 13, 11, 18, 10, 21, 18, 7, 9, 6, 7, 5, 11, 15, 51, 15, 10, 14, 32, 6, 10, 22, 11, 14, 9, 11, 13, 25, 11, 22, 23, 28, 13, 40, 23, 14, 18, 14, 12, 5, 27, 18, 12, 10, 15, 21, 23, 21, 11, 7, 9, 24, 14, 12, 12, 18, 14, 9, 13, 12, 11, 14, 20, 8, 36, 37, 6, 24, 20, 28, 23, 11, 13, 21, 72, 13, 20, 17, 8, 19, 13, 14, 17, 7, 19, 53, 17, 16, 16, 5, 23, 11, 13, 12, 9, 9, 5, 8, 29, 22, 35, 45, 48, 43, 14, 31, 7, 10, 10, 9, 8, 18, 19, 2, 29, 176, 7, 8, 9, 4, 8, 5, 6, 5, 6, 8, 8, 3, 18, 3, 3, 21, 26, 9, 8, 24, 14, 10, 8, 12, 15, 21, 10, 20, 14, 9, 6),
    Proverbs(           "Proverbs",         "Pr",   33, 22, 35, 27, 23, 35, 27, 36, 18, 32, 31, 28, 25, 35, 33, 33, 28, 24, 29, 30, 31, 29, 35, 34, 28, 28, 27, 28, 27, 33, 31),
    Ecclesiastes(       "Ecclesiastes",     "Ec",   18, 26, 22, 17, 19, 12, 29, 17, 18, 20, 10, 14),
    SongOfSolomon(      "Song of Solomon",  "So",   17, 17, 11, 16, 16, 12, 14, 14),
    Isaiah(             "Isaiah",           "Is",   31, 22, 26, 6, 30, 13, 25, 23, 20, 34, 16, 6, 22, 32, 9, 14, 14, 7, 25, 6, 17, 25, 18, 23, 12, 21, 13, 29, 24, 33, 9, 20, 24, 17, 10, 22, 38, 22, 8, 31, 29, 25, 28, 28, 25, 13, 15, 22, 26, 11, 23, 15, 12, 17, 13, 12, 21, 14, 21, 22, 11, 12, 19, 11, 25, 24),
    Jeremiah(           "Jeremiah",         "Je",   19, 37, 25, 31, 31, 30, 34, 23, 25, 25, 23, 17, 27, 22, 21, 21, 27, 23, 15, 18, 14, 30, 40, 10, 38, 24, 22, 17, 32, 24, 40, 44, 26, 22, 19, 32, 21, 28, 18, 16, 18, 22, 13, 30, 5, 28, 7, 47, 39, 46, 64, 34),
    Lamentations(       "Lamentations",     "La",   22, 22, 66, 22, 22),
    Ezekiel(            "Ezekiel",          "Eze",  28, 10, 27, 17, 17, 14, 27, 18, 11, 22, 25, 28, 23, 23, 8, 63, 24, 32, 14, 44, 37, 31, 49, 27, 17, 21, 36, 26, 21, 26, 18, 32, 33, 31, 15, 38, 28, 23, 29, 49, 26, 20, 27, 31, 25, 24, 23, 35),
    Daniel(             "Daniel",           "Da",   21, 49, 100, 34, 30, 29, 28, 27, 27, 21, 45, 13),
    Hosea(              "Hosea",            "Ho",   9, 25, 5, 19, 15, 11, 16, 14, 17, 15, 11, 15, 15, 10),
    Joel(               "Joel",             "Joe",  20, 27, 5),
    Amos(               "Amos",             "Am",   15, 16, 15, 13, 27, 14, 17, 14, 15),
    Obadiah(            "Obadiah",          "Ob",   21),
    Jonah(              "Jonah",            "Jon",  16, 11, 10, 11),
    Micah(              "Micah",            "Mic",  16, 13, 12, 14, 14, 16, 20),
    Nahum(              "Nahum",            "Na",   14, 14, 19),
    Habakkuk(           "Habakkuk",         "Hab",  17, 20, 19),
    Zephaniah(          "Zephaniah",        "Zep",  18, 15, 20),
    Haggai(             "Haggai",           "Hag",  15, 23),
    Zechariah(          "Zechariah",        "Zec",  17, 17, 10, 14, 11, 15, 14, 23, 17, 12, 17, 14, 9, 21),
    Malachi(            "Malachi",          "Mal",  14, 17, 24, 6),
    Matthew(            "Matthew",          "Mt",   25, 23, 17, 25, 48, 34, 29, 34, 38, 42, 30, 50, 58, 36, 39, 28, 27, 35, 30, 34, 46, 46, 39, 51, 46, 75, 66, 20),
    Mark(               "Mark",             "Mk",   45, 28, 35, 41, 43, 56, 37, 38, 50, 52, 33, 44, 37, 72, 47, 20),
    Luke(               "Luke",             "Lk",   80, 52, 38, 44, 39, 49, 50, 56, 62, 42, 54, 59, 35, 35, 32, 31, 37, 43, 48, 47, 38, 71, 56, 53),
    John(               "John",             "Jn",   51, 25, 36, 54, 47, 71, 53, 59, 41, 42, 57, 50, 38, 31, 27, 33, 26, 40, 42, 31, 25),
    Acts(               "Acts",             "Ac",   6, 47, 26, 37, 42, 15, 60, 40, 43, 48, 30, 25, 52, 28, 41, 40, 34, 28, 41, 38, 40, 30, 35, 27, 27, 32, 44, 31),
    Romans(             "Romans",           "Ro",   32, 29, 31, 25, 21, 23, 25, 39, 33, 21, 36, 21, 14, 23, 33, 27),
    FirstCorinthians(   "1 Corinthians",    "1Co",  31, 16, 23, 21, 13, 20, 40, 13, 27, 33, 34, 31, 13, 40, 58, 24),
    SecondCorinthians(  "2 Corinthians",    "2Co",  24, 17, 18, 18, 21, 18, 16, 24, 15, 18, 33, 21, 14),
    Galatians(          "Galatians",        "Ga",   24, 21, 29, 31, 26, 18),
    Ephesians(          "Ephesians",        "Eph",  23, 22, 21, 32, 33, 24),
    Philippians(        "Philippians",      "Php",  30, 30, 21, 23),
    Colossians(         "Colossians",       "Col",  29, 23, 25, 18),
    FirstThessalonians( "1 Thessalonians",  "1Th",  10, 20, 13, 18, 28),
    SecondThessalonians("2 Thessalonians",  "2Th",  12, 17, 18),
    FirstTimothy(       "1 Timothy",        "1Ti",  20, 15, 16, 16, 25, 21),
    SecondTimothy(      "2 Timothy",        "2Ti",  18, 26, 17, 22),
    Titus(              "Titus",            "Tt",   6, 15, 15),
    Philemon(           "Philemon",         "Phm",  25),
    Hebrews(            "Hebrews",          "Heb",  14, 18, 19, 16, 14, 20, 28, 13, 28, 39, 40, 29, 25),
    James(              "James",            "Jas",  27, 26, 18, 17, 20),
    FirstPeter(         "1 Peter",          "1Pe",  25, 25, 22, 19, 14),
    SecondPeter(        "2 Peter",          "2Pe",  21, 22, 18),
    FirstJohn(          "1 John",           "1Jn",  10, 29, 24, 21, 21),
    SecondJohn(         "2 John",           "2Jn",  13),
    ThirdJohn(          "3 John",           "3Jn",  15),
    Jude(               "Jude",             "Jud",  25),
    Revelation(         "Revelation",       "Re",   20, 29, 22, 11, 14, 17, 17, 13, 21, 11, 19, 17, 18, 20, 8, 21, 18, 24, 21, 15, 27, 21);

    private final String name; //Name that is displayed
    private final String code; //What is used for building a query URL
    private final int[] verseCount;

    Book(String name, String code, int... verseCount) {
        this.name = name;
        this.code = code;
        this.verseCount = verseCount;
    }

    //Can definitely be improved...
    public static Book fromString(String name) {
        for (Book book : EnumSet.allOf(Book.class)) {
            if(book.getName().toLowerCase().contains(name.toLowerCase())) return book;
        }

        return null;
    }

    public static String[] getList() {
		String[] books = new String[Book.values().length];

        for(int i = 0; i < Book.values().length; i++) {
            books[i] = Book.values()[i].getName();
        }

        return books;
    }

    public String getName() { return name; }
    public String getCode() {return code; }
	/** Returns the number of chapters in the book */
    public int chapterCount() {
        return verseCount.length;
    }
    /** Returns the number of verses in the specific chapter of the book */
	public int verseInChapterCount(int chapter) {
        if(chapter <= verseCount.length && chapter != 0) {
            return verseCount[chapter - 1];
        }
        else return -1;
    }

	public int lastChapter() {
		return verseCount.length;
	}

	public int lastVerse() {
		return verseCount[verseCount.length - 1];
	}
}

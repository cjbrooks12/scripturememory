package com.caseybrooks.common.app;


import com.caseybrooks.common.R;

/**
 * Enums representing the various screens within the apps.
 *
 * There are three main "types" of screens:
 *
 * TOP LEVEL
 * A top-level item is shown as a parent in the navigation drawer, and represents a View that is highly
 * important or is a category of other important items. Top-Level icons each have an icon, but that
 * icon's color cannot be changed. The id of a top-level item goes from 1-100
 *
 * TOP LEVEL SUBITEM
 * Many top-level elements represent a group of dynamically-created subitems (like tags in
 * Scripture Now!), or a small set of highly-related Top-Level views that fit better when grouped
 * than when listed individually as top-level (like all the Debug screens). Subitems may declare a
 * specific icon to show in the sublist, or may just use a default circle icon. Subitems cannot
 * be declaratively shown, and must be created as the parent Top-Level item requests children.
 * Generally speaking, if there is a static set of pages to list together in a subitem list, declare
 * that in ActivityBase, otherwise let the MainActivity of each app pull the necessary data to
 * populate the lists dynamically. Subitems cannot have a list of subitems themselves. The icon
 * color can be set for subitems, unlike the parent icons. The id of a top-level subitem goes from 101-1000.
 *
 * INNER LEVEL
 * Inner-level items are not shown in the Navigation Drawer, do not have an associated icon, and
 * represent a deeper level to the app's navigation hierarchy. Any top-level screen will still have
 * the naviagation drawer useable, but inner-level screens disable the use of the drawer and replace
 * the "hamburger" icon with a back arrow, indicating the deeper level within the app. The id of an
 * inner item goes from 1001-10000
 *
 * Final note: id 0 is reserved to be used as a marker for convenience in saving the preference of
 * which screen to show by default when first opening the app. Since a valid id opens that screen,
 * an id of 0 will tell the app to open the last top-level screen that was viewed.
 */
// TODO: make this only define features with their fragments and their IDs
public enum AppFeature {
    //Enum              Name                    Icon                            Has Children    Supports FAB    id

    //Top-level features
    Dashboard(          "Dashboard",            R.drawable.ic_home,             false,          true,           1),
    Read(               "Read",                 R.drawable.ic_local_library,    false,          false,          2),
    Discover(           "Discover",             R.drawable.ic_find_in_page,     true,           false,          4),
    MemorizationState(  "Memorization State",   R.drawable.ic_group_work,       true,           false,          7),
    Tags(               "Tags",                 R.drawable.ic_tag,              true,           false,          8),
    Help(               "Help",                 R.drawable.ic_help,             false,          false,          9),
    Settings(           "Settings",             R.drawable.ic_settings,         false,          false,          10),
    Prayers(            "Prayers",              R.drawable.ic_thumb_up,         false,          true,           11),
    Debug(              "Debug",                R.drawable.ic_bug_report,       true,           false,          12),

    //Some features are top-level but as subitems of another. Functionally the same as top-level, but can't have children
    DebugDatabase(      "Debug Database",       R.drawable.ic_database,                         false,          101),
    DebugPreferences(   "Debug Preferences",    R.drawable.ic_key,                              false,          102),
    DebugCache(         "Debug Cache",          R.drawable.ic_file,                             false,          103),
    TopicalBible(       "Topical Bible",        R.drawable.ic_search,                           false,          104),
    TopicsList(         "Topics List",          R.drawable.ic_list,                             false,          105),
    ImportVerses(       "Import Verses",        R.drawable.ic_import,                           false,          106),

    //inner features that are managed in the same way but should never show up in the main drawer
    Edit(               "Edit",                                                                 false,          1001),
    Practice(           "Practice",                                                             false,          1002),
    Flashcards(         "Flashcards",                                                           false,          1003),

    //Not actually a true "feature" but a convenience marker for settings
    LastVisited(        "Last Visited",                                                         false,          0);

    private final String title;
    private final int iconResId;
    private final boolean hasChildren;
    private final boolean supportsFAB;
    private final boolean isTopLevel;
    private final int id;

    AppFeature(String title, int iconResId, boolean hasChildren, boolean supportsFAB, int id) {
        this.title = title;
        this.iconResId = iconResId;
        this.supportsFAB = supportsFAB;
        this.hasChildren = hasChildren;
        this.isTopLevel = true;
        this.id = id;
    }

    AppFeature(String title, int iconResId, boolean supportsFAB, int id) {
        this.title = title;
        this.iconResId = iconResId;
        this.supportsFAB = supportsFAB;
        this.hasChildren = false;
        this.isTopLevel = true;
        this.id = id;
    }

    AppFeature(String title, boolean supportsFAB, int id) {
        this.title = title;
        this.iconResId = 0;
        this.supportsFAB = supportsFAB;
        this.hasChildren = false;
        this.isTopLevel = false;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean supportsFAB() {
        return supportsFAB;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public boolean isTopLevel() {
        return isTopLevel;
    }

    public int getId() {
        return id;
    }

    public static AppFeature getFeatureForId(int id) {
        for(AppFeature feature : AppFeature.values()) {
            if(id == feature.getId())
                return feature;
        }
        return null;
    }
}

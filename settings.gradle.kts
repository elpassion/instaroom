val includeAndroid = System.getenv("INCLUDE_ANDROID") == "true"
include(":backend")

if (includeAndroid) include(":android")

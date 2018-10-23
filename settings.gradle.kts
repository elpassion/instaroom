val includeAndroid = false

include(":backend")

@Suppress("ConstantConditionIf")
if (includeAndroid) include(":android")

package com.hearthappy.router.migration

data class RouterMigrationRule(
    val id: String,
    val description: String,
    val type: RuleType,
    val target: TargetType = TargetType.ANY,
    val oldValue: String,
    val newValue: String
) {

    enum class RuleType {
        LITERAL,
        REGEX
    }

    enum class TargetType {
        ANY,
        SOURCE,
        GRADLE
    }
}

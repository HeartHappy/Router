package com.hearthappy.router.enums

enum class RouteType {
    ACTIVITY {
        override fun typeName(): String = "RouteType.ACTIVITY"
    },
    FRAGMENT {
        override fun typeName(): String = "RouteType.FRAGMENT"
    },
    SERVICE {
        override fun typeName(): String = "RouteType.SERVICE"
    },
    BROADCAST {
        override fun typeName(): String = "RouteType.BROADCAST"
    },
    CONTENT_PROVIDER {
        override fun typeName(): String = "RouteType.CONTENT_PROVIDER"
    },
    METHOD {
        override fun typeName(): String = "RouteType.METHOD"
    },
    SERVICE_PROVIDER {
        override fun typeName(): String = "RouteType.SERVICE_PROVIDER"
    },
    UNKNOWN {
        override fun typeName(): String = "RouteType.UNKNOWN"
    };

    abstract fun typeName(): String
}

package us.cyberstar.data.entity

data class ArSceneCenterHitTest(val worldCoordinate: FloatArray, val worldNormal: FloatArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArSceneCenterHitTest

        if (!worldCoordinate.contentEquals(other.worldCoordinate)) return false
        if (!worldNormal.contentEquals(other.worldNormal)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = worldCoordinate.contentHashCode()
        result = 31 * result + worldNormal.contentHashCode()
        return result
    }
}
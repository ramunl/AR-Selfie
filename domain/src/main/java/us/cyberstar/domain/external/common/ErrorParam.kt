package us.cyberstar.domain.external.common

interface ErrorParam {
    fun errCode(): Int?
    fun errorMsg(): String
}
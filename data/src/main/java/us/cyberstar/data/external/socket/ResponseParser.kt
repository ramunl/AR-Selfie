package us.cyberstar.data.external.socket

import us.cyberstar.data.model.BackendResponseModel

interface ResponseParser {
    fun parse(response: String): BackendResponseModel
}
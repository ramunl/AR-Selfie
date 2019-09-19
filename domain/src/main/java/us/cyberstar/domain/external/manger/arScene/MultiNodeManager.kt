package us.cyberstar.domain.external.manger.arScene

import com.google.ar.core.Frame


interface MultiNodeManager {

    fun subscribeToPostCreated()
    fun destroy()
    fun unsubscribeFromPostCreated()
    fun getInfo(): String
}
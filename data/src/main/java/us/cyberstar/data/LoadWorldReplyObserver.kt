package us.cyberstar.data

import proxy.Proxy
import us.cyberstar.data.entity.LoadWorldReplyEntity

interface LoadWorldReplyObserver {
    fun onWorldRequestReply(replyEntity: LoadWorldReplyEntity?)
    fun onWorldRequestReply(reply: Proxy.MultipleLoadWorldReply)
}
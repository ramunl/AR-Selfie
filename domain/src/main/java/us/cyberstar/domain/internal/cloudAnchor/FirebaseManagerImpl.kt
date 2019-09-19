/*
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.cyberstar.domain.internal.cloudAnchor

import android.content.Context
import com.google.ar.core.Session
import com.google.common.base.Preconditions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import us.cyberstar.common.external.SnackBarProvider
import us.cyberstar.domain.external.cloudAnchor.CloudAnchorIdListener
import us.cyberstar.domain.external.cloudAnchor.FirebaseManager
import us.cyberstar.domain.external.cloudAnchor.RoomCodeListener
import javax.inject.Inject

/** A helper class to manage all communications with Firebase.  */
internal class FirebaseManagerImpl @Inject constructor(
    context: Context,
    snackBarProvider: SnackBarProvider
) : FirebaseManager {

    companion object {
        // Names of the nodes used in the Firebase Database
        private val ROOT_FIREBASE_HOTSPOTS = "hotspot_list"
        private val ROOT_LAST_ROOM_CODE = "last_room_code"
        // Some common keys and values used when writing to the Firebase Database.
        private val KEY_DISPLAY_NAME = "display_name"
        private val KEY_ANCHOR_ID = "hosted_anchor_id"
        private val KEY_TIMESTAMP = "updated_at_timestamp"
        private val DISPLAY_NAME_VALUE = "Android EAP Sample"
    }

    private val app: FirebaseApp? by lazy { FirebaseApp.initializeApp(context) }
    private val rootRef:DatabaseReference? by lazy  {FirebaseDatabase.getInstance(app!!).reference}

    private val hotspotListRef: DatabaseReference? by lazy {
        rootRef?.child(ROOT_FIREBASE_HOTSPOTS)
    }
    private val roomCodeRef: DatabaseReference? by lazy {
        rootRef?.child(ROOT_LAST_ROOM_CODE)
    }
    private var currentRoomRef: DatabaseReference? = null
    private var currentRoomListener: ValueEventListener? = null


    init {
        if (app != null) {
            DatabaseReference.goOnline()
        } else {
            Timber.e("Could not connect to Firebase Database!")
        }
    }

    /**
     * Gets a new room code from the Firebase Database. Invokes the listener method when a new room
     * code is available.
     */
    override fun getNewRoomCode(listener: RoomCodeListener) {
        Preconditions.checkNotNull<FirebaseApp>(app, "Firebase App was null")
        roomCodeRef!!.runTransaction(
            object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var nextCode: Long? = java.lang.Long.valueOf(1)
                    val currVal = currentData.value
                    if (currVal != null) {
                        val lastCode = java.lang.Long.valueOf(currVal.toString())
                        nextCode = lastCode + 1
                    }
                    currentData.value = nextCode
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (!committed) {
                        listener.onError(error)
                        return
                    }
                    val roomCode = currentData!!.getValue(Long::class.java)
                    listener.onNewRoomCode(roomCode)
                }
            })
    }

    /** Stores the given anchor ID in the given room code.  */
    override fun storeAnchorIdInRoom(roomCode: Long, cloudAnchorId: String) {
        Preconditions.checkNotNull<FirebaseApp>(app, "Firebase App was null")
        val roomRef = hotspotListRef!!.child(roomCode.toString())
        roomRef.child(KEY_DISPLAY_NAME).setValue(DISPLAY_NAME_VALUE)
        roomRef.child(KEY_ANCHOR_ID).setValue(cloudAnchorId)
        roomRef.child(KEY_TIMESTAMP).setValue(System.currentTimeMillis())
    }

    /**
     * Registers a new listener for the given room code. The listener is invoked whenever the data for
     * the room code is changed.
     */
    override fun registerNewListenerForRoom(roomCode: Long?, listener: CloudAnchorIdListener) {
        Preconditions.checkNotNull<FirebaseApp>(app, "Firebase App was null")
        clearRoomListener()
        currentRoomRef = hotspotListRef!!.child(roomCode.toString())
        currentRoomListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val valObj = dataSnapshot.child(KEY_ANCHOR_ID).value
                if (valObj != null) {
                    val anchorId = valObj.toString()
                    if (!anchorId.isEmpty()) {
                        listener.onNewCloudAnchorId(anchorId)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Timber.w("The Firebase operation was cancelled." + databaseError.toException())
            }
        }
        currentRoomRef!!.addValueEventListener(currentRoomListener!!)
    }

    /**
     * Resets the current room listener registered using [.registerNewListenerForRoom].
     */
    fun clearRoomListener() {
        if (currentRoomListener != null && currentRoomRef != null) {
            currentRoomRef!!.removeEventListener(currentRoomListener!!)
            currentRoomListener = null
            currentRoomRef = null
        }
    }
}

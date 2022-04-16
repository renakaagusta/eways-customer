package com.eways.customer.utils.firebase.firestore

import android.util.Log
import android.view.View
import com.eways.customer.kabarcluster.viewdto.KabarClusterCommentViewDTO
import com.eways.customer.kabarcluster.viewdto.KabarClusterPostViewDTO
import com.eways.customer.utils.date.SLDate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.proyek.infrastructures.kabarcluster.entities.Comment
import com.proyek.infrastructures.kabarcluster.entities.Post
import com.proyek.infrastructures.order.chat.entities.Chat
import java.text.SimpleDateFormat

/*
W/System: ClassLoader referenced unknown path: /data/app/com.eways.agent-1/lib/arm64
W/ResourceType: No package identifier when getting name for resource number 0x00000000
W/DynamiteModule: Local module descriptor class for com.google.firebase.auth not found.
W/DynamiteModule: Local module descriptor class for com.google.firebase.auth not found.
 */

object Firestore {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val chatChannelRef = firestoreInstance.collection("chats")
    val postChannelRef = firestoreInstance.collection("posts")

    var token = ""

    fun ChatListener(orderId: String, onListen: (ArrayList<Chat>) -> Unit): ListenerRegistration {
        return chatChannelRef
            .document(orderId)
            .collection("messages")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = ArrayList<Chat>()
                querySnapshot!!.documents!!.forEach {
                    items.add(it.toObject(Chat::class.java)!!)
                }
                onListen(items)
            }
    }

    fun PostListener(
        clusterId: String,
        view: View?,
        onListen: (View?, ArrayList<KabarClusterPostViewDTO>) -> Unit
    ): ListenerRegistration {
        return postChannelRef
            .document(clusterId)
            .collection("post")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "PostMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = ArrayList<Post>()
                querySnapshot!!.documents!!.forEach {
                    items.add(it.toObject(Post::class.java)!!)
                }

                Log.d("postfire", items.toString())

                val listKabarClusterPostViewDTO = ArrayList<KabarClusterPostViewDTO>()
                items.forEach {
                    val SLDate = SLDate()
                    SLDate.date = SimpleDateFormat("yyyy-MM-dd").parse(it.created_at)
                    var dto = KabarClusterPostViewDTO(
                        it.id!!,
                        it.user_id!!,
                        it.pinned == 1,
                        "",
                        it.content!!,
                        "",
                        SLDate,
                        0
                    )

                    listKabarClusterPostViewDTO.add(
                        dto
                    )
                }
                onListen(view, listKabarClusterPostViewDTO)
            }
    }

    fun CommentListener(
        clusterId: String,
        postId: String,
        onListen: (ArrayList<KabarClusterCommentViewDTO>) -> Unit
    ): ListenerRegistration {
        return postChannelRef
            .document(clusterId)
            .collection("post")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "PostMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = ArrayList<Comment>()
                querySnapshot!!.documents!!.forEach {
                    items.add(it.toObject(Comment::class.java)!!)
                }

                Log.d("commentfire", items.toString())

                val listKabarClusterCommentViewDTO = ArrayList<KabarClusterCommentViewDTO>()

                items.forEach {
                    val SLDate = SLDate()
                    SLDate.date = SimpleDateFormat("yyyy-MM-dd").parse(it.created_at)

                    listKabarClusterCommentViewDTO.add(
                        KabarClusterCommentViewDTO(
                            it.id!!,
                            it.user_id!!,
                            it.content!!,
                            SLDate
                        )
                    )
                }
                onListen(listKabarClusterCommentViewDTO)
            }
    }

}
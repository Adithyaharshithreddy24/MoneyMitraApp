package com.example.moneymitra.auth

import com.example.moneymitra.data.model.Chit
import com.example.moneymitra.data.model.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChitRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userChits(uid: String) =
        db.collection("users").document(uid).collection("chits")

    // ✅ ADD MANAGER CHIT
    suspend fun addManagerChit(
        chit: Chit,
        members: List<Member>
    ): Result<Unit> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val chitId = userChits(uid).document().id

            val newChit = chit.copy(
                id = chitId,
                manager = true,
                managerId = uid
            )

            val chitDoc = userChits(uid).document(chitId)

            chitDoc.set(newChit).await()

            members.forEach { member ->
                val memberRef = chitDoc.collection("members").document()
                val memberId = memberRef.id

                memberRef.set(member.copy(id = memberId, managerId = uid)).await()
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ ADD MEMBER CHIT
    suspend fun addMemberChit(chit: Chit): Result<Unit> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val chitId = userChits(uid).document().id

            userChits(uid)
                .document(chitId)
                .set(chit.copy(id = chitId, manager = false, due = 0))
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ GET CHITS
    suspend fun getChits(): Result<List<Chit>> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val snapshot = userChits(uid).get().await()

            Result.success(snapshot.toObjects(Chit::class.java))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ GET MEMBERS WITH ID
    suspend fun getMembers(chitId: String): List<Member> {

        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = userChits(uid)
            .document(chitId)
            .collection("members")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val member = doc.toObject(Member::class.java)
            member?.copy(id = doc.id)
        }
    }

    // ✅ UPDATE MEMBER
    suspend fun updateMember(
        chitId: String,
        memberId: String,
        updatedMember: Member
    ) {
        val uid = auth.currentUser?.uid ?: return

        userChits(uid)
            .document(chitId)
            .collection("members")
            .document(memberId)
            .set(updatedMember)
            .await()
    }

    // ✅ UPDATE MEMBER DUE
    suspend fun updateDue(
        chitId: String,
        memberId: String,
        newDue: Double
    ) {
        val uid = auth.currentUser?.uid ?: return

        userChits(uid)
            .document(chitId)
            .collection("members")
            .document(memberId)
            .update("due", newDue)
            .await()
    }

    // ✅ MARK PAYOUT
    suspend fun markPayout(
        chitId: String,
        memberId: String
    ) {
        val uid = auth.currentUser?.uid ?: return

        userChits(uid)
            .document(chitId)
            .collection("members")
            .document(memberId)
            .update("payout", true)
            .await()
    }

    // 🔥 NEW: UPDATE CHIT (USED FOR EDIT + PAY)
    suspend fun updateChit(chit: Chit) {
        val uid = auth.currentUser?.uid ?: return

        userChits(uid)
            .document(chit.id)
            .set(chit)
            .await()
    }

    // 🔥 NEW: DELETE CHIT
    suspend fun deleteChit(chitId: String) {
        val uid = auth.currentUser?.uid ?: return

        userChits(uid)
            .document(chitId)
            .delete()
            .await()
    }
}
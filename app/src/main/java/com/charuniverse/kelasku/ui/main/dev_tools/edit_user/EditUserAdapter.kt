package com.charuniverse.kelasku.ui.main.dev_tools.edit_user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.data.models.User
import kotlinx.android.synthetic.main.recycler_user.view.*

class EditUserAdapter(
    private val events: EditUserEvents,
    private val users: List<User>
) : RecyclerView.Adapter<EditUserAdapter.ViewHolder>() {

    interface EditUserEvents {
        fun onUserClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_user, parent, false)
        )
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.itemView.let {
            it.tvRecyclerUserEmail.text = user.email

            it.setOnClickListener {
                events.onUserClick(user)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
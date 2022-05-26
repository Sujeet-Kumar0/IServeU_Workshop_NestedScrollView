package com.example.bluetoothandnestedscrollview

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.*



class ViewHolder ( val postModel : MutableList <PostModel>):RecyclerView.Adapter<PostViewHolder>() {
        override fun onCreateViewHolder ( parent : ViewGroup , viewType : Int ) : PostViewHolder {
            val view = LayoutInflater .from(parent.context).inflate(R.layout.card, parent,false )
            return PostViewHolder ( view )

        }

        override fun onBindViewHolder ( holder : PostViewHolder , position : Int ) {
            return holder.bindView ( postModel [ position ] )

        }
        override fun getItemCount ( ) : Int {
            return postModel.size
        }
    }
class PostViewHolder ( itemView : View ) : RecyclerView.ViewHolder ( itemView ) {
    private val tvAnime: TextView = itemView . findViewById (R.id.tvAnime)
    private val tvCharacter: TextView = itemView.findViewById(R.id.tvCharacter)
    private val tvQuote: TextView = itemView.findViewById(R.id.tvQuote)

    fun bindView(postModel: PostModel) {
        tvAnime.text = postModel.anime
        tvCharacter.text = postModel.character
        tvQuote.text = postModel.quote
    }
}


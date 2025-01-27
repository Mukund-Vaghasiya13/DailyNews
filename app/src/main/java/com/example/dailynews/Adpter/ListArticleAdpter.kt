package com.example.dailynews.Adpter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailynews.R
import com.example.dailynews.modle.Article
import com.squareup.picasso.Picasso

class ListArticleAdpter(val list:List<Article>,val activitCurrent:Activity,val callback:(url:String)->Unit): RecyclerView.Adapter<ListArticleAdpter.EachArticles>()  {
    class EachArticles(private val view: View):RecyclerView.ViewHolder(view) {
        val card:CardView = view.findViewById(R.id.card)
        val image:ImageView = view.findViewById(R.id.ArticleImage)
        val textView:TextView = view.findViewById(R.id.des)
        val title:TextView = view.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EachArticles {
        val layout = LayoutInflater.from(activitCurrent).inflate(R.layout.each_article_view, parent, false)
        return EachArticles(layout)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EachArticles, position: Int) {
      val article = list[position]
        holder.textView.text = article.content.toString()
        holder.title.text = article.title.toString()
        if(article.urlToImage?.isNotEmpty() == true){
            Picasso.get().load(article.urlToImage).into(holder.image)
        }else{
            Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRREmTHtU3tWq7ND0KoL8Fj0B2g7gSE4JhdI6dFhro2vzlJaWd7XOnyM94NrAlC4Ho7rlE&usqp=CAU").into(holder.image)
        }
        holder.card.setOnClickListener {
            article.url?.let { callback(it) }
        }
    }
}
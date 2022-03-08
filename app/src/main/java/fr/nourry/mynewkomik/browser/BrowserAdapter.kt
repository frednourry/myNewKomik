package fr.nourry.mynewkomik.browser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.nourry.mynewkomik.Comic
import fr.nourry.mynewkomik.R
import fr.nourry.mynewkomik.loader.ComicLoadingManager
import fr.nourry.mynewkomik.loader.ComicLoadingProgressListener
import fr.nourry.mynewkomik.loader.ComicLoadingResult
import timber.log.Timber
import java.io.File


class BrowserAdapter(private val comics:List<Comic>, private val listener:OnComicAdapterListener?):RecyclerView.Adapter<BrowserAdapter.ViewHolder>(), View.OnClickListener, ComicLoadingProgressListener {
    interface OnComicAdapterListener {
        fun onComicClicked(comic: Comic)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<CardView>(R.id.cardView)!!
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)!!
        val textView = itemView.findViewById<TextView>(R.id.textView)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = comics[position]
        val comicAdapter = this
        with (holder) {
            cardView.tag = comic
            cardView.setOnClickListener(this@BrowserAdapter)
            textView.text = comic.file.name

            if (comic.file.isFile) {
                Glide.with(imageView.context)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(imageView)
                ComicLoadingManager.getInstance().loadComicInImageView(comic, imageView, comicAdapter)
            } else {
                Glide.with(imageView.context)
                    .load(R.drawable.ic_library_temp)
                    .into(imageView)
                ComicLoadingManager.getInstance().loadComicDirectoryInImageView(comic, imageView, comicAdapter)

            }
        }
    }

    override fun getItemCount(): Int = comics.size

    override fun onClick(v: View) {
        listener?.onComicClicked(v.tag as Comic)
    }

    override fun onProgress(currentIndex: Int, size: Int) {
    }

    override fun onFinished(result: ComicLoadingResult, image: ImageView?, path: File?) {
        Timber.d("onFinished $path" )
        if (result == ComicLoadingResult.SUCCESS && image!= null && path != null && path.absolutePath != "" && path.exists()) {
            // TODO Be sure this image view is still waiting this result...

            Glide.with(image.context)
                .load(path)
                .into(image)
        } else {
            Timber.w("onFinished:: $result imageView=$image path=$path")
        }
    }

}
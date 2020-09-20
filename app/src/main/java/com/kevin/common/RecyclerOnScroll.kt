package com.kevin.common

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerOnScroll(private val onScrollLockListener: OnScrollLockListener) : RecyclerView.OnScrollListener() {

    interface OnScrollLockListener {
        var isLockScroll: Boolean
        fun increasePage()
        fun setMoreScroll()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView.childCount

        val manager = recyclerView.layoutManager

        val totalItemCount = manager!!.itemCount

        val firstVisibleItem: Int
        if (manager is GridLayoutManager) {
            firstVisibleItem = manager.findFirstVisibleItemPosition()
        } else {
            firstVisibleItem = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
        }

        val count = totalItemCount - visibleItemCount
        if (firstVisibleItem >= count && totalItemCount != 0 && !onScrollLockListener.isLockScroll) {
            onScrollLockListener.isLockScroll = true
            onScrollLockListener.increasePage()
            onScrollLockListener.setMoreScroll()
        }
    }

}

fun <T : RecyclerView.ViewHolder> RecyclerView.addOnItemClickListener(listener : OnItemClickListener<T>){
    addOnItemTouchListener(ItemClickListener(context, listener = listener))
}

fun  <T : RecyclerView.ViewHolder> RecyclerView.addOnItemClickListener(block : (viewHolder : T, position : Int) -> Unit){
    addOnItemTouchListener(ItemClickListener(context, block = block))
}



interface OnItemClickListener<T : RecyclerView.ViewHolder>{
    fun onItemClick (viewHolder : T, position : Int)
}

open class ItemClickListener<T : RecyclerView.ViewHolder> (context : Context,
                                                           private val block : ((viewHolder : T, position : Int) -> Unit)? = null,
                                                           private val listener: OnItemClickListener<T>? = null) : RecyclerView.SimpleOnItemTouchListener(){

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }
    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)

        //터치한 곳의 View가 RecyclerView 안의 아이템이고 그 아이템의 View가 null이 아니라
        //정확한 Item의 View를 가져왔고, gestureDetector에서 한번만 누르면 true를 넘기게 구현했으니
        //한번만 눌려서 그 값이 true가 넘어왔다면
        if (childView != null && gestureDetector.onTouchEvent(e)) {

            //현재 터치된 곳의 position을 가져오고
            val currentPosition = rv.getChildAdapterPosition(childView)
            rv.findViewHolderForAdapterPosition(currentPosition)?.let { holder ->

                listener?.onItemClick(holder as T, currentPosition)
                block?.invoke(holder as T, currentPosition)
            }
            return true
        }
        return false
    }
}
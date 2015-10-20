
package gubo.sample.view;

import android.view.*;
import android.widget.*;
import android.support.v7.widget.*;
import android.graphics.drawable.*;

import com.android.volley.toolbox.*;

import gubo.slipwire.*;
import gubo.sample.data.*;

/*
 *
 */
class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.ViewHolder>
{
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private static int ID = 0;

        final NetworkImageView thumbnailview;
        final TextView titleview;
        final int id = ++ID;

        /*
         * TODO: why RecyclerView create new instances when notifyDataSetChanged, but itemcount not changed ?
         */
        public ViewHolder( final View view ) {
            super( view );
            thumbnailview = ( NetworkImageView )view.findViewById( R.id.book_thumbnail );
            titleview = ( TextView)view.findViewById( R.id.book_title );
        }
    }

    private final DataSource<Book> datasource;
    private final int pagesize;

    private int itemcount;

    BooksRecyclerViewAdapter( final DataSource<Book> datasource, final int pagesize ) throws IllegalArgumentException {
        if ( datasource == null ) { throw new IllegalArgumentException(); }
        this.datasource = datasource;
        this.pagesize = Math.max( pagesize,0 );
        this.itemcount = 0;
    }

    @Override
    public BooksRecyclerViewAdapter.ViewHolder onCreateViewHolder( final ViewGroup parent,final int viewType ) {
        final View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.book,parent,false );
        final BooksRecyclerViewAdapter.ViewHolder viewholder = new BooksRecyclerViewAdapter.ViewHolder( view );
        return viewholder;
    }

    @Override
    public int getItemCount() {
        return itemcount;
    }

    public void setItemCount( final int itemcount ) {
        this.itemcount = Math.max( itemcount,0 );
    }

    public void page( final Pagination pagination ) {
        datasource.getReadyFor( pagination.start, pagination.count );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder,final int position ) {
        final Book book = datasource.getDataFor( position );
        if ( book != null ) {
            final String text = ( four( position ) + "  H" + four( holder.id ) + "  " + book.title );
            holder.thumbnailview.setImageUrl( book.thumbnailurl,VOLLEY.getInstance().getImageLoader() );
            holder.titleview.setText( text );
        } else {
            holder.thumbnailview.setBackground( new ColorDrawable( 0xFF440044 ) );
            holder.titleview.setText( "..." );
        }
    }

    private String four( final int n ) {
        String four = String.valueOf( n );
        while ( four.length() < 4 ) {
            four = "0"+four;
        }
        return four;
    }
}

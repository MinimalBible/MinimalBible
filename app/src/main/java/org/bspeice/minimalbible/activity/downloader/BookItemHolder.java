package org.bspeice.minimalbible.activity.downloader;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.todddavies.components.progressbar.ProgressWheel;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.downloader.manager.BookManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
* Created by bspeice on 5/20/14.
*/
public class BookItemHolder {

    // TODO: The holder should register and unregister itself for DownloadProgress events
    // so that we can display live updates.

    private final Book b;
    @InjectView(R.id.download_txt_item_acronym)
    TextView acronym;
    @InjectView(R.id.txt_download_item_name)
    TextView itemName;
    @InjectView(R.id.download_ibtn_download)
    ImageButton isDownloaded;
    @InjectView(R.id.download_prg_download)
    ProgressWheel downloadProgress;
    @Inject
    BookManager bookManager;
    @Inject @Named("DownloadActivityContext")
    Context ctx;
    private Subscription subscription;

    // TODO: Factory style?
    public BookItemHolder(View v, Book b, Injector injector) {
        ButterKnife.inject(this, v);
        injector.inject(this);
        this.b = b;
    }

    public void bindHolder() {
        acronym.setText(b.getInitials());
        itemName.setText(b.getName());
        DLProgressEvent dlProgressEvent = bookManager.getDownloadProgress(b);
        if (dlProgressEvent != null) {
            displayProgress((int) dlProgressEvent.toCircular());
        } else if (bookManager.isInstalled(b)) {
            displayInstalled();
        }
        //TODO: Refactor
        subscription = bookManager.getDownloadEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<DLProgressEvent, Boolean>() {
                    @Override
                    public Boolean call(DLProgressEvent event) {
                        return event.getB().getInitials().equals(b.getInitials());
                    }
                })
                .subscribe(new Action1<DLProgressEvent>() {
                    @Override
                    public void call(DLProgressEvent event) {
                        BookItemHolder.this.displayProgress(event.toCircular());
                    }
                });
    }

    private void displayInstalled() {
        isDownloaded.setImageResource(R.drawable.ic_action_cancel);
    }

    @OnClick(R.id.download_ibtn_download)
    public void onDownloadItem(View v) {
        if (bookManager.isInstalled(b)) {
            // Remove the book
            boolean didRemove = bookManager.removeBook(b, bookManager.getRealBook(b));
            if (didRemove) {
                isDownloaded.setImageResource(R.drawable.ic_action_download);
            } else {
                Toast.makeText(ctx, ctx.getText(R.string.book_removal_failure)
                        , Toast.LENGTH_SHORT).show();
            }
        } else {
            bookManager.installBook(this.b);
        }
    }

    /**
     * Display the current progress of this download
     * @param progress The progress out of 360 (degrees of a circle)
     */
    private void displayProgress(int progress) {

        int downloadView;

        if (progress == DLProgressEvent.PROGRESS_BEGINNING) {
            // Download starting
            downloadView = downloadProgress.getId();

            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);

            downloadProgress.spin();
        } else if (progress < 360) {
            // Download in progress
            downloadView = downloadProgress.getId();

            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);

            downloadProgress.stopSpinning();
            downloadProgress.setProgress(progress);
        } else {
            // Download complete
            subscription.unsubscribe();
            downloadView = downloadProgress.getId();

            isDownloaded.setVisibility(View.VISIBLE);
            downloadProgress.setVisibility(View.GONE);
            displayInstalled();
        }

        RelativeLayout.LayoutParams acronymParams =
                (RelativeLayout.LayoutParams) acronym.getLayoutParams();
        acronymParams.addRule(RelativeLayout.LEFT_OF, downloadView);

        RelativeLayout.LayoutParams nameParams =
                (RelativeLayout.LayoutParams) itemName.getLayoutParams();
        nameParams.addRule(RelativeLayout.LEFT_OF, downloadView);
    }

    public void onScrollOffscreen() {
        subscription.unsubscribe();
    }
}

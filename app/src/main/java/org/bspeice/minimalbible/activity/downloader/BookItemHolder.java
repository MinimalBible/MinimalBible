package org.bspeice.minimalbible.activity.downloader;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

import org.bspeice.minimalbible.Injector;
import org.bspeice.minimalbible.R;
import org.bspeice.minimalbible.activity.downloader.manager.BookDownloadManager;
import org.bspeice.minimalbible.activity.downloader.manager.DLProgressEvent;
import org.bspeice.minimalbible.activity.downloader.manager.InstalledManager;
import org.crosswire.jsword.book.Book;

import javax.inject.Inject;

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

    @InjectView(R.id.download_txt_item_acronym)
    TextView acronym;
    @InjectView(R.id.txt_download_item_name)
    TextView itemName;
    @InjectView(R.id.download_ibtn_download)
    ImageButton isDownloaded;
    @InjectView(R.id.download_prg_download)
    ProgressWheel downloadProgress;

    @Inject
    BookDownloadManager bookDownloadManager;
    @Inject
    InstalledManager installedManager;

    private final Book b;
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
        DLProgressEvent dlProgressEvent = bookDownloadManager.getInProgressDownloadProgress(b);
        if (dlProgressEvent != null) {
            displayProgress((int) dlProgressEvent.toCircular());
        } else if (installedManager.isInstalled(b)) {
            displayInstalled();
        }
        //TODO: Refactor
        subscription = bookDownloadManager.getDownloadEvents()
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
                        BookItemHolder.this.displayProgress((int) event.toCircular());
                    }
                });
    }

    private void displayInstalled() {
        isDownloaded.setImageResource(R.drawable.ic_action_cancel);
    }

    @OnClick(R.id.download_ibtn_download)
    public void onDownloadItem(View v) {
        if (installedManager.isInstalled(b)) {
            // Remove the book
            installedManager.removeBook(b);
            isDownloaded.setImageResource(R.drawable.ic_action_download);
        } else {
            bookDownloadManager.installBook(this.b);
        }
    }

    /**
     * Display the current progress of this download
     * TODO: Clean up this logic if at all possible...
     * @param progress The progress out of 360 (degrees of a circle)
     */
    private void displayProgress(int progress) {


        if (progress == DLProgressEvent.PROGRESS_BEGINNING) {
            // Download starting
            RelativeLayout.LayoutParams acronymParams =
                    (RelativeLayout.LayoutParams)acronym.getLayoutParams();
            acronymParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            RelativeLayout.LayoutParams nameParams =
                    (RelativeLayout.LayoutParams)itemName.getLayoutParams();
            nameParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);

            downloadProgress.spin();
        } else if (progress < 360) {
            // Download in progress
            RelativeLayout.LayoutParams acronymParams =
                    (RelativeLayout.LayoutParams)acronym.getLayoutParams();
            acronymParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            RelativeLayout.LayoutParams nameParams =
                    (RelativeLayout.LayoutParams)itemName.getLayoutParams();
            nameParams.addRule(RelativeLayout.LEFT_OF, downloadProgress.getId());

            isDownloaded.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.VISIBLE);

            downloadProgress.stopSpinning();
            downloadProgress.setProgress(progress);
        } else {
            // Download complete
            subscription.unsubscribe();
            RelativeLayout.LayoutParams acronymParams =
                    (RelativeLayout.LayoutParams)acronym.getLayoutParams();
            acronymParams.addRule(RelativeLayout.LEFT_OF, isDownloaded.getId());

            RelativeLayout.LayoutParams nameParams =
                    (RelativeLayout.LayoutParams)itemName.getLayoutParams();
            nameParams.addRule(RelativeLayout.LEFT_OF, isDownloaded.getId());

            isDownloaded.setVisibility(View.VISIBLE);
            downloadProgress.setVisibility(View.GONE);
            displayInstalled();
        }
    }

    public void onScrollOffscreen() {
        subscription.unsubscribe();
    }
}

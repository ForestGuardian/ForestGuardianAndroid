package org.forestguardian.View.CustomViews;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.forestguardian.DataAccess.Local.Report;
import org.forestguardian.R;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by emma on 22/05/17.
 */

public class ReportItemView extends LinearLayout {

    @BindView(R.id.report_item_title) TextView titleView;
    @BindView(R.id.report_item_description) TextView descriptionView;
    @BindView(R.id.report_item_picture) ImageView pictureView;

    private Report mReport;

    public ReportItemView(final Context context, Report pReport) {
        super(context);
        inflate(context, R.layout.list_report_item, this);
        ButterKnife.bind(this);

        mReport = pReport;
        titleView.setText(mReport.getTitle());
        descriptionView.setText(mReport.getDescription());
        Observable.create(e -> {
            Bitmap picture = BitmapFactory.decodeStream( new URL(mReport.getPicture()).openConnection().getInputStream() );
            if (!e.isDisposed()){
                e.onNext(picture);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( bitmap -> pictureView.setImageBitmap((Bitmap)bitmap) );
    }

}
